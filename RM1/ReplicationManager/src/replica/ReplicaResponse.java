package replica;

import java.io.Serializable;
import java.util.ArrayList;

public class ReplicaResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4343409346045918654L;
	private String replicaName ;
	private String  resultOpenAccount ;
	private boolean resultGetLoan;
	private boolean resultTransferLoan;
	private boolean resultDelayPayment;
	private ArrayList<String> resultPrintCustomerInfo;
	

	public void setReplicaName(String replicaname){
		replicaName = replicaname;
	}
	
	
	/**
	 * @return the replicaName
	 */
	public String getReplicaName() {
		return replicaName;
	}
	
	public void setResultOpenAccount(String result){
		resultOpenAccount = result;
	}
	
	/**
	 * @return the result
	 */
	public String getResultOpenAccount() {
		return resultOpenAccount;
	}
	
	public void setResultGetLoan(boolean result){
		resultGetLoan = result;
	}
	
	public boolean getResultGetLoan(){
		return resultGetLoan;
	}
	
	public void setResultTransferLoan(boolean result){
		resultTransferLoan = result;
	}
	
	public boolean getResultTransferLoan(){
		return resultTransferLoan;
	}
	
	public void setResultDelayPayment(boolean result){
		resultDelayPayment = result;
	}
	
	public boolean getResultDelayPayment(){
		return resultDelayPayment;
	}
	
	public void setResultPrintCustomerInfo(ArrayList<String> result){
		resultPrintCustomerInfo = result;
	}
	
	public ArrayList<String> getResultPrintCustomerInfo(){
		return resultPrintCustomerInfo;
	}

}
