import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;

import replica.ClientRequest;;

public class SequencerBuffer implements Runnable {
	private Queue<ClientRequest> queueBuffer;

	public SequencerBuffer() {
		queueBuffer = new LinkedList<ClientRequest>();
	}
	
	class SequencerBufferTimerTask extends TimerTask{
		ClientRequest m;
		SequencerBufferTimerTask(ClientRequest m){
			this.m = m;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			boolean isRemoveMessage=removeSequencerMessage(m);
			System.out.println("remove sequence message "+m.getSequence() +" is "+isRemoveMessage);
		}
	}
	
	public boolean addSequencerMessage(ClientRequest message) {
		boolean isAddMessage = queueBuffer.add(message);
		if (isAddMessage) {
			Timer t = new Timer();
			t.schedule(new SequencerBufferTimerTask(message), Configuration.MESSAGE_BUFFER_TIME_TO_LIVE);
		}
		return isAddMessage;
	}

	public boolean removeSequencerMessage(ClientRequest message) {
		boolean result=false;
		if(queueBuffer.size()>0)
			result=queueBuffer.remove(message);
		return  result;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			InetAddress replicaInet = InetAddress.getByName(Configuration.SEQUENCER_MULTICAST_ADDR);
			@SuppressWarnings("resource")
			DatagramSocket replicaSocket = new DatagramSocket(Configuration.REPLICA_MULTICAST_PORT,replicaInet);
			
			byte[] databuffer = new byte[1024];
			DatagramPacket idRequest = new DatagramPacket(databuffer,databuffer.length);
			//step 6.1 if replica request a sequence message, receive it.
			replicaSocket.receive(idRequest);
			SocketAddress desAddr = replicaSocket.getRemoteSocketAddress();
			
			ByteBuffer b = ByteBuffer.wrap(idRequest.getData());
			int id = b.getInt();
			
			Iterator<ClientRequest> it = queueBuffer.iterator();
			while(it.hasNext()){
				ClientRequest msg = it.next();
				if(msg.getSequence()==id){
					//prepare byte stream to send data
					byte[] replyBytes = null;
					ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
					ObjectOutputStream objOut = new ObjectOutputStream(byteOutStream);
					
					//step 4. attach the data
					objOut.writeObject(msg);
					objOut.flush();
					replyBytes = byteOutStream.toByteArray();
					@SuppressWarnings("resource")
					DatagramSocket replyReplicaSocket = new DatagramSocket(desAddr);
					DatagramPacket replyReplica = new DatagramPacket(replyBytes,
							replyBytes.length, replicaInet, Configuration.REPLICA_MULTICAST_PORT);
					replyReplicaSocket.send(replyReplica);
					break;
				}
			}
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
