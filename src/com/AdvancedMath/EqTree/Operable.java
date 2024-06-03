package com.AdvancedMath.EqTree;

public interface Operable
{
	/**
	 * Negates the value of the current object in-place
	 * 
	 */
	public void negate ();

	/**
	 * Negates the value of the current object
	 * 
	 * @return A copy of the object with its value negated
	 */
	public Operable negateCopy ();

	/**
	 * Adds the values of current object with v
	 * 
	 * @param v The value to add
	 * @return The sum of both values
	 * @throws IllegalArgumentException if this and v are not compatible
	 */
	public Operable add (Operable v) throws IllegalArgumentException;

	/**
	 * Subtracts the values of current object with v
	 * 
	 * @param v The value to subtract
	 * @return The difference of both values
	 * @throws IllegalArgumentException if this and v are not compatible
	 */
	public Operable subtract (Operable v) throws IllegalArgumentException;

	/**
	 * Multiplies the values of current object with v
	 * 
	 * @param v The value to multiply with
	 * @return The product of both values
	 * @throws IllegalArgumentException if this and v are not compatible
	 */
	public Operable multiply (Operable v) throws IllegalArgumentException;

	/**
	 * Divides the values of current object by v
	 * 
	 * @param v The value to divide by
	 * @return The result of the division both values
	 * @throws IllegalArgumentException if this and v are not compatible
	 * @throws ArithmeticException if the value of v is zero
	 */
	public Operable divide (Operable v) throws IllegalArgumentException, ArithmeticException;

	/**
	 * Raises the value of the current object to the power of that of v
	 * 
	 * @param v The exponent
	 * @return The result of the this raied to the power of v
	 * @throws IllegalArgumentException if this and v are not compatible
	 */
	public Operable pow (Operable v) throws IllegalArgumentException;
}
