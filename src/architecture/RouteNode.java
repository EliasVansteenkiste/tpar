package architecture;

import global.GlobalConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import TRouter.RouteNodeData;

public class RouteNode implements Comparable<RouteNode> {
	public final String name;
	
	public final short x;
	public final short y;
	public final short n;
	public final RouteNodeType type;
	public final short capacity;
	public final double baseCost;
	
	private Collection<RouteNode> children;
	public RouteNodeData routeNodeData;
//	private Set<RouteNode> parents;
	
	public boolean target;

	public RouteNode(String name, int capacity, int x, int y, int n, RouteNodeType t) {
		super();
		if(GlobalConstants.routeNodesWithName)
			this.name = name;
		else
			this.name = null;

		this.x = (short)x;
		this.y = (short)y;
		this.n = (short)n;
		this.type = t;
		this.capacity = (short)capacity;
		this.baseCost = calculateBaseCost();
		
		this.target = false;
		this.routeNodeData = null;
		
		children = new ArrayList<RouteNode>();
//		parents = new HashSet<RouteNode>();
	}

	public RouteNode(String name) {
		this(name, -1, -1, -1, -1, RouteNodeType.SOURCE);
	}
	
	public void resetDataInNode(){
		this.target = false;
	}

	private double calculateBaseCost() {
		switch (type) {
		case SOURCE:
		case OPIN:
		case HCHAN:
		case VCHAN:
			return 1;
		case SINK:
			return 0;
		case IPIN:
			return 0.95;
		default:
			throw new RuntimeException();
		}
	}
	
	@Override
	public String toString() {
		if(name == null)
			return type+"_"+x+"_"+y+"_"+n;
		return name;
	}
	
	private void addChild(RouteNode node) {
		if(!children.contains(node)) {
			children.add(node);
		}
	}

	public Collection<RouteNode> getChildren() {
		return children;
	}

	private void addParent(RouteNode node) {
//		parents.add(node);
	}
	
//	public Collection<RouteNode> getParents() {
//		return parents;
//	}

	public static void connect(Vector<RouteNode> vector, Vector<RouteNode> vector2) {
		if (vector!=null && vector2!=null)
		for (int i=0; i<vector.size();i++) {
			connect(vector.get(i),vector2.get(i));
		}
	}
	
	//(a*x+b) mod ChannelWidth
	public static void connect(Vector<RouteNode> vector1, Vector<RouteNode> vector2, int a, int b) {
		if (vector1!=null && vector2!=null){
			int channelWidth = vector1.size();
			for (int i=0; i<channelWidth;i++) {
				connect(vector1.get(i),vector2.get(((a*i+b+2*channelWidth)%channelWidth)) );
			}
		}
	}
	
	public static void connect(Vector<RouteNode> vector, Vector<RouteNode> vector2, int parity, int offset, int a, int b) {
		int j;
		if (vector != null && vector2 != null)
			for (int i = 0; i < vector.size(); i++) {
				if (i % 2 == parity) {
					j = ((((a * (i / 2) + b) % (vector.size() / 2) + (vector.size() / 2)) % (vector.size() / 2)) * 2)+ (i % 2)+ offset;
					//System.out.println("a is "+a+",b is "+b+", i is "+i+" en j is "+j);
					connect(vector.get(i), vector2.get(j));
				}
			}

	}
	
	public static void connect(RouteNode node, Vector<RouteNode> channel) {
		for (RouteNode wire:channel) {
			connect(node, wire);
		}
	}

	static int ic2 = 0;
	static int nc2 = 0;
	public static void connect(RouteNode node, Vector<RouteNode> channel, double fcout) {
		for (RouteNode wire:channel) {
			if(ic2/(double)nc2 <= fcout) {
				connect(node, wire);
				ic2++;
			}
			nc2++;
		}
	}

	public static void connect(Vector<RouteNode> channel, RouteNode node) {
		for (RouteNode wire:channel) {
			connect(wire, node);
		}
	}

	static int ic1 = 0;
	static int nc1 = 0;
	public static void connect(Vector<RouteNode> channel, RouteNode node, double fcin) {
		for (RouteNode wire:channel) {
			if(ic1/(double)nc1 <= fcin) {
				connect(wire, node);
				ic1++;
			}
			nc1++;
		}
	}
	
	public static void connect(RouteNode parent, RouteNode child) {
		parent.addChild(child);
		child.addParent(parent);
	}

	public boolean isWire() {
		return type == RouteNodeType.HCHAN || type == RouteNodeType.VCHAN;
	}
	
	public void reduceMemoryUsage() {
		((ArrayList<RouteNode>)children).trimToSize();
	}
	
	@Override
	public int compareTo(RouteNode o) {
		int r = type.compareTo(o.type);
		if (this == o)
			return 0;
		else if (r < 0)
			return -1;
		else if (r > 0)
			return 1;
		else if(x < o.x)
			return -1;
		else if (x > o.x)
			return 1;
		else if (y < o.y)
			return -1;
		else if (y > o.y)
			return 1;
		else if (n < o.n)
			return -1;
		else if (n > o.n)
			return 1;
		else 
			return Long.valueOf(this.hashCode()).compareTo(Long.valueOf(o.hashCode()));
			//throw new RuntimeException(); 
	}
	
}
