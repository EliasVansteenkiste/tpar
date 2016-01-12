package tpr;

import java.io.FileNotFoundException;

public class Testcases {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws FileNotFoundException,
			Exception {
//		placeTestCases();
//		mcwTestCases();
//		routeTestCases();
		
//		parTestCases();
		parMAC_PE_float();
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

		placeOnly(20, 20, placementEffort, 4, 2, test,
				"tconmap/crossbar-results/cross16-sweep_LUT_conventional4/",
				"cross16-sweep", false);
		placeOnly(4, 4, placementEffort, 4, 2, test,
				"tconmap/crossbar-results/cross16-sweep_TLC_conventional4/",
				"cross16-sweep", false);
		placeOnly(4, 4, placementEffort, 4, 2, test,
				"tconmap/crossbar-results/cross16-sweep_TLC_conventional4/",
				"cross16-sweep", true);
		placeOnly(20, 20, placementEffort, 6, 2, test,
				"tconmap/crossbar-results/cross16-sweep_LUT_prioritycut6/",
				"cross16-sweep", false);
		placeOnly(4, 4, placementEffort, 6, 2, test,
				"tconmap/crossbar-results/cross16-sweep_TLC_prioritycut6/",
				"cross16-sweep", false);
		placeOnly(4, 4, placementEffort, 6, 2, test,
				"tconmap/crossbar-results/cross16-sweep_TLC_prioritycut6/",
				"cross16-sweep", true);
		placeOnly(11, 10, placementEffort, 4, 2, test,
				"tconmap/tlc2-results/tlc2-sweep_LUT_conventional4/",
				"tlc2-sweep", false);
		placeOnly(8, 8, placementEffort, 4, 2, test,
				"tconmap/tlc2-results/tlc2-sweep_TLC_conventional4/",
				"tlc2-sweep", false);	//49 BLE ca. without sharing
		placeOnly(5, 6, placementEffort, 4, 2, test,
				"tconmap/tlc2-results/tlc2-sweep_TLC_conventional4/",
				"tlc2-sweep", true);	//23 BLE ca. with sharing
		placeOnly(9, 8, placementEffort, 6, 2, test,
				"tconmap/tlc2-results/tlc2-sweep_LUT_prioritycut6/",
				"tlc2-sweep", false);
		placeOnly(4, 5, placementEffort, 6, 2, test,
				"tconmap/tlc2-results/tlc2-sweep_TLC_prioritycut6/",
				"tlc2-sweep", false);	//16 BLE ca. without sharing
		placeOnly(4, 4, placementEffort, 6, 2, test,
				"tconmap/tlc2-results/tlc2-sweep_TLC_prioritycut6/",
				"tlc2-sweep", true);	//14 BLE ca. with sharing
		// test.placementOnly(6, 6, placementEffort, 6, 2, "tmp.net", null, null,
		// "tconmap/tlc2_out.p", "tconmap/tlc2.log");
		placeOnly(7, 8, placementEffort, 4, 2, test,
				"tconmap/tlc4-results/tlc4-sweep_LUT_conventional4/",
				"tlc4-sweep", false); // IO limited, actual size 6x6
		placeOnly(7, 7, placementEffort, 4, 2, test,
				"tconmap/tlc4-results/tlc4-sweep_TLC_conventional4/",
				"tlc4-sweep", false); // IO limited, actual size ?
		placeOnly(7, 7, placementEffort, 4, 2, test,
				"tconmap/tlc4-results/tlc4-sweep_TLC_conventional4/",
				"tlc4-sweep", true); // IO limited, actual size ?
		placeOnly(7, 8, placementEffort, 6, 2, test,
				"tconmap/tlc4-results/tlc4-sweep_LUT_prioritycut6/",
				"tlc4-sweep", false); // IO limited, actual size ?
		placeOnly(7, 7, placementEffort, 6, 2, test,
				"tconmap/tlc4-results/tlc4-sweep_TLC_prioritycut6/",
				"tlc4-sweep", false); // IO limited, actual size ?
		placeOnly(7, 7, placementEffort, 6, 2, test,
				"tconmap/tlc4-results/tlc4-sweep_TLC_prioritycut6/",
				"tlc4-sweep", true); // IO limited, actual size ?
		placeOnly(9, 9, placementEffort, 4, 2, test,
				"tconmap/regExPE-results/grid_tile_LUT_conventional4/",
				"grid_tile", false); // IO limited, actual size 7x7
		placeOnly(6, 5, placementEffort, 4, 2, test,
				"tconmap/regExPE-results/grid_tile_TLC_conventional4/",
				"grid_tile", false);
		placeOnly(6, 5, placementEffort, 4, 2, test,
				"tconmap/regExPE-results/grid_tile_TLC_conventional4/",
				"grid_tile", true);
		placeOnly(9, 9, placementEffort, 6, 2, test,
				"tconmap/regExPE-results/grid_tile_LUT_prioritycut6/",
				"grid_tile", false); // IO limited, actual size 6x6
		placeOnly(5, 4, placementEffort, 6, 2, test,
				"tconmap/regExPE-results/grid_tile_TLC_prioritycut6/",
				"grid_tile", false);
		placeOnly(5, 4, placementEffort, 6, 2, test,
				"tconmap/regExPE-results/grid_tile_TLC_prioritycut6/",
				"grid_tile", true);
		
	
//		placeOnly(111, 111, placementEffort, 4, 2, test,
//				"tconmap/secretblaze-results/sb_execute04_1_TLC_conventional4/",
//				"sb_execute04_1");	//IO limited, actual 75x75
//		placeOnly(111, 111, placementEffort, 6, 2, test,
//				"tconmap/secretblaze-results/sb_execute04_1_TLC_prioritycut6/",
//				"sb_execute04_1");	//IO limited, actual 66x66
	

//	secretblaze
//		placeOnly(280, 280, placementEffort, 4, 2, test,
//				"tconmap/secretblaze-results/sb_core5_1_LUT_conventional4/",
//				"sb_core5_1", false);	//65467 BLE ca.
//		placeOnly(278, 278, placementEffort, 4, 2, test,
//				"tconmap/secretblaze-results/sb_core5_1_TLC_conventional4/",
//				"sb_core5_1", false); // 64173 BLE ca. without sharing
//		placeOnly(270, 270, placementEffort, 4, 2, test,
//				"tconmap/secretblaze-results/sb_core5_1_TLC_conventional4/",
//				"sb_core5_1", true);	//60655 BLE ca. with sharing
//		placeOnly(245, 244, placementEffort, 6, 2, test,
//				"tconmap/secretblaze-results/sb_core5_1_LUT_prioritycut6/",
//				"sb_core5_1", false);	//50523 BLE ca.
//		placeOnly(245, 245, placementEffort, 6, 2, test,
//				"tconmap/secretblaze-results/sb_core5_1_TLC_prioritycut6/",
//				"sb_core5_1", false); // 49868 BLE ca. without sharing
//		placeOnly(238, 238, placementEffort, 6, 2, test,
//				"tconmap/secretblaze-results/sb_core5_1_TLC_prioritycut6/",
//				"sb_core5_1", true);	//47099 BLE ca. with sharing
		
//CRYPTO OLD
		/*
//		placeOnly(279, 279, placementEffort, 4, 2, test,
//				"tconmap/modules_AES_3DES-results/modules_exp_LUT_conventional4/",
//				"modules_exp", false);	//64864 BLE ca. without sharing
//		placeOnly(278, 278, placementEffort, 4, 2, test,
//				"tconmap/modules_AES_3DES-results/modules_exp_TLC_conventional4/",
//				"modules_exp", false);	//64375 BLE ca. without sharing
//		placeOnly(270, 270, placementEffort, 4, 2, test, //TODO
//				"tconmap/modules_AES_3DES-results/modules_exp_TLC_conventional4/",
//				"modules_exp", true);	//60370 BLE ca. with sharing
//		placeOnly(140, 140, placementEffort, 6, 2, test, //TODO
//				"tconmap/modules_AES_3DES-results/modules_exp_LUT_prioritycut6/",
//				"modules_exp", false);	//16250 BLE ca. without sharing
//		placeOnly(137, 137, placementEffort, 6, 2, test,
//				"tconmap/modules_AES_3DES-results/modules_exp_TLC_prioritycut6/",
//				"modules_exp", false);	//15603 BLE ca. without sharing
//		placeOnly(127, 127, placementEffort, 6, 2, test,
//				"tconmap/modules_AES_3DES-results/modules_exp_TLC_prioritycut6/",
//				"modules_exp", true);	//13516 BLE ca. with sharing
 * */

		
//CRYPTO with REG
		placeOnly(311, 311, placementEffort, 4, 2, test,
				"tconmap/modules_AES_3DES_reg-results/modules_LUT_conventional4/",
				"modules", false);	//80752 BLE ca. without sharing
//		placeOnly(311, 311, placementEffort, 4, 2, test,
//				"tconmap/modules_AES_3DES_reg-results/modules_TLC_conventional4/",
//				"modules", false);	//80622 BLE ca. without sharing
//		placeOnly(303, 303, placementEffort, 4, 2, test,
//				"tconmap/modules_AES_3DES_reg-results/modules_TLC_conventional4/",
//				"modules", true);	//76622 BLE ca. with sharing
//		placeOnly(157, 157, placementEffort, 6, 2, test,
//				"tconmap/modules_AES_3DES_reg-results/modules_LUT_prioritycut6/",
//				"modules", false);	//20492 BLE ca. without sharing
//		placeOnly(155, 155, placementEffort, 6, 2, test,
//				"tconmap/modules_AES_3DES_reg-results/modules_TLC_prioritycut6/",
//				"modules", false);	//20072 BLE ca. without sharing
//		placeOnly(147, 147, placementEffort, 6, 2, test,
//				"tconmap/modules_AES_3DES_reg-results/modules_TLC_prioritycut6/",
//				"modules", true);	//17988 BLE ca. with sharing
		
		// min cw = 4 -> test cw 5
		// test.par(8, 8, 5, 10, 30, 4, 1, "testcases/BVHCell-tconmap_out.net",
		// "testcases/BVHCell-tconmap_out.p", "testcases/testcases.log");
		// min cw = 4 -> test cw 5
		// test.par(7, 7, 5, 10, 30, 4, 1,
		// "testcases/combined_blocks_gen1-tconmap_out.net",
		// "testcases/combined_blocks_gen1-tconmap_out.p",
		// "testcases/testcases.log");
		// min cw = 6 -> test cw 7
		// test.par(13, 13, 7, 10, 30, 4, 1,
		// "testcases/combined_blocks_gen4-tconmap_out.net",
		// "combined_blocks_gen4-tconmap_out.p", "testcases/testcases.log");
		// min cw = 4 -> test cw 5
		// test.par(8, 8, 5, 10, 30, 4, 1, "testcases/multMux-tconmap_out.net",
		// "testcases/multMux-tconmap_out.p", "testcases/testcases.log");
		// min cw = 6 -> test cw 7
		// test.par(15, 15, 7, 10, 30, 4, 1,
		// "testcases/treeMult16-tconmap_out.net",
		// "testcases/treeMult16-tconmap_out.p", "testcases/testcases.log");
	}

	public static void parTestCases() throws FileNotFoundException, Exception {
		TestConnectionRouter test = new TestConnectionRouter();
		//NOT USED
		
		// test.par(100, 100, 30, 10, 30, 6, 2, "tconmap/crossbar.net", null,
		// null, "tconmap/crossbar_out.p", "tconmap/crossbar.log");
		// test.par(6, 6, 8, 10, 30, 6, 2, "tconmap/crossbar.net", null, null,
		// "tconmap/crossbar_out.p", "tconmap/crossbar.log");
		// test.par(6, 6, 6, 10, 30, 6, 2, "tconmap/tlc2.net", null, null,
		// "tconmap/tlc2_out.p", "tconmap/tlc2.log");
		// test.par(7, 7, 4, 10, 30, 6, 2, "tconmap/tlc4.net", null, null,
		// "tconmap/tlc4_out.p", "tconmap/tlc4.log");
//		par(5, 4, 10, 2, 30, 6, 2, test,
//				"tconmap/regExPE-results/grid_tile_TLC_prioritycut6/",
//				"grid_tile");

		// // test.par(6, 6, 8, 10, 30, 4, 1, "tconmap/crossbar_4.net",
		// "tconmap/crossbar_4_out.p", "tconmap/crossbar_4.log");
		// test.par(6, 6, 8, 10, 30, 6, 2, "tconmap/crossbar.net",
		// "tconmap/crossbar-sharing.txt", "tconmap/crossbar_out.p",
		// "tconmap/crossbar.log");
		// // test.par(8, 8, 5, 10, 30, 4, 2, "tconmap/tlc2_4.net",
		// "tconmap/tlc2_4_out.p", "tconmap/tlc2_4.log");
		// test.par(6, 6, 6, 10, 30, 6, 2, "tconmap/tlc2.net",
		// "tconmap/tlc2-sharing.txt", "tconmap/tlc2_out.p",
		// "tconmap/tlc2.log");
		// // test.par(7, 7, 4, 10, 30, 4, 2, "tconmap/tlc4_4.net",
		// "tconmap/tlc4_4_out.p", "tconmap/tlc4_4.log");
		// test.par(7, 7, 4, 10, 30, 6, 2, "tconmap/tlc4.net",
		// "tconmap/tlc4-sharing.txt", "tconmap/tlc4_out.p",
		// "tconmap/tlc4.log");
		// // test.par(6, 6, 4, 10, 30, 4, 2, "tconmap/regExPE_4.net",
		// "tconmap/regExPE_4.p", "tconmap/regExPE_4.log");
		// test.par(5, 5, 7, 10, 30, 6, 2, "tconmap/regExPE.net",
		// "tconmap/regExPE-sharing.txt", "tconmap/regExPE.p",
		// "tconmap/regExPE.log");

		// test.par(285, 285, 5, 10, 30, 4, 2, "tconmap/sb_core5_1.net",
		// "tconmap/sb_core5_1_out.p", "tconmap/sb_core5_1.log");
		// test.par(4, 4, 5, 10, 30, 4, 1, "tconmap/treeMult4b.net",
		// "tconmap/treeMult4b_out.p", "tconmap/treeMult4b.log");
		// test.par(8, 8, 10, 10, 30, 6, 2, "tconmap/BVHCell.net",
		// "tconmap/BVHCell_out.p", "tconmap/BVHCell.log");
		// min cw = 4 -> test cw 5
//		test.par(8, 8, 5, 10, 30, 4, 1, "testcases/BVHCell-tconmap_out.net",null,null,
//		 "testcases/BVHCell-tconmap_out.p", "testcases/testcases.log");
		// min cw = 4 -> test cw 5
		// test.par(7, 7, 5, 10, 30, 4, 1,
		// "testcases/combined_blocks_gen1-tconmap_out.net",
		// "testcases/combined_blocks_gen1-tconmap_out.p",
		// "testcases/testcases.log");
		// min cw = 6 -> test cw 7
		// test.par(13, 13, 7, 10, 30, 4, 1,
		// "testcases/combined_blocks_gen4-tconmap_out.net",
		// "combined_blocks_gen4-tconmap_out.p", "testcases/testcases.log");
		// min cw = 4 -> test cw 5
		// test.par(8, 8, 5, 10, 30, 4, 1, "testcases/multMux-tconmap_out.net",
		// "testcases/multMux-tconmap_out.p", "testcases/testcases.log");
		// min cw = 6 -> test cw 7
		// test.par(15, 15, 7, 10, 30, 4, 1,
		// "testcases/treeMult16-tconmap_out.net",
		// "testcases/treeMult16-tconmap_out.p", "testcases/testcases.log");

		test.par(8, 8, 10, 10, 30, 4, 1, "testcases/BVHCell-tconmap_out.net",null,null,
				 "testcases/BVHCell-tconmap_out.p", "testcases/testcases.log");
		test.par(8, 8, 10, 10, 30, 4, 1, "testcases/barrelshift-tconmap_out.net",null,null,
				 "testcases/barrelshift-tconmap_out.net.p", "testcases/testcases.log");
		test.par(8, 8, 5, 10, 30, 4, 1, "testcases/combined_blocks_gen1-tconmap_out.net",null,null,
				 "testcases/combined_blocks_gen1-tconmap_out.p", "testcases/testcases.log");
		test.par(14, 14, 10, 10, 30, 4, 1, "testcases/combined_blocks_gen4-tconmap_out.net",null,null,
				 "testcases/combined_blocks_gen4-tconmap_out.p", "testcases/testcases.log");
		test.par(8, 8, 5, 10, 30, 4, 1, "testcases/multMux-tconmap_out.net",null,null,
				 "testcases/multMux-tconmap_out.p", "testcases/testcases.log");
		test.par(14, 14, 5, 10, 30, 4, 1, "testcases/mux3-tconmap_out.net",null,null,
				 "testcases/mux3-tconmap_out.p", "testcases/testcases.log");
		test.par(15, 15, 10, 10, 30, 4, 1, "testcases/treeMult16-tconmap_out.net",null,null,
				 "testcases/treeMult16-tconmap_out.p", "testcases/testcases.log");
	
	}

	public static void parMAC_PE_float() throws FileNotFoundException, Exception {
		TestConnectionRouter test = new TestConnectionRouter();

		test.par(8, 8, 10, 10, 30, 4, 1, "testcases/MAC_PE_float_LUT_conventional4/MAC_PE_float.net",null,null,
				 "testcases/MAC_PE_float_LUT_conventional4/MAC_PE_float.p", "testcases/MAC_PE_float.log");
		
		test.par(8, 8, 10, 10, 30, 4, 1, "testcases/MAC_PE_float_TCON_conventional4/MAC_PE_float.net",null,null,
				 "testcases/MAC_PE_float_LUT_conventional4/MAC_PE_float.p", "testcases/MAC_PE_float.log");
		
		test.par(8, 8, 10, 10, 30, 4, 1, "testcases/MAC_PE_float_TLC_conventional4/MAC_PE_float.net",null,null,
				 "testcases/MAC_PE_float_LUT_conventional4/MAC_PE_float.p", "testcases/MAC_PE_float.log");
		
		test.par(8, 8, 10, 10, 30, 4, 1, "testcases/MAC_PE_float_TLUT_conventional4/MAC_PE_float.net",null,null,
				 "testcases/MAC_PE_float_LUT_conventional4/MAC_PE_float.p", "testcases/MAC_PE_float.log");
	
	}
	
	public static void routeTestCases() throws FileNotFoundException, Exception {
		TestConnectionRouter test = new TestConnectionRouter();
		int routingEffort = 30;
		int overrideChannelWidth = 20;
		int orcw = overrideChannelWidth;
		//WARNING: mcw may be outdated

		routeOnly(Math.max(orcw,7), routingEffort, 4, 2, test,
				"tconmap/crossbar-results/cross16-sweep_LUT_conventional4/",
				"cross16-sweep", false); // min 6
		routeOnly(Math.max(orcw, 14), routingEffort, 4, 2, test,
				"tconmap/crossbar-results/cross16-sweep_TLC_conventional4/",
				"cross16-sweep", false); // min 12
		routeOnly(Math.max(orcw,14), routingEffort, 4, 2, test,
				"tconmap/crossbar-results/cross16-sweep_TLC_conventional4/",
				"cross16-sweep", true); // min 12
		routeOnly(Math.max(orcw,7), routingEffort, 6, 2, test,
				"tconmap/crossbar-results/cross16-sweep_LUT_prioritycut6/",
				"cross16-sweep", false); // min 6
		routeOnly(Math.max(orcw,14), routingEffort, 6, 2, test,
				"tconmap/crossbar-results/cross16-sweep_TLC_prioritycut6/",
				"cross16-sweep", false); // min 12
		routeOnly(Math.max(orcw,14), routingEffort, 6, 2, test,
				"tconmap/crossbar-results/cross16-sweep_TLC_prioritycut6/",
				"cross16-sweep", true); // min 12
		routeOnly(Math.max(orcw,7), routingEffort, 4, 2, test,
				"tconmap/tlc2-results/tlc2-sweep_LUT_conventional4/",
				"tlc2-sweep", false); // min 6
		routeOnly(Math.max(orcw,7), routingEffort, 4, 2, test,
				"tconmap/tlc2-results/tlc2-sweep_TLC_conventional4/",
				"tlc2-sweep", false); // min 7
		routeOnly(Math.max(orcw,10), routingEffort, 4, 2, test,
				"tconmap/tlc2-results/tlc2-sweep_TLC_conventional4/",
				"tlc2-sweep", true); // min 7
		routeOnly(Math.max(orcw,10), routingEffort, 6, 2, test,
				"tconmap/tlc2-results/tlc2-sweep_LUT_prioritycut6/",
				"tlc2-sweep", false); // min 8
		routeOnly(Math.max(orcw,10), routingEffort, 6, 2, test,
				"tconmap/tlc2-results/tlc2-sweep_TLC_prioritycut6/",
				"tlc2-sweep", false); // min 7
		routeOnly(Math.max(orcw,8), routingEffort, 6, 2, test,
				"tconmap/tlc2-results/tlc2-sweep_TLC_prioritycut6/",
				"tlc2-sweep", true); // min 7
		routeOnly(Math.max(orcw,5), routingEffort, 4, 2, test,
				"tconmap/tlc4-results/tlc4-sweep_LUT_conventional4/",
				"tlc4-sweep", false); // min 4
		routeOnly(Math.max(orcw,5), routingEffort, 4, 2, test,
				"tconmap/tlc4-results/tlc4-sweep_TLC_conventional4/",
				"tlc4-sweep", false); // min 4
		routeOnly(Math.max(orcw,5), routingEffort, 4, 2, test,
				"tconmap/tlc4-results/tlc4-sweep_TLC_conventional4/",
				"tlc4-sweep", true); // min 4
		routeOnly(Math.max(orcw,5), routingEffort, 6, 2, test,
				"tconmap/tlc4-results/tlc4-sweep_LUT_prioritycut6/",
				"tlc4-sweep", false); // min 4
		routeOnly(Math.max(orcw,5), routingEffort, 6, 2, test,
				"tconmap/tlc4-results/tlc4-sweep_TLC_prioritycut6/",
				"tlc4-sweep", false); // min 4
		routeOnly(Math.max(orcw,4), routingEffort, 6, 2, test,
				"tconmap/tlc4-results/tlc4-sweep_TLC_prioritycut6/",
				"tlc4-sweep", true); // min 4
		routeOnly(Math.max(orcw,6), routingEffort, 4, 2, test,
				"tconmap/regExPE-results/grid_tile_LUT_conventional4/",
				"grid_tile", false); // min 4
		routeOnly(Math.max(orcw,7), routingEffort, 4, 2, test,
				"tconmap/regExPE-results/grid_tile_TLC_conventional4/",
				"grid_tile", false); // min 6
		routeOnly(Math.max(orcw,7), routingEffort, 4, 2, test,
				"tconmap/regExPE-results/grid_tile_TLC_conventional4/",
				"grid_tile", true); // min 6
		routeOnly(Math.max(orcw,5), routingEffort, 6, 2, test,
				"tconmap/regExPE-results/grid_tile_LUT_prioritycut6/",
				"grid_tile", false); // min 5
		routeOnly(Math.max(orcw,10), routingEffort, 6, 2, test,
				"tconmap/regExPE-results/grid_tile_TLC_prioritycut6/",
				"grid_tile", false); // min 6
		routeOnly(Math.max(orcw,10), routingEffort, 6, 2, test,
				"tconmap/regExPE-results/grid_tile_TLC_prioritycut6/",
				"grid_tile", true); // min 6

		
		int bigChannelWidth = 10;

//		routeOnly(bigChannelWidth, routingEffort, 4, 2, test,
//				"tconmap/secretblaze-results/sb_core5_1_LUT_conventional4/",
//				"sb_core5_1", false);
//		routeOnly(bigChannelWidth, routingEffort, 4, 2, test,
//				"tconmap/secretblaze-results/sb_core5_1_TLC_conventional4/",
//	        	"sb_core5_1", false);
//		routeOnly(bigChannelWidth, routingEffort, 4, 2, test,
//				"tconmap/secretblaze-results/sb_core5_1_TLC_conventional4/",
//				"sb_core5_1", true);
//		routeOnly(bigChannelWidth, routingEffort, 6, 2, test,
//				"tconmap/secretblaze-results/sb_core5_1_LUT_prioritycut6/",
//				"sb_core5_1", false);
//		routeOnly(bigChannelWidth, routingEffort, 6, 2, test,
//				"tconmap/secretblaze-results/sb_core5_1_TLC_prioritycut6/",
//				"sb_core5_1", false);
//		routeOnly(bigChannelWidth, routingEffort, 6, 2, test,
//				"tconmap/secretblaze-results/sb_core5_1_TLC_prioritycut6/",
//				"sb_core5_1", true);
		

//CRYPTO OLD!
//		routeOnly(30, routingEffort, 6, 2, test,
//				"tconmap/modules_AES_3DES-results/modules_exp_LUT_prioritycut6/",
//				"modules_exp", false);
//		routeOnly(30, routingEffort, 6, 2, test,
//				"tconmap/modules_AES_3DES-results/modules_exp_TLC_prioritycut6/",
//				"modules_exp", false);
//		routeOnly(30, routingEffort, 6, 2, test,
//				"tconmap/modules_AES_3DES-results/modules_exp_TLC_prioritycut6/",
//				"modules_exp", true);

		bigChannelWidth = 10;
		//CRYPTO with REG
//		routeOnly(bigChannelWidth, routingEffort, 4, 2, test,
//				"tconmap/modules_AES_3DES_reg-results/modules_LUT_conventional4/",
//				"modules", false);
//		routeOnly(bigChannelWidth, routingEffort, 4, 2, test,
//				"tconmap/modules_AES_3DES_reg-results/modules_TLC_conventional4/",
//				"modules", false);
//		routeOnly(bigChannelWidth, routingEffort, 4, 2, test,
//				"tconmap/modules_AES_3DES_reg-results/modules_TLC_conventional4/",
//				"modules", true);
//		routeOnly(bigChannelWidth, routingEffort, 6, 2, test,
//				"tconmap/modules_AES_3DES_reg-results/modules_LUT_prioritycut6/",
//				"modules", false);
//		routeOnly(bigChannelWidth, routingEffort, 6, 2, test,
//				"tconmap/modules_AES_3DES_reg-results/modules_TLC_prioritycut6/",
//				"modules", false);
//		routeOnly(bigChannelWidth, routingEffort, 6, 2, test,
//				"tconmap/modules_AES_3DES_reg-results/modules_TLC_prioritycut6/",
//				"modules", true);
	}

	public static void mcwTestCases() throws FileNotFoundException, Exception {
		TestConnectionRouter test = new TestConnectionRouter();
		int max_chan_width = 20;
		int min_chan_width = 3;
		int routingEffort = 30;

		mcw(max_chan_width, min_chan_width, routingEffort, 4, 2, test,
				"tconmap/crossbar-results/cross16-sweep_LUT_conventional4/",
				"cross16-sweep", false);
		mcw(max_chan_width, min_chan_width, routingEffort, 4, 2, test,
				"tconmap/crossbar-results/cross16-sweep_TLC_conventional4/",
				"cross16-sweep", false);
		mcw(max_chan_width, min_chan_width, routingEffort, 4, 2, test,
				"tconmap/crossbar-results/cross16-sweep_TLC_conventional4/",
				"cross16-sweep", true);
		mcw(max_chan_width, min_chan_width, routingEffort, 6, 2, test,
				"tconmap/crossbar-results/cross16-sweep_LUT_prioritycut6/",
				"cross16-sweep", false);
		mcw(max_chan_width, min_chan_width, routingEffort, 6, 2, test,
				"tconmap/crossbar-results/cross16-sweep_TLC_prioritycut6/",
				"cross16-sweep", false);
		mcw(max_chan_width, min_chan_width, routingEffort, 6, 2, test,
				"tconmap/crossbar-results/cross16-sweep_TLC_prioritycut6/",
				"cross16-sweep", true);
		mcw(max_chan_width, min_chan_width, routingEffort, 4, 2, test,
				"tconmap/tlc2-results/tlc2-sweep_LUT_conventional4/",
				"tlc2-sweep", false);
		mcw(max_chan_width, min_chan_width, routingEffort, 4, 2, test,
				"tconmap/tlc2-results/tlc2-sweep_TLC_conventional4/",
				"tlc2-sweep", false);
		mcw(max_chan_width, min_chan_width, routingEffort, 4, 2, test,
				"tconmap/tlc2-results/tlc2-sweep_TLC_conventional4/",
				"tlc2-sweep", true);
		mcw(max_chan_width, min_chan_width, routingEffort, 6, 2, test,
				"tconmap/tlc2-results/tlc2-sweep_LUT_prioritycut6/",
				"tlc2-sweep", false);
		mcw(max_chan_width, min_chan_width, routingEffort, 6, 2, test,
				"tconmap/tlc2-results/tlc2-sweep_TLC_prioritycut6/",
				"tlc2-sweep", false);
		mcw(max_chan_width, min_chan_width, routingEffort, 6, 2, test,
				"tconmap/tlc2-results/tlc2-sweep_TLC_prioritycut6/",
				"tlc2-sweep", true);
		mcw(max_chan_width, min_chan_width, routingEffort, 4, 2, test,
				"tconmap/tlc4-results/tlc4-sweep_LUT_conventional4/",
				"tlc4-sweep", false);
		mcw(max_chan_width, min_chan_width, routingEffort, 4, 2, test,
				"tconmap/tlc4-results/tlc4-sweep_TLC_conventional4/",
				"tlc4-sweep", false);
		mcw(max_chan_width, min_chan_width, routingEffort, 4, 2, test,
				"tconmap/tlc4-results/tlc4-sweep_TLC_conventional4/",
				"tlc4-sweep", true);
		mcw(max_chan_width, min_chan_width, routingEffort, 6, 2, test,
				"tconmap/tlc4-results/tlc4-sweep_LUT_prioritycut6/",
				"tlc4-sweep", false);
		mcw(max_chan_width, min_chan_width, routingEffort, 6, 2, test,
				"tconmap/tlc4-results/tlc4-sweep_TLC_prioritycut6/",
				"tlc4-sweep", false);
		mcw(max_chan_width, min_chan_width, routingEffort, 6, 2, test,
				"tconmap/tlc4-results/tlc4-sweep_TLC_prioritycut6/",
				"tlc4-sweep", true);
		mcw(max_chan_width, min_chan_width, routingEffort, 4, 2, test,
				"tconmap/regExPE-results/grid_tile_LUT_conventional4/",
				"grid_tile", false);
		mcw(max_chan_width, min_chan_width, routingEffort, 4, 2, test,
				"tconmap/regExPE-results/grid_tile_TLC_conventional4/",
				"grid_tile", false);
		mcw(max_chan_width, min_chan_width, routingEffort, 4, 2, test,
				"tconmap/regExPE-results/grid_tile_TLC_conventional4/",
				"grid_tile", true);
		mcw(max_chan_width, min_chan_width, routingEffort, 6, 2, test,
				"tconmap/regExPE-results/grid_tile_LUT_prioritycut6/",
				"grid_tile", false);
		mcw(max_chan_width, min_chan_width, routingEffort, 6, 2, test,
				"tconmap/regExPE-results/grid_tile_TLC_prioritycut6/",
				"grid_tile", false);
		mcw(max_chan_width, min_chan_width, routingEffort, 6, 2, test,
				"tconmap/regExPE-results/grid_tile_TLC_prioritycut6/",
				"grid_tile", true);

		
//		mcw(max_chan_width, min_chan_width, routingEffort, 4, 2, test,
//				"tconmap/secretblaze-results/sb_execute04_1_TLC_conventional4/",
//				"sb_execute04_1");
//		mcw(max_chan_width, min_chan_width, routingEffort, 6, 2, test,
//				"tconmap/secretblaze-results/sb_execute04_1_TLC_priority6/",
//				"sb_execute04_1");
		
//		test.findMinimalChannelWidthBinSearch(20, 20, 30, 6, 2,
//				"tconmap/sb_core5_1-flat.net", "tconmap/sb_core5_1_out.p",
//				"tconmap/sb_core5_1_mcw.log", "tconmap/sb_core5_1_mcw.log");

		// 4 Mininimum Channel Width Connection router
		// test.findMinimalChannelWidthBinSearch(5, 2, 30, 4, 1,
		// "testcases/BVHCell-tconmap_out.net",
		// "testcases/BVHCell-tconmap_out.p", "testcases/testcases.log",
		// "testcases/testcases_mcw.log");
		// 4 Mininimum Channel Width Connection router
		// test.findMinimalChannelWidthBinSearch(5, 2, 30, 4, 1,
		// "testcases/combined_blocks_gen1-tconmap_out.net",
		// "testcases/combined_blocks_gen1-tconmap_out.p",
		// "testcases/testcases.log", "testcases/testcases_mcw.log");
		// 6 Mininimum Channel Width Connection router
		// test.findMinimalChannelWidthBinSearch(10, 5, 30, 4, 1,
		// "testcases/combined_blocks_gen4-tconmap_out.net",
		// "combined_blocks_gen4-tconmap_out.p", "testcases/testcases.log",
		// "testcases/testcases_mcw.log");
		// 4 Mininimum Channel Width Connection router
		// test.findMinimalChannelWidthBinSearch(8, 3, 30, 4, 1,
		// "testcases/multMux-tconmap_out.net",
		// "testcases/multMux-tconmap_out.p", "testcases/testcases.log",
		// "testcases/testcases_mcw.log");
		// 6 Mininimum Channel Width Connection router
		// test.findMinimalChannelWidthBinSearch(8, 3, 30, 4, 1,
		// "testcases/treeMult16-tconmap_out.net",
		// "testcases/treeMult16-tconmap_out.p", "testcases/testcases.log",
		// "testcases/testcases_mcw.log");
	}

	//
	// public void determineFPGADimensions(String netfile) throws
	// FileNotFoundException, Exception {
	// //Read in con netlist.
	// System.out.println("Read in "+netfile);
	// Readnetlist parser=new Readnetlist(new FileInputStream(new
	// File(netfile)));
	// Circuit c = null;
	// c = parser.read(true);
	// int CLBs = c.clbs.size();
	// double calcDim = Math.ceil(Math.sqrt(1.20*CLBs));
	// System.out.println("CLBs = "+CLBs+", calculated dimension = "+calcDim+", Inputs = "+c.inputs.size()+"/"+(4*calcDim)+", Outputs = "+c.outputs.size()+"/"+(4*calcDim));
	// }

}
