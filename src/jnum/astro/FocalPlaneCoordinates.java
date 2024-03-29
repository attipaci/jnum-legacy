/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
 * All rights reserved. 
 * 
 * This file is part of crush.
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.astro;

import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;
import jnum.fits.FitsToolkit;
import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.math.SphericalCoordinates;
import jnum.text.GreekLetter;


public class FocalPlaneCoordinates extends SphericalCoordinates {

	private static final long serialVersionUID = 6324566580599103464L;
		

	public FocalPlaneCoordinates() {}


	public FocalPlaneCoordinates(String text) { super(text); } 


	public FocalPlaneCoordinates(double x, double y) { super(x, y); }

	/* (non-Javadoc)
	 * @see jnum.math.SphericalCoordinates#getFITSLongitudeStem()
	 */
	@Override
	public String getFITSLongitudeStem() { return "FLON"; }
	
	/* (non-Javadoc)
	 * @see jnum.math.SphericalCoordinates#getFITSLatitudeStem()
	 */
	@Override
	public String getFITSLatitudeStem() { return "FLAT"; }
	
	
	@Override
    public String getTwoLetterCode() { return "FP"; }
	
	@Override
    public CoordinateSystem getCoordinateSystem() {
        return defaultCoordinateSystem;
    }
     
    @Override
    public CoordinateSystem getLocalCoordinateSystem() {
        return defaultLocalCoordinateSystem;
    }
    
	/* (non-Javadoc)
	 * @see kovacs.math.SphericalCoordinates#edit(nom.tam.util.Cursor, java.lang.String)
	 */
	@Override
	public void editHeader(Header header, String keyStem, String alt) throws HeaderCardException {	
		super.editHeader(header, keyStem, alt);	

        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
		c.add(new HeaderCard("WCSNAME" + alt, getCoordinateSystem().getName(), "coordinate system description."));
	}
	
	
    @SuppressWarnings("hiding")
    public static CoordinateSystem defaultCoordinateSystem, defaultLocalCoordinateSystem;
		
    
    static {  
        CoordinateAxis xAxis = createAxis("Focal-plane X", "X", "X", af);  
        CoordinateAxis yAxis = createAxis("Focal-plane Y", "Y", "Y", af);
        
        defaultCoordinateSystem = new CoordinateSystem("Focal Plane Coordinates");
        defaultCoordinateSystem.add(xAxis);
        defaultCoordinateSystem.add(yAxis);
        
        CoordinateAxis xOffsetAxis = createOffsetAxis("Focal-plane dX", "dX", GreekLetter.Delta + " X");
        CoordinateAxis yOffsetAxis = createOffsetAxis("Focal-plane dY", "dY", GreekLetter.Delta + " Y");
        
        defaultLocalCoordinateSystem = new CoordinateSystem("Focal Plane Offsets");
        defaultLocalCoordinateSystem.add(xOffsetAxis);
        defaultLocalCoordinateSystem.add(yOffsetAxis);

    }

	
}
