package FrontEndServices;

import replica.ClientRequest;
import replica.ReplicaResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.time.Clock;
import java.util.HashMap;

public class CrashFailure extends Failure implements Runnable{

	private Clock clk;
	private DatagramSocket receiverSocket;
	private long[] responseTime;


	public CrashFailure(HashMap<String, InetSocketAddress> replicaManagerDatabase, ClientRequest clntReq, Sequencer seq){
		super(replicaManagerDatabase, clntReq, seq);
		clk = Clock.systemDefaultZone();
		responseTime = new long[3];
		receiverSocket = seq.getSendSocket();
	}



	public void receiveRestResponse(){
		ReplicaResponse rplcRspns;
		long startTime, stopTime;
		int counter=0;

		while(counter<3)
		{
			startTime = 0 ;
			stopTime = 0;
			try{
				receiverSocket.setSoTimeout(5000);
			}catch(SocketException se){
				System.out.println(se.getMessage());
			}

			try{
				/** Receive packet from RM's
				 * 
				 */
				byte[] receiveBuffer = new byte[512];
				DatagramPacket receivedPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

				startTime = clk.millis();
				System.out.println("Debug: In receiveRestResponse just before receive");
				try{
					receiverSocket.receive(receivedPacket);
				}catch(SocketTimeoutException e){
					System.out.println("Debug: In TIME OUT EXCEPTION : ");
					detectFailure();
					break;
				}
				
				// Sending acknowledgment back. 
				DatagramPacket reply = new DatagramPacket("OK".getBytes(),"OK".getBytes().length, receivedPacket.getAddress(),receivedPacket.getPort());
				receiverSocket.send(reply);
				
				System.out.println("Debug: In receiveRestResponse just after receive");
				stopTime= clk.millis();



				responseTime[counter] = stopTime - startTime;
				

				/** Deserialize the received object
				 * 
				 */
				ByteArrayInputStream bs = new ByteArrayInputStream(receivedPacket.getData());
				ObjectInputStream is = new ObjectInputStream(bs);

				try{
					rplcRspns = (ReplicaResponse)is.readObject();
					repResponse[counter] = rplcRspns;
					
					
				System.out.println("Debug: In receiveResponse : Loop : "+ counter +" Replica Name : "+ repResponse[counter].getReplicaName() + " "+ repResponse[counter].getResultOpenAccount());
				counter++;
				}catch(ClassNotFoundException e){
					System.out.println(e.getMessage());
				}
			}catch(IOException e){
				System.out.println(e.getMessage());
			}
		}
	}

	public void detectFailure(){
		boolean flagRM1 = false;
		boolean flagRM2 = false;
		boolean flagRM3 = false;
		int counter=0;
		while(counter<3 && repResponse[counter]!= null ){
			//			System.out.println("Debug: In DETECT FAILURE : LOOP : "+counter+" results reveived from replica : "+ repResponse[counter].getReplicaName());
			if(repResponse[counter].getReplicaName().equals("RM1")){
				flagRM1 = true;
			}
			else if(repResponse[counter].getReplicaName().equals("RM2")){
				flagRM2 = true;
			}
			else if(repResponse[counter].getReplicaName().equals("RM3")){
				flagRM3 = true;
			}
			counter++;
		}

		if (!flagRM1){
			reportFailure("RM1");			
		}else if (! flagRM2){
			reportFailure("RM2");
		}else if(!flagRM3){
			reportFailure("RM3");
		}
	}

	public void reportFailure(String failedRM){

		DatagramSocket reportSocket = seq.getSendSocket();
		String reportmessage = "CRASH_FAILURE,"+failedRM;
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



	@Override
	public void run() {
		receiveRestResponse();
		int i=0;
		while(i<3 && repResponse[i] != null){
			System.out.println("Run MEthod : "+repResponse[i].getReplicaName());
			i++;
		}

	}
}


