package graph;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import util.GraphLoader;


/**
* @author Marco Umer marco.umer@gmail.com
* The MinCut class supports the search in a graph of the minimum number of edges (minimum cut) that if removed 
* separate the graph in two connected components
* The algorithm used to find the minimum number of edges to disconnect, runs multiple iterations of the randomised Karger’s algorithm 
* to identify a possible cut and it then chooses the minimum recorded cut number.
* The Karger’s removes edges at random and merge its two vertexes in a single node till a single couple of nodes are left in the graph.
* The number of links between these two nodes is a potential cut and it is the minimum cut only if no edge from a minimum cut set was
* removed in the process.
*
* MinCut class uses the class CapGraph.java to represent the graph under investigation and leverage its method "getCCs" to
* divide the graph in connected components (or communities in this exercise). 
* This class assumes the graph to be un-directed because it focuses on a facebook's friend graph data set. 
**/

public class MinCut {

	// stores the graph investigated by this class
	private CapGraph graph= new CapGraph();
	// stores a list with the time required to run each iteration of the algorithm
	private LinkedList<Long> debugTimes= new LinkedList<Long>();
	// store a list with the number of edges in the minimum cuts discovered
	private LinkedList<Integer> minCutList= new LinkedList<Integer>();

	
	public MinCut(String DataSet) {
		// The class constructor load data set in the class's graph and select the connected component with more edges
		// to perform the min cut search if the graph is not fully connected
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
		System.out.println("  Number of Vertexex: "+graph.GetVertexesNumber());
		System.out.println("  Number of Edges: "+graph.GetEdgesNumber()/2);			
	}
	

	private Integer Kager(CapGraph workingGraph){
		// The Kager's method retrieves the number of edges in a set that if removed, divide the graph in two disjointed components

		// Input value: a CapGraph graph to search the minimum cut on
		// Output value: the number o edges in the discovered cut
		// Side effect: collapses the workingGraph in two nodes with the edges between them representing the discovered cut
				
		// The Karger’s algorithm can be summarized as:
		//		While there are more than two vertexes in the graph
		//			Pick a random edge in the graph and merge its two vertexes in a single (contracting the graph)
		//			Update all references to the original nodes to the single collapsed node
		//			Remove self loop created by merging two nodes
		//		Return the links between the last two vertexes as representation of the discovered cut
		
		Integer fromNode;
		Integer toNode;
		
		// iterate collapsing nodes in the graph until only two nodes are left
		while (workingGraph.GetVertexesNumber()>2) {			
			// retrieve a random node in the graph
			fromNode=workingGraph.getOneVertice();
			// retrieve a random edge from the random node
			toNode=workingGraph.getOneNeighbor(fromNode);
			// merge in the identified node and its neighbor
			workingGraph.mergeVertexes(fromNode, toNode);	
		}
		// return the number of edges between the two last nodes - divided by 2 because working with undirected graph
		return (workingGraph.GetEdgesNumber()/2);
	}
	
	public int findMinCut(int iterations){
		int cutToReturn=0;
		long startTime, endTime, sumTime, time, minTime, maxTime;
				
		for (int n=0;n<iterations;n++) {
			// create a temporary copy of the class's CapGrapgh, keeping the original for new iterations
			// Required because the Kager's method updates the graph under investigation			
			CapGraph workingGraph=graph.clone(); 			

			startTime = System.currentTimeMillis();
			
			// apply the Kager on the temporary working graph and store the returned number of edges in a list for later analysis
			minCutList.add(Kager(workingGraph));
			endTime   = System.currentTimeMillis();

			// record the time used to run Kager in a list for later analysis
			debugTimes.add(endTime - startTime);						
		}

		// visualize the number of cut and running time for each iteration
		System.out.println("   Cuts recorded: "+minCutList);
		System.out.println("   Running time recorded (msec): "+debugTimes);
		
		
		minTime=debugTimes.peek();
		maxTime=minTime;
		sumTime=0;
		
		// calculate the minmum, maximum and average time require to implement the iterations
		Iterator TimeIterator = debugTimes.iterator();		
		while(TimeIterator.hasNext()) {	
			time=(long) TimeIterator.next();
			sumTime+=time;
			minTime=Math.min(time, minTime);
			maxTime=Math.max(time, maxTime);

		}
		
		System.out.println(" ---------------------- ");
		System.out.println("   Min/Avg/Max Runnig time (msec):" +minTime+"/"+sumTime/iterations+"/"+maxTime);
		System.out.println(" ---------------------- ");

		// Calculate the minimum number of edges recorded in the discovered graph's cut set
		cutToReturn=minCutList.peek();
		Iterator minCutIterator = minCutList.iterator();		
		while(minCutIterator.hasNext()) {	
			cutToReturn=Math.min(cutToReturn,(int)minCutIterator.next());
		}		
		// return the minimum cut discovered
		return cutToReturn;	
	}
	
	
	public static void main(String[] args) {

		// select the number of iterations
		// To assure a 0.01 error, must repeat 20*n*n times which is not feasible with the current running times for the data set selected
		// Tested using iterations of 100 times for 1000 and 2000 data sets 

		int iteration=100;
		
		String dataSet="data/facebook_1000.txt";
//		String dataSet="data/facebook_2000.txt";
//		String dataSet="data/facebook_ucsd.txt";
//		String dataSet="data/marco1.txt";
		
		// create an instance of MinCut with the selected data set
		MinCut minCutTest = new MinCut(dataSet);

		System.out.println(" ---------------------- ");
		System.out.println("Starting Min-cut execution");				
		System.out.println(" ---------------------- ");
		// perform the search of the minimum cut for the number of iteration specified and visualize the
		// minimum number of edges in the cut series
		System.out.println("Answer to minum cut question for "+ dataSet +" largest connected component is :"+minCutTest.findMinCut(iteration));
	}
}

