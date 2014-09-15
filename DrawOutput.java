import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import java.util.*;

public class DrawOutput extends JFrame {

	private JPanel contentPane;
	private OutputPanel panel;
	private ArrayList<Site> sites;
	
	public DrawOutput(ArrayList<Site> sitelist)
	{
		sites = sitelist; 
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 600, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		panel = new OutputPanel();
		contentPane.add(panel, BorderLayout.CENTER);

	}

	class OutputPanel extends JPanel {
		ArrayList<Point> p;
		ArrayList<Point> l;
		double minx;
		double maxx;
		double miny;
		double maxy;
		//To make sure aspect ratio is preserved
		double min;
		double max;
		TreeSet<Point> vlist;
		
		//Global for how far we want to extend our external lines
		final int linelength = 10;
				
		public OutputPanel() {
	        setBorder(BorderFactory.createLineBorder(Color.black));
		    
	        //p is a list of all the points (including sites and vertices)
	        p = new ArrayList<Point>();
	        //vlist is a list of all the vertices
		    vlist = new TreeSet<Point>(new PointComparator()); 
	
		    for(int i = 0; i < sites.size(); i++)
		    {
		    	p.add(sites.get(i).getPoint());			
		    	for(int j = 0; j < sites.get(i).getVertexList().size(); j++)
		    	{
		    		Point pt = sites.get(i).getVertexList().get(j);
		    		p.add(pt);
		    		//vlist shouldn't contain duplicates
		    		if(!vlist.contains(pt))
		    			vlist.add(pt);
		    		
		    		//Sets the sites attached to any vertex 
		    		if(pt.gets1() == null)
		    			sites.get(i).getVertexList().get(j).sets1(sites.get(i));
		    		else if(pt.gets2() == null)
		    			sites.get(i).getVertexList().get(j).sets2(sites.get(i));
		    		else if(pt.gets3() == null)
		    			sites.get(i).getVertexList().get(j).sets3(sites.get(i));
		    		else
		    			throw new RuntimeException("Four sites have one vertex!"); //Not ready to deal with this case yet
		    	}
		    }
		    if(p.size() > 0)
		    {
		    	xComparator x = new xComparator();
		    	minx = Collections.min(p, x).getx();
		    	maxx = Collections.max(p, x).getx();
	        
		    	yComparator y = new yComparator();
		    	miny = Collections.min(p, y).gety();
		    	maxy = Collections.max(p, y).gety();
	        
		    	//For drawing purposes, to preserve aspect ratio
		    	if(minx < miny) min = minx; else min = miny;	        
		    	if(maxx > maxy) max = maxx; else max = maxy;
		    	
		    	//With enough points, some of the intersections are at 2000+. If these intersections are to be displayed,
		    	//the more interesting part of the diagram will appear small.
		    	if(min < -1000) min = -1000;
		    	if(max > 1000) max = 1000;
		    }
	    }

	    public Dimension getPreferredSize() {
	        return new Dimension(500,500);
	    }
	    
	    //Converts a double to a pixel, where (min, min) = (50, 50) and (max, max) = (400, 400) in pixels.
	    private int xtopixel(double x)
	    {
	    	return (int) ((Double) ((((x - min)/(max - min))*400) + 50)).intValue();
	    }
	    //Same as xtopixel, except we flip to account for the computer's preference of the *top* left as the origin.
	    private int ytopixel(double y)
	    {
	    	return 500 -  (int) ((Double) ((((y - min)/(max - min))*400) + 50)).intValue();
	    }
	    //Calculate the midpoint of two points.
	    private Point midpoint(Point a, Point b)
	    {
	    	return new Point((a.getx() + b.getx())/2, (a.gety() + b.gety())/2);
	    }
	    //Draw a line segment starting at point A on the line AB, pointing away from C. Useful for external lines.
	    private void drawThroughABAwayFromC(Point a, Point b, Point c, Graphics g)
	    {
	    	double drawtox;
	    	double drawtoy;
	    	//If A is closer to C than is B, then take A + linelength*(B-A).
	    	//For instance, if linelength = 1, then this just draws from A to B, which is indeed away from C.
	    	//Otherwise, take A - linelength*(B-A).
	    	//For instance, if linelength = 1, then this just draws from A to -B, which is indeed away from C.
	    	if(c.distance(a) < c.distance(b))
			    {
					drawtox = a.getx() + linelength*(b.getx() - a.getx());
					drawtoy = a.gety() + linelength*(b.gety() - a.gety());
			    }
			else
			    {
					drawtox = a.getx() - linelength*(b.getx() - a.getx());
					drawtoy = a.gety() - linelength*(b.gety() - a.gety());
			    }
			g.drawLine(xtopixel(drawtox), ytopixel(drawtoy), xtopixel(a.getx()), ytopixel(a.gety()));
	    }
	    
	    public void paintComponent(Graphics g) {
	        super.paintComponent(g);       
	       	        
	        Site s;
	        ArrayList<Point> q;
	        Point v1;
	        Point v2;
	        Point midpoint;
	        int nummysite1;
	        int nummysite2;
	        Site otherv1first;
	        Site otherv1second;
	        Site otherv2first;
	        Site otherv2second;
	        Point vt;
	        Site s1;
	        Site s2;
	        Site norm;
	        Site bothmissing;
	        boolean cont;
	        int numdrawn;

	        //Special case where there is no input
	        if(sites.size() == 0)
	        	return;
	        
	        //Special case where input is one point.
	        if(sites.size() == 1)
		    {
	        	g.setColor(Color.red);
	        	g.fillRect(xtopixel(sites.get(0).getPoint().getx()), ytopixel(sites.get(0).getPoint().gety()), 3, 3);
	        	g.setColor(Color.black);
	        	return;
		    }
	        
	        //Special case where input is two points.
	       	if(sites.size() == 2)
		    {                     
	       		Point a = sites.get(0).getPoint();
	       		Point b = sites.get(1).getPoint();
	       		g.setColor(Color.red);
			    g.fillRect(xtopixel(a.getx()), ytopixel(a.gety()), 3, 3);
			    g.fillRect(xtopixel(b.getx()), ytopixel(b.gety()), 3, 3);
	        	g.setColor(Color.black);
	        	
	        	//Slope of the perpendicular bisector
	        	double slope = -1/a.Slope(b);
	        	
	        	//The perpendicular bisector passes through...
	        	midpoint = new Point((a.getx() + b.getx())/2, (a.gety()+b.gety())/2);
	        	
	        	//A point on the line through (A, B) with slope m is given by (A + k, B + mk) for any k.
	        	Point point2 = new Point(midpoint.getx() + 2000, midpoint.gety() + 2000*slope);
	        	Point point1 = new Point(midpoint.getx() - 2000, midpoint.gety() - 2000*slope);
	        	g.drawLine(xtopixel(point1.getx()), ytopixel(point1.gety()), xtopixel(point2.getx()), ytopixel(point2.gety()));
		    }
	       	//First nontrivial case
	       	if(sites.size() == 3)
		    {
	       		Point a = sites.get(0).getPoint();
	       		Point b = sites.get(1).getPoint();
	       		Point c = sites.get(2).getPoint();
	       		Point center = sites.get(0).getVertexList().get(0);
	       		Point mid1 = midpoint(a, b);
	       		Point mid2 = midpoint(a, c);
	       		Point mid3 = midpoint(b, c);
	       		drawThroughABAwayFromC(center, mid1, c, g);
	       		drawThroughABAwayFromC(center, mid2, b, g);
	       		drawThroughABAwayFromC(center, mid3, a, g);			
		    }
	        //General case
	        for(int i = 0; i < sites.size(); i++)
	        {
	        	s = sites.get(i);
	        	//Order the vertices around this site
	        	s.sortPoints();
	        	g.setColor(Color.red);
	        	g.drawRect(xtopixel(s.getPoint().getx()), ytopixel(s.getPoint().gety()), 1, 1);
	        	g.setColor(Color.black);
	        	q = s.getVertexList();

	        	if(q.size() > 0)
	        	{	        	
		        	for(int j = 0; j < q.size() - 1; j++)
		        	{
		        		v1 = q.get(j);	
		        		v2 = q.get(j+1);
		        		//Determine if it's actually an edge, or just the completion of the polygon. 
		        		//If it's an edge, the two points have two vertices in common. Now, we load
		        		//the site numbers and vertices (this is where modularity comes in handy).
		        		nummysite1 = v1.gets(s);
		        		nummysite2 = v2.gets(s);
		        		otherv1first = v1.gets(nummysite1 + 1);
		        		otherv1second = v1.gets(nummysite1 + 2);
		        		otherv2first = v2.gets(nummysite2 + 1);
		        		otherv2second = v2.gets(nummysite2 + 2);
		        		//Clearly, both share site s. Determine if they share another site, and draw if they do.
		        		if(otherv1first == otherv2first || otherv1first == otherv2second 
		        				||otherv1second == otherv2first || otherv1second == otherv2second)
		        		{		        			
		        			g.drawLine(xtopixel(q.get(j).getx()), ytopixel(q.get(j).gety()), 
			        					xtopixel(q.get(j+1).getx()), ytopixel(q.get(j+1).gety()));
			        		
							g.drawRect(xtopixel(q.get(j).getx()), ytopixel(q.get(j).gety()), 1, 1);
							
							//Record that we drew a line corresponding to site s (so we can see if we need to draw external lines).
							//If we already drew two lines for the site (which means that double-counting is occurring),
							//or that Java called a repaint somewhere, then don't draw.
							if(v1.getint(nummysite1) < 2)
								v1.incint(nummysite1);
							if(v2.getint(nummysite2) < 2)
								v2.incint(nummysite2);
		        		}
		        	}
		        	//If only two points correspond to this vertex, we don't want to double back and count both. Method is as above.
		        	if(q.size() > 2)
		        	{
		        		v1 = q.get(q.size() - 1);
		        		v2 = q.get(0);
		        		nummysite1 = v1.gets(s);
		        		nummysite2 = v2.gets(s);
		        		otherv1first = v1.gets(nummysite1 + 1);
		        		otherv1second = v1.gets(nummysite1 + 2);
		        		otherv2first = v2.gets(nummysite2 + 1);
		        		otherv2second = v2.gets(nummysite2 + 2);
		        		if(otherv1first == otherv2first || otherv1first == otherv2second 
		        				||otherv1second == otherv2first || otherv1second == otherv2second)
		        		{
	        			
		        			g.drawLine(xtopixel(q.get(q.size() - 1).getx()), ytopixel(q.get(q.size() - 1).gety()), 
		        					xtopixel(q.get(0).getx()), ytopixel(q.get(0).gety()));
		        		
		        			g.drawRect(xtopixel(q.get(q.size() - 1).getx()), ytopixel(q.get(q.size() - 1).gety()), 1, 1);
						
		        			if(v1.getint(nummysite1) < 2)
		        				v1.incint(nummysite1);
		        			if(v2.getint(nummysite2) < 2)
		        				v2.incint(nummysite2);
		        		}
		        	}		        
	        	}	        	
	        }
	        //Determine if we need to draw external lines.
	        
	        
	        while(!vlist.isEmpty())
	        {
	        	s1 = null;
	        	s2 = null;
	        	norm = null; //The third vertex
	        	cont = true;
	        	bothmissing = null;
	        	
	        	vt = vlist.first();
	        	vlist.remove(vt);
	        	
	        	numdrawn = vt.getint1() + vt.getint2() + vt.getint3();
	        	//The (double counted) number of lines drawn from vt. Six denotes that all lines have been
	        	//drawn; four denotes that one external line should be drawn; two denotes that we need two
	        	//external lines; and zero denotes that no internal lines have been drawn. This should only
	        	//occur in the three case (which we handled above), or in the case where multiple site events coincide.

	        	//The boolean cont denotes whether this is a genuine case or not.
	        	
	        	//Filter out the case where we need to draw two lines (handled below)
	        	if(numdrawn > 3)
	        	{
	        		//Find the sites for which the vertex does not contain two segments, and the one for which it does (norm).
	        		//The external line must be drawn at these sites.
		        	if(vt.getint1() == 1) s1 = vt.gets1();
		        	else norm = vt.gets1();
		        	
		        	if(vt.getint2() == 1)
		        	{
		        		if(s1 == null) s1 = vt.gets2();
		        		else s2 = vt.gets2();
		        	}
		        	else
		        	{
		        		if(norm == null) norm = vt.gets2();
		        		else cont = false;
		        	}
		        	if(vt.getint3() == 1)
		        	{
		        		if(s1 == null) s1 = vt.gets3();
		        		else s2 = vt.gets3();
		        	}
		        	else
		        	{
		        		if(norm == null) norm = vt.gets3();
		        		else cont = false;
		        	}
		        	if(cont) drawThroughABAwayFromC(vt, midpoint(s1.getPoint(), s2.getPoint()), norm.getPoint(), g);
	        	}
	        	else if(numdrawn == 2 || numdrawn == 3)
	        	{
	        		//Bothmissing denotes the site for which no internal lines have been drawn yet at this vertex.
	        		if(vt.getint1() == 1) s1 = vt.gets1();
		        	else bothmissing = vt.gets1();
		        	
		        	if(vt.getint2() == 1)
		        	{
		        		if(s1 == null) s1 = vt.gets2();
		        		else s2 = vt.gets2();
		        	}
		        	else
		        	{
		        		if(bothmissing == null) bothmissing = vt.gets2();
		        		else cont = false;
		        	}
		        	if(vt.getint3() == 1)
		        	{
		        		if(s1 == null) s1 = vt.gets3();
		        		else s2 = vt.gets3();
		        	}
		        	else
		        	{
		        		if(bothmissing == null) bothmissing = vt.gets3();
		        		else cont = false;
		        	}
		        	if(cont)
		        	{
		        		if(bothmissing == null) return;
		        		drawThroughABAwayFromC(vt, midpoint(s1.getPoint(), bothmissing.getPoint()), s2.getPoint(), g);
		        		drawThroughABAwayFromC(vt, midpoint(s2.getPoint(), bothmissing.getPoint()), s1.getPoint(), g);	        		
		        	}
	        	}
	        }	        
	    }
	}
}

//Returns equal if the x and y coordinates are equal
class PointComparator implements Comparator<Point>
{
public int compare (Point p1, Point p2)
{
	if(p1.getx() < p2.getx()) return -1;
	else if(p1.getx() > p2.getx()) return 1;
	else if(p1.gety() < p2.gety()) return -1;
	else if(p1.gety() > p2.gety()) return 1;
	else return 0;
}
}
