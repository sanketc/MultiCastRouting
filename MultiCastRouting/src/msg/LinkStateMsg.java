
package msg;

import java.util.HashSet;

public class LinkStateMsg extends Message{

	public static final String LINK_STATE_MSG = "linkstate";
	
	public static final String SPACE = " ";
	
	private String timeStamp;
	
	private HashSet<String> inLinkSet;
	
	public LinkStateMsg(String id, int timeStamp, HashSet<String> inLinkSet) {
		super(id);
		this.timeStamp = timeStamp + "";
		this.inLinkSet = inLinkSet;
	}
	
	public LinkStateMsg(String[] tokens) {
		super(tokens[1]);
		this.timeStamp = tokens[2];
		this.inLinkSet = new HashSet<String>();
		for(int i = 3; i < tokens.length; i++){
			inLinkSet.add(tokens[i]);
		}
	}

	public HashSet<String> getInLinkSet() {
		return inLinkSet;
	}

	public String toString(){
		return LINK_STATE_MSG + SPACE + 
				getMsgID() + SPACE +
				formatStr() + SPACE +
				formatInLinks();
	}
	
	public String getTimeStamp() {
		return timeStamp;
	}

	private String formatStr(){
		String s = timeStamp;
		if(s.length() == 1)
			s = "0" + s;
		return s;
	}
	
	private String formatInLinks(){
		String s = "";
		for(String linkID: inLinkSet){
			s+=linkID;
			s+=SPACE;
		}
		return s.trim();
	}
	
}
