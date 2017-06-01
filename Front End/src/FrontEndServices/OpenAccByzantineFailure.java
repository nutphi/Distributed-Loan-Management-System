package FrontEndServices;

import java.net.InetSocketAddress;
import java.util.HashMap;

import replica.ClientRequest;

public class OpenAccByzantineFailure extends ByzantineFailure implements Runnable{
	
	private String finalResult;
	private String faultyRM;
	public OpenAccByzantineFailure(HashMap<String, InetSocketAddress> replicaManagerDatabase, ClientRequest clntReq, Sequencer seq){
		super(replicaManagerDatabase, clntReq, seq);
		finalResult = null;
		faultyRM = null;
	}
	
	
	
	public void run(){
		receiveResponse();
		detectFailure();
		
		if(faultyRM != null){
			reportFailure(faultyRM);
		}
		
	}
	
	
	public void detectFailure(){
		
		System.out.println("Debug: In detectFailure of Byzantine Failure of Open Account: ");
		
		if (repResponse[0].getResultOpenAccount().equals(repResponse[1].getResultOpenAccount())){
			if(repResponse[1].getResultOpenAccount().equals(repResponse[2].getResultOpenAccount())){
				finalResult = repResponse[2].getResultOpenAccount();
			}else {
				finalResult = repResponse[1].getResultOpenAccount();
				faultyRM = repResponse[2].getReplicaName();
			}
		}
		else if (repResponse[1].getResultOpenAccount().equals(repResponse[2].getResultOpenAccount())){
			if(repResponse[2].getResultOpenAccount().equals(repResponse[0].getResultOpenAccount())){
				finalResult = repResponse[2].getResultOpenAccount();
			}else{
				finalResult = repResponse[2].getResultOpenAccount();
				faultyRM = repResponse[0].getReplicaName();
			}
		}
		else if (repResponse[0].getResultOpenAccount().equals(repResponse[2].getResultOpenAccount())){
			if(repResponse[2].getResultOpenAccount().equals(repResponse[1].getResultOpenAccount())){
				finalResult = repResponse[2].getResultOpenAccount();
			}else{
				finalResult = repResponse[2].getResultOpenAccount();
				faultyRM = repResponse[1].getReplicaName();
			}
		}
	}
	
	public String getFinalResult(){
		return finalResult;
	}

}
