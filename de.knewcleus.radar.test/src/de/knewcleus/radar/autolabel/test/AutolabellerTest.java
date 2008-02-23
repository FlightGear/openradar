package de.knewcleus.radar.autolabel.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.knewcleus.radar.autolabel.Autolabeller;
import de.knewcleus.radar.autolabel.BoundedSymbol;
import de.knewcleus.radar.autolabel.LabelCandidate;
import de.knewcleus.radar.autolabel.LabeledObject;
import de.knewcleus.radar.autolabel.OverlapModel;

public class AutolabellerTest extends JPanel {
	protected final Random random=new Random();
	protected final Autolabeller autolabeller=new Autolabeller();

	public AutolabellerTest() {
		super(true);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Set<LabeledObject> currentObjects=new HashSet<LabeledObject>(autolabeller.getCurrentLabelling().keySet());
				int removed=0;
				for (LabeledObject object: currentObjects) {
					if (random.nextDouble()<0.001) {
						removed++;
						autolabeller.removeLabeledObject(object);
					} else {
						((PointObject)object).update();
					}
				}
				
				if (random.nextDouble()<0.05) {
					createObjects((int)(random.nextDouble()*10));
				}
				long startTime=System.currentTimeMillis();
				int changes=autolabeller.label(startTime+250);
				long endTimeReduction=System.currentTimeMillis();
				
				System.out.println("time for "+autolabeller.getCurrentLabelling().size()+" objects, changed "+changes+" labels");
				System.out.println("    runtime    : "+(endTimeReduction-startTime)*1.0E-3+" seconds");
				repaint();
			}
		});
	}
	
	public void createObjects(int n) {
		for (int i=0;i<n;i++) {
			double x,y,vx,vy;
			x=random.nextDouble();
			y=random.nextDouble();
			vx=(random.nextDouble()-0.5)*0.01;
			vy=(random.nextDouble()-0.5)*0.01;
			PointObject labeledObject=new PointObject(x,y,vx,vy,0.005);
			autolabeller.addLabeledObject(labeledObject);
		}
	}
	
	private <T> boolean intersects(Collection<? extends T> c1, Collection<? extends T> c2) {
		for (T obj: c1) {
			if (c2.contains(obj))
				return true;
		}
		return false;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		Color transparentGrey=new Color(0.5f,0.5f,0.5f,0.25f);
		Graphics2D g2d=(Graphics2D)g;
		
		OverlapModel<BoundedSymbol> overlapModel=autolabeller.getOverlapModel();
		
		Map<LabeledObject, LabelCandidate> currentLabelling=autolabeller.getCurrentLabelling();
		Collection<LabelCandidate> activeLabels=currentLabelling.values();
		
		for (LabeledObject object: currentLabelling.keySet()) {
			PointObject labeledObject=(PointObject)object;
			double x,y,rx,ry;
			
			x=labeledObject.getX()*getWidth();
			y=labeledObject.getY()*getHeight();
			rx=labeledObject.getR()*getWidth();
			ry=labeledObject.getR()*getHeight();
			
			Ellipse2D ellipse2D=new Ellipse2D.Double(x-rx,y-ry,2*rx,2*ry);
			g2d.setColor(Color.BLACK);
			g2d.draw(ellipse2D);
			
			Set<BoundedSymbol> overlaps=overlapModel.getOverlaps(labeledObject);
			if (intersects(overlaps, activeLabels)) {
				g2d.setColor(transparentGrey);
				g2d.fill(ellipse2D);
			}
		}
		
		for (LabelCandidate candidate: activeLabels) {
			LabeledObject object=candidate.getAssociatedObject();
			double top,bottom,left,right;
			
			top=candidate.getTop()*getHeight();
			bottom=candidate.getBottom()*getHeight();
			left=candidate.getLeft()*getWidth();
			right=candidate.getRight()*getWidth();
			
			double cxo,cyo;
			cxo=(object.getLeft()+object.getRight())*getWidth()/2.0;
			cyo=(object.getTop()+object.getBottom())*getHeight()/2.0;
			
			double cx=(left+right)/2.0,cy=(top+bottom)/2.0;
			
			Rectangle2D rectangle2D=new Rectangle2D.Double(left,top,right-left,bottom-top);
			Line2D line2D=new Line2D.Double(cx,cy,cxo,cyo);
			
			g2d.setColor(Color.BLACK);
			
			String conflictCount=Integer.toString(autolabeller.getOverlapModel().getOverlaps(candidate).size());
			int width=g2d.getFontMetrics().stringWidth(conflictCount);
			int height=g2d.getFontMetrics().getMaxAscent()+g2d.getFontMetrics().getMaxDescent();
			
			double tx=(left+right-width)/2.0;
			double ty=(top+bottom-height)/2.0+g2d.getFontMetrics().getMaxAscent();
			
			g2d.drawString(conflictCount, (float)tx, (float)ty);
			g2d.draw(rectangle2D);
			g2d.draw(line2D);
			
			if (autolabeller.getConflictingLabels().contains(candidate.getAssociatedObject())) {
				g2d.setColor(new Color(1.0f,0.0f,0.0f,0.5f));
				g2d.fill(rectangle2D);
			}
		}
		
		g2d.setColor(Color.RED);
		g2d.drawString(Double.toString(autolabeller.getCurrentCost())+" "+Double.toString(autolabeller.getCurrentTemperature()), 0, getHeight()-g2d.getFontMetrics().getMaxDescent());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AutolabellerTest autolabellerTest=new AutolabellerTest();
		autolabellerTest.setPreferredSize(new Dimension(700,700));
		
		autolabellerTest.createObjects(5);
		
		JFrame frame=new JFrame("Autolabeller Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(autolabellerTest);
		autolabellerTest.setVisible(true);
		frame.setVisible(true);
		frame.pack();
	}

}
