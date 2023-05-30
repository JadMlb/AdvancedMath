package com.AdvancedMath.Functionalities;

/**
 * Enumeration for supported arithmetic operations as well as mathematical functions
 */
public enum Operators
{
	OPR ("(", -1, 0, null), 	// Open Parentheses (
	CPR (")", -1, 0, null), 	// Close Parentheses )
	// PER ("%", 0, 1, null), 		// percent
	EQU ("=", 0, 2, null), 		// Equals =
	ADD ("+", 1, 2, null), 		// plus +
	SUB ("-", 1, 2, null), 		// minus -
	MUL ("*", 2, 2, null), 		// times *
	DIV ("/", 2, 2, null), 		// divide /
	POW ("^", 3, 2, null), 		// power ^
	FAC ("!", 4, 1, null), 		// x!
	LN ("ln", 5, 1, "EXP"), 	// ln(x)
	EXP ("e^", 5, 1, "LN"),	 	// e^(x)
	ABS ("abs", 5, 1, null), 	// abs(x) <=> |x|
	SIN ("sin", 5, 1, "ASIN"),
	COS ("cos", 5, 1, "ACOS"),
	TAN ("tan", 5, 1, "ATAN"),
	ASIN ("asin", 5, 1, "SIN"),
	ACOS ("acos", 5, 1, "COS"),
	ATAN ("atan", 5, 1, "TAN"),
	SINH ("sinh", 5, 1, "ASH"),
	COSH ("cosh", 5, 1, "ACH"),
	TANH ("tanh", 5, 1, "ATH"),
	ASH ("asinh", 5, 1, "SINH"),// inverse hyperbolic sin
	ACH ("acosh", 5, 1, "COSH"),// inverse hyperbolic cos
	ATH ("atanh", 5, 1, "TANH"),// inverse hyperbolic tan
	// DDX ("d_dx[]", -1, 1),		// derive
	// INT ("∫[]{}dx", -1, 2),		// integrate
	// LIM ("lim{}[]", -1, 2),		// limit
	// NMT (null, -1, 0), 			// insert new matrix
	// DET ("det", -1, 1), 		// matrix determinant
	// ROM ("T", -1, 1), 			// rotate matrix [a b c] => [a \n b \n c]
	// INV ("inv", -1, 1),			// inverse matrix
	// NVC (null, -1, 0), 			// new vector
	// DOT ("·", 1, 2, null), 			// vector dot product (opt+shift+9)
	// CRX ("x", 1, 2, null)			// vector cross product
	;

	private String name, fnInverse;
	private int pri, nbParam;

	private Operators (String name, int pri, int nbParam, String fnInverse)
	{
		this.name = name;
		this.pri = pri;
		this.nbParam = nbParam;
		this.fnInverse = fnInverse;
	}

	/**
	 * Gets the priority of this operator
	 * 
	 * @return The priority of the operator
	 */
	public int pri ()
	{
		return this.pri;
	}

	/**
	 * Gets the number of parameters that this operator takes. If one parameter is supported, it is going to be 
	 * the right child in the binary tree, except for FAC, which takes one parameter as the left child
	 * 
	 * @return Number of parameters taken
	 */
	public int nbParams ()
	{
		return this.nbParam;
	}

	/**
	 * Gets the operator that is the inverse of the current one. Only applied for functions (exp, ln, sin, ...)
	 * 
	 * @return The inverse function of this operator
	 */
	public Operators inverse ()
	{
		if (fnInverse == null)
			return null;

		return Operators.valueOf (fnInverse);
	}

	/**
	 * Gets the string form of the operator
	 * 
	 * @return {@code String} representation of the operator
	 */
	@Override
	public String toString ()
	{
		return name;
	}

	/**
	 * Gets the operator value from the provided string
	 * 
	 * @param name The name of the operator
	 * @return The operator of type {@code Operators} corresponding to the provided name
	 */
	public static Operators fromString (String name)
	{
		for (Operators o : Operators.values())
			if (o.toString().equals (name))
				return o;

		return null;
	}
}
