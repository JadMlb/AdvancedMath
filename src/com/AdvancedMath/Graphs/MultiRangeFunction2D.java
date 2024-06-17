package com.AdvancedMath.Graphs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import com.AdvancedMath.Numbers.FloatValue;
import com.AdvancedMath.Numbers.FractionValue;
import com.AdvancedMath.Numbers.Number;
import com.AdvancedMath.Numbers.Value;
import com.AdvancedMath.EqTree.Node;
import com.AdvancedMath.EqTree.NumberNode;
import com.AdvancedMath.EqTree.OperatorNode;
import com.AdvancedMath.EqTree.VariableNode;

/**
 * Class that represents a function defined in multiple ranges
 */
public class MultiRangeFunction2D extends Function
{
	private HashMap<Range, Node> def;
	private HashMap<Node, Range> defRange;

	/**
	 * Creates a {@code Function} name(variables) = expression, defined on given range. New Function definitions on new ranges can be added later
	 * 
	 * @param name The name of the function (f(x), g(x, y), ...)
	 * @param variables An array of {@code String} containing the names of the variables that this function depends on
	 * @param range The range of definition of this function
	 * @param expression The {@code Node} that represents this function
	 * @throws IllegalArgumentException if the definition range is R = (-infinity, infinity). For the latter case, use {@link Function2D}
	 * 
	 * @see MultiRangeFunction2D#addFunctionDefiniton(Range, Node)
	 * @see Function2D
	 */
	public MultiRangeFunction2D (String name, String variable, Range range, String expression)
	{
		super (name, new HashSet<> (Arrays.asList (variable)));
		
		if (range == Range.R)
			throw new IllegalArgumentException ("A 'MultiRangeFunction2D' cannot be difined on (-infinity, infinity). For that case use 'Function2D'");

		this.def = new HashMap<>();
		this.defRange = new HashMap<>();
		Node parsed = Node.parse (expression + ")");
		this.def.put (range, parsed);
		this.defRange.put (parsed, range);
	}

	/**
	 * Creates a {@code Function} name(variables) = expression, defined on given range. New Function definitions on new ranges can be added later
	 * 
	 * @param name The name of the function (f(x), g(x, y), ...)
	 * @param variables An array of {@code String} containing the names of the variables that this function depends on
	 * @param range The range of definition of this function
	 * @param expression The {@code Node} that represents this function
	 * @throws IllegalArgumentException if the definition range is R = (-infinity, infinity). For the latter case, use {@link Function2D}
	 * 
	 * @see MultiRangeFunction2D#addFunctionDefiniton(Range, Node)
	 * @see Function2D
	 */
	public MultiRangeFunction2D (String name, String variable, Range range, Node expression)
	{
		super (name, new HashSet<> (Arrays.asList (variable)));
		
		if (range == Range.R)
			throw new IllegalArgumentException ("A 'MultiRangeFunction2D' cannot be difined on (-infinity, infinity). For that case use 'Function2D'");

		this.def = new HashMap<>();
		this.defRange = new HashMap<>();
		Node parsed = Node.parse (expression + ")");
		this.def.put (range, parsed);
		this.defRange.put (parsed, range);
	}

	/**
	 * Adds a new definition for a range. 
	 * <p>If the range already has a mapping, the new definition replaces the old one.
	 * <p>If the definition exists
	 * <ul>
	 * 	<li>and the ranges are adjacent, the range is extended so that the new range's
	 * 		lower bound is equal to the smallest of the lower bounds and the upper bound is the biggest of the upper bounds
	 * 	</li>
	 * 	<li>and the ranges are separated by some value, 2 entries of the same value are in the range map</li>
	 * 
	 * @param range The range of the new definition
	 * @param expression The definition of the function on the specified range
	 */
	public void addFunctionDefiniton (Range range, Node expression)
	{
		if (defRange.get (expression) == null)
		{
			this.def.put (range, expression);
			this.defRange.put (expression , range);
			return;
		}
		if (defRange.get(expression).equals (range))
			return;
		if (defRange.get(expression).areAdjacentAndContinous (range) || range.areAdjacentAndContinous (defRange.get (expression)))
			defRange.get(expression).appendInPlace (range);
		else
			this.def.put (range, expression);
	}
	
	/**
	 * Adds a new definition for a range. 
	 * <p>If the range already has a mapping, the new definition replaces the old one.
	 * <p>If the definition exists
	 * <ul>
	 * 	<li>and the ranges are adjacent, the range is extended so that the new range's
	 * 		lower bound is equal to the smallest of the lower bounds and the upper bound is the biggest of the upper bounds
	 * 	</li>
	 * 	<li>and the ranges are separated by some value, 2 entries of the same value are in the range map</li>
	 * 
	 * @param range The range of the new definition
	 * @param expression The definition of the function on the specified range
	 */
	public void addFunctionDefiniton (Range range, String expression)
	{
		addFunctionDefiniton (range, Node.parse (expression + ")"));
	}

	/**
	 * Gets the {@code Node} tree representing this function. Equivalent to {@code toNode (Range.R)}
	 * 
	 * @return The binary tree of this function on the range R (-infinty, +infinity)
	 * @throws 
	 */
	@Override
	public Node toNode ()
	{
		if (this.def.keySet().size() != 1 || !this.def.containsKey (Range.R))
			throw new IllegalArgumentException ("Cannot invoke this method on a function that is defined on multiple ranges, or that is not defined on (-infinty, +infinity)");
		return def.get (Range.R);
	}
	
	/**
	 * Gets the {@code Node} tree representing this function on the given rangeof if the range is included in the defined range of the function
	 * 
	 * @return The binary tree of this function on the given range
	 */
	public Node toNode (Range r)
	{
		Node func = def.get (r);
		if (func == null)
			for (Range range : def.keySet())
				if (r.in (range))
					return def.get (range);

		return func;
	}

	/**
	 * Get the value of f(a, b, ...)
	 * 
	 * @param x The values mapped to the name of the variables at which we want to evaluate the function. 
	 * @return The value of the function for the specified variables
	 * 
	 * @throws IllegalArgumentException if the map does not contain an entry for the variable or if the mapped number is imaginary
	 */
	public Node of (HashMap<String, Number> x)
	{
		Number val = x.get (getVariables().iterator().next());
		if (val == null)
			throw new IllegalArgumentException ("Variable mapping does not contain an entry for the variable in the function");
			
		if (!val.isPureReal())
			throw new IllegalArgumentException ("The value of the variable must be a real value");
		
		Node tree = null;	
		for (Range r : this.def.keySet())
			if (r.contains (val.getX()))
			{
				tree = def.get (r);
				break;
			}

		if (tree == null)
			throw new NullPointerException ("This value falls outside of the defined range of the function");

		if (tree instanceof OperatorNode o)
			// return OperatorNode.simplify (o, x);
			throw new UnsupportedOperationException ("This mode is currently not supported");
		if (tree instanceof VariableNode v && x != null && x.get (v.getName()) != null)
			return new NumberNode (x.get (v.getName()));
		return new NumberNode (Number.valueOf (tree, x));
	}

	@Override
	protected void calcDomain ()
	{}

	/**
	 * Returns the first order derivative of this {@code Function}
	 * 
	 * @param var The variable we are differentiating in respect of
	 * @return The derivative of this function in respect of the variable provided
	 */
	public Function differentiate (String var)
	{
		MultiRangeFunction2D derivative = new MultiRangeFunction2D (getName() + "'", getVariables().iterator().next(), null, (Node) null);
		for (Range r : def.keySet())
			derivative.addFunctionDefiniton (r, def.get(r).differentiate(var));

		return derivative;
	}

	/**
	 * Gets the {@code String} representation of this function
	 * 
	 * @return The {@code String} form of the function
	 */
	@Override
	public String toString ()
	{
		String fn = "";

		for (Range r : def.keySet())
			fn += "\t" + def.get (r) + "\t" + super.getVariables().iterator().next() + " " + r.toString() + '\n';
		
		return super.toString() + "{\n" + fn + "}";
	}

	public static MultiRangeFunction2D unitStep ()
	{
		return unitStep (Number.ONE, FractionValue.ZERO);
	}
	
	public static MultiRangeFunction2D unitStep (Value x0)
	{
		return unitStep (Number.ONE, x0);
	}
	
	public static MultiRangeFunction2D unitStep (Number amplitude)
	{
		return unitStep (amplitude, FractionValue.ZERO);
	}
	
	public static MultiRangeFunction2D unitStep (Number amplitude, Value x0)
	{
		MultiRangeFunction2D unit = new MultiRangeFunction2D ("u", "x", Range.gte (x0), new NumberNode (amplitude));
		unit.addFunctionDefiniton (Range.lt (x0), new NumberNode (Number.ZERO));
		return unit;
	}

	public static MultiRangeFunction2D rectangular ()
	{
		return rectangular (Number.ONE, FractionValue.ZERO, FractionValue.ONE);
	}
	
	public static MultiRangeFunction2D rectangular (Value width)
	{
		return rectangular (Number.ONE, FractionValue.ZERO, width);
	}

	public static MultiRangeFunction2D rectangular (Number amplitude, Value x0, Value width)
	{
		Value tOver2 = width.divide (new FractionValue (2, 1)),
				lower = x0.subtract (tOver2),
				upper = x0.add (tOver2);
		MultiRangeFunction2D rect = new MultiRangeFunction2D ("rect", "x", new Range (lower, true, true, upper), new NumberNode (amplitude));
		rect.addFunctionDefiniton (Range.lt (lower), new NumberNode (Number.ZERO));
		rect.addFunctionDefiniton (Range.gt (upper), new NumberNode (Number.ZERO));
		return rect;
	}

	public static MultiRangeFunction2D dirac ()
	{
		return dirac (FractionValue.ZERO);
	}

	public static MultiRangeFunction2D dirac (Value x0)
	{
		Value lower = x0.subtract (new FloatValue (1e-7)), upper = x0.add (new FloatValue (1e-7));
		MultiRangeFunction2D d = new MultiRangeFunction2D ("𝞭", "x", Range.lt (lower), new NumberNode (Number.ZERO));
		d.addFunctionDefiniton (new Range (lower, true, true, upper), new NumberNode (Number.ONE));
		d.addFunctionDefiniton (Range.gt (upper), new NumberNode (Number.ZERO));
		return d;
	}

	public static MultiRangeFunction2D sgn ()
	{
		MultiRangeFunction2D sgn = new MultiRangeFunction2D ("sgn", "x", Range.lt (new FloatValue (1e-7)), new NumberNode (Number.real (-1.)));
		sgn.addFunctionDefiniton (new Range (-1e-7, true, true, 1e-7), new NumberNode (Number.ZERO));
		sgn.addFunctionDefiniton (Range.gt (new FloatValue (1e-7)), new NumberNode (Number.ONE));
		return sgn;
	}
}
