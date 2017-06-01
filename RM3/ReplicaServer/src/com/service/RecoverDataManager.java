package com.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;

import com.beans.Customer;
import com.beans.Data;
import com.beans.Loan;
import com.config.Configuration;

//This class recovers the data. It takes new data from RM and updates its database.
public class RecoverDataManager implements Runnable {

	private BankServerImplementation bank1Obj;
	private BankServerImplementation bank2Obj;
	private BankServerImplementation bank3Obj;

	public BankServerImplementation getBank1Obj() {
		return bank1Obj;
	}

	public void setBank1Obj(BankServerImplementation bank1Obj) {
		System.out.println("1" + bank1Obj);
		this.bank1Obj = bank1Obj;
	}

	public BankServerImplementation getBank2Obj() {
		return bank2Obj;
	}

	public void setBank2Obj(BankServerImplementation bank2Obj) {
		System.out.println("2" + bank2Obj);
		this.bank2Obj = bank2Obj;
	}

	public BankServerImplementation getBank3Obj() {
		return bank3Obj;
	}

	public void setBank3Obj(BankServerImplementation bank3Obj) {
		System.out.println("3" + bank3Obj);
		this.bank3Obj = bank3Obj;
	}

	@Override
	public void run() {
		UDPListener();
	}

	// This will listen for data recovery.
	public void UDPListener() {

		int port = Configuration.DATA_RECOVERY_PORT;
		DatagramSocket socket = null;

		try {
			socket = new DatagramSocket(port);
			byte[] buffer = new byte[10000000];

			while (true) {
				DatagramPacket request = new DatagramPacket(buffer,
						buffer.length);

				socket.receive(request);

				ByteArrayInputStream baos = new ByteArrayInputStream(buffer);
				ObjectInputStream oos = new ObjectInputStream(baos);

				Data data = (Data) oos.readObject();

				if (data.getBankName().equalsIgnoreCase("TD")) {

					Character key = data.getKey();

					if (data.getCustomers() != null) {
						HashMap<Character, List<Customer>> accountMap = bank1Obj
								.getAccountMap();
						accountMap.put(key, data.getCustomers());
						bank1Obj.counter.incrementAndGet();
					} else if (data.getLoans() != null) {
						HashMap<Character, List<Loan>> loanMap = bank1Obj
								.getLoanMap();
						loanMap.put(key, data.getLoans());
						bank1Obj.loanCounter.incrementAndGet();
					}

				} else if (data.getBankName().equalsIgnoreCase("RBC")) {

					Character key = data.getKey();

					if (data.getCustomers() != null) {
						HashMap<Character, List<Customer>> accountMap = bank2Obj
								.getAccountMap();
						accountMap.put(key, data.getCustomers());
						bank2Obj.counter.incrementAndGet();
					} else if (data.getLoans() != null) {
						HashMap<Character, List<Loan>> loanMap = bank2Obj
								.getLoanMap();
						loanMap.put(key, data.getLoans());
						bank2Obj.loanCounter.incrementAndGet();
					}

				} else if (data.getBankName().equalsIgnoreCase("BMO")) {

					Character key = data.getKey();

					if (data.getCustomers() != null) {
						HashMap<Character, List<Customer>> accountMap = bank3Obj
								.getAccountMap();
						accountMap.put(key, data.getCustomers());
						bank3Obj.counter.incrementAndGet();
					} else if (data.getLoans() != null) {
						HashMap<Character, List<Loan>> loanMap = bank3Obj
								.getLoanMap();
						loanMap.put(key, data.getLoans());
						bank3Obj.loanCounter.incrementAndGet();
					}

				}

				if (data.getBankName().equalsIgnoreCase("POISON")) {

					DatagramPacket reply = new DatagramPacket(
							"FINISH".getBytes(), "FINISH".getBytes().length,
							request.getAddress(), request.getPort());

					socket.send(reply);
				} else {
					DatagramPacket reply = new DatagramPacket("OK".getBytes(),
							"OK".getBytes().length, request.getAddress(),
							request.getPort());

					socket.send(reply);
				}

			}

		} catch (SocketException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}

}
