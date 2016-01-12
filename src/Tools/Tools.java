package Tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;

import tpr.Test;
import circuit.Circuit;

public class Tools {
	
	public void writeToFile(String str, String filename){
		try{
			// Create file 
			FileWriter fstream = new FileWriter(filename,true);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(str);
			//Close the output stream
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
 	public void determineFPGADimensions(String netfile) throws FileNotFoundException, Exception {
		//Read in con netlist.
		System.out.println("Read in "+netfile);
		Readnetlist parser=new Readnetlist(new FileInputStream(new File(netfile)));
		Circuit c = null;
		c = parser.read(true);
		int CLBs = c.clbs.size();
		double calcDim = Math.ceil(Math.sqrt(1.20*CLBs));
		System.out.println("CLBs = "+CLBs+", calculated dimension = "+calcDim+", Inputs = "+c.inputs.size()+"/"+(4*calcDim)+", Outputs = "+c.outputs.size()+"/"+(4*calcDim));
	}
	public int findMinimalChannelWidth(Test test, int startChannelWidth, int routingIterations, String netFile, String pFile, String logFile, String minChanWidths) throws FileNotFoundException, circuit.parser.conlist.ParseException{
		int cw = startChannelWidth;
		int rits = 1;
		int legals = 0;
		for(;rits>0&&cw>0;cw--){
			rits = test.routeOnly(cw, routingIterations, netFile, pFile, logFile);
			if(rits>0)legals++;
		}
		if(cw>0){
			if(legals<1) writeToFile(netFile+": failed", minChanWidths);
			else writeToFile(netFile+": mTW = "+(cw+2)+"\n", minChanWidths);
		}else{
			writeToFile(netFile+": failed", minChanWidths);
		}
		
		return (cw+1);
	}	
	public int findMinimalChannelWidthBinSearch(Test test, int upperBoundaryChannelWidth, int lowerBoundaryChannelWidth, int routingIterations, String netFile, String pFile, String logFile, String minChanWidths) throws FileNotFoundException, circuit.parser.conlist.ParseException{
		//Input guards
		if(upperBoundaryChannelWidth<1 || lowerBoundaryChannelWidth<1){
			System.out.println("Error in findMinimalChannelWidthBinSearch: Boundaries should be greater than 0.");
			writeToFile(netFile+": Error: Boundaries should be greater than 0.\n", minChanWidths);
			return -2;
		}
		if(upperBoundaryChannelWidth<lowerBoundaryChannelWidth){
			System.out.println("Error in findMinimalChannelWidthBinSearch: Upper boundary for the channel width is lower than the lower boundary.");
			writeToFile(netFile+": Error: Upper boundary for the channel width is lower than the lower boundary.\n", minChanWidths);
			return -2;
		}
		//Route with upper boundary channel width
		System.out.println("Route with upper boundary channel width of "+upperBoundaryChannelWidth);
		int ritsup = test.routeOnly(upperBoundaryChannelWidth, routingIterations, netFile, pFile, logFile);
		if(ritsup<=0){
			System.out.println("Warning: Routing with upper boundary, "+upperBoundaryChannelWidth+", for the channel width failed.");
			writeToFile(netFile+": Warning: Routing with upper boundary, "+upperBoundaryChannelWidth+", for the channel width failed.\n", minChanWidths);
			return -1;
		}
		//Route with lower boundary channel width
		System.out.println("Route with lower boundary channel width of "+lowerBoundaryChannelWidth);
		int ritslow = test.routeOnly(lowerBoundaryChannelWidth, routingIterations, netFile, pFile, logFile);
		if(ritslow>0){
			System.out.println("Warning: Routing with lower boundary for the channel width succeeded, no certainity about the minimimum channel width. Channel width could be lower than lower boundary.");
			writeToFile(netFile+": Warning: Routing with lower boundary, "+lowerBoundaryChannelWidth+", for the channel width succeeded, no certainity about the minimimum channel width. Channel width could be lower than lower boundary.\n", minChanWidths);
			return lowerBoundaryChannelWidth;
		}
		int low = lowerBoundaryChannelWidth;
		int high = upperBoundaryChannelWidth;
		while ((high-low)>1){
			int cw = low + (high-low)/2;
			System.out.println("low "+low+", high "+high+", routing with cw "+cw);
			int rits = test.routeOnly(cw, routingIterations, netFile, pFile, logFile);
			if(rits>0){
				System.out.println("Routing succeeded, high = "+cw);
				high = cw;
			}else{
				System.out.println("Routing failed, low = "+cw);
				low = cw;
			}
		}
		System.out.println("Minimum channel width found: "+high);
		writeToFile(netFile+": Minimum channel width "+high+"\n", minChanWidths);
		return high;
	}

}
