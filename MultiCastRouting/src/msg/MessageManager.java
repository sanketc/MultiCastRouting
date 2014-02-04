package msg;

/**
 * Manager class for messages.
 * @author Sanket Chandorkar
 *
 */
public class MessageManager {
	
	public static Message processMsg(String msg) throws Exception{
		
		String[] tokens = msg.split(" ");
		if(tokens.length < 1){
			throw new IllegalMessageException();
		}
		
		String type = tokens[0];
		String idStr = tokens[1];
		
		if(type.equalsIgnoreCase(HelloMsg.HELLO_MSG)){
			return new HelloMsg(idStr);
		}
		else if(type.equalsIgnoreCase(LinkStateMsg.LINK_STATE_MSG)){
			return new LinkStateMsg(tokens);
		}
		else if(type.equalsIgnoreCase(JoinMsg.JOIN_MSG)){
			return new JoinMsg(tokens);
		}
		else if(type.equalsIgnoreCase(DataMsg.DATA_MSG)){
			return new DataMsg(tokens);
		}
		else{
			throw new IllegalMessageException();
		}
	}
	
}
