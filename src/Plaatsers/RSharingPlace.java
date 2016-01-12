package Plaatsers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import architecture.Architecture2D;
import architecture.ClbSite;
import architecture.Site;
import circuit.Circuit;
import circuit.Clb;



public class RSharingPlace extends Rplace {
	
	public RSharingPlace(Circuit c, Architecture2D a, Random rand) {
		super(c, a, rand);
	}

	@Override
	public void placeCLBs() {
		ArrayList<ClbSite> temp = a.getClbSites();
		for(Site s : temp)
			s.removeAllBlocks();
		Collections.shuffle(temp, rand);
		ArrayList<Clb> sharedClbs = new ArrayList<Clb>();
		for(Clb b : c.getClbs()) {
			if(!b.getActivationSet().isAlwaysActive()) {
				sharedClbs.add(b);
				continue;
			}
			if(temp.isEmpty())
				throw new RuntimeException("No clb sites left: FPGA size is probably too small for design");
			Site site = temp.remove(temp.size() - 1);
			site.addBlock(b);
		}
		for(Clb b : sharedClbs) {
			boolean found = false;
			for(ClbSite site : temp) {
				if(b.getActivationSet().canShareWith(Clb.getActivationSets(site.getClbs()))) {
					site.addBlock(b);
					found = true;
					break;
				}
			}
			if(!found)
				System.out.println(b + "\tnot placed");
		}
	}
}
