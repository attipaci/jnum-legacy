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
// Copyright (c) 2007 Attila Kovacs 

package jnum.plot;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

import jnum.plot.colorscheme.BBLike;
import jnum.plot.colorscheme.Colorful;
import jnum.plot.colorscheme.CoolBlue;
import jnum.plot.colorscheme.DayTime;
import jnum.plot.colorscheme.Doppler;
import jnum.plot.colorscheme.Glacier;
import jnum.plot.colorscheme.GreyScale;
import jnum.plot.colorscheme.NightTime;
import jnum.plot.colorscheme.Orangy;
import jnum.plot.colorscheme.Rainbow;
import jnum.plot.colorscheme.Temperature;



public abstract class ColorScheme {

	public int noData = Color.TRANSLUCENT;

	public ColorScheme() {}
	

	public abstract int getRGB(double scaledintensity);


	public abstract Color getHighlight();
	

	public static ColorScheme getInstanceFor(String name) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		name = name.toLowerCase();
		return schemes.containsKey(name) ? schemes.get(name).getConstructor().newInstance() : null;
	}

	public static Hashtable<String, Class<? extends ColorScheme>> schemes = new Hashtable<String, Class<? extends ColorScheme>>();
	

	public final static int getRGB(final int r, final int g, final int b) {
		return 0xFF000000 | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
	}

	public final static int getRGB(final float r, final float g, final float b) {
		final int R = (Math.min(255, (int)Math.floor(256.0F * r)));
		final int G = (Math.min(255, (int)Math.floor(256.0F * g)));
		final int B = (Math.min(255, (int)Math.floor(256.0F * b)));
		return getRGB(R, G, B);
	}
	static {
		schemes.put("grayscale", GreyScale.class);
		schemes.put("greyscale", GreyScale.class);
		schemes.put("gray", GreyScale.class);
		schemes.put("grey", GreyScale.class);
		
		schemes.put("colorful", Colorful.class);
		schemes.put("color", Colorful.class);
		
		schemes.put("bblike", BBLike.class);
		schemes.put("bb", BBLike.class);
		
		schemes.put("daytime", DayTime.class);
		schemes.put("day", DayTime.class);
		schemes.put("hot", DayTime.class);
		
		schemes.put("nighttime", NightTime.class);
		schemes.put("night", NightTime.class);
		
		schemes.put("doppler", Doppler.class);
	
		schemes.put("glacier", Glacier.class);
		schemes.put("ice", Glacier.class);
		schemes.put("icy", Glacier.class);
		schemes.put("cold", Glacier.class);
		
		schemes.put("rainbow", Rainbow.class);
		
		schemes.put("temperature", Temperature.class);
		schemes.put("heat", Temperature.class);
		
		schemes.put("blue", CoolBlue.class);
		
		schemes.put("orange", Orangy.class);
	}
}

