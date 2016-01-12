package TRouter;

import architecture.RouteNode;

public class QueueElement implements Comparable<QueueElement> {
	final RouteNode node;
	final QueueElement prev;

	public QueueElement(RouteNode node, QueueElement qe) {
		this.node = node;
		this.prev = qe;
	}

	@Override
	public String toString() {
		return node.name+"@"+getPartialPathCost();
	}
	
	@Override
	public int hashCode() {
		return (int)this.getPartialPathCost() ^ this.node.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		QueueElement rn = (QueueElement)o;
		return this.getPartialPathCost() == rn.getPartialPathCost() && this.node == rn.node;
	}
	
	@Override
	public int compareTo(QueueElement obj) {
		if (getLowerBoundTotalPathCost() < obj.getLowerBoundTotalPathCost())
			return -1;
		else if (getLowerBoundTotalPathCost() == obj.getLowerBoundTotalPathCost())
			return node.compareTo(obj.node);
		else 
			return 1;
	}

	double getPartialPathCost() {
		return node.routeNodeData.getPartialPathCost();
	}
	
	double getLowerBoundTotalPathCost() {
		return node.routeNodeData.getLowerBoundTotalPathCost();
	}
	
}
