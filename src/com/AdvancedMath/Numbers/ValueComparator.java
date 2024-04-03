package com.AdvancedMath.Numbers;

import java.util.Comparator;

public class ValueComparator implements Comparator<Value>
{
	@Override
	public int compare (Value v1, Value v2)
	{
		return v1.compareTo (v2);
	}	
}
