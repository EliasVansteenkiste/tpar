package tpr;

import java.io.FileNotFoundException;

public class Test_VCGRA_MAC {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws FileNotFoundException,
			Exception {
				parMAC_PE_float();
				placeTestCases();
				mcwTestCases();
	}

	private static void placeOnly(int height, int width, int placementEffort,
			int K, int L, TestConnectionRouter test, String dir, String base, boolean sharing)
			throws FileNotFoundException, Exception {
		String netFile = dir + base + ".net";
		String sharingFile = sharing ? dir + "sharing.txt" : null;
		String flatNetFile = sharing ? dir + base + "_flat.net" : null;
		String pFile = dir + base + (sharing ? "_out.p" : "_out_ns.p");
		String logFile = dir + "../" + base + "_place.log"; //dir + base + ".log";
		test.placementOnly(height, width, placementEffort, K, L, netFile,
				sharingFile, flatNetFile, pFile, logFile);
	}
	
	private static void routeOnly(int channelWidth, int routingIterations, int K, int L,
			TestConnectionRouter test, String dir, String base, boolean sharing)
			throws FileNotFoundException, Exception {
		String netFile = dir + base + (sharing ? "_flat.net" : ".net");
		String pFile = dir + base + (sharing ? "_out.p" : "_out_ns.p");
		String logFile = dir + "../route.log";
		test.routeOnly(channelWidth, routingIterations, K, L, netFile, pFile,
				logFile);
	}
	
	private static void par(int height, int width, int placementEffort, int channelWidth, int routingIterations,
			int K, int L, TestConnectionRouter test, String dir, String base)
			throws FileNotFoundException, Exception {
		test.par(height, width, channelWidth, placementEffort,
				routingIterations, K, L, dir + base + ".net", dir
						+ "sharing.txt", dir + base + "_flat.net", dir + base
						+ "_out.p", dir + base + ".log");
	}

	private static void mcw(int upperBoundaryChannelWidth,
			int lowerBoundaryChannelWidth, int routingIterations, int K, int L,
			TestConnectionRouter test, String dir, String base, boolean sharing)
			throws FileNotFoundException, Exception {
		String netFile = dir + base + (sharing ? "_flat.net" : ".net");
		String pFile = dir + base + (sharing ? "_out.p" : "_out_ns.p");
		String logFile = dir + base + "_mcw.log";
		String mcwFile = "mcw.log";
		//dir + base + "_mcw.log"
		int mcw = test.findMinimalChannelWidthBinSearch(upperBoundaryChannelWidth,
				lowerBoundaryChannelWidth, routingIterations, K, L, 
				netFile,
				pFile, 
				logFile, 
				mcwFile);
		if(mcw>0)
			routeOnly((int)Math.round(mcw * 1.2), routingIterations, K, L, test, dir, base, sharing);
	}

	public static void placeTestCases() throws FileNotFoundException, Exception {
		TestConnectionRouter test = new TestConnectionRouter();
		int placementEffort = 10;

		
		placeOnly(50, 50, placementEffort, 4, 2, test,
				"testcases/MAC_PE_float_LUT_conventional4/",
				"MAC_PE_float", false);
		
		placeOnly(50, 50, placementEffort, 4, 2, test,
				"testcases/MAC_PE_float_TCON_conventional4/",
				"MAC_PE_float", false);
		
		placeOnly(50, 50, placementEffort, 4, 2, test,
				"testcases/MAC_PE_float_TLC_conventional4/",
				"MAC_PE_float", false);
		
		placeOnly(50, 50, placementEffort, 4, 2, test,
				"testcases/MAC_PE_float_TLUT_conventional4/",
				"MAC_PE_float", false);
	}
	
	public static void mcwTestCases() throws FileNotFoundException, Exception {
		TestConnectionRouter test = new TestConnectionRouter();
		int max_chan_width = 20;
		int min_chan_width = 3;
		int routingEffort = 30;


		mcw(max_chan_width, min_chan_width, routingEffort, 4, 2, test,
				"testcases/MAC_PE_float_LUT_conventional4/",
				"MAC_PE_float", false);
			mcw(max_chan_width, min_chan_width, routingEffort, 4, 2, test,
				"testcases/MAC_PE_float_TCON_conventional4/",
				"MAC_PE_float", false);
		mcw(max_chan_width, min_chan_width, routingEffort, 4, 2, test,
				"testcases/MAC_PE_float_TLC_conventional4/",
				"MAC_PE_float", false);
		mcw(max_chan_width, min_chan_width, routingEffort, 4, 2, test,
				"testcases/MAC_PE_float_TLUT_conventional4/",
				"MAC_PE_float", false);
		
		test.findMinimalChannelWidthBinSearch(50, 50, 30, 4, 2,
				"testcases/MAC_PE_float_LUT_conventional4/MAC_PE_float.net", "testcases/MAC_PE_float_LUT_conventional4/MAC_PE_float.p",
				"testcases/MAC_PE_float_LUT_conventional4/MAC_PE_float.log", "testcases/MAC_PE_float_LUT_conventional4/MAC_PE_float.log");

		test.findMinimalChannelWidthBinSearch(50, 50, 30, 4, 2,
				"testcases/MAC_PE_float_TCON_conventional4/MAC_PE_float.net", "testcases/MAC_PE_float_TCON_conventional4/MAC_PE_float.p",
				"testcases/MAC_PE_float_TCON_conventional4/MAC_PE_float.log", "testcases/MAC_PE_float_TCON_conventional4/MAC_PE_float.log");
		
		test.findMinimalChannelWidthBinSearch(50, 50, 30, 4, 2,
				"testcases/MAC_PE_float_TLC_conventional4/MAC_PE_float.net", "testcases/MAC_PE_float_TLC_conventional4/MAC_PE_float.p",
				"testcases/MAC_PE_float_TLC_conventional4/MAC_PE_float.log", "testcases/MAC_PE_float_TLC_conventional4/MAC_PE_float.log");

		test.findMinimalChannelWidthBinSearch(50, 50, 30, 4, 2,
				"testcases/MAC_PE_float_TLUT_conventional4/MAC_PE_float.net", "testcases/MAC_PE_float_TLUT_conventional4/MAC_PE_float.p",
				"testcases/MAC_PE_float_TLUT_conventional4/MAC_PE_float.log", "testcases/MAC_PE_float_TLUT_conventional4/MAC_PE_float.log");

	}

	public static void parMAC_PE_float() throws FileNotFoundException, Exception {
		TestConnectionRouter test = new TestConnectionRouter();
		test.par(50, 50, 10, 10, 30, 4, 2, "testcases/MAC_PE_float_LUT_conventional4/MAC_PE_float.net",null,null,
				 "testcases/MAC_PE_float_LUT_conventional4/MAC_PE_float.p", "testcases/MAC_PE_float_LUT_conventional4/MAC_PE_float_par.log");
				 
		test.par(50, 50, 10, 10, 30, 4, 2, "testcases/MAC_PE_float_TCON_conventional4/MAC_PE_float.net",null,null,
				 "testcases/MAC_PE_float_TCON_conventional4/MAC_PE_float.p", "testcases/MAC_PE_float_TCON_conventional4/MAC_PE_float_par.log");
				 
		test.par(50, 50, 10, 10, 30, 4, 2, "testcases/MAC_PE_float_TLC_conventional4/MAC_PE_float.net",null,null,
				 "testcases/MAC_PE_float_TLC_conventional4/MAC_PE_float.p", "testcases/MAC_PE_float_TLC_conventional4/MAC_PE_float_par.log");
				 
		test.par(50, 50, 10, 10, 30, 4, 2, "testcases/MAC_PE_float_TLUT_conventional4/MAC_PE_float.net",null,null,
				 "testcases/MAC_PE_float_TLUT_conventional4/MAC_PE_float.p", "testcases/MAC_PE_float_TLUT_conventional4/MAC_PE_float_par.log");
	}
	

}
