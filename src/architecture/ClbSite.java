package architecture;

import java.util.Collection;
import java.util.Vector;

import circuit.Clb;

public class ClbSite extends Site {
	
	public final Vector<RouteNode> opin;
	public final Vector<RouteNode> ipin; 

	public ClbSite(String naam, int x, int y, int n, int K, int L) {
		super(x, y, n, SiteType.CLB, naam);
		source = new RouteNode(naam+"_source", L, x, y, n, RouteNodeType.SOURCE);
		opin = new Vector<RouteNode>();
		for(int i=0;i<L;i++) {
			opin.add(new RouteNode(naam+"_opin_"+i, 1, x, y, n, RouteNodeType.OPIN));
		}
		RouteNode.connect(source, opin);
		
		sink = new RouteNode(naam+"_sink", K, x, y, n, RouteNodeType.SINK);
		ipin = new Vector<RouteNode>();
		for(int i=0;i<K;i++) {
			ipin.add(new RouteNode(naam+"_ipin_"+i, 1, x, y, n, RouteNodeType.IPIN));
		}
		RouteNode.connect(ipin, sink);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<Clb> getClbs() {
		return (Collection<Clb>)(Collection)getBlocks();
	}

}
