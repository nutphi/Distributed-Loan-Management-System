package com.rm.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.beans.Data;
import com.config.Configuration;

public class DataTransporter {
	
	private BlockingQueue<Data> queue;
	private BlockingQueue<Data> dataSynchronizationQueue = new ArrayBlockingQueue<Data>(
			100);
	private boolean isDataMigrationComplete=false;
	DatagramSocket fetchDataSocket;
	

	public DataTransporter(BlockingQueue<Data> queue) {
		try {
			fetchDataSocket=new DatagramSocket(Configuration.FETCH_DATA_ON_PORT);
		} catch (SocketException e) {			
			e.printStackTrace();
		}
		this.queue = queue;
	}

	// This method takes the data from queue and transport to other RM
	public void transportData() {

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				boolean breakLoopFlag = false;
				while (true) {

					DatagramSocket socket = null;

					try {
						socket = new DatagramSocket();

						// Change the localhost address with the fail
						// RM(i.e.
						// Replica-Server) address.
						InetAddress host = InetAddress.getByName(RecoveryManager.failedRMIPAddress);
						int serverPort = Configuration.FETCH_DATA_ON_PORT;

						while (true) {

							Data data = queue.take();

							System.out.println("transportData:"+data);
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							ObjectOutputStream oos = new ObjectOutputStream(
									baos);
							

							oos.writeObject(data);
							oos.flush();

							byte[] m = baos.toByteArray();

							DatagramPacket request = new DatagramPacket(m,
									m.length, host, serverPort);

							socket.send(request);

							byte[] buffer = new byte[10000000];
							DatagramPacket reply = new DatagramPacket(buffer,
									buffer.length);

							System.out.println("---------------transportData-----------");
							socket.receive(reply);

							String replyMessage = new String(reply.getData(),
									0, reply.getLength());

							System.out.println("DataTransporter:"
									+ replyMessage);
							
							if (data.getBankName().equals("POISON")) {

								breakLoopFlag = true;
								break;
							}

						}
						

						if (breakLoopFlag) {
							break;
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			}
		});
		thread.start();
	}

	// This RM receives the data from another RM, and stores it into
	// synchronization queue.
	public void fetchData() {

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				boolean breakLoopFlag = false;
				while (true) {
					
					//int port = Configuration.FETCH_DATA_ON_PORT;
					//DatagramSocket fetchDataSocket = null;

					try {
						//fetchDataSocket = new DatagramSocket(port);
						byte[] buffer = new byte[10000000];

						while (true) {

							DatagramPacket request = new DatagramPacket(buffer,
									buffer.length);
							fetchDataSocket.receive(request);

							ByteArrayInputStream baos = new ByteArrayInputStream(
									buffer);
							ObjectInputStream oos = new ObjectInputStream(baos);
							Data data = (Data) oos.readObject();
							System.out.println("fetchData:"+data);
							
							try {
								System.out.println("datasync:" + data);
								dataSynchronizationQueue.put(data);
								

							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							DatagramPacket reply = new DatagramPacket("Ok"
									.getBytes(), "Ok".getBytes().length,
									request.getAddress(), request.getPort());

							fetchDataSocket.send(reply);
							
							if (data.getBankName().equals("POISON")) {
								breakLoopFlag=true;
								break;
							}

						}
						//fetchDataSocket.close();
						

					} catch (SocketException e) {
						System.out.println("ERROR: fetchData:"+e.getMessage());
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} finally {
						/*if (fetchDataSocket != null) {
							fetchDataSocket.close();
						}*/
					}
					//Anunay update the change
					if(breakLoopFlag){
						break;
					}
				}

			}
		});

		thread.start();
	}

	// This method transfers the data to Replica-Server
	public void performDataSync() {

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				boolean breakLoopFlag=false;
				while (true) {

					DatagramSocket socket = null;

					try {
						socket = new DatagramSocket();
						
						InetAddress host = InetAddress.getByName("localhost");
						int serverPort = Configuration.DATA_RECOVERY_PORT;

						while (true) {

							Data data = dataSynchronizationQueue.take();
							System.out.println("performDataSync:"+data);

							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							ObjectOutputStream oos = new ObjectOutputStream(
									baos);

							oos.writeObject(data);
							oos.flush();

							byte[] m = baos.toByteArray();

							DatagramPacket request = new DatagramPacket(m,
									m.length, host, serverPort);

							if (data.getBankName().equals("POISON")) {
								socket.send(request);
								breakLoopFlag = true;
								break;
							}
							socket.send(request);

							byte[] buffer = new byte[10000000];
							DatagramPacket reply = new DatagramPacket(buffer,
									buffer.length);

							socket.receive(reply);

							String replyMessage = new String(reply.getData(),
									0, reply.getLength());

							System.out.println("DataTransporter:"
									+ replyMessage);
						}
						

						if (breakLoopFlag) {
							isDataMigrationComplete=true;
							break;
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			}
		});

		thread.start();
	}
}
