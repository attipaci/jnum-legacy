/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     crush is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     crush is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with crush.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/

package jnum.math;

public class Offset2D extends Vector2D {
    /**
     * 
     */
    private static final long serialVersionUID = -2072131491069589417L;
    private Coordinate2D reference;
    
    public Offset2D(Coordinate2D reference) {
        this.reference = reference;
    }
    
    public Offset2D(Coordinate2D reference, Vector2D offset) {
        this(reference);
        copy(offset);
    }
    
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Offset2D)) return false;
        Offset2D offset = (Offset2D) o;
        if(!offset.getCoordinateClass().equals(getCoordinateClass())) return false;
        return super.equals(o);
    }
    
    @Override
    public int hashCode() { return super.hashCode() ^ reference.hashCode(); }
    
    public Class<?> getCoordinateClass() { return reference.getClass(); }
    
    public Coordinate2D getReference() { return reference; }
    
}
