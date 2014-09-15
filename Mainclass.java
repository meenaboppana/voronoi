import java.util.*;

public class Mainclass {

	private ArrayList<Site> sites;

    //We let the beachline be an ordered tree of parabola segments, where the ordering is done by a 
	//variable "order" that is immutable for each parabola segment. This invariant is crucial to our program.
	private TreeSet <ParabolaSegment> beachline2 = new TreeSet<ParabolaSegment> (new OrderComparator());
	
    private ArrayList<Site> siteEvents;

    //We encode circle events as an array of 5 points, the first one being the bottom of the circle, the next three being the three foci of
    //parabola segments from left to right, and the final point being the circumcenter of the circle.
    private TreeSet <Site[]> circleEvents = new TreeSet<Site[]> (new circleComparator());

    // All circle/site events must occur within these x values.
    private final double minx = -1000000;
    private final double maxx = 1000000; 
    
    //Where the sweep line starts.
    private double on = 10000;
    
    //Calls a circle event if the interval on which the ParabolaSegment is defined is less than lengthlimit
    //at the correct sweep line for the circle event.
    private double lengthlimit= 10;
    
    //Double precision is an issue if the sweep line is assigned exactly to the circle event. Instead,
    //we assign the sweep line to a small amount above the actual circle event.
    private double updeviation = 0.001;    
    
    public Mainclass(ArrayList<Site> sitelist)
    {
    	this.run(sitelist);
    }

    //Generates the site events, takes the next event from siteEvent or circleEvents,
    //and calls update until no more events left. Then calls graphics.
    public void run(ArrayList<Site> sitelist)
    {
    	sites = sitelist;
    	siteEvents = new ArrayList<Site>();
    	genSiteEvents();
        
	   //"On" records the position of the current site event and update ensures that the next event occurs below this point
       //The first event must occur at y = {less than on}.
    	while(!(siteEvents.isEmpty()) || !(circleEvents.isEmpty())){
      	      on = update(on);
           }
    	
	   DrawOutput d = new DrawOutput(sitelist);
	   d.setVisible(true);
    }

    //Finds the circle or site event with highest y coordinate and calls circleEvent or siteEvent respectively.
    //Also checks that this event is not above the current sweep line.
   	private double update(double on){   
	    
	    if(siteEvents.isEmpty()){ 
	    	Site[] fce = circleEvents.first();
	    	if(on < fce[0].getPoint().gety())
	    		{
    				circleEvents.remove(fce);
    				return on;
	    		}
	    	circleEvent(circleEvents.first());
	    	circleEvents.remove(circleEvents.first());
	    	return fce[0].getPoint().gety();
	      }	
	      else{
	         if(circleEvents.isEmpty()){
	        	 on = siteEvents.get(0).getPoint().gety();
	        	 siteEvent(siteEvents.remove(0));
	        	 return on;	          
	          }
	          else{
	        	  if(circleEvents.first()[0].getPoint().gety() > siteEvents.get(0).getPoint().gety()){
	        		  Site[] fce = circleEvents.first();
	        		  if(on < fce[0].getPoint().gety())
	        		  {
	        			  circleEvents.remove(fce);
	        			  return on;
	        		  }
	        		  circleEvent(circleEvents.first());
	        		  circleEvents.remove(circleEvents.first());
	        		  return fce[0].getPoint().gety();
	              	}
	        	  	else{
	        	  		on = siteEvents.get(0).getPoint().gety();
	        	  		siteEvent(siteEvents.remove(0));   
	        	  		return on;
	        	  	}
	           	}
	      	}
   	}

   //Generates the site events.  Sorts in order of y coordinates from greatest to least.
    private void genSiteEvents(){ 
        for(int i=0; i < sites.size(); i++) {siteEvents.add(sites.get(i));}

        Collections.sort(siteEvents, new VerticalSiteComp());
    }
    
    //This is what causes our order problems. This is linear search, which causes our implementation be order n^2
    //instead of n log n. TreeSets do not allow us to access the root node, so in order to do a binary search
    //we would have to use a different data structure implementation. The Java libraries were not useful.
    //We were able to find other people's implementations of red-black or two-three trees that we could modify
    //to allow us to access the nodes, but we had to break their abstraction barrier to be able to use these,
    //and they seemed unreliable. (Many trees have search, but to run the algorithm properly, we would have to shift parabolas down
    //during each segment of the search. As a note, this means that the directrices of the beach line parabolas would not be the same.)
    //Our equivalent construction *almost* worked, but there were significantly more cases where there were minor failures -
    //meaning that our implementation had been correct, but perhaps there were cases when the red-black tree implementation
    //that we were using didn't work as designed.
    //We would rather trust our own strong invariant of order in the tree set than the invariants
    //of the two-three tree. Speed is not an issue for our numbers of points, but an "industrial strength" version would
    //require implementation of a suitable data structure here.
    private ParabolaSegment find(double d, double y)
    {	
	//Starting at the beginning of the beachline, we shift consecutive pairs of parabola segments down and check if 
        //d is under one of these segments.
    	ParabolaSegment p = beachline2.first();
    	while(p != null)
    	{
    		if(beachline2.higher(p) == null) {return p;}    			
    		shifttwo(p, beachline2.higher(p), y);
    		if(p.getStart() < d && p.getEnd() >= d) {return p;}
    		p = beachline2.higher(p);
    	}
    	return p;
    }
    
    //Same disclaimer applies as with find. Findshort specifies that the segment found must have a low length,
    //and that the nodes must match the ones calculated in the function updateCircleEvent.
    private ParabolaSegment findshort(double d, double y, Site s, Site c1, Site c2)
    {
    	ParabolaSegment p = beachline2.first();
    	while(p != null)
    	{
    		if(beachline2.higher(p) == null) return null;
   
   			shifttwo(p, beachline2.higher(p), y+updeviation);
    			
   			if(p.getStart() < d+1 && p.getEnd() >= d-1 && p != beachline2.first() && 
    				p.getParabola().getFocusSite() == s && p.getEnd() - p.getStart() < lengthlimit)
    			{
    				if((beachline2.lower(p).getParabola().getFocusSite() == c1 &&
    					beachline2.higher(p).getParabola().getFocusSite() == c2) ||
    					(beachline2.lower(p).getParabola().getFocusSite() == c2 &&
    					beachline2.higher(p).getParabola().getFocusSite() == c1))
    					return p;
    				
   					p = beachline2.higher(p);
    			}
    			p = beachline2.higher(p);		
    	}
   	return null;
    }
    
    //Handles a site event by shifting down the two corresponding parabolas associated with the x coordinate of the site event (left, center, and right) and 
    //adds a parabola segment in the correct spot to the beach line.
    private void siteEvent(Site s)
    {      
    	//Since the focus and the directrix are the same, this is a vertical line
       Parabola par = new Parabola(s, new Hline(s.getPoint().gety()));
       
       //Declare one parabolic arc, defined on (minx, maxx).
       if(beachline2.isEmpty())
 	  	{	      
 	      ParabolaSegment seg = new ParabolaSegment(minx, maxx, par, 2);
	      beachline2.add(seg);
 	      return;
 	  	}
       //Find the segment under which we need to remove segment.
       ParabolaSegment center = find(s.getPoint().getx(), s.getPoint().gety());
        
       //Case when the point is being inserted precisely under the intersection of the two arcs.
       if(center.getStart() == s.getPoint().getx() || center.getEnd() == s.getPoint().getx())
       {
    	   ParabolaSegment seg = new ParabolaSegment(s.getPoint().getx(), s.getPoint().getx(), par,
    			   (center.getOrder() + beachline2.lower(center).getOrder())/2);
    	   beachline2.add(seg);
    	   updateCircleEvents(seg);
    	   updateCircleEvents(beachline2.lower(seg));
    	   updateCircleEvents(beachline2.higher(seg));
    	   return;
       }
       //The general case is to remove center and put in three new parabolas.
       ParabolaSegment seg0;
       
       //if center is the lowest, define the order as just center/2. Otherwise, put the order as in between
       //center and the lowest. Nothing has the same order (until we get to machine epsilon).
       if(beachline2.lower(center) == null)
    	   seg0 = new ParabolaSegment(center.getStart(), s.getPoint().getx(), center.getParabola(),
    	   			center.getOrder()/2);
       else
    	   seg0 = new ParabolaSegment(center.getStart(), s.getPoint().getx(), center.getParabola(),
    			   (center.getOrder() + beachline2.lower(center).getOrder())/2);
       
       ParabolaSegment seg = new ParabolaSegment(s.getPoint().getx(),s.getPoint().getx(), par,
    		   		(seg0.getOrder() + center.getOrder())/2);

	   ParabolaSegment seg2 = new ParabolaSegment(s.getPoint().getx(), center.getEnd(), center.getParabola(), center.getOrder());

	   beachline2.remove(center);
	   beachline2.add(seg0);
	   beachline2.add(seg);
	   beachline2.add(seg2);
	   updateCircleEvents(seg0);
	   updateCircleEvents(seg);
	   updateCircleEvents(seg2);	   
   }
    
      
    //Given a new parabola segment, checks if there are any new circle events involving the site corresponding to the segment and adds them to 
    //the list of circle events.
    private void updateCircleEvents(ParabolaSegment par){
       ParabolaSegment previous = beachline2.lower(par);
       ParabolaSegment next = beachline2.higher(par);
       if (previous != null && next != null)
	   {
    	   Site[] event = new Site[5];

    	   event[1] = previous.getParabola().getFocusSite();
    	   event[2] = par.getParabola().getFocusSite();
    	   event[3] = next.getParabola().getFocusSite(); 
    	   event[0] = new Site(bottomCircle(event[1].getPoint(), event[2].getPoint(), event[3].getPoint()));
    	   event[4] = new Site(circumcenter(event[1].getPoint(), event[2].getPoint(), event[3].getPoint()));
    	   
    	   //Checks if any of the parabola segments are the same. In this case, there is no event.
    	   if(previous.getParabola().getFocusSite() != next.getParabola().getFocusSite() &&
    			   previous.getParabola().getFocusSite() != par.getParabola().getFocusSite() &&
    			   par.getParabola().getFocusSite() != next.getParabola().getFocusSite())
    	   {		    	 
    		   circleEvents.add(event);		     
    	   }
	   }
    }
    

    //Checks if the the three segments are still exist and are next to each other
    //Deletes the middle segment on the beach line - recalculate the parts of the beachline that are changing
    //Adds point to the set of vertices associated with a site
      private void circleEvent (Site[] p)
      {        
    	 //Could be written more efficiently once we have findshort.
       ParabolaSegment center = findshort(p[0].getPoint().getx(), p[0].getPoint().gety(), p[1], p[2], p[3]);
       
    	   if(center == null)
           {
        	   center = findshort(p[0].getPoint().getx(), p[0].getPoint().gety(), p[2], p[1], p[3]);
        	   if(center == null)
        	   {
        		   center = findshort(p[0].getPoint().getx(), p[0].getPoint().gety(), p[3], p[1], p[2]);
        	   }
        	   if(center == null)
        	   {
        		   return;
        	   }
           }
       if(beachline2.lower(center) == null || beachline2.higher(center) == null) return;       
       beachline2.remove(center);
       beachline2.lower(center).setEnd(center.getEnd());
       updateCircleEvents(beachline2.lower(center));
       updateCircleEvents(beachline2.higher(center));
              
	//Adds the circumcenter to the list of vertices associated with each of the three sites
		if(!p[1].getVertexList().contains(p[4].getPoint())) p[1].addVertex(p[4].getPoint());
		if(!p[2].getVertexList().contains(p[4].getPoint())) p[2].addVertex(p[4].getPoint());
		if(!p[3].getVertexList().contains(p[4].getPoint()))	p[3].addVertex(p[4].getPoint());
      }
      
    //FORMULA FROM WIKIPEDIA Finds the circumcenter of three points, assuming no two points have the same x coordinate.
    private Point circumcenter(Point a, Point b, Point c){
    	double ax = a.getx();
        double ay = a.gety();
        double bx = b.getx();
        double by = b.gety();
        double cx = c.getx();
        double cy = c.gety();
        double d = 2*(ax*(by-cy)+bx*(cy-ay)+cx*(ay-by));
 
        double center_x = ((ax*ax + ay*ay)*(by-cy) + (bx*bx + by*by)*(cy-ay) + (cx*cx + cy*cy)*(ay-by))/d;
        double center_y = ((ax*ax + ay*ay)*(cx-bx) + (bx*bx + by*by)*(ax-cx) + (cx*cx + cy*cy)*(bx-ax))/d;

        return new Point(center_x,center_y);
    }

    //Returns the bottom of the circumcircle of a, b, and c.
    private Point bottomCircle(Point a, Point b, Point c){
    	Point center = circumcenter(a, b, c);
    	double radius = a.distance(center);
    	return new Point(center.getx(), center.gety() - radius);
    }

    //Shift two parabolas downwards to y.
    public void shifttwo(ParabolaSegment left, ParabolaSegment right, double y)
    {
    	ArrayList<Point> intersect;
    	double newstart;
    	//Shiftng down something on the end is easy.
    	if(left == null) right.getParabola().setDirectrix(new Hline(y));
    	else if(right == null) left.getParabola().setDirectrix(new Hline(y));
    	else
    	{
    		right.getParabola().setDirectrix(new Hline(y));
    		left.getParabola().setDirectrix(new Hline(y));
    		intersect = left.getParabola().intersectParabola(right.getParabola());
    		//If there is no intersection, they are the same or something.
    		if(intersect.size() == 0)
    		{
    			return;
    		}
    		//If left has a higher focus, then left is flatter, so we want the first (leftmost) intersection.
    		//If right has a higher focus, then right is flatter, so we want the second (rightmost) intersection.
    		if(left.getParabola().getFocusSite().getPoint().gety() > right.getParabola().getFocusSite().getPoint().gety())
    			newstart = intersect.get(0).getx();
    		else
    			newstart = intersect.get(intersect.size() - 1).getx();
    		left.setEnd(newstart);
    		right.setStart(newstart);
    	}
    }
    
//Testing code
	public void printbeachline()
	{
		ParabolaSegment p;
		int i = 1;
		if(!beachline2.isEmpty())
		{
			p = beachline2.first();
		    System.out.println("First beach line part is " + ((Integer) ((Double) beachline2.first().getStart()).intValue()).toString() + " to " + ((Integer) ((Double) beachline2.first().getEnd()).intValue()).toString() + "and it is " + beachline2.first().toString());
		    while(beachline2.higher(p) != null)
		    {
		    	p = beachline2.higher(p);
		    	System.out.println(((Integer) (i+1)).toString() + "th beach line2 part is" + ((Integer) ((Double) p.getStart()).intValue()).toString() + " to " + ((Integer) ((Double) p.getEnd()).intValue()).toString() + " and it is " + p.toString());
		    	System.out.println("Order is " + ((Integer) ((Double) (p.getOrder()*1000)).intValue()).toString());
		    	i++;
		    }
		}
	}
    
//Compares points based on their y values, greatest to least
class VerticalSiteComp implements Comparator<Site>
{
	public int compare(Site h1, Site h2){
	    return Double.compare(h2.getPoint().gety(), h1.getPoint().gety());
	}
}

class OrderComparator implements Comparator<ParabolaSegment>
{
	public int compare(ParabolaSegment s1, ParabolaSegment s2)
	{
		if(s1.getOrder() < s2.getOrder()) return -1;
		else if(s1.getOrder() > s2.getOrder()) return 1;
		else return 0;
	}
}

//Compares circles by their y coordinates, greatest to least
class circleComparator implements Comparator<Site[]>
{
	public int compare(Site[] c1, Site[] c2)
	{
		if(c2[0].getPoint().gety() < c1[0].getPoint().gety()) return -1;
		else if(c1[0].getPoint().gety() < c2[0].getPoint().gety()) return 1;
		else return 0;
	}
}

}