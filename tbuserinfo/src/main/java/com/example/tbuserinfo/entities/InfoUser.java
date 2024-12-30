package com.example.tbuserinfo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class InfoUser {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userInfoId;

	@Column(nullable = true, columnDefinition = "varchar(50)")
	private int accountId;

	@Column(nullable = true, columnDefinition = "varchar(255)")
	private String fullName;

	@Column(nullable = true,  columnDefinition = "varchar(10)")
	private String sex ;
	
	@Column(nullable = true,  columnDefinition = "varchar(30)")
	private String email;
	
	@Column(nullable = true,  columnDefinition = "varchar(10)")
	private String phoneNumber;
	
	public InfoUser() {}

	public int getUserInfoId() {
		return userInfoId;
	}

	public void setUserInfoId(int userInfoId) {
		this.userInfoId = userInfoId;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	
	
}
