package PlacementParser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import circuit.Circuit;

public class Placement {
	public String netlist_file;
	public String architecture_file;
	
	public Map<String,PlaatsingUnit> plaatsingsmap;

	public int width;
	public int height;

	public Placement() {
		super();
		plaatsingsmap= new HashMap<String,PlaatsingUnit>(); 
	}
	
	public Placement IOPlacement(Circuit c) {
		Placement result;
		result = new Placement();
		
		for(Iterator<PlaatsingUnit> i=plaatsingsmap.values().iterator();i.hasNext();) {
			PlaatsingUnit pu=i.next();
			if (c.inputs.containsKey(pu.name)||(c.outputs.containsKey(pu.name))) {
				result.plaatsingsmap.put(pu.name,new PlaatsingUnit(pu));
			}
		}
		result.width=this.width;
		result.height=this.height;
		
		return result;
	}
	
	public void zwaartepunt() {
		int som_x=0;
		int som_y=0;
		for(PlaatsingUnit pu:plaatsingsmap.values()) {
			som_x+=pu.x;
			som_y+=pu.y;
		}
		double zx=((double)som_x/plaatsingsmap.size()-(double)width/2)/width;
		double zy=((double)som_y/plaatsingsmap.size()-(double)height/2)/height;
		
		System.out.println(zx+"	"+zy);
	}
	
}
