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

public class PlacementManipulatorCLB implements PlacementManipulator{

	protected final Architecture2D a;
	protected final Circuit circuit;

	protected final int maxFPGAdimension;
	protected final Vector<Clb> vClbs;
	
	protected final Random rand;


	public PlacementManipulatorCLB(Architecture2D a, Circuit c) {
		this(a, c, new Random());
	}
	
	public PlacementManipulatorCLB(Architecture2D a, Circuit c, Random rand) {
		this.a=a;
		this.circuit=c;
		maxFPGAdimension = Math.max(a.width,a.height);
		vClbs = new Vector<Clb>();
		vClbs.addAll(circuit.clbs.values());
		this.rand = rand;
	}
	
	
	public Swap findSwap(int Rlim) {
		Swap swap=new Swap();
		Clb b = vClbs.elementAt(rand.nextInt(vClbs.size()));
		swap.pl1 = b.site;
		swap.bl1 = Arrays.asList((Block)b);
		swap.pl2 = a.randomClbSite(Rlim, swap.pl1);
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