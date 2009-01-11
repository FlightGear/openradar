package de.knewcleus.openradar.ui.test;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;

import de.knewcleus.openradar.view.mouse.MouseInteractionManager;

public class MouseInteractionTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final JFrame frame = new JFrame("Mouse Click Test");
		final JLabel label = new JLabel();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(label);
		frame.setSize(300, 50);
		final MouseInteractionManager interactionManager = new MouseInteractionManager(MouseEvent.BUTTON1, MouseEvent.BUTTON1_DOWN_MASK);
		label.addMouseListener(interactionManager);
		label.addMouseMotionListener(interactionManager);
		label.addMouseListener(new MouseAdapter() {
			long start;
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton()!=MouseEvent.BUTTON1) {
					return;
				}
				final long timeMillis = System.currentTimeMillis() - start;
				label.setText(String.format("%5dms",timeMillis));
				System.out.println("internal click count="+e.getClickCount());
				start = System.currentTimeMillis();
			}
		});
		frame.setVisible(true);
	}

}
