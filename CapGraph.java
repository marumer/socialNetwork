/**
 * 
 */
package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Random;


/**
 * @author Marco Umer marco.umer@gmail.com
 * 
 * The CapGraph class implement the Graph's interface providing support of a graph data structure and method to manipulate it
 * 
 * *
 */
public class CapGraph implements Graph {
	
	// The class's graph is stored in term of a source node and a list of its neighbours 
	private HashMap<Integer, ArrayList<Integer>> adjGraph = new HashMap<Integer, ArrayList<Integer>>();	

	/* (non-Javadoc)
	 * @see graph.Graph#addVertex(int)
	 * 
	 * Add the node represented as integer to the graph   
	 */
	@Override
	public void addVertex(int node) {
		adjGraph.put(node, new ArrayList<Integer>());		
	}

	/* (non-Javadoc)
	 * @see graph.Graph#addEdge(int, int)
	 * 
	 * Add an edge between from fromNode to toNode to the class's graph
	 */
	@Override
	public void addEdge(int fromNode, int toNode) {
		adjGraph.get(fromNode).add(toNode);

	}

	/*
	 * Remove the edge between from fromNode to toNode from the class's graph
	 */

	public void removeEdge(int fromNode, int toNode) {
		if (adjGraph.containsKey(fromNode)) {
			adjGraph.get(fromNode).remove((Integer)toNode);
		}
		
	}

	public String toString() {
		
		for (Integer v:adjGraph.keySet()) {
			System.out.println(v + " -> " + adjGraph.get(v));
		}
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see graph.Graph#getEgonet(int)
	 */
	@Override
	public Graph getEgonet(int center) {
		// return new graph with center, directly connected vertices
		
		CapGraph egoGraph = new CapGraph();
		
		if (!adjGraph.containsKey(center)) {
			// return null graph is center doesn't belong to graph
			return egoGraph;
		}

		egoGraph.addVertex(center);

		// checkSet simplify checking if a vertex is part of the set we are considering
		//HashMap<Integer, Integer> checkSet = new HashMap<Integer, Integer>();
		HashSet<Integer> checkSet = new HashSet<Integer>();

		checkSet.add(center);
		for (Integer n:adjGraph.get(center)) {
			checkSet.add(n);
		}		
		
		for (Integer v_i:adjGraph.get(center)) {

			// add edges between center and directly connected vertexes
			egoGraph.addEdge(center, v_i); 
			// add directly connected vertexes
			egoGraph.addVertex(v_i);
			
			// and edges between them and the center (part of checkSet)
			
			for (Integer n:adjGraph.get(v_i)) {
				if (checkSet.contains(n)) {
					egoGraph.addEdge(v_i, n);
				}
			}
		}
		
		return egoGraph;
	}

	/*
	 * Return the transposed of the class's graph inverting the direction of each directed link in the original graph
	 */
	
	public CapGraph transposeGraph () {
		CapGraph adjGraphT = new CapGraph();
		
		for (Integer v:adjGraph.keySet()) {
			adjGraphT.addVertex(v);
		}
		
		for (Integer From:adjGraph.keySet()) {
			for (Integer To: adjGraph.get(From)) {
				adjGraphT.addEdge(To, From);
			}
		}
		return adjGraphT;
	}
	
	public  LinkedList<Integer> BFS(CapGraph workingGraph, LinkedList<Integer> vertices){
		// initialize set visited and Stack finished
		HashSet<Integer> visited=new HashSet<Integer>(); 
		LinkedList<Integer> finished = new LinkedList<Integer>();
		
		while (!vertices.isEmpty()) {
			Integer v = vertices.pop();
			if (!visited.contains(v)) {
				BFSVisit(workingGraph, v, visited, finished);
			}
		}
		return finished;
	}

	/*
	 * Return the list of neighbor of node
	 */
	public ArrayList<Integer> getNeighbour(Integer node){
		return adjGraph.get(node);
	}

	/*
	 * Return the list of vertexes in the graph
	 */	
	public Set<Integer> getVertices(){
		return adjGraph.keySet();
	}

	/*
	 * Return one vertex at random from the class's graph
	 */	
	public Integer getOneVertice(){
		Random randomGenerator = new Random();
		Object[] nodes = adjGraph.keySet().toArray();
		return (Integer)nodes[randomGenerator.nextInt(nodes.length)];		
	}

	/*
	 * Return one neighbor at random for the node in the class's graph
	 */	

	public Integer getOneNeighbor(Integer node){
		Random randomGenerator = new Random();

		Object[] neighbours =adjGraph.get(node).toArray();
		//System.out.println("Neigh lenght "+ neighbours.length);
		
		return (Integer)neighbours[randomGenerator.nextInt(neighbours.length)];		
	}

	/*
	 * Compress the nodeFrom and a nodeTo in a single node represented by nodeFrom 
	 */	
	
	public void mergeVertexes(Integer fromNode, Integer toNode){

		// remove from fromNode's neighbor list any reference to toNode - this delete edges between these two nodes
		for (Iterator<Integer> iterator = adjGraph.get(fromNode).iterator(); iterator.hasNext();){
				Integer i = iterator.next();
				if(i.equals(toNode)) {
					iterator.remove();
				}			
			}
		
		// remove from toNode's neighbor list any reference to fromNode - this delete edges between these two nodes		
		for (Iterator<Integer> iterator = adjGraph.get(toNode).iterator(); iterator.hasNext();){
				Integer i = iterator.next();
				if(i.equals(fromNode)) {
					iterator.remove();
				}				
			}
		
		// add all neighbors from toNode to fromNode
		adjGraph.get(fromNode).addAll(adjGraph.get(toNode));
		// remove toNode form the class' graph
		adjGraph.remove(toNode);
		
		// update throughout the graph any reference to toNode replacing them with fromNode
		for (Integer node:adjGraph.keySet()) {
			ArrayList<Integer> toAdd=new ArrayList<Integer>();
			for (Iterator<Integer> iterator = adjGraph.get(node).iterator(); iterator.hasNext();){
				Integer i = iterator.next();
				if(i.equals(toNode)) {
					iterator.remove();
					toAdd.add(fromNode);
				}
			}
			adjGraph.get(node).addAll(toAdd);
		}	
	}

	/*
	 * Return the total number of vertexes in the class's graph
	 */	
	public int GetVertexesNumber() {
		if (adjGraph.isEmpty()) return 1;
		return(adjGraph.keySet().size());
	}

	/*
	 * Return the total number of edges in the class's graph
	 */	
	public int GetEdgesNumber() {
		int toReturn=0;
		
		for (Integer v:adjGraph.keySet()) {
			toReturn+=adjGraph.get(v).size();
		}
		
		return(toReturn);
	}
	
	/*
	 * Return the total number of vertexes in the class's graph
	 */	
	
	public  void BFSVisit(CapGraph workingGraph, Integer v, HashSet<Integer> visited, LinkedList<Integer> finished){
		visited.add(v);
		for (Integer n:workingGraph.getNeighbour(v)) {
			if (!visited.contains(n)) {
				BFSVisit(workingGraph, n, visited, finished);
			}
		}
		
		finished.push(v);
	}
	
	/*
	 * Return a copy of the class's graph to modify without impact the original graph
	 */	
	
	public CapGraph clone() {
		
		CapGraph copyGraph=new CapGraph();

		// iterate all nodes in the class's grapgh
		for(Integer node:adjGraph.keySet()) {
			// add node to the copy
			copyGraph.addVertex(node);				
			ArrayList<Integer> toNodes= adjGraph.get(node);
			// iterate all neighbors and add them to the copy
			for (Integer toNode:toNodes) {
				copyGraph.addEdge(node, toNode);;
			}			
		}
		return copyGraph;	
	}

	
	/* (non-Javadoc)
	 * @see graph.Graph#getSCCs()
	 */
	@Override
	public List<Graph> getSCCs() {		

		ArrayList<Graph> toReturn = new ArrayList<Graph>();
		
		// Store a copy of the class'graph to manipulate
		CapGraph workingGraph= new CapGraph();
		// Store a transposed representation of the working graph
		CapGraph workingGraphT= new CapGraph();

		workingGraph.adjGraph=adjGraph;
		workingGraphT=workingGraph.transposeGraph();
		
		// retrieve the list of all vertexes in the class's graph
		LinkedList<Integer> vertices = new LinkedList<>();
		vertices.addAll(workingGraph.getVertices());

		// call the BFS breath first search method to retrieve the all nodes in a graph component
		LinkedList<Integer> finished=workingGraph.BFS(workingGraph, vertices);
		
		HashSet<Integer> visited=new HashSet<Integer>(); // new empty visited list
		
		while (!finished.isEmpty()) {
			Integer v = finished.pop();
			if (!visited.contains(v)) {
				
				LinkedList<Integer> finishedT = new LinkedList<Integer>();
				BFSVisit(workingGraphT, v, visited, finishedT);
				
				// Populate a new Graph with the nodes discovered in the BFS
				CapGraph newGraph = new CapGraph();
				for (Integer n:finishedT) {
					newGraph.addVertex(n);
				}
				// Populate the newly created Graph with the edges between nodes discovered
				// this wasn't required in week1 assignment but they speak about subgraph

				for (Integer from:newGraph.getVertices())
				{
					for (Integer to:workingGraph.getNeighbour(from)) {
						if (finishedT.contains(to)) {
							newGraph.addEdge(from, to);
						}
					}
				}
				
				toReturn.add(newGraph);

			}
			
		}
		
		
		return toReturn;
	}

	/*
	 * Return a list of subgraphs connected components in the class's graph
	 * This method is similar to getSCC but it doesn't implement recursion computation because of risk stackoverflow when dealing with 
	 * really huge graph
	 */	

	public LinkedList<CapGraph> getCCs() {		
		
		LinkedList<CapGraph> GraphsToReturn = new LinkedList<CapGraph>();
		
		//Vertexes visited in the search
		HashSet<Integer> visited=new HashSet<Integer>();

		// Create a list to iterate on the vertexes	
		LinkedList<Integer> vertexes = new LinkedList<>();
		vertexes.addAll(adjGraph.keySet());

		// explore all vertexes with a BFS search and create a new graph to return when ending exploring a graph's isolated component
		while (!vertexes.isEmpty()) {
			Integer node = vertexes.pop();

			// if the node hasn't being visited before
			if (!visited.contains(node)) {
				// add to the list to explore
				LinkedList<Integer> NodeToExplore=new LinkedList<Integer>();
				// add to the queue to explore all neighbours of node
				NodeToExplore.addAll(adjGraph.get(node));

				// Store a list of nodes found in the BFS exploration 
				LinkedList<Integer> BfsTree = new LinkedList<Integer>();	
			
				while(!NodeToExplore.isEmpty()) {
					Integer exploringNode=NodeToExplore.pop();
					if (!visited.contains(exploringNode)) {
						visited.add(exploringNode);
						NodeToExplore.addAll(adjGraph.get(exploringNode));
						BfsTree.push(exploringNode);						
					}					
				}					
								
				// Populate a new Graph with the nodes discovered in the BFS
				CapGraph newGraph = new CapGraph();
				for (Integer n:BfsTree) {
					newGraph.addVertex(n);
				}
				GraphsToReturn.add(newGraph);
				
				// Populate the newly created Graph with the edges between nodes discovered
				HashSet<Integer> GraphNodes= new HashSet<Integer>();
				GraphNodes.addAll(newGraph.getVertices());

				for (Integer from:newGraph.getVertices())
				{	
					for (Integer to:adjGraph.get(from)) {
						if (GraphNodes.contains(to)) {
							newGraph.addEdge(from, to);
						}
					}			
				}
			}
		}
		
		return GraphsToReturn;
	}
	
	
	/* (non-Javadoc)
	 * @see graph.Graph#exportGraph()
	 */
	@Override
	public HashMap<Integer, HashSet<Integer>> exportGraph() {
	
		HashMap<Integer, HashSet<Integer>> toReturn = new HashMap<Integer, HashSet<Integer>>();
		
		for (Integer n:adjGraph.keySet()) {
						
			toReturn.put(n, new HashSet<Integer>());
			toReturn.get(n).addAll(adjGraph.get(n));
		}
		
		return toReturn;
	}

}
