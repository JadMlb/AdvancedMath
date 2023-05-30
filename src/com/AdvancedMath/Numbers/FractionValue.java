package com.AdvancedMath.Numbers;

import com.AdvancedMath.Functionalities.Operations;

/**
 * Class that represents a fraction
 */
public class FractionValue extends Value
{
	public static FractionValue ZERO = new FractionValue (0, 1), ONE = new FractionValue (1, 1);
	
	private int num, denom;

	/**
	 * Creates a new {@code FractionValue} with the given values for the numerator and the denominator
	 * 
	 * @param num The numerator
	 * @param denom The denominator
	 * @throws IllegalArgumentException if the denominator is 0
	 */
	public FractionValue (int num, int denom) 
	{
		if (denom == 0)
			throw new IllegalArgumentException ("Denominator cannot be 0");
		
		this.num = num;
		this.denom = denom;
	}

	public int getNumerator () 
	{
		return this.num;
	}

	public void setNumerator (int num) 
	{
		this.num = num;
	}

	public int getDenomenator () 
	{
		return this.denom;
	}

	/**
	 * @param denom The new value for the denominator
	 * @throws IllegalArgumentException if the denominator is 0
	 */
	public void setDenomenator (int denom) 
	{
		if (denom == 0)
			throw new IllegalArgumentException ("Denominator cannot be 0");
		this.denom = denom;
	}

	/**
	 * Gets the {@code FloatValue} equal to this fraction
	 * 
	 * @return The {@code FloatValue} of this fraction
	 */
	public FloatValue toFloatValue ()
	{
		return new FloatValue (((double) num) / denom);
	}

	/**
	 * Simplifies this fraction to its irriductible form
	 * 
	 * @see Operations#gcd(int, int)
	 */
	public void reduce ()
	{
		int gcd = Operations.gcd (num, denom);

		num /= gcd; 
		denom /= gcd;
	}

	/**
	 * Returns a simplified copy of this fraction to its irriductible form
	 * 
	 * @return The simplifies fraction
	 * @see Operations#gcd(int, int)
	 */
	public FractionValue reduceCopy ()
	{
		int gcd = Operations.gcd (num, denom);

		return new FractionValue (num / gcd, denom / gcd);
	}

	/**
	 * Inverts this fraction, i.e., the denominator becomes the numerator and vice versa.
	 * 
	 * @return The inverted fraction
	 */
	public FractionValue inverse ()
	{
		FractionValue res = new FractionValue (denom, num);
		res.reduce();
		return res;
	}

	@Override
	public Value add (Value v)
	{
		if (v instanceof FloatValue || v instanceof ConstantValue)
			return new FloatValue (this.toFloatValue().getDoubleValue() + v.getDoubleValue());

		FractionValue fr = (FractionValue) v;
		return new FractionValue(num * fr.getDenomenator() + denom * fr.getNumerator(), denom * fr.getDenomenator()).reduceCopy();
	}

	@Override
	public Value subtract (Value v)
	{
		if (v instanceof FloatValue || v instanceof ConstantValue)
			return new FloatValue (this.toFloatValue().getDoubleValue() - v.getDoubleValue());

		FractionValue fr = (FractionValue) v;
		return new FractionValue(num * fr.getDenomenator() - denom * fr.getNumerator(), denom * fr.getDenomenator()).reduceCopy();
	}

	@Override
	public Value multiply (Value v)
	{
		if (v instanceof FloatValue f)
			return new FloatValue (this.toFloatValue().getDoubleValue() * f.getDoubleValue());
		
		if (v instanceof ConstantValue c)
			return c.multiply (this);

		FractionValue fr = (FractionValue) v;
		return new FractionValue(num * fr.getNumerator(), denom * fr.getDenomenator()).reduceCopy();
	}

	@Override
	public Value divide (Value v)
	{
		if (v instanceof FloatValue f)
			return new FloatValue (this.toFloatValue().getDoubleValue() / f.getDoubleValue());

		if (v instanceof ConstantValue c)
			return c.divide (this);

		FractionValue fr = (FractionValue) v;
		return this.multiply (fr.inverse());
	}

	@Override
	public Value pow (Value v)
	{
		if (v instanceof FloatValue || v instanceof ConstantValue)
			return new FloatValue (Math.pow (this.toFloatValue().getDoubleValue(), v.getDoubleValue()));

		FractionValue fr = (FractionValue) v;
		return new FloatValue (Math.pow (this.toFloatValue().getDoubleValue(), fr.toFloatValue().getDoubleValue()));
	}

	@Override
	public String toString () 
	{
		if (denom == 1)
			return String.valueOf (num);
		return num + "/" + denom;
	}

	@Override
	public double getDoubleValue ()
	{
		return this.toFloatValue().getDoubleValue();
	}

	@Override
	public Value negateCopy ()
	{
		return new FractionValue (-num, denom);
	}

	@Override
	public void negate ()
	{
		this.num = - this.num;
	}
}
