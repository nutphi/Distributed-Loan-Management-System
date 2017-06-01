package com.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.beans.Customer;
import com.beans.Data;
import com.beans.Loan;
import com.config.Configuration;

public class DataMigrator implements Runnable {

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
		System.out.println("bank1" + bank1Obj + " bank2" + bank2Obj + " bank3"
				+ bank3Obj);
		dataMigrationListener();
	}

	/*public void dataMigrationListener() {

		int port = Configuration.DATA_MIGRATION_PORT;
		DatagramSocket socket = null;

		try {
			socket = new DatagramSocket(port);
			byte[] buffer = new byte[10000000];

			while (true) {
				DatagramPacket request = new DatagramPacket(buffer,
						buffer.length);

				socket.receive(request);
				String message = new String(new String(request.getData(), 0,
						request.getLength()));

				if (message.equalsIgnoreCase("MIGRATE")) {

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(baos);

					Data bankData1 = new Data();

					bankData1.setAccountMap(bank1Obj.getAccountMap());
					bankData1.setLoanMap(bank1Obj.getLoanMap());
					bankData1.setBankName(bank1Obj.getBankName());

					Data bankData2 = new Data();
					bankData2.setAccountMap(bank2Obj.getAccountMap());
					bankData2.setLoanMap(bank2Obj.getLoanMap());
					bankData2.setBankName(bank2Obj.getBankName());

					Data bankData3 = new Data();
					bankData3.setAccountMap(bank3Obj.getAccountMap());
					bankData3.setLoanMap(bank3Obj.getLoanMap());
					bankData3.setBankName(bank3Obj.getBankName());

					ArrayList<Data> list = new ArrayList<Data>();
					
					if (bank1Obj.getAccountMap().size() != 0) {
						//System.out.println("asd");
						list.add(bankData1);
					}
					if (bank2Obj.getAccountMap().size() != 0) {
						//System.out.println("asd");
						list.add(bankData2);
					}
					if (bank3Obj.getAccountMap().size() != 0) {
						//System.out.println("asd");
						list.add(bankData3);
					}

					// oos.writeObject(bank1Obj.getAccountMap());
					oos.writeObject(list);
					oos.flush();

					byte[] m = baos.toByteArray();

					DatagramPacket reply = new DatagramPacket(m, m.length,
							request.getAddress(), request.getPort());

					socket.send(reply);
				}
			}

		} catch (SocketException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}*/
	
	public void dataMigrationListener() {

		int port = Configuration.DATA_MIGRATION_PORT;
		DatagramSocket socket = null;
		int counter = 0;
		
		Set<Character> accountKeyObj1=null;
		Set<Character> accountKeyObj2=null;
		Set<Character> accountKeyObj3=null;
		Set<Character> loanKeyObj1=null;
		Set<Character> loanKeyObj2=null;
		Set<Character> loanKeyObj3=null;
		
		Iterator<Character> itrAcc1=null;
		Iterator<Character> itrAcc2=null;
		Iterator<Character> itrAcc3=null;
		Iterator<Character> itrLoan1=null;
		Iterator<Character> itrLoan2=null;
		Iterator<Character> itrLoan3=null;
		
		HashMap<Character, List<Customer>> accountMap1=null;
		HashMap<Character, List<Customer>> accountMap2=null;
		HashMap<Character, List<Customer>> accountMap3=null;
		HashMap<Character, List<Loan>> loanMap1=null;
		HashMap<Character, List<Loan>> loanMap2=null;
		HashMap<Character, List<Loan>> loanMap3=null;
		
		

		try {
			socket = new DatagramSocket(port);
			byte[] buffer = new byte[10000000];

			while (true) {
				DatagramPacket request = new DatagramPacket(buffer,
						buffer.length);

				socket.receive(request);
				String message = new String(new String(request.getData(), 0,
						request.getLength()));

				
				
				if (message.equalsIgnoreCase("MIGRATE")) {
					
					if(counter==0){
						
						accountKeyObj1=bank1Obj.getAccountMap().keySet();
						accountKeyObj2=bank2Obj.getAccountMap().keySet();
						accountKeyObj3=bank3Obj.getAccountMap().keySet();
						loanKeyObj1=bank1Obj.getLoanMap().keySet();
						loanKeyObj2=bank2Obj.getLoanMap().keySet();
						loanKeyObj3=bank3Obj.getLoanMap().keySet();
						
						itrAcc1=accountKeyObj1.iterator();
						itrAcc2=accountKeyObj2.iterator();
						itrAcc3=accountKeyObj3.iterator();
						
						itrLoan1=loanKeyObj1.iterator();
						itrLoan2=loanKeyObj2.iterator();
						itrLoan3=loanKeyObj3.iterator();
						
						accountMap1=bank1Obj.getAccountMap();
						accountMap2=bank2Obj.getAccountMap();
						accountMap3=bank3Obj.getAccountMap();
						loanMap1=bank1Obj.getLoanMap();
						loanMap2=bank2Obj.getLoanMap();
						loanMap3=bank3Obj.getLoanMap();
						
						
						counter++;
					}					
					
					
					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(baos);

					
					Data bankData = new Data();
					
					if(itrAcc1.hasNext()){
						System.out.println("yes2");
						Character key=itrAcc1.next();
						bankData.setKey(key);
						bankData.setCustomers(accountMap1.get(key));
						bankData.setBankName(bank1Obj.getBankName());
					}else if(itrAcc2.hasNext()){
						System.out.println("yes3");
						Character key=itrAcc2.next();
						bankData.setKey(key);
						bankData.setCustomers(accountMap2.get(key));
						bankData.setBankName(bank2Obj.getBankName());
					}else if(itrAcc3.hasNext()){
						System.out.println("yes4");
						Character key=itrAcc3.next();
						bankData.setKey(key);
						bankData.setCustomers(accountMap3.get(key));
						bankData.setBankName(bank3Obj.getBankName());
					}else if(itrLoan1.hasNext()){
						System.out.println("yes5");
						Character key=itrLoan1.next();
						bankData.setKey(key);
						bankData.setLoans(loanMap1.get(key));
						bankData.setBankName(bank1Obj.getBankName());
					}else if(itrLoan2.hasNext()){
						System.out.println("yes6");
						Character key=itrLoan2.next();
						bankData.setKey(key);
						bankData.setLoans(loanMap2.get(key));
						bankData.setBankName(bank2Obj.getBankName());
					}else if(itrLoan3.hasNext()){
						System.out.println("yes7");
						Character key=itrLoan3.next();
						bankData.setKey(key);
						bankData.setLoans(loanMap3.get(key));
						bankData.setBankName(bank3Obj.getBankName());
					}else{
						bankData.setBankName("POISON");
						counter=0;
					}
														
					
					if (!bankData.getBankName().equals("POISON")) {
						System.out.println(bankData);
					}
					

					// oos.writeObject(bank1Obj.getAccountMap());
					oos.writeObject(bankData);
					oos.flush();

					byte[] m = baos.toByteArray();

					DatagramPacket reply = new DatagramPacket(m, m.length,
							request.getAddress(), request.getPort());

					socket.send(reply);
				}else{
					
				}
			}

		} catch (SocketException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
		
		
	}
	
	

}
