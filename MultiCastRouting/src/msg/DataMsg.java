package msg;

public class DataMsg extends Message{
	
	public static final String DATA_MSG = "data";
	
	public static final String SPACE = " ";
	
	private String rootID;
	
	private String msg;
	
	public DataMsg(String id, String rootID, String msg) {
		super(id);
		this.rootID = rootID;
		this.msg = msg;
	}

	public DataMsg(String[] tokens) {
		super(tokens[1]);
		this.rootID = tokens[2];
		this.msg = "";
		for(int i = 3; i < tokens.length ; i++)
			this.msg += SPACE + tokens[i];
		this.msg = this.msg.trim();
	}

	public String toString(){
		return DATA_MSG + SPACE + 
				getMsgID() + SPACE +
				rootID + SPACE +  msg;
	}

	public String getRootID() {
		return rootID;
	}
	
	public String getMsg() {
		return msg;
	}
}
