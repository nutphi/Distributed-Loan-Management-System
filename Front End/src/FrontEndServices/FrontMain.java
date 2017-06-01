package FrontEndServices;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.io.PrintWriter;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

public class FrontMain {


	public static void main(String args[]) throws InvalidName{

		InetSocketAddress seq = null;

		try {

			ORB orb=ORB.init(args, null);
			POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));

			HashMap<String, InetSocketAddress> replicaManagerDatabase = new HashMap<String, InetSocketAddress>();
			try{
				InetSocketAddress rm1Inet = new InetSocketAddress("132.205.93.53", 8888);
				InetSocketAddress rm2Inet = new InetSocketAddress("132.205.93.52", 8888);
				InetSocketAddress rm3Inet = new InetSocketAddress("132.205.93.51", 8888);
				replicaManagerDatabase.put("RM1", rm1Inet);
				replicaManagerDatabase.put("RM2", rm2Inet);
				replicaManagerDatabase.put("RM3", rm3Inet);

				seq = new InetSocketAddress("132.205.93.53", 7776);
			
			}catch (Exception e){
				System.out.println(e.getMessage());
			}

			FrontEnd FrontEnds[] = new FrontEnd[3] ;

			FrontEnds[0] = new FrontEnd ( "TD",replicaManagerDatabase,seq, "High Availability") ;
			FrontEnds[1] = new FrontEnd ( "RBC",replicaManagerDatabase,seq, "High Availability") ;
			FrontEnds[2] = new FrontEnd ( "BMO",replicaManagerDatabase,seq, "High Availability") ;	


			System.out.println("The following three FrontEnds are currently part of the DLMS");
			System.out.println("1. TD Canada Trust");
			System.out.println("2. Scotia FrontEnd");
			System.out.println("3. FrontEnd of Montreal");


			byte[] id1 = rootPOA.activate_object(FrontEnds[0]);
			byte[] id2 = rootPOA.activate_object(FrontEnds[1]);
			byte[] id3 = rootPOA.activate_object(FrontEnds[2]);

			org.omg.CORBA.Object ref1 = rootPOA.id_to_reference(id1);
			org.omg.CORBA.Object ref2 = rootPOA.id_to_reference(id2);
			org.omg.CORBA.Object ref3 = rootPOA.id_to_reference(id3);

			String ior1 = orb.object_to_string(ref1);
			String ior2 = orb.object_to_string(ref2);
			String ior3 = orb.object_to_string(ref3);

			System.out.println(ior1);
			System.out.println(ior2);
			System.out.println(ior3);

			PrintWriter file1 = new PrintWriter("TDCanada_ior.txt");
			PrintWriter file2 = new PrintWriter("Scotia_ior.txt");
			PrintWriter file3 = new PrintWriter("BOM_ior.txt");
			file1.println(ior1);
			file2.println(ior2);
			file3.println(ior3);
			file1.close();
			file2.close();
			file3.close();

			rootPOA.the_POAManager().activate();
			orb.run();



		} catch ( Exception e ) {
			System.out.println ( e.getMessage() ) ;
		}		
	}
}

