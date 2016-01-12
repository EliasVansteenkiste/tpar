package architecture;

public class IoSite extends Site {

	public RouteNode opin;
	public RouteNode ipin;

	public IoSite(String naam, int x, int y, int n) {
		super(x, y, n, SiteType.IO, naam);
		
		source = new RouteNode(naam+"_source", 1, x, y, n, RouteNodeType.SOURCE);
		opin = new RouteNode(naam+"_opin", 1, x, y, n, RouteNodeType.OPIN);
		sink = new RouteNode(naam+"_sink", 1, x, y, n, RouteNodeType.SINK);
		ipin = new RouteNode(naam+"_ipin", 1, x, y, n, RouteNodeType.IPIN);
		
		RouteNode.connect(source, opin);
		RouteNode.connect(ipin, sink);
		
	}

}
