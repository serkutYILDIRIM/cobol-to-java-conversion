package com.example.custproc.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Service;

import com.example.custproc.dao.AccountDao;
import com.example.custproc.mapper.NumericRules;
import com.example.custproc.mapper.ReportFormatter;
import com.example.custproc.model.Account;
import com.example.custproc.model.BatchResult;
import com.example.custproc.model.Transaction;

/** Business rules of CUSTPROC. All run state lives in a per-call {@link Run}. */
@Service
public class CustomerTransactionService {

    private static final BigDecimal OVERDRAFT_FEE = new BigDecimal("35.00");
    private static final int BALANCE_INTEGER_DIGITS = 9;
    private static final int TOTAL_INTEGER_DIGITS = 11;

    private final AccountDao accountDao;
    private final ReportFormatter formatter;
    private final Clock clock;
    // The master file is a shared resource, so runs are serialized.
    private final ReentrantLock masterLock = new ReentrantLock();

    public CustomerTransactionService(AccountDao accountDao, ReportFormatter formatter, Clock clock) {
        this.accountDao = accountDao;
        this.formatter = formatter;
        this.clock = clock;
    }

    public BatchResult process(List<Transaction> transactions) {
        masterLock.lock();
        try (AccountDao.Session master = accountDao.open()) {
            Run run = new Run(master);
            run.initialize();
            for (Transaction transaction : transactions) {
                run.processTransaction(transaction);
            }
            return run.terminate();
        } finally {
            masterLock.unlock();
        }
    }

    private final class Run {

        private final AccountDao.Session master;
        private final List<String> report = new ArrayList<>();
        private final List<String> errors = new ArrayList<>();
        private final List<String> console = new ArrayList<>();
        private int recordsRead;
        private int recordsProcessed;
        private int errorCount;
        private BigDecimal totalDeposits = new BigDecimal("0.00");
        // RPT-TYPE keeps its previous value when the transaction type is not D, W, or F.
        private String reportType = ReportFormatter.fit("", 10);

        Run(AccountDao.Session master) {
            this.master = master;
        }

        void initialize() {
            String today = LocalDate.now(clock).format(DateTimeFormatter.BASIC_ISO_DATE);
            report.add(formatter.header(today));
        }

        void processTransaction(Transaction transaction) {
            recordsRead = NumericRules.increment(recordsRead);
            Optional<Account> found = master.find(transaction.customerId());
            if (found.isEmpty()) {
                logError(transaction, "ACCOUNT NUMBER NOT FOUND IN VSAM MASTER FILE");
            } else if (found.get().isFrozen()) {
                logError(transaction, "TRANSACTION REJECTED: ACCOUNT STATUS IS FROZEN");
            } else {
                apply(found.get(), transaction);
            }
        }

        private void apply(Account account, Transaction transaction) {
            BigDecimal newBalance = account.balance();
            if (transaction.isDeposit()) {
                newBalance = truncateBalance(newBalance.add(transaction.amount()));
                totalDeposits = NumericRules.truncate(totalDeposits.add(transaction.amount()), TOTAL_INTEGER_DIGITS);
            } else if (transaction.isWithdrawal()) {
                newBalance = truncateBalance(newBalance.subtract(transaction.amount()));
                if (newBalance.signum() < 0) {
                    console.add("OVERDRAFT FOR CUST: " + account.customerId());
                    newBalance = truncateBalance(newBalance.subtract(OVERDRAFT_FEE));
                }
            } else if (transaction.isFee()) {
                newBalance = truncateBalance(newBalance.subtract(transaction.amount()));
            }
            updateMasterAndReport(account, transaction, newBalance);
        }

        private void updateMasterAndReport(Account account, Transaction transaction, BigDecimal newBalance) {
            if (!master.rewrite(account.withBalance(newBalance))) {
                console.add("CRITICAL REWRITE ERROR ON ID: " + account.customerId());
            }
            if (transaction.isDeposit() || transaction.isWithdrawal() || transaction.isFee()) {
                reportType = ReportFormatter.fit(formatter.reportTypeText(transaction), 10);
            }
            report.add(formatter.detail(account.customerId(), account.name(), reportType,
                    account.balance(), newBalance));
            recordsProcessed = NumericRules.increment(recordsProcessed);
        }

        private void logError(Transaction transaction, String message) {
            errors.add(formatter.error(transaction.customerId(), message));
            errorCount = NumericRules.increment(errorCount);
        }

        BatchResult terminate() {
            console.add("--- PROCESSING COMPLETE ---");
            console.add("TOTAL RECORDS READ    : " + String.format("%05d", recordsRead));
            console.add("TOTAL RECS PROCESSED  : " + String.format("%05d", recordsProcessed));
            console.add("TOTAL ERRORS LOGGED   : " + String.format("%05d", errorCount));
            console.add("TOTAL VALUE DEPOSITED : " + String.format("%013d", totalDeposits.unscaledValue()));
            return new BatchResult(List.copyOf(report), List.copyOf(errors), List.copyOf(console),
                    recordsRead, recordsProcessed, errorCount, totalDeposits);
        }

        private BigDecimal truncateBalance(BigDecimal value) {
            return NumericRules.truncate(value, BALANCE_INTEGER_DIGITS);
        }
    }
}
