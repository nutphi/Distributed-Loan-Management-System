package com.rm.service;

import java.util.concurrent.atomic.AtomicBoolean;

public class ReplicaManager {
	
	AtomicBoolean isFaulty;
	
	public ReplicaManager(AtomicBoolean isFaulty){
		this.isFaulty=isFaulty;
	}
	
	public ReplicaServerService initializeReplicaServer() {
		
		ReplicaServerService replicaService = new ReplicaServerService(isFaulty);
		
		Thread thread1 = new Thread(replicaService);
		thread1.start();
		
		return replicaService;
	}

}
