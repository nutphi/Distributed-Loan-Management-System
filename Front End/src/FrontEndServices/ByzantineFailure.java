package FrontEndServices;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.HashMap;

import replica.ClientRequest;
import replica.ReplicaResponse;

public class ByzantineFailure extends Failure{

	private DatagramSocket receiverSocket;

	public ByzantineFailure(HashMap<String, InetSocketAddress> replicaManagerDatabase, ClientRequest clntReq, Sequencer seq){
		super(replicaManagerDatabase, clntReq, seq);
		receiverSocket = seq.getSendSocket();
	}

	public void receiveResponse(){
		ReplicaResponse rplcRspns;

		int counter=0;

		while(counter<3)
		{

			try{
				/** Receive packet from RM's
				 * 
				 */
				byte[] receiveBuffer = new byte[512];
				DatagramPacket receivedPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);


				System.out.println("Debug: In receiveRestResponse just before receive");

				receiverSocket.receive(receivedPacket);

				System.out.println("Debug: In receiveRestResponse just after receive");
					
				

				// Sending acknowledgment back. 
				DatagramPacket reply = new DatagramPacket("OK".getBytes(),"OK".getBytes().length, receivedPacket.getAddress(),receivedPacket.getPort());

						receiverSocket.send(reply);
				
				/** Deserialize the received object
				 * 
				 */
				
				
				ByteArrayInputStream bs = new ByteArrayInputStream(receivedPacket.getData());
				ObjectInputStream is = new ObjectInputStream(bs);

				try{
					rplcRspns = (ReplicaResponse)is.readObject();
					repResponse[counter] = rplcRspns;
					System.out.println("Debug: In receiveResponse : Loop : "+ counter +" Replica Name : "+ repResponse[counter].getReplicaName() + " "+ repResponse[counter].getResultGetLoan());
					counter++;

				}catch(ClassNotFoundException e){
					System.out.println(e.getMessage());
				}
			}catch(IOException e){
				System.out.println(e.getMessage());
			}
		}


	}

	public void reportFailure(String failedRM){
		DatagramSocket reportSocket = seq.getSendSocket();
		String reportmessage = "FAULT_FAILURE,"+failedRM;
		byte[] reportBuffer = reportmessage.getBytes();

		System.out.println("Debug: In reportFailure : faulty RM is : "+ failedRM);
		try{		
				if(failedRM.equals("RM1")){
					DatagramPacket reportPacketRM1 = new DatagramPacket(reportBuffer, reportmessage.length(), replicaManagerDatabase.get("RM1").getAddress(),replicaManagerDatabase.get("RM1").getPort() );	
					reportSocket.send(reportPacketRM1);
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					DatagramPacket reportPacketRM2 = new DatagramPacket(reportBuffer, reportmessage.length(), replicaManagerDatabase.get("RM2").getAddress(),replicaManagerDatabase.get("RM2").getPort() );
					DatagramPacket reportPacketRM3 = new DatagramPacket(reportBuffer, reportmessage.length(), replicaManagerDatabase.get("RM3").getAddress(),replicaManagerDatabase.get("RM3").getPort() );
					
					reportSocket.send(reportPacketRM2);
					reportSocket.send(reportPacketRM3);
				}
				
				if(failedRM.equals("RM2")){
					DatagramPacket reportPacketRM2 = new DatagramPacket(reportBuffer, reportmessage.length(), replicaManagerDatabase.get("RM2").getAddress(),replicaManagerDatabase.get("RM2").getPort() );	
					reportSocket.send(reportPacketRM2);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					DatagramPacket reportPacketRM1 = new DatagramPacket(reportBuffer, reportmessage.length(), replicaManagerDatabase.get("RM1").getAddress(),replicaManagerDatabase.get("RM1").getPort() );
					DatagramPacket reportPacketRM3 = new DatagramPacket(reportBuffer, reportmessage.length(), replicaManagerDatabase.get("RM3").getAddress(),replicaManagerDatabase.get("RM3").getPort() );
					
					reportSocket.send(reportPacketRM1);
					reportSocket.send(reportPacketRM3);
				}
				
				if(failedRM.equals("RM3")){
					DatagramPacket reportPacketRM3 = new DatagramPacket(reportBuffer, reportmessage.length(), replicaManagerDatabase.get("RM3").getAddress(),replicaManagerDatabase.get("RM3").getPort() );	
					reportSocket.send(reportPacketRM3);
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					DatagramPacket reportPacketRM2 = new DatagramPacket(reportBuffer, reportmessage.length(), replicaManagerDatabase.get("RM2").getAddress(),replicaManagerDatabase.get("RM2").getPort() );
					DatagramPacket reportPacketRM1 = new DatagramPacket(reportBuffer, reportmessage.length(), replicaManagerDatabase.get("RM1").getAddress(),replicaManagerDatabase.get("RM1").getPort() );
					
					reportSocket.send(reportPacketRM2);
					reportSocket.send(reportPacketRM1);
				}
				
				
							
		}catch(IOException e){
			System.out.println(e.getMessage());
		}

	}
	
	
	
		
	

}
