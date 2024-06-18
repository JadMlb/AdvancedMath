package com.AdvancedMath.Exceptions;

/**
 * Exception thrown whenever a variable is not allowed to be used
 * 
 * @see com.AdvancedMath.Graphs.Function
 */
public class VariableNotAllowedException extends Exception
{
	public VariableNotAllowedException (String variable)
	{
		super ("The variable \"" + variable + "\" is not allowed to be in the tree");
	}
}
