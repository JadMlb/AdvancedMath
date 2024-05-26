package com.AdvancedMath.EqTree;

/**
 * Interface that defines wether an item can be simplified or not
 */
public interface Simplifiable
{
	/**
	 * Simplifies current node
	 * 
	 * @return binary tree representing the simplified equation
	 */
	public Node simplify ();
}
