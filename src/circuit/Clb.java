package circuit;

import java.util.ArrayList;
import java.util.Collection;

import Sharing.ActivationSet;

public class Clb extends Block {
	public final Pin[] output;
	public final int nro;
	public final Pin[] input;
	public final int nri;
	public final Pin clock;
	
	private ActivationSet aset = null;
	
	public Clb(String name, int nro, int nri) {
		super(name, BlockType.CLB);
		// TODO Auto-generated constructor stub
		this.nro = nro;
		this.nri = nri;
		
		output = new Pin[nro];
		for (int i=0; i<nro; i++) {
			output[i]=new Pin(name+"_out"+i, PinType.SOURCE, this);
		}
		input  = new Pin[nri];
		for (int i=0; i<nri; i++) {
			input[i]=new Pin(name+"_in"+i, PinType.SINK, this);
		}
		clock=new Pin(name+"_clock", PinType.SINK, this);

	}
	
	public Clb(Clb other) {
		this(other.name, other.nro, other.nri);
	}

	public ActivationSet getActivationSet() {
		return aset;
	}
	
	public void setActivationSet(ActivationSet aset) {
		this.aset = aset;
	}
	
	public static Collection<ActivationSet> getActivationSets(Collection<Clb> clbs) {
		Collection<ActivationSet> sets = new ArrayList<ActivationSet>();
		for(Clb clb : clbs)
			sets.add(clb.getActivationSet());
		return sets;
	}
		
}
