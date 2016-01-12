package circuit.writer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import circuit.Circuit;
import circuit.Clb;
import circuit.Connection;
import circuit.Input;
import circuit.Output;
import circuit.Pin;


public class ConlistWriter {

	public static void writeCircuit(Circuit circuit, String filename) throws FileNotFoundException {
		PrintStream stream = new PrintStream(new FileOutputStream(filename));
		
		for(Input input : circuit.getInputs()) {
//			.input i[10]_ipin
//			pinlist: i[10]_ipin_out
			stream.println(".input "+input.getName());
			stream.println("pinlist: "+input.getName()+"_out");
		}
		stream.println();
		
		for(Output output : circuit.getOutputs()) {
//			.output o[10]_opin
//			pinlist: o[10]_opin_in
			stream.println(".output "+output.getName());
			stream.println("pinlist: "+output.getName()+"_in");
		}
		stream.println();

		for(Clb clb : circuit.getClbs()) {
//			.clb a230 #TLUT
//			pinlist:  a230_in0 a230_in1 a230_in2 a230_in3 a230_in4 a230_in5 a230_out a230_ff open
//			subblock: subb 0 1 2 3 4 5
			stream.println(".clb "+clb.getName());
			stream.print("pinlist:");
			for(Pin pin : clb.input)
				stream.print(" "+pin.name);
			for(Pin pin : clb.output)
				stream.print(" "+pin.name);
			stream.print(" "+"open"); //clb.clock.name); // not really supported
			stream.println();
			stream.println("subblock: subb 0 1 2 3 4 5");
		}
		stream.println();
		
		for(Connection con : circuit.getCons()) {
//			.con a304_ff a230_in0
			stream.println(".con "+con.source.name + " " + con.sink.name);
		}
		stream.println();
		
		stream.close();
	}
}
