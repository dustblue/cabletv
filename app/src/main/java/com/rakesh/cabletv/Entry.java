package com.rakesh.cabletv;

/**
 * Created by Rakesh on 22-12-2017.
 */

class Entry {
    private String userName;
    private Transaction transaction;

    String getUserName() {
        return userName;
    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    Transaction getTransaction() {
        return transaction;
    }

    void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
