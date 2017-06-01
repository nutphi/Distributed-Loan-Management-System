package com.config;

public class Configuration {

	public static final int UDP_PORT_ADDRESS_REPLICA_RECOVERY = 8888;
	public static final int NEW_UDP_DISPATCHER_PORT = 8445;
	public static final int UDP_SEQUENCER_MESSAGE_LISTENER_PORT = 8414;
	public static final int FETCH_DATA_ON_PORT = 8301;
	
	
	public static final String REPLICA_NAME = "RM3";
	public static final String CRASH_FAILURE = "CRASH_FAILURE";
	public static final String FAULT_FAILURE = "FAULT_FAILURE";
	public static final String MIGRATION_REPLICA_SERVER_ADDRESS = "localhost";
	
	//Here you can set the replica_manager_name
	public static final String REPLICA_MANAGER_NAME="RM3";
	
	public static final int DATA_MIGRATION_PORT = 8787;
	
	//This port gets the data from RM to recover
	public static final int DATA_RECOVERY_PORT = 8901;
	
	
	public static final String RM1_IP_ADDRESS="132.205.93.53";
	public static final String RM2_IP_ADDRESS="132.205.93.52";
	public static final String RM3_IP_ADDRESS="132.205.93.51";
	
	
}
