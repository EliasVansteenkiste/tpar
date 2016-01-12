package Plaatsers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import architecture.Architecture2D;
import architecture.ClbSite;
import architecture.IoSite;
import architecture.Site;
import circuit.Circuit;
import circuit.Clb;
import circuit.Input;
import circuit.Output;



public class Rplace {
	protected final Circuit c;
	protected final Architecture2D a;
	protected final Random rand;
	
	public Rplace(Circuit c, Architecture2D a, Random rand) {
		this.c = c;
		this.a = a;
		this.rand = rand;
	}

	public void placeCLBs() {
		ArrayList<ClbSite> temp = a.getClbSites();
		for(Site s : temp)
			s.removeAllBlocks();
		Collections.shuffle(temp, rand);
		for(Clb b : c.getClbs()) {
			if(temp.isEmpty())
				throw new RuntimeException("No clb sites left: FPGA size is probably too small for design");
			Site site = temp.remove(temp.size() - 1);
			site.addBlock(b);
		}
	}

	private void placeIOs() {
		placeInputs();
		placeOutputs();
	}

	private void placeOutputs() {
		List<IoSite> temp = new ArrayList<IoSite>(a.getOutputSites());
		for(Site s : temp)
			s.removeAllBlocks();
		Collections.shuffle(temp, rand);
		for(Output out:c.getOutputs()) {
			if(temp.isEmpty())
				throw new RuntimeException("No output sites left: FPGA size is probably too small for design");
			Site site = temp.remove(temp.size() - 1);
			site.addBlock(out);
		}
	}

	private void placeInputs() {
		List<IoSite> temp = new ArrayList<IoSite>(a.getInputSites());
		for(Site s : temp)
			s.removeAllBlocks();
		Collections.shuffle(temp, rand);
		for(Input in:c.getInputs()) {
			if(temp.isEmpty())
				throw new RuntimeException("No input sites left: FPGA size is probably too small for design");
			Site site = temp.remove(temp.size() - 1);
			site.addBlock(in);
		}
	}

	public void placeCLBsandIOs() {
		placeCLBs();
		placeIOs();
		c.sanityCheckPlacement();
	}
}
