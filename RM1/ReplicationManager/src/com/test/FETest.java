package com.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.config.Configuration;

import replica.ReplicaResponse;

public class FETest {

	public static void main(String arg[]) {
		int port = Configuration.UDP_SEQUENCER_MESSAGE_LISTENER_PORT;
		DatagramSocket socket = null;

		try {
			socket = new DatagramSocket(64000);
			byte[] buffer = new byte[10000000];

			System.out.println("FETest:");
			while (true) {
				
				DatagramPacket request = new DatagramPacket(buffer,
						buffer.length);

				socket.receive(request);
				

				ByteArrayInputStream baos = new ByteArrayInputStream(
						buffer);
				ObjectInputStream oos = new ObjectInputStream(baos);

				ReplicaResponse response = (ReplicaResponse) oos.readObject();
				System.out.println("FETest:"+response);

				System.out.println(response.getReplicaName()+" "+response.getResultDelayPayment()+" "+response.getResultGetLoan()+" "+response.getResultTransferLoan()+" "+response.getResultOpenAccount());								

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
}
