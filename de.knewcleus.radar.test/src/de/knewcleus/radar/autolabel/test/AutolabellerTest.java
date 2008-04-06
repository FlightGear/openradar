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

import de.knewcleus.fgfs.IUpdateable;
import de.knewcleus.fgfs.Updater;
import de.knewcleus.radar.autolabel.ChargePotentialAutolabeller;
import de.knewcleus.radar.autolabel.Label;
import de.knewcleus.radar.autolabel.LabeledObject;

public class AutolabellerTest extends JPanel implements IUpdateable {
	private static final long serialVersionUID = 1541306043056168679L;
	protected final Random random=new Random();
	protected final ChargePotentialAutolabeller autolabeller=new ChargePotentialAutolabeller(1E-5,1E-3);

	public AutolabellerTest() {
		super(true);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton()==MouseEvent.BUTTON1) {
					/* Add a label at the given position */
					final double x,y;
					x=(double)e.getX()/getWidth();
					y=(double)e.getY()/getHeight();
					
					final double vx,vy;
					vx=(random.nextDouble()-0.5)*0.005;
					vy=(random.nextDouble()-0.5)*0.005;
					PointObject labeledObject=new PointObject(x,y,vx,vy,0.005);
					autolabeller.addLabeledObject(labeledObject);
				}
				repaint();
			}
		});
	}
	
	@Override
	public void update(double dt) {
		Set<LabeledObject> objectsToRemove=new  HashSet<LabeledObject>();
		for (LabeledObject labeledObject: autolabeller.getLabeledObjects()) {
			if (labeledObject instanceof PointObject) {
				PointObject pointObject=(PointObject)labeledObject;
				pointObject.update();
				if (pointObject.getX()<0.0 || pointObject.getX()>1.0 ||
						pointObject.getY()<0.0 || pointObject.getY()>1.0) {
					objectsToRemove.add(labeledObject);
				}
			}
		}
	
		for (LabeledObject labeledObject: objectsToRemove) {
			autolabeller.removeLabeledObject(labeledObject);
		}
		long startTime=System.currentTimeMillis();
		int i=0;
		while (System.currentTimeMillis()<startTime+100) {
			i++;
			autolabeller.updateOneLabel();
		}
		long endTimeReduction=System.currentTimeMillis();
		
		double totalRuns=(double)i/autolabeller.getLabeledObjects().size();
		System.out.println("time for "+totalRuns+" runs");
		System.out.println("    runtime    : "+(endTimeReduction-startTime)*1.0E-3+" seconds");
		
		repaint();
	}
	
	public void createObjects(int n) {
		for (int i=0;i<n;i++) {
			double x,y,vx,vy;
			x=random.nextDouble();
			y=random.nextDouble();
			vx=(random.nextDouble()-0.5)*0.005;
			vy=(random.nextDouble()-0.5)*0.005;
			PointObject labeledObject=new PointObject(x,y,vx,vy,0.005);
			autolabeller.addLabeledObject(labeledObject);
		}
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		Graphics2D g2d=(Graphics2D)g;
		
		for (LabeledObject object: autolabeller.getLabeledObjects()) {
			if (!(object instanceof PointObject))
				continue;
			PointObject labeledObject=(PointObject)object;
			double x,y,rx,ry;
			
			x=labeledObject.getX()*getWidth();
			y=labeledObject.getY()*getHeight();
			rx=labeledObject.getR()*getWidth();
			ry=labeledObject.getR()*getHeight();
			
			Ellipse2D pointMarker=new Ellipse2D.Double(x-rx,y-ry,2*rx,2*ry);
			g2d.setColor(Color.BLACK);
			g2d.draw(pointMarker);
			
			Label label=object.getLabel();
			final double top,bottom,left,right;
			
			final Rectangle2D labelBounds=label.getBounds2D();
			
			top=labelBounds.getMinY()*getHeight();
			bottom=labelBounds.getMaxY()*getHeight();
			left=labelBounds.getMinX()*getWidth();
			right=labelBounds.getMaxX()*getWidth();
			
			final Rectangle2D objectBounds=object.getBounds2D();
			final double cxo,cyo;
			cxo=objectBounds.getCenterX()*getWidth();
			cyo=objectBounds.getCenterY()*getHeight();
			
			double cx=(left+right)/2.0,cy=(top+bottom)/2.0;
			
			Rectangle2D labelRectangle=new Rectangle2D.Double(left,top,right-left,bottom-top);
			Line2D leaderLine=new Line2D.Double(cx,cy,cxo,cyo);

			g2d.draw(labelRectangle);
			g2d.draw(leaderLine);
			
			Line2D headingLine=new Line2D.Double(cxo,cyo,cxo+60*labeledObject.getVx()*getWidth(),cyo+60*labeledObject.getVy()*getHeight());
			
			g2d.draw(headingLine);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AutolabellerTest autolabellerTest=new AutolabellerTest();
		autolabellerTest.setPreferredSize(new Dimension(700,700));
		
		Updater updater=new Updater(autolabellerTest,250);
		updater.start();
		
		JFrame frame=new JFrame("ChargePotentialAutolabeller Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(autolabellerTest);
		autolabellerTest.setVisible(true);
		frame.setVisible(true);
		frame.pack();
	}

}
