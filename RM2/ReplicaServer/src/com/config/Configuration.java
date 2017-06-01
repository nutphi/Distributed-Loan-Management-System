package com.config;
public class Configuration {
	
	/*
	Configuration for RMI project
	
	Note that your server shall be registered as (for example SERVER 1)
		rmi://RMI_SERVER_1_NAME:RMI_SERVER_1_PORT/RMI_SERVER_1_ADDRESS
	*/
	public static final String RMI_SERVER_1_NAME = "localhost";
	public static final String RMI_SERVER_2_NAME = "localhost";
	public static final String RMI_SERVER_3_NAME = "localhost";
	
	public static final String RMI_SERVER_1_ADDRESS = "rbc";
	public static final String RMI_SERVER_2_ADDRESS = "td";
	public static final String RMI_SERVER_3_ADDRESS = "bmo";
	
	public static final int WEB_SERVICE_PORT1 = 8882;
	public static final int WEB_SERVICE_PORT2 = 8883;
	public static final int WEB_SERVICE_PORT3 = 8884;
	
	public static final int UDP_SERVER_1_PORT = 3131;
	public static final int UDP_SERVER_2_PORT = 3132;
	public static final int UDP_SERVER_3_PORT = 3133;
	
	public static final int NEW_UDP_DISPATCHER_PORT = 8445;
	public static final int DATA_MIGRATION_PORT = 8787;
	
	//This port gets the data from RM to recover
	public static final int DATA_RECOVERY_PORT = 8901; 
	
}