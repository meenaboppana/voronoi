import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JButton;

import java.util.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

//The main entry point. Launches an instance of mainclass with
//the points that have been inputted.
public class InputClass extends JFrame implements ActionListener {

	private JPanel contentPane;
	private InputPanel panel;
    private JButton button;
	private ArrayList<Site> sites;
	
	//Want to check if the inputs have the same x and y coordinates.
	//As of now, we are not prepared to handle the case where they do.
    private TreeSet<Point> xcheck;
    private TreeSet<Point> ycheck;

    public InputClass()
	{
    	//There are precision problems that result from the points
    	//being too close together. We have the option to set multiplier,
    	//which spaces out the points.
	    final double multiplier = 1;
	    sites = new ArrayList<Site>();
	    xcheck = new TreeSet<Point>(new xComparator());
	    ycheck = new TreeSet<Point>(new yComparator());

	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 600, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		panel = new InputPanel(multiplier);
		panel.addMouseListener(new MouseListener() {
            
        public void mousePressed(MouseEvent e) 
        {
        	//Note y is reversed because the natural ordering to the computer puts
        	//(0, 0) in the top left, but the natural ordering to the human puts 
        	//(0, 0) in the bottom left.
        	Point p = new Point(multiplier*e.getX(), multiplier*(500-e.getY()));
        	//Reject the point if the x coordinate or the y coordinate already is contained. We considered requiring
        	//the new point to be outside a bounding box around the old points (close points are more likely to break it)
        	//but decided that this was better.
        	if(xcheck.floor(p) != null && xcheck.floor(p).getx() == p.getx())
        		System.out.println("Same x!");
        	else if(ycheck.floor(p) != null && ycheck.floor(p).gety() == p.gety())
        		System.out.println("Same y!");
        	else
		    {
        		xcheck.add(p);
        		ycheck.add(p);
        		sites.add(new Site(p));
        		
        		//Redraw the panel to reflect the new points.
        		panel.repaint();
		    }
        }
            public void mouseReleased(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseClicked(MouseEvent e) {}
        });
		button = new JButton("DRAW!");
		button.addActionListener(this);
		contentPane.add(panel, BorderLayout.CENTER);
		contentPane.add(button, BorderLayout.PAGE_START);
	}
    //The entry point for the program.
    public static void main(String[] args) {
    	InputClass i = new InputClass();
    	i.setVisible(true);
    }	
    public void actionPerformed(ActionEvent e)
    {
	if(e.getSource() == button)
	    {
			//Initialize the algorithmic part of the program.
	    	new Mainclass(sites);
	    	this.dispose();
	    }
    }

    public int getPoints() {return 0;}
    public boolean isDone() {return false;}

    class InputPanel extends JPanel
    {
	double multiplier;
	public InputPanel(double mult)
	{
        setBorder(BorderFactory.createLineBorder(Color.black));
	    multiplier = mult;
	}
	 public Dimension getPreferredSize() {
	        return new Dimension(500,500);
	    }

	public void paintComponent(Graphics g) {
		super.paintComponent(g);  
		g.setColor(Color.red);
	       
		for(int i = 0; i < sites.size(); i++)
		{
			//Remember to reverse y since the computer's natural order puts the origin at the top left but ours puts it at the bottom left.
			g.fillRect(((int) ((Double) (sites.get(i).getPoint().getx()/multiplier)).intValue()), 
					500-((int) ((Double) (sites.get(i).getPoint().gety()/multiplier)).intValue()), 3, 3);
		}
	}
    }

}

