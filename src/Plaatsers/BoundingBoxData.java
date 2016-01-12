package Plaatsers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import circuit.Block;

public class BoundingBoxData {

	private double weight;
	
	private int size;
	
	public List<Block> blocks;
		
	Block owner;
	
	public double getWeight(){
		return weight;
	}
	
	public BoundingBoxData(Collection<Block> blocks) {
		Set<Block> blocks_set = new HashSet<Block>(blocks);
		this.blocks = new ArrayList<Block>(blocks_set);
		setWeightandSize();
	}
	
	public int calculateBoundingBox() {
		int bb;
		if(blocks.size()==2){
			Block b = blocks.get(0);
			Block c = blocks.get(1);
			bb=Math.abs(b.site.x-c.site.x)+Math.abs(b.site.y-c.site.y)+2;
		}else{
			int min_x = Integer.MAX_VALUE;
			int max_x = -1;
			int min_y = Integer.MAX_VALUE;
			int max_y = -1;
			for(Block bl : blocks) {
				if (bl.site.x < min_x)min_x=bl.site.x;			
				if (bl.site.x > max_x)max_x=bl.site.x;			
				if (bl.site.y < min_y)min_y=bl.site.y;
				if (bl.site.y > max_y)max_y=bl.site.y;
			}
			
			bb=(max_x-min_x+1)+(max_y-min_y+1);
		}
		return bb;
	}
	
	public int getBoundingBox() {
		return calculateBoundingBox();
	}
	
	public double calculateCost() {
		return weight*calculateBoundingBox();
	}

	public double getCost() {
		return weight*getBoundingBox();
	}
	
	private void setWeightandSize() {
		size = blocks.size();
		switch (size) {
			case 1:  weight=1; break;
			case 2:  weight=1; break;
			case 3:  weight=1; break;
			case 4:  weight=1.0828; break;
			case 5:  weight=1.1536; break;
			case 6:  weight=1.2206; break;
			case 7:  weight=1.2823; break;
			case 8:  weight=1.3385; break;
			case 9:  weight=1.3991; break;
			case 10: weight=1.4493; break;
			case 11:
			case 12:
			case 13:
			case 14:
			case 15: weight=(blocks.size()-10)*(1.6899-1.4493)/5+1.4493;break;				
			case 16:
			case 17:
			case 18:
			case 19:
			case 20: weight=(blocks.size()-15)*(1.8924-1.6899)/5+1.6899;break;
			case 21:
			case 22:
			case 23:
			case 24:
			case 25: weight=(blocks.size()-20)*(2.0743-1.8924)/5+1.8924;break;		
			case 26:
			case 27:
			case 28:
			case 29:
			case 30: weight=(blocks.size()-25)*(2.2334-2.0743)/5+2.0743;break;		
			case 31:
			case 32:
			case 33:
			case 34:
			case 35: weight=(blocks.size()-30)*(2.3895-2.2334)/5+2.2334;break;		
			case 36:
			case 37:
			case 38:
			case 39:
			case 40: weight=(blocks.size()-35)*(2.5356-2.3895)/5+2.3895;break;		
			case 41:
			case 42:
			case 43:
			case 44:
			case 45: weight=(blocks.size()-40)*(2.6625-2.5356)/5+2.5356;break;		
			case 46:
			case 47:
			case 48:
			case 49:
			case 50: weight=(blocks.size()-45)*(2.7933-2.6625)/5+2.6625;break;
			default: weight=(blocks.size()-50)*0.02616+2.7933;break;
		}
	}
	
	public String toString(){
		return owner.toString();
	}
	
}
