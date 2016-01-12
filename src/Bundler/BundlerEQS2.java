package Bundler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

import circuit.Bundle;
import circuit.Circuit;
import circuit.Connection;
import circuit.Pin;

public class BundlerEQS2 {
	final Circuit c;
	int [] color;
	int [] y;
	Map<Pin,Set<Connection>> sourceBundlesCPY;
	Map<Pin,Set<Connection>> sinkBundlesCPY;
	Set<Connection> consCPY;
	Set<Set<Connection>> bundleSets;

	int [][] Edges;
	int NrOfVertices, colorsAvailable;
	
	boolean found = false;
	boolean found1solution = false;
	
	public BundlerEQS2(Circuit c){
		this.c = c;
		sourceBundlesCPY = new HashMap<Pin,Set<Connection>>(c.sourceBundles);
		sinkBundlesCPY = new HashMap<Pin,Set<Connection>>(c.eqSinkBundles);
		consCPY = new HashSet<Connection>(c.cons);
		c.bundles = new HashSet<Bundle>();
	}
	
	public void bundle(){
		//1.First we simplify the minimal vertex clique cover problem
		//2.Secondly we transform the minimal vertex clique cover problem to a minimal vertex colouring problem
		//3.thirdly we solve the colouring problem by an exhaustive algorithm using backtrack method
		//4.In a last stage we bundle the connections in the same colour
		
		//1.
		removeStaticNetsFromProblem();
		
		System.out.println("Total number number of connections left to bundle: "+consCPY.size());
		while(!consCPY.isEmpty()){
			//Select connected graph
			Set<Connection> consInCluster = new HashSet<Connection>();
			Set<Set<Connection>> bundlesInCluster = new HashSet<Set<Connection>>(); 
			Set<Set<Connection>> sourceBundlesInCluster = new HashSet<Set<Connection>>();
			Set<Set<Connection>> sinkBundlesInCluster = new HashSet<Set<Connection>>();
			
			findNextCluster(consInCluster, bundlesInCluster, sourceBundlesInCluster,
					sinkBundlesInCluster);
			
			System.out.println("A cluster with " + consInCluster.size()
					+ " connections is discovered.");
			if(consInCluster.size()<21) {
				solveExactColoring(consInCluster, bundlesInCluster);
			} else {
				solveHeuristicColoring(consInCluster, sourceBundlesInCluster,
						sinkBundlesInCluster);
			}
		}
	}

	private void findNextCluster(Set<Connection> consInCluster,
			Set<Set<Connection>> bundlesInCluster,
			Set<Set<Connection>> sourceBundlesInCluster,
			Set<Set<Connection>> sinkBundlesInCluster) {
		Queue<Connection> qe=new LinkedList<Connection>();
		
		//Pick a con that hasn't been clustered yet
		qe.add(consCPY.iterator().next());
		
		// Expand cluster
		while (!qe.isEmpty()) {
			Connection con = qe.poll();
			consInCluster.add(con);
			consCPY.remove(con);
			Set<Connection> sourceBundle = sourceBundlesCPY.remove(con.source);
			Set<Connection> sinkBundle = sinkBundlesCPY.remove(con.equivSink);
			if (sourceBundle != null) {
				sourceBundlesInCluster.add(new HashSet<Connection>(sourceBundle));
				bundlesInCluster.add(sourceBundle);
				for (Connection cn : sourceBundle) {
					if (!consInCluster.contains(cn)) {
						qe.add(cn);
					}
				}
			}
			if (sinkBundle != null) {
				sinkBundlesInCluster.add(new HashSet<Connection>(sinkBundle));
				bundlesInCluster.add(sinkBundle);
				for (Connection cn : sinkBundle) {
					if (!consInCluster.contains(cn)) {
						qe.add(cn);
					}
				}
			}
		}
	}

	private void solveHeuristicColoring(Set<Connection> consInCluster,
			Set<Set<Connection>> sourceBundlesInCluster,
			Set<Set<Connection>> sinkBundlesInCluster) {
		//Greedy heuristic for bundling
		System.out.println("Find a node clique partition via an greedy heuristic algorithm.");
		int before = totalConnectionsInBundles();
		while(consInCluster.size()>0){
			//Select bundles with maximum number of connections
			Vector<Set<Connection>> maxBundles = new Vector<Set<Connection>>();
			int maxCons = findBundlesWithMaxCons(sourceBundlesInCluster,
					sinkBundlesInCluster, maxBundles);
			
			//Add maxBundles and adapt remaining sobs/sibs
			while(!maxBundles.isEmpty()) {
				Set<Connection> bundleSet = maxBundles.lastElement();
				maxBundles.removeElementAt(maxBundles.size()-1);
				if(bundleSet.size() != maxCons)
					 continue;
				//create new bundle
				consInCluster.removeAll(bundleSet);
				Bundle bundle = new Bundle(new HashSet<Connection>(bundleSet));
				c.bundles.add(bundle);
				sourceBundlesInCluster.remove(bundleSet);
				sinkBundlesInCluster.remove(bundleSet);
				
				updateBundles(sourceBundlesInCluster, sinkBundlesInCluster,
						bundleSet);
				maxBundles.remove(bundleSet);
			}
		}
		int after = totalConnectionsInBundles();
		System.out.println("Cons added for this cluster: "+(after-before));
		//System.out.println("bundled: Number of bundles: "+NrOfBundles);
	}

	private void updateBundles(Set<Set<Connection>> sourceBundlesInCluster,
			Set<Set<Connection>> sinkBundlesInCluster, Set<Connection> bundleSet) {
		// update sobs/sibs
		for (Set<Connection> sob : new ArrayList<Set<Connection>>(sourceBundlesInCluster)) {
			sourceBundlesInCluster.remove(sob);
			sob.removeAll(bundleSet);
			sourceBundlesInCluster.add(sob);
		}
		for (Set<Connection> sib : new ArrayList<Set<Connection>>(sinkBundlesInCluster)) {
			sourceBundlesInCluster.remove(sib);
			sib.removeAll(bundleSet);
			sourceBundlesInCluster.add(sib);
		}
	}

	private int findBundlesWithMaxCons(
			Set<Set<Connection>> sourceBundlesInCluster,
			Set<Set<Connection>> sinkBundlesInCluster,
			Vector<Set<Connection>> maxBundles) {
		int maxCons = 0;
		for(Set<Connection> sob:sourceBundlesInCluster){
			int sizeSOB = sob.size();
			if(sizeSOB > maxCons){
				maxCons = sizeSOB;
				maxBundles.clear();
				maxBundles.add(sob);
			}else if(sizeSOB == maxCons){
				maxBundles.add(sob);
			}
		}
		for(Set<Connection> sib:sinkBundlesInCluster){
			int sizeSIB = sib.size();
			if(sizeSIB > maxCons){
				maxCons = sizeSIB;
				maxBundles.clear();
				maxBundles.add(sib);
			}else if(sizeSIB == maxCons){
				maxBundles.add(sib);
			}
		}
		return maxCons;
	}

	private void solveExactColoring(Set<Connection> consInCluster,
			Set<Set<Connection>> ssBundlesInCluster) {
		System.out.println("Find a minmum node clique partition via an exhaustive graph colouring algorithm.");
		//Construct connectivity matrix for the selected connected graph
		Vector<Connection> consConGraph = new Vector<Connection>();
		consConGraph.addAll(consInCluster);
		NrOfVertices = consInCluster.size();
		
		Edges = new int[NrOfVertices+1][NrOfVertices+1];
		color = new int[NrOfVertices+1];//initial value, 0, zero means no color
		y = new int[NrOfVertices+1];
		for(Set<Connection> cluster:ssBundlesInCluster){
			for(Connection cnA:cluster){
				for(Connection cnB:cluster){
					if(!cnA.equals(cnB)){
						int idxA = consConGraph.indexOf(cnA)+1;
						int idxB = consConGraph.indexOf(cnB)+1;
						Edges[idxA][idxB]=1;	
					}
				}
			}
		}
		//2. Invert Connectivity
		int nrOfEdges = 0;
		for(int i=1;i<=consConGraph.size();i++){
			for(int j=1;j<=consConGraph.size();j++){
				if(i!=j){
					if(Edges[i][j]==1)Edges[i][j]=0;
					else{
						Edges[i][j]=1;
						nrOfEdges++;
					}
				}
			}
		}
		nrOfEdges = nrOfEdges/2;
		
		//3. Find Minimal Colouring
		//Find minimal nr of pins, upper bound for chromatic number
		Set<Pin> sources = new HashSet<Pin>();
		Set<Pin> sinks = new HashSet<Pin>();
		Set<Connection> cons = new HashSet<Connection>();
		for(Set<Connection> cluster:ssBundlesInCluster){
			for(Connection con:cluster){
				sources.add(con.source);
				sinks.add(con.equivSink);??
				cons.add(con);
			}
		}
		int vertices = cons.size();
		int upperBound;
		int sosize = sources.size();
		int sisize = sinks.size();
		if(sosize>sisize)upperBound = sosize;
		else upperBound = sisize;
//		System.out.println("Upper bound (Minimum pin partition): "+upperBound);
		
		//Find a lower bound for chromatic number
		double lowerBound = Math.pow(vertices, 2)/(Math.pow(vertices, 2)-2*nrOfEdges);
//		System.out.println("Vertices = "+vertices);
//		System.out.println("Edges = "+nrOfEdges);
//		System.out.println("Lower bound = "+lowerBound);
		int lb = (int)Math.ceil(lowerBound);
		
		//Minimal Colouring
//		System.out.println("\tFind minimal Colouring.. ");
		found1solution = false;
		for(colorsAvailable=upperBound;colorsAvailable>=lb;colorsAvailable--){
			found = false;
//			System.out.println("\tTrying chromatic number "+colorsAvailable);
			mColoring(1);
			if (found == false){
				colorsAvailable++;
				System.out.println("\tSolution not found. Chromatic Number is "+colorsAvailable);
				break;
			}
		}
		if(found == true)colorsAvailable++;
		
		
		//4. add bundles of connections with the same colour
		int before = totalConnectionsInBundles();
		if(!found1solution){//Defensive coding
			System.out.println("Error: Minimal Colouring not found, programmeerfout?");
		}else{
//			System.out.println("colorsAvailable: "+colorsAvailable);
			for(int colorNr=1;colorNr<colorsAvailable+1;colorNr++){
				Set<Connection> s = new HashSet<Connection>();
				bundleSets.add(s);
				Bundle bundle = new Bundle(s);
				c.bundles.add(bundle);
				for(int i=1;i<=NrOfVertices;i++){
					if(y[i]==colorNr)s.add(consConGraph.elementAt(i-1));
				}
			}
		}
		int after = totalConnectionsInBundles();
		System.out.println("Cons added for this cluster: "+(after-before));
		
		//reset values
		Edges=null;//Defensive coding
		color=null;//Defensive coding
		found = false;
	}

	private void removeStaticNetsFromProblem() {
		//Remove static nets from the sourceBundlesCPY and add them to bundleSets
		bundleSets = new HashSet<Set<Connection>>();
		for(Pin source:c.sourceBundles.keySet()){
			boolean staticNet=true;
			Set<Connection> sourceBundle = sourceBundlesCPY.get(source);
			for(Connection con:sourceBundle){
				if(con.sinkFanin!=1){
					staticNet = false;
					break;
				}
			}
			if(staticNet){
				bundleSets.add(sourceBundle);
				Bundle b = new Bundle(sourceBundle);
				c.bundles.add(b);
				consCPY.removeAll(sourceBundle);
				sourceBundlesCPY.remove(source);
			}
		}
		//Something else
		for(Pin sink:c.eqSinkBundles.keySet()){
			boolean singleSinkBundle=true;
			Set<Connection> sinkBundle = sinkBundlesCPY.get(sink);
			for(Connection con:sinkBundle){
				if(con.sourceFanout!=1){
					singleSinkBundle = false;
					break;
				}
			}
			if(singleSinkBundle){
				bundleSets.add(sinkBundle);
				Bundle b = new Bundle(sinkBundle);
				c.bundles.add(b);
				consCPY.removeAll(sinkBundle);
				sinkBundlesCPY.remove(sink);
			}
		}
	}

	public void printBundles(){
		int bundleNr = 0;
//		System.out.println("Cons in circuit:"+c.cons.size());
//		int nrOfCons = 0;
		for(Set<Connection> cset:bundleSets){
			bundleNr++;
			System.out.println("Bundle "+bundleNr+":");
			for(Connection con:cset){
				System.out.println("\t"+con.source+" - "+con.sink);
//				nrOfCons++;
			}
		}
//		System.out.println("Cons in bundles: "+nrOfCons);
	}
		
	private void mColoring(int k){
		if(found)return;
		while(true){
			nextValue(k);
			if(color[k] == 0)return;
			if(k == NrOfVertices){
//				System.out.println("\tColouring found.");
				for(int i=1;i<=k;i++){
					y[i]=color[i];
//					if(i<40)System.out.print(color[i]+" ");//prevent overflow of line buffer (hence we take max 40 integers)
				}
				System.out.println();
				found = true;
				found1solution = true;
				return;
			}else mColoring(k+1);
		}
	}
	
	private void nextValue(int k){
		int j;
		while(true){
			color[k] = (color[k]+1)%(colorsAvailable+1);
			if(color[k]==0)return;
			for(j=1; j<=NrOfVertices; j++)if( (Edges[k][j] != 0) && (color[k] == color[j]) )break;
			if(j == NrOfVertices+1)return;
		}
	}
	
	private int totalConnectionsInBundles(){
		int total = 0;
		for(Bundle b:c.bundles){
			total+=b.conSet.size();
		}
		return total;
	}
	
}
