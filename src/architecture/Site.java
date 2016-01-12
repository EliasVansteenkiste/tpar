package architecture;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import circuit.Block;

public class Site {
	public final int x;
	public final int y;
	public final int n;
	
	public final SiteType type;
	
	private ArrayList<Block> blocks;
	
	public final String naam;
	public RouteNode source;
	public RouteNode sink;
	
	public Site(int x, int y, int n, SiteType t, String naam) {
		super();	
		this.x=x;
		this.y=y;
		this.n=n;
		this.type=t;
		this.naam=naam;
		this.blocks = new ArrayList<Block>();
	}
	
	double afstand(Site p) {
		return Math.abs(x-p.x)+Math.abs(y-p.y);
	}

	@Override
	public String toString() {
		return naam;
	}

	public void addBlock(Block block) {
		this.blocks.add(block);
		block.site = this;
	}

	public void removeBlock(Block block) {
		if(!this.blocks.remove(block))
			throw new RuntimeException();
		block.site = null;
	}

	public void removeAllBlocks() {
		for(Block block : getBlocks())
			removeBlock(block);
	}
	
	public Block getBlock() {
		if(this.blocks.size()>1)
			throw new RuntimeException();
		if(this.blocks.size() == 0)
			return null;
		else
			return this.blocks.get(0);
	}

	public Collection<Block> getBlocks() {
		return this.blocks;
	}

	public Block getRandomBlock(Random rand) {
		if(this.blocks.size() == 0)
			return null;
		else
			return this.blocks.get(rand.nextInt(this.blocks.size()));
	}
	
	public void sanityCheck() {
		for(Block block : this.blocks)
			if(block.site != this)
				throw new RuntimeException();
	}

	public boolean isOccupied() {
		return !blocks.isEmpty();
	}
}
