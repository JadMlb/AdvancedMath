package com.AdvancedMath.EqTree;

import com.AdvancedMath.Numbers.FloatValue;
import com.AdvancedMath.Numbers.FractionValue;
import com.AdvancedMath.Numbers.Number;
import com.AdvancedMath.Numbers.Value;

public class VariableNode extends Node implements Operable
{
	private String name;
	private Number multiplier;
	private Value power;

	/**
	 * Creates an instance of {@code VariableNode} with the specified parameters under the format: multiplier * name ^ power
	 * 
	 * @param multiplier The multiplier specifying how many of the variables there are. If null, the value defaults to a {@code Number} with a value of 1
	 * @param name The name / label of the variable
	 * @param power The power the variable is raised to. If null, the value defaults to a {@code FloatValue} with a value of 1
	 * 
	 * @throws IllegalArgumentException if given name is null
	 */
	public VariableNode (Number multiplier, String name, Value power)
	{
		if (name == null)
			throw new IllegalArgumentException ("VariableNode variable name cannot be null");
		
		if (multiplier == null)
			this.multiplier = Number.ONE;
		else
			this.multiplier = multiplier;
		this.name = name;
		
		if (power == null)
			this.power = new FloatValue (1.);
		else
			this.power = power;
	}

	/**
	 * Creates an instance of {@code VariableNode} with the specified parameters under the format: name
	 * <p>Returns a {@code new VariableNode (null, name, null)}</p>
	 * 
	 * @param name The name / label of the variable
	 * @throws IllegalArgumentException if given name is null
	 * @see #VariableNode(Number, String, Value)
	 */
	public VariableNode (String name)
	{
		this (null, name, null);
	}

	public String getName ()
	{
		return this.name;
	}

	public void setName (String name)
	{
		this.name = name;
	}

	public Number getMultiplier ()
	{
		return multiplier;
	}

	public void setMultiplier (Number multiplier)
	{
		this.multiplier = multiplier;
	}
	
	public Value getPower ()
	{
		return power;
	}

	public void setPower (Value power)
	{
		this.power = power;
	}

	/**
	 * @return a deep copy of the VariableNode
	 */
	@Override
	public Node simplify ()
	{
		return new VariableNode (multiplier.clone(), name, power.clone());
	}

	/**
	 * Evaluates whether two instances of {@code VariableNode} have the same variable name and degree in order to operate on them.
	 * 
	 * @param v The other instance of {@code VariableNode} to check for compatibility
	 * @return {@code true} if both instances have the same variable name, {@code false} otherwise
	 */
	public boolean isCompatibleWith (VariableNode v)
	{
		return this.name.equals (v.name) && this.power.equals (v.power);
	}

	@Override
	public int sgn ()
	{
		return multiplier.getX().compareTo (FractionValue.ZERO);
	}

	@Override
	public void negate ()
	{
		this.multiplier = this.multiplier.negate();
	}

	@Override
	public Operable negateCopy ()
	{
		return new VariableNode (multiplier.negate(), name, power.clone());
	}

	@Override
	public Operable add (Operable v) throws IllegalArgumentException
	{
		if (v instanceof VariableNode var && isCompatibleWith (var))
			return new VariableNode (multiplier.add (var.multiplier), name, power.clone());
		else
			throw new IllegalArgumentException ("Cannot add a VariableNode to a non VariableNode");
	}

	@Override
	public Operable subtract (Operable v) throws IllegalArgumentException
	{
		if (v instanceof VariableNode var && isCompatibleWith (var))
			if (this.multiplier.equals (var.multiplier))
				return new NumberNode (Number.ZERO);
			else
				return new VariableNode (multiplier.subtract (var.multiplier), name, power.clone());
		else
			throw new IllegalArgumentException ("Cannot subtract a non VariableNode from a VariableNode");
	}

	@Override
	public Operable multiply (Operable v) throws IllegalArgumentException
	{
		if (v instanceof VariableNode var)
			if (this.name.equals (var.name))
				return new VariableNode (this.multiplier.multiply (var.multiplier), name, power.add ((var.getPower())));
			else
				throw new IllegalArgumentException ("Cannot multiply VariableNodes of different variable names");
		else if (v instanceof NumberNode n)
			return new VariableNode (multiplier.multiply (n.getValue()), name, power.clone());
		else
			throw new IllegalArgumentException ("Cannot multiply a VariableNode with a non Operable");
	}

	@Override
	public Operable divide (Operable v) throws IllegalArgumentException, ArithmeticException
	{
		if (v instanceof VariableNode var)
			if (this.name.equals (var.name))
				if (this.power.equals (var.power))
					return new NumberNode (this.multiplier.divide (var.multiplier));
				else
					return new VariableNode (this.multiplier.divide (var.multiplier), name, power.subtract ((var.getPower())));
			else
				throw new IllegalArgumentException ("Cannot divide VariableNodes of different variable names");
		else if (v instanceof NumberNode n)
			if (n.getValue().equals (Number.ZERO))
				throw new ArithmeticException ("Cannot divide by zero");
			else
				return new VariableNode (multiplier.divide (n.getValue()), name, power.clone());
		else
			throw new IllegalArgumentException ("Cannot divide a VariableNode by a non Operable");
	}

	@Override
	public Operable pow (Operable v) throws IllegalArgumentException
	{
		if (v instanceof VariableNode)
			throw new IllegalArgumentException ("VariableNode cannot be the exponent of another");
		else if (v instanceof NumberNode n)
			if (n.getValue().isPureReal())
				return new VariableNode (multiplier.clone(), name, power.multiply (n.getValue().getX()));
			else
				throw new IllegalArgumentException ("The exponent of a VariableNode cannot be a complex Number");
		else
			throw new IllegalArgumentException ("Cannot raise a VariableNode to the power of a non VariableNode");
	}

	@Override
	public String toString () 
	{
		String s = "";
		boolean needsBrackets = !multiplier.isPureImaginary() && !multiplier.isPureReal();
		
		if (needsBrackets)
			s += "(";

		if (!multiplier.equals (Number.ONE))
		{
			if (multiplier.equals (Number.real (-1.)))
				s += "-";
			else
				s += multiplier;
		}

		if (needsBrackets)
			s += ")";

		s += name;

		if (!power.equals (FractionValue.ONE))
			s += "^" + power;

		return s;
	}
	
	/**
	 * Checks if the given argument is a declared variable
	 * @return {@code true} if {@code o} is this instance or a {@code VariableNode} with the same values of name, multiplier and power of this variable
	 */
	@Override
	public boolean equals (Object o)
	{
		if (this == o)
			return true;
		if (o instanceof VariableNode n)
			return multiplier.equals (n.multiplier) && name.equals (n.name) && power.equals (n.power);
		return false;
	}
}
