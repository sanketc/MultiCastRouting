package msg;

public class JoinInfo {
	
	private String rootID;
	
	private JoinMsg jMsg;
	
	public int timeStamp;
	
	public JoinInfo(JoinMsg jMsg, int timeStamp){
		this.rootID = jMsg.getSenderID();
		this.jMsg = jMsg;
		this.timeStamp = timeStamp;
	}
	
	public String getChildNode(){
		return jMsg.getMsgID();
	}

	public JoinMsg getjMsg() {
		return jMsg;
	}

	public void setjMsg(JoinMsg jMsg) {
		this.jMsg = jMsg;
	}

	public String getRootID() {
		return rootID;
	}
}
