package circuit;

import java.util.HashSet;
import java.util.Set;

import architecture.RouteNode;


public class Connection implements Comparable<Connection> {
	public final String name;
	public final Pin source;
	public final Pin sink;
	public final Pin equivSink;
	public int sinkFanin;
	public int sourceFanout;
	
	public Set<RouteNode> routeNodes;
	

	public Connection(String name, Pin source, Pin sink, Pin equivSink) {
		super();
		this.name = name;
		this.sink = sink;
		this.source = source;
		this.equivSink = equivSink;
		
		this.routeNodes = new HashSet<RouteNode>();
		this.sinkFanin = 1;
		this.sourceFanout = 1;
	}
	
	public Connection(String name) {
		this(name, null, null, null);
	}

	public Connection(Pin source, Pin sink) {
		this(source.toString()+"_"+sink.toString(), source, sink, null);
	}
	
	public Connection(Pin source, Pin sink, Pin equivSink) {
		this(source.toString()+"_"+sink.toString()+"_"+equivSink.toString(), source, sink, equivSink);
	}
	
	public Connection(String name, Pin source, Pin sink) {
		this(name, source, sink, null);
	}
	
	public Connection(Connection con) {
		this(con.name, con.source, con.sink, con.equivSink);
		this.routeNodes.addAll(con.routeNodes);
	}
	
	public void resetConnection(){
		this.routeNodes.clear();
	}
	
	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public boolean equals(Object o){
		if (o == null) return false;
	    if (!(o instanceof Connection)) return false;
	    Connection co = (Connection) o;
		if((co.sink==this.sink)&&(co.source==this.source)){
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		return this.sink.hashCode()^this.source.hashCode();
	}

	public RouteNode getSourceRouteNode() {
		return source.owner.site.source;
	}

	public RouteNode getSinkRouteNode() {
		return sink.owner.site.sink;
	}

	@Override
	public int compareTo(Connection other) {
		int r = this.source.compareTo(other.source);
		if(r!=0)
			return r;
		else
			return this.sink.compareTo(other.sink);
	}
	
	
	
}
