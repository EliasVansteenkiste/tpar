package tpr;

import com.lexicalscope.jewel.cli.Option;


public interface Arguments {
	
	@Option(description = "the number of iterations the router gets to solve the congestion", defaultValue="30", shortName="i") 
	int getNrOfRoutingIterations();
	
	@Option(description = "the placement effort", defaultValue="10",shortName="e") 
	int getPlacementEffort();
	
	@Option(description = "the channel width, the number of tracks in the channels", defaultValue="10",shortName="c") 
	int getChannelWidth();
	
	@Option(description = "the location of log file", defaultValue="temp.log",shortName="l") 
	String getLogFileName();
	
	@Option(description = "the location of placement file", shortName="p") 
	String getPlacementFileName(); 
	boolean isPlacementFileName();
	
	@Option(description = "the location of the net file", shortName="n") 
	String getNetFilename();
	
	@Option(description = "the width of the FPGA", shortName="w") 
	int getWidth();
	
	@Option(description = "the height of the FPGA", shortName="h") 
	int getHeight();
	
	@Option(description = "the route only option") 
	boolean getRouteOnly();
	
	@Option(description = "the placement only option") 
	boolean getPlacementOnly();
	
	@Option(helpRequest = true, description = "display help") 
	boolean getHelp();
}
