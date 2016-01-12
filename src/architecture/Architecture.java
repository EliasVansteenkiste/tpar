package architecture;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;


public abstract class Architecture {
	public final Random rand;
//	private final Map<String,Site> siteMap;
//	private final Map<String,RouteNode> routeNodeMap;
	private final ArrayList<Site> sites;
	private final ArrayList<RouteNode> routeNodes;
	
	public Architecture() {
		super();

		rand = new Random(1);

//		siteMap = new HashMap<String,Site>();
//		routeNodeMap = new HashMap<String, RouteNode>();
		sites = new ArrayList<Site>();
		routeNodes = new ArrayList<RouteNode>();
	}

	public void printRoutingGraph(PrintStream stream) {
		for(RouteNode node : getRouteNodes()) {
			stream.println("Node "+node.name);
			for (RouteNode child : node.getChildren()) {
				stream.print(child.name+ " ");
			}
			stream.println();
			stream.println();
		}
	}

	public void addSite(Site site) {
//		siteMap.put(site.naam,site);
		sites.add(site);
	}
	
	public Collection<Site> getSites() {
//		return siteMap.values();
		return sites;
	}

//	public Site getSite(String name) {
//		return siteMap.get(name);
//	}

	public void addRouteNode(RouteNode node) {
//		routeNodeMap.put(node.name, node);
		routeNodes.add(node);
	}

	public Collection<RouteNode> getRouteNodes() {
//		return routeNodeMap.values();
		return routeNodes;
	}

	public abstract double lowerEstimateConnectionCost(RouteNode child, RouteNode target);
	
//	public RouteNode getOrMakeRouteNode(String name) {
//		RouteNode result=routeNodeMap.get(name);
//		if (result==null) {
//			result=new RouteNode(name);
//			addRouteNode(result);
//		}
//		return result;
//	}

}
