package com.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * This endpoint interface
 * @author Anunay
 *
 */

public interface IOperations {


	public String openAccount( String bank,
			 String firstName,
			 String lastName,
			 String emailId,
			 String phoneNumber,
			 String password);


	public boolean getLoan(String bank, String accountNumber, String password,
			 int loanAmount);


	public boolean delayPayment(String bank, String loanId,
			 String currentDueDate,  String newDueDate);


	public String printCustomerInfo(String bank);


	public boolean transferLoan(String loanId,String currentBank,
			String otherBank);

}
