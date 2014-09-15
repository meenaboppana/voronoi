import java.util.*;

public class Parabola
{
  //A parabola can be defined relative to a point, whether
  //or not the point is embedded in a site.
  private Site site;

  private Hline directrix;

  public Parabola (Site s, Hline d)
  {
	  site = s;
	  directrix = d;
  }
 
  public Site getFocusSite () {return site;}
 
  public Hline getDirectrix () {return directrix;}
  
  public void setDirectrix (Hline newDirectrix) {directrix = newDirectrix;}

  //For testing purposes
  public String toString()
  {
	 return "Point is: " + this.getFocusSite().toString() + 
			 " and directrix is at y = " + 
			 ((Integer) ((Double) this.getDirectrix().gety()).intValue()).toString();
  }
  
  //Return the point(s) of the intersection of this
  //with i, ordered by x.
  public ArrayList <Point> intersectParabola (Parabola i)
  {       
      Point focus1 = this.getFocusSite().getPoint ();
      Point focus2 = i.getFocusSite().getPoint ();
      Hline directrix1 = this.getDirectrix ();
      Hline directrix2 = i.getDirectrix ();     

      ArrayList<Point> soltn = new ArrayList<Point>();       
      
      double a = focus1.getx ();
      double b = focus1.gety ();
      double c = directrix1.gety ();
      double d = focus2.getx ();
      double e = focus2.gety ();
      double f = directrix2.gety ();
      
      //Upward deviation. Check if the focus and directrix are equal.
      if(Math.abs(b - c) < 0.2)
      {
    	  soltn.add(new Point(a, ((a-d)*(a-d) + e*e - f*f)/(2*(e-f))));
    	  return soltn;
      }
      if(Math.abs(e - f) < 0.2)
  	  {
    	  soltn.add(new Point(d, ((d-a)*(d-a) + b*b - c*c)/(2*(b-c))));
  	  }
           
      double p = 1/(b-c) - 1/(e-f);
      double q = 2*d/(e-f) - 2*a/(b-c);
      double r = (a*a+b*b-c*c)/(b-c) - (d*d + e*e - f*f)/(e-f);

      double discriminant = q*q-4*p*r;

    
      if (p == 0) 
	  {
	      if (q!=0){
	    	  soltn.add(new Point ((-r/q), 
	    			  (((-r/q)-a)*((-r/q)-a) + b*b - c*c)/(2*(b-c))));
	      }
	      return soltn;
	  }
      if (discriminant > 0)
      {
    	  double x1 = (-q-Math.sqrt(discriminant))/(2*p);
    	  double x2 = (-q+Math.sqrt(discriminant))/(2*p);

    	  if(x1 < x2)
    	  {	   
    		  soltn.add(new Point (x1, ((x1-a)*(x1-a) + b*b - c*c)/(2*(b-c))));
    		  soltn.add(new Point (x2, ((x2-a)*(x2-a) + b*b - c*c)/(2*(b-c))));
    	  }
    	  else
    	  {
    		  soltn.add(new Point (x2, ((x2-a)*(x2-a) + b*b - c*c)/(2*(b-c))));
    		  soltn.add(new Point (x1, ((x1-a)*(x1-a) + b*b - c*c)/(2*(b-c))));
    	  }    
      }
      else if (discriminant == 0)
      { 
    	 double x = -q/(2*p);
         soltn.add(new Point (x,((x-a)*(x-a) + b*b - c*c)/(2*(b-c))));
      }
      //Since all parabolas have focus above the directrix,
      //they have positive first coordinate and are upwards
      //pointing. Then, if they are not the same parabola,
      //they should intersect. However, it's possible that one of them
      //is nearly a vertical line, so we're missing something. 
      //In this special case, where one of them is a vertical line 
      //k, the solution to the other is y = f(k).
      else{    	  
    	  if(Math.abs(b - c) < 5)
          {
        	  soltn.add(new Point(a, ((a-d)*(a-d) + e*e - f*f)/(2*(e-f))));
        	  return soltn;
          }
    	  else if(Math.abs(e - f) < 5)
      	  {
        	  soltn.add(new Point(d, ((d-a)*(d-a) + b*b - c*c)/(2*(b-c))));
      	  }
    	  else throw new ArithmeticException (this.toString() + " intersected with " + i.toString());

      }
        return soltn;
  }
}
