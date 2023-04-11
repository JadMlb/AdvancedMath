package Functionalities;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.FontMetrics;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import Graphs.Function;
import Graphs.Point;
import Numbers.FloatValue;
import Numbers.Number;

/**
 * A class that plots multiple functions on a {@code JPanel}.
 * Can be displayed in its own frame or it can be added to one
 */
public class Graph extends JPanel
{
	private double[] xRange = new double [2];
	private String[] axesNames = new String [] {"x", "y"};
	private String title = "Graph";
	private double xStep = 0.01;
	private HashMap<Function, Color> fnColors = new HashMap<>();
	private HashMap<Function, HashMap<Double, Double>> fnPoints = new HashMap<>();
	private HashMap<Function, Integer> fnIndex = new HashMap<>();
	private JButton legend = new JButton ("Legend");
	private DefaultListModel<String> dlm = new DefaultListModel<>();
	private JList<String> legendList = new JList<> (dlm);

	/**
	 * Creates a new {@code Graph} with the given bounds and the default title "Graph"
	 * <ul>
	 * 	<li>If {@code xRangeStart} or {@code xRangeEnd} are {@code Double.NEGATIVE_INFINITY} or {@code Double.POSITIVE_INFINITY}, only 100 divisions are taken</li>
	 * 	<li>If both bounds are infinite, the result is between -50 and 50</li>
	 * 	<li>If {@code xRangeStart} is {@code Double.NEGATIVE_INFINITY}, it is set to -50</li>
	 * </ul>
	 * 
	 * @param xRangeStart The lower bound to show on the graph
	 * @param xRangeEnd The upper bound to show on the graph
	 */
	public Graph (double xRangeStart, double xRangeEnd)
	{
		super();

		setSize (700, 700);
		
		if (xRange[0] == Double.NEGATIVE_INFINITY)
			xRange[0] = -50;
		else
			xRange[0] = xRangeStart;

		if (xRange[1] == Double.POSITIVE_INFINITY)
			xRange[1] = xRange[0] + 100;
		else
			xRange[1] = xRangeEnd;

		legendList.setFont (legendList.getFont().deriveFont (12f));
		legend.addActionListener
		(
			new ActionListener ()
			{
				@Override
				public void actionPerformed (ActionEvent e)
				{
					JFrame legend = new JFrame (title + ": legend");
					JPanel p = new JPanel();
					p.setBorder (BorderFactory.createEmptyBorder (20, 20, 20, 20));
					p.setBackground (Color.WHITE);
					p.add (legendList);
					legend.add (p);
					legend.pack();
					legend.setVisible (true);
				}
			}
		);

		legend.setBounds (getWidth() - 100, getHeight() - 100, 75, 75);

		this.add (legend);
	}
	
	/**
	 * Creates a new {@code Graph} with the given bounds with the given title and labels. If any title / label is {@code null}, the default value is taken
	 * <ul>
	 * 	<li>If {@code xRangeStart} or {@code xRangeEnd} are {@code Double.NEGATIVE_INFINITY} or {@code Double.POSITIVE_INFINITY}, only 100 divisions are taken</li>
	 * 	<li>If both bounds are infinite, the result is between -50 and 50</li>
	 * 	<li>If {@code xRangeStart} is {@code Double.NEGATIVE_INFINITY}, it is set to -50</li>
	 * </ul>
	 * 
	 * @param xRangeStart The lower bound to show on the graph
	 * @param xRangeEnd The upper bound to show on the graph
	 * @param title The title of the graph (default: "Graph")
	 * @param xTitle The label of the x axis (default: "x")
	 * @param yTitle The label of the y axis (default: "y")
	 */
	public Graph (double xRangeStart, double xRangeEnd, String title, String xTitle, String yTitle)
	{
		super();

		setSize (700, 700);
		
		if (xRange[0] == Double.NEGATIVE_INFINITY)
			xRange[0] = -1000;
		else
			xRange[0] = xRangeStart;

		if (xRange[1] == Double.POSITIVE_INFINITY)
			xRange[1] = xRange[0] + 1000;
		else
			xRange[1] = xRangeEnd;
		
		if (xTitle != null && !xTitle.isBlank())
			axesNames[0] = xTitle;
		
		if (yTitle != null && !yTitle.isBlank())
			axesNames[1] = yTitle;

		if (title != null && !title.isBlank())
			this.title = title;

		legendList.setFont (legendList.getFont().deriveFont (12f));
		legend.addActionListener
		(
			new ActionListener ()
			{
				@Override
				public void actionPerformed (ActionEvent e)
				{
					JFrame legend = new JFrame (title + ": legend");
					JPanel p = new JPanel();
					p.setBorder (BorderFactory.createEmptyBorder (20, 20, 20, 20));
					p.setBackground (Color.WHITE);
					p.add (legendList);
					legend.add (p);
					legend.pack();
					legend.setVisible (true);
				}
			}
		);

		legend.setBounds (getWidth() - 100, getHeight() - 100, 75, 75);

		this.add (legend);
	}

	/**
	 * Adds a function to the set to be plotted. Will not display the plotted function, nor the graph
	 * 
	 * @param function {@code Function} to be plotted
	 * @param color	{@code Color} that this function will be plotted with
	 * @see Graph#show()
	 */
	public void plot (Function function, Color color)
	{
		fnColors.put (function, color);
		getPoints (function);
		fnIndex.put (function, dlm.getSize());
		
		String legendElement = "<html><span style = 'color: rgba(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ",1);'>&mdash;</span>" + function.toString();
		
		((DefaultListModel<String>) legendList.getModel()).addElement (legendElement);
	}

	/**
	 * Removes the given function from the set of the plotted ones. Will not refresh the graph.
	 * 
	 * @param function The {@code Function} to be removed
	 */
	public void removeFunction (Function function)
	{
		fnColors.remove (function);
		fnPoints.remove (function);
		((Container) legendList.getModel()).remove (fnIndex.get (function));
		fnIndex.remove (function);
	}

	private void getPoints (Function f)
	{
		HashMap<String, Number> values = new HashMap<>();
		for (double i = xRange[0]; i <= xRange[1] && i <= 1000; i += xStep)
		{
			values.put (f.getVariables().iterator().next(), Number.real (i));
			
			if (fnPoints.get (f) == null)
				fnPoints.put (f, new HashMap<>());

			fnPoints.get(f).put (i, Number.valueOf (f.toNode(), values).getX().getDoubleValue());
		}
	}

	/**
	 * Gets the title of the graph
	 * 
	 * @return The current title of the graph
	 */
	public String getTitle ()
	{
		return this.title;
	}

	/**
	 * Sets the title of the graph
	 * 
	 * @param title The new tittle of the graph
	 */
	public void setTitle (String title)
	{
		this.title = title;
	}
	
	public String getXLabel ()
	{
		return this.axesNames[0];
	}

	public void setXLabel (String label)
	{
		this.axesNames[0] = label;
	}
	
	public String getYLabel ()
	{
		return this.axesNames[1];
	}

	public void setYLabel (String label)
	{
		this.axesNames[1] = label;
	}

	public double[] getXRange ()
	{
		return xRange;
	}

	/**
	 * Sets the range shown on the {@code Graph}
	 * 
	 * <ul>
	 * 	<li>If {@code start} or {@code end} are {@code Double.NEGATIVE_INFINITY} or {@code Double.POSITIVE_INFINITY}, only 100 divisions are taken</li>
	 * 	<li>If both bounds are infinite, the result is between -50 and 50</li>
	 * 	<li>If {@code start} is {@code Double.NEGATIVE_INFINITY}, it is set to -50</li>
	 * </ul>
	 * 
	 * @param start The new starting x value
	 * @param end The new end of the range on the x axis
	 */
	public void setXRange (double start, double end)
	{
		if (xRange[0] == Double.NEGATIVE_INFINITY)
			xRange[0] = -50;
		else
			xRange[0] = start;

		if (xRange[1] == Double.POSITIVE_INFINITY)
			xRange[1] = xRange[0] + 100;
		else
			xRange[1] = end;
		
		for (Function f : fnPoints.keySet())
			getPoints (f);
	}

	@Override
	protected void paintComponent (Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		
		int nbDivs = (int) ((xRange[1] - xRange[0]));
		double scaleX = (getWidth() - 20) / nbDivs;
		double scaleY = (getHeight() - 20) / nbDivs;
		Point O = new Point (new FloatValue (getWidth() / 2.0), new FloatValue (getHeight() / 2.0));

		FontMetrics metrics = g.getFontMetrics();
		
		// draw axes
		g2d.setStroke (new BasicStroke (1.5f));
		g2d.setPaint (Color.BLACK);
		// x axis
		g2d.drawLine (0, (int) O.getY().getDoubleValue(), getWidth(), (int) O.getY().getDoubleValue());
		g2d.drawString (axesNames[0], getWidth() - metrics.stringWidth (axesNames[0]) - 10, (int) O.getY().getDoubleValue() + metrics.getHeight());
		// y axis
		g2d.drawLine ((int) O.getX().getDoubleValue(), 0, (int) O.getX().getDoubleValue(), getHeight());
		g2d.drawString (axesNames[1], (int) O.getX().getDoubleValue() + 10, 10);

		// put label for origin
		g2d.drawString ("O", (int) O.getX().getDoubleValue() + 5, (int) O.getY().getDoubleValue() + 15);

		// draw divisions
		int count = 0;
		for (double i = xRange[0]; i <= xRange[1]; i += 10 * xStep)
		{
			g2d.setPaint (Color.BLACK);
			g2d.setFont (getFont().deriveFont (10f));
			
			if (i != 0)
			{
				// draw x axis scale
				g2d.drawLine ((int) (O.getX().getDoubleValue() + i * scaleX), (int) O.getY().getDoubleValue() - 5, (int) (O.getX().getDoubleValue() + i * scaleX), (int) O.getY().getDoubleValue() + 5);
				if (count % 5 == 0 && Math.abs (i) >= 0.001)
					g2d.drawString (String.valueOf ((float) i), (int) (O.getX().getDoubleValue() + i * scaleX), (int) O.getY().getDoubleValue() + 15);

				// draw y axis scale
				g2d.drawLine ((int) O.getX().getDoubleValue() - 5, (int) (O.getY().getDoubleValue() - i * scaleY), (int) O.getX().getDoubleValue() + 5, (int) (O.getY().getDoubleValue() - i * scaleY));
				if (count % 5 == 0 && Math.abs (i) >= 0.001)
					g2d.drawString (String.valueOf ((float) i), (int) O.getX().getDoubleValue() + 15, (int) (O.getY().getDoubleValue() - i * scaleY));
			}

			count++;
		}

		double last = xRange[0];
		
		// draw curves
		for (Function f : fnPoints.keySet())
			for (double i = xRange[0] + xStep; i <= xRange[1]; i += xStep)
			{
				if (fnPoints.get(f).get (last) != null && fnPoints.get(f).get (i) != null)
				{
					g2d.setPaint (fnColors.get (f));
					g2d.drawLine ((int) (O.getX().getDoubleValue() + last * scaleX), (int) (O.getY().getDoubleValue() - fnPoints.get(f).get (last) * scaleY), (int) (O.getX().getDoubleValue() + i * scaleX), (int) (O.getY().getDoubleValue() - fnPoints.get(f).get (i) * scaleY));
					last = i;
				}
				
				if (fnPoints.get(f).get (last) == null)
					last = i;
			}
	}

	/**
	 * Displays the {@code Graph} in a new {@code JFrame}
	 */
	public void display ()
	{
		JFrame f = new JFrame (title);
		f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		f.setSize (700, 700);
		f.add (this);
		f.setVisible (true);
	}
}
