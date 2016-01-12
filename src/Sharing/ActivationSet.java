package Sharing;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import circuit.Clb;


public class ActivationSet {
	private final int id;
	private Set<Clb> clbs;
	private Set<ActivationSet> sharingOpportunities;
	
	ActivationSet(int id) {
		this.id = id;
		clbs = new HashSet<Clb>();
		sharingOpportunities = new HashSet<ActivationSet>();
	}
	
	
	public int getId() {
		return id;
	}
	
	public void addClb(Clb clb) {
		clbs.add(clb);
		if(clb.getActivationSet() != null)
			throw new RuntimeException();
		clb.setActivationSet(this);
	}

	public void addClbs(Collection<Clb> nodes) {
		for(Clb n : nodes)
			addClb(n);
	}
	
	public Set<Clb> getClbs() {
		return clbs;
	}

	public Set<ActivationSet> getSharingOpportunities() {
		return sharingOpportunities;
	}
	
	public void setSharingOpportunities(Set<ActivationSet> sharingOpportunities) {
		this.sharingOpportunities = sharingOpportunities;
	}
	
	public void addSharingOpportunity(ActivationSet sharingOpportunity) {
		this.sharingOpportunities.add(sharingOpportunity);
	}
	
	public boolean canShareWith(ActivationSet set) {
		return getSharingOpportunities().contains(set);
	}

	public boolean canShareWith(Collection<ActivationSet> sets) {
		return getSharingOpportunities().containsAll(sets);
	}
	
	public void sanityCheck() {
		for(Clb clb : getClbs())
			if(clb.getActivationSet() != this)
				throw new RuntimeException();
		for(ActivationSet set : getSharingOpportunities())
			if(!set.canShareWith(this))
				throw new RuntimeException();
	}
	
	public int numClbs() {
		return getClbs().size();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ActivationSet(");
		sb.append("activation_function{"+id);
		sb.append("},share_with{");
		ArrayList<ActivationSet> sharing_opportunities = new ArrayList<ActivationSet>(getSharingOpportunities());
		Collections.sort(sharing_opportunities, new Comparator<ActivationSet>() { public int compare(ActivationSet a, ActivationSet b) {return Integer.valueOf(a.id).compareTo(b.id);} });
		for(ActivationSet set : sharing_opportunities) {
			sb.append(set.id);
			sb.append(',');
		}
		sb.append("},num_clbs{"+numClbs());
//			sb.append("},nodes{");
//			for(Node node : getNodes()) {
//				sb.append(node.getName());
//				sb.append(',');
//			}
		sb.append("})");
		return sb.toString();
	}
	
	public void printActivationSet(PrintStream stream) {
		stream.println("set id: "+id);
		stream.println("clbs:");
		for(Clb n : getClbs()) {
			stream.println(n.getName());
		}
		stream.println("sharing ids:");
		for(ActivationSet shareSet : getSharingOpportunities())
			stream.println(shareSet.id);
		stream.println();
	}


	public boolean isAlwaysActive() {
		return sharingOpportunities.size() == 0;
	}
}