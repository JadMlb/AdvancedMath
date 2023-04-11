package EqTree;

import Numbers.Number;

/**
 * Class that holds a {@code Number} element in it. Child node in a binary tree
 */
public class NumberNode extends Node
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
