/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.math;

import java.text.NumberFormat;

import jnum.ExtraMath;



public class PolarVector2D extends Coordinate2D implements Scalable, Inversion, Normalizable {

	private static final long serialVersionUID = 6615579007848120214L;


	public PolarVector2D() { super(); }

	public PolarVector2D(double r, double phi) { super(r, phi); }

	public PolarVector2D(PolarVector2D template) { super(template); }

	
    @Override
    public PolarVector2D copy() {
        return (PolarVector2D) super.copy();
    }
	
	/* (non-Javadoc)
	 * @see jnum.Coordinate2D#isNull()
	 */
	@Override
	public boolean isNull() { return x() == 0.0; }

	@Override
	public final void scale(double value) { scaleX(value); }

	public final void rotate(double angle) { addY(angle); }

	public final double dot(PolarVector2D v) { 
	    if(isNull()) return 0.0;
	    if(v.isNull()) return 0.0;
        return Math.sqrt(x()*x() + v.x()*v.x() - 2.0 * Math.abs(x() * v.x()) * Math.cos(y() - v.y()));    
	}

	public final double norm() { return x() * x(); }

	public final double length() { return Math.abs(x()); }

	public final double angle() { return y(); }

	@Override
	public final void normalize() throws IllegalStateException {
		if(x() == 0.0) throw new IllegalStateException("Null Vector");
		setX(1.0);
	}

	@Override
	public final void invert() { addY(Math.PI); }

	public final void projectOn(PolarVector2D v) {
		setX(dot(v) / v.x());
		setY(v.y());
	}

	public final void reflectOn(PolarVector2D v) {
		setY(2*v.y() - y());
	}
	
	public final void setCartesian(final double x, final double y) {
		set(ExtraMath.hypot(x, y), Math.atan2(y, x));
	}

	public final Vector2D cartesian() { 
		Vector2D v = new Vector2D();
		v.setX(x() * Math.cos(y()));
		v.setY(x() * Math.sin(y()));
		return v;
	}

	public void math(char op, double b) throws IllegalArgumentException {
		switch(op) {
		case '*': scaleX(b); break;
		case '/': scaleX(1.0 / b); break;
		case '^': setX(Math.pow(x(), b)); scaleY(b); break;
		default: throw new IllegalArgumentException("Illegal Operation: "+ op);
		}
	}

	
    public static PolarVector2D[] copyOf(PolarVector2D[] array) {
        PolarVector2D[] copy = new PolarVector2D[array.length];
        for(int i=array.length; --i >= 0; ) if(array[i] != null) copy[i] = array[i].copy();
        return copy;
    }

	@Override
	public String toString(NumberFormat nf) { return "(" + nf.format(x()) + " cis " + nf.format(y()) + ")"; }

	/* (non-Javadoc)
	 * @see jnum.Coordinate2D#toString()
	 */
	@Override
	public String toString() { return "(" + x() + " cis " + y() + ")"; }

}
