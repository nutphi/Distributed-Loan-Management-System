package Client;

import java.io.*;

import org.omg.CORBA.ORB;

import FrontEndIDLInterface.*;

public class CustomerClient implements Runnable{
	
	private ORB orb;
	
	public CustomerClient(ORB orbtemp){
		orb = orbtemp;
	}

	public void run() {
//		String[] allBanks = {"TD", "RBC", "BMO"} ;
		char [] firstchar = {'a','b','c','d','e','f','g','h','i','j','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
		int rand = (int) (Math.random() * 24) ;
		int randBank = (int)(Math.random() * 3) ;
//		frontEndIdlInterface bankObject = getRemoteObject ( allBanks[randBank] ) ;
		frontEndIdlInterface bankObject = getRemoteObject ( "TD" ) ;
		String firstName = firstchar[rand]+"First" + rand ;
		String lastName = "last" + rand ;
		String email = "emailaddress" + rand ;
		String phoneNumber = "51451451" + rand;
		String password = "password" + rand ;
//		String bank = allBanks[randBank];
		String bank = "TD";
		int loanAmount = 1000 * rand;
		String accNo = null ;
		try {
			accNo = bankObject.openAccount(bank, firstName, lastName, email, phoneNumber, password);
//			accNo = bankObject.openAccount("TD", "Shubham", "Singh", "shubham@singh", "5147135885", "asdasdasd");
			System.out.println("Account created with name :"+firstName+" "+" lastname "+lastName +" " + " Phone number : " +  phoneNumber + " Password: " + password +" with account no. : "+ accNo + " at " + "TD");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		
//		boolean test = false;
//		if ( accNo != null && !accNo.equals("Invalid Entry") && !accNo.equals("User exist") ){
//			try {
//				test = bankObject.getLoan(bank, accNo, password, loanAmount);
//				if(test){
//					System.out.println("Loan created with name :"+ firstName + lastName +" "+ " with account no. : "+ accNo + " at " + bank);
//				}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}
//---------------------------------------------------------------------------------Get Menu--------------------------------------------------------------------------------------------------------

	public void getMenu () {

		while ( true ) {
			try {
				InputStreamReader in = new InputStreamReader ( System.in ) ;
				BufferedReader r = new BufferedReader ( in ) ;
				System.out.println( "Enter an operation" );
				System.out.println("1. Create Account" ); 
				System.out.println("2. Get Loan");
				System.out.println("3. Transfer Loan");
				System.out.println("4. Exit") ;
				int choice = Integer.parseInt(r.readLine()) ;
				if ( choice  == 1 ) {
					getaccountInfo () ;
				} else if ( choice == 2 ) {
					getloanInfo () ;
				}else if(choice == 3){
					getLoanTransferInfo();
				}
				else if ( choice == 4 ) {
					break ;
				} else {
					System.out.println( "You have entered a wrong choice. Please try again" ) ;
					continue ;
				}
				System.out.println("Press any key to continue:...");
				r.readLine() ;
			} catch ( IOException e ) {
				System.out.println( e.getMessage() ) ;
				break ;
			} catch ( NumberFormatException e ) {
				System.out.println( "You have entered a wrong choice. Please try again" ) ;
				continue ;
			} 
		}
	}

	//------------------------------------------------------------------------------------------Get Account Info-------------------------------------------------------------------------------
	
	public void getaccountInfo() throws IOException{

		InputStreamReader in = new InputStreamReader ( System.in ) ;
		BufferedReader r = new BufferedReader ( in ) ;
		System.out.print("Enter the first name: ");
		String fName = r.readLine () ;
		System.out.print("Enter the last name: ");
		String lName = r.readLine () ;
		System.out.print("Enter the phone number: ");
		String phoneNumber = r.readLine () ;
		System.out.print("Enter the password: ");
		String password = r.readLine () ;
		System.out.print("Enter the email address: ");
		String email = r.readLine () ;
		System.out.print("Enter Bank: ");
		String bank = r.readLine () ;

		frontEndIdlInterface bankObj = getRemoteObject ( bank ) ;

		if ( bankObj == null ) {
			System.out.println( "Sorry! the demanded service can't be provided at the moment" );
			System.out.println("Please try again later" );
		}

		try {
			String result ;
			result = bankObj.openAccount( bank, fName, lName, email, phoneNumber, password);
			if ( result != null   && !result.equals("Invalid Entry") ) {
				System.out.println("Your operation was successful" );
				System.out.println("Your Account Number is :"+ result);
			}else if( result.equals("User exist")){
					System.out.println(" Account already exists !");
				}
			else{
					System.out.println( result);
			}
		} catch ( Exception e ) {
			System.out.println("Your opeartion failed");
		}
	}

//--------------------------------------------------------------------Get Loan Info-------------------------------------------------
	public void getloanInfo() throws IOException{
		
		InputStreamReader in = new InputStreamReader ( System.in ) ;
		BufferedReader r = new BufferedReader ( in ) ;
		System.out.println("Enter Bank: ");
		String bank = r.readLine () ;
		System.out.print("Enter account No: ");
		String acc_no = r.readLine () ;
		System.out.print("Enter the password: ");
		String password = r.readLine () ;
		System.out.print("Enter loan amount: ");
		String loanAmts = r.readLine () ;
		int loanAmt= Integer. parseInt(loanAmts);
		
		frontEndIdlInterface bankObj = getRemoteObject ( bank ) ;

		if ( bankObj == null ) {
			System.out.println( "Sorry! the demanded service can't be provided at the moment" );
			System.out.println("Please try again later" );
		}

		try {
			boolean result ;
			result = bankObj.getLoan( bank, acc_no, password, loanAmt) ;
			if ( result ) {
				System.out.println("Your loan is sactioned" );
			}else {
				System.out.println("Your loan is rejected");
			}
		} catch ( Exception e ) {
			System.out.println("Your opeartion failed");
		}

	}

//---------------------------------------------------------------------------------Loan Transfer Info-------------------------------------------------------------------------------------	
	public void getLoanTransferInfo() throws IOException{
		
		InputStreamReader in = new InputStreamReader ( System.in ) ;
		BufferedReader r = new BufferedReader ( in ) ;
		
		System.out.print("Enter Loan ID: ");
		String loanID = r.readLine () ;
		System.out.print("Enter current bank: ");
		String currentBank = r.readLine () ;
		System.out.print("Enter name of the other Bank: ");
		String otherBank = r.readLine () ;
		
		frontEndIdlInterface bankObj = getRemoteObject ( currentBank ) ;
		
		if(bankObj == null){
			System.out.println("Sorry! the demanded service can't be provided at the moment ");
			System.out.println("Please try again later");
		}
		try{
			boolean result= false;
			result = bankObj.transferLoan(loanID, currentBank, otherBank );
			if(result){
				System.out.println("Loan transfered successfully.");
			}
			else{
				System.out.println("Your loan could not be transfered due to some problem.");
			}
		}catch(Exception e){
			System.out.println(" "+ e.getMessage());
		}
		
				
	}
	
	
	
	
	public frontEndIdlInterface getRemoteObject ( String Bankname )  {
		try{

			if (Bankname.equals("TD")){
				BufferedReader br = new BufferedReader(new FileReader ("TDCanada_ior.txt"));
				String ior = br.readLine();
				br.close();
				org.omg.CORBA.Object o = orb.string_to_object(ior);
				frontEndIdlInterface ciObj = frontEndIdlInterfaceHelper.narrow(o);
				return ciObj;
			}
			else if (Bankname.equals("RBC" )){
				BufferedReader br = new BufferedReader(new FileReader ("Scotia_ior.txt"));
				String ior = br.readLine();
				br.close();
				org.omg.CORBA.Object o = orb.string_to_object(ior);
				frontEndIdlInterface ciObj = frontEndIdlInterfaceHelper.narrow(o);
				return ciObj;
			}
			else{
				BufferedReader br = new BufferedReader(new FileReader ("BOM_ior.txt"));
				String ior = br.readLine();
				br.close();
				org.omg.CORBA.Object o = orb.string_to_object(ior);
				frontEndIdlInterface ciObj = frontEndIdlInterfaceHelper.narrow(o);
				return ciObj;
			}
		}catch(IOException e){
			e.printStackTrace();
			//System.out.println("  "+ e.getMessage());
			return null;
		}

	}
}