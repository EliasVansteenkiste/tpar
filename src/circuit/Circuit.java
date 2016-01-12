package circuit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import PlacementParser.PlaatsingUnit;
import PlacementParser.Placement;
import architecture.Architecture2D;
import architecture.RouteNode;
import architecture.Site;

public class Circuit {
	public Map<String,Output> outputs;
	public Map<String,Input> inputs;
	public Map<String,Clb>	clbs;
	public Map<String,Connection> connections;
	public Set<Connection> cons;

	public Map<Pin,Set<Connection>> sourceBundles;
	public Map<Pin,Set<Connection>> sinkBundles;
	public Map<Pin,Set<Connection>> eqSinkBundles;
	public Set<Bundle> bundles;

	public boolean bundlesCreated;
	public boolean conRouted;
	public boolean bundleRouted;
	
	public Circuit() {
		super();
		outputs = new HashMap<String,Output>();
		inputs = new HashMap<String,Input>();
		clbs = new HashMap<String,Clb>();
		connections = new HashMap<String,Connection>();
		
		cons = new HashSet<Connection>();
		conRouted = false;
		bundleRouted = false;
	}

	public int numBlocks() {
		return inputs.size()+outputs.size()+clbs.size();
	}
	
	public int numClbs() {
		return clbs.size();
	}

	public int numInputs() {
		return inputs.size();
	}

	public int numOutputs() {
		return outputs.size();
	}
	
	public void applyPlacement(Placement p, Architecture2D a) {
		for (PlaatsingUnit pu : p.plaatsingsmap.values()) {
			Block b = getBlock(pu.name);
			if(b == null)
				continue;
				//throw new RuntimeException(
					//	"Block from placement not found in circuit: " + pu.name);
			Site s = a.getSite(pu.x, pu.y, pu.n);
			s.addBlock(b);
			b.fixed = true;
		}
		a.sanityCheck();
		sanityCheckPlacement();
	}

	public void sanityCheckPlacement() {
		for(Block block : getAllBlocks())
			if(block.site == null)
				throw new RuntimeException("Block not placed: " + block.name);
	}

	private Block getBlock(String naam) {
		Block result=null;
		result=inputs.get(naam);
		if (result == null) result=outputs.get(naam);
		if (result == null) result=clbs.get(naam);
		return result;
	}
	
	public Clb getClb(String naam) {
		return clbs.get(naam);
	}

	public Collection<Clb> getClbs() {
		return clbs.values();
	}

	public Collection<Input> getInputs() {
		return inputs.values();
	}
	
	public Collection<Output> getOutputs() {
		return outputs.values();
	}
	
	public Collection<Connection> getCons() {
		return cons;
	}
	
	public Collection<Block> getAllBlocks() {
		ArrayList<Block> list = new ArrayList<Block>();
		list.addAll(getInputs());
		list.addAll(getOutputs());
		list.addAll(getClbs());
		return list;
	}
	
	public int totalRouteNodes() {
		if(conRouted){
			//Making one big set of all routenodes
			Set<RouteNode> allRNs = new HashSet<RouteNode>();
			for(Connection con:this.cons){
				allRNs.addAll(con.routeNodes);
			}
			return allRNs.size();
		}else{
			return 0;
		}
	}

	public int totalRouteNodesCON() {
		//Making one big set of all routenodes
		Set<RouteNode> allRNs = new HashSet<RouteNode>();
		for(Connection con:this.cons){
			allRNs.addAll(con.routeNodes);
		}
		return allRNs.size();
	}
	
	public int totalWiresCon(){
		//Making one big set of all routenodes
		Set<RouteNode> allRNs = new HashSet<RouteNode>();
		for(Connection con:this.cons){
			allRNs.addAll(con.routeNodes);
		}
		//count the wires
		int totalWires = 0;
		for (RouteNode node: allRNs) {
			if (node.isWire()) {
				totalWires++;
			}
		}
		return totalWires;
	}
	
	public int totalWires() {
		int result=0;
		if(conRouted){
			//Making one big set of all routenodes
			Set<RouteNode> allRNs = new HashSet<RouteNode>();
			for(Connection con:this.cons){
				allRNs.addAll(con.routeNodes);
			}
			//count the wires
			for (RouteNode node: allRNs) {
				if (node.isWire()) {
					result++;
				}
			}
		}else{
			System.out.println("No routing present");
		}
		return result;
	}

	public void ripUpRouting(){
		conRouted = false;
		for(Connection con:cons){
			for(RouteNode rn:con.routeNodes){
				rn.resetDataInNode();
			}
			con.routeNodes.clear();
		}
	}

	public void createBundles(){
		if(!bundlesCreated){
			sourceBundles = new HashMap<Pin,Set<Connection>>();
			sinkBundles = new HashMap<Pin,Set<Connection>>();
			for(Connection con:cons){
				Set<Connection> sourceBundle;
				if((sourceBundle = sourceBundles.get(con.source))==null){
					sourceBundle = new HashSet<Connection>();
					sourceBundles.put(con.source, sourceBundle);
				}
				sourceBundle.add(con);
				Set<Connection> sinkBundle;
				if((sinkBundle = sinkBundles.get(con.sink))==null){
					sinkBundle = new HashSet<Connection>();
					sinkBundles.put(con.sink, sinkBundle);
				}
				sinkBundle.add(con);	
			}
			for(Set<Connection> cset:sourceBundles.values()){
				int size = cset.size();
				for(Connection con:cset)con.sourceFanout=size;
			}
			for(Set<Connection> cset:sinkBundles.values()){
				int size = cset.size();
				for(Connection con:cset)con.sinkFanin=size;
			}
		}
		
	}

	/**
	 * Turn a circuit with Clb resource sharing (multiple Clbs per site) after placement into a circuit with one "merged" Clb per site
	 */
	public Circuit flattenClbSharing(Architecture2D arch) {
	    Circuit circuit = new Circuit();
	    
	    HashMap<Pin,Pin> pinMap = new HashMap<Pin,Pin>();
	    
	    for(Input input : inputs.values()) {
	    	Input ninput = new Input(input);
	    	pinMap.put(input.output, ninput.output);
	    	circuit.inputs.put(ninput.name, ninput);
	    	translateSite(arch, input).addBlock(ninput);
	    }
	    
	    for(Output output : outputs.values()) {
	    	Output noutput = new Output(output);
	    	pinMap.put(output.input, noutput.input);
	    	circuit.outputs.put(noutput.name, noutput);
	    	translateSite(arch, output).addBlock(noutput);;
	    }
	    
	    for(Clb clb : clbs.values()) {
			Site nsite = translateSite(arch, clb);
			Clb nclb;
			if(nsite.isOccupied()) {	//merge with the clb that already occupies this site
				nclb = (Clb)nsite.getBlock();
			} else {	// add a new clb
		    	nclb = new Clb(clb);
		    	circuit.clbs.put(nclb.name, nclb);
		    	nsite.addBlock(nclb);
			}
			
	    	for(int i=0; i<clb.input.length; i++)
	    		pinMap.put(clb.input[i], nclb.input[i]);
	    	for(int i=0; i<clb.output.length; i++)
	    		pinMap.put(clb.output[i], nclb.output[i]);
	    	pinMap.put(clb.clock, nclb.clock);
	    }
	    
	    for(Connection con : cons) {
	    	Connection ncon = new Connection(con.name, pinMap.get(con.source), pinMap.get(con.sink), pinMap.get(con.equivSink));
	    	if(ncon.sink == ncon.source)
	    		ncon.sink.con = ncon;
	    	else
	    		circuit.cons.add(ncon);
	    }

		return circuit;
	}

	private Site translateSite(Architecture2D arch, Block node) {
		return arch.getSite(node.site.x, node.site.y, node.site.n);
	}

	public void printPlacement() {
		for(Clb clb : getClbs()) {
			System.out.println(clb + " -> " + clb.site);
		}
	}

}
