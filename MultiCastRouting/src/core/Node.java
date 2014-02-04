package core;

import graph.IllegalStateException;
import graph.NetworkGraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

import msg.DataMsg;
import msg.HelloMsg;
import msg.JoinInfo;
import msg.JoinMsg;
import msg.LinkStateMsg;
import msg.Message;
import msg.MessageManager;

import common.Globals;

/**
 * Node class
 * @author Sanket Chandorkar
 */
public class Node {

	/**
	 * ID of the node.
	 */
	private String id;
	
	private BufferedReader inReader;
	
	private PrintWriter outWriter;
	
	private NetworkGraph networkGraph;
	
	/**
	 * Set containing all those source nodes which have receiver nodes along 
	 * this node path. 
	 */
	private ArrayList<JoinInfo> joinList;
	
	/**
	 * Stores incomming interface or in links for this node
	 * detected using the hello messages.
	 */
	private HashSet<String> inLinkSet;
	
	/**
	 * Current time stamp for this node.
	 */
	private int timeStamp;
	
	public Node(String id) throws Exception {
		this.id = id;
		File inFile = new File(Globals.getInputFile(id));
		File outFile = new File(Globals.getOutputFile(id));
		outWriter = new PrintWriter(new FileWriter(outFile), true);
		if(!inFile.exists()){
			FileWriter fw = new FileWriter(inFile);
			fw.close();
		}
		inReader = new BufferedReader(new FileReader(inFile));
		inLinkSet = new HashSet<String>();
		timeStamp = 0;
		networkGraph = new NetworkGraph();
		joinList = new ArrayList<JoinInfo>();
	}

	public static void main(String[] args) throws Exception{
		Node node = Node.processArguments(args);
		System.out.println(" - Started process execution : Node = " + node.getId() + " | Node Type: " + node.getClass());
		node.start();
		node.Finalize();
//		node.printInLinks();	// debug
//		node.networkGraph.printGraph(node.getId());		// debug
		System.out.println(" - Ended process execution : Node = " + node.getId() + " | Node Type: " + node.getClass());
		System.exit(Globals.EXIT_SUCCESS);
	}


	private void start() throws Exception{
		
		for (int i = 0; i < 150; i++) {
			
			// Send hello message
			sendHelloMessage(i);
			
			// Send a link-state-advertisement
			sendLinkStateAdvertisement(i);
			
			// send data message if its time to send the same
			sendDataMessage(i);
			
			// Send join message to each parent 
			refreshParent(i); 
			
			// Reads Input File Messages
			readInputFileMessages(i);
			
			// Update current graph for current node
			networkGraph.addUpdateNetworkGraph(new LinkStateMsg(id, timeStamp, inLinkSet), i);

			// Check for expiry of networkNodes in network graph.
			networkGraph.updateLiveness(i);
			
			// update join nodes
			updateForLivenessJoinInfo(i);
			
			// Sleep for 1 sec
			Thread.sleep(1000);
			
		}
	}
	
	private void sendDataMessage(int itr) {
		if(this instanceof SenderNode){
			// send data after every 10 secs
			if(itr%11 == 0){ 
				SenderNode snode = (SenderNode) this;
				writeMsg((new DataMsg(snode.getId(), snode.getId(), snode.getMsg())).toString());
			}
		}
	}

	/**
	 * Send join message to each parent of each tree I am involved in, if it is time to do so
	 */
	private void refreshParent(int itr) throws Exception{
		
		if(this instanceof ReceiverNode){
			ReceiverNode rnode = (ReceiverNode) this;
			String _senderID = rnode.getSenderID(); 
			String _receiverID = rnode.getId(); 
			int ts =rnode.timeStamp; 
			
			if(ts != -1){
				if(ts%12 != 0){	// is it time to send next message
					rnode.timeStamp = rnode.timeStamp + 1;
					return;
				}
			}
			
			JoinMsg jMsg = generateJoinMsg(_senderID, _receiverID);
			if(jMsg == null){
				rnode.timeStamp = -1;
				return;
			}
			rnode.timeStamp = 1;
			writeMsg(jMsg.toString());
		}
	}
	
	private void updateForLivenessJoinInfo(int itr){
		ArrayList<JoinInfo> joinList_temp = new ArrayList<>();
		for(JoinInfo j: joinList){
			if( !((itr - j.timeStamp) > 20) )
				joinList_temp.add(j);
		}
		joinList = joinList_temp;
	}
	
	private JoinMsg generateJoinMsg(String _senderID, String _receiverID) throws Exception{
		
		// check if sender is live in this nodes graph
		if(networkGraph.netNodeTab.get(_senderID) == null)
			return null;
	
		ArrayList<String> path1 = networkGraph.getShortestPath(_senderID, _receiverID);
		if(this instanceof ReceiverNode)
//			System.out.println("path1: " + path1); 
		// node is not live / no path exists. 
		if(path1.size() == 1){
			return null;
		}
		
		String _parentID;	// M
		ArrayList<String> interNodes;
		if(path1.size() == 2){
			_parentID = _senderID;
			interNodes = new ArrayList<>();
		}
		else{
			_parentID = path1.get(path1.size() - 2);
//			System.out.println("_parentID " + _parentID);
//			System.out.println("_receiverID " + _receiverID);
			ArrayList<String> path2 = networkGraph.getShortestPath(_receiverID, _parentID);
			if(path2.size() == 1)
				throw new IllegalStateException();
			else if(path2.size() == 2){
				interNodes = new ArrayList<>();
			}
			else{
				interNodes = new ArrayList<>();
				for(int i = 1 ; i < path2.size()-1; i++ ){
					interNodes.add(path2.get(i));
				}
			}
		}
//		System.out.println(id + " | " +new JoinMsg(_receiverID, _senderID, _parentID, interNodes));
		return new JoinMsg(_receiverID, _senderID, _parentID, interNodes);
	}

	/**
	 * Send a link-state-advertisement if it is time for a new one
	 * Assumption: Message is broadcasted only if information is present about the inLinks.
	 */
	private void sendLinkStateAdvertisement(int itr) throws Exception {
		// send link-state-advertisement after every 10 sec
		if(itr%11 == 0){ 
			writeMsg((new LinkStateMsg(id, timeStamp++, inLinkSet)).toString());
		}
	}

	/**
	 * Read the input file and process each new message received
	 * @param itr
	 */
	private void readInputFileMessages(int itr) throws Exception {
		String inMsg = null;
		while( (inMsg = readMsg()) != null){
			Message msgObj= MessageManager.processMsg(inMsg);
			
			// process hello messages
			if(msgObj instanceof HelloMsg){
				// update incoming interface
				inLinkSet.add(msgObj.getMsgID());
				continue;
			}
			
			// process link state messages
			if (msgObj instanceof LinkStateMsg) {
				LinkStateMsg lsm = (LinkStateMsg) msgObj;
				
				// process this message: add/update network graph
				boolean fwr = networkGraph.addUpdateNetworkGraph(lsm, itr);
				
				// forward to out interface
				if(fwr == true)
					writeMsg(inMsg);
				
				continue;
			}
			
			// process and forward only if in chain
			if(msgObj instanceof JoinMsg){
				
				JoinMsg jMsg = (JoinMsg) msgObj;
				
				if(this instanceof SenderNode){
					if(jMsg.getSenderID().equals(id))
						continue;	// drop packet					
				}
				
				// if this is the join node in the tree
				if(jMsg.getParentID().equals(id) && !jMsg.getInterNodes().isEmpty())
					throw new IllegalStateException();
				
				// this is joining node in the tree
				if(jMsg.getParentID().equals(id) && jMsg.getInterNodes().isEmpty()){
					// update that its in tree
					JoinInfo jInfo = getFromJoinList(jMsg.getSenderID());
					if(jInfo != null)
						joinList.remove(jInfo);
					
					// replace old from new
					joinList.add(new JoinInfo(jMsg, itr));
					
					// send new message
					String _senderID = jMsg.getSenderID(); 
					String _receiverID = id; 
					
					JoinMsg jMsgNew = generateJoinMsg(_senderID, _receiverID);
					if(jMsgNew == null){
						System.out.println("msg dropped");  // TODO: comment
						// drop message
						continue;
//						throw new IllegalStateException();	// TODO: handle when a node is dead/ not live
					}
					
					writeMsg(jMsgNew.toString());
					continue;
				}
				
				// foward msg if in chain
				if(jMsg.getFirst().equals(id)){
					jMsg.removeFirstInterNode();
					writeMsg(jMsg.toString());
					continue;
				}
				
				continue;	// discard message
			}
			
			if(msgObj instanceof DataMsg){
				DataMsg dMsg = (DataMsg) msgObj;
				
				// filter for sender : so that the message does not bounce back
				if(this instanceof SenderNode){
					if(id.equals(dMsg.getRootID()))
						continue;
				}
				
				
				// for receiver
				if(this instanceof ReceiverNode){
					ReceiverNode rnode = (ReceiverNode) this;
					if(rnode.getSenderID().equals(dMsg.getRootID())){
						// for this receiver , log msg
						rnode.writeReceivedMsg(dMsg.getMsg());
					}
					continue;
				}
				
				// for joined nodes in the tree
				if(joinListContains(dMsg.getRootID())){
					if(dMsg.getMsgID().equals( (getFromJoinList(dMsg.getRootID()).getChildNode())) )
							continue;
					
					// foward msg
					dMsg.setMsgID(id);	// update msg
					writeMsg(dMsg.toString());
					continue;
				}
				
				continue;	// discard message
			}
			
		}
	}

	/**
	 * Send hello message, if it is time for another one
	 * @param itr	iterator denoting time in secs.
	 * @throws Exception
	 */
	private void sendHelloMessage(int itr) throws Exception {
		// send hello message after every 5 sec
		if(itr%6 == 0){ 
			writeMsg((new HelloMsg(id)).toString());
		}
	}
	
	private static Node processArguments(String[] args) throws Exception{
		
		if(args.length != 1 && args.length != 3){
			System.out.println("Incorrect number of arguments");
			Node.printUsage();
			System.out.println("Exiting program Now !!!");
			System.exit(Globals.EXIT_ERROR);
		}
		
		if(args.length == 1){
			int id = 0;
			String idStr = args[0];
			try{
				id = Integer.parseInt(idStr);
			}
			catch(NumberFormatException excp){
				System.out.println("Incorrect argument format: Please provide a number as argument");
				Node.printUsage();
				System.out.println("Exiting program Now !!!");
				System.exit(Globals.EXIT_ERROR);
			}
			return new Node(idStr);
		}
		else if(args.length == 3){
			int id = 0;
			String idStr = args[0];
			String idOtherStr = args[2];
			
			try{
				id = Integer.parseInt(idStr);
				if(args[1].equalsIgnoreCase("sender")){
					return new SenderNode(idStr, idOtherStr);
				}
				else if(args[1].equalsIgnoreCase("receiver")){
					return new ReceiverNode(idStr, idOtherStr);
				}
			}
			catch(NumberFormatException excp){
				System.out.println("Incorrect argument format: Please provide a number as argument");
				Node.printUsage();
				System.out.println("Exiting program Now !!!");
				System.exit(Globals.EXIT_ERROR);
			}
		}
		
		System.out.println("Incorrect usage: Please enter command as one of the below:");
		Node.printUsage();
		System.out.println("Exiting program Now !!!");
		System.exit(Globals.EXIT_ERROR);
		return null;
	}
	
	private static void printUsage(){
		System.out.println("Usage 1: node <node_id>");
		System.out.println("Usage 2: node <sender_id> sender \"msg\" ");
		System.out.println("Usage 3: node <receiver_id> receiver <sender_id>");
	}
	
	private void writeMsg(String msg){
		outWriter.println(msg);
	}
	
	public String readMsg() throws Exception{
		return inReader.readLine();
	}
	
	private void Finalize() throws Exception{
		inReader.close();
		outWriter.close();
		if(this instanceof ReceiverNode){
			ReceiverNode rn = (ReceiverNode) this;
			rn.Finalize();
		}
	}

	public String getId() {
		return id;
	}
	
	private JoinInfo getFromJoinList(String idToCheck) {
		for(JoinInfo j: joinList){
			if(j.getRootID().equals(idToCheck))
				return j;
		}
		return null;
	}
	
	private boolean joinListContains(String idToCheck) {
		for(JoinInfo j: joinList){
			if(j.getRootID().equals(idToCheck))
				return true;
		}
		return false;
	}
	// ------------------------ For debugging purposes -------------
	
	private void printInLinks() {
		System.out.println("  -- Incomming nodes for node = " + id);
		for(String s: inLinkSet)
			System.out.println("  > " + s);
	}
}
