package TRouter;

import java.util.Vector;

import util.CountingSet;
import circuit.Connection;
import circuit.Pin;


public class RouteNodeData {
	double pres_cost;
	double acc_cost;
	private double partial_path_cost;
	private double lower_bound_total_path_cost;
	int occupation;

	private CountingSet<Pin> sinksSet;
	private CountingSet<Pin> eqsinksSet;
	private CountingSet<Pin> sourcesSet;
	
    public RouteNodeData(boolean ipin_or_sink) {
    	pres_cost = 1;
    	acc_cost = 1;
    	occupation = 0;
    	resetPathCosts();

		sinksSet = null;
		eqsinksSet = null;
		sourcesSet = null;
	}

	public void resetPathCosts() {
		partial_path_cost = Double.MAX_VALUE;
		lower_bound_total_path_cost = Double.MAX_VALUE;
	}
	
	public boolean pathCostsSet() {
		return partial_path_cost != Double.MAX_VALUE ||
				lower_bound_total_path_cost != Double.MAX_VALUE;
	}

	public void setPartialPathCost(double new_cost) {
		this.partial_path_cost = new_cost; // don't forget to reset
	}

	public double getPartialPathCost() {
		return this.partial_path_cost;
	}

	public void setLowerBoundTotalPathCost(
			double lower_bound_total_path_cost) {
		this.lower_bound_total_path_cost = lower_bound_total_path_cost;
	}
	
	public double getLowerBoundTotalPathCost() {
		return this.lower_bound_total_path_cost;
	}

	public boolean updateLowerBoundTotalPathCost(
			double new_lower_bound_total_path_cost) {
		if (new_lower_bound_total_path_cost < lower_bound_total_path_cost) {
			lower_bound_total_path_cost = new_lower_bound_total_path_cost;
			return true;
		}
		return false;
	}

	public boolean updatePartialPathCost(double new_partial_path_cost) {
		if (new_partial_path_cost < partial_path_cost) {
			partial_path_cost = new_partial_path_cost;
			return true;
		}
		return false;
	}
	
	public void addSink(Pin sink) {
		if(sinksSet == null)
			sinksSet = new CountingSet<Pin>();
		sinksSet.add(sink);
	}

	public void addEqSink(Pin equivSink) {
		if(eqsinksSet == null)
			eqsinksSet = new CountingSet<Pin>();
		eqsinksSet.add(equivSink);
	}

	public void addSource(Pin source) {
		if(sourcesSet == null)
			sourcesSet = new CountingSet<Pin>();
		sourcesSet.add(source);
	}

	public int numUniqueEqSinks() {
		if(eqsinksSet == null)
			return 0;
		return eqsinksSet.uniqueSize();
	}

	public int numUniqueSources() {
		if(sourcesSet == null)
			return 0;
		return sourcesSet.uniqueSize();
	}

	public int numUniqueSinks() {
		if(sinksSet == null)
			return 0;
		return sinksSet.uniqueSize();
	}

	public void removeSink(Pin sink) {
		sinksSet.remove(sink);
		if(sinksSet.isEmpty())
			sinksSet = null;
	}

	public void removeEqSink(Pin eqsink) {
		eqsinksSet.remove(eqsink);
		if(eqsinksSet.isEmpty())
			eqsinksSet = null;
	}

	public void removeSource(Pin source) {
		sourcesSet.remove(source);
		if(sourcesSet.isEmpty())
			sourcesSet = null;
	}

	public boolean containsSink(Pin sink) {
		if(sinksSet == null)
			return false;
		return sinksSet.contains(sink);
	}

	public boolean containsEqSink(Pin equivSink) {
		if(eqsinksSet == null)
			return false;
		return eqsinksSet.contains(equivSink);
	}

	public boolean containsSource(Pin source) {
		if(sourcesSet == null)
			return false;
		return sourcesSet.contains(source);
	}

	public int countEqSinkUses(Pin equivSink) {
		if(eqsinksSet == null)
			return 0;
		return eqsinksSet.count(equivSink);
	}

	public int countSourceUses(Pin source) {
		if(sourcesSet == null)
			return 0;
		return sourcesSet.count(source);
	}

	public int countSinkUses(Pin sink) {
		if(sinksSet == null)
			return 0;
		return sinksSet.count(sink);
	}
}
