package Plaatsers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import architecture.Architecture2D;
import architecture.Site;
import circuit.Block;
import circuit.BlockType;
import circuit.Circuit;
import circuit.Clb;

public class PlacementManipulatorIOCLB implements PlacementManipulator {

	protected final Architecture2D a;
	protected final Circuit circuit;

	protected final int maxFPGAdimension;
	protected final Vector<Block> vBlocks;
	
	protected final Random rand;

	public PlacementManipulatorIOCLB(Architecture2D a, Circuit c) {
		this(a, c, new Random());
	}
	
	public PlacementManipulatorIOCLB(Architecture2D a, Circuit c, Random rand) {
		this.a=a;
		this.circuit=c;
		maxFPGAdimension = Math.max(a.width,a.height);
		vBlocks = new Vector<Block>();
		vBlocks.addAll(circuit.clbs.values());
		vBlocks.addAll(circuit.inputs.values());
		vBlocks.addAll(circuit.outputs.values());
		this.rand = rand;
	}
	
	public Swap findSwap(int Rlim) {
		Swap swap=new Swap();
		Block b = vBlocks.elementAt(rand.nextInt(vBlocks.size()));
		swap.pl1 = b.site;
		swap.bl1 = Arrays.asList(b);
		if(b.type==BlockType.CLB){
			swap.pl2 = a.randomClbSite(Rlim, swap.pl1);
		}else if(b.type == BlockType.INPUT){
			swap.pl2 = a.randomISite(Rlim, swap.pl1);
		}else if(b.type == BlockType.OUTPUT){
			swap.pl2 = a.randomOSite(Rlim, swap.pl1);
		}
		if(swap.pl2.isOccupied())
			swap.bl2 = Arrays.asList(swap.pl2.getBlock());
		return swap;
	}
	
	public int maxFPGAdimension() {
		return maxFPGAdimension;
	}

	public double numBlocks() {
		return circuit.numBlocks();
	}
	
	public void placementCLBsConsistencyCheck() {
		a.sanityCheck();
		Set<Clb> clbs = new HashSet<Clb>(circuit.clbs.values());
		for(Site s:a.getSites()){
			if(s.isOccupied() && s.getBlock().type==BlockType.CLB) {
				if(!clbs.remove(s.getBlock())){
					throw new RuntimeException("Placement consistency check failed! clb:"+s.getBlock()+", site:"+s);
				}
			}
		}
	}

}