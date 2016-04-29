package graph;

import java.awt.Point;
import java.util.HashMap;

/**
* @author Marco Umer marco.umer@gmail.com
* 
* The flow class is an helper class that stores a graph' representation in term of edges and allows to store flow units per edge	
* A directed link is represented by a couple of Integer (x source and d destination) and well fit in the Point class (import java.awt.Point)
*
**/

public class Flows {
		
	// Flows from any source to destination nodes are recorded in a HashMap flowLinks using a Point as key and a Integer value as number of flow units
	// as a key and Integer to record the number of flows traversing the link
	private HashMap<Point, Integer> flowLinks = new HashMap<Point, Integer>();      

	
	public void allocateFlows(Integer source, HashMap<Integer,Integer> BFSTreeUpstream) {

		// This methods allocates flow units to edges between a source and a destination node following the shortest path in the BFS tree
		// Assume path from source to destination as being already populated in BFSTreeUpstream
		// Function explore upstream from destination to source
		
		// Input value: an integer representing a source node
		// Input value: a tree sourced at the "source" node, representing for each node the neighbor versus the source
		// Side effect: change the flowLinks private data structure adding flow units as exploring a tree
		

		// Algorithm implemented:
		//		Trace back the BFS path in BFSTreeUpstream while finding the source node
		//		  Add one unit of flow to flowLinks for the edge being explored
		//		  Assure that the edge has being already recorded as key or create it
		//		  Check the next edge toward the source node
		//		Add the last unit of flow on the edge toward the source and return

		// for each node in the BFS tree search the path back to the source node (provided as input)
		for (Integer node:BFSTreeUpstream.keySet()) {
			// search the tree upstream up finding the source node
			while (!BFSTreeUpstream.get(node).equals(source)) {
				// represent the edge between node and its parent toward upstream as a Point of two integers
				// as working on a undirected graph, represent the edge with the lower node integer as x and higher as y
				Point edge = new Point(Math.min(BFSTreeUpstream.get(node),node),Math.max(BFSTreeUpstream.get(node),node));
				
				// for each edge traversed as exploring the tree, allocate a flow unit in flowLinks for the edge or add the edge with 1 flow unit			
				if (flowLinks.containsKey(edge)) {
					flowLinks.put(edge, flowLinks.get(edge)+1);
				} else {
					flowLinks.put(edge,1);
				}
				// search the next node toward the source
				node=BFSTreeUpstream.get(node);			
			}
			
			// add the last edge to source
			Point edge = new Point(Math.min(BFSTreeUpstream.get(node),node),Math.max(BFSTreeUpstream.get(node),node));
				if (flowLinks.containsKey(edge)) {
					flowLinks.put(edge, flowLinks.get(edge)+1);
				} else {
					flowLinks.put(edge,1);
			}	
		}	
	}


	public Point moreFlow(){
		// this method return the edge (represented as Point variable) in the flowLinks data structure with higher number of flow units
		
		Point pointToReturn=new Point();
		Integer numFlows=0;
		
		if (flowLinks.isEmpty()) {
			System.out.println("No Edges has being identified !!! - Check algorith if any flow have being allocated");
			return null;
		}
		
		// for each edge recorded in flowLinks
		for (Point point:flowLinks.keySet()) {
			// if the number of flow units accumulated is higher
			if (flowLinks.get(point)>numFlows){
				// make this the edge to return and update the max value of flow units found
				pointToReturn=point;
				numFlows=flowLinks.get(pointToReturn);
			}
		}
		
		return pointToReturn;
	}
	
	public void resetFlows(){
		flowLinks.clear();
	}
	
	public String toString() {
		return flowLinks.toString();
	}
	
	public static void main(String[] args) {
		
		// The following example test the flow call 
		System.out.println("Example using Flows");
		HashMap<Integer,Integer> BFSTreeUpstreamTest= new HashMap<Integer,Integer>();
		Flows flowTest = new Flows();
		
		// static definition of the tree to search
		BFSTreeUpstreamTest.put(5, 3);
		BFSTreeUpstreamTest.put(4, 3);
		BFSTreeUpstreamTest.put(3, 2);
		BFSTreeUpstreamTest.put(2, 1);
		
		System.out.println("Example BFS"+BFSTreeUpstreamTest);		
		
		flowTest.allocateFlows(1,BFSTreeUpstreamTest);

		System.out.println("Example Flows allocation: "+flowTest);
		System.out.println(flowTest.moreFlow());
	
	}

}
