package de.knewcleus.openradar.ui.test;

import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.knewcleus.openradar.ui.VerticalScrollPane;
import de.knewcleus.openradar.ui.plaf.refghmi.REFGHMILookAndFeel;
import de.knewcleus.openradar.ui.vehicles.FlightLevelListModel;

public class ScrollableListMenuTest {
	public static void main(String[] args) {
		LookAndFeel refghmiLookAndFeel=new REFGHMILookAndFeel();
		try {
			UIManager.setLookAndFeel(refghmiLookAndFeel);
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		final FlightLevelListModel listModel=new FlightLevelListModel(40,245,10);
		final JList listMenu=new JList(listModel);
		listMenu.setDragEnabled(false);
		VerticalScrollPane verticalScrollPane=new VerticalScrollPane();
		verticalScrollPane.getViewport().setView(listMenu);

		final int index=listModel.getIndexForLevel(220);
		listMenu.setSelectedIndex(index);
		listMenu.ensureIndexIsVisible(index);
		
		PointerInfo pointerInfo=MouseInfo.getPointerInfo();
		final JFrame frame=new JFrame("ScrollableListMenuTest", pointerInfo.getDevice().getDefaultConfiguration());;
		frame.setContentPane(verticalScrollPane);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		listMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int index=listMenu.getSelectedIndex();
				System.out.println(listModel.getLevelForIndex(index));
				frame.dispose();
			}
		});

	}
}
