package com.AdvancedMath.Numbers;

import java.text.NumberFormat;

public abstract class Value implements Cloneable
{
	/**
	 * Sets the value to the opposite of the current one. If the current value is positive, it becomes negative and vice versa.
	 */
	public abstract void negate ();

	/**
	 * Returns the opposite value of the current one without modifying the original.
	 * 
	 * @return A new {@code Value} with its value equal to the negative of the current one
	 */
	public abstract Value negateCopy ();
	public abstract Value add (Value v);
	public abstract Value subtract (Value v);
	public abstract Value multiply (Value v);
	public abstract Value divide (Value v);
	public abstract Value pow (Value v);
	@Override
	public abstract Value clone ();

	/**
	 * Gets the {@code double} that this {@code Value} represents
	 * 
	 * @return The {@code double} equal to this {@code Value}
	 */
	public abstract double getDoubleValue();
	
	/**
	 * Compares two {@code Value}s
	 * 
	 * <p>If this {@code Value} is
	 * <ul>
	 * 	<li>equal to {@code v}, 0 is returned</li>
	 * 	<li>greater than {@code v}, 1 is returned</li>
	 * 	<li>less than {@code v}, -1 is returned</li>
	 * 
	 * @param v
	 * @return
	 */
	public int compare (Value v)
	{
		if (this == v)
			return 0;

		if (this.getDoubleValue() > v.getDoubleValue())
			return 1;
		else if (this.getDoubleValue() < v.getDoubleValue())
			return -1;
		return 0;
	}

	/**
	 * Values' implementation of equals method
	 * 
	 * @param o The parameter to test the equality
	 * @return {@code true} if parameter has same double value or to a precison of 10^-5 or if the 2 objects are the same, false otherwise
	 * 
	 * @see Value#getDoubleValue()
	 */
	@Override
	public boolean equals (Object o)
	{
		if (o instanceof Double d)
			return getDoubleValue() == d;
		
		if (!(o instanceof Value v))
			return false;

		if (this == o)
			return true;

		return this.getDoubleValue() == v.getDoubleValue() || Math.abs ((this.getDoubleValue() - v.getDoubleValue()) / this.getDoubleValue()) * 100 < 0.00001;
	}

	/**
	 * Formats a double value, if the number has 0 as the value of the decimal part .0, don't display it
	 * 
	 * @param d The {@code double} to format
	 * @return The {@code String} representing this {@code double}
	 */
	protected String formatNumber (double d)
	{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits (20);

		// String nb = String.valueOf (d);
		// if (nb.indexOf (".") > -1)
		// 	if (Long.valueOf (nb.split("\\.")[1]) == 0)
		// 		nb = nb.split("\\.")[0];

		return nf.format (d);
	}
}
