package Client;
import org.omg.CORBA.ORB;


public class Main {
	public static void main ( String[] args ) throws InterruptedException {
		
		ORB orb = ORB.init(args,null);
		
		
		CustomerClient a = new CustomerClient (orb) ;
		Thread accountCreation[] = new Thread[3] ;
		for ( int i = 0 ; i < accountCreation.length ; i++ ) {
			accountCreation[i] = new Thread(a) ;
			accountCreation[i].start();
			Thread.sleep(500);
		}
		for ( int i = 0 ; i < accountCreation.length ; i++ ) {
			try {
				accountCreation[i].join();
			} catch ( InterruptedException e ) {
				continue ;
			}
		}
		a.getMenu();		
	}

}