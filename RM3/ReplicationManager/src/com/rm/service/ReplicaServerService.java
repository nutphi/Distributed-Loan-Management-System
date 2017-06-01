package com.rm.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;

//This is to initialize and configure replica-server
public class ReplicaServerService implements Runnable {

	private AtomicBoolean isFaulty;
	private Process runBroker;

	public Process getRunBroker(){
		return runBroker;
	}
	
	public ReplicaServerService(AtomicBoolean isFaulty) {
		this.isFaulty = isFaulty;
	}

	
	@Override
	public void run() {
		initializeReplicaServer();
	}

	public void initializeReplicaServer() {
		try {
			ProcessBuilder broker = new ProcessBuilder("java.exe", "-cp",
					"G:\\Workspace\\ReplicaServer\\bin",
					"com.server.BankServerConfiguration");

			runBroker = broker.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					runBroker.getInputStream()));

			BufferedReader reader1 = new BufferedReader(new InputStreamReader(
					runBroker.getErrorStream()));

			String str = null;
			while ((str = reader.readLine()) != null) {				
				System.out.println(str);

			}

			while ((str = reader1.readLine()) != null) {
				System.out.println(str);
			}

			runBroker.waitFor();

			System.out.println("Program complete");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	
}
