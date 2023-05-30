package com.AdvancedMath.Graphs;

import com.AdvancedMath.Numbers.Value;

public interface Graphable
{
	/**
	 * Gets the distance between two {@code Graphable}s
	 * 
	 * @param g The other {@code Graphable}
	 * @return The distance between the two {@code Graphable}s
	 */
	public Double distance (Graphable g);
	
	/**
	 * Gets the distance of that point from the origin
	 * 
	 * @return distance to the origin (0,0)
	 */
	public Double length ();

	/**
	 * Gets the angle made between the x axis and the segment created by joining the origin O (0, 0) and the {@code Graphable}. The positive direction is set as anti-clockwise
	 * 
	 * @return The argument of the {@code Graphable}
	 */
	public Value argument ();
}
