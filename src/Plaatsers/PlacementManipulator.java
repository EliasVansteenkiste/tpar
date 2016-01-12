package Plaatsers;

public interface PlacementManipulator {

	public Swap findSwap(int Rlim);
	
	public int maxFPGAdimension();

	public double numBlocks();
	
	public void placementCLBsConsistencyCheck();

}
