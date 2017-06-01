package FrontEndServices;

import java.net.InetSocketAddress;
import java.util.HashMap;

import replica.ClientRequest;

public class GetLoanByzantineFailure extends ByzantineFailure implements Runnable {
	
	private boolean finalResult;
	private String faultyRM;
	
	public GetLoanByzantineFailure(HashMap<String, InetSocketAddress> replicaManagerDatabase, ClientRequest clntReq, Sequencer seq){
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
		
		System.out.println("Debug: In detectFailure of Byzantine Failure of Get Loan: ");
		
		if (repResponse[0].getResultGetLoan() == (repResponse[1].getResultGetLoan())){
			if(repResponse[1].getResultGetLoan() == repResponse[2].getResultGetLoan()){
				finalResult = repResponse[1].getResultGetLoan();
				System.out.println("Final result for Get Loan : "+ finalResult);
			}else{
				System.out.println("Final result for Get Loan : "+ finalResult);
				finalResult = repResponse[1].getResultGetLoan();
				faultyRM = repResponse[2].getReplicaName();
			}
		}
		else if (repResponse[1].getResultGetLoan() == (repResponse[2].getResultGetLoan())){
			if(repResponse[2].getResultGetLoan() == repResponse[0].getResultGetLoan()){
				finalResult = repResponse[2].getResultGetLoan();
				System.out.println("Final result for Get Loan : "+ finalResult);
			}else{
				System.out.println("Final result for Get Loan : "+ finalResult);
				finalResult = repResponse[2].getResultGetLoan();
				faultyRM = repResponse[0].getReplicaName();
			}
		}
		else if (repResponse[0].getResultGetLoan() == (repResponse[2].getResultGetLoan())){
			if(repResponse[1].getResultGetLoan() == repResponse[2].getResultGetLoan()){
				finalResult = repResponse[1].getResultGetLoan();
				System.out.println("Final result for Get Loan : "+ finalResult);
			}else{
				System.out.println("Final result for Get Loan : "+ finalResult);
				finalResult = repResponse[2].getResultGetLoan();
				faultyRM = repResponse[1].getReplicaName();
			}
		}
	}
	
	public boolean getFinalResult(){
		return finalResult;
		
	}

}
