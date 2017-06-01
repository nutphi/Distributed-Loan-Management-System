package com.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.config.Configuration;

import replica.ClientRequest;

public class RequestDispatcher implements Runnable {

	private BankServerImplementation bank1Obj;
	private BankServerImplementation bank2Obj;
	private BankServerImplementation bank3Obj;

	public BankServerImplementation getBank1Obj() {
		return bank1Obj;
	}

	public void setBank1Obj(BankServerImplementation bank1Obj) {
		System.out.println("1"+bank1Obj);
		this.bank1Obj = bank1Obj;
	}

	public BankServerImplementation getBank2Obj() {
		return bank2Obj;
	}

	public void setBank2Obj(BankServerImplementation bank2Obj) {
		System.out.println("2"+bank2Obj);
		this.bank2Obj = bank2Obj;
	}

	public BankServerImplementation getBank3Obj() {
		return bank3Obj;
	}

	public void setBank3Obj(BankServerImplementation bank3Obj) {
		System.out.println("3"+bank3Obj);
		this.bank3Obj = bank3Obj;
	}

	public void run() {
		System.out.println("bank1"+bank1Obj+" bank2"+bank2Obj+" bank3"+bank3Obj);
		UDPListener();
	}

	public void UDPListener() {

		int port = Configuration.NEW_UDP_DISPATCHER_PORT;
		DatagramSocket socket = null;

		String accountNumber = null;
		Boolean getLoanStatus = false;
		Boolean delayPaymentStatus = false;
		Boolean transferLoanStatus = false;

		String testReply = null;
		System.out.println("bank1"+bank1Obj+" bank2"+bank2Obj+" bank3"+bank3Obj);

		try {
			socket = new DatagramSocket(port);
			byte[] buffer = new byte[10000000];

			while (true) {
				DatagramPacket request = new DatagramPacket(buffer,
						buffer.length);

				System.out.println("*********************before receive******************");				
				socket.receive(request);
				System.out.println("*********************after receive******************");
				
				ByteArrayInputStream baos = new ByteArrayInputStream(buffer);
				
				System.out.println("udpListener: after baos");
				
				ObjectInputStream oos = new ObjectInputStream(baos);
				
				System.out.println("udpListener: after oos");
				
				

				ClientRequest message = (ClientRequest) oos.readObject();
				
				System.out.println("udpListener: after message");
				
				System.out.println("RS Message -1");
				if (message.get_bank().equalsIgnoreCase("TD")) {
					System.out.println("RS Message -2");
					if (message.getMethodName()==0) {
						System.out.println("RS Message -3");
						System.out.println(bank1Obj);
						accountNumber = bank1Obj.openAccount(
								message.get_bank(),
								message.get_firstName(),
								message.get_lastName(),
								message.get_emailAddress(),
								message.get_phoneNumber(),
								message.get_password());
						System.out.println("RS Message -4");
						testReply = accountNumber;
						System.out.println("RequestDispatcher.UDPListener(): "+testReply);
					} else if (message.getMethodName()==1) {
						getLoanStatus = bank1Obj.getLoan(message.get_bank(),
								message.get_accountNumber(),
								message.get_password(),
								message.get_loanAmount());
						testReply = getLoanStatus.toString();
						System.out.println("RequestDispatcher.UDPListener(): "+testReply);
					} else if (message.getMethodName()==3) {
						delayPaymentStatus = bank1Obj.delayPayment(
								message.get_bank(),
								message.get_loanID(),
								message.getCurrentDate(),
								message.getNewDuedate());
						testReply = delayPaymentStatus.toString();
					} else if (message.getMethodName()==2) {
						transferLoanStatus = bank1Obj.transferLoan(
								message.get_loanID(),
								message.get_CurrentBank(),
								message.get_OtherBank());
						testReply = transferLoanStatus.toString();
						System.out.println("RequestDispatcher.UDPListener(): "+testReply);
					}
				} else if (message.get_bank().equalsIgnoreCase("RBC")) {
					System.out.println("RS Message -2");
					if (message.getMethodName()==0) {
						System.out.println("RS Message -3");
						System.out.println(bank2Obj);
						accountNumber = bank2Obj.openAccount(
								message.get_bank(),
								message.get_firstName(),
								message.get_lastName(),
								message.get_emailAddress(),
								message.get_phoneNumber(),
								message.get_password());
						System.out.println("RS Message -4");
						testReply = accountNumber;
						System.out.println("RequestDispatcher.UDPListener(): "+testReply);
					} else if (message.getMethodName()==1) {
						getLoanStatus = bank2Obj.getLoan(message.get_bank(),
								message.get_accountNumber(),
								message.get_password(),
								message.get_loanAmount());
						testReply = getLoanStatus.toString();
						System.out.println("RequestDispatcher.UDPListener(): "+testReply);
					} else if (message.getMethodName()==3) {
						delayPaymentStatus = bank2Obj.delayPayment(
								message.get_bank(),
								message.get_loanID(),
								message.getCurrentDate(),
								message.getNewDuedate());
						testReply = delayPaymentStatus.toString();
						System.out.println("RequestDispatcher.UDPListener(): "+testReply);
					} else if (message.getMethodName()==2) {
						transferLoanStatus = bank2Obj.transferLoan(
								message.get_loanID(),
								message.get_CurrentBank(),
								message.get_OtherBank());
						testReply = transferLoanStatus.toString();
						System.out.println("RequestDispatcher.UDPListener(): "+testReply);
					}
				}  else if (message.get_bank().equalsIgnoreCase("BMO")) {
					System.out.println("RS Message -2");
					if (message.getMethodName()==0) {
						System.out.println("RS Message -3");
						System.out.println(bank3Obj);
						accountNumber = bank3Obj.openAccount(
								message.get_bank(),
								message.get_firstName(),
								message.get_lastName(),
								message.get_emailAddress(),
								message.get_phoneNumber(),
								message.get_password());
						testReply = accountNumber;
						System.out.println("RequestDispatcher.UDPListener(): "+testReply);
					} else if (message.getMethodName()==1) {
						getLoanStatus = bank3Obj.getLoan(message.get_bank(),
								message.get_accountNumber(),
								message.get_password(),
								message.get_loanAmount());
						testReply = getLoanStatus.toString();
						System.out.println("RequestDispatcher.UDPListener(): "+testReply);
					} else if (message.getMethodName()==3) {
						delayPaymentStatus = bank3Obj.delayPayment(
								message.get_bank(),
								message.get_loanID(),
								message.getCurrentDate(),
								message.getNewDuedate());
						testReply = delayPaymentStatus.toString();
						System.out.println("RequestDispatcher.UDPListener(): "+testReply);
					} else if (message.getMethodName()==2) {
						transferLoanStatus = bank3Obj.transferLoan(
								message.get_loanID(),
								message.get_CurrentBank(),
								message.get_OtherBank());
						testReply = transferLoanStatus.toString();
						System.out.println("RequestDispatcher.UDPListener(): "+testReply);
					}
				} 
				
				System.out.println("RequestDispatcher.updListener before reply");
				DatagramPacket reply = new DatagramPacket(testReply.getBytes(),
						testReply.getBytes().length, request.getAddress(),
						request.getPort());
				
				socket.send(reply);
				System.out.println("RequestDispatcher.updListener after reply");
			}

		} catch (SocketException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("classNotFound"+e.getMessage());
			e.printStackTrace();
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}

}
