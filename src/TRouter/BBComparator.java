package TRouter;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import circuit.Connection;
import circuit.Pin;

public class BBComparator implements Comparator<Connection> {
	Map<Connection, Integer> base;
	Map<Pin,Set<Connection>> sourceBundles;
	Map<Pin,Set<Connection>> sinkBundles;
	
	public BBComparator(Map<Connection, Integer> base, Map<Pin,Set<Connection>> sourceBundles, Map<Pin,Set<Connection>> sinkBundles) {
		this.base = base;
		this.sourceBundles = sourceBundles;
		this.sinkBundles = sinkBundles;
	}
	
	public int compare(Connection a, Connection b) {
		if(base.get(a) > base.get(b)){
			return 1;
		}else if(base.get(a) == base.get(b)){
			int afanout = sourceBundles.get(a.source).size();
			int afanin = sinkBundles.get(a.sink).size();
			int bfanout = sourceBundles.get(b.source).size();
			int bfanin = sinkBundles.get(b.sink).size();
			int conA = afanin + afanout;
			int conB = bfanin + bfanout;
			if(conA>conB){
				return 1;
			}else if(conA==conB){
				if(a.hashCode()>b.hashCode()){
					return 1;
				}else if(a.hashCode()<b.hashCode()){
					return -1;
				}else{
					if(a != b)
						System.out.println("Failure: Error while comparing 2 connections. HashCode of Two Connections was identical");
					return 0;
				}
			}else{
				return -1;
			}
		}else{
			return -1;
		}
	}
}



