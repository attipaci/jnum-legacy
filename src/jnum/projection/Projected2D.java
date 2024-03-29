/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.projection;

import jnum.Util;
import jnum.math.Coordinate2D;
import jnum.math.Offset2D;


public class Projected2D<CoordinateType extends Coordinate2D> extends Offset2D {

    private static final long serialVersionUID = -5475466255246701534L;

    private Projection2D<CoordinateType> projection;

    public Projected2D(Projection2D<CoordinateType> projection, CoordinateType coords) {
        super(projection.getReference());
        this.projection = projection;
        setCoordinates(coords);
    }

    public Projected2D(Projection2D<CoordinateType> projection) {
        super(projection.getReference());
        this.projection = projection;
    }
    
    /* (non-Javadoc)
     * @see jnum.math.Offset2D#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Projected2D)) return false;
        Projected2D<?> offset = (Projected2D<?>) o;
        if(!Util.equals(offset.projection, projection)) return false;
        return super.equals(o);
    }
    
    /* (non-Javadoc)
     * @see jnum.math.Offset2D#hashCode()
     */
    @Override
    public int hashCode() { return super.hashCode() ^ projection.hashCode(); }
    

    public void setCoordinates(CoordinateType coords) {
        projection.project(coords, this);
    }
    

    public void getCoordinates(CoordinateType toCoords) {
        projection.deproject(this, toCoords);
    }
    

    public final CoordinateType getCoordinates() {
        CoordinateType coords = projection.getCoordinateInstance();
        getCoordinates(coords);
        return coords;
    }
    

    public Projection2D<CoordinateType> getProjection() { return projection; }
    
   
    public Projected2D<CoordinateType> getReprojected(Projection2D<CoordinateType> p, CoordinateType temp) {
        projection.deproject(this, temp);
        return new Projected2D<CoordinateType>(p, temp); // TODO new Vector2D(this); ???
    }
   
    public final Projected2D<CoordinateType> getReprojected(Projection2D<CoordinateType> p) {
        return getReprojected(p, p.getCoordinateInstance());
    }

}
