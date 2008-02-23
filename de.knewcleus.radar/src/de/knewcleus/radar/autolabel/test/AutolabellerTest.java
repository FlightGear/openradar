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
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.knewcleus.radar.autolabel.Autolabeller;
import de.knewcleus.radar.autolabel.BoundedSymbol;
import de.knewcleus.radar.autolabel.LabelCandidate;
import de.knewcleus.radar.autolabel.LabelCostModel;
import de.knewcleus.radar.autolabel.LabeledObject;
import de.knewcleus.radar.autolabel.OverlapModel;

public class AutolabellerTest extends JPanel {
	protected final Set<PointObject> labeledObjects=new HashSet<PointObject>();
	protected final Random random=new Random();
	protected final Autolabeller autolabeller=new Autolabeller();

	public AutolabellerTest() {
		super();
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				autolabeller.removeOne();
				repaint();
			}
		});
	}
	
	public void createObjects(int n) {
		for (int i=0;i<n;i++) {
			double x,y;
			x=random.nextDouble();
			y=random.nextDouble();
			PointObject labeledObject=new PointObject(x,y,0.005);
			labeledObjects.add(labeledObject);
			autolabeller.addLabeledObject(labeledObject);
		}
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		Color transparentGrey=new Color(0.5f,0.5f,0.5f,0.25f);
		Graphics2D g2d=(Graphics2D)g;
		
		LabelCostModel costModel=autolabeller.getCostModel();
		
		OverlapModel<BoundedSymbol> overlapModel=autolabeller.getOverlapModel();
		
		double maximumCost=costModel.getMaximumCost();
		double minimumCost=costModel.getMinimumCost();
		
		double negativeCost=Math.min(0.0, minimumCost);
		double positiveCost=Math.max(0.0, maximumCost);
		
		for (PointObject labeledObject: labeledObjects) {
			double x,y,rx,ry;
			
			x=labeledObject.getX()*getWidth();
			y=labeledObject.getY()*getHeight();
			rx=labeledObject.getR()*getWidth();
			ry=labeledObject.getR()*getHeight();
			
			Ellipse2D ellipse2D=new Ellipse2D.Double(x-rx,y-ry,2*rx,2*ry);
			g2d.setColor(Color.BLACK);
			g2d.draw(ellipse2D);
			
			if (!overlapModel.getOverlaps(labeledObject).isEmpty()) {
				g2d.setColor(transparentGrey);
				g2d.fill(ellipse2D);
			}
		}
		
		for (LabelCandidate candidate: autolabeller.getCandidates()) {
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
			g2d.draw(rectangle2D);
			g2d.draw(line2D);
			
			double cost=costModel.getCandidateCost(candidate);
			
			if (cost>0.0 && positiveCost>0.0) {
				float relativeCost=(float)(cost/positiveCost);
				Color transparentRed=new Color(1.0f,1.0f-relativeCost,1.0f-relativeCost,0.75f);
				g2d.setColor(transparentRed);
				g2d.fill(rectangle2D);
			} else if (cost<0.0 && negativeCost<0.0) {
				float relativeCost=(float)(cost/negativeCost);
				Color transparentGreen=new Color(1.0f-relativeCost,1.0f,1.0f-relativeCost,0.75f);
				g2d.setColor(transparentGreen);
				g2d.fill(rectangle2D);
			}
		}
		
		LabelCandidate nextCandidate=autolabeller.getNextCandidate();
		
		if (nextCandidate!=null) {
			double top,bottom,left,right;
			
			top=nextCandidate.getTop()*getHeight();
			bottom=nextCandidate.getBottom()*getHeight();
			left=nextCandidate.getLeft()*getWidth();
			right=nextCandidate.getRight()*getWidth();
			
			Rectangle2D rectangle2D=new Rectangle2D.Double(left,top,right-left,bottom-top);
			
			g2d.setColor(new Color(1.0f,1.0f,0.0f,0.5f));
			g2d.fill(rectangle2D);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AutolabellerTest autolabellerTest=new AutolabellerTest();
		autolabellerTest.setPreferredSize(new Dimension(700,700));
		
		int objectCount=100;
		
		long startTime=System.nanoTime();
		autolabellerTest.createObjects(objectCount);
		long endTimeCreation=System.nanoTime();
		autolabellerTest.autolabeller.prepare();
		long endTimePreparation=System.nanoTime();
		
		autolabellerTest.autolabeller.label();
		
		long endTimeReduction=System.nanoTime();
		
		System.out.println("time for "+objectCount+" objects");
		System.out.println("    creation   : "+(endTimeCreation-startTime)*1.0E-9+" seconds");
		System.out.println("    preparation: "+(endTimePreparation-endTimeCreation)*1.0E-9+" seconds");
		System.out.println("    reduction  : "+(endTimeReduction-endTimePreparation)*1.0E-9+" seconds");
		System.out.println("    total      : "+(endTimeReduction-startTime)*1.0E-9+" seconds");
		
		JFrame frame=new JFrame("Autolabeller Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(autolabellerTest);
		autolabellerTest.setVisible(true);
		frame.setVisible(true);
		frame.pack();
	}

}
