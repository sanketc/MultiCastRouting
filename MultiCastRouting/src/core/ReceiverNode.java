package core;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import common.Globals;

/**
 * For receiver node.
 * @author Sanket Chandorkar
 */
public class ReceiverNode extends Node {

	/**
	 * Sender node id to join.
	 */
	private String senderID;

	/**
	 * File handle for that file that records the messages receined from the sender.
	 */
	private PrintWriter rcv_msg_pw;
	
	public int timeStamp = -1;
	
	public ReceiverNode(String receiver_id, String sender_id) throws Exception{
		super(receiver_id);
		this.senderID = sender_id;
		File file = new File(Globals.getReceiverFile(sender_id, receiver_id));
		this.rcv_msg_pw = new PrintWriter(new FileWriter(file), true);
	}
	
	public void writeReceivedMsg(String msg){
		rcv_msg_pw.println(msg);
	}

	public String getSenderID() {
		return senderID;
	}
	
	public void Finalize(){
		rcv_msg_pw.close();
	}
}
