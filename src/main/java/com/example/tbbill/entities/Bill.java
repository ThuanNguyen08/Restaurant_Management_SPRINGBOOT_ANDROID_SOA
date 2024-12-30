package com.example.tbbill.entities;

import java.time.LocalDateTime;
import jakarta.persistence.*;


@Entity
@Table(name = "tbbill")
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer billID;
    
    private Integer tableID;
    private Integer userInfoID;
    private LocalDateTime billDate;
    private Integer totalAmount;
    private String status; // PENDING, PAID, CANCELLED
    
    public Bill() {}
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
	public Integer getUserInfoID() {
		return userInfoID;
	}
	public void setUserInfoID(Integer userInfoID) {
		this.userInfoID = userInfoID;
	}
	public LocalDateTime getBillDate() {
		return billDate;
	}
	public void setBillDate(LocalDateTime billDate) {
		this.billDate = billDate;
	}
	public Integer getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Integer totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
    
    
    
}