package com.AdvancedMath.EqTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.stream.Collectors;

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
		ArrayList<String> opsSt = new ArrayList<>
		(
			Arrays.asList (Operators.values())
				.stream()
				.map (x -> x.toString())
				.collect (Collectors.toList())
		);
		
		ArrayList<Object> tokens = tokenize ("(" + input + ")", opsSt);
		
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
	 * @return {@code ArrayList} of the separated tokens
	 */
	private static ArrayList<Object> tokenize (String input, ArrayList<String> opsSt)
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

		if (left instanceof OperatorNode o && trySimplify (o, oper, right))
			toPush = left;
		else if (right instanceof OperatorNode o && !(left instanceof OperatorNode) && trySimplify (o, oper, left))
			toPush = right;

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
	private static boolean trySimplify (OperatorNode subtree, Operators op, Node n)
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
				
				// operate
				// if simplification happened no need to continue
				if (cur instanceof OperatorNode on && on.getOperator().pri() == op.pri() && n instanceof Operable o)
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
					if (op.pri() == 1) // only if +/-
					{
						// check for common elements to factorize if possible
						// subT = 2 * ln(x)
						// toAdd = 3x * ln(x)
						boolean isSubTLeft = true, isToAddLeft = true;
						for (int i = 0; i < 4; i++)
						{
							Node subTNode = isSubTLeft ? subT.getLeft() : subT.getRight();
							Node toAddNode = isToAddLeft ? toAdd.getLeft() : toAdd.getRight();
							
							if (subTNode.equals (toAddNode))
								break;

							isSubTLeft = (i >> 1) == 0;
							isToAddLeft = (i & 0x01) == 0;
						}

						Node factored = new OperatorNode (
												op,
												isSubTLeft ? subT.getRight() : subT.getLeft(),
												isToAddLeft ? toAdd.getRight() : toAdd.getLeft()
											);
						
						if (!nodes.empty())
						{
							Node parent = nodes.pop();
							parent.left = new OperatorNode (Operators.MUL, factored, isSubTLeft ? subT.getLeft() : subT.getRight());;
							nodes.push (parent);
						}
						else if (isSubTLeft)
							cur.right = factored;
						else
							cur.left = factored;

						return true;
					}
				}
				
				cur = cur.right;
			}
		
		return false;
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
}
