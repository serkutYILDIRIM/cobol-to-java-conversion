package com.example.custproc.dao;

import java.util.Optional;

import com.example.custproc.model.Account;

/** Access to the customer master (VSAM MASTER-FILE replacement). */
public interface AccountDao {

    /** Opens the master for I-O; the caller must close the session. Throws MasterFileException on failure. */
    Session open();

    interface Session extends AutoCloseable {

        Optional<Account> find(String customerId);

        /** Rewrites an existing record; returns false when the key does not exist (INVALID KEY). */
        boolean rewrite(Account account);

        @Override
        void close();
    }
}
