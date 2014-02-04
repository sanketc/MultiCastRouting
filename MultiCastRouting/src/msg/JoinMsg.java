package msg;

import java.util.ArrayList;

public class JoinMsg extends Message {

	public static final String JOIN_MSG = "join";
	
	public static final String SPACE = " ";
	
	/**
	 * Root of the tree
	 */
	private String senderID;
	
	private String parentID;
	
	/**
	 * List of intermediate nodes
	 */
	private ArrayList<String> interNodes;
	
	public JoinMsg(String id, String senderID, String parentID,	ArrayList<String> interNodes) {
		super(id);
		this.senderID = senderID;
		this.parentID = parentID;
		this.interNodes = interNodes;
	}

	public JoinMsg(String[] tokens) {
		super(tokens[1]);
		this.senderID = tokens[2];
		this.parentID = tokens[3];
		this.interNodes = new ArrayList<String>();
		for(int i = 4; i < tokens.length; i++){
			interNodes.add(tokens[i]);
		}
	}

	public String toString(){
		return JOIN_MSG + SPACE + 
				getMsgID() + SPACE +
				senderID + SPACE +
				parentID + SPACE +
				formatInterNodes();
	}
	
	private String formatInterNodes(){
		String s = "";
		for(String linkID: interNodes){
			s = s + linkID + SPACE;
		}
		return s.trim();
	}
	
	public void removeFirstInterNode(){
		if(!interNodes.isEmpty())
			interNodes.remove(interNodes.get(0));
	}
	
	public String getFirst(){
		if(interNodes.isEmpty())
			return "";
		else
			return interNodes.get(0);
	}
	
	public String getSenderID() {
		return senderID;
	}

	public String getParentID() {
		return parentID;
	}

	public ArrayList<String> getInterNodes() {
		return interNodes;
	}
	
}
