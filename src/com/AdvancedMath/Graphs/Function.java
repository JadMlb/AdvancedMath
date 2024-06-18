package com.AdvancedMath.Graphs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

import com.AdvancedMath.EqTree.Node;
import com.AdvancedMath.EqTree.OperatorNode;
import com.AdvancedMath.Numbers.FractionValue;
import com.AdvancedMath.Numbers.Number;

public abstract class Function
{
	private HashSet<String> variables;
	private String name;
	protected ArrayList<Range> domain;

	/**
	 * Creates a {@code Function} name(variables) = expression
	 * 
	 * @param name The name of the function (f(x), g(x, y), ...)
	 * @param variables The set of variables that this function depends on
	 * @param expression The {@code Node} that represents this function
	 */
	public Function (String name, HashSet<String> variables)
	{
		this.name = name;
		this.variables = variables;
	}

	/**
	 * Gets the set of variables that the {@code Function} depends on
	 * 
	 * @return {@code HashSet} of variable names
	 */
	public HashSet<String> getVariables () 
	{
		return this.variables;
	}

	/**
	 * Gets the name of the {@code Function}
	 * 
	 * @return The name of the function
	 */
	public String getName ()
	{
		return this.name;
	}

	/**
	 * Sets the name of the function
	 * 
	 * @param name The new name of the function
	 */
	public void setName (String name)
	{
		this.name = name;
	}

	/**
	 * Gets the union of the ranges on which the function is defined
	 * 
	 * @return Set of {@code Range} that produce a valid output of the function
	 */
	public ArrayList<Range> getDomainOfDefinition ()
	{
		return domain;
	}

	// calculate domain of definition
	protected abstract void calcDomain ();
	
	protected ArrayList<Range> calcDomain (Node root)
	{
		if (root == null)
			return null;

		ArrayList<Range> ranges = new ArrayList<>();

		if (root instanceof OperatorNode o)
		{
			switch (o.getOperator())
			{
				case ADD: case SUB: case MUL:
				case EXP:
				case ABS:
				case SIN: case COS:
				case SINH: case COSH: case TANH:
				case ASH:
					ranges.add (Range.R);
					break;
				case DIV: 
					ranges.add (Range.R);
					break;
				case POW: // for even roots
					ranges.add (Range.R);
					break;
				case LN:
					ranges.add (Range.gt (FractionValue.ZERO));
					break;
				case TAN: // FIXME: repetive values, how to represent that: R - {(2k+1)pi/2}
					ranges.add (Range.R);
					break;
				case ASIN: 
					ranges.add (Range.R);
					break;
				case ACOS: 
					ranges.add (Range.R);
					break;
				case ATAN:
					ranges.add (Range.R);
					break;
				case ACH:
				ranges.add (Range.gt (FractionValue.ZERO));
					break;
				case ATH:
					ranges.add (Range.R);
					break;
				default:
					return null;
			}
		}
		else
			ranges.add (Range.R);

		return ranges;
	}

	/**
	 * Gets the {@code Node} tree representing this function. Equivalent to {@code toNode (Range.R)}
	 * 
	 * @return The binary tree of this function on the range R (-infinty, +infinity)
	 */
	public abstract Node toNode ();
	
	/**
	 * Gets the {@code Node} tree representing this function on the given rangeof if the range is included in the defined range of the function
	 * 
	 * @return The binary tree of this function on the given range
	 */
	public abstract Node toNode (Range r);

	/**
	 * Get the value of f(a, b, ...)
	 * 
	 * @param x The values mapped to the name of the variables at which we want to evaluate the function. 
	 * @return The value of the function for the specified variables
	 */
	public abstract Node of (HashMap<String, Number> x);

	/**
	 * Returns the first order derivative of this {@code Function}
	 * 
	 * @param var The variable we are differentiating in respect of
	 * @return The derivative of this function in respect of the variable provided on all the ranges (if exist) of the function
	 * @see Range
	 */
	public abstract Function differentiate (String var);

	// public Function integrate (String var)
	// {
	// 	return null;
	// }

	// public Number integrate (String var, double a, double b)
	// {
	// 	return null;
	// }

	/**
	 * Gets the {@code String} representation of this function
	 * 
	 * @return The {@code String} form of the function
	 */
	@Override
	public String toString ()
	{
		ArrayList<String> variabless = new ArrayList<String> (variables.stream().sorted().collect (Collectors.toList()));
		Collections.sort (variabless);
		String vars = "";
		for (int i = 0; i < variabless.size(); i++)
		{
			if (i != 0 && i != variabless.size() - 1)
				vars += ", ";
			vars += variabless.get (i);
		}
		return name + "(" + vars + ") = ";
	}

	// public abstract Function multiply (Function f);
}
