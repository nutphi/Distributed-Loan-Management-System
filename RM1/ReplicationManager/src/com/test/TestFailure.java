package com.test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.config.Configuration;

public class TestFailure {

	public static void main(String arg[]) {
		DatagramSocket socket = null;

		try {
			socket = new DatagramSocket();

			byte[] m = (Configuration.CRASH_FAILURE+","+"RM2").getBytes();

			InetAddress host = InetAddress.getByName("localhost");
			int serverPort = Configuration.UDP_PORT_ADDRESS_REPLICA_RECOVERY;

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
