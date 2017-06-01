package com.rm.main;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.rm.service.RecoveryManager;
import com.rm.service.ReplicaManager;
import com.rm.service.ReplicaMessageDispatcher;
import com.rm.service.ReplicaServerService;
import com.rm.service.UDPSequencerMessageListener;

import replica.ClientRequest;

public class ReplicaManagerLauncher {
	
	
	public static void main(String arg[]){
		
		AtomicBoolean isFaulty=new AtomicBoolean(false);
		
		//This starts replica server
		ReplicaManager replicaManager=new ReplicaManager(isFaulty);
		ReplicaServerService service=replicaManager.initializeReplicaServer();
		
		System.out.println("Replica Server Initialized!!");
		
		
		BlockingQueue<ClientRequest> deliveryQueue = new ArrayBlockingQueue<ClientRequest>(
				10);
		UDPSequencerMessageListener udpServer=new UDPSequencerMessageListener(deliveryQueue, isFaulty);
		
		//This configure udp listener that listens to sequencer
		udpServer.configureUDPMessageListener();
		udpServer.storeMessage();
		udpServer.addToDeliveryQueue();
		
		
		ReplicaMessageDispatcher dispatcher=new ReplicaMessageDispatcher(deliveryQueue, isFaulty);
		
		//This forwards the request to replica-server
		dispatcher.dispatchMessageReplica();
		
		
		//Write code that will detect the failure and will kill the process. Pass the isFaulty flag as an argument
		RecoveryManager recoveryManager=new RecoveryManager(isFaulty, service);
		recoveryManager.failureListener();
		
		System.out.println("Finished");		
	}
}
