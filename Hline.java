//Represents a horizontal line with coordinate y.
//Useful for representing the directrix of a parabola,
//and the sweep line.
public class Hline {

	private double y;
	
	public Hline(double yt){
		y = yt;
	}
	
	public void sety(double yt){
		y = yt;
	}
	
	public double gety(){
		return y;
	}
}