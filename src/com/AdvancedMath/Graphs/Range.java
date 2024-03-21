package com.AdvancedMath.Graphs;

import com.AdvancedMath.Numbers.FloatValue;
import com.AdvancedMath.Numbers.Value;

/**
 * Class that represents a range on the x axis. All values are real values
 */
public class Range implements Cloneable
{
	public static final Range R = new Range (Double.NEGATIVE_INFINITY, false, false, Double.POSITIVE_INFINITY);
	
	private Value lower, upper;
	private boolean includeLower, includeUpper;

	/**
	 * Creates a new range from lower to upper
	 * 
	 * <p>e.g.
	 * <ul>
	 * 	<li>To create the range [0, 5] (or 0 <= x <= 5): {@code new Range (0, true, true, 5)}</li>
	 * 	<li>To create the range [0, 5) (or 0 <= x < 5): {@code new Range (0, true, false, 5)}</li>
	 * 	<li>To create the range (0, 5] (or 0 < x <= 5): {@code new Range (0, false, true, 5)}</li>
	 * 	<li>To create the range (0, 5) (or 0 < x < 5): {@code new Range (0, false, false, 5)}</li>
	 * </ul>
	 * 
	 * @param lower Lower bound of the range
	 * @param includeLower Determines if the range includes the lower bound
	 * @param includeUpper Determines if the range includes the upper bound
	 * @param upper Upper bound of the range
	 */
	public Range (Value lower, boolean includeLower, boolean includeUpper, Value upper)
	{
		this.lower = lower;
		this.includeLower = includeLower;
		this.includeUpper = includeUpper;
		this.upper = upper;
	}
	
	/**
	 * Creates a new range from {@code lower} to {@code upper}. {@code lower} and {@code upper} are converted to {@code FloatValue}
	 * 
	 * <p>e.g.
	 * <ul>
	 * 	<li>To create the range [0, 5] (or 0 <= x <= 5): {@code new Range (0, true, true, 5)}</li>
	 * 	<li>To create the range [0, 5) (or 0 <= x < 5): {@code new Range (0, true, false, 5)}</li>
	 * 	<li>To create the range (0, 5] (or 0 < x <= 5): {@code new Range (0, false, true, 5)}</li>
	 * 	<li>To create the range (0, 5) (or 0 < x < 5): {@code new Range (0, false, false, 5)}</li>
	 * </ul>
	 * 
	 * @param lower Lower bound of the range, to be converted to {@code FloatValue}
	 * @param includeLower Determines if the range includes the lower bound
	 * @param includeUpper Determines if the range includes the upper bound
	 * @param upper Upper bound of the range, to be converted to {@code FloatValue}
	 * 
	 * @see FloatValue
	 */
	public Range (double lower, boolean includeLower, boolean includeUpper, double upper)
	{
		this.lower = new FloatValue (lower);
		this.includeLower = includeLower;
		this.includeUpper = includeUpper;
		this.upper = new FloatValue (upper);
	}

	public Range (double centre, double length, boolean includeBorders)
	{
		this.lower = new FloatValue (centre - length / 2);
		this.upper = new FloatValue (centre + length / 2);
		this.includeLower = this.includeUpper = includeBorders;
	}
	
	public Range (double centre, double length)
	{
		this (centre, length, true);
	}

	public Value getLowerBound ()
	{
		return lower;
	}

	private void swapLowerUpper ()
	{
		Value tmp = lower;
		lower = upper;
		upper = tmp;
		boolean t = includeLower;
		includeLower = includeUpper;
		includeUpper = t;
	}
	
	/**
	 * Sets the lower bound of the range. If {@code v} > upper, then the range becomes [upper, v]
	 * 
	 * @param v The new lower bound, or new upper bound if {@code v} > upper
	 * @throws IllegalArgumentException if {@code v} is equal to any of the bounds
	 */
	public void setLowerBound (Value v)
	{
		if (v.equals (lower) || v.equals (upper))
			throw new IllegalArgumentException ("New bound cannot be equal to existing bounds");
		
		if (v.compare (upper) == 1)
		{
			swapLowerUpper();
			this.upper = v;
		}
		else
			this.lower = v;
	}
	
	public Value getUpperBound ()
	{
		return upper;
	}

	/**
	 * Sets the lower bound of the range. If {@code v} < lower, then the range becomes [v, lower]
	 * 
	 * @param v The new upper bound, or new lower bound if {@code v} < upper
	 * @throws IllegalArgumentException if {@code v} is equal to any of the bounds
	 */
	public void setUpperBound (Value v)
	{
		if (v.equals (lower) || v.equals (upper))
			throw new IllegalArgumentException ("New bound cannot be equal to existing bounds");
		
		if (v.compare (lower) == -1)
		{
			swapLowerUpper();;
			this.lower = v;
		}
		else
			this.upper = v;
	}
	
	public boolean isLowerIncluded ()
	{
		return includeLower;
	}
	
	public boolean isUpperIncluded ()
	{
		return includeUpper;
	}
	
	public void setIsLowerIncluded (boolean includeLower)
	{
		this.includeLower = includeLower;
	}
	
	public void setIsUpperIncluded (boolean includeUpper)
	{
		this.includeUpper = includeUpper;
	}

	/**
	 * Checks if this range is inside another range
	 * 
	 * @param r The range to test if it includes / englobes this range
	 * @return {@code true} if this range is within the bounds of {@value r}, i.e. if the bounds of this range are within those of r, or equal to them such as for both ranges those equal bounds have the same inclusivity level, {@code false} otherwise
	 */
	public boolean in (Range r)
	{
		int lower = this.lower.compare (r.lower), upper = this.upper.compare (r.upper);
		return ((lower == 1 || lower == 0 && this.includeLower && r.includeLower) && (upper == -1 || upper == 0 && this.includeUpper && r.includeUpper));
	}

	/**
	 * Checks if this range intersects with another range
	 * 
	 * @param r The other range to check if it intersects with this one
	 * @return {@code true} if any of the ranges in included in the other, or if there exists an intersection between them, {@code false} otherwise
	 * @see Range#in(Range)
	 */
	public boolean intersects (Range r)
	{
		if (this.in (r) || r.in (this))
			return true;
		
		int thisCmpR = this.lower.compare (r.lower);
		if (thisCmpR < 0)
			return this.contains (r.lower) && r.contains (this.upper);
		return r.contains (this.lower) && this.contains (r.upper);
	}

	/**
	 * Retrieves, if exists, the intersection of two ranges
	 * 
	 * @param r The other range
	 * @return {@code null} if there is no intersection, or the common range if an intersection exists
	 */
	public Range getIntersectingRange (Range r)
	{
		if (!this.intersects (r))
			return null;
		
		if (this.in (r))
			return this.clone();
		if (r.in (this))
			return r.clone();

		int thisCmpR = this.lower.compare (r.lower);
		if (thisCmpR < 0)
			return new Range (r.lower, r.includeLower, this.includeUpper, this.upper);
		return new Range (this.lower, this.includeLower, r.includeUpper, r.upper);
	}

	/**
	 * Checks if a value is contained in this range
	 * 
	 * @param v The value
	 * @return {@code true} if {@code v} is within the bounds of the range, or equal to one of the bounds such as said bound is included
	 * @see Range#isLowerIncluded()
	 * @see Range#isUpperIncluded()
	 */
	public boolean contains (Value v)
	{
		int lower = v.compare (this.lower), upper = v.compare (this.upper);
		return ((lower == 1 || lower == 0 && this.includeLower) && (upper == -1 || upper == 0 && this.includeUpper));
	}

	/**
	 * Checks if the ranges are adjacent and continuous, i.e., the upper bound of this is equal to the lower bound of r, and that at least one of those bounds are included
	 * 
	 * @param r The range to check
	 * @return {@code true} if this range ends where r starts and that that point is included in at least one of the ranges, {@code false} otherwise
	 */
	public boolean areAdjacentAndContinous (Range r)
	{
		return this.upper.equals (r.lower) && (includeUpper || r.includeLower);
	}

	/**
	 * Appends two ranges to form one range that englobes them, equivalent to union operator on two ranges that are adjacent and continuous.
	 * In other words, the first range must end where the other starts and at least one of the common bounds must be included.
	 * 
	 * @param r The range adjacent to this one in order to be appended to it
	 * @return The appended range englobing both ranges
	 * @throws IllegalArgumentException if ranges are not adjacent and continuous
	 */
	public Range append (Range r)
	{
		if (this.in (r))
			return r;
		if (this.areAdjacentAndContinous (r))
			return new Range (lower, includeLower, r.includeUpper, r.upper);
		if (r.areAdjacentAndContinous (this))
			return new Range (r.lower, r.includeLower, includeUpper, upper);
		throw new IllegalArgumentException ("Ranges must be adjacent to be appended");
	}

	/**
	 * Modifies this range by appending another one to it to form one range that englobes them, equivalent to union operator on two ranges that are adjacent and continuous.
	 * In other words, the first range must end where the other starts and at least one of the common bounds must be included.
	 * 
	 * @param r The range adjacent to this one in order to be appended to it
	 * @throws IllegalArgumentException if ranges are not adjacent and continuous
	 */
	public void appendInPlace (Range r)
	{
		if (this.in (r))
		{
			setLowerBound (r.lower);
			setUpperBound (r.upper);
			setIsLowerIncluded (r.includeLower);
			setIsUpperIncluded (r.includeUpper);
		}
		if (this.areAdjacentAndContinous (r))
		{
			setUpperBound (r.upper);
			setIsUpperIncluded (r.includeUpper);
		}
		if (r.areAdjacentAndContinous (this))
		{
			setLowerBound (r.lower);
			setIsLowerIncluded (r.includeLower);
		}
		throw new IllegalArgumentException ("Ranges must be adjacent to be appended");
	}

	/**
	 * Removes the intersection of the ranges from the first range and returns the resulting split ranges
	 * 
	 * @param r The range whose intersection will split the first one
	 * @return The list of remaining parts of the original range after splitting
	 */
	public Range[] subtract (Range r)
	{
		Range intersect = getIntersectingRange (r);
		
		if (intersect == null)
			throw new IllegalArgumentException ("Provided range is outside the bounds of this range.");

		Range lowerPart = null, upperPart = null;

		if (this.lower.compare (intersect.lower) == -1 || this.lower.compare (intersect.lower) == 0 && !intersect.includeLower)
		{
			lowerPart = new Range (this.lower, this.includeLower, intersect.includeLower, intersect.lower);
		}
		
		if (intersect.upper.compare (this.upper) == -1 || intersect.upper.compare (this.upper) == 0 && !intersect.includeUpper)
		{
			upperPart = new Range (intersect.upper, intersect.includeUpper, this.includeUpper, this.upper);
		}

		if (lowerPart != null && upperPart == null)
			return new Range[] {lowerPart};
			
		if (lowerPart == null && upperPart != null)
			return new Range[] {upperPart};
		
		return new Range[] {lowerPart, upperPart};
	}

	/**
	 * Gets the length of a range which is the distance between lower and upper range
	 * 
	 * @return The length of the range
	 */
	public double length ()
	{
		return upper.getDoubleValue() - lower.getDoubleValue();
	}

	/**
	 * Creates a range that represents the operation greater than (>), which translates to x in (v, +inf)
	 * 
	 * @param v The lower bound of the range
	 * @return The range equal to x > v (which is equivalent to x in (v, inf))
	 */
	public static Range gt (Value v)
	{
		return new Range (v, false, false, new FloatValue (Double.POSITIVE_INFINITY));
	}
	
	/**
	 * Creates a range that represents the operation greater than or equal (>=), which translates to x in [v, +inf)
	 * 
	 * @param v The lower bound of the range
	 * @return The range equal to x >] v (which is equivalent to x in [v, inf))
	 */
	public static Range gte (Value v)
	{
		return new Range (v, true, false, new FloatValue (Double.POSITIVE_INFINITY));
	}
	
	/**
	 * Creates a range that represents the operation less than (<), which translates to x in (-inf, v)
	 * 
	 * @param v The upper bound of the range
	 * @return The range equal to x < v (which is equivalent to x in (-inf, v))
	 */
	public static Range lt (Value v)
	{
		return new Range (new FloatValue (Double.NEGATIVE_INFINITY), false, false, v);
	}
	
	/**
	 * Creates a range that represents the operation less than (<=), which translates to x in (-inf, v]
	 * 
	 * @param v The upper bound of the range
	 * @return The range equal to x <= v (which is equivalent to x in (-inf, v])
	 */
	public static Range lte (Value v)
	{
		return new Range (new FloatValue (Double.NEGATIVE_INFINITY), true, false, v);
	}

	/**
	 * Gets the String representation of this range in the form of an interval
	 * <p> The format is as following: "in [|(lower, upper]|)"
	 */
	@Override
	public String toString ()
	{
		return "in " + (includeLower ? "[" : "(") + lower.toString() + ", " + upper.toString() + (includeUpper ? "]" : ")");
	}

	/**
	 * Gets the String representation of this range using the symbols <, <=, >=, >
	 * 
	 * @param variable The name of the variable that belongs to this range
	 * @return The string representing this range using <, <=, >=, >
	 */
	public String toStringUsingSigns (String variable)
	{
		return lower.toString() + (includeLower ? " <= " : " < ") + variable + (includeUpper ? " <= " : " < ") + upper.toString();
	}

	@Override
	public Range clone ()
	{
		return new Range (lower.clone(), includeLower, includeUpper, upper.clone());
	}
	
	@Override
	public boolean equals (Object o)
	{
		if (this == o)
			return true;

		if (o instanceof Range r)
			return this.lower == r.lower && this.includeLower == r.includeLower && this.upper == r.upper && this.includeUpper == r.includeUpper;
		
		return false;
	}
}

