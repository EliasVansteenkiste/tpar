package Plaatsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import Sharing.ActivationSet;
import architecture.Architecture2D;
import architecture.ClbSite;
import architecture.Site;
import circuit.Block;
import circuit.BlockType;
import circuit.Circuit;
import circuit.Clb;

/**
 * PlacementManipulatorSharingIOCLB is an adapted version of
 * PlacementManipulatorIOCLB for LUT resource sharing.
 */
public class PlacementManipulatorSharingIOCLB implements PlacementManipulator {

	protected final Architecture2D a;
	protected final Circuit circuit;

	protected final int maxFPGAdimension;
	protected final Vector<Block> vBlocks;

	protected final Random rand;

	public PlacementManipulatorSharingIOCLB(Architecture2D a, Circuit c) {
		this(a, c, new Random(1));
	}

	public PlacementManipulatorSharingIOCLB(Architecture2D a, Circuit c,
			Random rand) {
		this.a = a;
		this.circuit = c;
		maxFPGAdimension = Math.max(a.width, a.height);
		vBlocks = new Vector<Block>();
		vBlocks.addAll(circuit.getClbs());
		vBlocks.addAll(circuit.getInputs());
		vBlocks.addAll(circuit.getOutputs());
		Collections.sort(vBlocks);
		this.rand = rand;
	}

	public Swap findSwap(int Rlim) {
		Swap swap = new Swap();
		Block b = vBlocks.elementAt(rand.nextInt(vBlocks.size()));
		swap.pl1 = b.site;
		swap.bl1 = Arrays.asList(b);
		if (b.type == BlockType.CLB) {
			swap.pl2 = a.randomClbSite(Rlim, swap.pl1);
			if (((Clb) b).getActivationSet().canShareWith(
					Clb.getActivationSets(((ClbSite) swap.pl2).getClbs()))) {
				swap.bl2 = Arrays.asList();
			} else {
				swap.bl1 = new ArrayList<Block>(swap.pl1.getBlocks());
				swap.bl2 = new ArrayList<Block>(swap.pl2.getBlocks());
			}
		} else if (b.type == BlockType.INPUT) {
			swap.pl2 = a.randomISite(Rlim, swap.pl1);
			if (swap.pl2.getBlock() != null)
				swap.bl2 = Arrays.asList(swap.pl2.getBlock());
			else
				swap.bl2 = Arrays.asList();
		} else if (b.type == BlockType.OUTPUT) {
			swap.pl2 = a.randomOSite(Rlim, swap.pl1);
			if (swap.pl2.getBlock() != null)
				swap.bl2 = Arrays.asList(swap.pl2.getBlock());
			else
				swap.bl2 = Arrays.asList();
		}
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
		Set<Clb> clbs = new HashSet<Clb>(circuit.getClbs());
		for (Site s : a.getSites()) {
			HashSet<ActivationSet> sets = new HashSet<ActivationSet>();
			for (Block b : s.getBlocks()) {
				if (b.type == BlockType.CLB) {
					Clb bb = (Clb) b;
					if (!clbs.remove(b)) {
						throw new RuntimeException(
								"Placement consistency check failed! clb:"
										+ s.getBlock() + ", site:" + s);
					}
					if (!bb.getActivationSet().canShareWith(sets))
						throw new RuntimeException(
								"Problem with CLB resource sharing");
					sets.add(bb.getActivationSet());
				}
			}
		}
	}

}