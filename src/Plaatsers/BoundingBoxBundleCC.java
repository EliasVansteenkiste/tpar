package Plaatsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import architecture.Site;
import circuit.Block;
import circuit.Bundle;
import circuit.Circuit;
import circuit.Clb;
import circuit.Input;
import circuit.Output;

public class BoundingBoxBundleCC implements CostCalculator {
	Map<Bundle, BoundingBoxData> boundingBoxData;
	Map<Block, BundleData> bundleData;
	final Circuit circuit;
	

	public BoundingBoxBundleCC(Circuit c) {
		this.circuit=c;
		this.initializeData();
	}
	
	public Circuit getCircuit(){
		return circuit;
	}

	/**
	 * Calculate the cost of all blocks on a particular site
	 */
	private double calculateNetCost(Site site) {
		// TODO: cost is now sum of costs of all blocks, does not take into
		// account resource sharing between nets of different blocks
		double result = 0;
		for (Block block : site.getBlocks()) {
			// System.out.println(blockData.get(block).blocks.size());
			for (Bundle bundle : bundleData.get(block).bundles) {
				BoundingBoxData nd = boundingBoxData.get(bundle);
				if (nd != null) {
					result += nd.calculateCost();
				}
			}
		}
		return result;
	}

	public double calculateTotalCost() {
		double totaleKost=0;
		for(BoundingBoxData bbd: boundingBoxData.values()) {
			totaleKost+=bbd.calculateCost();
		}
		return totaleKost;
	}

	public double averageNetCost() {
		return calculateTotalCost()/boundingBoxData.size();
	}

	private void initializeData() {
		bundleData = new HashMap<Block, BundleData>();
		for(Clb block:circuit.getClbs()) {
			bundleData.put(block, new BundleData());
		}
		for(Input block:circuit.getInputs()) {
			bundleData.put(block, new BundleData());
		}
		for(Output block:circuit.getOutputs()) {
			bundleData.put(block, new BundleData());
		}
		
		boundingBoxData = new HashMap<Bundle, BoundingBoxData>();
		for(Bundle bundle:circuit.bundles) {
			Set<Block> blockSet = bundle.blocks();
			boundingBoxData.put(bundle, new BoundingBoxData(blockSet));
			for(Block block:blockSet){
				bundleData.get(block).bundles.add(bundle);
			}
		}
		for(BundleData bundleD : bundleData.values())
			bundleD.bundles = new ArrayList<Bundle>(bundleD.bundles);
	}

	public double calculateDeltaCost(Swap swap) {
		double costBefore = calculateNetCost(swap.pl1)
				+ calculateNetCost(swap.pl2);

		swap.apply();

		double costAfter = calculateNetCost(swap.pl1)
				+ calculateNetCost(swap.pl2);
		double deltaCost = costAfter - costBefore;

		swap.unapply();

		return deltaCost;
	}

	@Override
	public void apply(Swap swap) {
		swap.apply();
	}

}
