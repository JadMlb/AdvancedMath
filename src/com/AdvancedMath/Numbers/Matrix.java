package com.AdvancedMath.Numbers;

/**
 * Class that represents matrices and vectors
 */
public class Matrix
{
	private Number[][] data;

	/**
	 * Creates an empty {@code Matrix} of dimensions rows*cols
	 * 
	 * @param row The number of rows
	 * @param col The number of columns
	 * @throws IllegalArgumentException if any of the arguments is 0
	 */
	public Matrix (int row, int col) 
	{
		if (row == 0 || col == 0)
			throw new IllegalArgumentException ("Cannot create a matrix with dimensions 0");
		data = new Number [row][col];
	}

	/**
	 * Creates a {@code Matrix} with the given 2-D array
	 * 
	 * @param data The 2-D array to turn into a {@code Matrix}
	 */
	public Matrix (Number[][] data)
	{
		this.data = data;
	}

	/**
	 * Turns an array of {@code Number}s into a Matrix and distributes those elements according to {@code columnWidth}, so it takes the closest multiple of {@code columnWidth} of elements from {@code data}.
	 * If the size of {@code data} is less than {@code columnWidth}, a row vector with the size of {@code data} is returned.
	 * 
	 * @param data The array of {@code Number}s to be converted into a {@code Matrix}
	 * @param columnWidth The width of the matrix, i.e., the number of elements per row
	 */
	public Matrix (Number[] data, int columnWidth)
	{
		if (data.length < columnWidth)
			this.data = new Number [1][data.length];
		else
			this.data = new Number [data.length / columnWidth][columnWidth];

		for (int i = 0; i < getRowCount(); i++)
			for (int j = 0; j < getColCount(); j++)
				this.data[i][j] = data[i * columnWidth + j];
	}

	/**
	 * Creates a diagonal matrix from the values provided
	 * 
	 * @param numbers Array of numbers to be put on the diagonal
	 * @return A diagonal matrix with the provided values on the diagonal
	 * @throws IllegalArgumentException if the array is null or of size 0
	 */
	public static Matrix diagonal (Number[] numbers)
	{
		if (numbers == null || numbers.length == 0)
			throw new IllegalArgumentException ("Cannot create a matrix with dimensions 0");
		
		Matrix m = new Matrix (numbers.length, numbers.length);
		for (int i = 0; i < numbers.length; i++)
			for (int j = 0; j < numbers.length; j++)
				if (i == j)
					m.setValueAt (i, i, numbers[i]);
				else
					m.setValueAt (i, j, 0.0, 0.0);

		return m;
	}

	/**
	 * Creates an identity matrix of size {@code size}, i.e., a matrix with 1 on the diagonal and 0 everywhere else 
	 * 
	 * @param size The dimensions of the identity matrix
	 * @return An identity matrix
	 * @throws IllegalArgumentException if size is 0
	 */
	public static Matrix identity (int size)
	{
		if (size == 0)
			throw new IllegalArgumentException ("Cannot create a matrix with dimensions 0");

		Matrix m = new Matrix (size, size);
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				if (i == j)
					m.setValueAt (i, i, 1.0, 0.0);
				else
					m.setValueAt (i, j, 0.0, 0.0);

		return m;
	}

	/**
	 * Creates a matrix of diemnsions rows * columns, with 0 everywhere
	 * 
	 * @param rows The number of rows
	 * @param cols The number of columns
	 * @return A matrix with 0 everywhere
	 * @throws IllegalArgumentException if any of the dimensions is 0
	 */
	public static Matrix zeros (int rows, int cols)
	{
		if (rows == 0 || cols == 0)
			throw new IllegalArgumentException ("Cannot create a matrix with dimensions 0");

		Matrix m = new Matrix (rows, cols);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				m.setValueAt (i, j, Number.ZERO);

		return m;
	}
	
	/**
	 * Creates a matrix of dimensions {@code rows} * {@code cols} with 1 everywhere
	 * 
	 * @param rows The number of rows 
	 * @param cols The number of columns 
	 * @return A matrix filled with 1 everywhere
	 * @throws IllegalArgumentException if any of the dimensions is 0
	 */
	public static Matrix ones (int rows, int cols)
	{
		if (rows == 0 || cols == 0)
			throw new IllegalArgumentException ("Cannot create a matrix with dimensions 0");

		Matrix m = new Matrix (rows, cols);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				m.setValueAt (i, j, 1.0, 0.0);

		return m;
	}

	/**
	 * Creates a column vector, i.e., a matrix with number of columns = 1 and number of rows = size of {@code numbers}	
	 * 
	 * @param numbers The array to be made into a column vector
	 * @return The column vector of {@code numbers}
	 * @throws IllegalArgumentException if {@code numbers} is null or empty
	 */
	public static Matrix columnVector (Number[] numbers)
	{
		if (numbers == null || numbers.length == 0)
			throw new IllegalArgumentException ("The given array of numbers is empty. Cannot create a column vector from an empty array");

		Matrix m = new Matrix (numbers.length, 1);
		for (int i = 0; i < numbers.length; i++)
			m.setValueAt (i, 0, numbers[i]);
		return m;
	}
	
	/**
	 * Creates a row vector, i.e., a matrix with number of rows = 1 and number of columns = size of {@code numbers}	
	 * 
	 * @param numbers The array to be made into a row vector
	 * @return The row vector of {@code numbers}
	 * @throws IllegalArgumentException if {@code numbers} is null or empty
	 */
	public static Matrix rowVector (Number[] numbers)
	{
		if (numbers == null || numbers.length == 0)
			throw new IllegalArgumentException ("The given array of numbers is empty. Cannot create a row vector from an empty array");

		Matrix m = new Matrix (1, numbers.length);
		for (int i = 0; i < numbers.length; i++)
			m.setValueAt (0, i, numbers[i]);
		return m;
	}

	/**
	 * Sets the value at {@code row}, {@code col} of the matrix to the number {@code valueRealPart} + i * {@code valueImaginaryPart}
	 * 
	 * @param row The row of the cell we want to change its value
	 * @param col The column of the cell we want to change its value
	 * @param valueRealPart The real part of the new value
	 * @param valueImaginaryPart The imaginary part of the real value
	 */
	public void setValueAt (int row, int col, Double valueRealPart, Double valueImaginaryPart)
	{
		data[row][col] = new Number (valueRealPart, valueImaginaryPart);
	}

	/**
	 * Sets the value at {@code row}, {@code col} of the matrix to the number {@code n}
	 * 
	 * @param row The row of the cell we want to change its value
	 * @param col The column of the cell we want to change its value
	 * @param n The new value
	 */
	public void setValueAt (int row, int col, Number n)
	{
		data[row][col] = n;
	}

	/**
	 * Gets the value at {@code row}, {@code col} of the matrix
	 * 
	 * @param row The row of the cell we want to change its value
	 * @param col The column of the cell we want to change its value
	 * @return The value of the cell at those coordinates
	 */
	public Number getValueAt (int row, int col)
	{
		return data[row][col];
	}

	/**
	 * Gets the number of rows of the matrix
	 * 
	 * @return The number of rows
	 */
	public int getRowCount ()
	{
		return data.length;
	}

	/**
	 * Gets the number of columns of the matrix
	 * 
	 * @return The number of columns
	 */
	public int getColCount ()
	{
		return data[0].length;
	}

	/**
	 * Returns a row vector equal to the specified row
	 * 
	 * @param row The index of the row we want to get
	 * @return The row with the coordinate specified
	 */
	public Matrix getRow (int row)
	{
		return Matrix.rowVector (data[row]);
	}

	/**
	 * Returns a column vector equal to the specified column
	 * 
	 * @param col The index of the column we want to get
	 * @return The column with the coordinate specified
	 */
	public Matrix getColumn (int col)
	{
		Matrix c = new Matrix (getRowCount(), 1);

		for (int i = 0; i < getRowCount(); i++)
			c.setValueAt (i, 0, data[i][col]);

		return c;
	}
	
	/**
	 * Sets the values whose row number is equal to {@code row} to the new ones from {@code m} respectively, multiplied by {@code scale}
	 * 
	 * @param row The index of the row to be changed
	 * @param m A row vector containing the new values
	 * @param scale The multiplier of the new values
	 * @throws IllegalArgumentException if {@code m} is not a row vector, or if its size is not equal to the column count of this matrix
	 */
	public void setRow (int row, Matrix m, Number scale)
	{
		if (!m.isRowVector())
			throw new IllegalArgumentException ("The new 'row' must be a row vector");

		if (m.getColCount() != getColCount())
			throw new IllegalArgumentException ("The new row must have the same column count as the matrix");

		for (int i = 0; i < getColCount(); i++)
			data[row][i] = m.getValueAt(0, i).multiply (scale);
	}

	/**
	 * Sets the values whose column number is equal to {@code col} to the new ones from {@code m} respectively, multiplied by {@code scale}
	 * 
	 * @param col The index of the column to be changed
	 * @param m A column vector containing the new values
	 * @param scale The multiplier of the new values
	 * @throws IllegalArgumentException if {@code m} is not a column vector, or if its size is not equal to the row count of this matrix
	 */
	public void setColumn (int col, Matrix m, Number scale)
	{
		if (!m.isColumnVector())
			throw new IllegalArgumentException ("The new 'column' must be a column vector");
			
		if (m.getRowCount() != getRowCount())
			throw new IllegalArgumentException ("The new column must have the same row count as the matrix");

		for (int i = 0; i < getRowCount(); i++)
			data[i][col] = m.getValueAt(i, 0).multiply (scale);
	}
	
	/**
	 * Sets the values whose row number is equal to {@code row} to the new ones from {@code newRow} respectively, multiplied by {@code scale}
	 * 
	 * @param row The index of the row to be changed
	 * @param newRow An array containing the new values
	 * @param scale The multiplier of the new values
	 * @throws IllegalArgumentException if the size of {@code newRow} is not equal to the column count of this matrix
	 */
	public void setRow (int row, Number[] newRow, Number scale)
	{
		if (newRow.length != getColCount())
			throw new IllegalArgumentException ("The new row must have the same column count as the matrix");

		if (scale == null)
			scale = Number.ONE;

		for (int i = 0; i < getColCount(); i++)
			data[row][i] = newRow[0].multiply (scale);
	}

	/**
	 * Sets the values whose row number is equal to {@code col} to the new ones from {@code newCol} respectively, multiplied by {@code scale}
	 * 
	 * @param col The index of the column to be changed
	 * @param newCol An array containing the new values
	 * @param scale The multiplier of the new values
	 * @throws IllegalArgumentException if the size of {@code newCol} is not equal to the column count of this matrix
	 */
	public void setColumn (int col, Number[] newCol, Number scale)
	{
		if (newCol.length != getRowCount())
			throw new IllegalArgumentException ("The new column must have the same row count as the matrix");

		if (scale == null)
			scale = Number.ONE;

		for (int i = 0; i < getRowCount(); i++)
			data[i][col] = newCol[i];
	}

	/**
	 * Checks if this matrix is a column vector, i.e., checks if the number of rows is equal to 1 and the number of columns is greater than 1
	 * 
	 * @return {@code true} if this matrix is a row vector, {@code false otherwise}
	 */
	public boolean isRowVector ()
	{
		return getRowCount() == 1 && getColCount() > 1;
	}
	
	/**
	 * Checks if this matrix is a column vector, i.e., checks if the number of columns is equal to 1 and the number of rows is greater than 1
	 * 
	 * @return {@code true} if this matrix is a column vector, {@code false otherwise}
	 */
	
	public boolean isColumnVector ()
	{
		return getColCount() == 1 && getRowCount() > 1;
	}

	/**
	 * Checks if this matrix is square, i.e., checks if the number of columns is equal to the number of rows
	 * 
	 * @return {@code true} if this matrix is square, {@code false otherwise}
	 */
	
	public boolean isSquare ()
	{
		return getColCount() == getRowCount();
	}

	/**
	 * Swaps two rows specified by their indeces
	 * 
	 * @param row1 The index of the first row
	 * @param row2 The index of the second row
	 */
	public void swapRows (int row1, int row2)
	{
		Number[] tmp = data[row1];
		data[row1] = data[row2];
		data[row2] = tmp;
	}

	/**
	 * Swaps two columns specified by their indeces
	 * 
	 * @param col1 The index of the first column
	 * @param col2 The index of the second column
	 */
	public void swapColumns (int col1, int col2)
	{
		for (int i = 0; i < getRowCount(); i++)
		{
			Number tmp = data[i][col1];
			data[i][col1] = data[i][col2];
			data[i][col2] = tmp;
		}
	}

	/**
	 * Gets the elements on the diagonal of this matrix. Always starts from 0,0.
	 * 
	 * @return An array of the numbers on the diagonal
	 */
	public Number[] getDiagonal ()
	{
		Number[] diag = new Number [Math.min (getColCount(), getRowCount())];
		for (int i = 0; i < diag.length; i++)
			diag[i] = data[i][i];

		return diag;
	}

	/**
	 * Transposes this matrix, i.e., interchanges the columns into rows and vice versa
	 * 
	 * @return The transpose if this matrix
	 */
	public Matrix transpose ()
	{
		Matrix res = new Matrix (getColCount(), getRowCount());

		for (int i = 0; i < getRowCount(); i++)
			for (int j = 0; j < getColCount(); j++)
				res.setValueAt (j, i, data[i][j]);

		return res;
	}

	/**
	 * Creates a sub matrix by removing the specified row and column from the original. If either of {@code row} or {@code col} is equal to -1, only a column, or row wil be removed
	 * 
	 * @param row The row to be removed
	 * @param col The column to be removed
	 * @return The sub matrix, equal to the parent matrix, without the given rows and columns
	 */
	public Matrix subMatrix (int row, int col)
	{
		Matrix m = new Matrix (getRowCount() - 1, getColCount() - 1);

		int r = 0, c = 0;
		for (int i = 0; i < getRowCount(); i++)
		{
			if (i == row)
				continue;

			c = 0;
			for (int j = 0; j < getColCount(); j++)
			{
				if (j == col)
					continue;

				m.setValueAt (r, c, data[i][j]);
				c++;
			}
			r++;
		}

		return m;
	}

	/**
	 * Calculates the determinant of this matrix using the cofactor method
	 * 
	 * @return The determinant of the matrix
	 * @throws IllegalArgumentException if the matrix is not square
	 * @see Matrix#isSquare()
	 * @see Matrix#subMatrix(int, int)
	 */
	public Number determinant ()
	{
		if (!isSquare())
			throw new IllegalArgumentException ("Cannot compute the determinant of a non square matrix");

		if (getRowCount() == 2)
			return data[0][0].multiply(data[1][1]).subtract (data[0][1].multiply (data[1][0]));

		Number det = Number.ZERO;
		Matrix row = getRow (0);
		for (int i = 0; i < row.getColCount(); i++)
			// det = det.add (Number.ONE.negate().pow(Number.real ((double) i)).multiply(row.getValueAt (0, i)).multiply (subMatrix(0, i).determinant()));
			det = det.add (Number.ONE.negate().pow(i).multiply(row.getValueAt (0, i)).multiply (subMatrix(0, i).determinant()));
		
		return det;
	}

	/**
	 * Adds two matrices by adding each element with its corresponding element from the other matrix
	 * 
	 * @param m The matrix to add to the current one
	 * @return The result of the addition
	 * @throws IllegalArgumentException if the matrices don't have the same dimensions
	 */
	public Matrix add (Matrix m)
	{
		if (this.getRowCount() != m.getRowCount() || this.getColCount() != m.getColCount())
			throw new IllegalArgumentException ("Only matrices with the same dimensions can be added");

		Matrix res = new Matrix (getRowCount(), getColCount());
		for (int i = 0; i < getRowCount(); i++)
			for (int j = 0; j < getColCount(); j++)
				res.setValueAt (i, j, data[i][j].add (m.getValueAt (i, j)));

		return res;
	}
	
	/**
	 * Subtracts two matrices by subtracting each element with its corresponding element from the other matrix
	 * 
	 * @param m The matrix to subtract from the current one
	 * @return The result of the subtraction
	 * @throws IllegalArgumentException if the matrices don't have the same dimensions
	 */
	public Matrix sub (Matrix m)
	{
		if (this.getRowCount() != m.getRowCount() || this.getColCount() != m.getColCount())
			throw new IllegalArgumentException ("Only matrices with the same dimensions can be subtracted");

		Matrix res = new Matrix (getRowCount(), getColCount());
		for (int i = 0; i < getRowCount(); i++)
			for (int j = 0; j < getColCount(); j++)
				res.setValueAt (i, j, data[i][j].subtract (m.getValueAt (i, j)));

		return res;
	}

	/**
	 * Computes the dot product of 2 vectors
	 * 
	 * <p><strong>Note</strong> that the first vector must be a row vector while the second a column vector
	 * 
	 * @param v1 a row vector
	 * @param v2 a column vector
	 * @return the dot product of v1 and v2
	 * @throws IllegalArgumentException if v1 or v2 are matrices, or if v1 is a column vector, or if both are vectors of the same type (row or column)
	 * @see Matrix#transpose()
	 */
	public static Number vectDot (Matrix v1, Matrix v2)
	{
		if (v1.isColumnVector() || v2.isRowVector())
			throw new IllegalArgumentException ("The first argument must be a row vector and the second a column vector");
		
		if (v1.getRowCount() > 1 && v1.getColCount() > 1 || v2.getRowCount() > 1 && v2.getColCount() > 1)
			throw new IllegalArgumentException ("Provided arguments are matrices, they need to be vectors. For matrix multiplication, use Matrix.multiply");

		Number res = Number.ZERO;
		for (int i = 0; i < v1.getColCount(); i++)
			res = res.add (v1.getValueAt(0, i).multiply (v2.getValueAt (i, 0)));

		return res;
	}

	/**
	 * Multiplies all of the elements of this matrix by {@code n}
	 * 
	 * @param n The number to scale the elements of this matrix by
	 * @return The result of the multiplication
	 */
	public Matrix multiply (Number n)
	{
		if (n.equals (Number.ZERO))
			return Matrix.zeros (getRowCount(), getColCount());

		if (n.equals (Number.ONE))
			return this;

		Matrix res = new Matrix (getRowCount(), getColCount());
		
		for (int i = 0; i < getRowCount(); i++)
			for (int j = 0; j < getColCount(); j++)
				res.setValueAt (i, j, data[i][j].multiply (n));

		return res;
	}

	/**
	 * Multiplies two matrices as this x n
	 * 
	 * @param m The matrix to multiply with
	 * @return The result of the multiplication
	 */
	public Matrix multiply (Matrix m)
	{
		if (getColCount() != m.getRowCount())
			throw new IllegalArgumentException ("The provided matrix must have the same number of rows as the number of columns of the first one");
		
		Matrix res = new Matrix (getRowCount(), m.getColCount());

		for (int i = 0; i < getRowCount(); i++)
			for (int j = 0; j < m.getColCount(); j++)
				res.setValueAt (i, j, vectDot (this.getRow (i), m.getColumn (j)));
		
		return res;
	}

	/**
	 * Checks if this matrix is equal to another object, if and only if {@code o} is a {@code Matrix} with the same values as this
	 */
	@Override
	public boolean equals (Object o)
	{
		if (this == o)
			return true;

		if (o instanceof Matrix m)
		{
			if (getRowCount() != m.getRowCount() && getColCount() != m.getColCount())
				return false;

			for (int i = 0; i < getRowCount(); i++)
				for (int j = 0; j < getColCount(); j++)
					if (!data[i][j].equals (m.data[i][j]))
						return false;
			return true;
		}
		
		return false;
	}

	@Override
	public String toString ()
	{
		String s = "[\n";
		for (int i = 0; i < getRowCount(); i++)
		{
			for (int j = 0; j < getColCount(); j++)
				s += data[i][j] + "\t";
			s += "\n";
		}
		return s + "]";
	}
}
