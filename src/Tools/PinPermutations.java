package Tools;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import circuit.Circuit;
import circuit.Connection;
import circuit.Pin;

public class PinPermutations {
	public PinPermutations(){
		super();
	}
	
	public int sobsWithCons2SameBlock(Circuit c){
		c.createBundles();
		int sobs = 0;
		for(Set<Connection> sob:c.sourceBundles.values()){
			boolean sobContainsCon2SameBlock = false;
			for(Connection con1:sob){
				if(sobContainsCon2SameBlock) break;
				for(Connection con2:sob){
					if(sobContainsCon2SameBlock) break;
					if(!con1.equals(con2) && con1.sink.owner.equals(con2.sink.owner)){
						sobs++;
						sobContainsCon2SameBlock = true;
					}
				}
			}
		}
		return sobs;
	}
	
/*	public int PinAssignmentViaGreedyPermutation(Circuit placedCircuit){
		placedCircuit.createBundles();
		HashSet<Block> blocks = new HashSet<Block>();
		for(Set<Connection> sob:placedCircuit.sourceBundles.values()){
			boolean sobContainsCon2SameBlock = false;
			for(Connection con1:sob){
				for(Connection con2:sob){
					if(!con1.equals(con2) && con1.sink.owner.equals(con2.sink.owner)){
						blocks.add(con1.sink.owner);
					}
				}
			}
		}
		for(Block block:blocks){
			Map<Pin,Set<Integer>> sources = new HashMap<Pin,Set<Integer>>();
			if(block.type == BlockType.CLB){
				Clb clb = (Clb) block;
				Set<Integer> allModes = new HashSet<Integer>();
				for(int i=0; i<clb.input.length;i++){
					for(Connection con:placedCircuit.sinkBundles.get(clb.input[i])){
						Set<Integer> modes;
						if((modes=sources.get(con.source))==null){
							modes = new HashSet<Integer>();
							sources.put(con.source, modes);
						}
						for(int mode:con.modes){
							modes.add(mode);
							allModes.add(mode);
						}
					}
				}
				//Remove sink bundles
				placedCircuit.sinkBundles.remove(clb.input[0]);
				placedCircuit.sinkBundles.remove(clb.input[1]);
				placedCircuit.sinkBundles.remove(clb.input[2]);
				placedCircuit.sinkBundles.remove(clb.input[3]);
				//Check if there are sources needed in every mode
				int maxNOmodes = allModes.size();
			    boolean[][] pinOccupied = new boolean[4][maxNOmodes];
				ValueComparator bvc =  new ValueComparator(sources);
			    TreeMap<Pin,Set<Integer>> sortedSources = new TreeMap<Pin,Set<Integer>>(bvc);
			    //Greedy select pin for the signals (sources) needed in the most modes
			    Vector<HashSet<Connection>> sibs =  new Vector<HashSet<Connection>>();
			    for(int i=0; i<4; i++){
			    		Map.Entry<Pin,Set<Integer>> entry = sortedSources.firstEntry();
			    		sortedSources.remove(entry.getKey());
			    		for(int mode:entry.getValue()){
			    			pinOccupied[i][mode] = true;
			    		}
			    		HashSet<Connection> cons = new HashSet<Connection>();
			    		cons.add(new Connection(entry.getKey(),clb.input[i]));
			    		sibs.add(cons);
			    }
			    while(sortedSources.size()>0){
			    		Map.Entry<Pin,Set<Integer>> entry = sortedSources.firstEntry();
			    		boolean[] fits = {true,true,true,true};
			    		int[] remainingSlots = {-1,-1,-1,-1};
			    		for(int i=0;i<4;i++){
			    			for(int mode:entry.getValue()){
			    				if(pinOccupied[i][mode]) fits[i] = false;
			    			}
			    			if(fits[i]){
			    				int remaining = 0;
			    				//calculate remaining slots
			    				for(int k=0;k<maxNOmodes;k++){
			    					if(!pinOccupied[i][k]) remaining++;
			    				}
			    				remainingSlots[i] = remaining;
			    			}
			    		}
			    		boolean pinAssigned = false;
			    		int maxPin
			    		for(int i=0;i<4;i++){
			    			
			    		}
			    		
			    		
			    }
			    
				for(){

				}

				
			}
			
		}
		return sobsWithCon2SameBlock.size();
	}
*/
/*	public int PinAssignmentViaBruteForcePermutation(Circuit placedCircuit){
		placedCircuit.createBundles();
		HashSet<Block> blocks = new HashSet<Block>();
		for(Set<Connection> sob:placedCircuit.sourceBundles.values()){
			boolean sobContainsCon2SameBlock = false;
			for(Connection con1:sob){
				for(Connection con2:sob){
					if(!con1.equals(con2) && con1.sink.owner.equals(con2.sink.owner)){
						blocks.add(con1.sink.owner);
					}
				}
			}
		}
		for(Block bl:blocks){
			Map<Pin,Set<Integer>> sources = new HashMap<Pin,Set<Integer>>();
			if(block.type == BlockType.CLB){
				Clb clb = (Clb) bl;
				Set<Integer> allModes = new HashSet<Integer>();
				for(int i=0; i<clb.input.length;i++){
					for(Connection con:placedCircuit.sinkBundles.get(clb.input[i])){
						Set<Integer> modes;
						if((modes=sources.get(con.source))==null){
							modes = new HashSet<Integer>();
							sources.put(con.source, modes);
						}
						for(int mode:con.modes){
							modes.add(mode);
							allModes.add(mode);
						}
					}
				}
				//Remove sink bundles
				placedCircuit.sinkBundles.remove(clb.input[0]);
				placedCircuit.sinkBundles.remove(clb.input[1]);
				placedCircuit.sinkBundles.remove(clb.input[2]);
				placedCircuit.sinkBundles.remove(clb.input[3]);
				//Check if there are sources needed in every mode
				int maxNOmodes = allModes.size();
			    boolean[][] pinOccupied = new boolean[4][maxNOmodes];
				ValueComparator bvc =  new ValueComparator(sources);
			    TreeMap<Pin,Set<Integer>> sortedSources = new TreeMap<Pin,Set<Integer>>(bvc);
			    //Greedy select pin for the signals (sources) needed in the most modes
			    Vector<HashSet<Connection>> sibs =  new Vector<HashSet<Connection>>();
			    for(int i=0; i<4; i++){
			    		Map.Entry<Pin,Set<Integer>> entry = sortedSources.firstEntry();
			    		sortedSources.remove(entry.getKey());
			    		for(int mode:entry.getValue()){
			    			pinOccupied[i][mode] = true;
			    		}
			    		HashSet<Connection> cons = new HashSet<Connection>();
			    		cons.add(new Connection(entry.getKey(),clb.input[i]));
			    		sibs.add(cons);
			    }
			    while(sortedSources.size()>0){
			    		Map.Entry<Pin,Set<Integer>> entry = sortedSources.firstEntry();
			    		boolean[] fits = {true,true,true,true};
			    		int[] remainingSlots = {-1,-1,-1,-1};
			    		for(int i=0;i<4;i++){
			    			for(int mode:entry.getValue()){
			    				if(pinOccupied[i][mode]) fits[i] = false;
			    			}
			    			if(fits[i]){
			    				int remaining = 0;
			    				//calculate remaining slots
			    				for(int k=0;k<maxNOmodes;k++){
			    					if(!pinOccupied[i][k]) remaining++;
			    				}
			    				remainingSlots[i] = remaining;
			    			}
			    		}
			    		boolean pinAssigned = false;
			    		int maxPin
			    		for(int i=0;i<4;i++){
			    			
			    		}
			    		
			    		
			    }
			    
				for(){

				}

				
			}
			
		}
		return sobsWithCon2SameBlock.size();
	}
*/
}

class ValueComparator implements Comparator<Pin> {

    Map<Pin,Set<Integer>> base;
    public ValueComparator(Map<Pin,Set<Integer>> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(Pin a, Pin b) {
        if (base.get(a).size() < base.get(b).size()) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}