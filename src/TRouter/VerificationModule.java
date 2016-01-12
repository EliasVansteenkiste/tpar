package TRouter;

import java.util.HashSet;

import architecture.RouteNode;
import architecture.RouteNodeType;
import circuit.Bundle;
import circuit.Circuit;
import circuit.Connection;
import circuit.Pin;

public class VerificationModule {

	Circuit c;

	public VerificationModule(Circuit c) {
		this.c = c;
	}

	public boolean verificateBundleTroute() {
		boolean legal = true;
		for (Bundle b1 : c.bundles) {
			if (!legal)
				break;
			for (Bundle b2 : c.bundles) {
				if (!legal)
					break;
				for (RouteNode rn : b1.routeNodes) {
					if (rn.type != RouteNodeType.SINK
							&& rn.type != RouteNodeType.SOURCE) {
						if (!legal)
							break;
						if (b2.routeNodes.contains(rn)) {
							HashSet<Pin> sinks = new HashSet<Pin>();
							HashSet<Pin> sources = new HashSet<Pin>();
							HashSet<Connection> cons = new HashSet<Connection>();
							for (Connection con : b1.conSet) {
								if (con.routeNodes.contains(rn)) {
									sinks.add(con.sink);
									sources.add(con.source);
									cons.add(con);
								}
							}
							for (Connection con : b2.conSet) {
								if (con.routeNodes.contains(rn)) {
									sinks.add(con.sink);
									sources.add(con.source);
									cons.add(con);
								}
							}
							if (sinks.size() > 1 && sources.size() > 1) {
								legal = false;
								System.out.println("failed at " + rn
										+ "(cons size:" + cons.size() + ")");
								for (Connection con : cons) {
									System.out.println(con.source + " - "
											+ con.sink + "("
											+ con.sink.owner.site.sink + ")");
								}
							}
						}
					}
				}

			}
		}
		return legal;
	}

	public boolean verificateConnectionRouter() {
		boolean legal = true;
		for (Connection c1 : c.cons) {
			if (!legal)
				break;
			for (Connection c2 : c.cons) {
				if (!legal)
					break;
				if (c1.source != c2.source && c1.sink != c2.sink) {
					for (RouteNode rn : c1.routeNodes) {
						if (rn.type != RouteNodeType.SINK && rn.type != RouteNodeType.SOURCE
								&& c2.routeNodes.contains(rn)) {
							System.out.println(c1.source + " - " + c1.sink
									+ " - " + c1.equivSink + "\n" + c2.source
									+ " - " + c2.sink + " - " + c2.equivSink
									+ "\n" + "\nshare the following resource\n"
									+ rn);
							legal = false;
						}
					}
				}
			}
		}
		return legal;
	}

	public boolean verificateConnectionRouterEQS() {
		boolean legal = true;
		for (Connection c1 : c.cons) {
			if (!legal)
				break;
			for (Connection c2 : c.cons) {
				if (!legal)
					break;
				if (c1.source != c2.source && c1.sink != c2.sink && c1.equivSink != c2.equivSink) {
					for (RouteNode rn : c1.routeNodes) {
						if (rn.type != RouteNodeType.SINK && rn.type != RouteNodeType.SOURCE
								&& c2.routeNodes.contains(rn)) {
							System.out.println(c1.source + " - " + c1.sink
									+ " - " + c1.equivSink + "\n" + c2.source
									+ " - " + c2.sink + " - " + c2.equivSink
									+ "\n" + "\nshare the following resource\n"
									+ rn);
							legal = false;
						}
					}
				}
			}
		}
		return legal;
	}

}
