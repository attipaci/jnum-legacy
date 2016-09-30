/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     kovacs.util is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     kovacs.util is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with kovacs.util.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
package jnum.io.fits;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import jnum.Unit;
import jnum.Util;
import jnum.text.TextWrapper;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.FitsFactory;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.BufferedDataOutputStream;
import nom.tam.util.Cursor;

// TODO: Auto-generated Javadoc
/**
 * The Class FitsExtras.
 */
public final class FitsToolkit {

	/**
	 * Adds the long hierarch key.
	 *
	 * @param cursor the cursor
	 * @param key the key
	 * @param value the value
	 * @throws HeaderCardException the header card exception
	 */
	public static void addLongHierarchKey(Cursor<String,HeaderCard> cursor, String key, String value) throws HeaderCardException {
		FitsToolkit.addLongHierarchKey(cursor, key, 0, value);
	}

	/**
	 * Gets the abbreviated hierarch key.
	 *
	 * @param key the key
	 * @return the abbreviated hierarch key
	 */
	public static String getAbbreviatedHierarchKey(String key) {
		int max = 66 - MIN_VALUE_LENGTH;
		if(key.length() <= max) return key;
		
		int n = (max - 3) / 2;
		return key.substring(0, n) + "---" + key.substring(key.length() - n, key.length());
	}

	
	// Searches unit the following unit definitions in the comment, in order of priority
	//
	//   1. blah-blah [unitname] blah...
	//   2. blah-blah (unitname) blah...
	/**
	 * Gets the commented unit value.
	 *
	 * @param header the header
	 * @param key the key
	 * @param defaultValue the default value
	 * @param defaultUnitValue the default unit value
	 * @return the commented unit value
	 */
	//   3. unitname blah-blah...
	public static double getCommentedUnitValue(Header header, String key, double defaultValue, double defaultUnitValue) {
		HeaderCard card = header.findCard(key);
		
		double value = header.getDoubleValue(key, defaultValue);
		
		if(card != null) {
			try { value *= getCommentedUnit(card.getComment()).value(); }
			catch(Exception e) { value *= defaultUnitValue; }
			
		}
		
		return value;
	}
	
	
	/**
	 * Gets the commented unit.
	 *
	 * @param comment the comment
	 * @return the commented unit
	 * @throws Exception the exception
	 */
	public static Unit getCommentedUnit(String comment) throws Exception {
		
		// Unit in (the first) square bracketed value; 
		if(comment.contains("[")) {
			int end = comment.indexOf(']');
			if(end < 0) end = comment.length();
			return Unit.get(comment.substring(comment.indexOf('(') + 1, end).trim()); 
		}
		// Unit in (the first) bracketed value; 
		else if(comment.contains("(")) {
			int end = comment.indexOf(')');
			if(end < 0) end = comment.length();
			return Unit.get(comment.substring(comment.indexOf('(') + 1, end).trim());
		}
		// Unit is first word in comment; 
		else {
			StringTokenizer tokens = new StringTokenizer(comment);
			return Unit.get(tokens.nextToken());
		}
	}
	
	/**
	 * Adds the long hierarch key.
	 *
	 * @param cursor the cursor
	 * @param key the key
	 * @param part the part
	 * @param value the value
	 * @throws HeaderCardException the header card exception
	 */
	public static void addLongHierarchKey(Cursor<String, HeaderCard> cursor, String key, int part, String value) throws HeaderCardException {	
		if(FitsFactory.isLongStringsEnabled()) {
		    cursor.add(new HeaderCard("HIERARCH." + key, value, null));
		    return;
		}
	    
	    key = getAbbreviatedHierarchKey(key);
		if(value.length() == 0) value = "true";
		
		String alt = part > 0 ? "." + part : "";
	
		int available = 69 - (key.length() + alt.length() + 3);
	
		if(available < 1) {
			Util.warning(null, "Cannot write FITS key: " + key);
			return;
		}
		
		if(value.length() < available) cursor.add(new HeaderCard("HIERARCH." + key + alt, value, null));
		else { 
			if(alt.length() == 0) {
				part = 1;
				alt = "." + part;
				available -= 2;
			}
	
			cursor.add(new HeaderCard("HIERARCH." + key + alt, value.substring(0, available), null));
			addLongHierarchKey(cursor, key, (char)(part+1), value.substring(available)); 
		}
	}

	/**
	 * Adds the long key.
	 *
	 * @param header the header
	 * @param key the key
	 * @param value the value
	 * @param comment the comment
	 * @throws HeaderCardException the header card exception
	 */
	public static void addLongKey(Header header, String key, String value, String comment) throws HeaderCardException {
		Cursor<String, HeaderCard> cursor = header.iterator();
		while(cursor.hasNext()) cursor.next();
		FitsToolkit.addLongKey(cursor, key, value, comment);
	}

	/**
	 * Gets the long key.
	 *
	 * @param header the header
	 * @param key the key
	 * @return the long key
	 */
	public static String getLongKey(Header header, String key) {
		if(key.length() >= 8) key = key.substring(0, 6) + "-";
	
		String value = header.getStringValue(key);
	
		if(value == null) {
			value = new String();
			char ext = 'A';
			String part;
			do {
				part = header.getStringValue(key + ext);
				if(part != null) value += part;
				ext++;
			} 
			while(part != null);
		}
	
		return value;
	}

	/**
	 * Adds the long key.
	 *
	 * @param cursor the cursor
	 * @param key the key
	 * @param value the value
	 * @param comment the comment
	 * @throws HeaderCardException the header card exception
	 */
	public static void addLongKey(Cursor<String, HeaderCard> cursor, String key, String value, String comment) throws HeaderCardException {
		if(FitsFactory.isLongStringsEnabled()) {
		    cursor.add(new HeaderCard(key, value, comment));
		    return;
		}
	    
	    if(key.length() >= 8) key = key.substring(0, 6) + "-";
		
		final int size = 65 - comment.length();
	
		if(value.length() <= size) {
			cursor.add(new HeaderCard(key, value, comment));
			return;
		}
	
		int start = 0;	
		char ext = 'A';
	
		while(start < value.length()) {
			int end = start + size;
			if(end > value.length()) end = value.length();
	
			cursor.add(new HeaderCard(key + ext, value.substring(start, end), comment));
	
			ext++;
			start = end;
		}
	}
	
	
	public static void addLongKeyConvention(Cursor<String, HeaderCard> cursor) throws HeaderCardException {
	    if(FitsFactory.isLongStringsEnabled()) cursor.add(new HeaderCard("LONGSTRN", "OGIP 1.0", "FITS standard long string convention."));
	    else cursor.add(new HeaderCard("LONGSTRN", "CRUSH", "CRUSH's own long string convention."));
	}
	
	/*
    public static double fitsDouble(double value) {
	if(value < 0.0 && value > -1.0) {
	    DecimalFormat df = new DecimalFormat("0.0000000000000E0");
	    return Double.parseDouble(df.format(value));
	}
	else if(value > 0.0 && value < 1.0) {
	    DecimalFormat df = new DecimalFormat("0.00000000000000E0");
	    return Double.parseDouble(df.format(value));
	}
	else return value;
    }

    public static void addLongFitsKey(Header header, String key, String value, String comment) 
	throws FitsException, HeaderCardException {

	Cursor cursor = header.iterator();
	while(cursor.hasNext()) cursor.next();
	addLongFitsKey(cursor, key, value, comment);
    }

    public static String getLongFitsKey(Header header, String key) {
	String value = header.getStringValue(key);

	if(value == null) {
	    value = new String();
	    char ext = 'A';
	    String part;
	    do {
		part = header.getStringValue(key + ext);
		if(part != null) value += part;
		ext++;
	    } 
	    while(part != null);
	}

	return value;
    }


    public static void addLongFitsKey(Cursor cursor, String key, String value, String comment) 
	throws FitsException, HeaderCardException {

	final int size = 65 - comment.length();

	if(value.length() <= size) {
	    cursor.add(new HeaderCard(key, value, comment));
	    return;
	}

	int start = 0;	
	char ext = 'A';

	while(start < value.length()) {
	    int end = start + size;
	    if(end > value.length()) end = value.length();

	    cursor.add(new HeaderCard(key + ext, value.substring(start, end), comment));

	    ext++;
	    start = end;
	}
    }
	 */
	
	
    /**
	 * Write.
	 *
	 * @param fits the fits
	 * @param fileName the file name
	 * @throws FitsException the fits exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void write(Fits fits, String fileName) throws FitsException, IOException {
	    BufferedDataOutputStream stream = new BufferedDataOutputStream(new FileOutputStream(fileName));
	   
	    try { fits.write(stream); }
	    catch(FitsException e) { throw e; }
	    finally { stream.close(); }   
	}
	
	public static void addHistory(Header header, String history) throws HeaderCardException {
	    // manually wrap long history entries into multiple lines... 
	    if(history.length() <= MAX_HISTORY_LENGTH) header.addLine(new HeaderCard("HISTORY", history, false));
        else {
            TextWrapper wrapper = new TextWrapper(MAX_HISTORY_LENGTH);
            wrapper.setWrapAfterChars(wrapper.getWrapAfterChars() + extraHistoryBreaksAfter);
            StringTokenizer lines = new StringTokenizer(wrapper.wrap(history, 4), "\n");
            addHistory(header, lines.nextToken());
            while(lines.hasMoreTokens()) addHistory(header, "... " + lines.nextToken().substring(4));
        }
       
	}
	
	public static void addHistory(Cursor<String, HeaderCard> cursor, String history) throws HeaderCardException {
	    // manually wrap long history entries into multiple lines... 
	    if(history.length() <= MAX_HISTORY_LENGTH) cursor.add(new HeaderCard("HISTORY", history, false));
	    else {
	        TextWrapper wrapper = new TextWrapper(MAX_HISTORY_LENGTH);
	        wrapper.setWrapAfterChars(wrapper.getWrapAfterChars() + extraHistoryBreaksAfter);
	        StringTokenizer lines = new StringTokenizer(wrapper.wrap(history, 4), "\n");
	        addHistory(cursor, lines.nextToken());
	        while(lines.hasMoreTokens()) addHistory(cursor, "... " + lines.nextToken().substring(4));
	    }
	}

	
	public static String extraHistoryBreaksAfter = "/\\:;_=";
	
	public static final int MIN_VALUE_LENGTH = 5;
	public static final int MAX_HISTORY_LENGTH = 71;

}
