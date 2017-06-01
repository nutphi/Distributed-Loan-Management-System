package com.test;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.config.Configuration;

import replica.ClientRequest;

public class SendSerializedObject {

	public static void main(String arg[]) {
		ClientRequest message = new ClientRequest("TD", "Anunayy", "Amar", "anunay.amar17@gmail.com", "123123", "123123");
		
		message.setMethodName(0);		
		message.setSequence(1);
		InetSocketAddress sockAddress=null;
		
		try {
			sockAddress=new InetSocketAddress(InetAddress.getByName("localhost"), 64000);
			
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		message.setClientRequestAddress(sockAddress);
				

		sendUDPRequest(message);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		message.set_firstName("Mi2shrkaa");
		message.setSequence(2);
		sendUDPRequest(message);
	}

	public static void sendUDPRequest(ClientRequest message) {

		DatagramSocket socket = null;

		try {
			socket = new DatagramSocket();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(message);
			oos.flush();

			byte[] m = baos.toByteArray();

			InetAddress host = InetAddress.getByName("localhost");
			int serverPort = Configuration.UDP_SEQUENCER_MESSAGE_LISTENER_PORT;

			DatagramPacket request = new DatagramPacket(m, m.length, host,
					serverPort);

			socket.send(request);

			byte[] buffer = new byte[10000000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

			socket.receive(reply);
			String replyMessage = new String(reply.getData(), 0,
					reply.getLength());

			System.out.println("replyMessage:" + replyMessage);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
