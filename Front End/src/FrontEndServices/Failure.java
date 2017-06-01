package FrontEndServices;


import java.net.InetSocketAddress;
import java.util.HashMap;

import replica.ClientRequest;
import replica.ReplicaResponse;

public class Failure {
	
	
	private ClientRequest clntReq;
	protected Sequencer seq;
	protected ReplicaResponse[] repResponse;
	public HashMap<String , InetSocketAddress> replicaManagerDatabase ;
	
	public Failure(HashMap<String, InetSocketAddress> replicaManagerDatabase, ClientRequest clntReq, Sequencer seq){
		this.clntReq = clntReq;
		this.replicaManagerDatabase = replicaManagerDatabase;
		this.seq = seq;
		repResponse = new ReplicaResponse[3];
	}
	
		

}
