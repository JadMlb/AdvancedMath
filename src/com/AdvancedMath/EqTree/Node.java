package com.AdvancedMath.EqTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Stack;
import java.util.stream.Collectors;

import com.AdvancedMath.Exceptions.VariableNotAllowedException;
import com.AdvancedMath.Functionalities.Operators;
import com.AdvancedMath.Numbers.Number;

/**
 * Class that represents a node in a binary tree
 */
public abstract class Node
{
	private Node right, left;

	public Node () {}

	public Node (Node left, Node right)
	{
		this.left = left;
		this.right = right;
	}

	public Node getLeft ()
	{
		return left;
	}

	public void setLeft (Node left)
	{
		this.left = left;
	}

	public Node getRight ()
	{
		return right;
	}

	public void setRight (Node right)
	{
		this.right = right;
	}

	/**
	 * Counts the number of nodes in a tree including root and all parent nodes
	 * 
	 * @return Number of nodes in the tree
	 */
	public int countNodes ()
	{
		if (this.left == null && this.right == null)
			return 1;

		int count = 0;

		if (this.left != null)
			count += this.left.countNodes();

		if (this.right != null)
			count += this.right.countNodes();

		return 1 + count;
	}

	/**
	 * Transforms a {@code String} equation to a binary tree of {@code Node}s
	 * <p>e.g. (x+3) * 5y will be turned into its corresponding tree as simplified as possible
	 * 
	 * @param input {@code String} representing the equation
	 * @return A parsable binary tree of {@code Node}s
	 */
	public static Node parse (String input)
	{
		try
		{
			return parse (input, null);
		}
		catch (VariableNotAllowedException vnae)
		{
			return null;
		}
	}

	/**
	 * Transforms a {@code String} equation to a binary tree of {@code Node}s
	 * <p>e.g. (x+3) * 5y will be turned into its corresponding tree as simplified as possible
	 * 
	 * @param input {@code String} representing the equation
	 * @param allowedVars {@code HashSet} listing allowed variables in the tree
	 * @return A parsable binary tree of {@code Node}s
	 * @throws VariableNotAllowedException if a variable that is not in the list of allowed ones is detected
	 */
	public static Node parse (String input, HashSet<String> allowedVars) throws VariableNotAllowedException
	{
		ArrayList<String> opsSt = new ArrayList<>
		(
			Arrays.asList (Operators.values())
				.stream()
				.map (x -> x.toString())
				.collect (Collectors.toList())
		);
		
		ArrayList<Object> tokens = tokenize ("(" + input + ")", opsSt, allowedVars);
		
		return toTree (tokens);
	}
	
	/**
	 * Transform analyzed input to tree of nodes
	 * 
	 * @param tokens {@code ArrayList} of tokens
	 * @return {@code Node} representing the tree of the equation
	 * @see OperatorNode#tokenize(String, ArrayList)
	 */
	private static Node toTree (ArrayList<Object> tokens)
	{
		/* 
			is i ? =>
			is num | var ? => 
				before cur is [var | num | )] => push * to o
				push to n
			is op ? => 
				is ( ? =>
					before cur is [var | num | )] ? => push * to o
					push ( to o
				is ) ? => 
					loop [o ! empty & o.peek ! (]
						pop & take o
						get how many params it takes 
						pop as many needed from n
						push op node to n
					o ! empty & o.peek is ( ? => pop o
				is ^ & o.peek is ^ ? =>	push to o
				=>
					loop [o ! empty & o.peek ! ( & o.peek.pri >= curOp.pri]
						pop & take o
						get how many params it takes 
						pop as many needed from n
						push op node to n
					cur is fn & before cur is [num | var | )] ? => push * to o
					push op to o
			next
		*/

		boolean iFound = false;
		Stack<Node> nodes = new Stack<>();
		Stack<Operators> ops = new Stack<>();

		for (int i = 0; i < tokens.size(); i++)
		{
			Object o = tokens.get (i);
			if (o instanceof Operators op)
			{
				switch (op)
				{
					case OPR:
						if (i > 0 && requiresMultiplication (tokens.get (i - 1)))
							ops.push (Operators.MUL);
						ops.push (Operators.OPR);
						break;
					
					case CPR:
						while (!ops.empty() && ops.peek() != Operators.OPR)
							reduce (nodes, ops, iFound);

						if (!ops.empty() && ops.peek() == Operators.OPR) 
							ops.pop();
						break;
					
					default:
						if (op == Operators.POW && !ops.empty() && ops.peek() == Operators.POW)
							ops.push (Operators.POW);
						
						else
						{
							while (!ops.empty() && ops.peek() != Operators.OPR && ops.peek().pri() >= op.pri())
								reduce (nodes, ops, iFound);
							
							if (op.pri() == 5 && i > 0 && requiresMultiplication (tokens.get (i - 1)))
								ops.push (Operators.MUL);

							ops.push (op);
						}
				}
			}
			else if (o instanceof String s) // number or variable
			{
				if (i > 0 && requiresMultiplication (tokens.get (i - 1)))
					ops.push (Operators.MUL);
				
				if (isNumber (s))
					nodes.push (new NumberNode (Number.real (Double.parseDouble (s))));
				else if (s.equals ("i"))
				{
					iFound = true;
					nodes.push (new NumberNode (Number.I));
				}
				else if (s.equals ("e"))
					nodes.push (new NumberNode (Number.E));
				else if (s.equals ("π"))
					nodes.push (new NumberNode (Number.PI));
				else
					nodes.push (new VariableNode (s));
			}
			else if (o instanceof Number n)
			{
				nodes.push (new NumberNode (n));
			}
		}

		while (!ops.empty() && ops.peek() != Operators.OPR)
			reduce (nodes, ops, iFound);

		if (!ops.empty() && ops.peek() == Operators.OPR) 
			ops.pop();

		return nodes.pop();
	}

	/**
	 * Analyse the input and separate tokens 
	 * @param input The input {@code String} to be analysed
	 * @param opsSt {@code ArrayList} of all operators in form of string
	 * @param allowedVars {@code HashSet} listing allowed variables
	 * @return {@code ArrayList} of the separated tokens
	 * @throws VariableNotAllowedException if a variable that is not in the list of allowed ones is detected
	 */
	private static ArrayList<Object> tokenize (String input, ArrayList<String> opsSt, HashSet<String> allowedVars) throws VariableNotAllowedException
	{
		ArrayList<Object> analysedInput = new ArrayList<>();
		String nb = null;

		input = input.replaceAll (" ", "");

		for (int i = 0; i < input.length();)
		{
			if (Character.isLetter (input.charAt (i)))
			{
				if (nb != null)
				{
					if (nb.equals ("-"))
						nb = "-1";
					
					analysedInput.add (nb);
					nb = null;
				}
				
				if (i + 2 < input.length() && opsSt.contains (input.substring (i, i + 2)))
				{
					analysedInput.add (Operators.fromString (input.substring (i, i + 2)));
					i += 2;
				}
				else if (i + 3 < input.length() && opsSt.contains (input.substring (i, i + 3)))
				{
					analysedInput.add (Operators.fromString (input.substring (i, i + 3)));
					i += 3;
				}
				else if (i + 4 < input.length() && opsSt.contains (input.substring (i, i + 4)))
				{
					analysedInput.add (Operators.fromString (input.substring (i, i + 4)));
					i += 4;
				}
				else if (i + 5 < input.length() && opsSt.contains (input.substring (i, i + 5)))
				{
					analysedInput.add (Operators.fromString (input.substring (i, i + 5)));
					i += 5;
				}
				else if (i + 6 < input.length() && opsSt.contains (input.substring (i, i + 6)))
				{
					analysedInput.add (Operators.fromString (input.substring (i, i + 6)));
					i += 6;
				}
				else if (input.substring(i, i + 1).equals ("e"))
				{
					analysedInput.add (Number.E);
					i++;
				}
				else if (input.substring(i, i + 1).equals ("π"))
				{
					analysedInput.add (Number.PI);
					i++;
				}
				else
				{
					String variable = String.valueOf (input.charAt(i));
					if (allowedVars != null && !allowedVars.contains (variable))
						throw new VariableNotAllowedException (variable);

					analysedInput.add (input.charAt(i) + "");
					i++;
				}
				
				int lastIdx = analysedInput.size() - 1;
				if (analysedInput.get (lastIdx) instanceof Operators)
				{
					if (lastIdx >= 1 && analysedInput.get (lastIdx - 1) instanceof Operators o1 && o1 == ((Operators) analysedInput.get(lastIdx)).inverse())
					{
						analysedInput.remove (lastIdx);
						analysedInput.remove (lastIdx - 1);
					}
					else if (lastIdx >= 2 && analysedInput.get (lastIdx - 2) instanceof Operators o1 && o1 == ((Operators) analysedInput.get(lastIdx)).inverse())
					{
						analysedInput.remove (lastIdx);
						analysedInput.remove (lastIdx - 1);
						analysedInput.remove (lastIdx - 2);
					}
				}
			}
			else if (Character.isDigit (input.charAt (i)))
			{
				if (nb == null)	nb = "";

				nb += input.charAt (i++);
			}
			else if (opsSt.contains (input.charAt (i) + ""))
			{
				if ((input.charAt (i) + "").equals (Operators.SUB.toString()) && (i == 0 || i > 0 && input.charAt (i - 1) == '('))
				{
					if (nb != null)
						nb = "-" + nb;
					else
						nb = "-";
				}
				else
				{
					if (nb != null)
					{
						if (nb.equals ("-"))
							nb = "-1";
					
						analysedInput.add (nb);
						nb = null;
					}
	
					analysedInput.add (Operators.fromString (input.charAt (i) + ""));
				}
				
				i++;
			}
			else if (input.charAt (i) == '.')
			{
				if (nb == null)	nb = "0";
				nb += input.charAt (i++);
			}
		}
		
		return analysedInput;
	}

	private static void reduce (Stack<Node> nodes, Stack<Operators> ops, boolean iFound)
	{
		Operators oper = ops.pop();
		Node left = null, right = null, toPush = null;
		
		switch (oper.nbParams())
		{
			case 1:
				if (oper == Operators.FAC/*  || oper == Operators.PER */)
					left = nodes.pop();
				else
					right = nodes.pop();
				break;

			case 2:
				right = nodes.pop();
				left = nodes.pop();

				switch (oper)
				{
					case ADD:
						if (left instanceof VariableNode l && right instanceof VariableNode r && l.isCompatibleWith (r))
						{
							l.setMultiplier (l.getMultiplier().add (r.getMultiplier()));
							toPush = l;
						}
						break;

					case SUB:
						if (left instanceof VariableNode l && right instanceof VariableNode r && l.isCompatibleWith (r))
						{
							if (l.getMultiplier().equals (r.getMultiplier()))
								toPush = new NumberNode (Number.ZERO);
							else
							{
								l.setMultiplier (l.getMultiplier().subtract (r.getMultiplier()));
								toPush = l;
							}
						}
						break;

					case MUL:
						if (left instanceof VariableNode v)
						{
							if (right instanceof NumberNode n)
							{
								v.setMultiplier (v.getMultiplier().multiply (n.getValue()));
								toPush = v;
							}
							else if (right instanceof VariableNode r && v.isCompatibleWith (r))
							{
								v.setPower (v.getPower().add (r.getPower()));
								toPush = v;
							}
						}
						else if (right instanceof VariableNode v)
						{
							if (left instanceof NumberNode n)
							{
								v.setMultiplier (v.getMultiplier().multiply (n.getValue()));
								toPush = v;
							}
							else if (left instanceof VariableNode r && v.isCompatibleWith (r))
							{
								v.setPower (v.getPower().add (r.getPower()));
								toPush = v;
							}
						}
						break;
					
					case DIV:
						if (left instanceof NumberNode n && n.getValue().equals (Number.ZERO))
							toPush = new NumberNode (Number.ZERO);
						else if (right instanceof NumberNode n && n.getValue().equals (Number.ZERO))
							toPush = new NumberNode (Number.real (Double.POSITIVE_INFINITY)); // TODO: check if left side is positive or negative to set the correct sign of infinity
						else if (left.equals (right))
							toPush = new NumberNode (Number.ONE);
						else if (left instanceof VariableNode l && right instanceof VariableNode r && l.isCompatibleWith (r))
						{
							if (l.getPower().compareTo (r.getPower()) == 0)
								toPush = new NumberNode (l.getMultiplier().divide (r.getMultiplier()));
							else
							{
								l.setMultiplier (l.getMultiplier().divide (r.getMultiplier()));
								l.setPower (l.getPower().subtract (r.getPower()));
							}
						}
						break;

					case POW:
						if (left instanceof VariableNode l && right instanceof NumberNode r && r.getValue().isPureReal())
						{
							l.setPower (l.getPower().multiply (r.getValue().getX()));
							toPush = l;
						}
						break;
							
					default:
				}
				break;
		}

		if (left instanceof OperatorNode o && left.countNodes() >= right.countNodes() && trySimplify (o, oper, right))
			toPush = left;
		else if (
				right instanceof OperatorNode o
				&& (
					!(left instanceof OperatorNode)
					|| ((OperatorNode) left).getOperator().pri() == 5 && left.countNodes() < right.countNodes()
				)
				&& trySimplify (o, oper, left)
			)
			toPush = right;
		else if (toPush == null && left != null && right != null && left.getClass().equals (right.getClass()) && left instanceof Operable)
			toPush = simplifyAtomic (new OperatorNode (oper, left, right));

		if (toPush != null)
			nodes.push (toPush);
		else
			nodes.push (new OperatorNode (oper, left, right));
	}

	/**
	 * Simplifies the given subtree by trying to combine like terms, e.g. 3x^2 + 5 - 20 + 4x^2 will result in 7x^2 - 15
	 * 
	 * @param subtree Current subtree to expand
	 * @param op Operator attached to the Node wished to be added
	 * @param n Node to add to tree
	 * @return true if simplification happened, false otherwise
	 */
	protected static boolean trySimplify (OperatorNode subtree, Operators op, Node n)
	{
		Stack<Node> nodes = new Stack<>();
		Node cur = subtree;

		// in-order, iterative tree traversal
		while (!nodes.empty() || cur != null)
			if (cur != null)
			{
				nodes.push (cur);
				cur = cur.left;
			}
			else
			{
				cur = nodes.pop();

				OperatorNode parentNode = null;
				try
				{
					parentNode = (OperatorNode) nodes.peek();
				}
				catch (EmptyStackException ese) {}

				// check if parent (which is guaranteed to be an OperatorNode) has same priority as the node to be added
				// to avoid cases like factorizing the numerator of a fraction with a non fraction
				// or if current node is the root
				
				// operate
				// if simplification happened no need to continue
				if ((parentNode == null || parentNode.getOperator() != Operators.DIV) && cur instanceof OperatorNode on && on.getOperator().pri() == op.pri() && n instanceof Operable o)
				{
					if (cur.left instanceof Operable l)
					{
						try
						{
							cur.left = (Node) switch (op) {
								case ADD -> l.add (o);
								case SUB -> l.subtract (o);
								case MUL -> l.multiply (o);
								case DIV -> l.divide (o);
								case POW -> l.pow (o);
								default -> l;
							};
							return true;
						}
						catch (Exception e) {}
					}

					if (cur.right instanceof Operable r)
					{
						try
						{
							// handle the case where the operator is "-" => need to negate the value of the right child before operating on it
							Operable right = r;
							if (on.getOperator() == Operators.SUB)
								right = r.negateCopy();
							
							Operable res = switch (op) {
								case ADD -> right.add (o);
								case SUB -> right.subtract (o);
								case MUL -> right.multiply (o);
								case DIV -> right.divide (o);
								case POW -> right.pow (o);
								default -> r;
							};
							
							// remove redundant resulting sign when + or - is operator and value is < 0
							if (res.sgn() == -1)
							{
								res.negate();
								on.setOperator (Operators.SUB);
							}

							cur.right = (Node) res;

							return true;
						}
						catch (Exception e) {}
					}
				}
				else if (cur instanceof OperatorNode subT && n instanceof OperatorNode toAdd)
				{
					// FIXME: generalize this if
					if (subT.equals (toAdd))
					{
						// FIXME: add support for other operators
						switch (op)
						{
							case ADD:
								cur = new OperatorNode (Operators.MUL, new NumberNode (Number.real (2.)), new OperatorNode (toAdd.getOperator(), toAdd.getLeft(), toAdd.getRight()));
								break;
							
							case SUB:
								// TODO: remove node from tree
								break;

							case MUL:
								cur = new OperatorNode (Operators.POW, new OperatorNode (toAdd.getOperator(), toAdd.getLeft(), toAdd.getRight()), new NumberNode (Number.real (2.)));
								break;

							case DIV:
								break;
						
							default:
								break;
						}
						Node parent = nodes.pop();
						cur = new OperatorNode (Operators.MUL, new NumberNode (Number.real (2.)), new OperatorNode (toAdd.getOperator(), toAdd.getLeft(), toAdd.getRight()));
						parent.left = cur;
						nodes.push (parent);
						return true;
					}
					
					if (op.pri() == 1 && subT.getOperator().pri() < 5) // only if +/-
					{
						// check for common elements to factorize if possible

						boolean commonFactorFound = false, isSubTLeft = true, isToAddLeft = true;
						boolean toAddIsFunction = toAdd.getOperator().pri() == 5;

						if (toAddIsFunction)
						{
							// cannot try and factorise argument of a function with its multiplier (e.g. (x+2) * e^(x+2) => cannot facotrise x+2 together)
							// rather, check if the tree to add is in the children of the original subtree

							for (int i = 0; i < 2; i++)
							{
								isSubTLeft = (i & 0x01) == 0;
								Node subTNode = isSubTLeft ? subT.getLeft() : subT.getRight();
								
								if (subTNode.equals (toAdd))
								{
									commonFactorFound = true;
									break;
								}
							}
						}
						else
						{
							// safe to factorize
							for (int i = 0; i < 4; i++)
							{
								isSubTLeft = (i >> 1) == 0;
								isToAddLeft = (i & 0x01) == 0;
								Node subTNode = isSubTLeft ? subT.getLeft() : subT.getRight();
								Node toAddNode = isToAddLeft ? toAdd.getLeft() : toAdd.getRight();
								
								if (subTNode.equals (toAddNode))
								{
									commonFactorFound = true;
									break;
								}
							}
						}
						
						if (commonFactorFound)
						{
							Node factorized = new OperatorNode (
														op,
														subT.getOperator().pri() == 2 ? (isSubTLeft ? subT.getRight() : subT.getLeft()) : new NumberNode (Number.ONE),
														toAddIsFunction ? new NumberNode (Number.ONE) : (isToAddLeft ? toAdd.getRight() : toAdd.getLeft())
													);
							
							factorized = simplifyAtomic ((OperatorNode) factorized);
							
							if (!nodes.empty())
							{
								Node parent = nodes.pop();
								parent.left = new OperatorNode (Operators.MUL, factorized, isSubTLeft ? subT.getLeft() : subT.getRight());;
								nodes.push (parent);
							}
							else if (isSubTLeft)
								if (subT.getOperator().pri() == 2)
									cur.right = factorized;
								else
									cur.left = new OperatorNode (
														Operators.MUL,
														factorized,
														isSubTLeft ? subT.getLeft() : subT.getRight()
													);
							else
								if (subT.getOperator().pri() == 2)
									cur.left = factorized;
								else
									cur.right = new OperatorNode (
														Operators.MUL,
														factorized,
														isSubTLeft ? subT.getLeft() : subT.getRight()
													);
	
							return true;
						}
					}
				}
				
				cur = cur.right;
			}
		
		return false;
	}

	protected static Node simplifyAtomic (OperatorNode n)
	{
		// in case of missing values, fill them in with one
		switch (n.getOperator())
		{
			case ADD: case SUB:
				if (n.getRight() == null)
					n.setRight (new NumberNode (Number.ONE));
				else if (n.getLeft() == null)
					n.setLeft (new NumberNode (Number.ONE));

				if (n.getLeft() instanceof NumberNode l && l.getValue().equals (Number.ZERO))
					return n.getRight();
					
				if (n.getRight() instanceof NumberNode r && r.getValue().equals (Number.ZERO))
					return n.getLeft();

				if (n.getRight() instanceof Operable o && o.sgn() < 0)
				{
					Operators newOperator = null;
					if (n.getOperator() == Operators.ADD)
						newOperator = Operators.SUB;
					else
						newOperator = Operators.ADD;

					return new OperatorNode (newOperator, n.getLeft(), ((Node) o.negateCopy()));
				}
				break;

			case MUL:
				// 0 * x or x * 0
				if (
					n.getLeft() instanceof NumberNode nbL && nbL.getValue().equals (Number.ZERO) ||
					n.getRight() instanceof NumberNode nbR && nbR.getValue().equals (Number.ZERO) ||
					n.getLeft() instanceof VariableNode vL && vL.getMultiplier().equals (Number.ZERO) ||
					n.getRight() instanceof VariableNode vR && vR.getMultiplier().equals (Number.ZERO)
				)
					return new NumberNode (Number.ZERO);
				
				// 1 * x
				if (n.getLeft() instanceof NumberNode l && l.getValue().equals (Number.ONE))
					return n.getRight();
					
				// x * 1
				if (n.getRight() instanceof NumberNode r && r.getValue().equals (Number.ONE))
					return n.getLeft();

				// a * (1 *|/ b)
				if (n.getRight() instanceof OperatorNode r && r.getOperator().pri() == Operators.DIV.pri() && r.getLeft().equals (Number.ONE))
					return combine (Operators.DIV, n.getLeft(), r.getRight());
				// a * (b * 1)
				if (n.getRight() instanceof OperatorNode r && r.getOperator() == Operators.MUL && r.getRight().equals (Number.ONE))
					return combine (Operators.DIV, n.getLeft(), r.getRight());
				break;
			case DIV:
				if (n.getLeft().equals (n))
			
				// 0 / x
				if (n.getLeft() instanceof Operable o && o.evaluatesToZero())
					return new NumberNode (Number.ZERO);
				
				// x / 0
				if (n.getRight() instanceof Operable nbR && nbR.evaluatesToZero())
					return new NumberNode (
						new Number (
							nbR.sgn() >= 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY,
							nbR.sgn() >= 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY
						)
					);
				
				// x / 1
				if (n.getRight() instanceof NumberNode r && r.getValue().equals (Number.ONE))
					return n.getLeft();

				if (n.getLeft().equals (n.getRight()))
					return new NumberNode (Number.ONE);
				break;

			default:
				break;
		}

		if (n.getLeft() instanceof Operable l && n.getRight() instanceof Operable r)
		{
			try
			{
				return (Node) switch (n.getOperator())
				{
					case ADD -> l.add (r);
					case SUB -> l.subtract (r);
					case MUL -> l.multiply (r);
					case DIV -> l.divide (r);
					case POW -> l.pow (r);
					default -> n;
				};
			}
			catch (Exception e)
			{
				// e.printStackTrace();
			}
		}
		
		return n;
	}

	protected static Node combine (Operators op, Node left, Node toAdd)
	{
		if (left instanceof OperatorNode oL && Node.trySimplify (oL, op, toAdd))
			return left;
		return simplifyAtomic (new OperatorNode (op, left, toAdd));
	}

	private static boolean isNumber (String s)
	{
		try
		{
			Double.parseDouble (s);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}

	private static boolean requiresMultiplication (Object o)
	{
		if (o instanceof Operators op && op == Operators.CPR)
			return true;
		else if (o instanceof String s && (isNumber (s) || s.length() == 1))
			return true;

		return false;
	}

	public abstract Node differentiate (String variable);
}
