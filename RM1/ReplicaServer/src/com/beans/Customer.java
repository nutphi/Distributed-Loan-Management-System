package com.beans;

import java.io.Serializable;

/**
 * This is a customer class which stores customer related information.
 * @author Anunay
 *
 */
public class Customer extends User implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String accountNumber;
	private double creditLimit;

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public double getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(double creditLimit) {
		this.creditLimit = creditLimit;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Customer) {
			Customer account = (Customer) obj;
			if (accountNumber.equals(account.getAccountNumber())
					&& getPassword().equals(account.getPassword())) {
				return true;
			}
		}
		return false;
	}

	public String toString() {

		String message = "Account Number: " + accountNumber + ", First Name: "
				+ getFirstName() + ", Last Name: " + getLastName()
				+ ", Password: " + getPassword() + ", Email ID: "
				+ getEmailId() + ", Phone Number: " + getPhoneNumber()+ ", Credit Limit: " + creditLimit;

		return message;
	}
}
