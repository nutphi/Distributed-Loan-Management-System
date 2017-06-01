package com.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Data implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * private HashMap<Character, List<Customer>> accountMap; private
	 * HashMap<Character, List<Loan>> loanMap; private String bankName;
	 * 
	 * public HashMap<Character, List<Customer>> getAccountMap() { return
	 * accountMap; } public void setAccountMap(HashMap<Character,
	 * List<Customer>> accountMap) { this.accountMap = accountMap; } public
	 * HashMap<Character, List<Loan>> getLoanMap() { return loanMap; } public
	 * void setLoanMap(HashMap<Character, List<Loan>> loanMap) { this.loanMap =
	 * loanMap; } public String getBankName() { return bankName; } public void
	 * setBankName(String bankName) { this.bankName = bankName; }
	 * 
	 * public String toString(){ return accountMap+ " "+loanMap+" "+bankName; }
	 */

	private Character key;
	private List<Customer> customers;
	private List<Loan> loans;
	private String bankName;

	public Character getKey() {
		return key;
	}

	public void setKey(Character key) {
		this.key = key;
	}

	public List<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}

	public List<Loan> getLoans() {
		return loans;
	}

	public void setLoans(List<Loan> loans) {
		this.loans = loans;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String toString(){ return customers+ " "+loans+" "+bankName; }
}
