package com.AdvancedMath.Numbers;

import com.AdvancedMath.Functionalities.Operators;

/**
 * Class representing constant values, like Pi, e, phi (golden ratio), ln(5), e^(-2), sqrt(10), ...
 */
public class ConstantValue extends Value
{
	public static ConstantValue PI = new ConstantValue (Math.PI), E = new ConstantValue (Math.E), PHI = new ConstantValue (1.618033988749);
	
	private double[] arguments = null;
	private double multiplier = 1;
	private Operators operator = null;

	/**
	 * Creates a new {@code ConstantValue} with value as its value. Equivalent to creating a {@code ConstantValue} with {@code POW} as the operator and 1 as the second argument, and to {@code FloatValue}
	 * 
	 * @param value The value of this constant
	 * @see FloatValue
	 */
	public ConstantValue (double value) 
	{
		this.arguments = new double [2];
		this.arguments[0] = value;
		this.arguments[1] = 1;
		operator = Operators.POW;
	}
	
	private ConstantValue (double multiplier, Operators operator, double[] arguments) 
	{
		this.arguments = arguments;
		this.multiplier = multiplier;
		this.operator = operator;
	}

	/**
	 * Creates a {@code ConstantValue} from any other {@code Value}
	 * 
	 * @param v The {@code Value} to be transformed
	 * @return The equivalent {@code ConstantValue}
	 */
	public static ConstantValue fromValue (Value v)
	{
		if (v instanceof ConstantValue c)
			return c;

		return new ConstantValue (v.getDoubleValue());
	}

	/**
	 * Creates a {@code ConstantValue} in the form of multiplier * e^(argument)
	 * 
	 * @param multiplier The multiplier of the value
	 * @param argument The argument of exp, i.e., the power that e is raised to
	 * @return The {@code ConstantValue} representing this relationship
	 */
	public static ConstantValue exp (double multiplier, double argument)
	{
		return new ConstantValue (multiplier, Operators.EXP, new double [] {argument});
	}
	
	/**
	 * Creates a {@code ConstantValue} in the form of multiplier * ln(argument)
	 * 
	 * @param multiplier The multiplier of the value
	 * @param argument The argument of ln
	 * @return The {@code ConstantValue} representing this relationship
	 * @throws IllegalArgumentException if the argument is less than or equal to 0
	 */
	public static ConstantValue ln (double multiplier, double argument)
	{
		if (argument <= 0)
			throw new IllegalArgumentException ("ln is defined for all postivie, non-zero numbers");
		
		return new ConstantValue (multiplier, Operators.LN, new double [] {argument});
	}
	
	/**
	 * Creates a {@code ConstantValue} in the form of multiplier * a^b
	 * 
	 * @param multiplier The multiplier of the value
	 * @param a The base of the power
	 * @param b The exponent
	 * @return The {@code ConstantValue} representing this relationship
	 * @throws IllegalArgumentException if any argument is {@code NaN} or infinite
	 */
	public static ConstantValue pow (double multiplier, double a, double b)
	{
		if (Double.isInfinite (multiplier) || Double.isNaN (multiplier) || Double.isInfinite (a) || Double.isNaN (a) || Double.isInfinite (b) || Double.isNaN (b))
			throw new IllegalArgumentException ("NaN and Infinity are not valable for any of the arguments or multiplier");
		
		return new ConstantValue (multiplier, Operators.POW, new double[] {a, b});
	}

	public double[] getArguments () 
	{
		return this.arguments;
	}

	public void setArguments (double[] arguments) 
	{
		this.arguments = arguments;
	}

	public double getMultiplier () 
	{
		return this.multiplier;
	}

	/**
	 * Sets the multiplier of this {@code ConstantValue}. If the provided argument is null, nothing is done
	 * 
	 * @param multiplier The new value of the multiplier
	 */
	public void setMultiplier (double multiplier) 
	{
		if (operator == null)
			return;
		
		this.multiplier = multiplier;
	}

	public Operators getOperator () 
	{
		return this.operator;
	}

	public void setOperator (Operators operator) 
	{
		this.operator = operator;
	}

	private boolean haveSameArguments (ConstantValue c)
	{
		if (arguments.length != c.getArguments().length)
			return false;
		
		for (int i = 0; i < arguments.length; i++)
			if (arguments[i] != c.getArguments()[i])
				return false;
		return true;
	}

	@Override
	public void negate ()
	{
		this.multiplier = - this.multiplier;
	}

	@Override
	public Value negateCopy ()
	{
		if (operator == null)
		{
			ConstantValue negated = new ConstantValue (arguments[0]);
			negated.setMultiplier (- multiplier);
			return negated;
		}

		return new ConstantValue (- multiplier, operator, arguments);
	}

	/**
	 * Gets the absolute value of this ConstantValue by returning it in a FloatValue
	 */
	@Override
	public Value abs ()
	{
		return new FloatValue (Math.abs (getDoubleValue()));
	}

	/**
	 * Adds two {@code ConstantValue}s. Takes into consideration the operator and changes the multiplier accordingly.
	 * If the operator is ln, and both {@code Value}s have the same multiplier, affects the argument according to ln(a) + ln(b) = ln(a*b) and ln(a) - ln(b) = ln(a/b)
	 * Else, returns a {@code FloatValue}
	 * 
	 * @see FloatValue
	 */
	@Override
	public Value add (Value v)
	{
		if (v instanceof ConstantValue c && c.getOperator() != null && operator != null && operator == c.getOperator())
			if (operator == Operators.LN && Math.abs (multiplier) == Math.abs (c.getMultiplier()))
				if (multiplier > 0 && c.getMultiplier() > 0)
					return ConstantValue.ln (multiplier, arguments[0] * c.getArguments()[1]);
				else if (multiplier > 0 && c.getMultiplier() < 0)
					return ConstantValue.ln (multiplier, arguments[0] / c.getArguments()[1]);
				else if (multiplier < 0 && c.getMultiplier() > 0)
					return ConstantValue.ln (multiplier, arguments[0] / c.getArguments()[1]);
				else
					return ConstantValue.ln (multiplier, arguments[0] * c.getArguments()[1]);
			else if (haveSameArguments (c))
				return new ConstantValue (multiplier + c.getMultiplier(), operator, arguments);

		return new FloatValue (getDoubleValue() + v.getDoubleValue());
	}

	/**
	 * Subtracts two {@code ConstantValue}s. Takes into consideration the operator and changes the multiplier accordingly.
	 * If the operator is ln, and both {@code Value}s have the same multiplier, affects the argument according to ln(a) + ln(b) = ln(a*b) and ln(a) - ln(b) = ln(a/b)
	 * Else, returns a {@code FloatValue}
	 * 
	 * @see FloatValue
	 */
	@Override
	public Value subtract (Value v)
	{
		if (v instanceof ConstantValue c && c.getOperator() != null && operator != null && operator == c.getOperator())
			if (operator == Operators.LN && Math.abs (multiplier) == Math.abs (c.getMultiplier()))
				if (multiplier > 0 && c.getMultiplier() > 0)
					return ConstantValue.ln (multiplier, arguments[0] / c.getArguments()[1]);
				else if (multiplier > 0 && c.getMultiplier() < 0)
					return ConstantValue.ln (multiplier, arguments[0] * c.getArguments()[1]);
				else if (multiplier < 0 && c.getMultiplier() > 0)
					return ConstantValue.ln (multiplier, arguments[0] * c.getArguments()[1]);
				else
					return ConstantValue.ln (multiplier, arguments[0] / c.getArguments()[1]);
			else if (haveSameArguments (c))
				return new ConstantValue (multiplier - c.getMultiplier(), operator, arguments);

		return new FloatValue (getDoubleValue() - v.getDoubleValue());
	}

	/**
	 * Multiplies two {@code ConstantValue}s. Takes into consideration the operator and changes the multiplier accordingly.
	 * If the operator is {@code POW} and both {@code Value}s have the same base or {@code EXP}, affects the argument according to a^m * a^n = a^(m+n)
	 * Else, returns a {@code FloatValue}
	 * 
	 * @see FloatValue
	 */
	@Override
	public Value multiply (Value v)
	{
		if (v instanceof ConstantValue c)
		{
			if (operator == c.getOperator())
				if (operator == Operators.EXP)
					return ConstantValue.exp (multiplier * c.getMultiplier(), arguments[0] + c.getArguments()[1]);
				else if (operator == Operators.POW && arguments[0] == c.getArguments()[0])
					return ConstantValue.pow (multiplier * c.getMultiplier(), arguments[0], arguments[1] + c.getArguments()[1]);
		}
		else
			return new ConstantValue (multiplier * v.getDoubleValue(), operator, arguments);

		return new FloatValue (getDoubleValue() * v.getDoubleValue());
	}

	/**
	 * Divides two {@code ConstantValue}s. Takes into consideration the operator and changes the multiplier accordingly.
	 * If the operator is {@code POW} and both {@code Value}s have the same base or {@code EXP}, affects the argument according to a^m / a^n = a^(m-n)
	 * Else, returns a {@code FloatValue}
	 * 
	 * @see FloatValue
	 */
	@Override
	public Value divide (Value v)
	{
		if (v.getDoubleValue() == 0)
			throw new ArithmeticException ("Cannot divide by zero");
		
		if (v instanceof ConstantValue c)
		{
			if (operator == c.getOperator())
				if (operator == Operators.EXP)
					return ConstantValue.exp (multiplier / c.getMultiplier(), arguments[0] - c.getArguments()[1]);
				else if (operator == Operators.POW && arguments[0] == c.getArguments()[0])
					return ConstantValue.pow (multiplier / c.getMultiplier(), arguments[0], arguments[1] - c.getArguments()[1]);
		}
		else
			return new ConstantValue (multiplier / v.getDoubleValue(), operator, arguments);

		return new FloatValue (getDoubleValue() / v.getDoubleValue());
	}

	/**
	 * Raises the {@code ConstantValue} to the power of {@code Value}. Affects the multiplier accordingly.
	 * If the operator is {@code POW} and both {@code Value}s have the same base or {@code EXP}, affects the argument
	 * Else, returns a {@code FloatValue}
	 * 
	 * @see FloatValue
	 */
	@Override
	public Value pow (Value v)
	{
		if (!(v instanceof ConstantValue) && operator != Operators.LN)
			return new ConstantValue (Math.pow (multiplier, v.getDoubleValue()), operator, new double [] {arguments[0], arguments[1] * v.getDoubleValue()});
		else if (v instanceof ConstantValue c && operator == Operators.EXP && c.getOperator() == Operators.LN)
			return pow (Math.pow (multiplier, c.getDoubleValue()), c.getArguments()[0], arguments[0] * c.getMultiplier());
		return new FloatValue (Math.pow (getDoubleValue(), v.getDoubleValue()));
	}

	@Override
	public double getDoubleValue ()
	{
		if (operator == Operators.EXP)
			return multiplier * Math.exp (arguments[0]);
		else if (operator == Operators.LN)
			return multiplier * Math.log (arguments[0]);
		return multiplier * Math.pow (arguments[0], arguments[1]);
	}

	@Override
	public Value clone ()
	{
		return new ConstantValue (multiplier, operator, arguments.clone());
	}

	@Override
	public String toString () 
	{
		StringBuilder sb = new StringBuilder();
		
		if (multiplier != 1 && multiplier != -1)
		{
			sb.append (formatNumber (multiplier));
			sb.append ("x");
		}
		else if (multiplier == -1)
			sb.append ("-");

		if (operator == Operators.POW)
		{
			if (arguments[1] - (int) arguments[1] != 0)
			{
				sb.append (formatNumber (1 / arguments[1]));
				sb.append ("_rt(");
			}
		}
		else
		{
			sb.append (operator);
			sb.append ("(");
		}
		
		if (arguments[0] == PI.getDoubleValue())
			sb.append ("π");
		else if (arguments[0] == E.getDoubleValue())
			sb.append ("e");
		else if (arguments[0] == PHI.getDoubleValue())
			sb.append ("phi");
		else
			sb.append (formatNumber (arguments[0]));

		if (operator == Operators.POW && arguments[1] != 1 && arguments[1] - (int) arguments[1] == 0)
		{
			sb.append (Operators.POW);
			sb.append (formatNumber (arguments[1]));
		}
		else if (operator != Operators.POW || operator == Operators.POW && arguments[1] - (int) arguments[1] != 0)
			sb.append (")");
		
		return sb.toString();
	}
}
