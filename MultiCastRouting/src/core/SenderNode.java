package core;

/**
 * For sender Node.
 * @author Sanket Chandorkar
 */
public class SenderNode extends Node {

	/**
	 * Message to broadcast.
	 */
	private String msg;

	public SenderNode(String id, String msg) throws Exception {
		super(id);
		this.msg = msg;
	}

	String getMsg() {
		return msg;
	}

}
