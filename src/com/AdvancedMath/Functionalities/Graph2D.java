package com.AdvancedMath.Functionalities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import com.AdvancedMath.EqTree.Node;
import com.AdvancedMath.EqTree.NumberNode;
import com.AdvancedMath.Graphs.Function;
import com.AdvancedMath.Graphs.Function2D;
import com.AdvancedMath.Graphs.MultiRangeFunction2D;
import com.AdvancedMath.Graphs.Range;
import com.AdvancedMath.Numbers.FloatValue;
import com.AdvancedMath.Numbers.FractionValue;
import com.AdvancedMath.Numbers.Number;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * A class that plots multiple functions on a {@code javafx.scene.Scene}.
 * Can be displayed in its own frame or it can be added to one
 */
public class Graph2D
{
	public static final int RANGE_LENGTH = 20;
	private static final int DEFAULT_RANGE_START = -10;
	public static final Range DEFAULT_RANGE = new Range (DEFAULT_RANGE_START, true, true, DEFAULT_RANGE_START + RANGE_LENGTH);
	
	private Range graphRange;
	private String[] axesNames;
	private String title;
	private double xStep;
	private HashMap<Function, Color> fnColors = new HashMap<>();
	private HashMap<Function, ArrayList<Number>> fnPoints = new HashMap<>();
	private int xStart = 0, yStart = 0; // used for dragging on the graph to move it

	/**
	 * Creates a new Graph with the ability to customise any of its characteristics.
	 * 
	 * @param title
	 * @param graphRange
	 * @param axesNames
	 * @param xStep
	 * 
	 * @see Graph2D#Graph2D(String)
	 * @see Graph2D#Graph2D(String, Range)
	 * @see Graph2D#Graph2D(String, String[], Range)
	 */
	public Graph2D (String title, Range graphRange, String[] axesNames, double xStep) 
	{
		if (graphRange == null)
			this.graphRange = DEFAULT_RANGE;
		else
			this.graphRange = graphRange;
		if (this.graphRange.getLowerBound().getDoubleValue() == Double.NEGATIVE_INFINITY)
			if (this.graphRange.getUpperBound().getDoubleValue() == Double.POSITIVE_INFINITY)
				this.graphRange = DEFAULT_RANGE;
			else
				this.graphRange.setLowerBound (this.graphRange.getUpperBound().subtract (new FloatValue (Double.valueOf (RANGE_LENGTH))));
		else if (this.graphRange.getUpperBound().getDoubleValue() == Double.POSITIVE_INFINITY)
			this.graphRange.setUpperBound (this.graphRange.getLowerBound().add (new FloatValue (Double.valueOf (RANGE_LENGTH))));

		if (axesNames != null && axesNames.length >= 2)
			this.axesNames = axesNames;
		else
			this.axesNames = new String [] {"x", "y"};
		if (title != null && !title.trim().isEmpty())
			this.title = title.trim();
		else
			this.title = "Graph";
		if (xStep > 0.001 && xStep <= 1)
			this.xStep = xStep;
		else
			this.xStep = 0.01;
	}

	public Graph2D (String title)
	{
		this (title, null, null, 0);
	}

	public Graph2D (String title, Range graphRange)
	{
		this (title, graphRange, null, 0);
	}

	public Graph2D (String title, String[] axesNames, Range graphRange)
	{
		this (title, graphRange, axesNames, 0);
	}

	public String getTitle ()
	{
		return title;
	}
	
	public void setTitle (String title)
	{
		this.title = title;
	}

	private void getPoints (Function f)
	{
		HashMap<String, Number> values = new HashMap<>();
		String varName = f.getVariables().iterator().next();
		
		for (double i = graphRange.getLowerBound().getDoubleValue(); i <= graphRange.getUpperBound().getDoubleValue() && i <= 1000; i += xStep)
		{
			Number x = Number.real (i);
			values.put (varName, x);
			
			if (fnPoints.get (f) == null)
				fnPoints.put (f, new ArrayList<>());

			try
			{
				Node res = f.of (values);
				if (res instanceof NumberNode n)
					fnPoints.get(f).add (new Number (x.getX(), n.getValue().getX()));
				else
					// TODO: Add support for sliders in this case
					throw new NumberFormatException ("This function is not a 2D function and thus returned a variable result");
			}
			catch (Exception e) {}
		}
	}

	/**
	 * Adds a function to the set to be plotted. Will not display the plotted function, nor the graph
	 * 
	 * @param function {@code Function} to be plotted
	 * @param color	{@code Color} that this function will be plotted with
	 * @see Graph2D#display()
	 * @throws NullPointerException if the given function is null
	 * @throws IllegalArgumentException if the function is not a {@link Function2D}
	 */
	public void plot (Function function, Color color)
	{
		if (function == null)
			throw new NullPointerException ("The function to be plotted cannot be null");

		if (function instanceof Function2D || function instanceof MultiRangeFunction2D)
		{
			fnColors.put (function, color);
			getPoints (function);
		}
		else
			throw new IllegalArgumentException ("The function plotted on a 2D graph must be 2D by itself");
	}

	/**
	 * Returns this graph in its own {@code javafx.scene.Scene} to be used in a JavaFX stage
	 * 
	 * <p>Use this function to refresh and rebuild the graph
	 * 
	 * @return a {@code javafx.scene.Scene} with this graph in it
	 */
	public Scene display ()
	{	
		NumberAxis xAxis = new NumberAxis(), yAxis = new NumberAxis();
		xAxis.setLabel (axesNames[0]);
		yAxis.setLabel (axesNames[1]);

		BorderPane bp = new BorderPane();
		
		LineChart<java.lang.Number, java.lang.Number> graph = new LineChart<java.lang.Number, java.lang.Number> (xAxis, yAxis);
		
		constructGraph (graph, xAxis, yAxis);

		graph.setOnMousePressed
		(
			new EventHandler<MouseEvent> ()
			{
				@Override
				public void handle (MouseEvent e)
				{
					xStart = (int) e.getSceneX();
					yStart = (int) e.getSceneY();
				}
			}
		);

		graph.setOnMouseDragged
		(
			new EventHandler<MouseEvent> ()
			{
				@Override
				public void handle (MouseEvent e)
				{	
					int moveAmount =  (int) (xStart - e.getSceneX());
					
					graphRange.setLowerBound (graphRange.getLowerBound().add (FractionValue.integer (moveAmount)));
					graphRange.setUpperBound (graphRange.getUpperBound().add (FractionValue.integer (moveAmount)));
					
					xStart = (int) e.getSceneX();
					yStart = (int) e.getSceneY();

					fnPoints.clear();
					for (Function f : fnColors.keySet())
					{
						getPoints (f);
						constructGraph (graph, xAxis, yAxis);
					}
				}
			}
		);
		
		graph.setCursor (Cursor.CROSSHAIR);
		graph.setCreateSymbols (false);
		graph.setLegendVisible (false);

		bp.setCenter (graph);

		HBox scope = new HBox();
		scope.getChildren().add (new Label ("Centred on: "));

		TextField centreOn = new TextField();
		centreOn.setOnKeyTyped (
			new EventHandler<KeyEvent> ()
			{
				@Override
				public void handle (KeyEvent e)
				{
					if (!Pattern.matches ("[0-9.,\\-]*", centreOn.getText()))
					// if (!Character.isDigit (e.getCharacter().charAt (0)) && e.getCharacter().charAt (0) != '-'  && e.getCharacter().charAt (0) != '.' && e.getCharacter().charAt (0) != ',' && e.getCharacter().charAt (0) != '\b')
						centreOn.setText (centreOn.getText (0, centreOn.getText().length() - 1));
				}
			}
		);
		scope.getChildren().add (centreOn);

		Button refresh = new Button ("Refresh");
		refresh.setOnAction (
			new EventHandler<ActionEvent> ()
			{
				@Override
				public void handle (ActionEvent e)
				{
					Range newRange = new Range (Double.parseDouble (centreOn.getText()), RANGE_LENGTH);
					if (!graphRange.equals (newRange))
					{
						graphRange = newRange;
						fnPoints.clear();
						for (Function f : fnColors.keySet())
						{
							getPoints (f);
							constructGraph (graph, xAxis, yAxis);
						}
					}
				}	
			}
		);
		scope.getChildren().add (refresh);

		bp.setBottom (scope);

		return new Scene (bp);
	}

	private void constructGraph (LineChart<java.lang.Number, java.lang.Number> graph, NumberAxis xAxis, NumberAxis yAxis)
	{
		for (int i = graph.getData().size() - 1; i >= 0; i--)
			graph.getData().remove (i);

		for (Function f : fnPoints.keySet())
		{
			XYChart.Series<java.lang.Number,java.lang.Number> series = new XYChart.Series<java.lang.Number,java.lang.Number>();
			for (Number pt : fnPoints.get (f))
				series.getData().add (new XYChart.Data<java.lang.Number, java.lang.Number> (pt.getX().getDoubleValue(), pt.getY().getDoubleValue()));
			
			graph.getData().add (series);
			
			series.setName (f.getName());
			String rgb = String.format (
				"%d, %d, %d",
				(int) (fnColors.get(f).getRed() * 255),
				(int) (fnColors.get(f).getGreen() * 255),
				(int) (fnColors.get(f).getBlue() * 255)
			);
			series.getNode().lookup(".chart-series-line").setStyle ("-fx-stroke: rgba(" + rgb + ", 1.0);");	
		}
	}
}
