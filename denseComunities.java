package graph;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import util.GraphLoader;

/**
 * @author Marco Umer marco.umer@gmail.com
 * 
 * DenseCommunities class supports the search for high density connected communities in a graph
 * A high density community has high number of edges between vertexes in the same community and lower number of edges 
 * toward vertexes from other communities.
 * The strategy used to separate the graph in communities consists in removing edges from the graph if they are the most traversed edges 
 * when considering shorter paths tree between each node in the graph. 
 * Removing the most traversed edge has the highest probability to separate an initial graph in more communities but we may need to
 * iterate the process multiple time because the graph may have redundant paths.

 * DenseCommunities class uses the class CapGraph.java to represent the graph under investigation and leverage its method "getSCCs" to
 * divide the graph in connected components (or communities in this exercise). 

 * This class supports two type of searches:
 * seachComponentsIterations that takes an integer as the number of edges to remove to divide the graph in dense communities
 * seachComponentsSet that removes from the graph the minimum number of edges to divide it in the requested number of dense communities
 * All searches take a second boolean parameter that instructs if debug messages should be printed
 * Searches visualize the number of communities discovered, their relative number of nodes, edges and densities
 * In the case of seachComponentsSet the search return also the number of iteration required to achieve the requested number of communities
 * This class assumes the graph to be un-directed because it focuses on a facebook's friend graph data set. 
 **
 */

public class denseComunities {

	// stores the graph investigated by this class
	private CapGraph graph;
	// stores a list with the time required to run each iteration of the algorithm
	private LinkedList<Long> debugTimes= new LinkedList<Long>();
	// records the flow units accumulated per edge in the graph to facilitate the selection of the most used one that will be removed
	private Flows flowsAccounting = new Flows();

	public denseComunities(String DataSet) {		
		// The class constructor load data set in the class's graph and select the connected component with more edges
		// to search for communities if the graph is not fully connected
		// Input value: a string with the path and name of the file with the graph definition
		
		// temporary graph used to search the largest connected component subgraph 
		CapGraph tempGraph= new CapGraph();
		// loadGraph utility load a text file when each raw represent an edge from source node to destination node as two integers
		GraphLoader.loadGraph(tempGraph, DataSet);

		// retrieve a list of the connected components subgraph in the initial graph
		LinkedList<CapGraph> sccList=tempGraph.getCCs();
		int higerEdges=0;

		// iterate the list of connected subgraph and select the one with more edges to perform the min cut search on 
		for (CapGraph g: sccList) {
			if (g.GetEdgesNumber()>higerEdges) {
				higerEdges=g.GetEdgesNumber();
				graph=g;
			}
		}
		
		// visualize information about the subgraph selected for the search
		System.out.println("Data set proposed has "+sccList.size()+" connected components");
		System.out.println("Selected componet with larger number of vertexes");
//		System.out.println("  Number of Vertexex: "+graph.GetVertexesNumber());
//		System.out.println("  Number of Edges: "+graph.GetEdgesNumber()/2);			
	}
		
	
	private HashMap<Integer,Integer> BFS(Integer node) {
		// Given a source node in the graph, BFS returns the breath search first tree from each node discovered back to the source node
		// Input value: the source node to calculate the breath search tree from 
		// Return value: a hashmap that stores as key a node discovered and as value the node toward the source via the discovered BFS 
		
		// store a set with the node visited during the BFS exploration
		HashSet<Integer> visitedNodes=new HashSet<Integer>();
		// store the BFS tree to return (see explanation )
		HashMap<Integer,Integer> BFSTreeUpstream=new HashMap<Integer,Integer>();
		// queue with the list of nodes to explore in the BFS exploration
		Queue<Integer> nodesToVisit = new LinkedList<Integer>();

		visitedNodes.add(node);		// add source node as visited node
		nodesToVisit.add(node);		// add source node to the end of queue
		
		// iterate the queue of nodes to visit while it is not empty
		while(!nodesToVisit.isEmpty()) {
			Integer nodeFrom=nodesToVisit.remove(); // remove the head of the queue
			
			// process each neighbors of nodeFrom
			for (Integer nodeTo:graph.getNeighbour(nodeFrom)) {
				
				// if the node under investigation hasn't being visited before
				if(!visitedNodes.contains(nodeTo)) {
					// record the upstream node toward the source node
					BFSTreeUpstream.put(nodeTo, nodeFrom);
					// mark the node as visited
					visitedNodes.add(nodeTo);
					// add the node under investigation to queue to visit its neighbors when processing a deeper level in the tree 
					nodesToVisit.add(nodeTo);
				}
			}				
		}
		return BFSTreeUpstream;
	}
	
	public void seachComponentsIterations(int iterations, boolean printDebug){
		// Perform the requested number of iterations on the graph, removing each time one undirected edge
		// from the investigated graph, aiming to increase the number of connected components and their density 
		// Input value: an integer number of edges to remove from the graph (one iteration = one edge removed)
		// Input value: a boolean value to print debug information when true
		// Side effect: change the graph store in the private variable "graph"

		// Hash that stores the BFS tree from each node discovered versus a specific source node 		
		HashMap<Integer,Integer> BFSTreeUpstream=new HashMap<Integer,Integer>();
		// reset the list of debug timers
		debugTimes.clear();
		
		for (int i=0;i<iterations;i++) {
			// reset the flowsAccounting helper class that store the flow units allocated to each edge
			flowsAccounting.resetFlows();

			long startTime = System.currentTimeMillis();

			// repeat the process for each node in the graph	
			for (Integer node:graph.getVertices()) {
				// reset the data structure storing the BFS discovered in the previous iteration
				BFSTreeUpstream.clear();
				// Retrieve a Breath Search First tree for the node under investigation
				BFSTreeUpstream=BFS(node);
				// allocate flow units on each edge discovered in the BFS tree
				flowsAccounting.allocateFlows(node, BFSTreeUpstream);
			}
			// represent a edge in the graph as couple of two integers
			Point edgeToRemove=new Point();
			// retrieve from the helper class flowsAccounting the edge that has accumulated more flow units
			edgeToRemove=flowsAccounting.moreFlow();
			
			// Exit if no edge to remove was identified
			if (edgeToRemove==null) return;

			// remove from the graph the edge with more flow units - both directed links because an undirected graph
			graph.removeEdge(edgeToRemove.x,edgeToRemove.y);
			graph.removeEdge(edgeToRemove.y,edgeToRemove.x);

			// print information about the edge removed if debugging is set to true
			if (printDebug) {
				System.out.println("Iteration: "+(i+1));
				System.out.println("Edge to remove: "+flowsAccounting.moreFlow());
				this.printComponents();			
			}

			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			// store the running time of this search in the debugTimes list
			debugTimes.add(totalTime);
		}

		if (printDebug) {
			printTimes();
		}
		
	}
	
	public void seachComponentsSet(int minNumberComponents, boolean printDebug){
		// Removes from the graph under investigation the minimum number of edges to divide it in the requested number of dense communities
		// Input value: an integer number of dense communities to discover
		// Input value: a boolean value to print debug information when true
		// Side effect: change the graph store in the private variable "graph"
		
		
		// minNumberComponents must be >0 and max the number of vertexes
		if (minNumberComponents<1 || minNumberComponents>graph.GetVertexesNumber()) {
			System.out.println("The minumum number of comunities must be higher than zero and lower than total number of nodes");
			return;
		}		
		int iteration=0;
		// call seachComponentsIterations as many times required to divide the graph in the requested number of dense communities
		while (graph.getCCs().size()<minNumberComponents) {
			seachComponentsIterations(1,printDebug);
			iteration++;
		}
		
		System.out.println("Total number of iteration required: "+iteration);
	}
	
	
	private void printComponents(){
		// Print information about connected communities - number of nodes, edges and density of the community
		
		LinkedList<Integer> nodes= new LinkedList<Integer>();
		LinkedList<Integer> edges= new LinkedList<Integer>();

		// list of connected communities as subgraph
		LinkedList<CapGraph> ccList=new LinkedList<CapGraph>();	
		// list of density for each subgraph
		LinkedList<Double> densities= new LinkedList<Double>();

		// retrieve from the current graph the connected communities and store them in a list of subgrapgh 
		ccList=graph.getCCs();
		
		System.out.println(" ---------------------------------------------- ");
		System.out.println(" Number of connected components: "+ccList.size());
		
		// for each subgraph print the number of vertexes, edges and density as actual number of edged divided number of edged in a full mesh
		for (CapGraph g: ccList) {
			nodes.add(g.GetVertexesNumber());
			if (g.GetVertexesNumber()==0) System.out.println(g.toString());
			
			edges.add(g.GetEdgesNumber()/2);
			if (g.GetVertexesNumber()>1) {
				densities.add(g.GetEdgesNumber()/(double)(g.GetVertexesNumber()*(g.GetVertexesNumber()-1)));					
			} else {
				densities.add(1.0);
			}
		}
		System.out.println("   Node Numbers "+nodes);			
		System.out.println("   Edge Numbers "+edges);
		System.out.println("   Densities "+densities);
		System.out.println(" ---------------------------------------------- ");		
	}
		
	private void printTimes(){
		// helper function that calculate the minimum, maximum and average time for each iteration of the algorithm
		
		Iterator debugTimesIterator = debugTimes.iterator();
		int iterations=0;
		long minTime=debugTimes.peek();
		long maxTime=minTime;
		long sumTime=0;
		long time;
		
		while(debugTimesIterator.hasNext()) {
			iterations++;
			time=(long) debugTimesIterator.next();
			sumTime+=time;
			minTime=Math.min(minTime, time);
			minTime=Math.max(maxTime, time);
		}
		
		System.out.println("Min/Avg/Max Runnig time :" +minTime+"/"+sumTime/iterations+"/"+maxTime);
	}
	
	public static void main(String[] args) {

		// true to visualize debug information in community searches
		boolean printDebug=false;
		
		// select one of the data set to test against
		 String dataSet="data/facebook_1000.txt";
		//		String dataSet="data/facebook_2000.txt";
		//		String dataSet="data/facebook_ucsd.txt";
		//		String dataSet="data/marco1.txt";
		
		System.out.println("Starting elaborating connected comunities in the proposed data set");		
		
		denseComunities test=new denseComunities(dataSet);
		// print the number of the communities in the initial graph
		test.printComponents();

		// choose the search between iteration and minimum communities to identify
		//test.seachComponentsIterations(10, printDebug);			
		test.seachComponentsSet(3,printDebug);
	
		System.out.println(" ---------------------------------------------- ");		
		System.out.println("	!!! Completed  !!!");	
		System.out.println(" ---------------------------------------------- ");			
		
		// print the number of the communities in the final graph after removing the edges
		test.printComponents();
			
	}	
}

