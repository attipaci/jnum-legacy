/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.data.mesh;

import java.text.ParsePosition;

import jnum.Copiable;
import jnum.text.Parser;



public class ObjectMesh<T> extends Mesh<T> {

	private static final long serialVersionUID = 86938797450633242L;


	public ObjectMesh(Class<T> type) {
		super(type);
	}


	public ObjectMesh(Class<T> type, int[] dimensions) {
		super(type, dimensions);
	}


	public ObjectMesh(Object data) {
		super(data);
	}
	
	  /* (non-Javadoc)
     * @see jnum.data.mesh.Mesh#newInstance()
     */
    @Override
    public Mesh<T> newInstance() {
        return new ObjectMesh<T>(elementClass);
    }
	
	/* (non-Javadoc)
	 * @see kovacs.data.AbstractArray#lineElementAt(java.lang.Object, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected T linearElementAt(Object linearArray, int index) {
		return ((T[]) linearArray)[index];
	}

	/* (non-Javadoc)
	 * @see kovacs.data.AbstractArray#setLineElementAt(java.lang.Object, int, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void setLinearElementAt(Object linearArray, int index, T value) {
		((T[]) linearArray)[index] = value;
	}

	/* (non-Javadoc)
	 * @see kovacs.data.AbstractArray#parseElement(java.lang.String)
	 */
	@Override
	public T parseElement(String text) throws ClassCastException, InstantiationException, IllegalAccessException  {
		T value = elementClass.newInstance();
		((Parser) value).parse(text, new ParsePosition(0));
		return value;
	}

    @SuppressWarnings("unchecked")
    public void fill(final T x) {
        if(!(x instanceof Copiable)) throw new IllegalArgumentException("filler value must implement Copiable");
        final MeshCrawler<T> i = iterator(); 
        while(i.hasNext()) i.setNext(((Copiable<T>) x).copy());
    }
    
    @SuppressWarnings("unchecked")
    public void fill(final int[] from, final int[] to, final T x) {
        if(!(x instanceof Copiable)) throw new IllegalArgumentException("filler value must implement Copiable");
        final MeshCrawler<T> i = iterator(from, to);
        while(i.hasNext()) i.setNext(((Copiable<T>) x).copy());
       
    }
   
}
