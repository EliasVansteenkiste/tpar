package Plaatsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import architecture.Architecture2D;
import architecture.ClbSite;
import circuit.Block;
import circuit.BlockType;
import circuit.Circuit;
import circuit.Clb;

/**
 * PlacementManipulatorSharingIOCLB is an adapted version of PlacementManipulatorIOCLB
 * for LUT resource sharing.
 */
public class PlacementManipulatorSharingIOCLB2 extends PlacementManipulatorSharingIOCLB {

	public PlacementManipulatorSharingIOCLB2(Architecture2D a, Circuit c) {
		this(a,c,new Random(1));
	}
	
	public PlacementManipulatorSharingIOCLB2(Architecture2D a, Circuit c, Random rand) {
		super(a,c,rand);
	}
	
	@Override
	public Swap findSwap(int Rlim) {
		Swap swap = new Swap();
		Block bl1 = vBlocks.elementAt(rand.nextInt(vBlocks.size()));
		swap.pl1 = bl1.site;
		swap.bl1 = Arrays.asList(bl1);
		if (bl1.type == BlockType.CLB) {
			swap.pl2 = a.randomClbSite(Rlim, swap.pl1);
			ArrayList<Clb> clbs2 = new ArrayList<Clb>(((ClbSite) swap.pl2).getClbs());
			if (((Clb) bl1).getActivationSet().canShareWith(
					Clb.getActivationSets(clbs2))) {	// the block bl1 can be added to the site
				swap.bl2 = Arrays.asList();
			} else {
				Clb bl2 = clbs2.remove(rand.nextInt(clbs2.size()));
				ArrayList<Clb> clbs1 = new ArrayList<Clb>(((ClbSite) swap.pl1).getClbs());
				clbs1.remove(bl1);
			
				if (((Clb) bl1).getActivationSet().canShareWith(
						Clb.getActivationSets(clbs2))
						&& bl2.getActivationSet().canShareWith(
								Clb.getActivationSets(clbs1))) {
					swap.bl2 = Arrays.asList((Block)bl2);	// the block bl1 can be swapped with block bl2 
				} else {	// swap all blocks on site pl1 and pl2
					swap.bl1 = new ArrayList<Block>(swap.pl1.getBlocks());
					swap.bl2 = new ArrayList<Block>(swap.pl2.getBlocks());
				}
			}
		} else if (bl1.type == BlockType.INPUT) {
			swap.pl2 = a.randomISite(Rlim, swap.pl1);
			if (swap.pl2.getBlock() != null)
				swap.bl2 = Arrays.asList(swap.pl2.getBlock());
			else
				swap.bl2 = Arrays.asList();
		} else if (bl1.type == BlockType.OUTPUT) {
			swap.pl2 = a.randomOSite(Rlim, swap.pl1);
			if (swap.pl2.getBlock() != null)
				swap.bl2 = Arrays.asList(swap.pl2.getBlock());
			else
				swap.bl2 = Arrays.asList();
		}
		return swap;
	}

}