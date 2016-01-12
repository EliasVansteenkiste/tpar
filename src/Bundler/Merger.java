package Bundler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import circuit.Circuit;
import circuit.Connection;
import circuit.Pin;
import circuit.PinType;

public class Merger {
	public Merger(){
		
	}
	
	public void merge(Circuit c){
		c.createBundles();
		Map<Pin,Set<Connection>> sinkBundlesCPY = new HashMap<Pin,Set<Connection>>(c.sinkBundles);
		for(Map.Entry<Pin,Set<Connection>> entry:c.sinkBundles.entrySet()){
			if(sinkBundlesCPY.containsKey(entry.getKey())){
				Set<Connection> currentCons = entry.getValue();
				Map<Pin,Set<Connection>> oneHopAway = new HashMap<Pin,Set<Connection>>();
				//find all the sinkbundles one hop away
				for(Connection con:entry.getValue()){
					Set<Connection> sob = c.sourceBundles.get(con.source);
					for(Connection conSob:sob){
						if(!oneHopAway.containsKey(conSob.sink)) oneHopAway.put(conSob.sink, c.sinkBundles.get(conSob.sink));
					}
				}
				//Check which one-hop-away-sinkbundles can be merged
				Map<Pin,Set<Connection>> mergeables = new HashMap<Pin,Set<Connection>>(oneHopAway);
				for(Map.Entry<Pin,Set<Connection>> entryOneHopAway:oneHopAway.entrySet()){
					boolean mergeIsLegal = true;
					for(Connection con1:entryOneHopAway.getValue()){
						if(!mergeIsLegal) break;
						for(Connection con2:currentCons){
							if(!mergeIsLegal) break;
							boolean activeAtSameTime = false;
							for(int mode1:con1.modes){
								if(con2.modes.contains(mode1)){
									activeAtSameTime = true;
								}
							}
							if(activeAtSameTime){
								if(con1.source!=con2.source) mergeIsLegal = false;
							}
						}
					}
					if(mergeIsLegal){
						mergeables.put(entryOneHopAway.getKey(), entryOneHopAway.getValue());
					}
				}
				//Merge sinkbundles by giving them the same equivalent sink pin
				if(mergeables.size()>0){
					String equivSinkName = entry.getKey().name;
					for(Map.Entry<Pin,Set<Connection>> mergeable:mergeables.entrySet()){
						equivSinkName += "_" + mergeable.getKey().name;
						System.out.print(mergeable.getKey()+", ");
					}
					System.out.println(" sinkbundles are merged.");
					Pin equivSink = new Pin(equivSinkName,PinType.EQSINK);
					for(Map.Entry<Pin,Set<Connection>> mergeable:mergeables.entrySet()){
						for(Connection con:mergeable.getValue()){
							con.equivSink = equivSink;
						}
						//remove merged bundle
						sinkBundlesCPY.remove(mergeable.getKey());
					}
				}
				//remove current bundle
				sinkBundlesCPY.remove(entry.getKey());
			}
		}
		//Reconstruct the sink bundles
		c.eqSinkBundles = new HashMap<Pin,Set<Connection>>();
		for(Connection con:c.cons){
			Set<Connection> eqSinkBundle;
			if((eqSinkBundle = c.eqSinkBundles.get(con.equivSink))==null){
				eqSinkBundle = new HashSet<Connection>();
				c.eqSinkBundles.put(con.equivSink, eqSinkBundle);
			}
			eqSinkBundle.add(con);
		}
	}
}
