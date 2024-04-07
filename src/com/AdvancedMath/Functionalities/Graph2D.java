package com.AdvancedMath.Functionalities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.AdvancedMath.EqTree.NumberNode;
import com.AdvancedMath.Graphs.Function;
import com.AdvancedMath.Graphs.Point;
import com.AdvancedMath.Graphs.Range;
import com.AdvancedMath.Numbers.FloatValue;
import com.AdvancedMath.Numbers.FractionValue;
import com.AdvancedMath.Numbers.Number;
import com.AdvancedMath.Numbers.Value;
import com.AdvancedMath.Numbers.ValueComparator;

public class Graph2D
{
	private double step = .01;
	private String title = "Graph";
	private Range windowHoriz = new Range (0, 2), windowVert = new Range (0, 2);
	private String[] axesNames = new String[] {"x", "y"};
	private HashMap<Function, Color> fnColors = new HashMap<>();
	private HashMap<Function, TreeMap<Value, Value>> fnPoints = new HashMap<>();

	/**
	 * Constructs a {@code Graph2D} instance with the restrictions provided
	 * 
	 * @param title The title to give this graph. Defaults to "Graph".
	 * @param windowHoriz The horizontal window that the graph covers (on the x axis). Defaluts to [-1, 1] inclusive.
	 * @param windowVert The vertical window that the graph covers (on the y axis). Defaluts to [-1, 1] inclusive.
	 * @param axesNames The labels to be given to the axes. If empty array, or array of length 1 is given, the result is the default value. If an array of length 2 and more is provided, only the first 2 elements are considered. Defaults to ["x", "y"].
	 * @param step The sampling step on the x axis to calculate the values of the graph's functions and draw them. If this value is too small, the programme will take long to generate the graph. If it is too big, the quality of the graph is compromised, even sometimes no graph can be generated. Defaults to 0.01.
	 */
	public Graph2D (String title, Range windowHoriz, String[] axesNames, double step)
	{
		if (title != null)
			this.title = title;

		if (windowHoriz != null && windowHoriz.length() <= 100)
		{
			this.windowHoriz = windowHoriz;
			this.windowVert = windowHoriz.clone();
		}

		if (axesNames != null && axesNames.length >= 2)
		{
			this.axesNames[0] = axesNames[0];
			this.axesNames[1] = axesNames[1];
		}

		if (step > 0)
			this.step = step;
	}

	/**
	 * Constructs a graph with default values.
	 * 
	 * @see Graph2D#Graph2D2(String, Range, String[], double)
	 */
	public Graph2D ()
	{
		this (null, null, null, 0);
	}

	/**
	 * Constucts a graph with specified title, and default values for the other properties.
	 * 
	 * @param title The title of the graph
	 * 
	 * @see Graph2D#Graph2D2(String, Range, String[], double)
	 */
	public Graph2D (String title)
	{
		this (title, null, null, 0);
	}
	
	/**
	 * Constucts a graph with specified title & axes labels, and default values for the other properties.
	 * 
	 * @param title The title of the graph
	 * @param axesNames The labels of the axes
	 * 
	 * @see Graph2D#Graph2D2(String, Range, String[], double)
	 */
	public Graph2D (String title, String[] axesNames)
	{
		this (title, null, null, 0);
	}

	public double getStep() 
	{
		return this.step;
	}

	public void setStep (double step) throws IllegalArgumentException
	{
		if (step <= 0)
			throw new IllegalArgumentException ("The step size cannot be 0 or negative.");
		this.step = step;
	}

	public String getTitle () 
	{
		return this.title;
	}

	public void setTitle (String title) 
	{
		this.title = title;
	}

	public Range getHorizontalWindow () 
	{
		return this.windowHoriz.clone();
	}

	public void setHorizontalWindow (Range windowHoriz) throws NullPointerException, IllegalArgumentException
	{
		if (windowHoriz == null)
			throw new NullPointerException ("The new horizontal window size must not be null.");
		
		if (windowHoriz.length() <= 0 || windowHoriz.length() > 100)
			throw new IllegalArgumentException ("The new horizontal window size has unsupported size.");

		this.windowHoriz = windowHoriz;
		this.windowVert = windowHoriz.clone();
	}

	public String[] getAxesNames() 
	{
		return this.axesNames;
	}

	public void setAxesNames (String[] axesNames) throws NullPointerException, IllegalArgumentException
	{
		if (axesNames == null)
			throw new NullPointerException ("The new labels for the axes must not be null");
		
		if (axesNames.length < 2)
			throw new IllegalArgumentException ("The array of new axes labels must contain at least 2 elements");
		
		this.axesNames[0] = axesNames[0];
		this.axesNames[1] = axesNames[1];
	}

	private void getPoints (Function f, Range hr, Range vr)
	{
		double start = hr.getLowerBound().getDoubleValue(), end = hr.getUpperBound().getDoubleValue();
		if (!hr.isLowerIncluded())
			start += step;
		if (!hr.isUpperIncluded())
			end -= step;
		if (fnPoints.get (f) == null)
			fnPoints.put (f, new TreeMap<> (new ValueComparator()));
		
		for (double i = start; i <= end; i += step)
		{
			try
			{
				HashMap<String, Number> xMapping = new HashMap<>();
				xMapping.put (f.getVariables().iterator().next(), Number.real (i));

				NumberNode nb = (NumberNode) f.of (xMapping);
				// if (vr.contains (nb.getValue().getX()))
					fnPoints.get(f).put (new FloatValue (i), nb.getValue().getX());
			}
			catch (Exception e) {}
		}
	}

	/**
	 * The function that is to be added to the set of plotted functions on the graph
	 * 
	 * @param f The new function to be plotted
	 * @param c The color of the curve representing this function on the graph
	 */
	public void plotFunction (Function f, Color c)
	{
		fnColors.put (f, c);
		getPoints (f, windowHoriz, windowVert);
	}
	
	/**
	 * Removes a given function from the graph
	 * 
	 * @param f The function to remove
	 */
	public void removeFunction (Function f)
	{
		fnColors.remove (f);
		fnPoints.remove (f);
	}

	/**
	 * Displays the graph in a new {@code JFrame}
	 */
	public void display ()
	{
		JFrame frame = new JFrame ("Graph: " + title);
		frame.setSize (1000, 700);
		
		GraphArea graphArea = new GraphArea (fnPoints, fnColors, windowHoriz, windowVert, axesNames, step);
		frame.add (graphArea);

		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		frame.setVisible (true);
	}
}

class GraphArea extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener
{
	private HashMap<Function, Color> fnColors;
	private HashMap<Function, TreeMap<Value, Value>> fnPoints;
	private Range windowHoriz, windowVert;
	private int size;
	private String[] axesNames;
	private double step;
	private Point start, O, oldO;

	public GraphArea (HashMap<Function, TreeMap<Value, Value>> fnPoints, HashMap<Function, Color> fnColors, Range windowHoriz, Range windowVert, String[] axesNames, double step)
	{
		this.fnPoints = fnPoints;
		this.fnColors = fnColors;
		this.size = fnPoints.get(fnPoints.keySet().iterator().next()).size();
		this.windowHoriz = windowHoriz;
		this.windowVert = windowVert;
		this.axesNames = axesNames;
		this.step = step;
		addMouseListener (this);
		addMouseMotionListener (this);
		addMouseWheelListener (this);
	}

	private void removePoints (Function f, Range r)
	{
		if (fnPoints.get (f) != null)
			for (Iterator<Value> it = fnPoints.get(f).keySet().iterator(); it.hasNext();)
			{
				Value x = it.next();
				
				if (r.contains (x))
				{
					it.remove();

					if (f == fnPoints.keySet().iterator().next()) // only first function updates size
						size--;
				}
			}
	}

	private void getPoints (Function f, Range hr)
	{
		double start = hr.getLowerBound().getDoubleValue(), end = hr.getUpperBound().getDoubleValue();
		if (fnPoints.get (f) == null)
			fnPoints.put (f, new TreeMap<> (new ValueComparator()));
		
		for (double i = start; i <= end; i += step)
		{
			try
			{
				HashMap<String, Number> xMapping = new HashMap<>();
				xMapping.put (f.getVariables().iterator().next(), Number.real (i));

				NumberNode nb = (NumberNode) f.of (xMapping);
				fnPoints.get(f).put (new FloatValue (i), nb.getValue().getX());
				if (f == fnPoints.keySet().iterator().next()) // only first function updates size
					size++;
			}
			catch (Exception e) {}
		}
	}

	private void refreshGraph (Range oldX, Range oldY, Range newX, Range newY)
	{
		if (newX == null && !newY.equals (oldY))
		{
			Range yOverlap = oldY.getIntersectingRange (newY);
			// 3 cases
			// null: no overlap => new set of points (shift up or down)
			// not null:
			// 		newY is in oldY => remove extra points
			// 		oldY is in newY => add extra points
			if (yOverlap == null)
			{
				for (Function f: fnPoints.keySet())
				{
					fnPoints.get(f).clear();
					getPoints (f, newX);
				}
			}
			else
			{
				Range[] newPointsRanges = oldY.subtract (newY);

				for (Range r : newPointsRanges)
				{
					// #################					oldY
					// 				-----------------		newY
					//				****					inter
					// #############	-------------		split
					// 				-----------------		oldY
					// #################					newY
					//				****					inter
					// #############	-------------		split
					for (Function f : fnPoints.keySet())
						if (r.getUpperBound().equals (newY.getLowerBound()) || r.getLowerBound().equals (newY.getUpperBound())) // to remove
							removePoints (f, r);
						else
							getPoints (f, oldX);
				}
			}
			
			windowVert.setBounds (newY.getLowerBound(), newY.getUpperBound());
		}
		else if (!newX.equals (oldX) && newY == null)
		{
			Range xOverlap = oldX.getIntersectingRange (newX);
			if (xOverlap == null)
			{
				for (Function f: fnPoints.keySet())
				{
					fnPoints.get(f).clear();
					getPoints (f, newX);
				}
			}
			else
			{
				Range[] newPointsRanges = oldX.subtract (newX);

				for (Range r : newPointsRanges)
				{
					for (Function f : fnPoints.keySet())
						if (r.getUpperBound().equals (newX.getLowerBound()) || r.getLowerBound().equals (newX.getUpperBound())) // to remove
							removePoints (f, r);
						else
							getPoints (f, r);
				}
			}
			windowHoriz.setBounds (newX.getLowerBound(), newX.getUpperBound());
		}
	}
				
	@Override
	public void paintComponent (Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;

		if (this.O == null)
			this.O = new Point (new FloatValue (getWidth() / 2.0), new FloatValue (getHeight() / 2.0));

		int nbDivs = (int) (windowHoriz.getUpperBound().subtract(windowHoriz.getLowerBound()).getDoubleValue());
		FloatValue scaleX = new FloatValue ((double) (getWidth() - 20) / nbDivs);
		FloatValue scaleY = new FloatValue ((double) (getHeight() - 20) / nbDivs);

		FontMetrics metrics = g.getFontMetrics();

		// draw axes
		g2d.setStroke (new BasicStroke (.75f));
		g2d.setPaint (Color.BLACK);
		// x axis
		g2d.drawLine (0, (int) O.getY().getDoubleValue(), getWidth(), (int) O.getY().getDoubleValue());
		g2d.drawString (axesNames[0], getWidth() - metrics.stringWidth (axesNames[0]) - 10, (int) O.getY().getDoubleValue() + metrics.getHeight());
		// y axis
		g2d.drawLine ((int) O.getX().getDoubleValue(), 0, (int) O.getX().getDoubleValue(), getHeight());
		g2d.drawString (axesNames[1], (int) O.getX().getDoubleValue() + 10, 10);

		// put label for origin
		g2d.drawString ("O", (int) O.getX().getDoubleValue() + 5, (int) O.getY().getDoubleValue() + 15);

		// draw grid
		int count = 0;
		for (double i = windowHoriz.getLowerBound().getDoubleValue(); i <= windowHoriz.getUpperBound().getDoubleValue(); i += 10 * step)
		{
			g2d.setFont (getFont().deriveFont (10f));
			
			if (i != 0)
			{
				g2d.setPaint (Color.BLACK);
				g2d.setStroke (new BasicStroke (.75f));

				// draw x axis scale
				g2d.drawLine ((int) (O.getX().getDoubleValue() + i * scaleX.getDoubleValue()), (int) O.getY().getDoubleValue() - 5, (int) (O.getX().getDoubleValue() + i * scaleX.getDoubleValue()), (int) O.getY().getDoubleValue() + 5);
				if (count % 5 == 0 && Math.abs (i) >= 0.001)
					g2d.drawString (String.valueOf ((float) i), (int) (O.getX().getDoubleValue() + i * scaleX.getDoubleValue()), (int) O.getY().getDoubleValue() + 15);

				g2d.setPaint (Color.LIGHT_GRAY);
				g2d.setStroke (new BasicStroke (.5f));
				
				// draw x grid line
				g2d.drawLine ((int) (O.getX().getDoubleValue() + i * scaleX.getDoubleValue()), 0, (int) (O.getX().getDoubleValue() + i * scaleX.getDoubleValue()), getHeight());
			}

			count++;
		}

		count = 0;
		for (double i = windowVert.getLowerBound().getDoubleValue(); i <= windowVert.getUpperBound().getDoubleValue(); i += 10 * step)
		{
			g2d.setFont (getFont().deriveFont (10f));
			
			if (i != 0)
			{
				g2d.setPaint (Color.BLACK);
				g2d.setStroke (new BasicStroke (.75f));

				// draw y axis scale
				g2d.drawLine ((int) O.getX().getDoubleValue() - 5, (int) (O.getY().getDoubleValue() - i * scaleY.getDoubleValue()), (int) O.getX().getDoubleValue() + 5, (int) (O.getY().getDoubleValue() - i * scaleY.getDoubleValue()));
				if (count % 5 == 0 && Math.abs (i) >= 0.001)
					g2d.drawString (String.valueOf ((float) i), (int) O.getX().getDoubleValue() + 15, (int) (O.getY().getDoubleValue() - i * scaleY.getDoubleValue()));
					
				g2d.setPaint (Color.LIGHT_GRAY);
				g2d.setStroke (new BasicStroke (.5f));
				
				// draw y grid line
				g2d.drawLine (0, (int) (O.getY().getDoubleValue() - i * scaleY.getDoubleValue()), getWidth(), (int) (O.getY().getDoubleValue() - i * scaleY.getDoubleValue()));
			}

			count++;
		}

		g2d.setStroke (new BasicStroke (1.5f));
		for (int i = 1; i < size; i++)
		{
			for (Function f : fnPoints.keySet())
			{
				try
				{
					Value v = (Value) fnPoints.get(f).keySet().toArray()[i], prev = (Value) fnPoints.get(f).keySet().toArray()[i - 1];
	
					if (prev != null)
					{
						g2d.setColor (fnColors.get (f));
						g2d.drawLine (
							(int) Math.round (O.getX().add(prev.multiply (scaleX)).getDoubleValue()),
							(int) Math.round (O.getY().subtract(fnPoints.get(f).get(prev).multiply (scaleY)).getDoubleValue()),
							(int) Math.round (O.getX().add(v.multiply (scaleX)).getDoubleValue()),
							(int) Math.round (O.getY().subtract(fnPoints.get(f).get(v).multiply (scaleY)).getDoubleValue())
						);
					}
				}
				catch (IndexOutOfBoundsException iobe) {}
			}
		}
	}

	@Override
	public void mouseDragged (MouseEvent e)
	{
		double distanceXScaled = e.getX() - start.getX().getDoubleValue(), distanceYScaled = e.getY() - start.getY().getDoubleValue();
		O.setX (O.getX().add (new FloatValue (distanceXScaled)));
		O.setY (O.getY().add (new FloatValue (distanceYScaled)));
		repaint();
		start = new Point (new FloatValue ((double) e.getX()), new FloatValue ((double) e.getY()));
	}

	@Override
	public void mouseMoved (MouseEvent e) {}

	@Override
	public void mouseClicked (MouseEvent e) {}

	@Override
	public void mousePressed (MouseEvent e)
	{
		start = new Point (new FloatValue ((double) e.getX()), new FloatValue ((double) e.getY()));
		oldO = new Point (O.getX(), O.getY());
	}

	@Override
	public void mouseReleased (MouseEvent e)
	{
		double scaleX = (getWidth() - 20) / windowHoriz.length();
		double scaleY = (getHeight() - 20) / windowVert.length();
		double distanceXScaled = O.getX().subtract(oldO.getX()).getDoubleValue(), distanceYScaled = oldO.getY().subtract(O.getY()).getDoubleValue();
		FloatValue distanceX = new FloatValue (distanceXScaled / scaleX), distanceY = new FloatValue (distanceYScaled / scaleY);
		Range newX = windowHoriz.clone(), newY = windowVert.clone();
		if (distanceX.compareTo (FractionValue.ZERO) != 0)
		{
			newX.setBounds (windowHoriz.getLowerBound().subtract (distanceX), windowHoriz.getUpperBound().subtract (distanceX));
		}
		
		if (distanceY.compareTo (FractionValue.ZERO) != 0)
		{
			newY.setBounds (windowVert.getLowerBound().subtract (distanceY), windowVert.getUpperBound().subtract (distanceY));
			windowVert.setBounds (newY.getLowerBound(), newY.getUpperBound());
		}
		refreshGraph (windowHoriz, windowVert, newX, null);
		repaint();
	}

	@Override
	public void mouseEntered (MouseEvent e) {}

	@Override
	public void mouseExited (MouseEvent e) {}

	@Override
	public void mouseWheelMoved (MouseWheelEvent e)
	{
		double rotations = e.getPreciseWheelRotation();
		if (step >= 1e-5 || rotations >= 0)
		{
			FloatValue expandValue = new FloatValue ((double) e.getScrollAmount() * rotations);
			FloatValue scale = new FloatValue ((getWidth() - 20) / windowHoriz.length());
			FloatValue expandValueX = (FloatValue) expandValue.divide (scale);
			FloatValue expandValueY = (FloatValue) expandValue.divide (scale);
			Range newX = new Range (windowHoriz.getLowerBound().add (expandValueX), windowHoriz.isLowerIncluded(), windowHoriz.isUpperIncluded(), windowHoriz.getUpperBound().subtract (expandValueX));
			refreshGraph (windowHoriz, windowVert, newX, null);
			windowVert.setBounds (windowVert.getLowerBound().add (expandValueY), windowVert.getUpperBound().subtract (expandValueY));
			repaint();
		}
	}
}