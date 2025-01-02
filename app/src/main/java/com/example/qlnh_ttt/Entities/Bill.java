package com.example.qlnh_ttt.Entities;

import java.time.LocalDateTime;

public class Bill {
    private Integer billID;

    private Integer tableID;
    private Integer userInfoID;
    private LocalDateTime billDate;
    private Integer totalAmount;
    private String status;

    public Bill(Integer billID, LocalDateTime billDate, Integer userInfoID, Integer tableID, String status, Integer totalAmount) {
        this.billID = billID;
        this.billDate = billDate;
        this.userInfoID = userInfoID;
        this.tableID = tableID;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public Integer getBillID() {
        return billID;
    }

    public void setBillID(Integer billID) {
        this.billID = billID;
    }

    public Integer getTableID() {
        return tableID;
    }

    public void setTableID(Integer tableID) {
        this.tableID = tableID;
    }

    public LocalDateTime getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDateTime billDate) {
        this.billDate = billDate;
    }

    public Integer getUserInfoID() {
        return userInfoID;
    }

    public void setUserInfoID(Integer userInfoID) {
        this.userInfoID = userInfoID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }
}
