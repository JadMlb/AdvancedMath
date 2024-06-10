package com.AdvancedMath.Graphs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

import com.AdvancedMath.EqTree.Node;
import com.AdvancedMath.EqTree.NumberNode;
import com.AdvancedMath.EqTree.OperatorNode;
import com.AdvancedMath.EqTree.VariableNode;
import com.AdvancedMath.Functionalities.Operators;
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
	 * Changes a variable name into another one
	 * 
	 * @param originalVariable The old variable
	 * @param newVariable The new variable name
	 */
	public void changeVariable (String originalVariable, String newVariable) 
	{
		variables.remove (originalVariable);
		variables.add (newVariable);
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

	protected static Node deriveNode (Node root, String var)
	{
		if (root == null)
			return null;
		
		if (root instanceof NumberNode)
			return new NumberNode (Number.ZERO);
		else if (root instanceof VariableNode v)
		{
			if (v.getName().equals (var))
				return new NumberNode (Number.ONE);
			else
				return new NumberNode (Number.ZERO);
		}
		else if (root instanceof OperatorNode o)
		{
			Number left = null, right = null;

			try
			{
				left = Number.valueOf (o.getLeft(), null);
			}
			catch (Exception e){}
			
			try
			{
				right = Number.valueOf (o.getRight(), null);
			}
			catch (Exception e){}

			// we are deriving a number
			if (o.getOperator().nbParams() == 2 && left != null && right != null || o.getOperator().nbParams() == 1 && right != null)
			{
				return new NumberNode (Number.ZERO);
			}
			
			switch (o.getOperator())
			{
				case ADD: case SUB: 
					if (left != null)
						return deriveNode (o.getRight(), var);
					else if (right != null)
						return deriveNode (o.getLeft(), var);

					return new OperatorNode (o.getOperator(), deriveNode (root.getLeft(), var), deriveNode (root.getRight(), var));
				case MUL: 
					Node derivLeft = null, derivRight = null;
					if (left != null)
					{
						derivLeft = new NumberNode (left);
						derivRight = deriveNode (o.getRight(), var);
					}
					else if (right != null)
					{
						derivLeft = new NumberNode (right);
						derivRight = deriveNode (o.getLeft(), var);
					}
					else
					{
						derivLeft = new OperatorNode (Operators.MUL, deriveNode (o.getLeft(), var), o.getRight());
						derivRight = new OperatorNode (Operators.MUL, o.getLeft(), deriveNode (o.getRight(), var));
					}

					try
					{
						Number n = Number.valueOf (derivRight, null);
						if (derivLeft instanceof NumberNode nb)
						{
							return new NumberNode (nb.getValue().multiply (n));
						}
					} 
					catch (Exception e){}
						
					return new OperatorNode
					(
						right == null && left == null ? Operators.ADD : Operators.MUL,
						derivLeft,
						derivRight
					);
				case DIV: 
					{
						Number pow = Number.ONE.negate();
						if (o.getRight() instanceof OperatorNode op && op.getOperator().equals (Operators.POW))
							pow = ((NumberNode) op.getRight()).getValue();

						Node equivalent = new OperatorNode
						(
							Operators.MUL,
							o.getLeft(),
							new OperatorNode
							(
								Operators.POW,
								o.getRight(),
								new NumberNode (pow)
							)
						);
						return deriveNode (equivalent, var);
					}
				case POW:
					try // Power is a number use n*f(x)^(n-1)*d_dx(f)
					{
						Number pow = Number.valueOf (o.getRight(), null);
						pow = pow.subtract (Number.ONE);
						Node powNode = null;
						if (pow.equals (Number.ONE))
							powNode = o.getLeft();
						else
							powNode = new OperatorNode
							(
								Operators.POW,
								o.getLeft(),
								new NumberNode (pow)
							);

						return new OperatorNode
						(
							Operators.MUL,
							new NumberNode (pow.add (Number.ONE)),
							new OperatorNode
							(
								Operators.MUL,
								deriveNode (root.getLeft(), var),
								powNode
							)
						);
					}
					catch (IllegalArgumentException iae) // use deriv (u^v) = u^v * (lnu * dv/dx + v/u * du/dx)
					{
						return new OperatorNode
						(
							Operators.MUL,
							o,
							new OperatorNode
							(
								Operators.ADD,
								new OperatorNode
								(
									Operators.MUL,
									new OperatorNode
									(
										Operators.LN,
										null,
										o.getLeft()
									),
									deriveNode (o.getRight(), var)
								),
								new OperatorNode
								(
									Operators.MUL,
									new OperatorNode
									(
										Operators.DIV,
										o.getRight(),
										o.getLeft()
									),
									deriveNode (o.getLeft(), var)
								)
							)
						);
					}
				case LN: return new OperatorNode
					(
						Operators.DIV,
						deriveNode (o.getRight(), var),
						o.getRight()
					);
				case EXP: return new OperatorNode
					(
						Operators.MUL,
						deriveNode (o.getRight(), var),
						new OperatorNode
						(
							Operators.EXP,
							null,
							o.getRight()
						)
					);
				case ABS: return new OperatorNode
					(
						Operators.DIV,
						o,
						o.getRight()
					);
				case SIN: return new OperatorNode
					(
						Operators.MUL,
						new OperatorNode
						(
							Operators.SUB,
							null,
							deriveNode (o.getRight(), var)
						),
						new OperatorNode
						(
							Operators.COS,
							null,
							o.getRight()
						)
					);
				case COS: return new OperatorNode
					(
						Operators.MUL,
						deriveNode (o.getRight(), var),
						new OperatorNode
						(
							Operators.SIN,
							null,
							o.getRight()
						)
					);
				case TAN: return new OperatorNode
					(
						Operators.DIV,
						deriveNode (o.getRight(), var),
						new OperatorNode
						(
							Operators.POW,
							new OperatorNode (Operators.COS, null, o.getRight()),
							new NumberNode (2.0, 0.0)
						)
					);
				case ASIN: return new OperatorNode
					(
						Operators.DIV,
						deriveNode (o.getRight(), var),
						new OperatorNode
						(
							Operators.POW,
							new OperatorNode
							(
								Operators.SUB,
								new NumberNode (Number.ONE),
								new OperatorNode (Operators.POW, o.getRight(), new NumberNode (Number.real (2.0)))
							),
							new NumberNode (Number.real (new FractionValue (1, 5)))
						)
					);
				case ACOS: return new OperatorNode
					(
						Operators.SUB,
						new NumberNode (Number.ZERO),
						new OperatorNode
						(
							Operators.DIV,
							deriveNode (o.getRight(), var),
							new OperatorNode
							(
								Operators.POW,
								new OperatorNode
								(
									Operators.SUB,
									new NumberNode (Number.ONE),
									new OperatorNode (Operators.POW, o.getRight(), new NumberNode (2.0, 0.0))
								),
								new NumberNode (Number.real (new FractionValue (1, 5)))
							)
						)
					);
				case ATAN: return new OperatorNode
					(
						Operators.DIV,
						deriveNode (o.getRight(), var),
						new OperatorNode
						(
							Operators.ADD,
							new NumberNode (Number.ONE),
							new OperatorNode (Operators.POW, o.getRight(), new NumberNode (2.0, 0.0))
						)
					);
				case SINH: return new OperatorNode
					(
						Operators.MUL,
						deriveNode (o.getRight(), var),
						new OperatorNode
						(
							Operators.COSH,
							null,
							o.getRight()
						)
					);
				case COSH: return new OperatorNode
					(
						Operators.MUL,
						deriveNode (o.getRight(), var),
						new OperatorNode
						(
							Operators.SINH,
							null,
							o.getRight()
						)
					);
				case TANH: return new OperatorNode
					(
						Operators.DIV,
						deriveNode (o.getRight(), var),
						new OperatorNode
						(
							Operators.POW,
							new OperatorNode (Operators.COSH, null, o.getRight()),
							new NumberNode (2.0, 0.0)
						)
					);
				case ASH: return new OperatorNode
					(
						Operators.DIV,
						deriveNode (o.getRight(), var),
						new OperatorNode
						(
							Operators.POW,
							new OperatorNode
							(
								Operators.ADD,
								new NumberNode (Number.ONE),
								new OperatorNode (Operators.POW, o.getRight(), new NumberNode (2.0, 0.0))
							),
							new NumberNode (Number.real (new FractionValue (1, 5)))
						)
					);
				case ACH: return new OperatorNode
					(
						Operators.DIV,
						deriveNode (o.getRight(), var),
						new OperatorNode 
						(
							Operators.MUL,
							new OperatorNode
							(
								Operators.POW,
								new OperatorNode
								(
									Operators.SUB,
									o.getRight(),
									new NumberNode (Number.ONE)
								),
								new NumberNode (Number.real (new FractionValue (1, 5)))
							),
							new OperatorNode
							(
								Operators.POW,
								new OperatorNode
								(
									Operators.ADD,
									o.getRight(),
									new NumberNode (Number.ONE)
								),
								new NumberNode (Number.real (new FractionValue (1, 5)))
							)
						)
					);
				case ATH: return new OperatorNode
					(
						Operators.DIV,
						deriveNode (o.getRight(), var),
						new OperatorNode
						(
							Operators.SUB,
							new NumberNode (Number.ONE),
							new OperatorNode (Operators.POW, o.getRight(), new NumberNode (2.0, 0.0))
						)
					);
				default:
					return null;
			}
		}

		return null;
	}

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
