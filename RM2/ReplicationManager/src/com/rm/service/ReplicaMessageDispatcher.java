package com.rm.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.config.Configuration;

import replica.ClientRequest;
import replica.ReplicaResponse;

public class ReplicaMessageDispatcher {

	private BlockingQueue<ClientRequest> deliveryQueue = null;
	private BlockingQueue<ReplicaResponse> responseQueue = new ArrayBlockingQueue<ReplicaResponse>(
			10);
	private AtomicBoolean isFaulty;
	
	public ReplicaMessageDispatcher(BlockingQueue<ClientRequest> deliveryQueue, AtomicBoolean isFaulty) {
		this.deliveryQueue = deliveryQueue;
		this.isFaulty=isFaulty;
	}

	public void dispatchMessageReplica() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				System.out.println("rmd1");
				sendMessage();
			}

			public void sendMessage() {
				DatagramSocket socket = null;
				System.out.println("rmd2");
				try {
					socket = new DatagramSocket();

					while (true) {
						
						System.out.println("ReplicaMessageDispatcher.sendMessage(): before taking from deliveryQueue");
						ClientRequest message = deliveryQueue.take();
						System.out.println("ReplicaMessageDispatcher.sendMessage(): after taking from deliveryQueue");
						System.out.println(""+message.get_bank()+" "+message.get_firstName()+" "+message.get_lastName());
						
						synchronized (isFaulty) {
							if (isFaulty.get()) {
								try {
									System.out.println("sendMessage waiting");
									isFaulty.wait();
									System.out.println("sendMessage executing");
								} catch (InterruptedException e) {

									e.printStackTrace();
								}
							}
						}
						
						System.out.println(message.getMethodName()
								+ " -opType");
						int operationType = message.getMethodName();

						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						ObjectOutputStream oos = new ObjectOutputStream(baos);
						oos.writeObject(message);
						oos.flush();

						byte[] m = baos.toByteArray();

						InetAddress host = InetAddress.getByName("localhost");
						int serverPort = Configuration.NEW_UDP_DISPATCHER_PORT;

						DatagramPacket request = new DatagramPacket(m,
								m.length, host, serverPort);

						System.out.println("ReplicaMessageDispatcher.sendMessage(): before sending to Replica server");
						socket.send(request);
						System.out.println("ReplicaMessageDispatcher.sendMessage(): after sending to Replica server");

						byte[] buffer = new byte[10000000];
						DatagramPacket reply = new DatagramPacket(buffer,
								buffer.length);

						System.out.println("sendMessage: before receive");
						try{
							socket.setSoTimeout(500);
							socket.receive(reply);
							System.out.println("sendMessage: after receive");
						
							String replyMessage = new String(reply.getData(), 0,
								reply.getLength());
						
						
						
							System.out.println("ReplicaMessageDispatcher.sendMessage(): before sendMessageFe replyMessage:"+replyMessage+ " "+ message.get_bank()+" "+message.getMethodName());
							//Comment code for time being
							sendMessageFE(message.getMethodName(),
								message.getClientRequestAddress().getAddress(), message.getClientRequestAddress().getPort(),
								replyMessage);

							System.out.println("ReplicaMessageDispatcher.sendMessage(): after sendMessageFe");
						}catch(SocketTimeoutException e){
							System.out.println("Dropping packet");
						}

					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (socket != null) {
						socket.close();
					}
				}
			}

			public void sendMessageFE(int operationType,
					InetAddress address, int port, String result) {
				
				System.out.println("ReplicaMessageDispatcher.sendMessageFE(): in sendMessageFE");
				ReplicaResponse response = new ReplicaResponse();
				response.setReplicaName(Configuration.REPLICA_NAME);
				//response.setDetectionAndRecoveryPort(Configuration.UDP_PORT_ADDRESS_REPLICA_RECOVERY);
				
				if (operationType==0) {
					response.setResultOpenAccount(result);
				} else if (operationType==1) {
					response.setResultGetLoan(Boolean.parseBoolean(result));
				} else if (operationType==2) {
					response.setResultTransferLoan(Boolean.parseBoolean(result));
				} else if (operationType==3) {
					response.setResultDelayPayment(Boolean.parseBoolean(result));
				} else if (operationType==4) {

				}

				
				DatagramSocket socket = null;

				try {
					socket = new DatagramSocket();

					System.out.println("ReplicaMessageDispatcher.sendMessageFE(): before writeObject");
					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(baos);
					oos.writeObject(response);
					oos.flush();

					System.out.println("ReplicaMessageDispatcher.sendMessage(): after writeObject");
					System.out.println("sendMessageFE:"+response+" port:"+port);
					byte[] m = baos.toByteArray();

					DatagramPacket request = new DatagramPacket(m, m.length,
							address, port);

					System.out.println("ReplicaMessageDispatcher.sendMessage(): before send socket request , Loan Result:"+ response.getResultGetLoan()+" REPLICA_NAME"+ response.getReplicaName());					
					socket.send(request);
					System.out.println("ReplicaMessageDispatcher.sendMessage(): after send sendMessageFe");
					
					byte[] buffer = new byte[10000000];
					DatagramPacket reply = new DatagramPacket(buffer,
							buffer.length);

					System.out.println("ReplicaMessageDispatcher.sendMessage(): before receive socket sendMessageFe");
					socket.receive(reply);
					System.out.println("ReplicaMessageDispatcher.sendMessage(): after receive sendMessageFe");
					
				} catch (SocketException e) {
					e.printStackTrace();
				} catch(IOException e){
					e.printStackTrace();
				}finally{
					socket.close();
				}

			}

		});

		thread.start();
	}
}
