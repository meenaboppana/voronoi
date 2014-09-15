//A parabola defined on an interval
public class ParabolaSegment
{
  private double start;
  private double end;
  private Parabola p;
  
  //Represents an invariant (place in the beachline) so should be final
  final double order;

  public ParabolaSegment (double s, double e, Parabola para, double o)
  {
         start = s;
         end = e;
         p = para;
         order = o;
  }

  public double getStart () {return start;}
    
  public double getEnd () {return end;}
 
  public Parabola getParabola () {return p;}
  
  public double getOrder() {return order;}

  public void setStart (double new_start) {start = new_start;}

  public void setEnd (double new_end) {end = new_end;}

  public void setParabola (Parabola new_p) {p = new_p;}
        
  public String toString()
  {
	return "From " + ((Integer) ((Double) start).intValue()).toString() + " to " + 
			((Integer) ((Double) end).intValue()).toString() + " and parabola is " +
			this.getParabola().toString();			
  }
}