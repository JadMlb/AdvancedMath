package com.AdvancedMath.EqTree;

import com.AdvancedMath.Numbers.FloatValue;
import com.AdvancedMath.Numbers.FractionValue;
import com.AdvancedMath.Numbers.Number;
import com.AdvancedMath.Numbers.Value;

public class VariableNode extends Node 
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
	 * Evaluates whether two instances of {@code VariableNode} have the same variable name in order to operate on them.
	 * 
	 * @param v The other instance of {@code VariableNode} to check for compatibility
	 * @return {@code true} if both instances have the same variable name, {@code false} otherwise
	 */
	public boolean isCompatibleWith (VariableNode v)
	{
		return this.name.equals (v.name);
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
