package com.AdvancedMath.EqTree;

import com.AdvancedMath.Numbers.Number;

/**
 * Class that holds a {@code Number} element in it. Child node in a binary tree
 */
public class NumberNode extends Node implements Operable
{
	private Number number;

	/**
	 * Create a node holding the {@code Number} real + i*imaginary
	 * 
	 * @param real The real part of the number
	 * @param imaginary The imaginary part of the number
	 */
	public NumberNode (Double real, Double imaginary)
	{
		super();

		this.number = new Number (real, imaginary);
	}
	
	/**
	 * Create a node holding the given {@code Number}
	 * 
	 * @param n The number to be wrapped in the node
	 */
	public NumberNode (Number n)
	{
		super();

		this.number = n;
	}

	/**
	 * Get the {@code Number} held in this node
	 * 
	 * @return The number held
	 */
	public Number getValue () 
	{
		return this.number;
	}

	/**
	 * Changes the {@code Number} held in this node
	 * 
	 * @param value The new number
	 */
	public void setValue (Number value) 
	{
		this.number = value;
	}

	/**
	 * @return a deep copy of the NumberNode
	 * 
	 */
	@Override
	public Node simplify ()
	{
		return new NumberNode (number.clone());
	}

	@Override
	public void negate ()
	{
		this.number = this.number.negate();
	}

	@Override
	public Operable negateCopy ()
	{
		return new NumberNode (number.negate());
	}

	@Override
	public Operable add (Operable v) throws IllegalArgumentException
	{
		if (v instanceof NumberNode n)
			return new NumberNode (this.number.add (n.number));
		else
			throw new IllegalArgumentException ("Cannot add a NumberNode to a non NumberNode");
	}

	@Override
	public Operable subtract (Operable v) throws IllegalArgumentException
	{
		if (v instanceof NumberNode n)
			return new NumberNode (this.number.subtract (n.number));
		else
			throw new IllegalArgumentException ("Cannot multiply a NumberNode with a non NumberNode");
	}

	@Override
	public Operable multiply (Operable v) throws IllegalArgumentException
	{
		if (v instanceof NumberNode n)
			return new NumberNode (this.number.multiply (n.getValue()));
		else if (v instanceof VariableNode var)
			return new VariableNode (this.number.multiply (var.getMultiplier()), var.getName(), var.getPower().clone());
		else
			throw new IllegalArgumentException ("Cannot multiply a VariableNode with a non Operable");
	}

	@Override
	public Operable divide (Operable v) throws IllegalArgumentException, ArithmeticException
	{
		if (v instanceof NumberNode n)
			if (n.number.equals (Number.ZERO))
				throw new ArithmeticException ("Cannot divide by zero");
			else if (this.number.equals (n.getValue()))
				return new NumberNode (Number.ONE);
			else
				return new NumberNode (this.number.divide (n.number));
		else if (v instanceof VariableNode var)
			if (var.getMultiplier().equals (Number.ZERO))
				throw new ArithmeticException ("Cannot divide by zero");
			else
				return new VariableNode (this.number.divide (var.getMultiplier()), var.getName(), var.getPower().negateCopy());
		else
			throw new IllegalArgumentException ("Cannot divide a NumberNode by a non Operable");
	}

	@Override
	public Operable pow (Operable v) throws IllegalArgumentException
	{
		if (v instanceof VariableNode)
			throw new IllegalArgumentException ("VariableNode cannot be the exponent of a NumberNode");
		else if (v instanceof NumberNode n)
		{
			if (this.number.equals (Number.ZERO))
				throw new IllegalArgumentException ("0^0 is undefined");
			else if (n.number.equals (Number.ONE))
				return new NumberNode (this.number.clone());
			else
				return new NumberNode (this.number.pow (n.number));
		}
		else
			throw new IllegalArgumentException ("Cannot raise a VariableNode to the power of a non VariableNode");
	}

	@Override
	public String toString () 
	{
		return number.toString();
	}

	/**
	 * Checks if the object is a number of the same value
	 * 
	 * @return {@code true} if the object is the same as this {@code Number}, or the object is a representation of a number (another {@code Number} or a {@code Node} that equals the number),
	 * {@code false} otherwise
	 */
	@Override
	public boolean equals (Object o)
	{
		if (this == o)
			return true;
		if (o instanceof Number n)
			return getValue().equals (n);
		if (o instanceof NumberNode n)
			return getValue().equals (n.getValue());
		if (o instanceof OperatorNode n)
			try
			{
				Number nb = Number.valueOf (n, null);
				return getValue().equals (nb);
			}
			catch (Exception e) {}
		return false;
	}
}
