package com.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jws.WebService;

import com.beans.Customer;
import com.beans.Loan;
import com.beans.Manager;
import com.config.Configuration;
import com.log.Logger;

@WebService(endpointInterface = "com.service.IOperations")
public class BankServerImplementation implements IOperations {

	public volatile AtomicInteger counter = new AtomicInteger(10000);
	public volatile AtomicInteger loanCounter = new AtomicInteger(200000);

	private HashMap<Character, List<Customer>> accountMap = new HashMap<Character, List<Customer>>();
	private HashMap<Character, List<Loan>> loanMap = new HashMap<Character, List<Loan>>();
	private List<Manager> managerList = new ArrayList<Manager>();

	private String bankName;
	private boolean udpServer;
	private BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(50);
	private static int accountCount=0;

	public HashMap<Character, List<Customer>> getAccountMap() {
		return accountMap;
	}

	public void setAccountMap(HashMap<Character, List<Customer>> accountMap) {
		this.accountMap = accountMap;
	}

	public HashMap<Character, List<Loan>> getLoanMap() {
		return loanMap;
	}

	public void setLoanMap(HashMap<Character, List<Loan>> loanMap) {
		this.loanMap = loanMap;
	}

	public void config(String bankName) {
		this.bankName = bankName;

		Thread loggerThread = new Thread(new Logger(blockingQueue, bankName));
		loggerThread.start();
	}

	/**
	 * This method check whether the customer has good credit standing.
	 * 
	 * @param firstName
	 * @param lastName
	 * @param phoneNumber
	 * @return True or False for success and failure respectively
	 */
	public Boolean isCreditOK(String firstName, String lastName,
			String phoneNumber) {
		List<Loan> loanList = loanMap.get(firstName.charAt(0));
		if (loanList != null) {
			Iterator<Loan> iterator = loanList.iterator();

			while (iterator.hasNext()) {
				Loan loan = iterator.next();
				Customer account = loan.getAccount();

				if (firstName.equals(account.getFirstName())
						&& lastName.equals(account.getLastName())
						&& phoneNumber.equals(account.getPhoneNumber())) {
					if (account.getCreditLimit() > 0) {
						return true;
					} else {
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * This method checks whether the customer has good credits with other banks
	 * 
	 * @param firstName
	 * @param lastName
	 * @param phoneNumber
	 */
	public String creditCheckUDP(String firstName, String lastName,
			String phoneNumber, int portNumber) {
		int port = portNumber;

		String replyMessage = "false";
		String message = "CREDIT_CHECK," + firstName + "," + lastName + ","
				+ phoneNumber;

		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();

			byte[] m = message.getBytes();
			InetAddress host = InetAddress.getByName("localhost");
			int serverPort = port;

			DatagramPacket request = new DatagramPacket(m, m.length, host,
					serverPort);
			socket.send(request);

			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

			socket.receive(reply);
			replyMessage = new String(reply.getData(), 0, reply.getLength());

			// System.out.println(bankName+" Reply:" + replyMessage);

			return replyMessage;
		} catch (SocketException e) {
			e.printStackTrace();
			return replyMessage;
		} catch (IOException e) {
			e.printStackTrace();
			return replyMessage;
		} finally {
			if (socket != null) {
				socket.close();
			}

		}

	}

	/**
	 * This method opens a bank account
	 * 
	 * @param bankName
	 * @param firstName
	 * @param lastName
	 * @param emailId
	 * @param phoneNumber
	 * @param password
	 * 
	 * @return accountNumber
	 */
	@Override
	public String openAccount(String bank, String firstName, String lastName,
			String emailId, String phoneNumber, String password) {
		
		accountCount++;
		
		if(accountCount==3){
			return "Anunay";
		}
		System.out.println("1");
		try {
			blockingQueue.put(new Date() + ": " + firstName + " " + lastName
					+ " has initiated a request to open an account at " + bank);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		Customer account = new Customer();

		account.setFirstName(firstName);
		account.setLastName(lastName);
		account.setEmailId(emailId);
		account.setPhoneNumber(phoneNumber);
		account.setPassword(password);
		account.setCreditLimit(1000);

		String accountNo = null;
		synchronized (counter) {
			System.out.println("test1");
			int accountSequence = counter.incrementAndGet();

			accountNo = firstName.charAt(0) + "" + accountSequence;
			// counter.set(new AtomicInteger(accountSequence));
			account.setAccountNumber(accountNo);
		}

		List<Customer> accountList = accountMap.get(accountNo.charAt(0));
		System.out.println("test2");
		if (accountList == null) {
			System.out.println("test3");
			accountList = new ArrayList<Customer>();
		} else {
			Iterator<Customer> accountIterator = accountList.iterator();
			System.out.println("test4");
			while (accountIterator.hasNext()) {
				Customer acct = accountIterator.next();

				if ((acct.getFirstName()).equals(firstName)
						&& (acct.getLastName().equals(lastName))
						&& (acct.getEmailId()).equals(emailId)) {

					return "Already-" + acct.getAccountNumber();

				}
				System.out.println("test5");
			}

		}

		synchronized (accountList) {
			System.out.println("test6");
			accountList.add(account);
			accountMap.put(firstName.charAt(0), accountList);
			System.out.println("test7");
		}
		System.out.println("test8");
		return accountNo;

	}

	/**
	 * This method approves or rejects a loan request.
	 * 
	 * @param accountNumber
	 * @param password
	 * @param loanAmount
	 * 
	 * @return success/failure
	 */
	@Override
	public boolean getLoan(String bank,String accountNumber, String password,
			int loanAmount) {

		// This add log information to the log file
		try {
			blockingQueue.put(new Date() + ": AccountNumber:" + accountNumber
					+ " has initiated a loan request for " + loanAmount
					+ " at " + bankName);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Customer customerAccount = getAccount(accountNumber, password);
		String loanId = null;
		int port1 = 0;
		int port2 = 0;

		if (customerAccount == null) {
			return false;
		}

		if (bankName.equals("RBC")) {
			port1 = Configuration.UDP_SERVER_2_PORT;
			port2 = Configuration.UDP_SERVER_3_PORT;
		} else if (bankName.equals("TD")) {
			port1 = Configuration.UDP_SERVER_1_PORT;
			port2 = Configuration.UDP_SERVER_3_PORT;
		} else if (bankName.equals("BMO")) {
			port1 = Configuration.UDP_SERVER_1_PORT;
			port2 = Configuration.UDP_SERVER_2_PORT;
		}

		boolean isCreditGood = Boolean.parseBoolean(creditCheckUDP(
				customerAccount.getFirstName(), customerAccount.getLastName(),
				customerAccount.getPhoneNumber(), port1))
				&& Boolean.parseBoolean(creditCheckUDP(
						customerAccount.getFirstName(),
						customerAccount.getLastName(),
						customerAccount.getPhoneNumber(), port2));

		if (!isCreditGood) {
			return false;
		}

		if (customerAccount != null) {

			if (customerAccount.getCreditLimit() >= loanAmount) {
				// Updating the new credit limit for customer

				customerAccount.setCreditLimit(customerAccount.getCreditLimit()
						- loanAmount);

				Loan loan = new Loan();

				synchronized (loanCounter) {
					loanId = accountNumber.charAt(0) + ""
							+ loanCounter.incrementAndGet();
					loan.setLoanId(loanId);
				}

				loan.setLoanAmount(loanAmount);

				Date date = new Date();
				if (loanAmount < 2000) {
					date.setMonth(date.getMonth() + 12);
				} else {
					date.setMonth(date.getMonth() + 24);
				}
				loan.setLoanDueDate(date);
				loan.setAccount(customerAccount);

				// Store the loan in loanMap, synchronized block is present in
				// saveLoan
				saveLoan(loanId, loan);

				updateAccountMap(customerAccount);

				/* System.out.println(accountMap); */

				return true;
			}
		}

		// System.out.println(accountMap);

		return false;
	}

	/**
	 * This method takes accountNumner and password and returns customer
	 * account.
	 * 
	 * @param accountNumber
	 * @param password
	 * @return CustomerAccount
	 */
	public Customer getAccount(String accountNumber, String password) {
		List<Customer> accountList = accountMap.get(accountNumber.charAt(0));

		if (accountList != null) {
			Iterator<Customer> iterator = accountList.iterator();

			while (iterator.hasNext()) {
				Customer account = iterator.next();

				if (account.getAccountNumber().equals(accountNumber)
						&& account.getPassword().equals(password)) {

					return account;
				}
			}
			return null;

		} else {
			return null;
		}

	}

	/**
	 * It updates the accountMap with an update account object.
	 * 
	 * @param account
	 * @return
	 */

	public boolean updateAccountMap(Customer account) {
		List<Customer> accountList = accountMap.get(account.getAccountNumber()
				.charAt(0));
		boolean accountPresent = false;

		if (accountList != null) {
			Iterator<Customer> iterator = accountList.iterator();

			while (iterator.hasNext()) {
				Customer customerAccount = iterator.next();

				if (account == customerAccount) {
					accountPresent = true;
					break;
				}
			}

		}

		if (accountPresent) {
			synchronized (accountList) {
				accountList.remove(account);
				accountList.add(account);
				accountMap.put(account.getAccountNumber().charAt(0),
						accountList);
			}
			return true;
		}

		return false;
	}

	/**
	 * It deletes an account from the accountMap.
	 * 
	 * @param account
	 * @return
	 */

	public boolean deleteAccount(Customer account) {
		List<Customer> accountList = accountMap.get(account.getAccountNumber()
				.charAt(0));

		if (accountList != null) {
			Iterator<Customer> iterator = accountList.iterator();

			while (iterator.hasNext()) {
				Customer customerAccount = iterator.next();

				if (account.getAccountNumber().equals(
						customerAccount.getAccountNumber())) {
					iterator.remove();
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * It stores the loan object into a loanMap.
	 * 
	 * @param loanId
	 * @param loan
	 */
	public void saveLoan(String loanId, Loan loan) {
		List<Loan> loanList = loanMap.get(loanId.charAt(0));
		if (loanList == null) {
			loanList = new ArrayList<Loan>();
		}
		synchronized (loanList) {
			loanList.add(loan);
			loanMap.put(loanId.charAt(0), loanList);
		}
	}

	@Override
	public boolean delayPayment(String bank, String loanId,
			String currentDueDate, String newDueDate) {

		// This add log information to the log file

		try {
			blockingQueue.put(new Date()
					+ ": Manager has initiated a request to delay payment for "
					+ loanId + " at " + bank);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		Date currentLoanDueDate = null;
		Date newLoanDueDate = null;

		try {
			currentLoanDueDate = df.parse(currentDueDate);
			newLoanDueDate = df.parse(newDueDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		List<Loan> loanList = loanMap.get(loanId.charAt(0));

		if (loanList != null) {
			Iterator<Loan> loanIterator = loanList.iterator();

			Loan loan = null;
			boolean loanFound = false;

			while (loanIterator.hasNext()) {
				loan = loanIterator.next();

				if (loan.getLoanId().equals(loanId)) {
					loanFound = true;
					break;
				}
			}

			if (loanFound) {
				synchronized (loanList) {
					loanList.remove(loan);
					loanMap.remove(loanId.charAt(0));

					loan.setLoanDueDate(newLoanDueDate);
					loanList.add(loan);

					loanMap.put(loanId.charAt(0), loanList);
				}

				return true;
			}
		}

		return false;
	}

	/**
	 * Prints customer and loan info
	 * 
	 * @return customerInfo
	 */
	@Override
	public String printCustomerInfo(String bank) {
		// This add log information to the log file

		String resultSet = "";

		try {
			blockingQueue
					.put(new Date()
							+ ": Manager has initiated a request for printing customer information at "
							+ bank);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		List<List<Customer>> accountList = new ArrayList<List<Customer>>(
				accountMap.values());
		Iterator<List<Customer>> iterator = accountList.iterator();

		System.out.println("\n CUSTOMER ACCOUNT INFORMATION - "
				+ bankName.toUpperCase()
				+ "\n__________________________________________________");
		resultSet = resultSet.concat("\n\n CUSTOMER ACCOUNT INFORMATION - "
				+ bankName.toUpperCase()
				+ "\n__________________________________________________");
		while (iterator.hasNext()) {
			List<Customer> accList = iterator.next();

			Iterator<Customer> itr = accList.iterator();

			while (itr.hasNext()) {
				Customer cust = itr.next();
				System.out.println(cust);
				resultSet = resultSet.concat("\n" + cust.toString());
			}
		}

		List<List<Loan>> loanList = new ArrayList<List<Loan>>(loanMap.values());
		Iterator<List<Loan>> loanIterator = loanList.iterator();

		System.out.println("\n CUSTOMER LOAN INFORMATION - "
				+ bankName.toUpperCase()
				+ "\n__________________________________________________");
		resultSet = resultSet.concat("\n\n CUSTOMER LOAN INFORMATION - "
				+ bankName.toUpperCase()
				+ "\n__________________________________________________");

		while (loanIterator.hasNext()) {
			List<Loan> lnList = loanIterator.next();

			Iterator<Loan> itr = lnList.iterator();

			while (itr.hasNext()) {
				Loan ln = itr.next();
				System.out.println(ln);
				resultSet = resultSet.concat("\n" + ln.toString());
			}
		}

		return resultSet;
	}

	/**
	 * This method transfers loan to another bank using UDP
	 * 
	 * @param firstName
	 * @param lastName
	 * @param emailId
	 * @param phoneNumber
	 * @param password
	 * @param loanAmount
	 * @param loanDueDate
	 * @param portNumber
	 * 
	 * @return success/failure
	 */
	public String transferLoanUDP(String firstName, String lastName,
			String emailId, String phoneNumber, String password,
			double loanAmount, String loanDueDate, int portNumber) {
		int port = portNumber;

		String replyMessage = "false";
		String message = "TRANSFER_LOAN," + firstName + "," + lastName + ","
				+ emailId + "," + phoneNumber + "," + password + ","
				+ loanAmount + "," + loanDueDate;

		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();

			byte[] m = message.getBytes();
			InetAddress host = InetAddress.getByName("localhost");
			int serverPort = port;

			DatagramPacket request = new DatagramPacket(m, m.length, host,
					serverPort);
			socket.send(request);

			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

			socket.receive(reply);
			replyMessage = new String(reply.getData(), 0, reply.getLength());

			return replyMessage;
		} catch (SocketException e) {
			e.printStackTrace();
			return replyMessage;
		} catch (IOException e) {
			e.printStackTrace();
			return replyMessage;
		} finally {
			if (socket != null) {
				socket.close();
			}

		}

	}

	/**
	 * This method transfers loan from one bank to another.
	 * 
	 * @param loanId
	 * @param currentBank
	 * @param otherBank
	 * @param transferStatus
	 */

	@Override
	public boolean transferLoan(String loanId, String currentBank,
			String otherBank) {

		int portNumber = 0;

		if (otherBank.equalsIgnoreCase("RBC")) {
			portNumber = Configuration.UDP_SERVER_1_PORT;
		} else if (otherBank.equalsIgnoreCase("TD")) {
			portNumber = Configuration.UDP_SERVER_2_PORT;
		} else if (otherBank.equalsIgnoreCase("BMO")) {
			portNumber = Configuration.UDP_SERVER_3_PORT;
		}

		List<Loan> loanList = loanMap.get(loanId.charAt(0));

		Customer customer = null;

		if (loanList != null) {
			Iterator<Loan> loanIterator = loanList.iterator();

			Loan loan = null;

			while (loanIterator.hasNext()) {
				loan = loanIterator.next();

				if (loan.getLoanId().equals(loanId)) {

					customer = loan.getAccount();

					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					String loanDueDate = sdf.format(loan.getLoanDueDate());

					String message = transferLoanUDP(customer.getFirstName(),
							customer.getLastName(), customer.getEmailId(),
							customer.getPhoneNumber(), customer.getPassword(),
							loan.getLoanAmount(), loanDueDate, portNumber);
					if (message.equalsIgnoreCase("Success")) {

						// Note: Here iterator is pointing to a loanlist that's
						// why I have used synchronized block on loanlist and
						// not on iterator
						synchronized (loanList) {
							loanIterator.remove();
						}
						customer.setCreditLimit(customer.getCreditLimit()
								+ loan.getLoanAmount());
						updateAccountMap(customer);

						return true;
					}
					break;
				}
			}

		}

		return false;
	}

	public String getBankName() {
		return bankName;
	}

}
