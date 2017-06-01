package com.server;

import javax.xml.ws.Endpoint;

import com.config.Configuration;
import com.service.BankServerImplementation;
import com.service.DataMigrator;
import com.service.RecoverDataManager;
import com.service.RequestDispatcher;
import com.udp.server.UDPServer;

public class BankServerConfiguration {

	/**
	 * @param args
	 */
	public static void main(String args[]) throws Exception {

		BankServerImplementation bank1 = configureServer("TD");
		BankServerImplementation bank2 = configureServer("RBC");
		BankServerImplementation bank3 = configureServer("BMO");

		RequestDispatcher requestDispatcher = new RequestDispatcher();
		System.out.println("bank1"+bank1+" bank2"+bank2+" bank3"+bank3);
		
		requestDispatcher.setBank1Obj(bank1);
		requestDispatcher.setBank2Obj(bank2);
		requestDispatcher.setBank3Obj(bank3);

		Thread dispatcherThread = new Thread(requestDispatcher);
		dispatcherThread.start();
		
		DataMigrator dataMigrator = new DataMigrator();
		System.out.println("bank1"+bank1+" bank2"+bank2+" bank3"+bank3);
		
		dataMigrator.setBank1Obj(bank1);
		dataMigrator.setBank2Obj(bank2);
		dataMigrator.setBank3Obj(bank3);
		
		Thread migratorThread = new Thread(dataMigrator);
		migratorThread.start();
		
		RecoverDataManager recoverData = new RecoverDataManager();
				
		recoverData.setBank1Obj(bank1);
		recoverData.setBank2Obj(bank2);
		recoverData.setBank3Obj(bank3);
		
		Thread recoverDataThread = new Thread(recoverData);
		recoverDataThread.start();


		System.out.println("Bank Service is up and running");
	}

	public static BankServerImplementation configureServer(String bankName) {

		int portNumber = 0;

		if (bankName.equals("RBC")) {
			portNumber = Configuration.WEB_SERVICE_PORT1;
		} else if (bankName.equals("TD")) {
			portNumber = Configuration.WEB_SERVICE_PORT2;
		} else if (bankName.equals("BMO")) {
			portNumber = Configuration.WEB_SERVICE_PORT3;
		}

		BankServerImplementation bankObj = new BankServerImplementation();
		bankObj.config(bankName);


		Thread udpServerThread = new Thread(new UDPServer(bankName, bankObj));
		udpServerThread.start();

		return bankObj;
	}

}
