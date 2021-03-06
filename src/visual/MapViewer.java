package visual;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import architecture.FourLutSanitizedDisjoint;
import architecture.RouteNode;
import circuit.Circuit;
import circuit.Connection;
import circuit.Net;

public class MapViewer {
	JPanel mainPanel;

	ConnectionPanel conPanel;

	FourLutSanitizedDisjoint a;
	Circuit c;
	ArchitecturePanel architecturePanel;
	Connection selectedCon;

	private void createAndShowGUI() {
		System.out.println("Created GUI on EDT? "
				+ SwingUtilities.isEventDispatchThread());
		JFrame f = new JFrame("Mapping result viewer");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setExtendedState(f.getExtendedState() | JFrame.MAXIMIZED_BOTH);

		architecturePanel = new ArchitecturePanel(700, a, c);

		architecturePanel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				RouteNode pressedNode = architecturePanel.getNode(e.getX(),
						e.getY());
				System.out.println(pressedNode);

				architecturePanel.setNodeColor(Color.GRAY);

				if (c.conRouted) {
					for (Connection con : c.cons) {
						if (con.routeNodes.contains(pressedNode)) {
							selectedCon = con;
							conPanel.setCon(selectedCon);
						} else {
							System.out.println("yeah");
						}
					}
				}

				architecturePanel.repaint();
			}
		});

		if (c.conRouted) {
			conPanel = new ConnectionPanel(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {
					conPanel.setChecked(e);

					for (Connection con : conPanel.selectedConnections()) {
						architecturePanel.setNodeColor(con, Color.BLUE);
					}
					for (Connection con : conPanel.nonselectedConnections()) {
						architecturePanel.setNodeColor(con, Color.GREEN);
					}

					architecturePanel.repaint();

				}

			}, c);

			for (Connection con : conPanel.connections) {
				architecturePanel.setNodeColor(con, Color.BLUE);
			}

		}

		mainPanel = new JPanel();
		mainPanel.add(architecturePanel);
		if (c.conRouted) {
			mainPanel.add(conPanel);
		}

		f.setContentPane(mainPanel);
		f.setExtendedState(f.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		f.setVisible(true);

	}

	public MapViewer(FourLutSanitizedDisjoint a, Circuit c) {
		super();
		this.a = a;
		this.c = c;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});

	}
}
