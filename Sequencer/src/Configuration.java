
public class Configuration {
	static int FRONT_END_PORT = 7777;
	
	
	//receive frontend multicast to all replica and sequencer
	static String FRONT_END_MULTICAST_ADDR = "224.2.0.0";
	static int SEQUENCER_PORT = 7776;
	
	//send sequencer multicast to all replica with replica port number
	static String SEQUENCER_MULTICAST_ADDR = "224.1.0.0";
	static int REPLICA_MULTICAST_PORT = 8414;
	
	//for buffer TTL
	static long MESSAGE_BUFFER_TIME_TO_LIVE = 300;
}
