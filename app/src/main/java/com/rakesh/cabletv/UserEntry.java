package com.rakesh.cabletv;

/**
 * Created by Rakesh on 26-12-2017.
 */

class UserEntry {
    private boolean ifPaid;
    private User user;
    private String lastPaidDate;

    public boolean isIfPaid() {
        return ifPaid;
    }

    public void setIfPaid(boolean ifPaid) {
        this.ifPaid = ifPaid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLastPaidDate() {
        return lastPaidDate;
    }

    public void setLastPaidDate(String lastPaidDate) {
        this.lastPaidDate = lastPaidDate;
    }
}
