package com.example.qlnh_ttt.Entities;

public class InfoUser {
    private int userInfoId;

    private int accountId;

    private String fullName;

    private String sex ;

    private String email;

    private String phoneNumber;

    public InfoUser(int userInfoId, int accountId, String fullName, String sex, String email, String phoneNumber) {
        this.userInfoId = userInfoId;
        this.accountId = accountId;
        this.fullName = fullName;
        this.sex = sex;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public int getUserInfoId() {
        return userInfoId;
    }

    public void setUserInfoId(int userInfoId) {
        this.userInfoId = userInfoId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
