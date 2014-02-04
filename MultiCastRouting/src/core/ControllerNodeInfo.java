package core;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import common.Globals;

/**
 * ControllerClass for Node information maintained at controller level. 
 * @author Sanket Chandorkar
 */
public class ControllerNodeInfo {
	
	private String id;
	
	private ArrayList<ControllerNodeInfo> outList;
	
	private BufferedReader outReader;
	
	private PrintWriter inWriter;

	public ControllerNodeInfo(String id) throws Exception {
		this.id = id;
		File inFile = new File(Globals.getInputFile(id));
		File outFile = new File(Globals.getOutputFile(id));
		inWriter = new PrintWriter(new FileWriter(inFile), true);
		if(!outFile.exists()){
			FileWriter fw = new FileWriter(outFile);
			fw.close();
		}
		outReader = new BufferedReader(new FileReader(outFile));
		outList = new ArrayList<>();
	}
	
	/**
	 * Forward the packet to all the input files of all the outgoing links.
	 * @throws Exception
	 */
	public void forwardMsg() throws Exception{
		String outMsg = null;
		while( (outMsg = readMsg()) != null){
			for(ControllerNodeInfo destNode : outList){
				destNode.writeMsg(outMsg);
			}
		}
	}
	
	public void addOutLink(ControllerNodeInfo node){
		outList.add(node);
	}
	
	public ArrayList<ControllerNodeInfo> getOutList(){
		return outList;
	}
	
	public void writeMsg(String msg){
		inWriter.println(msg);
	}

	public String readMsg() throws Exception{
		return outReader.readLine();
	}
	
	public void Finalize() throws Exception{
		inWriter.close();
		outReader.close();
	}

}
