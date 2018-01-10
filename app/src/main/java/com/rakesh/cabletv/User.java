package com.rakesh.cabletv;

/**
 * Created by Rakesh on 09-12-2017.
 */

class User {

    private String vc;
    private String caf;
    private String name;
    private String phone;
    private String address;
    private String cluster;
    private String installDate;
    private Boolean status;

    User() {
    }

    User(String vc, String caf, String name, String phone, String address, String cluster, String installDate, Boolean status) {
        this.vc = vc;
        this.caf = caf;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.cluster = cluster;
        this.installDate = installDate;
        this.status = status;
    }

    String getVc() {
        return vc;
    }

    void setVc(String vc) {
        this.vc = vc;
    }

    String getCaf() {
        return caf;
    }

    void setCaf(String caf) {
        this.caf = caf;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getPhone() {
        return phone;
    }

    void setPhone(String phone) {
        this.phone = phone;
    }

    String getAddress() {
        return address;
    }

    void setAddress(String address) {
        this.address = address;
    }

    String getCluster() {
        return cluster;
    }

    void setCluster(String cluster) {
        this.cluster = cluster;
    }

    String getInstallDate() {
        return installDate;
    }

    void setInstallDate(String installDate) {
        this.installDate = installDate;
    }

    Boolean getStatus() {
        return status;
    }

    void setStatus(Boolean status) {
        this.status = status;
    }

    Boolean search(String key) {
        return this.getName().contains(key)
                || this.getPhone().contains(key)
                || this.getVc().contains(key)
                || this.getCaf().contains(key)
                || this.getAddress().contains(key);
    }
}
