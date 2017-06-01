package com.rm.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.beans.Data;
import com.config.Configuration;

//Use Poison everywhere
//isFaulty for wait
//This is for crash recovery and fault recovery
public class RecoveryManager {

	private AtomicBoolean isFaulty;
	private BlockingQueue<Data> queue= new ArrayBlockingQueue<Data>(100);
	private DataTransporter dataTransporter;
	private ReplicaServerService service;
	public static String failedRMIPAddress; 
	

	public RecoveryManager(AtomicBoolean isFaulty, ReplicaServerService service) {
		this.isFaulty = isFaulty;
		this.service = service;
	}

	public void failureListener() {

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				crashListener();
				
				//Remove this later, this just for testing
				/*isFaulty.set(true);
				DataTransporter transporter=new DataTransporter(isFaulty, queue);
				transporter.transportData();
				transporter.fetchData();
				transporter.performDataSync();
				
				getReplicaServerData();*/
			}

			// This will fetch the data from its Replica Server
			public void getReplicaServerData() {
				DatagramSocket socket = null;

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				try {
					
					socket = new DatagramSocket();

					byte[] m = "MIGRATE".getBytes();

					InetAddress host = InetAddress
							.getByName(Configuration.MIGRATION_REPLICA_SERVER_ADDRESS);
					int serverPort = Configuration.DATA_MIGRATION_PORT;
					while (true) {
						DatagramPacket request = new DatagramPacket(m,
								m.length, host, serverPort);

						socket.send(request);

						byte[] buffer = new byte[10000000];
						DatagramPacket reply = new DatagramPacket(buffer,
								buffer.length);

						socket.receive(reply);

						ByteArrayInputStream baos = new ByteArrayInputStream(
								buffer);
						ObjectInputStream oos = new ObjectInputStream(baos);

						Data data = (Data) oos
								.readObject();
						System.out.println("getReplicaServerData:"+data);

						/*
						 * HashMap<Character, List<Customer>> message =
						 * (HashMap<Character, List<Customer>>) oos
						 * .readObject();
						 */
						queue.put(data);
						if (data.getBankName().equalsIgnoreCase("POISON")) {
							break;
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			public void crashListener() {

				dataTransporter = new DataTransporter(queue);
				
				int port = Configuration.UDP_PORT_ADDRESS_REPLICA_RECOVERY;				

				DatagramSocket socket = null;

				try {
					socket = new DatagramSocket(port);
					byte[] buffer = new byte[10000000];

					while (true) {
						DatagramPacket request = new DatagramPacket(buffer,
								buffer.length);

						System.out.println("RecoveryManager.crashListener() before receive");
						socket.receive(request);
						System.out.println("RecoveryManager.crashListener() after receive");

						//CRASH_FAILURE,FAILED_REPLICA_NAME
						String failureMessage = new String(new String(request
								.getData(), 0, request.getLength()));
						
						String fail[]=failureMessage.split(",");
						
						String failureType=fail[0];
						String failedReplicaName=fail[1];
						
						System.out.println("RecoveryManager.crashListener() failureType:"+failureType+" failedReplicaName:"+failedReplicaName);
						
						if(failedReplicaName.equalsIgnoreCase("RM1")){
							failedRMIPAddress=Configuration.RM1_IP_ADDRESS;
						}else if(failedReplicaName.equalsIgnoreCase("RM2")){
							failedRMIPAddress=Configuration.RM2_IP_ADDRESS;
						}if(failedReplicaName.equalsIgnoreCase("RM3")){
							failedRMIPAddress=Configuration.RM3_IP_ADDRESS;
						}

						if (failureType
								.equalsIgnoreCase(Configuration.CRASH_FAILURE) && Configuration.REPLICA_MANAGER_NAME.equalsIgnoreCase(failedReplicaName)) {
							isFaulty.set(true);
							//Uncomment later
							service.getRunBroker().destroyForcibly();
							initializeReplicaServer();
							dataTransporter.fetchData();
							dataTransporter.performDataSync();
							
							synchronized (isFaulty) {
								if (isFaulty.get()) {
									
									isFaulty.getAndSet(false);
									isFaulty.notifyAll();
									
								}
							}

							
						} else if (failureType
								.equalsIgnoreCase(Configuration.FAULT_FAILURE) && Configuration.REPLICA_MANAGER_NAME.equalsIgnoreCase(failedReplicaName)) {
							isFaulty.set(true);
							service.getRunBroker().destroyForcibly();
							//Uncomment later
							initializeReplicaServer();
							dataTransporter.fetchData();
							dataTransporter.performDataSync();
							
							synchronized (isFaulty) {
								if (isFaulty.get()) {
									isFaulty.getAndSet(false);
									isFaulty.notifyAll();
									
								}
							}

						}
						
						
						if(!failedReplicaName.equalsIgnoreCase(Configuration.REPLICA_NAME)){
							if(failedReplicaName.equals("RM1") && Configuration.REPLICA_NAME.equalsIgnoreCase("RM3")){
								System.out.println("----give data to failed---------");
								isFaulty.set(true);
								getReplicaServerData();
								dataTransporter.transportData();
								
								synchronized (isFaulty) {
									if (isFaulty.get()) {
										isFaulty.getAndSet(false);
										isFaulty.notifyAll();
									}
								}								
							}
							
							if(failedReplicaName.equals("RM2") && Configuration.REPLICA_NAME.equalsIgnoreCase("RM1")){
								System.out.println("----give data to failed---------");
								isFaulty.set(true);
								getReplicaServerData();
								dataTransporter.transportData();
								
								synchronized (isFaulty) {
									if (isFaulty.get()) {
										isFaulty.getAndSet(false);
										isFaulty.notifyAll();
									}
								}
							}
							
							if(failedReplicaName.equals("RM3") && Configuration.REPLICA_NAME.equalsIgnoreCase("RM2")){
								System.out.println("----give data to failed---------");
								isFaulty.set(true);
								getReplicaServerData();
								dataTransporter.transportData();
								
								synchronized (isFaulty) {
									if (isFaulty.get()) {
										isFaulty.getAndSet(false);
										isFaulty.notifyAll();
									}
								}
							}
						}
						
						synchronized (isFaulty) {
							if (isFaulty.get()) {
								isFaulty.getAndSet(false);
								
								try {
									Thread.sleep(2000);
								} catch (InterruptedException e) {
								
									e.printStackTrace();
								}
								isFaulty.notifyAll();
							}
						}
																		

						DatagramPacket reply = new DatagramPacket("OK"
								.getBytes(), "OK".getBytes().length, request
								.getAddress(), request.getPort());

						socket.send(reply);

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

			public void initializeReplicaServer() {

				service = new ReplicaServerService(
						isFaulty);

				Thread thread = new Thread(service);
				thread.start();
			}

		});
		thread.start();
	}

}
