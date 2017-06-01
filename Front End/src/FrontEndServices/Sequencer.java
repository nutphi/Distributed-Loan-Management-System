package FrontEndServices;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import replica.ClientRequest;

public class Sequencer{
	
	/**
	 * 
	 */
	
	private InetSocketAddress sequencerAddress;
	private DatagramSocket sendSocket;
	
	public Sequencer(InetSocketAddress sequencerAddress){
		this.sequencerAddress = sequencerAddress;
		
		try{
		this.sendSocket = new DatagramSocket();
		}catch(SocketException se){
			System.out.println("Error in creating Socket");
			se.printStackTrace();
		}
	}
	
	public DatagramSocket getSendSocket(){
		return sendSocket;
	}
	
	public void sendRequest(ClientRequest clntReq) {

		ByteArrayOutputStream bs = null;
		ObjectOutputStream os = null;
		try{
//			System.out.println(" Customer Name :"+ clntReq.get_firstName()+ " Customer last name : "+ clntReq.get_lastName() + " Customer phonenumber :"+ clntReq.get_phoneNumber());
			
			// Serializing the ClientRequest object

			bs = new ByteArrayOutputStream () ;
			os = new ObjectOutputStream ( bs ) ;
			os.writeObject( clntReq );


			// Sending client request to Sequencer
			
			byte[] sendBuffer = bs.toByteArray() ;
			//		DatagramPacket sendPacket = new DatagramPacket ( sendBuffer, sendBuffer.length, 
			//				sequencerAddress.getAddress(), sequencerAddress.getPort() ) ;
			DatagramPacket sendPacket = new DatagramPacket ( sendBuffer, sendBuffer.length, sequencerAddress.getAddress() , sequencerAddress.getPort() ) ;		
			sendSocket.send(sendPacket);
			
			System.out.println("Sent from sequencer");
			os.close() ;
			bs.close();
		}catch(SocketException se){
			System.out.println("Error in creating Socket");
			se.printStackTrace();
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}

}
