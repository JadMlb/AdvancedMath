package com.AdvancedMath.EqTree;

public class VariableNode extends Node 
{
	private String name;

	public VariableNode (String name)
	{
		this.name = name;
	}

	public String getName ()
	{
		return this.name;
	}

	public void setName (String name)
	{
		this.name = name;
	}

	@Override
	public String toString () 
	{
		return name;
	}
	
	/**
	 * Checks if the given argument is a declared variable
	 * @return {@code true} if {@code o} is this instance, if {@code o} is a {@code String} or a {@code VariableNode} of the same value as the name of this variable
	 */
	@Override
	public boolean equals (Object o)
	{
		if (this == o)
			return true;
		if (o instanceof VariableNode n)
			return name.equals (n.getName());
		if (o instanceof String s)
			return name.equals (s);
		return false;
	}
}
