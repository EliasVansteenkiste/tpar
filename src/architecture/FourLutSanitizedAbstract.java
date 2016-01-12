package architecture;

import java.util.Vector;

public abstract class FourLutSanitizedAbstract extends Architecture2D {

	public final int channelWidth;
	protected Vector<RouteNode>[][] horizontalChannels;
	protected Vector<RouteNode>[][] verticalChannels;

	@SuppressWarnings("unchecked")
	public FourLutSanitizedAbstract(int width, int height, int channelWidth, int K, int L) {
		super(width, height, K, L);
		this.channelWidth=channelWidth;
		
		horizontalChannels = new Vector[width+2][height+2];
		verticalChannels = new Vector[width+2][height+2];
		
		generateWires();
		
		generateSwitchBlocks();
		
		generateIOBConnectionBlocks();
		
		generateClbConnectionBlocks();
		
		reduceMemoryUsage(width, height);
		
		//We don't need to store these
		horizontalChannels = null;
		verticalChannels = null;
	}

	protected void reduceMemoryUsage(int width, int height) {
		//Minimise memory usage
		for (int x = 0; x<width+1; x++) {
			for (int y = 0; y<height+1; y++) {
				for(RouteNode rn : horizontalChannels[x][y])
					rn.reduceMemoryUsage();
				for(RouteNode rn : verticalChannels[x][y])
					rn.reduceMemoryUsage();
			}
		}
	}

	protected void generateClbConnectionBlocks() {
		//Connect CLBs
		for (ClbSite site : getClbSites()) {
			int x = site.x;
			int y = site.y;
			Vector<Vector<RouteNode>> chans = new Vector<Vector<RouteNode>>();
			chans.add(horizontalChannels[x][y - 1]);
			chans.add(verticalChannels[x - 1][y]);
			chans.add(horizontalChannels[x][y]);
			chans.add(verticalChannels[x][y]);
			connectClbSite(site,
					chans);
		}
	}

	protected void generateIOBConnectionBlocks() {
		//Generating the IO blocks
		for (int y=1; y<height+1; y++) {
			RouteNode.connect(((IoSite)this.getSite(0,y,0)).opin, verticalChannels[0][y]);
			RouteNode.connect(((IoSite)this.getSite(width+1,y,0)).opin, verticalChannels[width][y]);
			RouteNode.connect(verticalChannels[0][y], ((IoSite)this.getSite(0,y,1)).ipin);
			RouteNode.connect(verticalChannels[width][y], ((IoSite)this.getSite(width+1,y,1)).ipin);
		}
		for (int x=1; x<width+1; x++) {
			RouteNode.connect(((IoSite)this.getSite(x,0,0)).opin, horizontalChannels[x][0]);
			RouteNode.connect(((IoSite)this.getSite(x,height+1,0)).opin, horizontalChannels[x][height]);
			RouteNode.connect(horizontalChannels[x][0], ((IoSite)this.getSite(x,0,1)).ipin);
			RouteNode.connect(horizontalChannels[x][height], ((IoSite)this.getSite(x,height+1,1)).ipin);
		}
	}

	protected void generateWires() {
		//Generate routing wires 
		for (int x= 0; x<width+1; x++) {
			for (int y= 0; y<height+1; y++) {
				horizontalChannels[x][y]=new Vector<RouteNode>();
				if(x==0)
					continue;
				for (int i=0; i<channelWidth; i++) {
					RouteNode wire = new RouteNode("ChanX_"+x+"_"+y+"_"+i, 1, x, y, i, RouteNodeType.HCHAN);
					horizontalChannels[x][y].add(wire);
					addRouteNode(wire);
				}
				horizontalChannels[x][y].trimToSize();
			}
		}
		for (int x= 0; x<width+1; x++) {
			for (int y= 0; y<height+1; y++) {
				verticalChannels[x][y]=new Vector<RouteNode>();
				if(y==0)
					continue;
				for (int i=0; i<channelWidth; i++) {
					RouteNode wire = new RouteNode("ChanY_"+x+"_"+y+"_"+i, 1, x, y, i, RouteNodeType.VCHAN);
					verticalChannels[x][y].add(wire);
					addRouteNode(wire);
				}
				verticalChannels[x][y].trimToSize();
			}
		}
	}

	protected abstract void generateSwitchBlocks();

	protected void connectClbSite(ClbSite site, Vector<Vector<RouteNode>> chans) {
		final double fcout = 1;//0.66;
		final double fcin = 1;//0.66;
		int j=0;
		for(int i=0; i<site.opin.size(); i++)
			RouteNode.connect(site.opin.get(i), chans.get(j++%chans.size()), fcout);
		for(int i=0; i<site.ipin.size(); i++)
			RouteNode.connect(chans.get(j++%chans.size()), site.ipin.get(i), fcin);
	}

}
