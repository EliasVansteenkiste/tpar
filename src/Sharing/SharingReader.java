package Sharing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import circuit.Circuit;
import circuit.Clb;
import circuit.parser.conlist_ff.ParseException;
import circuit.parser.conlist_ff.Readconlist;


public class SharingReader {
	
	public static void main(String[] args) throws FileNotFoundException, ParseException {
		int K = Integer.parseInt(args[0]);
		int L = 2;
		String net_file = args[1];
		String sharing_file = args[2];
		
		Readconlist parser = new Readconlist(new FileInputStream(new File(net_file)));
		Circuit circuit = parser.read(K, L);
		readClbSharing(circuit, sharing_file);
		System.out.println("Net sharing verified");
	}
	
	private static void testAllClbsClassified(Circuit circuit, ActivationSet defaultSet) {
		for(Clb clb : circuit.clbs.values())
			if(clb.getActivationSet() == null) {
				System.err.println("Warning: Clb with name: " + clb.name + " not in an activation set");
				defaultSet.addClb(clb);
			}
	}

	private static class ActivationSetRead {
		int id;
		Collection<String> lut_names;
		Collection<String> tcon_names;
		Collection<String> ff_names;
		Collection<Integer> share_ids;
		
		public ActivationSetRead(int id, Collection<String> lut_names,
				Collection<String> tcon_names, Collection<String> ff_names,
				Collection<Integer> share_ids) {
			super();
			this.id = id;
			this.lut_names = lut_names;
			this.tcon_names = tcon_names;
			this.ff_names = ff_names;
			this.share_ids = share_ids;
		}
	}
	
	static Collection<ActivationSetRead> readActivationSets(String filename) throws FileNotFoundException {
		InputStream stream = new FileInputStream(new File(filename));
		Scanner sc = new Scanner(stream);
		
		Set<ActivationSetRead> activation_sets_read = new HashSet<ActivationSetRead>();
		
		while(sc.hasNext("set")) {
			sc.next("set");
			sc.next("id:");
			int set_id = sc.nextInt();
			sc.next("luts:");
			Collection<String> lut_names = new ArrayList<String>();
			while(!sc.hasNext("tcons:"))
				lut_names.add(sc.next());
			sc.next("tcons:");
			Collection<String> tcon_names = new ArrayList<String>();
			while(!sc.hasNext("ffs:"))
				tcon_names.add(sc.next());
			sc.next("ffs:");
			Collection<String> ff_names = new ArrayList<String>();
			while(!sc.hasNext("sharing"))
				ff_names.add(sc.next());
			sc.next("sharing");
			sc.next("ids:");
			Collection<Integer> share_ids = new ArrayList<Integer>();
			while(sc.hasNextInt())
				share_ids.add(sc.nextInt());
			//System.out.println(""+set_id + lut_names+ff_names + share_ids);
			activation_sets_read.add(
					new ActivationSetRead(set_id, lut_names, tcon_names, ff_names, share_ids));
		}
		sc.close();
		
		return activation_sets_read;
	}
	
	static Map<Integer, ActivationSet> constructActivationSets(Circuit circuit, Collection<ActivationSetRead> activation_sets_read) {
		Map<Integer, ActivationSet> asets = new HashMap<Integer, ActivationSet>();
		
		for(ActivationSetRead aset_read : activation_sets_read) {
			ActivationSet aset = new ActivationSet(aset_read.id);
			asets.put(aset.getId(), aset);
			for(String lut_name : aset_read.lut_names) {
				Clb clb = circuit.getClb(lut_name);
				if(clb!=null)
					aset.addClb(clb);
				clb = circuit.getClb(lut_name + "_not");
				if(clb!=null)
					aset.addClb(clb);
			}
			for(String tcon_name : aset_read.tcon_names) {
				Clb clb = circuit.getClb(tcon_name);
				if(clb!=null)
					aset.addClb(clb);
				clb = circuit.getClb(tcon_name + "_not");
				if(clb!=null)
					aset.addClb(clb);
			}
			for(String ff_name : aset_read.ff_names) {
				Clb clb = circuit.getClb(ff_name + "_ble");
				if(clb!=null)
					aset.addClb(clb);
			}
		}

		for(ActivationSetRead aset_read : activation_sets_read) {
			ActivationSet aset = asets.get(aset_read.id);
			for(Integer sharing_id : aset_read.share_ids)
				aset.addSharingOpportunity(asets.get(sharing_id));
		}
		return asets;
	}
	
	public static void readClbSharing(Circuit circuit, String sharing_file) throws FileNotFoundException {
		Collection<ActivationSetRead> activation_sets_read = readActivationSets(sharing_file);
		Map<Integer,ActivationSet> activation_sets = constructActivationSets(circuit, activation_sets_read);
		sanityCheck(activation_sets);

		testAllClbsClassified(circuit, activation_sets.get(1));
	}
	
	public static void sanityCheck(Map<Integer,ActivationSet> activation_sets) {
		for(ActivationSet set : activation_sets.values())
			set.sanityCheck();
	}

	public static void applyNoSharing(Circuit c) {
		ActivationSet aset = new ActivationSet(1);
		aset.addClbs(c.getClbs());
		aset.sanityCheck();
	}
}
