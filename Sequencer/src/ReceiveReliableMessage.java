import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;

import replica.ClientRequest;

public class ReceiveReliableMessage implements Runnable {
	DatagramSocket ds;
	DatagramPacket dp;
	ClientRequest message;
	Queue<ClientRequest> queue;

	public ReceiveReliableMessage()  throws SocketException{
		queue = new LinkedList<ClientRequest>();
		ds = new DatagramSocket(Configuration.SEQUENCER_PORT);
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		message = null;
		while (true) {
			try {
				byte receiveBytes[] = new byte[4096];
				ByteArrayInputStream baos = new ByteArrayInputStream(receiveBytes);
				dp = new DatagramPacket(receiveBytes, receiveBytes.length);
				ds.receive(dp);
				System.out.println(queue.size());
				ObjectInputStream oos = new ObjectInputStream(baos);
				queue.add((ClientRequest) oos.readObject());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public ClientRequest getClientRequest() {
		return queue.remove();
	}
	public int getClientRequestSize() {
		return queue.size();
	}
	
}
