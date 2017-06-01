package FrontEndServices;



import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import replica.ClientRequest;
import FrontEndIDLInterface.*;


public class FrontEnd extends frontEndIdlInterfacePOA{
	
	private String bank;
	private static String systemProperty;
	private InetSocketAddress sequencerAddress ;
	private HashMap<String, InetSocketAddress> replicaManagerDatabase;

	
	public FrontEnd(String bank, HashMap<String, InetSocketAddress> replicaManagerDatabase, InetSocketAddress sequencerAddress, String system_Property){
		this.bank = bank;
		this.sequencerAddress = sequencerAddress;
		this. replicaManagerDatabase = replicaManagerDatabase;
		systemProperty = system_Property;
	}
	
	
	
	public static String getSystemProperty(){
		return systemProperty;
	}
	
	public String getBankName(){
		return bank;
	}
	
	public String openAccount(String bank, String firstname, String lastname, String emailaddress, String phonenumber, String password) {
		ClientRequest clntReq = new ClientRequest(bank, firstname, lastname, emailaddress, phonenumber, password);
		Sequencer seq= new Sequencer(sequencerAddress);
		seq.sendRequest(clntReq);
		
		if(getSystemProperty().equals("Software Failure Tolerant")){
			OpenAccByzantineFailure bf = new OpenAccByzantineFailure(replicaManagerDatabase, clntReq, seq);
			
			Thread thrd = new Thread(bf);
			thrd.start();
			try{
				thrd.join();
				}catch(InterruptedException e){
					System.out.println("THread joined");
				}
			String result = bf.getFinalResult();
			return result;
		}
		else{
			CrashFailure crshflr = new CrashFailure(replicaManagerDatabase, clntReq, seq);
			Thread thrd = new Thread(crshflr);
			thrd.start();
			try{
			thrd.join();
			}catch(InterruptedException e){
				System.out.println("THread joined");
			}
			
			return crshflr.repResponse[0].getResultOpenAccount();
		}
	}
	
	public boolean getLoan(String bank, String accountnumber, String password, int loanAmount){
		
		ClientRequest clntReq = new ClientRequest(bank, accountnumber, password, loanAmount);
		Sequencer seq= new Sequencer(sequencerAddress);
		seq.sendRequest(clntReq);
		
		if(getSystemProperty().equals("Software Failure Tolerant")){
			GetLoanByzantineFailure bf = new GetLoanByzantineFailure(replicaManagerDatabase, clntReq, seq);
		
			Thread thrd = new Thread(bf);
			thrd.start();
			try{
				thrd.join();
				}catch(InterruptedException e){
					System.out.println("THread joined");
				}
			boolean result = bf.getFinalResult();
			return result;
		}
		else{
			CrashFailure crshflr = new CrashFailure(replicaManagerDatabase, clntReq, seq);
			Thread thrd = new Thread(crshflr);
			thrd.start();
			try{
			thrd.join();
			}catch(InterruptedException e){
				System.out.println("THread joined");
			}

			return crshflr.repResponse[0].getResultGetLoan();
		}
	}
	
	
	public boolean transferLoan(String loanID, String currentBank, String otherBank){
		ClientRequest clntReq = new ClientRequest(loanID, currentBank, otherBank);
		Sequencer seq= new Sequencer(sequencerAddress);
		seq.sendRequest(clntReq);
		
		if(getSystemProperty().equals("Software Failure Tolerant")){
			TransferLoanByzantineFailure bf = new TransferLoanByzantineFailure(replicaManagerDatabase, clntReq, seq);
			Thread thrd = new Thread(bf);
			thrd.start();
			try{
				thrd.join();
				}catch(InterruptedException e){
					System.out.println("THread joined");
				}
			boolean result = bf.getFinalResult();
			return result;
		}
		else{
			CrashFailure crshflr = new CrashFailure(replicaManagerDatabase, clntReq, seq);
			Thread thrd = new Thread(crshflr);
			thrd.start();
			try{
				thrd.join();
				}catch(InterruptedException e){
					System.out.println("THread joined");
				}
			return crshflr.repResponse[0].getResultTransferLoan();
		}
	}
	
	public boolean delayPayment(String bank, String loanID, cal currentDueDate, cal newDueDate){

		String curDay = Integer.toString(currentDueDate.day);
		String curMon = Integer.toString(currentDueDate.month);
		String curYr = Integer.toString(currentDueDate.year);
		
		String dueDay = Integer.toString(newDueDate.day);
		String dueMon = Integer.toString(newDueDate.month);
		String dueYr = Integer.toString(newDueDate.year);
		
		if(currentDueDate.day<10){
			curDay = "0"+dueDay;
		}
		if(newDueDate.day<10){
			dueDay = "0"+dueDay;
		}
		if(currentDueDate.month<10){
			curMon = "0"+curMon;
		}
		if(newDueDate.month<10){
			dueMon = "0"+dueMon;
		}
		
		String curr_date =curDay+"/"+curMon+"/"+curYr;
		String newdue_date = dueDay+"/"+dueMon+"/"+dueYr;
		
		ClientRequest clntReq = new ClientRequest(bank, loanID, curr_date, newdue_date);
		Sequencer seq= new Sequencer(sequencerAddress);
		seq.sendRequest(clntReq);
		
		if(getSystemProperty().equals("Software Failure Tolerant")){
			DelayPaymentByzantineFailure bf = new DelayPaymentByzantineFailure(replicaManagerDatabase, clntReq, seq);
			Thread thrd = new Thread(bf);
			thrd.start();
			try{
				thrd.join();
				}catch(InterruptedException e){
					System.out.println("THread joined");
				}
			boolean result = bf.getFinalResult();
			return result;
		}
		else{
			CrashFailure crshflr = new CrashFailure(replicaManagerDatabase, clntReq, seq);
			Thread thrd = new Thread(crshflr);
			thrd.start();
			try{
				thrd.join();
				}catch(InterruptedException e){
					System.out.println("THread joined");
				}
			return crshflr.repResponse[0].getResultDelayPayment();
		}
	}
	
	public customerInfo printCustomerInfo(String bank){
		ClientRequest clntReq = new ClientRequest(bank);
		Sequencer seq= new Sequencer(sequencerAddress);
		seq.sendRequest(clntReq);
		
		if(getSystemProperty().equals("Software Failure Tolerant")){
			PrintCustomerInfoByzantineFailure bf = new PrintCustomerInfoByzantineFailure(replicaManagerDatabase, clntReq, seq);
			Thread thrd = new Thread(bf);
			thrd.start();
			try{
				thrd.join();
				}catch(InterruptedException e){
					System.out.println("THread joined");
				}
			ArrayList<String> result = bf.getFinalResult();
			String resultarray [] =  result.toArray(new String [result.size()]);
			customerInfo sendResult = new customerInfo(resultarray);
			return sendResult;
		}
		else{
			CrashFailure crshflr = new CrashFailure(replicaManagerDatabase, clntReq, seq);
			Thread thrd = new Thread(crshflr);
			thrd.start();
			try{
				thrd.join();
				}catch(InterruptedException e){
					System.out.println("THread joined");
				}
			ArrayList<String> result = crshflr.repResponse[0].getResultPrintCustomerInfo();
			String resultarray [] =  result.toArray(new String [result.size()]);
			customerInfo sendResult = new customerInfo(resultarray);
			return sendResult;
			}
	}

}
 