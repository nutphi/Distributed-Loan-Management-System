package FrontEndServices;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

import replica.ClientRequest;

public class PrintCustomerInfoByzantineFailure extends ByzantineFailure implements Runnable{
	
	private ArrayList<String> finalResult;
	private String faultyRM;

	public PrintCustomerInfoByzantineFailure(HashMap<String, InetSocketAddress> replicaManagerDatabase, ClientRequest clntReq, Sequencer seq){
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
		
		

		
	}
	
	public ArrayList<String> getFinalResult(){
		return finalResult;
	}

}
