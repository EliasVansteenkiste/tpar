package tpr;

import java.io.FileNotFoundException;

public interface Test {
	
	public int routeOnly(int channelWidth, int routingIterations, int K, int L, String netFile, String pFile, String logFile) throws FileNotFoundException, Exception;
	
	public void placementOnly(int height, int width, int placementEffort, int K, int L, String netFile, String sharingFile, String pFile, String logFile) throws FileNotFoundException, Exception;
	
	public int par(int height, int width, int channelWidth, int placementEffort, int routingIterations, int K, int L, String netFile, String sharingFile, String pFile, String logFile) throws FileNotFoundException, Exception;
	
}