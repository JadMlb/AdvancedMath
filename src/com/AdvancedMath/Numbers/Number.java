package com.AdvancedMath.Numbers;

import java.util.ArrayList;
import java.util.HashMap;

import com.AdvancedMath.EqTree.Node;
import com.AdvancedMath.Graphs.Point;
import com.AdvancedMath.EqTree.NumberNode;
import com.AdvancedMath.EqTree.OperatorNode;
import com.AdvancedMath.EqTree.VariableNode;

/**
 * Class that represents any number. It uses a complex representation of a number (a + bi) 
 */
public class Number extends Point implements Cloneable
{
	public static Number ZERO = Number.real (0.0), 
						 ONE = Number.real (1.0),
						 I = Number.imaginary (1.0),
						 PI = Number.real (ConstantValue.PI),
						 E = Number.real (ConstantValue.E),
						 PHI = Number.real (ConstantValue.PHI);
	
	/**
	 * Creates a complex {@code Number} with values x and y as its components (x + yi)
	 * 
	 * @param x The real part of the number
	 * @param y The imaginary part of the number
	 * @see ConstantValue
	 * @see FloatValue
	 * @see FractionValue
	 */
	public Number (Value x, Value y)
	{
		super (x, y);
	}
	
	/**
	 * Creates a complex {@code Number} with values x and y as its components (x + yi)
	 * 
	 * @param x The real part of the number
	 * @param y The imaginary part of the number
	 */
	public Number (Double x, Double y)
	{
		super (new FloatValue (x), new FloatValue (y));
	}

	/**
	 * Creates a real number from {@code v}. Real numbers are complex numbers with their imaginary part set to 0
	 * 
	 * @param v The value of the real number
	 * @return A real {@code Number} equals to v + 0*i
	 */
	public static Number real (Value v)
	{
		return new Number (v, FractionValue.ZERO);
	}
	
	/**
	 * Creates a real number from {@code x}. Real numbers are complex numbers with their imaginary part set to 0
	 * 
	 * @param x The value of the real number
	 * @return A real {@code Number} equals to x + 0*i
	 */
	public static Number real (Double x)
	{
		return new Number (x, 0.0);
	}

	/**
	 * Creates an imaginary number from {@code v}. Imaginary numbers are complex numbers with their real part set to 0
	 * 
	 * @param v The value of the imaginary number
	 * @return An imaginary {@code Number} equals to 0 + v*i
	 */
	public static Number imaginary (Value v)
	{
		return new Number (FractionValue.ZERO, v);
	}
	
	/**
	 * Creates an imaginary number from {@code x}. Imaginary numbers are complex numbers with their real part set to 0
	 * 
	 * @param x The value of the imaginary number
	 * @return An imaginary {@code Number} equals to 0 + x*i
	 */
	public static Number imaginary (Double x)
	{
		return new Number (0.0, x);
	}

	/**
	 * Creates a complex {@code Number} a+bi from its polar form (r*e^(theta * i))
	 * 
	 * @param r The length of the number, i.e., the distance from the origin O(0, 0)
	 * @param theta The argument of the number, i.e., the angle the segment made by joining the origin with this point, with the x axis in the anti-clockwise direction
	 * @return The cartesian form (a+bi) of the polar form of this number
	 */
	public static Number fromPolar (Value r, Value theta)
	{
		Value piMulti = theta.divide (new FloatValue (Math.PI));
		FractionValue piMultiFrac = null;

		if (piMulti instanceof FloatValue fl)
			piMultiFrac = fl.getFraction();
		else if (piMulti instanceof ConstantValue c)
			piMultiFrac = new FloatValue(c.getDoubleValue()).getFraction();
		else
			piMultiFrac = (FractionValue) piMulti;
		
		// reduce angle to [-π, π]
		FractionValue pi = new FractionValue (1, 1), twoPi = new FractionValue (2, 1);
		while (piMultiFrac.compareTo (new FractionValue (-1, 1)) == -1)
		{
			piMultiFrac = (FractionValue) piMultiFrac.add (twoPi);
		}
		
		while (piMultiFrac.compareTo (pi) == 1)
		{
			piMultiFrac = (FractionValue) piMultiFrac.subtract (twoPi);
		}
		
		FractionValue 	zero = new FractionValue (0, 1), 
						piOver2 = new FractionValue (1, 2);
		
		int quad = 0;
		if (piMultiFrac.compareTo (zero) == -1)
			// less than -π/2
			if (piMultiFrac.compareTo (piOver2.negateCopy()) == -1)
				quad = 2;
			else
				quad = 3;
		else
			if (piMultiFrac.compareTo (piOver2) == 1)
				quad = 1;
			else
				quad = 0;
		
		// reduce angle to [0, π/2]
		switch (quad)
		{
			case 1: piMultiFrac = (FractionValue) piMultiFrac.subtract (piOver2); break;
			case 2: piMultiFrac = (FractionValue) piMultiFrac.add (pi); break;
			case 3: piMultiFrac = (FractionValue) piMultiFrac.add (piOver2); break;
		}

		Value[] components = new Value [2];

		ConstantValue 	sqrt3Over2 = ConstantValue.pow (r.multiply(piOver2).getDoubleValue(), 3, 0.5),
						sqrt2Over2 = ConstantValue.pow (r.multiply(piOver2).getDoubleValue(), 2, 0.5);

		if (piMultiFrac.equals (zero))
		{
			components[0] = r;
			components[1] = zero;
		}
		else if (piMultiFrac.equals (new FractionValue (1, 6)))
		{
			components[0] = sqrt3Over2;
			components[1] = piOver2.multiply (r);
		}
		else if (piMultiFrac.equals (new FractionValue (1, 4)))
		{
			components[0] = sqrt2Over2;
			components[1] = sqrt2Over2;
		}
		else if (piMultiFrac.equals (new FractionValue (1, 3)))
		{
			components[0] = piOver2.multiply (r);
			components[1] = sqrt3Over2;
		}
		else if (piMultiFrac.equals (piOver2))
		{
			components[0] = zero;
			components[1] = r;
		}
		else
		{
			components[0] = r.multiply (new FloatValue (Math.cos (theta.getDoubleValue())));
			components[1] = r.multiply (new FloatValue (Math.sin (theta.getDoubleValue())));
		}

		switch (quad)
		{
			case 1:
				Value temp = components[0];
				components[0] = components[1];
				components[1] = temp;
				components[0].negate(); 
				break;
			case 2: components[0] = components[0].negateCopy(); components[1] = components[1].negateCopy(); break;
			case 3: 
				temp = components[0];
				components[0] = components[1];
				components[1] = temp;
				components[1].negate(); 
				break;
		}

		return new Number (components[0], components[1]);
	}

	/**
	 * Gets the conjugate of this number by setting the imaginary part to its opposite
	 * 
	 * @return a - bi
	 */
	public Number conjugate ()
	{
		return new Number (getX(), getY().negateCopy());
	}

	/**
	 * Sets all components to their opposite
	 * 
	 * @return -a - bi
	 */
	public Number negate ()
	{
		return new Number (getX().negateCopy(), getY().negateCopy());
	}

	/**
	 * Adds two numbers
	 * 
	 * @param c
	 * @return The sum of this and c
	 */
	public Number add (Number c)
	{
		return new Number (this.getX().add (c.getX()), this.getY().add(c.getY()));
	}
	
	/**
	 * Subtarcts two numbers
	 * 
	 * @param c
	 * @return The difference of this and c
	 */
	public Number subtract (Number c)
	{
		return new Number (this.getX().subtract (c.getX()), this.getY().subtract (c.getY()));
	}

	/**
	 * Multiplies two numbers
	 * 
	 * @param c
	 * @return The product of this and c
	 */
	public Number multiply (Number c)
	{
		return new Number (this.getX().multiply(c.getX()).subtract (this.getY().multiply (c.getY())), this.getX().multiply(c.getY()).add (this.getY().multiply (c.getX())));
	}
	
	/**
	 * Divides a {@code Number} by a real, so both components are divided by this real
	 * 
	 * @param c
	 * @return The division result of this and d
	 * @throws IllegalArgumentException if {@code d} is 0
	 */
	public Number divide (Double d)
	{
		FloatValue infty = new FloatValue (Double.POSITIVE_INFINITY);
		boolean isPositiveX = this.getX().compareTo (FractionValue.ZERO) >= 0;
		boolean isPositiveY = this.getY().compareTo (FractionValue.ZERO) >= 0;
		if (d == 0) 
		{
			if (this.equals (Number.ZERO))
				throw new IllegalArgumentException ("Math error: 0/0 is undefined");
			else
				return new Number (isPositiveX ? infty : infty.negateCopy(), isPositiveY ? infty : infty.negateCopy());
		}
		else if (Double.isInfinite (d))
		{
			if (Double.isInfinite (this.getX().getDoubleValue()) || Double.isInfinite (this.getY().getDoubleValue()))
				throw new IllegalArgumentException ("Math error: ∞/∞ is undefined");
			return Number.ZERO;
		}
		return new Number (getX().divide (new FloatValue (d)), getY().divide (new FloatValue (d)));
	}
	
	/**
	 * Divides two numbers
	 * 
	 * @param c
	 * @return The division result of this and c
	 * @throws ArithmeticException if both this number and c are 0
	 */
	public Number divide (Number c)
	{
		FloatValue infty = new FloatValue (Double.POSITIVE_INFINITY);
		boolean isPositiveX = this.getX().compareTo (FractionValue.ZERO) >= 0;
		boolean isPositiveY = this.getY().compareTo (FractionValue.ZERO) >= 0;
		if (c.equals (Number.ZERO))
			if (this.equals (Number.ZERO))
				throw new ArithmeticException ("Math error: 0/0 is undefined");
			else
				return new Number (isPositiveX ? infty : infty.negateCopy(), isPositiveY ? infty : infty.negateCopy());
		else if (Double.isInfinite (c.getX().getDoubleValue()) || Double.isInfinite (c.getY().getDoubleValue()))
		{
			if (Double.isInfinite (this.getX().getDoubleValue()) || Double.isInfinite (this.getY().getDoubleValue()))
				throw new IllegalArgumentException ("Math error: ∞/∞ is undefined");
			return Number.ZERO;
		}
		
		return this.multiply(c.conjugate()).divide (Math.pow (c.length(), 2));
	}

	/**
	 * Raises this number to the power of n
	 * 
	 * <p>If {@code n} is negative, the result is equal to calculating 1 / (this^abs(n))
	 * 
	 * @param n The power that this number is raised to
	 * @return This number raised to the power of n
	 */
	public Number pow (int n)
	{
		return Number.fromPolar (new FloatValue (Math.pow (length(), n)), argument().multiply (new FractionValue (n, 1)));
	}

	/**
	 * Calculates this number raised to the power of another number
	 * 
	 * <ul>
	 * 	<li>If {@code n} is a real number and is decimal that is not an integer, the result is equal to finding the 
	 * 	nth root of this number raised to a certain power m calculted by using the {@code FloatValue.getFraction()} method</li>
	 * <li>Else returns the value of this number raised to the power of {@code n}</li>
	 * 
	 * @param n The power this number is raised to
	 * @return this ^ n
	 * @see Number#pow(int)
	 * @see Number#nthRoot(int)
	 * @see FloatValue#getFraction()
	 */
	public Number pow (Number n)
	{
		if (n.isPureReal())
			if (n.getX().getDoubleValue() - (int) n.getX().getDoubleValue() == 0)
				return pow ((int) n.getX().getDoubleValue());
			else
			{
				FractionValue pow = new FloatValue(n.getX().getDoubleValue()).getFraction();
				return pow(pow.getNumerator()).nthRoot(pow.getDenomenator()).get (0);
			}
		else
			return Number.fromPolar
			(
				ConstantValue.exp (Number.real(length()).pow (Number.real(n.getX())).getX().getDoubleValue(), - n.getY().getDoubleValue() * argument().getDoubleValue()),
				n.getX().multiply(argument()).add (ConstantValue.ln (n.getY().getDoubleValue(), length()))
			);
	}

	/**
	 * Returns all the nth roots of this number using complex numbers
	 * 
	 * <p> If {@code n} is negative, the values returned correspond to calculating 1 / (nthRoot(this))
	 * 
	 * @param n The order of the root
	 * @return {@code ArrayList} containing the nth roots of the number
	 */
	public ArrayList<Number> nthRoot (int n)
	{
		ArrayList<Number> roots = new ArrayList<>();
		boolean isNegative = false;

		if (n < 0)
		{
			isNegative = true;
			n = - n;
		}
		
		if (n == 1)
		{
			if (isNegative)
				roots.add (Number.ONE.divide (this));
			else
				roots.add (clone());
			return roots;
		}

		Value r = null;
		Double length = length();

		if (length == 0)
			roots.add (Number.ZERO);
		else
		{
			double x = Math.exp (1.0 / n * Math.log (length));
			if (x - (int) x != 0)
				r = ConstantValue.pow (1, length, 0.5);
			else
				r = new FloatValue (x);
			for (int i = 0; i < n; i++)
			{
				Number root = Number.fromPolar (r.clone(), ConstantValue.PI.multiply(new FloatValue (2.0)).multiply(new FractionValue (i, 1)).add(argument()).divide (new FractionValue (n, 1)));
				if (isNegative)
					root = Number.ONE.divide (root);

				roots.add (root);
			}
		}

		return roots;
	}

	/**
	 * Calculates the factorial of the closest long
	 * 
	 * @return factorial of this number
	 * 
	 * @throws IllegalArgumentException if this number is complex or its real component is negative
	 */
	public Number factorial ()
	{
		if (!isPureReal() || getX().compareTo (FractionValue.ZERO) == -1)
			throw new IllegalArgumentException ("Factorial for imaginary numbers and negative numbers is not defined");

		long fact = 1, val = Math.round (getX().getDoubleValue());
		for (long i = 2; i <= val; i++)
			fact *= i;

		return Number.real ((double) fact);
	}

	/**
	 * Gets the natural log with this number as its argument
	 * @return ln (this)
	 */
	public Number ln ()
	{
		return new Number (ConstantValue.ln (1, this.length()), this.argument());
	}

	/**
	 * Gets the cosine with this number as its argument
	 * @return cos (this)
	 */
	public Number cos ()
	{
		return new Number (Math.cos (getX().getDoubleValue()) * Math.cosh (getY().getDoubleValue()), Math.sin (getX().getDoubleValue()) * Math.sinh (getY().getDoubleValue()));
	}
	
	/**
	 * Gets the sine with this number as its argument
	 * @return sin (this)
	 */
	public Number sin ()
	{
		return new Number (Math.sin (getX().getDoubleValue()) * Math.cosh (getY().getDoubleValue()), - Math.cos (getX().getDoubleValue()) * Math.sinh (getY().getDoubleValue()));
	}

	/**
	 * Gets the hyperbolic cosine with this number as its argument
	 * @return cosh (this)
	 */
	public Number cosh ()
	{
		return new Number (Math.cosh (getX().getDoubleValue()) * Math.cos (getY().getDoubleValue()), Math.sinh (getX().getDoubleValue()) * Math.sin (getY().getDoubleValue()));
	}
	
	/**
	 * Gets the hyperbolic sine with this number as its argument
	 * @return sinh (this)
	 */
	public Number sinh ()
	{
		return new Number (Math.sinh (getX().getDoubleValue()) * Math.cos (getY().getDoubleValue()), Math.cosh (getX().getDoubleValue()) * Math.sin (getY().getDoubleValue()));
	}
	
	/**
	 * Gets the inverse cosine with this number as its argument
	 * @return acos (this), i.e., cos-1 (this)
	 */
	public Number acos ()
	{
		return Number.PI.multiply (Number.real (new FractionValue (1, 2))) // 1/2 * π 
			.add
			(
				Number.I.multiply
				(
					this.multiply (Number.I) // iz
					.add
					(
						// Number.ONE.subtract (this.pow (Number.real (2.0))) // 1 - z^2
						// .pow (Number.real (0.5)) // ^(1/2)
						Number.ONE.subtract (this.pow (2)) // 1 - z^2
						.nthRoot(2).get(0) // ^(1/2)
					)
					.ln()
				)
			);
	}
	
	/**
	 * Gets the inverse sine with this number as its argument
	 * @return asin (this), i.e., sin-1 (this)
	 */
	public Number asin ()
	{
		return Number.I.conjugate() // -i
			.multiply 
			(
				this.multiply (Number.I) // iz
				.add
				(
					// Number.ONE.subtract (this.pow (Number.real (2.0))) // 1 - z^2
					// .pow (Number.real (0.5)) // ^(1/2)
					Number.ONE.subtract (this.pow (2)) // 1 - z^2
					.nthRoot(2).get (0) // ^(1/2)
				)
				.ln()
			);
	}

	/**
	 * Gets the inverse hyperbolic cosine with this number as its argument
	 * @return acosh (this), i.e., cosh-1 (this)
	 */
	public Number acosh ()
	{
		return new Number (Math.cosh (getX().getDoubleValue()) * Math.cos (getY().getDoubleValue()), Math.sinh (getX().getDoubleValue()) * Math.sin (getY().getDoubleValue()));
	}
	
	/**
	 * Gets the inverse hyperbolic sine with this number as its argument
	 * @return asinh (this), i.e., sinh-1 (this)
	 */
	public Number asinh ()
	{
		return new Number (Math.sinh (getX().getDoubleValue()) * Math.cos (getY().getDoubleValue()), Math.cosh (getX().getDoubleValue()) * Math.sin (getY().getDoubleValue()));
	}

	/**
	 * Checks if two numbers are equal
	 */
	@Override
	public boolean equals (Object obj)
	{
		if (!(obj instanceof Number) && !(obj instanceof Value))
			return false;

		if (this == obj)
			return true;

		if (obj instanceof Value v)
			if (this.isPureImaginary())
				return false;
			else if (this.isPureReal())
				return this.getX().equals (v);
			else
				return false;

		Number n = (Number) obj;

		if (this.getX().equals (n.getX()) && this.getY().equals (n.getY()))
			return true;
		return false;
	}

	/**
	 * Checks if the number is a pure real, i.e., its imaginary part is 0
	 * 
	 * @return {@code true} if the imaginary part is 0, {@code false} otherwise
	 */
	public boolean isPureReal ()
	{
		return getY().getDoubleValue() == 0;
	}
	
	/**
	 * Checks if the number is a pure imaginary, i.e., its real part is 0, and the whole number is not 0
	 * 
	 * @return {@code true} if the real part is 0 and the imaginary part is not 0, {@code false} otherwise
	 */
	public boolean isPureImaginary ()
	{
		return (getX().getDoubleValue() == 0 && getY().getDoubleValue() != 0);
	}

	/**
	 * Checks if the number is valid, i.e., none of its components is infinite nor {@code NaN}
	 * 
	 * @return {@code true} if all components are finite and not {@code NaN}, {@code false} otherwise
	 */
	public boolean isValid ()
	{
		return (!Double.isNaN (getX().getDoubleValue()) && !Double.isNaN (getY().getDoubleValue()) && !Double.isInfinite (getX().getDoubleValue()) && !Double.isInfinite (getY().getDoubleValue()));
	}

	/**
	 * Gets the value from a {@code Node} if it represents a {@code Number}
	 * 
	 * @param eq The {@code Node} to get its value
	 * @param variables The mapping between all the potential variables in the tree to a value
	 * @return The numerical value of the tree
	 * @throws IllegalArgumentException if {@code variables} is null and there exist variables in the tree, or if no mapping between a variable in the tree to a {@code Number} is present in {@code variables}
	 */
	public static Number valueOf (Node eq, HashMap<String, Number> variables)
	{
		Number leftRes = null, rightRes = null;

		if (eq.getLeft() != null)
			leftRes = valueOf (eq.getLeft(), variables);

		if (eq.getRight() != null)
			rightRes = valueOf (eq.getRight(), variables);

		if (eq instanceof VariableNode v)
		{
			if (variables == null || variables.get (v.getName()) == null)
				throw new IllegalArgumentException ("The provided mapping of the variables to values is incomplete");
			
			return v.resolve (variables.get (v.getName()));
		}
		// else if (eq instanceof MatrixNode)
		// 	throw new IllegalArgumentException ("The provided node contains a matrix. Use appropriate method");
		else if (eq instanceof NumberNode n)
			return n.getValue();
		else if (eq instanceof OperatorNode op)
		{
			switch (op.getOperator())
			{
				case ADD: return leftRes.add (rightRes);
				case SUB: return leftRes.subtract (rightRes);
				case MUL: return leftRes.multiply (rightRes);
				case DIV: return leftRes.divide (rightRes);
				case POW: 
				if (!rightRes.isPureReal())
					return null;
				else
				{
					FractionValue power = new FloatValue(rightRes.getX().getDoubleValue()).getFraction();
					if (power.getDenomenator() == 1)
						return leftRes.pow ((int) rightRes.getX().getDoubleValue());
					else
						return leftRes.pow(power.getNumerator()).nthRoot(power.getDenomenator()).get (0);
				}
				case FAC: return leftRes.factorial();
				case LN: return rightRes.ln();
				case EXP: return Number.fromPolar (ConstantValue.exp (1, rightRes.getX().getDoubleValue()), rightRes.getY());
				case ABS: return Number.real (leftRes.length());
				case SIN: return rightRes.sin();
				case COS: return rightRes.cos();
				case TAN: return rightRes.sin().divide (rightRes.cos());
				case ASIN: return rightRes.asin();
				case ACOS: return rightRes.acos();
				// case ATAN: return rightRes.divide(rightRes.pow(Number.real (2.0)).add (Number.ONE).pow (Number.real (new FractionValue (1, 2)))).asin();
				case ATAN: return rightRes.divide(rightRes.pow(2).add (Number.ONE).nthRoot(2).get (0)).asin();
				case SINH: return rightRes.sinh();
				case COSH: return rightRes.cosh();
				case TANH: return rightRes.sinh().divide (rightRes.cosh());
				case ASH: return rightRes.asinh();
				case ACH: return rightRes.acosh();
				case ATH: return Number.real (0.5).multiply(Number.ONE.add(rightRes).divide(Number.ONE.subtract (rightRes)).ln());
				default: return null;
			}
		}

		return null;
	}

	@Override
	public Number clone ()
	{
		return new Number (getX().clone(), getY().clone());
	}

	@Override
	public String toString () 
	{
		if (Double.isInfinite (getX().getDoubleValue()) || Double.isInfinite (getY().getDoubleValue()))
			return String.valueOf (getX().getDoubleValue());
		if (!isValid())
			return "NaN";

		String s = "", x = getX().toString();
		
		if (getX().equals (FractionValue.ZERO))
			if (getY().equals (FractionValue.ZERO))
				s = FractionValue.ZERO.toString();
			else if (getY().equals (FractionValue.ONE))
				s = "i";
			else if (getY().equals (FractionValue.ONE.negateCopy()))
				s = "-i";
			else
				s = getX().toString() + "i";
		else
			if (getY().equals (FractionValue.ZERO))
				s = x;
			else if (getY().equals (FractionValue.ONE))
				s = x + " + i";
			else if (getY().equals (FractionValue.ONE.negateCopy()))
				s = x + " - i";
			else
				s = x + (getY().compareTo (FractionValue.ZERO) == 1 ? " + " : " - ") + getY().toString().replaceFirst ("-", "") + "i";
		
		return s;
	}

	/**
	 * Gets the {@code String} of this number in the polar form (re^(theta*i))
	 * 
	 * @return The polar form of this {@code Number}
	 */
	public String toStringPolar ()
	{
		double length = length();
		Value arg = argument();

		StringBuilder b = new StringBuilder();
		
		if (length != 1 && length != -1)
		{
			b.append (length);
			b.append ("*");
		}
		else if (length == -1)
			b.append ("-");
		
		b.append ("e^(i");
		
		if (!arg.equals (FractionValue.ONE))
		{
			b.append ("*");
			b.append (arg);
		}

		b.append (")");

		return b.toString();
	}
}
