package msg;

public class HelloMsg extends Message{

	public static final String HELLO_MSG = "hello";
	
	public HelloMsg(String id) {
		super(id);
	}

	public String toString(){
		return HELLO_MSG + " " + getMsgID();
	}
}
