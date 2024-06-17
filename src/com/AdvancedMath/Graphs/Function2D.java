package com.AdvancedMath.Graphs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import com.AdvancedMath.Numbers.Number;

import com.AdvancedMath.EqTree.Node;
import com.AdvancedMath.EqTree.NumberNode;
import com.AdvancedMath.EqTree.OperatorNode;
import com.AdvancedMath.EqTree.VariableNode;

/**
 * Class that represents functions to be manipulated
 */
public class Function2D extends Function
{
	private Node tree;

	/**
	 * Creates a {@code Function} name(variables) = expression. Parses the {@code String} expression and builds it into a tree
	 * 
	 * @param name The name of the function (f(x), g(x, y), ...)
	 * @param variables An array of {@code String} containing the names of the variables that this function depends on
	 * @param expression The {@code String} that represents this function
	 */
	public Function2D (String name, String variable, String expression)
	{
		super (name, new HashSet<> (Arrays.asList (variable)));
		this.tree = Node.parse (expression + ")");
	}

	/**
	 * Creates a {@code Function} name(variables) = expression, defined on R = (-infinity, +infinity)
	 * 
	 * @param name The name of the function (f(x), g(x, y), ...)
	 * @param variables An array of {@code String} containing the names of the variables that this function depends on
	 * @param expression The {@code Node} that represents this function
	 */
	public Function2D (String name, String variable, Node expression)
	{
		super (name, new HashSet<> (Arrays.asList (variable)));
		this.tree = expression;
	}

	private Function2D (String name, HashSet<String> variables, Node expression)
	{
		super (name, variables);
		this.tree = expression;
	}

	/**
	 * Gets the {@code Node} tree representing this function. Equivalent to {@code toNode (Range.R)}
	 * 
	 * @return The binary tree of this function on the range R (-infinty, +infinity)
	 */
	@Override
	public Node toNode ()
	{
		return tree;
	}
	
	/**
	 * Gets the {@code Node} tree representing this function on the given rangeof if the range is included in the defined range of the function
	 * 
	 * @return The binary tree of this function on the given range
	 */
	@Override
	public Node toNode (Range r)
	{
		return tree;
	}

	/**
	 * Get the value of f(a, b, ...)
	 * 
	 * @param x The values mapped to the name of the variables at which we want to evaluate the function. 
	 * @return The value of the function for the specified variables
	 */
	@Override
	public Node of (HashMap<String, Number> x)
	{
		if (tree instanceof OperatorNode o)
			// return OperatorNode.simplify (o, x);
			throw new UnsupportedOperationException ("This mode is currently not supported");
		if (tree instanceof VariableNode v && x != null && x.get (v.getName()) != null)
			return new NumberNode (x.get (v.getName()));
		return new NumberNode (Number.valueOf (tree, x));
	}

	@Override
	protected void calcDomain ()
	{
		
	}
	
	/**
	 * Executes {@link Function2D#differentiate(String)} by providing the declared variable and returns the first order derivative of this {@code Function} with respect to its variable
	 * 
	 * @return The derivative of this function in respect to its declared variable
	 * 
	 * @see Function2D#differentiate(String)
	 */
	public Function differentiate ()
	{
		// return new Function2D (getName() + "'", getVariables(), OperatorNode.simplify (deriveNode (tree, getVariables().iterator().next())));
		return differentiate (getVariables().iterator().next());
	}

	/**
	 * Returns the first order derivative of this {@code Function}
	 * 
	 * @param var The variable we are differentiating in respect of
	 * @return The derivative of this function in respect of the variable provided
	 */
	@Override
	public Function differentiate (String var)
	{
		// return new Function2D (getName() + "'", getVariables(), OperatorNode.simplify (deriveNode (tree, var)));
		return new Function2D (getName() + "'", getVariables(), tree.differentiate (var));
	}

	@Override
	public String toString ()
	{
		return super.toString() + tree.toString();
	}
}
