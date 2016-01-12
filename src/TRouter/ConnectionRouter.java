package TRouter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;

import architecture.Architecture;
import architecture.RouteNode;
import architecture.RouteNodeType;
import circuit.Circuit;
import circuit.Connection;




public class ConnectionRouter {
	final Architecture a;
	final Circuit c;
	
	private double pres_fac;
	private double alpha;
	private final PriorityQueue<QueueElement> queue;
	private final Collection<RouteNodeData> nodesTouched;
	
	final static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	public ConnectionRouter(Architecture a, Circuit c) {
		this.a = a;
		this.c = c;
		this.alpha = 1;
		this.nodesTouched = new ArrayList<RouteNodeData>();
		this.queue = new PriorityQueue<QueueElement>();
	}
    
    public int crouteHwithConSmallestBBFirstOrder(int nrOfTrials) {
    	nodesTouched.clear();
    	queue.clear();
		allocateRouteNodeData();
	    double initial_pres_fac = 0.5;
		double pres_fac_mult = 2;
		double acc_fac = 1;
		pres_fac = initial_pres_fac;
		int itry=1;
		
		//Deleting connections with non-connecting sinks 5 
		Set<Connection> setConsCopy = new HashSet<Connection>(c.cons);
		for (Connection con: setConsCopy) {
			if(con.source.owner.site==null){
				c.cons.remove(con);
				System.out.println("Warning: Owner of the source pin does not have a site. Removing "+con+" ...");
			}
			if(con.sink.owner.site==null){
				c.cons.remove(con);
				System.out.println("Warning: Owner of the sink pin does not have a site. Removing "+con+" ...");
			}
		}
		
		HashMap<Connection,Integer> mapOfConnections = new HashMap<Connection,Integer>();
		for(Connection con:c.cons){
			int bb = Math.abs(con.source.owner.site.x-con.sink.owner.site.x) + Math.abs(con.source.owner.site.y-con.sink.owner.site.y);
			mapOfConnections.put(con, bb);
		}

		c.createBundles();
		BBComparator bvc =  new BBComparator(mapOfConnections,c.sourceBundles,c.sinkBundles);
        TreeMap<Connection,Integer> sorted_mapOfConnections = new TreeMap<Connection, Integer>(bvc);
        sorted_mapOfConnections.putAll(mapOfConnections);
		
		while (itry <= nrOfTrials) {
			Calendar cal = Calendar.getInstance();
			System.out.print(dateFormat.format(cal.getTime()) + " ");
			System.out.print(itry + "..");
	        for(Connection con:sorted_mapOfConnections.keySet()){
				ripup(con);
				route(con);
				add(con);
			}

			//Check if the routing is realizable, if realizable return, the routing succeeded 
			if (routingIsFeasible()){
				c.conRouted = true;
				System.out.println("Routing succeeded in "+itry+" trials.");
				return itry;
			}
			
			//Print out overuse
			int nrOfOverused = 0;
			int totalOveruse = 0;
			HashSet<RouteNode> overUsedRNs = new HashSet<RouteNode>();
			for (RouteNode node: a.getRouteNodes()) {
				RouteNodeData data=getRouteNodeData(node);
				if (node.capacity < data.occupation) {
					overUsedRNs.add(node);
					nrOfOverused++;
					totalOveruse += (data.occupation-node.capacity);
					
				}
			}
			int nrOfIPINs = 0;
			for(RouteNode node:overUsedRNs){
				if(node.type == RouteNodeType.IPIN) nrOfIPINs++;
			}
			System.out.println("Overuse:"+nrOfOverused+" RNs ("+nrOfIPINs+" IPINs) of the "+c.totalRouteNodesCON()+" routed RNs ("+c.totalWiresCon()+" wires), total overuse = "+totalOveruse);

			itry++;
			
			//Updating the cost factors for next iteration
			if (itry == 1)
				pres_fac = initial_pres_fac;
			else
				pres_fac *= pres_fac_mult;
			
			updateCost(pres_fac, acc_fac);
			
		}
		if (itry==nrOfTrials+1){
			System.out.println("Routing failled after "+itry+" trials!");
			for (RouteNode node: a.getRouteNodes()) {
				RouteNodeData data=getRouteNodeData(node);
				if (node.capacity < data.occupation) {
					System.out.println(node+", basecost = "+node.baseCost+", capacity = "+node.capacity+", occupation = "+data.occupation+", type = "+node.type);
					//if(data.occupation>2)for(Connection con:data.cons)System.out.println("\tsink = "+con.sink+", source"+con.source+", equivSink = "+con.equivSink+", sink te bereiken = "+con.sink.con.sink.owner.site.sink);
				}
			}
		}
		freeRouteNodeData();
		return -1;
	}
    
    public int route(int nrOfTrials, int minimumNrOfIterations) {
    	nodesTouched.clear();
    	queue.clear();
		allocateRouteNodeData();
	    double initial_pres_fac = 0.5;
		double pres_fac_mult = 2;
		double acc_fac = 1;
		pres_fac = 0.5;
		int itry=1;
		
		//Deleting connections with non-connecting sinks 5 
		Set<Connection> setConsCopy = new HashSet<Connection>(c.cons);
		for (Connection con: setConsCopy) {
			if(con.source.owner.site==null){
				c.cons.remove(con);
				System.out.println("Warning: Owner of the source pin does not have a site. Removing "+con+" ...");
			}
			if(con.sink.owner.site==null){
				c.cons.remove(con);
				System.out.println("Warning: Owner of the sink pin does not have a site. Removing "+con+" ...");
			}
		}
		
		HashMap<Connection,Integer> mapOfConnections = new HashMap<Connection,Integer>();
		for(Connection con:c.cons){
			int bb = Math.abs(con.source.owner.site.x-con.sink.owner.site.x) + Math.abs(con.source.owner.site.y-con.sink.owner.site.y);
			mapOfConnections.put(con, bb);
		}

		c.createBundles();
		BBComparator bvc =  new BBComparator(mapOfConnections,c.sourceBundles,c.sinkBundles);
        TreeMap<Connection,Integer> sorted_mapOfConnections = new TreeMap<Connection, Integer>(bvc);
        sorted_mapOfConnections.putAll(mapOfConnections);
		
		while (itry <= nrOfTrials) {
	        for(Connection con:sorted_mapOfConnections.keySet()){
				ripup(con);
				route(con);
				add(con);
			}

			//Check if the routing is realizable, if realizable return, the routing succeeded 
			if (itry>minimumNrOfIterations && routingIsFeasible()){
				c.conRouted = true;
				return itry;
			}
			
			//Print out overuse
			int nrOfOverused = 0;
			int totalOveruse = 0;
			HashSet<RouteNode> overUsedRNs = new HashSet<RouteNode>();
			for (RouteNode node: a.getRouteNodes()) {
				RouteNodeData data=getRouteNodeData(node);
				if (node.capacity < data.occupation) {
					overUsedRNs.add(node);
					nrOfOverused++;
					totalOveruse += (data.occupation-node.capacity);
					
				}
			}
			int nrOfIPINs = 0;
			for(RouteNode node:overUsedRNs){
				if(node.type == RouteNodeType.IPIN) nrOfIPINs++;
			}		
			//Updating the cost factors
			if (itry == 1)
				pres_fac = initial_pres_fac;
			else
				pres_fac *= pres_fac_mult;
			
			updateCost(pres_fac, acc_fac);
			
			itry++;
		}
		if (itry==nrOfTrials+1){
			System.out.println("Routing failled after "+itry+" trials!");
			for (RouteNode node: a.getRouteNodes()) {
				RouteNodeData data=getRouteNodeData(node);
				if (node.capacity < data.occupation) {
					System.out.println(node+", basecost = "+node.baseCost+", capacity = "+node.capacity+", occupation = "+data.occupation+", type = "+node.type);
					//if(data.occupation>2)for(Connection con:data.cons)System.out.println("\tsink = "+con.sink+", source"+con.source+", equivSink = "+con.equivSink+", sink te bereiken = "+con.sink.con.sink.owner.site.sink);
				}
			}
		}
		freeRouteNodeData();
		return -1;
	}
	
	private void allocateRouteNodeData() {
		for (RouteNode node : a.getRouteNodes()) {
			node.routeNodeData = new RouteNodeData(
					node.type == RouteNodeType.IPIN
							|| node.type == RouteNodeType.SINK);
		}
	}
	
	private void freeRouteNodeData() {
		for(RouteNode node : a.getRouteNodes())
			node.routeNodeData = null;
	}

	private boolean routingIsFeasible() {
		for (RouteNode node : a.getRouteNodes()) {
			RouteNodeData data = getRouteNodeData(node);
			if (node.capacity < data.occupation) {
				return false;
			}
		}
		return true;
	}

	private void add(Connection con) {
		for (RouteNode node : con.routeNodes) {
			RouteNodeData data = getRouteNodeData(node);
			int occ;
			if (node.type == RouteNodeType.IPIN
					|| node.type == RouteNodeType.SINK) {
				data.addSink(con.sink);
				occ = data.numUniqueSinks();
			} else {
				data.addEqSink(con.equivSink);
				data.addSource(con.source);

				// Calculation of occupation
				occ = Math.min(data.numUniqueEqSinks(), data.numUniqueSources());
			}
			data.occupation = occ;

			// Calculation of present cost
			int cap = node.capacity;
			if (occ < cap) {
				data.pres_cost = 1.;
			} else {
				data.pres_cost = 1. + (occ + 1 - cap) * pres_fac;
			}
		}
	}

	private void ripup(Connection con) {
		for (RouteNode node : con.routeNodes) {
			RouteNodeData data = getRouteNodeData(node);
			int occ;
			if (node.type == RouteNodeType.IPIN
					|| node.type == RouteNodeType.SINK) {
				data.removeSink(con.sink);
				occ = data.numUniqueSinks();
			} else {
				data.removeEqSink(con.equivSink);
				data.removeSource(con.source);

				// Heuristic calculation of the occupation
				occ = Math.min(data.numUniqueEqSinks(), data.numUniqueSources());
			}
			// Calculation of present cost
			int cap = node.capacity;
			if (occ < cap) {
				data.pres_cost = 1.;
			} else {
				data.pres_cost = 1. + (occ + 1 - cap) * pres_fac;
			}
			data.occupation = occ;
		}
	}
		
	private boolean route(Connection con) {
		// Clear Routing
		con.routeNodes.clear();
		// Clear Queue
		queue.clear();
		// Set target flag sink
		RouteNode sink = con.getSinkRouteNode();
		sink.target = true;
		// Add source to queue
		RouteNode source = con.getSourceRouteNode();
		double source_cost = getRouteNodeCost(source, con);
		addNodeToQueue(source, null, source_cost,
				getLowerBoundTotalPathCost(source, con, source_cost));
		// Start Dijkstra / directed search
		while (!targetReached()) {
			expandFirstNode(con);
		}
		// Reset target flag sink
		sink.target = false;
		// Save routing in connection class
		saveRouting(con);
		// Check lower bounds
//		sanityCheckRoute(con);
		// Reset path cost from Dijkstra Algorithm
		resetPathCost();

		return true;
	}
		
	private void saveRouting(Connection con) {
		QueueElement qe = queue.peek();
		while (qe != null) {
			con.routeNodes.add(qe.node);
			qe = qe.prev;
		}
	}

	private boolean targetReached() {
		if(queue.peek()==null){
			System.out.println("queue is leeg");			
			return false;
		} else {
			return queue.peek().node.target;
		}
	}
	
	private void sanityCheckRoute(Connection con) {
		if(con.routeNodes.isEmpty())
			throw new RuntimeException();
//		double realTotalCost = getRouteNodeData(con.getSinkRouteNode()).getPartialPathCost();
//		if(realTotalCost == Double.MAX_VALUE)
//			throw new RuntimeException();
//		for(RouteNode rn : con.routeNodes)
//			if(getRouteNodeData(rn).getLowerBoundTotalPathCost() > realTotalCost)
//				throw new RuntimeException();
	}

	private void addNodeToQueue(RouteNode node, QueueElement prev, double new_partial_path_cost, double new_lower_bound_total_path_cost) {
		RouteNodeData nodeData = getRouteNodeData(node);
		if(!nodeData.pathCostsSet())
			nodesTouched.add(nodeData);
		nodeData.updatePartialPathCost(new_partial_path_cost);
		if (nodeData.updateLowerBoundTotalPathCost(new_lower_bound_total_path_cost)) {	//queue is sorted by lower bound total cost
			queue.add(new QueueElement(node, prev));
		}
	}
	
	private void resetPathCost() {
		for (RouteNodeData node : nodesTouched) {
			node.resetPathCosts();
		}
		nodesTouched.clear();
	}

	private void expandFirstNode(Connection con) {
		if (queue.isEmpty())
			throw new RuntimeException("Queue is empty: target unreachable?");
		QueueElement qe = queue.poll();
		RouteNode node = qe.node;
		for (RouteNode child : node.getChildren()) {
			double childCost = getRouteNodeData(node).getPartialPathCost() + getRouteNodeCost(child, con);
			double childCostEstimate = getLowerBoundTotalPathCost(child, con, childCost);
			addNodeToQueue(child, qe, childCost, childCostEstimate);
		}
	}

	/**
	 * This is just an estimate and not an absolute lower bound.
	 * The routing algorithm is therefore not A* and optimal.
	 * It's directed search and heuristic.
	 */
	private double getLowerBoundTotalPathCost(RouteNode node, Connection con, double partial_path_cost) {
		if(alpha == 0)
			return partial_path_cost;
		RouteNode target = con.getSinkRouteNode();
		RouteNodeData data = getRouteNodeData(node);
		int usage;
		if (node.type == RouteNodeType.IPIN || node.type == RouteNodeType.SINK) {
			usage = data.countSinkUses(con.sink) + 1;
		} else {
			usage = Math.max(data.countEqSinkUses(con.equivSink), data.countSourceUses(con.source)) + 1;
		}
		return partial_path_cost + alpha * a.lowerEstimateConnectionCost(node, target) / usage;
	}

	private double getRouteNodeCost(RouteNode node, Connection con) {
		RouteNodeData data = getRouteNodeData(node);
		double pres_cost;
		// How many connections validly share this routenode with this connection
		int usage;
		// How many nets that cannot share this routenode try to use it
		int overoccupation; 
		if (node.type == RouteNodeType.IPIN || node.type == RouteNodeType.SINK) {
			// Calculate usage
			usage = 1 + data.countSinkUses(con.sink);
			boolean containsSink = usage != 1;
			if (containsSink) {
				// Calculate overoccupation
				int occupation = data.numUniqueSinks();
				overoccupation = occupation - node.capacity;

				// Calculate cost
				if (overoccupation < 0) {
					pres_cost = 1.;
				} else {
					pres_cost = 1 + overoccupation * pres_fac;
				}
			} else {
				pres_cost = data.pres_cost;
			}
		} else {
			int usageSource = 1 + data.countSourceUses(con.source);
			int usageEqsink = 1 + data.countEqSinkUses(con.equivSink);
			boolean containsSource = usageSource != 1;
			boolean containsEqsink = usageEqsink != 1;
			usage = Math.max(usageSource, usageEqsink);
			if (containsSource || containsEqsink) {
				// Calculate overoccupation
				int occupation;
				if (usageSource < usageEqsink) {
					occupation = data.numUniqueEqSinks();
				} else {
					occupation = data.numUniqueSources();
				}
//				int occupationEqSink = data.numUniqueEqSinks();
//				if(!containsEqsink)
//					occupationEqSink++;
//				int occupationSource = data.numUniqueSources();
//				if(!containsSource)
//					occupationSource++;
//				occupation = Math.min(occupationEqSink, occupationSource);
				overoccupation = occupation - node.capacity;
				
				// Calculate cost
				if (overoccupation < 0) {
					pres_cost = 1.;
				} else {
					pres_cost = 1 + overoccupation * pres_fac;
				}
			} else {
				pres_cost = data.pres_cost;
			}
		}
//		pres_cost = Math.max(data.pres_cost, pres_cost);
		return node.baseCost * data.acc_cost * pres_cost / usage;
	}
	
	private void updateCost(double pres_fac, double acc_fac){//after one routing iteration
		 for (RouteNode node : a.getRouteNodes()) {
			RouteNodeData data = getRouteNodeData(node);
			int occ = data.occupation;
			int cap = node.capacity;
			
			if (occ >= cap) {
				data.acc_cost += (occ - cap) * acc_fac;
				data.pres_cost = 1.0 + (occ - cap + 1) * pres_fac;
			} else {
				data.pres_cost = 1.0;
			}
		}
	}

	private RouteNodeData getRouteNodeData(RouteNode node) {
		return node.routeNodeData;
	}

}


