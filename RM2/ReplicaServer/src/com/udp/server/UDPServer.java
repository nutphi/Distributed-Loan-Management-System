package com.udp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.omg.CORBA.BooleanHolder;
import org.omg.CORBA.StringHolder;

import com.beans.Customer;
import com.config.Configuration;
import com.service.BankServerImplementation;

public class UDPServer implements Runnable {

	private boolean udpServer = false;
	private String bankName;
	BankServerImplementation bankServer;

	public UDPServer(String bankName, BankServerImplementation bankServer) {
		super();
		this.bankName = bankName;
		this.bankServer = bankServer;
	}

	@Override
	public void run() {
		bankUDPListener();
	}

	/**
	 * This creates a bank UDP server. This UDP server listens for the incoming
	 * request from the client.
	 */
	public void bankUDPListener() {

		int port = 0;
		if (bankName.equals("RBC")) {
			port = Configuration.UDP_SERVER_1_PORT;
		} else if (bankName.equals("TD")) {
			port = Configuration.UDP_SERVER_2_PORT;
		} else if (bankName.equals("BMO")) {
			port = Configuration.UDP_SERVER_3_PORT;
		}

		// UDP Server is on
		udpServer = true;

		DatagramSocket socket = null;

		try {
			socket = new DatagramSocket(port);
			byte[] buffer = new byte[1000];

			while (true) {
				DatagramPacket request = new DatagramPacket(buffer,
						buffer.length);

				socket.receive(request);

				String message = new String(request.getData(), 0,
						request.getLength());

				String messagePart[] = message.split(",");

				if (messagePart[0].equals("CREDIT_CHECK")) {

					String replyMessage = bankServer.isCreditOK(messagePart[1],
							messagePart[2], messagePart[3]).toString();

					DatagramPacket reply = new DatagramPacket(
							replyMessage.getBytes(),
							replyMessage.getBytes().length,
							request.getAddress(), request.getPort());

					socket.send(reply);
				} else if (messagePart[0].equals("TRANSFER_LOAN")) {
					
					String firstName = messagePart[1];
					String lastName = messagePart[2];
					String emailId = messagePart[3];
					String phoneNumber = messagePart[4];
					String password = messagePart[5];
					int loanAmount = Integer.parseInt(messagePart[6]);
					String loanDueDate = messagePart[7];

					// This creates a customer account if it is not already
					// there, otherwise it will just return
					String accountNumber = bankServer
							.openAccount(bankName, firstName, lastName,
									emailId, phoneNumber, password);

					if (accountNumber.contains("Already")) {
						accountNumber = accountNumber.split("-")[1];
					}

					Boolean loanStatus = bankServer.getLoan(bankName, accountNumber,
							password, loanAmount);

					String replyMessage = null;

					if (loanStatus) {
						replyMessage = "Success";
					} else {

						// This is for atomic handling, if loan was not issued
						// in that case the created customer account is deleted.

						if (!accountNumber.contains("Already")) {
							Customer customer = new Customer();
							customer.setAccountNumber(accountNumber);
							bankServer.deleteAccount(customer);
						}
						replyMessage = "Failure";
					}

					DatagramPacket reply = new DatagramPacket(
							replyMessage.getBytes(),
							replyMessage.getBytes().length,
							request.getAddress(), request.getPort());

					socket.send(reply);
				}
			}

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				socket.close();
			}
		}

	}

	/**
	 * Checks the status of isUDPServerOn
	 * 
	 * @return
	 */
	public boolean isUDPServerOn() {
		return udpServer;
	}

}
