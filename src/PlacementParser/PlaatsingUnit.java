package PlacementParser;

public class PlaatsingUnit {
	public String name;
	public int x;
	public int y;
	public int n;
	
	public PlaatsingUnit(String naam, int x, int y, int n) {
		super();
		// TODO Auto-generated constructor stub
		this.name = naam;
		this.x = x;
		this.y = y;
		this.n = n;
	}

	public PlaatsingUnit(PlaatsingUnit pu) {
		this.name=pu.name;
		this.x=pu.x;
		this.y=pu.y;
		this.n=pu.n;
	}
	
	
}
