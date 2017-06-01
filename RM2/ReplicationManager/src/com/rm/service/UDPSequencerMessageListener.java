package com.rm.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.config.Configuration;

import replica.ClientRequest;

//This listens to message from sequencer
public class UDPSequencerMessageListener {

	private BlockingQueue<ClientRequest> holdBackQueue = new ArrayBlockingQueue<ClientRequest>(
			10);
	private int RMSequenceNumber = 0;
	Map<Integer, ClientRequest> messageMap = Collections
			.synchronizedMap(new HashMap<Integer, ClientRequest>());
	private BlockingQueue<ClientRequest> deliveryQueue = null;
	private AtomicBoolean isFaulty;

	public UDPSequencerMessageListener(BlockingQueue<ClientRequest> deliveryQueue,
			AtomicBoolean isFaulty) {
		this.deliveryQueue = deliveryQueue;
		this.isFaulty = isFaulty;
	}

	// This listens to message from sequencer
	public void configureUDPMessageListener() {

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				UDPListener();
			}

			public void UDPListener() {

				int port = Configuration.UDP_SEQUENCER_MESSAGE_LISTENER_PORT;
				//DatagramSocket socket = null;
				MulticastSocket socket = null;
				
				try {
					//socket = new DatagramSocket(port);
					InetAddress group = InetAddress.getByName("224.1.0.0");
					socket = new MulticastSocket(port);
					socket.joinGroup(group);
					byte[] buffer = new byte[10000000];

					while (true) {

						

						DatagramPacket request = new DatagramPacket(buffer,
								buffer.length);

						socket.receive(request);
						System.out.println("---------------udplistening1--------------");
						
						synchronized (isFaulty) {
							if (isFaulty.get()) {
								try {
									System.out.println("UDPListener waiting");
									isFaulty.wait();
									System.out.println("UDPListener executing");
								} catch (InterruptedException e) {

									e.printStackTrace();
								}
							}
						}

						ByteArrayInputStream baos = new ByteArrayInputStream(
								buffer);
						ObjectInputStream oos = new ObjectInputStream(baos);

						ClientRequest message = (ClientRequest) oos.readObject();

						System.out.println("UDPSequencerMessageListener.UDPListener "+message.get_bank());
						System.out.println(message.getMethodName());
						System.out.println("*****" + message.getClientRequestAddress().getPort());

						try {
							System.out.println("UDPSequencerMessageListener.UDPListener before holdBackQueue "+message.get_bank()+" "+message.get_firstName());
							holdBackQueue.put(message);
							System.out.println("UDPSequencerMessageListener.UDPListener after holdBackQueue "+message.get_bank()+" "+message.get_firstName());
							System.out.println("udp4");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						DatagramPacket reply = new DatagramPacket(
								"Ok".getBytes(), "Ok".getBytes().length,
								request.getAddress(), request.getPort());

						socket.send(reply);

					}

				} catch (SocketException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					if (socket != null) {
						socket.close();
					}
				}

			}

		});

		thread.start();
	}

	// This stores the message into map
	public void storeMessage() {

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					
					

					try {
						System.out.println("udp3");
						ClientRequest message = holdBackQueue.take();

						
						synchronized (isFaulty) {
							if (isFaulty.get()) {
								try {
									System.out.println("storeMessage waiting");
									isFaulty.wait();
									System.out.println("storeMessage executing");
								} catch (InterruptedException e) {

									e.printStackTrace();
								}
							}
						}
						
						synchronized (messageMap) {
							// Here duplicate messages are dropped
							System.out.println("UDPSequencerMessageListener.storeMessage: before putting into messageMap "+message);
							messageMap.put(message.getSequence(), message);
							System.out.println("UDPSequencerMessageListener.storeMessage: after putting into messageMap "+message);
						}

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		});
		thread.start();
	}

	// This delivers the message to delivery queue
	public void addToDeliveryQueue() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {				

					searchMessage();
				}
			}

			public void searchMessage() {

				ClientRequest message = null;

				synchronized (messageMap) {
					
					message = messageMap.get(RMSequenceNumber + 1);
					
				}
				
				
				if (message == null) {

				} else {
					RMSequenceNumber++;
					System.out.println("UDPSequencerMessageListener.addToDeliveryQueue: incrementing sequence number");
					try {
						

						synchronized (isFaulty) {
							if (isFaulty.get()) {
								try {
									isFaulty.wait();
								} catch (InterruptedException e) {

									e.printStackTrace();
								}
							}
						}
						
						System.out.println("UDPSequencerMessageListener.addToDeliveryQueue: before putting into deliveryQueue"+message);
						deliveryQueue.put(message);
						System.out.println("UDPSequencerMessageListener.addToDeliveryQueue: after putting deliveryQueue"+message);
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();
	}

}
