package com.example.SpringBoot.model;

public class StudentDTO {
	
	private String accountNumber;
	private String shortName;
	private String office;
	
	public StudentDTO(String accountNumber, String shortName, String office) {
		super();
		this.accountNumber = accountNumber;
		this.shortName = shortName;
		this.office = office;
	}
	
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getOffice() {
		return office;
	}
	public void setOffice(String office) {
		this.office = office;
	}

}
