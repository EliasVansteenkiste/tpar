package architecture;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class Architecture2D extends Architecture {

	public final int K; //#CLB inputs
	public final int L; //#CLB outputs
	public final int width;
	public final int height;
	public Site[][][] siteArray;
	public Vector<IoSite> Isites; 
	public Vector<IoSite> Osites;

	public Architecture2D(int width, int height, int K, int L) {
		super();
		this.width=width;
		this.height=height;
		this.K=K;
		this.L=L;
		
		int x, y, n;
		
		siteArray = new Site[width+2][height+2][2];
		
		
		//Generating the IO blocks
		for (y=1; y<height+1; y++) {
			for (n=0; n<2; n++) {
				putIoSite(0, y, n);
				putIoSite(width+1, y, n);
			}
		}
		for (x=1; x<width+1; x++) {
			for (n=0; n<2; n++) {
				putIoSite(x, 0, n);
				putIoSite(x, height+1, n);
			}
		}
		
		//Generate CLBs
		for (x=1; x<=width; x++) {
			for (y=1; y<=height; y++) {
				putClbSite(x,y,0);
			}
		}
		
		//Generate Isites set
		Isites = new Vector<IoSite>();
		for (y=1; y<height+1; y++) {
			Isites.add((IoSite)siteArray[0][y][0]);
			Isites.add((IoSite)siteArray[width+1][y][0]);
		}
		for (x=1; x<width+1; x++) {
			Isites.add((IoSite)siteArray[x][0][0]);
			Isites.add((IoSite)siteArray[x][height+1][0]);
		}
		
		//Generate Osites set
		Osites = new Vector<IoSite>();
		for (y=1; y<height+1; y++) {
			Osites.add((IoSite)siteArray[0][y][1]);
			Osites.add((IoSite)siteArray[width+1][y][1]);
		}
		for (x=1; x<width+1; x++) {
			Osites.add((IoSite)siteArray[x][0][1]);
			Osites.add((IoSite)siteArray[x][height+1][1]);
		}
		
	}
	
	private void putIoSite(int x,int y, int n) {
		IoSite site = new IoSite("Site_"+x+"_"+y+"_"+n, x, y,n);
		addSite(site, x, y, n);
		addRouteNodes(site);
	}
	
	private void putClbSite(int x,int y, int n) {
		ClbSite site = new ClbSite("Site_"+x+"_"+y+"_"+n, x, y, n, K, L);
		addSite(site, x, y, n);
		addRouteNodes(site);
	}
	
	public ClbSite randomClbSite(int Rlim, Site pl1) {
		Site pl2;
		do {
			//-1 is nodig om de coordinaten in clbPlaatsArray te verkrijgen.	
			int x_to=rand.nextInt(2*Rlim+1)-Rlim+pl1.x;	
			while (x_to<1) x_to+=width;
			while (x_to>= width+1) x_to-=width;
		
			int y_to=rand.nextInt(2*Rlim+1)-Rlim+pl1.y;					
			while (y_to<1) y_to+=height;
			while (y_to>= height+1) y_to-=height;
			
			pl2=siteArray[x_to][y_to][0];
		} while (pl1==pl2);
		return (ClbSite)pl2;
	}
	
	public Site randomISite(int Rlim, Site pl1) {
		Site pl2 = null;
		int manhattanDistance = -1;
		do {
			pl2 = Isites.elementAt(rand.nextInt(Isites.size()));
			if(pl2==null)System.out.println("woops");
			manhattanDistance = Math.abs(pl1.x-pl2.x)+Math.abs(pl1.y-pl2.y);
		} while (pl1==pl2||manhattanDistance>Rlim);
		return pl2;
	}
	
	public Site randomOSite(int Rlim, Site pl1) {
		Site pl2 = null;
		int manhattanDistance = -1;
		do {
			pl2 = Osites.elementAt(rand.nextInt(Osites.size()));
			if(pl2==null)System.out.println("woops");
			manhattanDistance = Math.abs(pl1.x-pl2.x)+Math.abs(pl1.y-pl2.y);
		} while (pl1==pl2||manhattanDistance>Rlim);
		return pl2;
	}

	private void addRouteNodes(ClbSite site) {
		addRouteNode(site.source);
		for (RouteNode opin:site.opin) {
			addRouteNode(opin);			
		}
		addRouteNode(site.sink);
		for (RouteNode ipin:site.ipin) {
			addRouteNode(ipin);			
		}
	}

	private void addRouteNodes(IoSite site) {
		addRouteNode(site.source);
		addRouteNode(site.opin);
		addRouteNode(site.sink);
		addRouteNode(site.ipin);
	}

	public Site getSite(int x, int y, int n) {
		return siteArray[x][y][n];
	}

	public void addSite(Site site, int x, int y, int n) {
		super.addSite(site);
		siteArray[x][y][n] = site;
	}
	
	public int numAvailableClbSites() {
		int num = 0;
		for(Site s:getSites())
			if(s.type.equals(SiteType.CLB))
				num++;
		return num;
	}
	
	public int numOccupiedClbSites() {
		int num = 0;
		for(Site s:getSites())
			if(s.type.equals(SiteType.CLB) && s.isOccupied())
				num++;
		return num;
	}
	
	@Override
	public double lowerEstimateConnectionCost(RouteNode source, RouteNode target) {
		return Math.abs(target.x-source.x) + Math.abs(target.y-source.y);
	}

	public void sanityCheck() {
		for(Site site:getSites())
			site.sanityCheck();
	}

	public  ArrayList<ClbSite> getClbSites() {
		ArrayList<ClbSite> temp = new ArrayList<ClbSite>();
		for (int x=1;x<width+1;x++) {
			for (int y=1;y<height+1;y++) {
				Site s = siteArray[x][y][0];
				temp.add((ClbSite)s);				
			}
		}
		return temp;
	}

	public List<IoSite> getOutputSites() {
		return Collections.unmodifiableList(Osites);
	}

	public List<IoSite> getInputSites() {
		return Collections.unmodifiableList(Isites);
	}
}
