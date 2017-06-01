import java.io.*;
import java.net.*;

import replica.ClientRequest;

public class Sequencer implements Runnable {
	
	private int uniqueId;
	private SequencerBuffer sequencerBuffer;
	
	public Sequencer() {
		uniqueId = 1;
		//sequencerBuffer = new SequencerBuffer();
		
		//Thread tbuffer = new Thread(sequencerBuffer);
		//to receive request id from replica to send an unicast message to that replica.
		//tbuffer.start();
	}

	public SequencerBuffer getSequenceBuffer(){
		return sequencerBuffer;
		
	}
	
	public int getNewUniqueId() {
		return uniqueId++;
	}
	
	public static void main(String[] args) {
		
		Sequencer sequencer = new Sequencer();
		Thread tSequencer = new Thread(sequencer);
		//to receive message from FrontEnd to add uniqueID to multicast messages to all replica.
		tSequencer.start();
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		//initial objects for front end and replica
		
		//InetAddress frontEndInet = null;
		DatagramSocket frontEndDatagramSocket = null;
		DatagramPacket messageFromFrontEnd = null;
		
		InetAddress replicaMulticastInet = null;
		MulticastSocket replicaSocket = null;
		DatagramPacket replyToReplica = null;
		
		//MulticastSocket frontendMulticastSocket = null;
		
		//message from front end
		ClientRequest message = null;
		//message to replica
		while (true) {
			try {
				
				//create frontend address 
				//frontEndInet = InetAddress.getByName(Configuration.SEQUENCER_ADDR);
				frontEndDatagramSocket = new DatagramSocket(Configuration.SEQUENCER_PORT);
				//frontEndInet = InetAddress.getByName(Configuration.FRONT_END_MULTICAST_ADDR);
				//frontendMulticastSocket = new MulticastSocket(Configuration.SEQUENCER_PORT);
				//frontendMulticastSocket.joinGroup(frontEndInet);
				
				
				//prepare byte stream to get data
				byte receiveBytes[] = new byte[4096];
				ByteArrayInputStream baos = new ByteArrayInputStream(receiveBytes);
				messageFromFrontEnd = new DatagramPacket(receiveBytes, receiveBytes.length);
				
				//step 1. receive a message from front end
				frontEndDatagramSocket.receive(messageFromFrontEnd);
				
				//use this instead
				//frontendMulticastSocket.receive(messageFromFrontEnd);
				
				
				//step 2. detach message data
				ObjectInputStream oos = new ObjectInputStream(baos);
				message = (ClientRequest) oos.readObject();
				
				//set the InetAddress of FE for replica to send backto them
				InetSocketAddress clntReqAdd = new InetSocketAddress(messageFromFrontEnd.getAddress(),messageFromFrontEnd.getPort());
				message.setClientRequestAddress(clntReqAdd);
				
				//step 3. add unique id to message
				message.setSequence(getNewUniqueId());
				
				System.out.println(message.get_bank());
				System.out.println(message.getSequence());
				//put message to buffer
				
				//create replica address for multicast message
				
				replicaMulticastInet = InetAddress.getByName(Configuration.SEQUENCER_MULTICAST_ADDR);
				replicaSocket = new MulticastSocket();
				replicaSocket.joinGroup(replicaMulticastInet);
				//prepare byte stream to send data
				byte[] replyBytes = null;
				ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
				ObjectOutputStream objOut = new ObjectOutputStream(byteOutStream);
				
				//step 4. attach the data
				objOut.writeObject(message);
				objOut.flush();
				replyBytes = byteOutStream.toByteArray();
				

				//step 5. multicast to replicas
				replyToReplica = new DatagramPacket(replyBytes,
						replyBytes.length, replicaMulticastInet, Configuration.REPLICA_MULTICAST_PORT);
				replicaSocket.send(replyToReplica);
				
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				// Close the connection
				if (frontEndDatagramSocket != null) {
					frontEndDatagramSocket.close();
				}
				if (replicaSocket != null) {
					replicaSocket.close();
				}
			}
		}
	}
}