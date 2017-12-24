package com.rakesh.cabletv;

/**
 * Created by Rakesh on 21-12-2017.
 */

class Transaction {

    private String vc;
    private int amount;
    private String dateTime;

    Transaction() {
    }

    Transaction(String vc, int amount, String dateTime) {
        this.vc = vc;
        this.amount = amount;
        this.dateTime = dateTime;
    }

    String getVc() {
        return vc;
    }

    void setVc(String vc) {
        this.vc = vc;
    }

    int getAmount() {
        return amount;
    }

    void setAmount(int amount) {
        this.amount = amount;
    }

    String getDateTime() {
        return dateTime;
    }

    void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
