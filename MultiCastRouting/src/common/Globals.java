package common;

import java.io.File;

/**
 * Globals
 * @author Sanket Chandorkar
 */
public class Globals {
	
	public static int EXIT_ERROR = -1;
	
	public static int EXIT_SUCCESS = 0;
	
	public static final String workingDir = (new File(".").getAbsolutePath());

	public static final String dataDir = workingDir + File.separator + "data";
	
	public static final String logDir = workingDir + File.separator + "log";
	
	public static final String topologyFile = dataDir + File.separator + "topology.txt";

	public static String getOutputFile(String id){
		return logDir + File.separator + "output_" + id + ".txt";
	}
	
	public static String getInputFile(String id){
		return logDir + File.separator + "input_" + id + ".txt";
	}
	
	public static String getReceiverFile(String sender_id, String receiver_id){
		return logDir + File.separator + receiver_id + 
				"_received_from_" + sender_id + ".txt";
	}
	

}
