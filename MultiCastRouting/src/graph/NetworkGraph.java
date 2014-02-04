package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.PriorityQueue;

import msg.LinkStateMsg;

/**
 * This class is useful for maintaining graph information.
 * @author Sanket Chandorkar
 */
public class NetworkGraph
{
	/**
	 * < id , Vertex Object >
	 */
	public Hashtable<String,NetworkNode> netNodeTab;
	
	public NetworkGraph(){
		netNodeTab = new Hashtable<>();
	}
	
	/**
	 * Add/Update the network graph.
	 * @return To / or Not to foward to out interface.
	 */
	public boolean addUpdateNetworkGraph(LinkStateMsg lsMsg, int itr){
		
		NetworkNode node = netNodeTab.get(lsMsg.getMsgID());
		HashSet<String> prevInLinks;
		
		// add / update node. 
		if(node == null){	// add
			node = new NetworkNode(lsMsg.getMsgID(), Integer.parseInt(lsMsg.getTimeStamp()), itr, lsMsg.getInLinkSet());
			netNodeTab.put(lsMsg.getMsgID(), node);
			prevInLinks = new HashSet<>();
		}
		else{	// update
			// update time stamp
			int currTimeStamp = Integer.parseInt(lsMsg.getTimeStamp());
			if(currTimeStamp <= node.getTimeStamp())
				return false;

			prevInLinks = node.getInLinks();
			node.update(currTimeStamp, itr, lsMsg.getInLinkSet());
		}
		
		// delete pre src -> dest links
		String destID = lsMsg.getMsgID();
		NetworkNode srcNode;
		for(String srcID: prevInLinks){
			srcNode = netNodeTab.get(srcID);
			if(srcNode != null){
				srcNode.deleteOutEdge(destID);
			}
		}
		
		// Add once again out edges
		for(String srcID: lsMsg.getInLinkSet()){
			srcNode = netNodeTab.get(srcID);
			if(srcNode == null){
				srcNode = new NetworkNode(srcID);
				netNodeTab.put(srcID, srcNode);
			}
			srcNode.addOutEgde(destID);
		}
		
		return true;
	}
	
	
	/**
	 * Check for expiry of networkNodes in network graph.
	 * @param itr
	 */
	public void updateLiveness(int itr) {
		Enumeration<NetworkNode> en = netNodeTab.elements();
		while(en.hasMoreElements()){
			NetworkNode v = en.nextElement();
			v.updateLiveness(itr);
		}
	}
    
	private void resetDistances(){
		Enumeration<NetworkNode> en = netNodeTab.elements();
		while(en.hasMoreElements()){
			NetworkNode v = en.nextElement();
			v.minDistance = Double.POSITIVE_INFINITY;
		}
	}
	
	private void computePaths(NetworkNode source) {
		source.minDistance = 0.;
		PriorityQueue<NetworkNode> vertexQueue = new PriorityQueue<NetworkNode>();
		vertexQueue.add(source);

		while (!vertexQueue.isEmpty()) {
			NetworkNode u = vertexQueue.poll();

			// Visit each edge exiting u
			for (String vStr : u.outLinks) {
				NetworkNode v  = netNodeTab.get(vStr);
				if(!v.isLive()) // added
					continue;
				double weight = 1;
				double distanceThroughU = u.minDistance + weight;
				if (distanceThroughU < v.minDistance) {
					vertexQueue.remove(v);

					v.minDistance = distanceThroughU;
					v.previous = u;
					vertexQueue.add(v);
				}
			}
		}
		source.previous = null;	// added for consistency
	}

    private static ArrayList<String> getShortestPathTo(NetworkNode target)
    {
    	ArrayList<String> path = new ArrayList<String>();
        for (NetworkNode vertex = target; vertex != null; vertex = vertex.previous)
            path.add(vertex.id);

        Collections.reverse(path);
        return path;
    }
    
    public ArrayList<String> getShortestPath(String src, String rcv) throws Exception{
    	
    	// reset internal values for shortest path calculation
    	resetDistances();
    	
       	NetworkNode srcNode = netNodeTab.get(src);
       	if(srcNode == null)
       		throw new IllegalStateException();
       	
       	NetworkNode rcvNode = netNodeTab.get(rcv);
       	if(rcvNode == null)
       		throw new IllegalStateException();
    	
		if (srcNode.isLive() && rcvNode.isLive()) {
			computePaths(srcNode);
			return getShortestPathTo(rcvNode);
		} else {
			ArrayList<String> path = new ArrayList<String>();
			path.add(rcv);
			return path;
		}
    }

    
    // ------------------- Debugging code ------------------------------
    
    private void addLink(String src_id, String dest_id){
    	NetworkNode src_vertex = getVertex(src_id);
    	NetworkNode dest_vertex = getVertex(dest_id);
    	src_vertex.addOutEgde(dest_id);
    }
    
    private NetworkNode getVertex(String id){
    	NetworkNode v = netNodeTab.get(id);
    	if(v == null){
    		v = new NetworkNode(id, 0, 0, new HashSet<String>());
    		netNodeTab.put(id, v);
    	}
    	return v;
    }

    public synchronized void printGraph(String currID) throws Exception{
    	Thread.sleep(Integer.parseInt(currID)*200);
    	Enumeration<NetworkNode> en = netNodeTab.elements();
		while(en.hasMoreElements()){
			NetworkNode v = en.nextElement();
			System.out.println("------------------- For Node: " + currID + "------------------------");
			System.out.println("Node : " + v.id);
			System.out.println("Is live:  " + v.isLive());
			System.out.println("outlinks : " + v.outLinks);
			System.out.println("inlinks : " + v.getInLinks());
			System.out.println("Timestamp: " + v.getTimeStamp());
			System.out.println("logTime: " + v.logTime);
		}
    }
    
//    public static void main(String[] args) throws Exception
//    {
//    	NetworkGraph g = new NetworkGraph();
//    	
//    	g.addLink("0","4");
//    	g.addLink("4","0");
//    	g.addLink("4","5");
//    	g.addLink("5","4");
//    	g.addLink("5","9");
//    	g.addLink("9","5");
//    	g.addLink("0","8");
//    	g.addLink("8","0");
//    	g.addLink("0","3");
//    	g.addLink("3","5");
//    	g.addLink("5","3");
//    	g.addLink("3","9");
//    	g.addLink("1","3");
//    	g.addLink("3","1");
//
//    	NetworkNode v0 = g.netNodeTab.get("1");
//    	
////    	NetworkNode v5 = g.netNodeTab.get("3");
////    	v5.setLive(false);
//    	
////    	System.out.println(g.getShortestPath("9","9"));  // exception cond
//    	
//    	g.resetDistances();
//        g.computePaths(v0);
//        
//        Enumeration<NetworkNode> en = g.netNodeTab.elements();
//		while(en.hasMoreElements()){
//			NetworkNode v = en.nextElement();
//			System.out.println("Distance to " + v + ": " + v.minDistance);
//			ArrayList<String> path = g.getShortestPath("1",v.id);
//			System.out.println("Path: " + path);
//		}
//	}
}
