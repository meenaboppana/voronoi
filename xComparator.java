import java.util.Comparator;

public class xComparator implements Comparator<Point>
{
	public int compare(Point p1, Point p2)
	{
		if(p1.getx() < p2.getx()) return -1;
		else if(p1.getx() > p2.getx()) return 1;
		else return 0;
	}
}