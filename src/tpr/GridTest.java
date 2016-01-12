package tpr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.Map.Entry;

import circuit.Circuit;
import circuit.Clb;
import circuit.Connection;
import circuit.Pin;
import circuit.parser.conlist.ParseException;
import circuit.parser.conlist.Readconlist;

public class GridTest {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws ParseException 
	 * @throws architecture.ParseException 
	 * @throws PlacementParser.ParseException 
	 * @throws circuit.parser.conlist.ParseException 
	 * @throws circuit.parser.netlist.ParseException 
	 */
	public static void main(String[] args) throws FileNotFoundException, Exception {
		//determineDimensions();
		//place();
		//minChannelWidth();
		//route();
		parTLUT();
	}
	
	public static void par() throws FileNotFoundException, Exception {
		TestConnectionRouter test = new TestConnectionRouter();
		test.par(8, 8, 8, 10, 30, "grid/grid_2x2_fpga.net", "testcases/BVHCell-tconmap_out..p", "testcases.log");
	}
	public static void determineDimensions() throws FileNotFoundException, Exception {
		TestConnectionRouter test = new TestConnectionRouter();
//		test.DetermineFPGADimensions("grid/grid_2x2_fpga.net");
		test.determineFPGADimensions("grid/grid_4x4_fpga.net");
		test.determineFPGADimensions("grid/grid_8x8_fpga.net");
//		test.DetermineFPGADimensions("grid/grid_10x10_fpga.net");
//		test.DetermineFPGADimensions("grid/grid_2x2_tmap.net");
		test.determineFPGADimensions("grid/grid_4x4_tmap.net");
		test.determineFPGADimensions("grid/grid_8x8_tmap.net");
//		test.DetermineFPGADimensions("grid/grid_10x10_tmap.net");
//		test.DetermineFPGADimensions("grid/grid_12x12_tmap.net");
		test.determineFPGADimensions("grid/grid_14x14_fpga.net");
		test.determineFPGADimensions("grid/grid_14x14_tmap.net");
	}
	
	public static void place() throws FileNotFoundException, Exception {
		TestConnectionRouter test = new TestConnectionRouter();
		
//		test.placementOnly(19, 19, 10, "grid/grid_2x2_fpga.net", "grid/grid_2x2_fpga.place", "grid/grid.log");
//		test.placementOnly(38, 38, 10, "grid/grid_4x4_fpga.net", "grid/grid_4x4_fpga.place", "grid/grid.log");
//		test.placementOnly(75, 75, 10, "grid/grid_8x8_fpga.net", "grid/grid_8x8_fpga.place", "grid/grid.log");
//		test.placementOnly(93, 93, 10, "grid/grid_10x10_fpga.net", "grid/grid_10x10_fpga.place", "grid/grid.log");
		test.placementOnly(130, 130, 10, "grid/grid_14x14_fpga.net", "grid/grid_14x14_fpga.place", "grid/grid.log");
		
//		test.placementOnly(14, 14, 10, "grid/grid_2x2_tmap.net", "grid/grid_2x2_tmap.place", "grid/grid.log");
//		test.placementOnly(27, 27, 10, "grid/grid_4x4_tmap.net", "grid/grid_4x4_tmap.place", "grid/grid.log");
//		test.placementOnly(53, 53, 10, "grid/grid_8x8_tmap.net", "grid/grid_8x8_tmap.place", "grid/grid.log");
//		test.placementOnly(66, 66, 10, "grid/grid_10x10_tmap.net", "grid/grid_10x10_tmap.place", "grid/grid.log");
//		test.placementOnly(79, 79, 10, "grid/grid_12x12_tmap.net", "grid/grid_12x12_tmap.place", "grid/grid.log");
		test.placementOnly(93, 93, 10, "grid/grid_14x14_tmap.net", "grid/grid_14x14_tmap.place", "grid/grid.log");

	}
	
	public static void minChannelWidth() throws FileNotFoundException, Exception {
		TestConnectionRouter test = new TestConnectionRouter();
		
//		test.findMinimalChannelWidthBinSearch(10, 3, 30, "grid/grid_2x2_fpga.net", "grid/grid_2x2_fpga.place", "grid/grid.log", "grid/minChanWidths.log");
		test.findMinimalChannelWidthBinSearch(10, 3, 30, "grid/grid_4x4_fpga.net", "grid/grid_4x4_fpga.place", "grid/grid.log", "grid/minChanWidths.log");
		test.findMinimalChannelWidthBinSearch(8, 4, 30, "grid/grid_8x8_fpga.net", "grid/grid_8x8_fpga.place", "grid/grid.log", "grid/minChanWidths.log");
//		test.findMinimalChannelWidthBinSearch(7, 6, 30, "grid/grid_10x10_fpga.net", "grid/grid_10x10_fpga.place", "grid/grid.log", "grid/minChanWidths.log");
		//test.findMinimalChannelWidthBinSearch(7, 5, 30, "grid/grid_14x14_fpga.net", "grid/grid_14x14_fpga.place", "grid/grid.log", "grid/minChanWidths.log");
		
		
//		test.findMinimalChannelWidthBinSearch(10, 3, 30, "grid/grid_2x2_tmap.net", "grid/grid_2x2_tmap.place", "grid/grid.log", "grid/minChanWidths.log");
		test.findMinimalChannelWidthBinSearch(10, 3, 30, "grid/grid_4x4_tmap.net", "grid/grid_4x4_tmap.place", "grid/grid.log", "grid/minChanWidths.log");
		test.findMinimalChannelWidthBinSearch(8, 4, 30, "grid/grid_8x8_tmap.net", "grid/grid_8x8_tmap.place", "grid/grid.log", "grid/minChanWidths.log");
//		test.findMinimalChannelWidthBinSearch(8, 4, 30, "grid/grid_10x10_tmap.net", "grid/grid_10x10_tmap.place", "grid/grid.log", "grid/minChanWidths.log");
//		test.findMinimalChannelWidthBinSearch(8, 4, 30, "grid/grid_12x12_tmap.net", "grid/grid_12x12_tmap.place", "grid/grid.log", "grid/minChanWidths.log");
//		test.findMinimalChannelWidthBinSearch(9, 8, 30, "grid/grid_14x14_tmap.net", "grid/grid_14x14_tmap.place", "grid/grid.log", "grid/minChanWidths.log");
	}
	
	public static void route() throws FileNotFoundException, Exception {
		TestConnectionRouter test = new TestConnectionRouter();
		//test.routeOnly(10, 30, "grid/grid_14x14_tmap.net", "grid/grid_14x14_tmap.place", "grid/grid.log");
		test.routeOnly(7, 30, "grid/grid_4x4_fpga.net", "grid/grid_4x4_fpga.place", "grid/grid.log");
		test.routeOnly(8, 30, "grid/grid_8x8_fpga.net", "grid/grid_8x8_fpga.place", "grid/grid.log");
		
		test.routeOnly(7, 30, "grid/grid_4x4_tmap.net", "grid/grid_4x4_tmap.place", "grid/grid.log");
		test.routeOnly(8, 30, "grid/grid_8x8_tmap.net", "grid/grid_8x8_tmap.place", "grid/grid.log");

	}
	
	public static void parTLUT() throws FileNotFoundException, Exception {
		TestPARConrStaticCircuits test = new TestPARConrStaticCircuits();
		Circuit c = makeTLUTimplementation("grid/grid_4x4_tmap.net");
		test.determineFPGADimensions(c);
		//test.par(28, 28, 6, 10, 30, c, "grid/grid_4x4_TLUT.place", "grid/grid_4x4_TLUT.log");
		//test.placementOnly(28, 28, 10, c, "grid/grid_4x4_TLUT.place", "grid/grid_4x4_TLUT.log");
		//test.routeOnly(4, 30, c, "grid/grid_4x4_TLUT.place", "grid/grid_4x4_TLUT.log");
		c = makeTLUTimplementation("grid/grid_8x8_tmap.net");
		test.determineFPGADimensions(c);
		//test.placementOnly(57, 57, 10, c, "grid/grid_8x8_TLUT.place", "grid/grid_8x8_TLUT.log");
		//test.routeOnly(7, 30, c, "grid/grid_8x8_TLUT.place", "grid/grid_8x8_TLUT.log");
		c = makeTLUTimplementation("grid/grid_14x14_tmap.net");
		test.determineFPGADimensions(c);
		test.placementOnly(101, 101, 10, c, "grid/grid_14x14_TLUT.place", "grid/grid_14x14_TLUT.log");
		//test.routeOnly(7, 30, c, "grid/grid_14x14_TLUT.place", "grid/grid_14x14_TLUT.log");
		//test.par(8, 8, 10, 10, 30, "clos 4x4 tcon/clos16.con", "clos 4x4 tcon/clos16con.place", "clos 4x4 tcon/clos16con.log");
		//test.par(18, 18, 18, 10, 30, "clos 4x4 tcon/clos64.con", "clos 4x4 tcon/clos64con.place", "clos 4x4 tcon/clos64con.log");
		//test.par(40, 40, 31, 10, 30, "clos 4x4 tcon/clos256.con", "clos 4x4 tcon/clos256con.place", "clos 4x4 tcon/clos64con.log");
	}
	static Circuit makeTLUTimplementation(String conList) throws FileNotFoundException, circuit.parser.conlist.ParseException{
		//Read in con netlist.
		System.out.println("Read in conlist "+conList+".. ");
		Readconlist parser=new Readconlist(new FileInputStream(new File(conList)));
		Circuit c=parser.read();
		c.createBundles();
		System.out.println("No. of Clbs in TCON implementation: "+c.clbs.size());
		int maxSize = 0;
		int no_tcons = 0;
		for(Entry<Pin,Set<Connection>> entry:c.sinkBundles.entrySet()){
			Set<Connection> sinkBundle = entry.getValue();
			System.out.print(sinkBundle.size()+", ");
			if(maxSize < sinkBundle.size()) maxSize = sinkBundle.size();
		}
		System.out.println("\nNumber of cons:"+c.cons.size());
		System.out.println("\nMaximum size for sinkBundles: "+maxSize);
		if(maxSize>5){
			System.out.println("This method is not designed for sinkBundles with more than 5 connections!");
			System.exit(2);
		}
		for(Entry<Pin,Set<Connection>> entry:c.sinkBundles.entrySet()){
			Set<Connection> sinkBundle = entry.getValue();
			if(sinkBundle.size()>1 && sinkBundle.size()<5){
				Pin sink = entry.getKey();
				Clb clb = new Clb("CLB_"+sink.name, 1, 4);
				Connection clb_sink = new Connection(clb.output[0], sink, sink);
				c.cons.add(clb_sink);
				c.clbs.put(clb.name, clb);
				int i=0;
				for(Connection con:sinkBundle){
					Pin source = con.source;
					c.cons.remove(con);
					Connection source_clb = new Connection(source,clb.input[0],clb.input[0]);
					i++;
					c.cons.add(source_clb);
				}
			}else if(sinkBundle.size()>4){
				Pin sink = entry.getKey();
				Clb clb = new Clb("CLB_"+sink.name, 1, 4);
				Connection clb_sink = new Connection(clb.output[0], sink, sink);
				c.cons.add(clb_sink);
				c.clbs.put(clb.name, clb);
				Clb intermed = new Clb("intermediairy_"+sink.name,1,4);
				Connection intermed_clb = new Connection(intermed.output[0],clb.input[0],clb.input[0]);
				c.cons.add(intermed_clb);
				c.clbs.put(intermed.name, intermed);
				int i=0;
				for(Connection con:sinkBundle){
					Pin source = con.source;
					c.cons.remove(con);
					if(i<3){
						Connection source_clb = new Connection(source,clb.input[i+1],clb.input[i+1]);
						c.cons.add(source_clb);
					}else{
						Connection source_intermed = new Connection(source,intermed.input[i-3],clb.input[i-3]);
						c.cons.add(source_intermed);
					}
				}
			}
		}
		c.bundlesCreated = false;
		c.createBundles();
		System.out.println("No. of Clbs in TLUT implementation: "+c.clbs.size());
		return c;
	}
	

}
