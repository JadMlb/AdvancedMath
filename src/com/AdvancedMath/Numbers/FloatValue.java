package com.AdvancedMath.Numbers;

/**
 * Class that wraps a {@code double}
 */
public class FloatValue extends Value
{
	private Double val;

	/**
	 * Creates a new {@code FloatValue} with the value {@code val}. If {@code val} is less than 10^-9, it is considered equal to 0
	 * 
	 * @param val
	 */
	public FloatValue (Double val) 
	{
		if (Math.abs (val) < 1e-9)
			this.val = 0.0;
		else
			this.val = val;
	}

	@Override
	public double getDoubleValue () 
	{
		return this.val;
	}

	public void setValue (Double val) 
	{
		this.val = val;
	}

	/**
	 * Gets the fraction represendation, or an approximate fraction of the provided decimal number.
	 * 
	 * <p>Inspired by this post https://math.stackexchange.com/a/1404453</p>
	 * <strong>Note:</strong> a fraction of an approximate value (after 30 tries and with a precision of 10^-5) will be returned even for irractional numbers
	 * 
	 * @return {@code FractionValue} of the decimal number
	 */
	public FractionValue getFraction ()
	{
		FractionValue lower = null, upper = null;
		if (this.val > 0)
		{
			lower = new FractionValue (0, 1);
			upper = new FractionValue (val.intValue() + 1, 1);
		}
		else
		{
			lower = new FractionValue (- val.intValue() - 1, 1);
			upper = new FractionValue (0, 1);
		}

		for (int i = 0; i < 30; i++)
		{
			if (val == lower.toFloatValue().getDoubleValue() || Math.abs ((val - lower.toFloatValue().getDoubleValue()) / val) * 100 < 0.00001)
				return lower.reduceCopy();
			
			if (val == upper.toFloatValue().getDoubleValue() || Math.abs ((val - upper.toFloatValue().getDoubleValue()) / val) * 100 < 0.00001)
				return upper.reduceCopy();

			FractionValue inter = new FractionValue (lower.getNumerator() + upper.getNumerator(), lower.getDenomenator() + upper.getDenomenator());
			if (inter.compare (this) == 1)
				upper = inter;
			else if (inter.compare (this) == -1)
				lower = inter;
			else
				return inter.reduceCopy();
		}

		if (Math.abs ((val - lower.toFloatValue().getDoubleValue()) / val) * 100 - Math.abs ((val - upper.toFloatValue().getDoubleValue()) / val) * 100 > 0)
			return upper.reduceCopy();
		return lower.reduceCopy();
	}

	/**
	 * @return A {@code FloatValue} equals to this + v
	 */
	@Override
	public Value add (Value v)
	{
		return new FloatValue (val + v.getDoubleValue());
	}

	/**
	 * @return A {@code FloatValue} equals to this - v
	 */
	@Override
	public Value subtract (Value v)
	{
		return new FloatValue (val - v.getDoubleValue());
	}

	/**
	 * @return A {@code FloatValue} equals to this * v
	 */
	@Override
	public Value multiply (Value v)
	{
		return new FloatValue (val * v.getDoubleValue());
	}

	/**
	 * @return A {@code FloatValue} equals to this / v
	 */
	@Override
	public Value divide (Value v)
	{
		return new FloatValue (val / v.getDoubleValue());
	}

	/**
	 * Raises this {@code FloatValue} to the power of {@code Value}.
	 * 
	 * If {@code v} is equal to 1/2, get the square root, else, if it is equal to 1/3, get the cubic root.
	 * 
	 * @apiNote Because of the limitations of {@link Math#pow(double, double)}, a {@code NaN} might be returned
	 */
	@Override
	public Value pow (Value v)
	{
		if (v.getDoubleValue() == 0.5 && val >= 0)
			return new FloatValue (Math.sqrt (val));
		if (v.getDoubleValue() == 1.0/3)
			return new FloatValue (Math.cbrt (val));
		
		return new FloatValue (Math.pow (val, v.getDoubleValue()));
	}

	@Override
	public Value negateCopy ()
	{
		return new FloatValue (-val);
	}

	@Override
	public void negate ()
	{
		this.val = - this.val;
	}

	@Override
	public String toString () 
	{
		return formatNumber (val);
	}
}
