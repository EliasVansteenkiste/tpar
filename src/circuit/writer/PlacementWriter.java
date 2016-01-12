package circuit.writer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import circuit.Block;
import circuit.Circuit;


public class PlacementWriter {
	
	public static void dumpPlacement(Circuit circuit, String file) throws FileNotFoundException {
		dumpPlacement(circuit, file, 0, 0);
	}
	
	public static void dumpPlacement(Circuit circuit, String file, int height, int width) throws FileNotFoundException {
		PrintStream stream = new PrintStream(new FileOutputStream(file));
		stream.println("Netlist file: na.net	Architecture file: na.arch");
		stream.println("Array size: "+width+" x "+height+" logic blocks");
		stream.println();
		stream.println("#block name	x	y	subblk	block number");
		stream.println("#----------	--	--	------	------------");
		for(Block blok:circuit.inputs.values()) {
			stream.println(blok.name+"	"+blok.site.x+"	"+blok.site.y+"	"+blok.site.n);
		}
		for(Block blok:circuit.clbs.values()) {
			stream.println(blok.name+"	"+blok.site.x+"	"+blok.site.y+"	"+blok.site.n);
		}
		for(Block blok:circuit.outputs.values()) {
			stream.println(blok.name+"	"+blok.site.x+"	"+blok.site.y+"	"+blok.site.n);
		}
		stream.close();
	}
	
}
