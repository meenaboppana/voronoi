import java.util.Comparator;

public class yComparator implements Comparator<Point>
{
	public int compare(Point p1, Point p2)
	{
		if(p1.gety() < p2.gety()) return -1;
		else if(p1.gety() > p2.gety()) return 1;
		else return 0;
	}
}