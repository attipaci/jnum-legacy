/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila[AT]sigmyne.com>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     jnum is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     jnum is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with jnum.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/
// (C)2007 Attila Kovacs <attila[AT]sigmyne.com>

package jnum.math.matrix;

import jnum.Copiable;
import jnum.data.ArrayUtil;
import jnum.math.AbsoluteValue;
import jnum.math.AbstractAlgebra;
import jnum.math.LinearAlgebra;
import jnum.math.Metric;



// TODO: Auto-generated Javadoc
// TODO Overwrite all methods from Matrix that are not fully supported by square-matrices...

@SuppressWarnings("unchecked")
public class GenericSquareMatrix<T extends LinearAlgebra<? super T> & AbstractAlgebra<? super T> & Metric<? super T> & AbsoluteValue & Copiable<? super T>> extends GenericMatrix<T> implements SquareMatrixAlgebra<T> {

	private static final long serialVersionUID = 276116236966727928L;


	public GenericSquareMatrix(Class<T> type) {
		super(type);
	}
	

	public GenericSquareMatrix(T[][] a) throws IllegalArgumentException {
		super(a);
	}


	public GenericSquareMatrix(Class<T> type, int size) {
		super(type, size, size);
	}
	

	public GenericSquareMatrix(GenericMatrix<T> a) {
		entry = a.entry;
		validate();
	}

	/* (non-Javadoc)
	 * @see kovacs.math.GenericMatrix#checkShape()
	 */
	@Override
	public void checkShape() throws IllegalStateException{
		if(rows() != cols()) throw new IllegalStateException(getDimensionString() + " is not a square matrix!");
		super.checkShape();
	}
	

	public void addRows(GenericMatrix<? extends T> b) {
		throw new UnsupportedOperationException("Cannot add rows to a " + getClass().getSimpleName());
	}
	

	public void addColumns(GenericMatrix<? extends T> b) {
		throw new UnsupportedOperationException("Cannot add columns to a " + getClass().getSimpleName());
	}
	

	public GenericSquareMatrix<T> getInverse() {
		return getLUInverse();
	}
	
	// Invert via Gauss-Jordan elimination
	public GenericSquareMatrix<T> getGaussInverse() {
		int size = size();
		GenericMatrix<T> combo = new GenericMatrix<T>(type, size, 2*size);
		for(int i=size; --i >= 0; ) combo.entry[i][i+size].setIdentity();
		combo.paste(this, 0, 0);
		combo.gaussJordan();
		GenericSquareMatrix<T> inverse = new GenericSquareMatrix<T>((T[][]) ArrayUtil.subArray(combo.entry, new int[] { 0, size }, new int[] { size, 2*size }));
		return inverse;
	}
	

	public GenericSquareMatrix<T> getLUInverse() {
		return new GenericLUDecomposition<T>(this).getInverse();
	}
	

	public void solve(GenericMatrix<T> inputVectors) {
		inputVectors.entry = getSolutionsTo(inputVectors.entry);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.SquareMatrixAlgebra#solve(kovacs.math.AbstractVector[])
	 */
	@Override
	public void solve(AbstractVector<T>[] inputVectors) {
		int size = size();
		GenericMatrix<T> combo = new GenericMatrix<T>(type, size, size + inputVectors.length);
		combo.paste(this, 0, 0);
		
		for(int col=inputVectors.length; --col >= 0; ) {
			AbstractVector<T> v = inputVectors[col];
			for(int row=size; --row >= 0; ) combo.setValue(row, size + col, v.getComponent(row));
		}

		combo.gaussJordan();
		
		for(int col=inputVectors.length; --col >= 0; ) {
			AbstractVector<T> v = inputVectors[col];
			for(int row=size; --row >= 0; ) v.setComponent(row, combo.getValue(row, size + col));
		}
	}
	

	public T[][] getSolutionsTo(T[][] inputMatrix) {
		int size = size();
		GenericMatrix<T> combo = new GenericMatrix<T>(type, size, size + inputMatrix[0].length);
		combo.paste(this, 0, 0);
		ArrayUtil.paste(inputMatrix, entry, new int[] { 0, size });
		combo.gaussJordan();
		return (T[][]) ArrayUtil.subArray(combo.entry, new int[] { 0, size }, new int[] { size, combo.cols() });
	}
	
	
	
	/* (non-Javadoc)
	 * @see kovacs.math.Inversion#invert()
	 */
	@Override
	public void invert() {
		entry = getInverse().entry;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.SquareMatrixAlgebra#size()
	 */
	@Override
	public final int size() { return rows(); }

	/* (non-Javadoc)
	 * @see kovacs.math.GenericMatrix#setIdentity()
	 */
	@Override
	public void setIdentity() {
		zero();
		for(int i=entry.length; --i >= 0; ) entry[i][i].setIdentity();
	}
	
	// indx is the row permutation, returns true/false for even/odd row exchanges...
	protected boolean decomposeLU(int[] index) { return decomposeLU(index, 1e-20); }
	

	protected boolean decomposeLU(int[] index, double tinyValue) {
		final int n = size();

		double[] v = new double[n];
		boolean evenChanges = true;
		
		T product = newEntry();
		
		for(int i=n; --i >= 0; ) {
			double big = 0.0;
			for(int j=n; --j >= 0; ) {
				final double tmp = entry[i][j].abs();
				if(tmp > big) big = tmp;
			}
			if(big == 0.0) throw new IllegalStateException("Singular matrix in LU decomposition.");
			v[i] = 1.0 / big;
		}
		for(int j=0; j<n; j++ ) {
			int imax = -1;
			
			for(int i=j; --i >= 0; ) {
				T sum = (T) entry[i][j].copy();
				for(int k=i; --k >= 0; ) {
					product.setProduct(entry[i][k], entry[k][j]);
					sum.subtract(product);
				}
				entry[i][j] = sum;
			}
			double big = 0.0;
			for(int i=n; --i >= j; ) {
				T sum = (T) entry[i][j].copy();
				for(int k=j; --k >= 0; ) {
					product.setProduct(entry[i][k], entry[k][j]);
					sum.subtract(product);
				}
				entry[i][j] = sum;
				final double tmp = v[i] * sum.abs();
				if (tmp >= big) {
					big=tmp;
					imax=i;
				}
			}
			if(j != imax) {
				for(int k=n; --k >= 0; ) {
					T tmp = entry[imax][k];
					entry[imax][k] = entry[j][k];
					entry[j][k] = tmp;
				}
				evenChanges = !evenChanges;
				v[imax] = v[j];
			}
			index[j] = imax;
			
			T diag = entry[j][j];
			
			if(diag.isNull()) {
				diag.setIdentity();
				diag.scale(tinyValue);
			}
			
			if(j != n-1) {
				T tmp = (T) diag.getInverse();
				for(int i=n; --i > j; ) entry[i][j].multiplyBy(tmp);
			}
		}
		return evenChanges;
	}
	
	
}
