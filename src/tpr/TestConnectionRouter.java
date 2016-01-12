package tpr;

import global.GlobalConstants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Random;

import Bundler.Bundler;
import Plaatsers.BoundingBoxBundleCC;
import Plaatsers.CostCalculator;
import Plaatsers.PlacementManipulator;
import Plaatsers.PlacementManipulatorIOCLB;
import Plaatsers.PlacementManipulatorSharingIOCLB2;
import Plaatsers.RSharingPlace;
import Plaatsers.Vplace;
import PlacementParser.ParseException;
import PlacementParser.Placement;
import PlacementParser.ReadPlacement;
import Sharing.SharingReader;
import TRouter.ConnectionRouter;
import TRouter.VerificationModule;
import architecture.Architecture2D;
import architecture.FourLutSanitizedWiltonUnidirectional;
import circuit.Circuit;
import circuit.parser.conlist_ff.Readconlist;
import circuit.writer.ConlistWriter;
import circuit.writer.PlacementWriter;

public class TestConnectionRouter {
	public void determineFPGADimensions(int K, int L, String netfile) throws FileNotFoundException, Exception {
		//Read in con netlist.
		System.out.println("Read in "+netfile);
		Readconlist parser=new Readconlist(new FileInputStream(new File(netfile)));
		Circuit c=parser.read(K, L);
		int CLBs = c.clbs.size();
		double calcDim = Math.ceil(Math.sqrt(1.20*CLBs));
		System.out.println("CLBs = "+CLBs+", calculated dimension = "+calcDim+", Inputs = "+c.inputs.size()+"/"+(4*calcDim)+", Outputs = "+c.outputs.size()+"/"+(4*calcDim));
	}	
	public int findMinimalChannelWidth(int startChannelWidth, int routingIterations, int K, int L, String netFile, String pFile, String logFile, String minChanWidths) throws FileNotFoundException, Exception{
		int cw = startChannelWidth;
		int rits = 1;
		int legals = 0;
		for(;rits>0&&cw>0;cw--){
			rits = routeOnly(cw, routingIterations, K, L, netFile, pFile, logFile);
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

	private Circuit readCircuit(int K, int L, String netFile, String sharingFile) throws FileNotFoundException, Exception {
		//Read in con netlist.
		System.out.println("Read in con netlist "+netFile+".. ");
		Readconlist parser=new Readconlist(new FileInputStream(new File(netFile)));
		Circuit c=parser.read(K, L);
		c.createBundles();
		System.out.println("No. of Clbs: "+c.clbs.size());
	
		if(sharingFile != null)
			SharingReader.readClbSharing(c, sharingFile);
		else
			SharingReader.applyNoSharing(c);
		return c;
	}
	
	private void architectureCheck(int height, int width, Circuit c, boolean lutSharing) {
		// Architecture Check
		if (!lutSharing && c.numClbs() > (height * width)) {
			System.out
					.println("Error: Dimensions of FPGA are too small to realize circuit.");
			System.out
					.println("Number of CLBs in the circuit exceeds the number of physical CLBs present on the FPGA.");
			System.out.println("Min. sqrt(height*width)=" + Math.sqrt(c.numClbs()));
			throw new RuntimeException();
		}
		if (c.numInputs() > (2 * (height + width))) {
			System.out
					.println("Error: Dimensions of FPGA are too small to realize circuit.");
			System.out
					.println("Number of inputs in the circuit exceeds the number of physical IOBs present on the FPGA.");
			System.out.println("Min. height+width=" + c.numInputs() / 2.);
			throw new RuntimeException();
		}
		if (c.numOutputs() > (2 * (height + width))) {
			System.out
					.println("Error: Dimensions of FPGA are too small to realize circuit.");
			System.out
					.println("Number of outputs in the circuit exceeds the number of physical IOBs present on the FPGA.");
			System.out.println("Min. height+width=" + c.numOutputs() / 2.);
			throw new RuntimeException();
		}
	}
	
	private class CircuitArchitecture { 
		final Circuit circuit;
		final Architecture2D architecture;
		public CircuitArchitecture(Circuit circuit, Architecture2D architecture) {
			this.circuit = circuit;
			this.architecture = architecture;
		}
	}
	
	private CircuitArchitecture placementSub(Circuit c, int height, int width, int placementEffort, int K, int L, int channelWidth, String netFile, String sharingFile, String flatNetFile, String pFile, String logFile) throws FileNotFoundException {
		//Bundler
		System.out.println("Start bundling.. ");
		Bundler blr = new Bundler(c);
		final long startTime0 = System.nanoTime();
		final long endTime0;
		try {
			blr.bundle();
		} finally {
		  endTime0 = System.nanoTime();
		}
		final long duration0 = endTime0 - startTime0;
		System.out.println("Runtime: "+(duration0/1.0E9));
		System.out.println("Connections are bundled in "+c.bundles.size()+" bundles");
//		blr.printBundles();
		
		
		//Aanmaken van de architectuur.
		System.out.println("Aanmaken van de architectuur.");
		Architecture2D a = new FourLutSanitizedWiltonUnidirectional(width,height,2,K,L);
	
		Random rand = new Random(1);
		PlacementManipulator pm0 = new PlacementManipulatorSharingIOCLB2(a,c,rand);
		BoundingBoxBundleCC bbbcc = new BoundingBoxBundleCC(c);

		//Random placement
		new RSharingPlace(c, a, rand).placeCLBsandIOs();
		pm0.placementCLBsConsistencyCheck();
		System.out.println("Total Cost random placement BBbundle cc: "+bbbcc.calculateTotalCost());			
		
		//Time placement process
		Vplace placer1= new Vplace(pm0,bbbcc);
		final long startTime1 = System.nanoTime();
		final long endTime1;
		//try {//
			placer1.place(placementEffort);
		//} finally {//
		  endTime1 = System.nanoTime();
		//}//
		pm0.placementCLBsConsistencyCheck();
		final long duration1 = endTime1 - startTime1;
		System.out.println("Runtime: "+(duration1/1.0E9));
		System.out.println("Total BB Cost after placement with bb bundle cc: "+bbbcc.calculateTotalCost());
		
		System.out.println("Clb sites occupied: " + a.numOccupiedClbSites()
				+ " out of: " + a.numAvailableClbSites() + " to place: "
				+ c.numClbs() + " clbs");
		
		Architecture2D flatArchitecture = new FourLutSanitizedWiltonUnidirectional(width,height,channelWidth,K,L);
		Circuit flatCircuit = c.flattenClbSharing(flatArchitecture);
		flatCircuit.createBundles();
		new PlacementManipulatorIOCLB(flatArchitecture, flatCircuit, rand).placementCLBsConsistencyCheck();

		//Not really needed
		System.out.println("Redo bundling...");
		new Bundler(flatCircuit).bundle();
		CostCalculator flatCalculator = new BoundingBoxBundleCC(flatCircuit);
		System.out.println("Total BB Cost after flatening circuit: "+flatCalculator.calculateTotalCost());
		
		//Dump flattened circuit
		if(flatNetFile != null)
			ConlistWriter.writeCircuit(flatCircuit, flatNetFile);
		
		//Dump placement
		if(pFile != null)
			PlacementWriter.dumpPlacement(flatCircuit, pFile, height, width);
			
		String line = "Placement effort: " + placementEffort + ", \nNet file: "
				+ netFile + ", \nSharing file: "
				+ (sharingFile == null ? "None" : sharingFile) + ",\n\t"
				+ "Bundling: rt = " + (duration0 / 1.0E9) + ", "
				+ c.bundles.size() + " bundles" + "\n\t" + "Placement: rt = "
				+ (duration1 / 1.0E9) + ", Total BBB Cost: "
				+ bbbcc.calculateTotalCost() + "\n\t"
				+ "Bundles after flattening: " + flatCircuit.bundles.size()
				+ " bundles" + "\n\t" + "Clbs used: "
				+ flatArchitecture.numOccupiedClbSites()
				+ ", Total BBB Cost after flattening: "
				+ flatCalculator.calculateTotalCost() + "\n\n";
	
		writeToFile(line,logFile);
		return new CircuitArchitecture(flatCircuit, flatArchitecture);
	}
	
	private int routeSub(Circuit c, Architecture2D a, int height, int width, int channelWidth, int routingIterations, int K, int L, String netFile, String sharingFile, String logFile, boolean verifyFlag) {
		int its = -1;
		ConnectionRouter router = new ConnectionRouter(a, c);
		final long startTime2 = System.nanoTime();
		final long endTime2;
		//try {
			its = router.crouteHwithConSmallestBBFirstOrder(routingIterations);
		//} finally {
		  endTime2 = System.nanoTime();
		//}
		final long duration2 = endTime2 - startTime2;
		System.out.println("Runtime: "+(duration2/1.0E9));
		System.out.println("Number of wires:  "+c.totalWires());
		
		if(verifyFlag && c.totalWires()>0){
			VerificationModule vfmodule = new VerificationModule(c);
			System.out.println("Starting verification... "+vfmodule.verificateConnectionRouterEQS());
		}
			
		String line = "Channel width: " + channelWidth
				+ ", max routing iterations: " + routingIterations
				+ ", \nNet file: " + netFile + ", \nSharing file: "
				+ (sharingFile == null ? "None" : sharingFile) + ",\n\t"
				+ "Routing: rt = " + (duration2 / 1.0E9) + ", wires = "
				+ c.totalWires() + ", iterations = " + its + "\n\n";

		writeToFile(line,logFile);
		return its;
	}
	
	public int par(int height, int width, int channelWidth, int placementEffort, int routingIterations, int K, int L, String netFile, String sharingFile, String flatNetFile, String pFile, String logFile) throws FileNotFoundException, Exception {
		Circuit c = readCircuit(K, L, netFile, sharingFile);
		
		architectureCheck(height, width, c, sharingFile!=null);
		
		CircuitArchitecture ca = placementSub(c, width, height, placementEffort, K, L, channelWidth, netFile, sharingFile, flatNetFile, pFile, logFile);

		c = ca.circuit;
		Architecture2D a = ca.architecture;
		
		return routeSub(c, a, height, width, channelWidth, routingIterations, K, L, netFile, sharingFile, logFile, false);
	}
	public void placementOnly(int height, int width, int placementEffort, int K, int L, String netFile, String sharingFile, String flatNetFile, String pFile, String logFile) throws FileNotFoundException, Exception{
		Circuit c = readCircuit(K, L, netFile, sharingFile);
		
		architectureCheck(height, width, c, sharingFile!=null);

		placementSub(c, width, height, placementEffort, K, L, 2, netFile, sharingFile, flatNetFile, pFile, logFile);
	}

 	public int routeOnly(int channelWidth, int routingIterations, int K, int L, String netFile, String pFile, String logFile) throws FileNotFoundException, Exception{
		Circuit c = readCircuit(K, L, netFile, null);

		// Read placement
		ReadPlacement placementParser=new ReadPlacement(new FileInputStream(pFile));
		Placement p = null;
		try {
			p = placementParser.read();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int height = p.height;
		int width = p.width;
		
		architectureCheck(height, width, c, false);
		
		//Aanmaken van de architectuur.
		System.out.println("Aanmaken van de architectuur.");
		Architecture2D a = new FourLutSanitizedWiltonUnidirectional(width,height,channelWidth,K,L);
		
		//Apply placement
		c.applyPlacement(p, a);
		
		return routeSub(c, a, height, width, channelWidth, routingIterations, K, L, netFile, null, logFile, GlobalConstants.verifyRoutingDuringMCWFlag);
	}
 	
	public int findMinimalChannelWidthBinSearch(int upperBoundaryChannelWidth, int lowerBoundaryChannelWidth, int routingIterations, int K, int L, String netFile, String pFile, String logFile, String minChanWidths) throws FileNotFoundException, Exception{
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
		int ritsup = routeOnly(upperBoundaryChannelWidth, routingIterations, K, L, netFile, pFile, logFile);
		if(ritsup<=0){
			System.out.println("Warning: Routing with upper boundary, "+upperBoundaryChannelWidth+", for the channel width failed.");
			writeToFile(netFile+": Warning: Routing with upper boundary, "+upperBoundaryChannelWidth+", for the channel width failed.\n", minChanWidths);
			return -1;
		}
		//Route with lower boundary channel width
		System.out.println("Route with lower boundary channel width of "+lowerBoundaryChannelWidth);
		int ritslow = routeOnly(lowerBoundaryChannelWidth, routingIterations, K, L, netFile, pFile, logFile);
		if(ritslow>0){
			System.out.println("Warning: Routing with lower boundary for the channel width succeeded, no certainity about the minimimum channel width. Channel width could be lower than lower boundary.");
			writeToFile(netFile+": Warning: Routing with lower boundary, "+lowerBoundaryChannelWidth+", for the channel width succeeded, no certainity about the minimimum channel width. Channel width could be lower than lower boundary.\n", minChanWidths);
			return lowerBoundaryChannelWidth;
		}
		int low = lowerBoundaryChannelWidth;
		int high = upperBoundaryChannelWidth;
		while ((high-low)>1){
			double ratio = 2./3.;
			int cw = (int)(low*(1-ratio) + high*ratio);
			if(cw <= low) cw = low + 1;
			if(cw >= high) cw = high - 1;
			System.out.println("low "+low+", high "+high+", routing with cw "+cw);
			int rits = routeOnly(cw, routingIterations, K, L, netFile, pFile, logFile);
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
}
