package com.rakesh.cabletv;

/**
 * Created by Rakesh on 21-12-2017.
 */

class Transaction {

    private String vc;
    private int amount;
    private String dateTime;
    private String newDateTime;

    Transaction() {
    }

    Transaction(String vc, int amount, String dateTime) {
        this.vc = vc;
        this.amount = amount;
        setDateTime(dateTime);
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
        setNewDateTime(dateTime);
    }

    String getNewDateTime() {
        return newDateTime;
    }

    void setNewDateTime(String oldDateTime) {
        String[] old = oldDateTime.split(" ");
        String[] dates = old[0].split("-");
        String newDate = dates[2] + "-" +
                dates[1] + "-" +
                dates[0] + " " +
                old[1];
        this.newDateTime = newDate;
    }


}
