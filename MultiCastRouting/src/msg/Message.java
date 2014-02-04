package msg;

public abstract class Message {
	
	private String id;
	
	public Message(String id){
		this.id = id;
	}
	
	public String getMsgID(){
		return id;
	}
	
	public void setMsgID(String id){
		this.id = id;
	}
}