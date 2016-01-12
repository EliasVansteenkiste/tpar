package Plaatsers;

import java.util.Collection;

import architecture.Site;
import circuit.Block;

public class Swap {
	Site pl1;
	Site pl2;
	Collection<Block> bl1;
	Collection<Block> bl2;
	
	public Swap() {}
	
	public Swap(Site pl1, Collection<Block> bl1, Site pl2, Collection<Block> bl2) {
		super();
		this.pl1 = pl1;
		this.bl1 = bl1;
		this.pl2 = pl2;
		this.bl2 = bl2;
	}

	public String toString(){
		return "Site 1: "+pl1+"- clb: "+bl1+", Site 2: "+pl2+"- clb: "+bl2;
	}

	public void apply() {
		Swap swap = this;
		for(Block b : swap.bl1)
			swap.pl1.removeBlock(b);
		for(Block b : swap.bl2)
			swap.pl2.removeBlock(b);
		for(Block b : swap.bl1)
			swap.pl2.addBlock(b);
		for(Block b : swap.bl2)
			swap.pl1.addBlock(b);
	}

	public void unapply() {
		new Swap(pl1, bl2, pl2, bl1).apply();
	}
}
