package circuit;

import architecture.Site;

public class Block implements Comparable<Block> {
	public final String name;
	public final BlockType type;
	
	public Site site;
	public boolean fixed;

	public Block(String name, BlockType type) {
		super();
		this.name = name;
		this.type = type;
	}

	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean equals(Object o) {
		Block other = (Block)o;
		return type.equals(other.type) && name.equals(other.name);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode()^type.hashCode();
	}
	
	@Override
	public int compareTo(Block other) {
		if(type.ordinal() < other.type.ordinal())
			return -1;
		else if(type.ordinal() > other.type.ordinal())
			return 1;
		else
			return name.compareTo(other.name);	
	}
	
	public String getName() {
		return name;
	}
	
}
