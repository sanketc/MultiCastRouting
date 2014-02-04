package graph;

import java.util.HashSet;

/**
 * Represents a node of the network graph. 
 * @author Sanket Chandorkar
 */
class NetworkNode implements Comparable<NetworkNode>
{
	/**
	 * Id for this node.
	 */
	public final String id;
	
	/**
	 * Set of output links.
	 */
	public HashSet<String> outLinks;
	
	/**
	 * Required for shortest path calculation.
	 */
	public double minDistance;
	
	/**
	 * Required for shortest path calculation.
	 */
	public NetworkNode previous;
    
	/**
	 * Helps in implementing the 30 sec node expiry info
	 * stores the time of receiving the message.
	 */
	int logTime;
	
	/**
	 * Time stamp of last message received
	 */
	private int timeStamp;
	
	/**
	 * 30 sec time out.
	 */
	private static final int timeOut = 30; 
	
	/**
	 * Set of in links. 
	 */
	public HashSet<String> inLinks;
	
	/**
	 * Indicates if the node is live.
	 * Liveness is subjected to the 30 sec expity;
	 */
	private boolean live;
	
	/**
	 * Create a live node.
	 * @param id
	 * @param timeStamp
	 * @param itr
	 * @param inLinks
	 */
	public NetworkNode(String id, int timeStamp, int itr,HashSet<String> inLinks) {
		this.id = id;
		this.timeStamp = timeStamp;
		this.logTime = itr;
		this.minDistance = Double.POSITIVE_INFINITY;
		this.live = true;
		this.outLinks = new HashSet<>();
		this.inLinks = inLinks;
	}
	
	/**
	 * Create a dead node.
	 * @param id
	 */
	public NetworkNode(String id) {
		this.id = id;
		this.timeStamp = -1;
		this.logTime = 0;
		this.minDistance = Double.POSITIVE_INFINITY;
		this.live = false;
		this.outLinks = new HashSet<>();
		this.inLinks = new HashSet<>();
	}
	
	public void update(int timeStamp, int itr, HashSet<String> inLinkSet) {
		this.timeStamp = timeStamp;
		this.logTime = itr;
		this.live = true;
		this.inLinks = inLinkSet;
	}

	/**
	 * Check for expiry of networkNodes in network graph.
	 * @param itr
	 */
	public void updateLiveness(int itr) {
		if(itr > (logTime + timeOut) )
			live = false;
	}

	public void addOutEgde(String destID){
		outLinks.add(destID);
	}
	
	public void deleteOutEdge(String destID) {
		outLinks.remove(destID);
	}

	public int compareTo(NetworkNode other) {
		return Double.compare(minDistance, other.minDistance);
	}

	public String toString() {
		return id;
	}

	// -------------- Getter and setters ------------------
	
	public int getTimeStamp() {
		return timeStamp;
	}

	public boolean isLive() {
		return live;
	}

	public HashSet<String> getInLinks() {
		return inLinks;
	}

	public void setLive(boolean value) {
		this.live = value;
	}
}
