package com.AdvancedMath.Graphs;

import com.AdvancedMath.Numbers.ConstantValue;
import com.AdvancedMath.Numbers.FloatValue;
import com.AdvancedMath.Numbers.FractionValue;
import com.AdvancedMath.Numbers.Value;

/**
 * A class that represents a point on a graph
 */
public class Point implements Graphable
{
	private Value x, y;

	/**
	 * Constructs a new instance of {@code Point}. If any of the arguments is null, zero will be taken
	 * 
	 * @param x The x coordinate of the point
	 * @param y The y coordinate of the point
	 */
	public Point (Value x, Value y) 
	{
		if (x == null)
			x = FractionValue.ZERO;

		if (y == null)
			y = FractionValue.ZERO;

		this.x = x;
		this.y = y;
	}

	public Value getX () 
	{
		return this.x;
	}
	
	public void setX (Value x) 
	{
		if (x != null)
			this.x = x;
	}

	public Value getY () 
	{
		return this.y;
	}
	
	public void setY (Value y) 
	{
		if (y != null)
			this.y = y;
	}

	@Override
	public Double distance (Graphable g)
	{
		Point p = (Point) g;
		return Math.sqrt (Math.pow (this.x.subtract(p.getX()).getDoubleValue(), 2) + Math.pow (this.y.subtract(p.getY()).getDoubleValue(), 2));
	}

	@Override
	public Double length ()
	{
		return Math.sqrt (this.x.pow(new FractionValue (2, 1)).add(this.y.pow (new FractionValue (2, 1))).getDoubleValue());
	}

	@Override
	public Value argument ()
	{
		FloatValue length = new FloatValue (length());
		Value[] comps = {x.divide (length), y.divide (length)};

		int quad = 0;

		if (comps[0].compareTo (FractionValue.ZERO) == 1)
			if (comps[1].compareTo (FractionValue.ZERO) == 1)
				quad = 0;
			else
			{
				quad = 3;
				comps[1].negate();
				
				Value temp = comps[0];
				comps[0] = comps[1];
				comps[1] = temp;
			}
		else
			if (comps[1].compareTo (FractionValue.ZERO) == 1)
			{
				quad = 1;
				comps[0].negate();
				
				Value temp = comps[0];
				comps[0] = comps[1];
				comps[1] = temp;
			}
			else
			{
				quad = 2;
				comps[0].negate();
				comps[1].negate();
			}
		
		ConstantValue 	sqrt3Over2 = ConstantValue.pow (0.5, 3, 0.5),
						sqrt2Over2 = ConstantValue.pow (0.5, 2, 0.5);
		Value arg = null;
		
		if (comps[0].equals (FractionValue.ONE))
			arg = new ConstantValue (0);
		else if (comps[0].equals (sqrt3Over2))
			arg = ConstantValue.pow (1.0 / 6, Math.PI, 1);
		else if (comps[0].equals (sqrt2Over2))
			arg = ConstantValue.pow (0.25, Math.PI, 1);
		else if (comps[0].equals (new FractionValue (1, 2)))
			arg = ConstantValue.pow (1.0 / 3, Math.PI, 1);
		else if (comps[0].equals (FractionValue.ZERO))
			arg = ConstantValue.pow (0.5, Math.PI, 1);
		else
			return new ConstantValue (Math.atan2 (y.getDoubleValue(), x.getDoubleValue()));

		switch (quad)
		{
			case 1: arg = arg.add (ConstantValue.pow (0.5, Math.PI, 1)); break;
			case 2: arg = arg.add (ConstantValue.PI); break;
			case 3: arg = arg.subtract (ConstantValue.pow (0.5, Math.PI, 1)); break;
		}

		return arg;
	}
}