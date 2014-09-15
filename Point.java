//Represents a point in the xy plane.
public class Point {

	private double x;
	private double y;
	
	//Useful for sorting the points about a vertex
	private double slope;
	
	//A vertex has three (or more, but we don't deal with this
	//case) sites which contain the vertex. 
	private Site s1 = null;
	private Site s2 = null;
	private Site s3 = null;
	
	//sicount denotes the number of lines that have been
	//drawn corresponding to the ith site. For an internal 
	//vertex, each site has a complete polygon, so each
	//vertex of that site is reached twice. Then, any site
	//should have two lines drawn for each vertex. This is
	//useful to determine which vertices are external.
	private int s1count = 0;
	private int s2count = 0;
	private int s3count = 0;
	
	public Point(double xh, double yh)
	{
		x = xh;
		y = yh;
	}
	
	//Shouldn't really be necessary but...
	public void setx (double xh) {x = xh;}
	public void sety (double yh) {y = yh;}
	
	public double getx () {return x;}
	public double gety () {return y;}

	public double getSlope() {return slope;}

	public double Slope (Point p)
	{
		if(p.getx() == x) throw new ArithmeticException("Vertical line!");
		else slope = (p.gety() - y)/(p.getx() - x);
		return slope;
	}

	public double distance (Point p){
		double x1 = Math.pow(p.getx() - x, 2);
		double x2 = Math.pow(p.gety() - y, 2);
		return Math.sqrt(x1+x2);
	}
	
	public Site gets1() {return s1;}
	public Site gets2() {return s2;}
	public Site gets3() {return s3;}
	
	public void sets1(Site s) {s1 = s;}
	public void sets2(Site s) {s2 = s;}
	public void sets3(Site s) {s3 = s;}
	
	public int getint1() {return s1count;}
	public int getint2() {return s2count;}
	public int getint3() {return s3count;}
	
	public void setint1(int i) {s1count = i;}
	public void setint2(int i) {s2count = i;}
	public void setint3(int i) {s3count = i;}
	
	public void incint1() {s1count++;}
	public void incint2() {s2count++;}
	public void incint3() {s3count++;}
	
	//Returns whichever number site is equal to s.
	public int gets(Site s)
	{
		if(s1 == s) return 1;
		else if(s2 == s) return 2;
		else if(s3 == s) return 3;
		else return 0;
	}
	
	//returns whichever site is equal to i%3. The modularity
	//reduces casework in DrawOutput.
	public Site gets(int i)
	{
		if((i%3) == 1) return s1;
		else if((i%3) == 2) return s2;
		else if((i%3) == 0) return s3;
		else return null;
	}
	
	public void incint(int i)
	{
		if((i%3) == 1) s1count++;
		else if((i%3) == 2) s2count++;
		else if((i%3) == 0) s3count++;
	
	}
	public int getint(int i)
	{
		if((i%3) == 1) return s1count;
		else if((i%3) == 2) return s2count;
		else if((i%3) == 0) return s3count;
		else return 0;
	}
	
	public String toString()
	{
		return "(" + ((Integer) ((Double) x).intValue()).toString() + 
		 "," + ((Integer) ((Double) y).intValue()).toString() + ")";
	}
}