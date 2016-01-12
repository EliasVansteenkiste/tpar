package tpr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import Bundler.Bundler;
import Plaatsers.BoundingBoxBundleCC;
import Plaatsers.PlacementManipulatorIOCLB;
import Plaatsers.Rplace;
import Plaatsers.Vplace;
import PlacementParser.Placement;
import PlacementParser.ReadPlacement;
import TRouter.ConnectionRouterBB;
import TRouter.VerificationModule;
import architecture.FourLutSanitizedDisjoint;
import circuit.Circuit;
import circuit.parser.conlist.ParseException;
import circuit.parser.conlist.Readconlist;
import circuit.writer.PlacementWriter;

import com.lexicalscope.jewel.cli.CliFactory;

public class ExportTPAR {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws ParseException 
	 * @throws architecture.ParseException 
	 * @throws PlacementParser.ParseException 
	 */
	public static void main(String[] args) throws FileNotFoundException, ParseException, PlacementParser.ParseException {
		System.out.println("Version information: TPack:advanced bundling, TPlace:SA with BB Bundle Estimation, TRoute:CrouteHwithConSmallestBBFirstOrder");
		/*
		String[] testArgs1 = {	"-p", "temp.p",	
        							"-n", "testcases/BVHCell-tconmap_out.net",	
        							"-w", "8",	
        							"-h", "8",
        							"--routeOnly"};
        String[] testArgs2 = { 	"--help" };
		*/
        final Arguments cli = CliFactory.parseArguments(Arguments.class, args);	

        if(cli.getPlacementOnly()){
        		if(cli.getRouteOnly()){
        			System.out.println("Error: conflicting command line options; 'Route only' and 'Placement only'");
        		}else{
        			placementOnly(cli.getHeight(),cli.getWidth(),cli.getPlacementEffort(),cli.getNetFilename(),cli.getPlacementFileName(),cli.getLogFileName());
        			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        			Date date = new Date();
        			writeToFile("---timeStamp---"+dateFormat.format(date)+"\n", cli.getLogFileName());	
        		}
        }else if(cli.getRouteOnly()){
        		routeOnly(cli.getHeight(),cli.getWidth(),cli.getChannelWidth(),cli.getNrOfRoutingIterations(),cli.getNetFilename(),cli.getPlacementFileName(),cli.getLogFileName());
    			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    			Date date = new Date();
    			writeToFile("---timeStamp---"+dateFormat.format(date)+"\n", cli.getLogFileName());	
        }else{
    			par(cli.getHeight(), cli.getWidth(), cli.getChannelWidth(), cli.getPlacementEffort(), cli.getNrOfRoutingIterations(), cli.getNetFilename(), cli.getPlacementFileName(), cli.getLogFileName());
    			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    			Date date = new Date();
    			writeToFile("---timeStamp---"+dateFormat.format(date)+"\n", cli.getLogFileName());
        }
	}
	public static int par(int height, int width, int channelWidth, int placementEffort, int routingIterations, String netFile, String pFile, String logFile) throws FileNotFoundException, circuit.parser.conlist.ParseException{
		//Read in con netlist.
		System.out.println("Read in con netlist "+netFile+".. ");
		Readconlist parser=new Readconlist(new FileInputStream(new File(netFile)));
		Circuit c=parser.read();
		c.createBundles();
		
		//Architecture Check
		if(c.clbs.size()>(height*width)){
			System.out.println("Error: Dimensions of FPGA are too small to realize circuit.\nNumber of CLBs in the circuit exceeds the number of physical CLBs present on the FPGA.");
		}
		if(c.inputs.size()>(2*(height+width))){
			System.out.println("Error: Dimensions of FPGA are too small to realize circuit.\nNumber of inputs in the circuit exceeds the number of physical IOBs present on the FPGA.");
		}
		if(c.outputs.size()>(2*(height+width))){
			System.out.println("Error: Dimensions of FPGA are too small to realize circuit.\nNumber of outputs in the circuit exceeds the number of physical IOBs present on the FPGA.");
		}
		
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
		FourLutSanitizedDisjoint a = new FourLutSanitizedDisjoint(width,height,channelWidth);
		
		Random rand = new Random(1);
		PlacementManipulatorIOCLB pm0 = new PlacementManipulatorIOCLB(a,c,rand);
		BoundingBoxBundleCC bbbcc = new BoundingBoxBundleCC(c);

		
		//Random placement
		Rplace.placeCLBsandIOs(c, a, rand);
		pm0.placementCLBsConsistencyCheck();
		System.out.println("Total Cost random placement BBbundle cc: "+bbbcc.calculateTotalCost());			
		
		//Time placement process
		Vplace placer1= new Vplace(pm0,bbbcc);
		final long startTime1 = System.nanoTime();
		final long endTime1;
		try {
			placer1.place(placementEffort);
		} finally {
		  endTime1 = System.nanoTime();
		}
		final long duration1 = endTime1 - startTime1;
		System.out.println("Runtime: "+(duration1/1.0E9));
		System.out.println("Total BB Cost after placement with bb bundle cc: "+bbbcc.calculateTotalCost());
		
		//Dump placement
		PlacementWriter.dumpPlacement(c, pFile);
		
		int its = -1;
		ConnectionRouterBB router = new ConnectionRouterBB(c);
		final long startTime2 = System.nanoTime();
		final long endTime2;
		try {
			its = router.crouteHwithConSmallestBBFirstOrder(a, routingIterations);
		} finally {
		  endTime2 = System.nanoTime();
		}
		final long duration2 = endTime2 - startTime2;
		System.out.println("Runtime: "+(duration2/1.0E9));
		System.out.println("Number of wires:  "+c.totalWires());
		
		if(c.totalWires()>0){
			VerificationModule vfmodule = new VerificationModule(c);
			System.out.println("Starting verification... "+vfmodule.verificateConnectionRouter());
		}
			
		String line = "Placement effort: "+placementEffort+",\tchannel width: "+channelWidth+"\t, routing iterations: "+routingIterations+",\tnet file: " + netFile
				+ "\n\t" + "Bundling: rt = "+(duration0/1.0E9)+", "+c.bundles.size()+" bundles"
				+ "\n\t" + "Placement: rt = "+(duration1/1.0E9)+", Total BBB Cost: "+bbbcc.calculateTotalCost()
				+ "\n\t" + "Routing: rt = "+(duration2/1.0E9)+", wires = "+c.totalWires()+"\n";
		
		writeToFile(line,logFile);
		
		return its;
	}	
	public static int routeOnly(int height, int width, int channelWidth, int routingIterations, String netFile, String pFile, String logFile) throws FileNotFoundException, circuit.parser.conlist.ParseException, PlacementParser.ParseException{
		//Read in con netlist.
		System.out.println("Read in con netlist "+netFile+".. ");
		Readconlist parser=new Readconlist(new FileInputStream(new File(netFile)));
		Circuit c=parser.read();
		c.createBundles();
		
		//Architecture Check
		if(c.clbs.size()>(height*width)){
			System.out.println("Error: Dimensions of FPGA are too small to realize circuit.\nNumber of CLBs in the circuit exceeds the number of physical CLBs present on the FPGA.");
		}
		if(c.inputs.size()>(2*(height+width))){
			System.out.println("Error: Dimensions of FPGA are too small to realize circuit.\nNumber of inputs in the circuit exceeds the number of physical IOBs present on the FPGA.");
		}
		if(c.outputs.size()>(2*(height+width))){
			System.out.println("Error: Dimensions of FPGA are too small to realize circuit.\nNumber of outputs in the circuit exceeds the number of physical IOBs present on the FPGA.");
		}
		
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
		FourLutSanitizedDisjoint a = new FourLutSanitizedDisjoint(width,height,channelWidth);

		ReadPlacement plaats_parser=new ReadPlacement(new FileInputStream(pFile));
		Placement p=plaats_parser.read();
		c.applyPlacement(p, a);
				
		int its = -1;
		ConnectionRouterBB router = new ConnectionRouterBB(c);
		final long startTime2 = System.nanoTime();
		final long endTime2;
		try {
			its = router.crouteHwithConSmallestBBFirstOrder(a, routingIterations);
		} finally {
		  endTime2 = System.nanoTime();
		}
		final long duration2 = endTime2 - startTime2;
		System.out.println("Runtime: "+(duration2/1.0E9));
		System.out.println("Number of wires:  "+c.totalWires());
		
		if(c.totalWires()>0){
			VerificationModule vfmodule = new VerificationModule(c);
			System.out.println("Starting verification... "+vfmodule.verificateConnectionRouter());
		}
			
		String line = "Channel Width:"+channelWidth+",\tRouting Iterations:"+routingIterations+ "\t,net file:" + netFile
				+ "\n\t" + "Bundling: rt = "+(duration0/1.0E9)+", "+c.bundles.size()+" bundles"
				+ "\n\t" + "Routing: rt = "+(duration2/1.0E9)+", wires = "+c.totalWires()+"\n";
		
		writeToFile(line,logFile);
		
		return its;
	}	
	public static void placementOnly(int height, int width, int placementEffort, String netFile, String pFile, String logFile) throws FileNotFoundException, circuit.parser.conlist.ParseException{
		//Read in con netlist.
		System.out.println("Read in con netlist "+netFile+".. ");
		Readconlist parser=new Readconlist(new FileInputStream(new File(netFile)));
		Circuit c=parser.read();
		c.createBundles();
		
		//Architecture Check
		if(c.clbs.size()>(height*width)){
			System.out.println("Error: Dimensions of FPGA are too small to realize circuit.\nNumber of CLBs in the circuit exceeds the number of physical CLBs present on the FPGA.");
		}
		if(c.inputs.size()>(2*(height+width))){
			System.out.println("Error: Dimensions of FPGA are too small to realize circuit.\nNumber of inputs in the circuit exceeds the number of physical IOBs present on the FPGA.");
		}
		if(c.outputs.size()>(2*(height+width))){
			System.out.println("Error: Dimensions of FPGA are too small to realize circuit.\nNumber of outputs in the circuit exceeds the number of physical IOBs present on the FPGA.");
		}
		
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
		FourLutSanitizedDisjoint a = new FourLutSanitizedDisjoint(width,height,1);
		
		Random rand = new Random(1);
		PlacementManipulatorIOCLB pm0 = new PlacementManipulatorIOCLB(a,c,rand);
		BoundingBoxBundleCC bbbcc = new BoundingBoxBundleCC(c);

		
		//Random placement
		Rplace.placeCLBsandIOs(c, a, rand);
		pm0.placementCLBsConsistencyCheck();
		System.out.println("Total Cost random placement BBbundle cc: "+bbbcc.calculateTotalCost());			
		
		//Time placement process
		Vplace placer1= new Vplace(pm0,bbbcc);
		final long startTime1 = System.nanoTime();
		final long endTime1;
		try {
			placer1.place(placementEffort);
		} finally {
		  endTime1 = System.nanoTime();
		}
		final long duration1 = endTime1 - startTime1;
		System.out.println("Runtime: "+(duration1/1.0E9));
		System.out.println("Total BB Cost after placement with bb bundle cc: "+bbbcc.calculateTotalCost());
		
		//Dump placement
		PlacementWriter.dumpPlacement(c, pFile);
			
		String line = "Placement effort: "+placementEffort+",\tnet file: " + netFile
				+ "\n\t" + "Bundling: rt = "+(duration0/1.0E9)+", "+c.bundles.size()+" bundles"
				+ "\n\t" + "Placement: rt = "+(duration1/1.0E9)+", Total BBB Cost: "+bbbcc.calculateTotalCost();
		
		writeToFile(line,logFile);
	}	
	public static void writeToFile(String str, String filename){
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
