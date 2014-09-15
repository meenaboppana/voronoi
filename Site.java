import java.util.*;
public class Site {

	private Point s;
	private ArrayList<Point> vertexlist;
	
	public Site(Point p){
		s = p;
		vertexlist = new ArrayList<Point>();
	}
	
	public Point getPoint(){
		return s;
	}
	
	public void setPoint(Point p){
		s = p;
	}
	
	public void addVertex(Point p){
		vertexlist.add(p);
	}
	
	public ArrayList<Point> getVertexList(){
		return vertexlist;
	}
	
	//Sorts the points in counterclockwise order from North
	public void sortPoints()
	{
		//List of points to the right of s;
		//points to the left of s.
		ArrayList<Point> rightlist = new ArrayList<Point>();
		ArrayList<Point> leftlist = new ArrayList<Point>();
		
		//Slope breaks on points with the same x coordinate,
		//so we add these separately
		Point north = null;
		Point south = null;
		
		for(int j = 0; j < vertexlist.size(); j++)
		{
			if(vertexlist.get(j).getx() < s.getx())
				leftlist.add(vertexlist.get(j));
			else if(vertexlist.get(j).getx() > s.getx())
				rightlist.add(vertexlist.get(j));
			else //equality case
			{
				if(vertexlist.get(j).gety() > s.gety())
					north = vertexlist.get(j);
				else
					south = vertexlist.get(j);
			}
		}
		vertexlist = new ArrayList<Point>();
		
		//Slope also sets an internal variable inside the point.
		for(int i = 0; i < rightlist.size(); i++)
		{
			rightlist.get(i).Slope(s);
		}
		for(int i = 0; i < leftlist.size(); i++)
		{
			leftlist.get(i).Slope(s);
		}
		//Need to do it in log n time, because there are n sites
		//and we want the algorithm to be bounded by n log n.
		SlopeComparator c = new SlopeComparator();
		Collections.sort(rightlist, c);
		Collections.sort(leftlist, c);
		if(north != null)
			vertexlist.add(north);
		//Now add between 360 degrees and 180 degrees
		for(int i = 0; i < leftlist.size(); i++)
			vertexlist.add(leftlist.get(i));
		if(south != null)
			vertexlist.add(south);
		//Now add between 180 degrees and zero degrees
		for(int i = 0; i < rightlist.size(); i++)
			vertexlist.add(rightlist.get(i));
		
	}
	
	public String toString()
	{
		return s.toString();
	}
	
}

//Compares the slopes (internalized, relative to p) of p1 and p2.
class SlopeComparator implements Comparator<Point>
{
	public int compare(Point p1, Point p2){
		return Double.compare(p1.getSlope(), p2.getSlope());
	}
}