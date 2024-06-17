package com.AdvancedMath.EqTree;

import com.AdvancedMath.Functionalities.Operators;
import com.AdvancedMath.Numbers.FractionValue;
import com.AdvancedMath.Numbers.Number;

/**
 * Class that represents the relationship between two other {@code Node}s
 */
public class OperatorNode extends Node
{
	private Operators operator;
	
	/**
	 * Create a new {@code OperatorNode} with given children.
	 * If the number of children of this operator is equal to 1, then the left child is null, except for {@code FAC}
	 * 
	 * @param operator The {@code Operator} that defines the relationship
	 * @param left The left child of the binary tree
	 * @param right The right child of the binary tree
	 * @see Operators
	 */
	public OperatorNode (Operators operator, Node left, Node right)
	{
		super (left, right);

		this.operator = operator;
	}

	public Operators getOperator () 
	{
		return this.operator;
	}

	public void setOperator (Operators operator) 
	{
		this.operator = operator;
	}

	/**
	 * Simplifies the given binary tree representing an equation
	 * 
	 * <p>e.g. provided (10*x)/(2*x), 5 is returned</p>
	 * 
	 * <p><strong>NB:</strong> This method is deprecated and returns {@code root} since {@code Node.parse} simplifies as it builds the tree</p>
	 * 
	 * @param root The root {@code Node} of the given tree
	 * @return The simplified tree, either a {@code NumberNode} or an {@code OperatorNode}
	 */
	@Deprecated
	public static Node simplify (Node root)
	{
		return root;
	}

	@Override
	public Node differentiate (String variable)
	{
		Node u = getLeft(), v = getRight();
		Node uPrime = null, vPrime = null;

		if (u != null)
			uPrime = u.differentiate (variable);
		
		if (v != null)
			vPrime = v.differentiate (variable);
		
		switch (operator)
		{
			case ADD: case SUB: 
				if (uPrime.equals (Number.ZERO))
					return operator.equals(Operators.SUB) && vPrime instanceof Operable vOp ? (Node) vOp.negateCopy() : vPrime;
				else if (vPrime.equals (Number.ZERO))
					return uPrime;
				else
					return combine (operator, uPrime, vPrime);
			case MUL: 
				return combine
				(
					Operators.ADD,
					// new OperatorNode (Operators.MUL, uPrime, v),
					combine (Operators.MUL, uPrime, v),
					// new OperatorNode (Operators.MUL, u, vPrime)
					combine (Operators.MUL, u, vPrime)
				);
			case DIV: 
				return combine
				(
					Operators.DIV,
					combine (Operators.SUB, combine (Operators.MUL, uPrime, v), combine (Operators.MUL, u, vPrime)),
					combine (Operators.POW, v, new NumberNode (Number.real (2.)))
				);
			case POW:
				if (v instanceof NumberNode pow) // Power is a number use n*u^(n-1)*d_dx(u)
				{
					NumberNode originalPower = new NumberNode (pow.getValue());
					pow = (NumberNode) pow.subtract (new NumberNode (Number.ONE));
					Node powNode = null;
					if (pow.getValue().equals (Number.ONE))
						powNode = u;
					else
						powNode = combine
						(
							Operators.POW,
							u,
							pow
						);

					return combine
					(
						Operators.MUL,
						originalPower,
						combine
						(
							Operators.MUL,
							uPrime,
							powNode
						)
					);
				}
				else // use deriv (u^v) = u^v * (lnu * dv/dx + v/u * du/dx)
				{
					return combine
					(
						Operators.MUL,
						this,
						combine
						(
							Operators.ADD,
							combine
							(
								Operators.MUL,
								combine
								(
									Operators.LN,
									null,
									u
								),
								vPrime
							),
							combine
							(
								Operators.MUL,
								combine
								(
									Operators.DIV,
									v,
									u
								),
								uPrime
							)
						)
					);
				}
			case LN: return combine
				(
					Operators.DIV,
					vPrime,
					v
				);
			case EXP: return combine
				(
					Operators.MUL,
					vPrime,
					combine
					(
						Operators.EXP,
						null,
						v
					)
				);
			case ABS: return combine
				(
					Operators.DIV,
					this,
					v
				);
			case SIN: return combine
				(
					Operators.MUL,
					combine
					(
						Operators.SUB,
						null,
						vPrime
					),
					combine
					(
						Operators.COS,
						null,
						v
					)
				);
			case COS: return combine
				(
					Operators.MUL,
					vPrime,
					combine
					(
						Operators.SIN,
						null,
						v
					)
				);
			case TAN: return combine
				(
					Operators.DIV,
					vPrime,
					combine
					(
						Operators.POW,
						combine (Operators.COS, null, v),
						new NumberNode (2.0, 0.0)
					)
				);
			case ASIN: return combine
				(
					Operators.DIV,
					vPrime,
					combine
					(
						Operators.POW,
						combine
						(
							Operators.SUB,
							new NumberNode (Number.ONE),
							combine (Operators.POW, v, new NumberNode (Number.real (2.0)))
						),
						new NumberNode (Number.real (new FractionValue (1, 5)))
					)
				);
			case ACOS: return combine
				(
					Operators.SUB,
					new NumberNode (Number.ZERO),
					combine
					(
						Operators.DIV,
						vPrime,
						combine
						(
							Operators.POW,
							combine
							(
								Operators.SUB,
								new NumberNode (Number.ONE),
								combine (Operators.POW, v, new NumberNode (2.0, 0.0))
							),
							new NumberNode (Number.real (new FractionValue (1, 5)))
						)
					)
				);
			case ATAN: return combine
				(
					Operators.DIV,
					vPrime,
					combine
					(
						Operators.ADD,
						new NumberNode (Number.ONE),
						combine (Operators.POW, v, new NumberNode (2.0, 0.0))
					)
				);
			case SINH: return combine
				(
					Operators.MUL,
					vPrime,
					combine
					(
						Operators.COSH,
						null,
						v
					)
				);
			case COSH: return combine
				(
					Operators.MUL,
					vPrime,
					combine
					(
						Operators.SINH,
						null,
						v
					)
				);
			case TANH: return combine
				(
					Operators.DIV,
					vPrime,
					combine
					(
						Operators.POW,
						combine (Operators.COSH, null, v),
						new NumberNode (2.0, 0.0)
					)
				);
			case ASH: return combine
				(
					Operators.DIV,
					vPrime,
					combine
					(
						Operators.POW,
						combine
						(
							Operators.ADD,
							new NumberNode (Number.ONE),
							combine (Operators.POW, v, new NumberNode (2.0, 0.0))
						),
						new NumberNode (Number.real (new FractionValue (1, 5)))
					)
				);
			case ACH: return combine
				(
					Operators.DIV,
					vPrime,
					combine 
					(
						Operators.MUL,
						combine
						(
							Operators.POW,
							combine
							(
								Operators.SUB,
								v,
								new NumberNode (Number.ONE)
							),
							new NumberNode (Number.real (new FractionValue (1, 5)))
						),
						combine
						(
							Operators.POW,
							combine
							(
								Operators.ADD,
								v,
								new NumberNode (Number.ONE)
							),
							new NumberNode (Number.real (new FractionValue (1, 5)))
						)
					)
				);
			case ATH: return combine
				(
					Operators.DIV,
					vPrime,
					combine
					(
						Operators.SUB,
						new NumberNode (Number.ONE),
						combine (Operators.POW, v, new NumberNode (2.0, 0.0))
					)
				);
			default:
				return null;
		}
	}

	@Override
	public String toString ()
	{
		String s = "";
		boolean openPar = false;
		if (operator.pri() > Operators.ADD.pri() && (getLeft() instanceof OperatorNode l && l.getOperator().pri() == Operators.ADD.pri()))
		{
			s += "(";
			openPar = true;
		}

		if (this.getLeft() != null)
			s += this.getLeft().toString();

		if (openPar)
		{
			s += ")";
			openPar = false;
		}
		
		s += this.operator.toString();

		Node leftestInRightSubtree = this.getRight();
		while (leftestInRightSubtree.getLeft() != null)
			leftestInRightSubtree = leftestInRightSubtree.getLeft();

		if (
			getRight() instanceof OperatorNode r && r.getOperator().pri() == Operators.ADD.pri()
			|| operator.pri() == 5
			|| leftestInRightSubtree instanceof Operable o && o.sgn() == -1
		)
		{
			s += "(";
			openPar = true;
		}
		
		if (this.getRight() != null)
			s += this.getRight().toString();
		
		if (openPar)
			s += ")";
		return s;
	}

	/**
	 * Checks if the object is a tree of the same value
	 * 
	 * @return 
	 * <ul>
	 * 	<li>{@code true}
	 * 		<ul>
	 * 			<li>If {@code o} is this instance</li>
	 * 			<li>If {@code o} is a {@code Number} or {@code NumberNode} and this instance is as well</li>
	 * 			<li>If {@code o} is an {@code OperatorNode} and has the same children (or in different forms)</li>
	 * 		</ul>
	 * 	</li>
	 * 	<li>{@code false} otherwise</li>
	 * </ul>
	 */
	@Override
	public boolean equals (Object o)
	{
		if (this == o)
			return true;
		if (o instanceof Number n)
			try
			{
				Number nb = Number.valueOf (this, null);
				return nb.equals (n);
			}
			catch (Exception e) {}
		if (o instanceof NumberNode n)
			try
			{
				Number nb = Number.valueOf (this, null);
				return nb.equals (n.getValue());
			}
			catch (Exception e) {}
		if (o instanceof OperatorNode n)
			if (this.operator == n.operator)
				if (this.getLeft() != null && n.getLeft() != null && this.getRight() != null && n.getRight() != null)
					return this.getLeft().equals (n.getLeft()) && this.getRight().equals (n.getRight())
						|| this.getLeft().equals (n.getRight()) && this.getRight().equals (n.getLeft());
				else if (this.getLeft() == null && n.getLeft() == null && this.getRight() != null && n.getRight() != null)
					return this.getRight().equals (n.getRight());
				else if (this.getLeft() != null && n.getLeft() != null && this.getRight() == null && n.getRight() == null)
					return this.getLeft().equals (n.getLeft());
		return false;
	}
}
