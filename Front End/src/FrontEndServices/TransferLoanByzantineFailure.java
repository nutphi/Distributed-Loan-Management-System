package FrontEndServices;

import java.net.InetSocketAddress;
import java.util.HashMap;

import replica.ClientRequest;

public class TransferLoanByzantineFailure extends ByzantineFailure implements Runnable {

	private boolean finalResult;
	private String faultyRM;

	public TransferLoanByzantineFailure(HashMap<String, InetSocketAddress> replicaManagerDatabase, ClientRequest clntReq, Sequencer seq){
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
		
		System.out.println("Debug: In detectFailure of Byzantine Failure of Transfer Loan: ");

		if (repResponse[0].getResultTransferLoan() == repResponse[1].getResultTransferLoan()){
			if(repResponse[1].getResultTransferLoan() == repResponse[2].getResultTransferLoan()){
				finalResult = repResponse[2].getResultTransferLoan();
			}else {
				finalResult = repResponse[1].getResultTransferLoan();
				faultyRM = repResponse[2].getReplicaName();
			}
		}
		else if (repResponse[1].getResultTransferLoan() == repResponse[2].getResultTransferLoan()){
			if(repResponse[0].getResultTransferLoan() == repResponse[2].getResultTransferLoan()){
				finalResult = repResponse[2].getResultTransferLoan();
			}else{
				finalResult = repResponse[2].getResultTransferLoan();
				faultyRM = repResponse[0].getReplicaName();
			}
		}
		else if (repResponse[0].getResultTransferLoan() == repResponse[2].getResultTransferLoan()){
			if(repResponse[1].getResultTransferLoan() == repResponse[2].getResultTransferLoan()){
				finalResult = repResponse[2].getResultTransferLoan();
			}else{
				finalResult = repResponse[2].getResultTransferLoan();
				faultyRM = repResponse[1].getReplicaName();
			}
		}

	}
	
	public boolean getFinalResult(){
		return finalResult;
	}

}
