package com.AdvancedMath.EqTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

import com.AdvancedMath.Functionalities.Operators;
import com.AdvancedMath.Numbers.Number;

/**
 * Class that represents the relationship between two other {@code Node}s
 */
public class OperatorNode extends Node
{
	private Operators operator;
	// used for the case of division (simplify)
	private static final HashMap<String, HashSet<String>> NUM_EQUIVALENCY_TABLE = new HashMap<>(), DEN_EQUIVALENCY_TABLE = new HashMap<>();

	static
	{
		// 0 = (), 1 = + or -, 2 = *, 3 = /, a, b and c are the nodes involved
		createMapping (new String[]{"0a1b03c", "a3c1b3c"}, true);
		createMapping (new String[]{"0a2b03c", "a20b3c0", "b20a3c0"}, true);
		createMapping (new String[]{"0a3b03c", "a30b2c0", "0a3c03b"}, true);
		createMapping (new String[]{"0a3b03c", "a30b2c0", "0a3c03b"}, false);
		createMapping (new String[]{"a30b3c0", "a2c03b0"}, false);	
	}
	
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

	private static void createMapping (String[] values, boolean isNum)
	{
		for (int i = 0; i < values.length; i++)
			for (int j = 0; j < values.length; j++)
				if (i != j)
				{
					HashSet<String> cur = isNum ? NUM_EQUIVALENCY_TABLE.get (values[i]) : DEN_EQUIVALENCY_TABLE.get (values[i]);
					if (cur == null)
						cur = new HashSet<>();
					
					cur.add (values[j]);
					if (isNum)
						NUM_EQUIVALENCY_TABLE.put (values[i], cur);
					else
						DEN_EQUIVALENCY_TABLE.put (values[i], cur);
				}
	}

	/**
	 * Simplifies the given binary tree representing an equation
	 * 
	 * <p>e.g. provided (10*x)/(2*x), 5 is returned
	 * 
	 * @param root The root {@code Node} of the given tree
	 * @return The simplified tree
	 */
	public static Node simplify (Node root)
	{
		if (root == null)
			return null;
		
		if (root instanceof OperatorNode o)
		{
			try
			{
				Number n = Number.valueOf (root, null);
				return new NumberNode (n);
			}
			catch (Exception e) {}
			
			if (o.operator.pri() == 5 && o.getRight() instanceof OperatorNode arg && arg.operator == o.operator.inverse())
				return simplify (arg.getRight());
			else if (o.operator == Operators.DIV)
			{
				if (o.getLeft().equals (o.getRight()))
					return new NumberNode (Number.ONE);

				Number divided = null, divisor = null;
				Node simplifiedLeft = simplify (o.getLeft()), simplifiedRight = simplify (o.getRight());
				try
				{
					divided = Number.valueOf (simplifiedLeft, null);
				}
				catch (Exception e) {}
				
				try
				{
					divisor = Number.valueOf (simplifiedRight, null);
				}
				catch (Exception e) {}

				if (divided != null && divisor != null)
					return new NumberNode (divided.divide (divisor));
				else if (divided != null && divided.equals (Number.ZERO))
					return new NumberNode (divided);
				else if (divisor != null)
				{
					if (divisor.equals (Number.ZERO))
						return new NumberNode (Double.NaN, Double.NaN);
					else if (divisor.equals (Number.ONE))
						return simplifiedLeft;
				}

				// get the case
				HashMap<Integer, Node> nodes = new HashMap<>();
				OperatorNode newO = new OperatorNode (Operators.DIV, simplifiedLeft, simplifiedRight);
				String[] cases = getCase (newO, nodes);
				
				HashSet<Node> casesNodes = new HashSet<>();
				casesNodes.add (newO);
				
				// split based on numerator
				if (NUM_EQUIVALENCY_TABLE.get (cases[0]) != null)
					for (String possib : NUM_EQUIVALENCY_TABLE.get (cases[0]))
					{
						boolean isAdd = false;
						if (cases[0].equals ("0a1b03c"))
							if (((OperatorNode) newO.getLeft()).operator == Operators.ADD)
								isAdd = true;

						casesNodes.add (construct (possib, nodes, false, isAdd));
					}
				
				// split based on denominator
				if (NUM_EQUIVALENCY_TABLE.get (cases[1]) != null)
					for (String possib : DEN_EQUIVALENCY_TABLE.get (cases[1]))
						casesNodes.add (construct (possib, nodes, true, false));

				Iterator<Node> i = casesNodes.iterator();
				Node currentBest = newO;
				int currentBestFitness = newO.countNodes();
				while (i.hasNext())
				{
					Node simplified = simplifyDiv (i.next());
					
					int fitness = simplified.countNodes();
					if (fitness < currentBestFitness)
					{
						currentBest = simplified;
						currentBestFitness = fitness;
					}
				}

				return currentBest;
			}
			else if (o.operator == Operators.MUL)
			{
				ArrayList<Node> nodes = new ArrayList<>();
				Stack<Node> stack = new Stack<>();
				Node cur = o;

				// check if the node corrsponds to a/b * c/d
				HashMap<Integer, Node> nodesMap = new HashMap<>();
				String[] cases = getCase (o, nodesMap);

				if (cases[0] != null && cases[1] != null)
				{
					HashSet<Node> casesNodes = new HashSet<>();
					casesNodes.add (o);
					
					// split based on numerator
					if (NUM_EQUIVALENCY_TABLE.get (cases[0]) != null)
						for (String possib : NUM_EQUIVALENCY_TABLE.get (cases[0]))
						{
							boolean isAdd = false;
							if (cases[0].equals ("0a1b03c"))
								if (((OperatorNode) o.getLeft()).operator == Operators.ADD)
									isAdd = true;
	
							casesNodes.add (construct (possib, nodesMap, false, isAdd));
						}
					
					// split based on denominator
					if (NUM_EQUIVALENCY_TABLE.get (cases[1]) != null)
						for (String possib : DEN_EQUIVALENCY_TABLE.get (cases[1]))
							casesNodes.add (construct (possib, nodesMap, true, false));
	
					Iterator<Node> it = casesNodes.iterator();
					Node currentBest = o;
					int currentBestFitness = o.countNodes();
					while (it.hasNext())
					{
						Node simplified = simplifyDiv (it.next());
						
						int fitness = simplified.countNodes();
						if (fitness < currentBestFitness)
						{
							currentBest = simplified;
							currentBestFitness = fitness;
						}
					}
	
					if (!currentBest.equals (o))
						return currentBest;
				}

				while (!stack.empty() || cur != null)
				{
					if (cur != null && cur instanceof OperatorNode n && n.operator == Operators.MUL)
					{
						stack.push (cur);
						cur = cur.getLeft();
					}
					else if (cur != null && cur instanceof OperatorNode)
					{
						nodes.add (simplify (cur));
						if (!stack.empty())
							cur = stack.pop().getRight();
						else break;
					}
					else if (cur != null)
					{
						int idx = nodes.indexOf (cur);
						if (idx > -1)
						{
							nodes.remove (idx);
							nodes.add (new OperatorNode (Operators.POW, cur, new NumberNode (Number.real (2.0))));
						}
						else
						{
							idx = indexOfNodeToPower (nodes, cur);
							if (idx > -1)
							{
								OperatorNode found = (OperatorNode) nodes.get(idx);
								Node pow = found.getRight();

								if (pow instanceof NumberNode n)
								{
									found.setRight (new NumberNode (n.getValue().add (Number.ONE)));
								}
								else
								{
									try
									{
										Number powNb = Number.valueOf (pow, null);
										found.setRight (new NumberNode (powNb.add (Number.ONE)));
										continue;
									}
									catch (Exception e) {}

									found.setRight (new OperatorNode (Operators.ADD, pow, new NumberNode (Number.ONE)));
								}
							}
							else
							{
								if (cur instanceof NumberNode n && n.getValue().equals (Number.ZERO))
									return new NumberNode (Number.ZERO);
								
								int numberIdx = containsNumber (nodes);
								if (numberIdx > -1 && cur instanceof NumberNode n)
								{
									((NumberNode) nodes.get (numberIdx)).setValue (n.getValue().multiply (((NumberNode) nodes.get (numberIdx)).getValue()));
									cur = cur.getLeft();
									continue;
								}

								nodes.add (simplify (cur));
							}
						}
						cur = cur.getLeft();
					}
					else
					{
						if (!stack.empty())
							cur = stack.pop().getRight();
						else break;
					}
				}

				OperatorNode newRoot = new OperatorNode (Operators.MUL, nodes.get (0), nodes.get (1));
				for (int i = 2; i < nodes.size(); i++)
				{
					newRoot = new OperatorNode (Operators.MUL, newRoot, nodes.get (i));
				}

				return newRoot;
			}
			else if (o.operator.pri() == 1) // + or -
			{
				// check if it is the case of a/b +- c/d
				HashMap<Integer, Node> nodesMap = new HashMap<>();
				String[] cases = getCase (o, nodesMap);

				if (cases[0] != null && cases[1] != null)
				{
					HashSet<Node> casesNodes = new HashSet<>();
					casesNodes.add (o);
					
					// split based on numerator
					if (NUM_EQUIVALENCY_TABLE.get (cases[0]) != null)
						for (String possib : NUM_EQUIVALENCY_TABLE.get (cases[0]))
						{
							boolean isAdd = false;
							if (cases[0].equals ("0a1b03c"))
								if (((OperatorNode) o.getLeft()).operator == Operators.ADD)
									isAdd = true;
	
							casesNodes.add (construct (possib, nodesMap, false, isAdd));
						}
					
					// split based on denominator
					if (NUM_EQUIVALENCY_TABLE.get (cases[1]) != null)
						for (String possib : DEN_EQUIVALENCY_TABLE.get (cases[1]))
							casesNodes.add (construct (possib, nodesMap, true, false));
	
					Iterator<Node> it = casesNodes.iterator();
					Node currentBest = o;
					int currentBestFitness = o.countNodes();
					while (it.hasNext())
					{
						Node simplified = simplifyDiv (it.next());
						
						int fitness = simplified.countNodes();
						if (fitness < currentBestFitness)
						{
							currentBest = simplified;
							currentBestFitness = fitness;
						}
					}
	
					if (!currentBest.equals (o))
						return currentBest;
				}
				
				Node simplifiedLeft = simplify (o.getLeft()),
					simplifiedRight = simplify (o.getRight());

				if (simplifiedLeft instanceof NumberNode n)
				{
					if (n.getValue().equals (Number.ZERO))
					{
						if (o.operator == Operators.SUB)
							if (simplifiedRight instanceof NumberNode nb)
								return new NumberNode (nb.getValue().negate());
							else
								return new OperatorNode (Operators.SUB, null, simplifiedRight);
						return simplifiedRight;
					}
				}
				
				if (simplifiedRight instanceof NumberNode n)
				{
					if (n.getValue().equals (Number.ZERO))
						return simplifiedLeft;
				}

				if (simplifiedLeft instanceof NumberNode l && simplifiedRight instanceof NumberNode r)
					return new NumberNode (l.getValue().subtract (r.getValue()));

				return new OperatorNode (o.operator, simplifiedLeft, simplifiedRight);
			}
			else if (o.operator == Operators.POW)
			{
				Node simplifiedLeft = simplify (o.getLeft()),
					simplifiedRight = simplify (o.getRight());

				if (simplifiedLeft instanceof NumberNode l && simplifiedRight instanceof NumberNode r)
					return new NumberNode (l.getValue().pow (r.getValue()));
				
				if (simplifiedRight instanceof NumberNode n)
				{
					if (n.getValue().equals (Number.ZERO))
						return new NumberNode (Number.ONE);
					else if (n.getValue().equals (Number.ONE))
						return simplifiedLeft;
				}
				
				if (simplifiedLeft instanceof NumberNode n)
				{
					if (n.getValue().equals (Number.ZERO) || n.getValue().equals (Number.ONE))
						return n;
				}
			}
		}

		return root;
	}

	private static String[] getCase (OperatorNode n, HashMap<Integer, Node> nodes)
	{
		// nodes is used to extract the nodes a, b and c to be used in "construct" later
		// size of nodes = 6, first 3 for numerator, last 3 for denominator
		String[] cases = new String [2];

		if (n.operator == Operators.DIV)
		{
			if (n.getLeft() instanceof OperatorNode o)
			{
				if (o.operator.pri() == 1) // + or -
					cases[0] = "0a1b03c";
				else if (o.operator == Operators.MUL)
					cases[0] = "0a2b03c";
				else if (o.operator == Operators.DIV)
					cases[0] = "0a3b03c";

				
				nodes.put (0, o.getLeft());
				nodes.put (1, o.getRight());
				nodes.put (2, n.getRight());
			}
			
			if (n.getRight() instanceof OperatorNode o)
			{
				if (o.operator == Operators.MUL)
					cases[1] = "a30b2c0";
				else if (o.operator == Operators.DIV)
					cases[1] = "a30b3c0";

				nodes.put (3, n.getLeft());
				nodes.put (4, o.getLeft());
				nodes.put (5, o.getRight());
			}
		}
		else if ((n.operator == Operators.ADD || n.operator == Operators.SUB) 
			&& n.getLeft() instanceof OperatorNode l && l.operator == Operators.DIV
			&& n.getRight() instanceof OperatorNode r && r.operator == Operators.DIV
			&& l.getRight().equals (r.getRight()))
		{
			cases[0] = "a3c1b3c";

			nodes.put (0, l.getLeft());
			nodes.put (1, r.getLeft());
			nodes.put (2, l.getRight());
		}
		else if (n.operator == Operators.MUL)
		{
			if (n.getLeft() instanceof OperatorNode o && o.operator == Operators.DIV)
			{
				cases[0] = "b20a3c0";

				nodes.put (0, o.getLeft());
				nodes.put (1, o.getRight());
				nodes.put (2, n.getRight());
			}
			
			if (n.getRight() instanceof OperatorNode o && o.operator == Operators.DIV)
			{
				cases[1] = "a20b3c0";

				nodes.put (3, n.getLeft());
				nodes.put (4, o.getLeft());
				nodes.put (5, o.getRight());
			}
		}
		
		return cases;
	}

	private static Node simplifyDiv (Node n)
	{
		if (n.getLeft() == null || n.getRight() == null)
			return n;
		
		Node simplifiedLeft = simplify (n.getLeft()), simplifiedRight = simplify (n.getRight());

		if (simplifiedLeft.equals (simplifiedRight))
			return new NumberNode (Number.ONE);

		if (simplifiedLeft instanceof NumberNode nb && nb.getValue().equals (Number.ZERO))
			return nb;

		if (simplifiedRight instanceof NumberNode nb && nb.getValue().equals (Number.ONE))
			return simplifiedLeft;

		OperatorNode newRoot = new OperatorNode (((OperatorNode) n).getOperator(), simplifiedLeft, simplifiedRight);
		if (simplifiedLeft.equals (n.getLeft()) && simplifiedRight.equals (n.getRight()))
			return newRoot;
		
		// if (!(simplifiedLeft instanceof OperatorNode) && !(simplifiedRight instanceof OperatorNode))
		// 	return newRoot;
		return simplify (newRoot);
	}

	private static Node construct (String divCase, HashMap<Integer, Node> nodes, boolean withOffset, boolean isAdd)
	{
		int zeros = 0; // count zeros to check if parentheses or to stop

		Stack<Node> nodesStack = new Stack<>();
		Stack<Operators> ops = new Stack<>();
		
		for (int i = 0; i < divCase.length(); i++)
		{
			char cur = divCase.charAt (i);

			switch (cur)
			{
				case 'a': if (nodes.get (withOffset ? 3 : 0) != null) nodesStack.push (nodes.get (withOffset ? 3 : 0)); break;
				case 'b': if (nodes.get (withOffset ? 4 : 1) != null) nodesStack.push (nodes.get (withOffset ? 4 : 1)); break;
				case 'c': if (nodes.get (withOffset ? 5 : 2) != null) nodesStack.push (nodes.get (withOffset ? 5 : 2)); break;
				case '1':
					while (!ops.empty() && ops.peek() != Operators.OPR && ops.peek().pri() >= Operators.ADD.pri())
					{
						Node right = nodesStack.pop();
						nodesStack.push (new OperatorNode (ops.pop(), nodesStack.pop(), right));
					}
					
					if (isAdd)
						ops.push (Operators.ADD);
					else
						ops.push (Operators.SUB);
					break;
				case '2': 
					while (!ops.empty() && ops.peek() != Operators.OPR && ops.peek().pri() >= Operators.MUL.pri())
					{
						Node right = nodesStack.pop();
						nodesStack.push (new OperatorNode (ops.pop(), nodesStack.pop(), right));
					}
					
					ops.push (Operators.MUL);
					break;
				case '3': 
					while (!ops.empty() && ops.peek() != Operators.OPR && ops.peek().pri() >= Operators.DIV.pri())
					{
						Node right = nodesStack.pop();
						nodesStack.push (new OperatorNode (ops.pop(), nodesStack.pop(), right));
					}
					
					ops.push (Operators.DIV);
					break;
				case '0':
					if (zeros == 0)
					{
						zeros++;
						ops.push (Operators.OPR);
						continue;
					}
					
					while (!ops.empty() && ops.peek() != Operators.OPR)
					{
						Node right = nodesStack.pop();
						nodesStack.push (new OperatorNode (ops.pop(), nodesStack.pop(), right));
					}

					if (!ops.empty() && ops.peek() == Operators.OPR) 
						ops.pop();
					break;
			}
		}

		while (!ops.empty())
		{
			Node right = nodesStack.pop();
			nodesStack.push (new OperatorNode (ops.pop(), nodesStack.pop(), right));
		}
		
		return nodesStack.pop();
	}

	private static int containsNumber (ArrayList<Node> arr)
	{
		for (int i = 0; i < arr.size(); i++)
		{
			if (arr.get (i) instanceof NumberNode)
				return i;
		}

		return -1;
	}

	// finds if the node raised to a power can be found in the array
	private static int indexOfNodeToPower (ArrayList<Node> arr, Node toFind)
	{
		for (int i = 0; i < arr.size(); i++)
		{
			if (arr.get (i) instanceof OperatorNode o && o.operator == Operators.POW && o.getLeft().equals (toFind))
				return i;
		}

		return -1;
	}

	@Override
	public String toString ()
	{
		String s = "";
		boolean openPar = false;
		if (operator == Operators.MUL && (getLeft() instanceof OperatorNode l && (l.getOperator() == Operators.ADD || l.getOperator() == Operators.SUB)))
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

		if (operator == Operators.MUL && (getRight() instanceof OperatorNode r && (r.getOperator() == Operators.ADD || r.getOperator() == Operators.SUB))
			|| operator.pri() == 5)
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
