package com.example.custproc.dao;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.example.custproc.exception.MasterFileException;
import com.example.custproc.mapper.PackedDecimal;
import com.example.custproc.model.Account;

/**
 * Fixed-width master file with the layout derived from the COBOL fields (151 bytes):
 * id X(10), name X(30), balance S9(9)V99 COMP-3 (6 bytes), status X(1), filler X(104).
 * Text is ISO-8859-1. Records are located by key through an index built at open time.
 */
@Repository
public class FixedWidthAccountDao implements AccountDao {

    static final int RECORD_LENGTH = 151;
    private static final int ID_LENGTH = 10;
    private static final int NAME_LENGTH = 30;
    private static final int BALANCE_OFFSET = ID_LENGTH + NAME_LENGTH;
    private static final int STATUS_OFFSET = BALANCE_OFFSET + PackedDecimal.LENGTH;
    private static final Charset CHARSET = StandardCharsets.ISO_8859_1;

    private final Path masterFile;

    public FixedWidthAccountDao(@Value("${custproc.master-file}") Path masterFile) {
        this.masterFile = masterFile;
    }

    @Override
    public Session open() {
        if (!Files.isRegularFile(masterFile)) {
            throw new MasterFileException("Master file does not exist");
        }
        try {
            RandomAccessFile file = new RandomAccessFile(masterFile.toFile(), "rw");
            try {
                return new FileSession(file);
            } catch (RuntimeException | IOException e) {
                file.close();
                throw e;
            }
        } catch (IOException e) {
            throw new MasterFileException("Master file could not be opened", e);
        }
    }

    private static final class FileSession implements Session {

        private final RandomAccessFile file;
        private final Map<String, Long> offsets = new HashMap<>();

        FileSession(RandomAccessFile file) throws IOException {
            this.file = file;
            long length = file.length();
            if (length % RECORD_LENGTH != 0) {
                throw new MasterFileException("Master file length is not a multiple of the record length");
            }
            byte[] key = new byte[ID_LENGTH];
            for (long offset = 0; offset < length; offset += RECORD_LENGTH) {
                file.seek(offset);
                file.readFully(key);
                if (offsets.put(new String(key, CHARSET), offset) != null) {
                    throw new MasterFileException("Master file contains duplicate keys");
                }
            }
        }

        @Override
        public Optional<Account> find(String customerId) {
            Long offset = offsets.get(customerId);
            if (offset == null) {
                return Optional.empty();
            }
            try {
                byte[] record = new byte[RECORD_LENGTH];
                file.seek(offset);
                file.readFully(record);
                byte[] balance = new byte[PackedDecimal.LENGTH];
                System.arraycopy(record, BALANCE_OFFSET, balance, 0, balance.length);
                return Optional.of(new Account(
                        new String(record, 0, ID_LENGTH, CHARSET),
                        new String(record, ID_LENGTH, NAME_LENGTH, CHARSET),
                        PackedDecimal.decode(balance),
                        (char) (record[STATUS_OFFSET] & 0xFF)));
            } catch (IOException | IllegalArgumentException e) {
                throw new MasterFileException("Master record could not be read", e);
            }
        }

        @Override
        public boolean rewrite(Account account) {
            Long offset = offsets.get(account.customerId());
            if (offset == null) {
                return false;
            }
            try {
                file.seek(offset + BALANCE_OFFSET);
                file.write(PackedDecimal.encode(account.balance()));
                return true;
            } catch (IOException e) {
                throw new MasterFileException("Master record could not be rewritten", e);
            }
        }

        @Override
        public void close() {
            try {
                file.close();
            } catch (IOException e) {
                throw new MasterFileException("Master file could not be closed", e);
            }
        }
    }

    /** Builds one 151-byte record; used by tests and data preparation tools. */
    public static byte[] encodeRecord(Account account) {
        byte[] record = new byte[RECORD_LENGTH];
        java.util.Arrays.fill(record, (byte) ' ');
        System.arraycopy(pad(account.customerId(), ID_LENGTH), 0, record, 0, ID_LENGTH);
        System.arraycopy(pad(account.name(), NAME_LENGTH), 0, record, ID_LENGTH, NAME_LENGTH);
        System.arraycopy(PackedDecimal.encode(account.balance()), 0, record, BALANCE_OFFSET, PackedDecimal.LENGTH);
        record[STATUS_OFFSET] = (byte) account.status();
        return record;
    }

    private static byte[] pad(String value, int width) {
        byte[] out = new byte[width];
        java.util.Arrays.fill(out, (byte) ' ');
        byte[] bytes = value.getBytes(CHARSET);
        System.arraycopy(bytes, 0, out, 0, Math.min(bytes.length, width));
        return out;
    }
}
