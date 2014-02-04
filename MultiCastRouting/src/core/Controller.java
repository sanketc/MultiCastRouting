package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Hashtable;

import common.Globals;

/**
 * Controller Class
 * @author Sanket Chandorkar
 */
public class Controller {

	/**
	 * Network map based on topology.txt
	 * <id, ControllerNodeInfo object >
	 */
	Hashtable<String, ControllerNodeInfo> netMap;
	
	public Controller() {
		netMap = new Hashtable<String, ControllerNodeInfo>();
	}

	public static void main(String[] args) throws Exception{
		Controller con = new Controller();
		con.readTopologyFile();
		System.out.println(" - Started process execution : Controller");
		con.start();
		con.Finalize();
		System.out.println(" - Ended process execution : Controller");
		System.exit(Globals.EXIT_SUCCESS);
	}

	private void start() throws Exception{
		long startTime = System.currentTimeMillis();
		
		while(true){
			
			// check and process messages.
			Enumeration<ControllerNodeInfo> en = netMap.elements();
			while(en.hasMoreElements()){
				ControllerNodeInfo node = en.nextElement();
				
				// foward the packet to all out going links
				node.forwardMsg();
			}
				
			Thread.sleep(200);
			long endTime = System.currentTimeMillis();
			int timeInSec = (int)((float)(endTime - startTime) / 1000);
			// breaking condition.
			if( timeInSec > 155 )
				break;
		}
	}

	private void readTopologyFile() throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(new File(Globals.topologyFile)));
		String line = null;
		while((line=br.readLine())!=null){
			line = line.trim();
			if(line.equals(""))
				continue;
			String[] tokens = line.split(" ");
			if(tokens.length != 2){
				System.out.println("Incorrect line format Error !!!");
				System.out.println("Line : " + line);
				System.out.println("File: " + Globals.topologyFile);
				System.out.println("Expected Format: <source_id> <destination_id>");
				System.out.println("Action: Skipping this line !!");
				continue;
			}
			int srcID, destID;
			String srcIDStr = tokens[0];
			String destIDStr = tokens[1];
			try{
				srcID = Integer.parseInt(srcIDStr);
				destID = Integer.parseInt(destIDStr);
			}
			catch(NumberFormatException excp){
				System.out.println("Incorrect line format Error !!!");
				System.out.println("Line : " + line);
				System.out.println("File: " + Globals.topologyFile);
				System.out.println("Expected Format: <source_id> <destination_id>");
				System.out.println("Action: Skipping this line !!");
				continue;
			}
			ControllerNodeInfo src_node = getControllerNodeInfoObject(srcIDStr);
			ControllerNodeInfo dest_node = getControllerNodeInfoObject(destIDStr);
			src_node.addOutLink(dest_node);
		}
		
		br.close();
		
	}
	
	private ControllerNodeInfo getControllerNodeInfoObject(String id) throws Exception{
		ControllerNodeInfo node = netMap.get(id);
		if(node == null){
			node = new ControllerNodeInfo(id);
			netMap.put(id, node);
		}
		return node;
	}
	
	private void Finalize() throws Exception{
		// check and process messages.
		Enumeration<ControllerNodeInfo> en = netMap.elements();
		while (en.hasMoreElements()) {
			ControllerNodeInfo node = en.nextElement();
			node.Finalize();
		}
	}
	
}