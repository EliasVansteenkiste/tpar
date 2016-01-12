package circuit.parser.conlist;

import java.io.PrintStream;
import java.util.Iterator;

import circuit.Circuit;
import circuit.Clb;
import circuit.Connection;
import circuit.Input;
import circuit.Output;
import circuit.Pin;

public class ConlistWriter {
	Circuit circuit;
	
	public ConlistWriter(Circuit c) {
		circuit = c;
	}

	public void write(PrintStream stream) {		
		//Write inputs
		for (Iterator<Input> i=circuit.inputs.values().iterator();i.hasNext();){
			Input in=i.next();
			printInput(stream, in);
		}
		
		//Write outputs
		for (Output out:circuit.outputs.values()) {
			printOutput(stream, out);
		}
				
		//Writing the CLB's
		for (Iterator<Clb> i=circuit.clbs.values().iterator();i.hasNext();) {
			Clb clb=i.next();
			printClb(stream, clb);
		}
		
		//Write connections
		for (Connection con: circuit.cons) {
			stream.println(".con " + con.source + " " + con.sink);
		}
	}

	private void printOutput(PrintStream stream, Output out) {
		stream.println(".output " + out.name);
		stream.println("pinlist: "+ out.input.name);

		stream.println();
	}

	private void printInput(PrintStream stream, Input in) {
		stream.println(".input " + in.name);
		stream.println("pinlist: " + in.output.name);
		stream.println();
	}
	
	private void printClb(PrintStream stream, Clb clb) {
		String subb= new String();
		subb+="subblock: LUT0";
		
		stream.println(".clb "+clb.name);
		stream.print("pinlist:");
		int k=0;
		for(Pin input:clb.input) {
			if(input!=null) {
				stream.print(" "+input.name);
				subb+=" "+k;
			}
			else {
				stream.print(" open");
				subb+=" open";
			}
			k++;
			    
		}
		for(Pin output:clb.output) {
			if(output!=null) {
				stream.print(" "+output.name);
				subb+=" "+k;
			}
			else {
				stream.print(" open");
				subb+=" open";
			}
			k++;
		}
		if(clb.clock.con!=null) {
			stream.println(" "+ clb.clock.name);
			subb+= " " + k;
		}
		else {
			stream.println(" open");
			subb+=" open";
		}
		stream.println(subb);
		stream.println();
	}

}
