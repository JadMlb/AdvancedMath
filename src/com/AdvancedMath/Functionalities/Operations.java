package com.AdvancedMath.Functionalities;

import com.AdvancedMath.Numbers.ConstantValue;
import com.AdvancedMath.Numbers.FractionValue;
import com.AdvancedMath.Numbers.Matrix;
import com.AdvancedMath.Numbers.Number;

/**
 * Class that defines static functions that perform general operations, and do not fit in specific class
 */
public class Operations
{
	/**
	 * Get the greatest common divisor (GCD) of 2 integers a and b using Euclid's algorithm
	 * 
	 * @param a
	 * @param b
	 * @return GCD of |a| and |b|
	 */
	public static int gcd (int a, int b)
	{
		if (b == 0)
			return a;

		if (a < 0)
			a = Math.abs (a);
		
		if (b < 0)
			b = Math.abs (b);
		
		return gcd (b, a % b);
	}
	
	/**
	 * Solves a quadratic equation of the form ax^2 + bx + c = 0
	 * 
	 * @param input {@code Matrix} as a row or column vector containing the coefficients a, b and c. Takes the first 3 elements of the provided input
	 * @return A {@code Matrix} as a row vector with the solutions to that equation
	 * @throws IllegalArgumentException if the input is {@code null}, a matrix (not a vector), or if the number of elements is less than 3
	 * @see Matrix
	 */
	public static Matrix solveQuad (Matrix input)
	{
		if (input == null)
			throw new IllegalArgumentException ("The input matrix cannot be null");

		if (input.isColumnVector())
			input = input.transpose();

		if (!input.isColumnVector() && !input.isRowVector())
			throw new IllegalArgumentException ("The provided input is a matrix instead of a vector");
		
		if (input.getColCount() < 3)
			throw new IllegalArgumentException ("The input matrix must contain at least 3 elements");
		
		Number two = Number.real (2.0),
				half = Number.real (new FractionValue (1, 2)),
				delta = input.getValueAt(0, 1).pow(two).subtract (input.getValueAt(0, 0).multiply (input.getValueAt(0, 2).multiply (Number.real (4.0))));

		Matrix sol = new Matrix (1, 2);
		sol.setValueAt (0, 0, Number.ZERO.subtract(input.getValueAt (0, 1)).add(delta.pow (half)).divide(input.getValueAt(0, 0).multiply (two)));
		sol.setValueAt (0, 1, Number.ZERO.subtract(input.getValueAt (0, 1)).subtract(delta.pow (half)).divide (input.getValueAt(0, 0).multiply (two)));

		return sol;
	}
	
	/**
	 * Solves a quadratic equation of the form ax^3 + bx^2 + cx + d = 0 by using the formulae of cubic roots
	 * 
	 * 
	 * @param input {@code Matrix} as a row or column vector containing the coefficients a, b, c and d. Takes the first 4 elements of the provided input
	 * @return A {@code Matrix} as a row vector with the solutions to that equation
	 * @throws IllegalArgumentException if the input is {@code null}, a matrix (not a vector), or if the number of elements is less than 4
	 * @see Matrix
	 * @see https://mathworld.wolfram.com/CubicFormula.html
	 */
	public static Matrix solveCubic (Matrix input)
	{
		if (input == null)
			throw new IllegalArgumentException ("The input matrix cannot be null");

		if (input.isColumnVector())
			input = input.transpose();

		if (!input.isColumnVector() && !input.isRowVector())
			throw new IllegalArgumentException ("The provided input is a matrix instead of a vector");
		
		if (input.getColCount() < 4)
			throw new IllegalArgumentException ("The input matrix must contain at least 4 elements");
		
		// transform eq to x^3 + fx^2 + gx + h = 0
		input = input.multiply (Number.ONE.divide (input.getValueAt (0, 0)));
		Number 	negativeThirdF = Number.real(new FractionValue (-1, 3)).multiply (input.getValueAt (0, 1)),
				half = Number.real (new FractionValue (1, 2)),
				two = Number.real (2.0),
				three = Number.real (3.0),
				q = three.multiply(input.getValueAt(0, 2)).subtract(input.getValueAt(0, 1).pow(two)).divide (9.0),
				r = Number.real(9.0).multiply(input.getValueAt(0, 1)).multiply(input.getValueAt(0, 2)).subtract(Number.real(27.0).multiply(input.getValueAt(0, 3))).subtract(two.multiply(input.getValueAt(0, 1).pow(three))).divide (54.0),
				d = q.pow(three).add (r.pow (two)),
				s = r.add(d.pow (half)).pow (Number.ONE.divide (3.0)),
				t = s.conjugate(),
				a = s.subtract (t),
				b = s.add (t),
				halfSqrt3iA = Number.imaginary(ConstantValue.pow (0.5, 3, 0.5)).multiply(a);
		
		return Matrix.rowVector 
		(
			new Number[]
			{
				negativeThirdF.add (b),
				negativeThirdF.subtract(half.multiply (b)).add (halfSqrt3iA),
				negativeThirdF.subtract(half.multiply (b)).subtract (halfSqrt3iA)
			}
		);
	}
}
