/*******************************************************************************
 * Copyright (c) 2015 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/

package jnum.fft;

import jnum.math.Scalable;

// TODO: Auto-generated Javadoc
/**
 * The Interface FourierTransforming.
 */
public interface FourierTransforming {

	/**
	 * Gets the volume count.
	 *
	 * @return the volume count
	 */
	public int getVolumeCount();
	
	/**
	 * Complex transform.
	 *
	 * @param isForward the is forward
	 */
	public void complexTransform(boolean isForward);

	/**
	 * The Interface Real.
	 */
	public interface Real extends FourierTransforming, Scalable {
		
		/**
		 * Real transform.
		 *
		 * @param isForward the is forward
		 */
		public void realTransform(boolean isForward);
		
	}
	
}
