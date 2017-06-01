package FrontEndServices;

import java.net.InetSocketAddress;
import java.util.HashMap;

import replica.ClientRequest;

public class DelayPaymentByzantineFailure extends ByzantineFailure implements Runnable {
	
	private boolean finalResult;
	private String faultyRM;

	public DelayPaymentByzantineFailure(HashMap<String, InetSocketAddress> replicaManagerDatabase, ClientRequest clntReq, Sequencer seq){
		super(replicaManagerDatabase, clntReq, seq);
		finalResult = false;
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
		
		System.out.println("Debug: In detectFailure of Byzantine Failure of Delay Payement: ");

		if (repResponse[0].getResultDelayPayment() == repResponse[1].getResultDelayPayment()){
			if(repResponse[1].getResultDelayPayment() == repResponse[2].getResultDelayPayment()){
				finalResult = repResponse[2].getResultDelayPayment();
			}else{
				finalResult = repResponse[1].getResultDelayPayment();
				faultyRM = repResponse[2].getReplicaName();
			}
		}
		else if (repResponse[1].getResultDelayPayment() == repResponse[2].getResultDelayPayment()){
			if(repResponse[0].getResultDelayPayment() == repResponse[2].getResultDelayPayment()){
				finalResult = repResponse[2].getResultDelayPayment();
			}else{
				finalResult = repResponse[2].getResultDelayPayment();
				faultyRM = repResponse[0].getReplicaName();
			}
		}
		else if (repResponse[0].getResultDelayPayment() == repResponse[2].getResultDelayPayment()){
			if(repResponse[1].getResultDelayPayment() == repResponse[2].getResultDelayPayment()){
				finalResult = repResponse[2].getResultDelayPayment();
			}else{
				finalResult = repResponse[2].getResultDelayPayment();
				faultyRM = repResponse[1].getReplicaName();
			}
		}

	}
	
	public boolean getFinalResult(){
		return finalResult;
	}


}
