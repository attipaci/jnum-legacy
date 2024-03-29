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

package jnum.util;

import java.io.Serializable;


/**
 * The Class representing a bitwise flag in a flag-space of an integer-type container. The flag has a unique letter
 * code identifying it in the flag-space it belongs to, and has a human-readable descriptive name. These allow
 * for human tracking, if desired, of which flags have been set.
 * <p>
 * 
 * For more basic bit-wise flagging, see the {@link jnum.Flagging} interface.
 * 
 * @param <Type> the generic integer type supporting this flag space.
 * 
 * @author Attila Kovacs <attila@sigmyne.com>
 * 
 * @see jnum.Flagging
 */
public abstract class Flag<Type extends Number> implements Serializable {
    
    private static final long serialVersionUID = 6666953592077360485L;
    
    private FlagSpace<Type> space;
    
    /** The letter code that uniquely identifies this flag in a given {@link FlagSpace}. */
    private char letterCode;
    
    /** The descriptive name of this flag. */
    private String name;
    
    /**
     * Instantiates a new bitwise flag, and adds it to the specified flag space. The flag will be identified with a
     * unique (in the flag space) letter code, and will have the specified human-readable descriptive name.
     *
     * @param space the flag-space (container) in which the flag lives.
     * @param value the integer value of the flag. It should have exactly one non-zero bit, i.e. equal to 2^p.
     * @param letterCode a unique letter code in the flag-space identifyng this flag
     * @param name a descriptive human-readable name for the flag.
     * @throws IllegalArgumentException if the flag value is not a single bit in the flag space...
     * @throws FlagConflictException the flag conflict exception
     */
    private Flag(FlagSpace<Type> space, Type value, char letterCode, String name) throws IllegalArgumentException {
        this.space = space;
        setValue(checkValue(value, space));
        this.letterCode = letterCode;
        this.name = name;
        
        space.put(this);
    }
    
    /**
     * Checks the flag value to ensure it sets only one bit, and in the specified flag-space...
     *
     * @param value the flag-space in which the value will exist.
     * @param space the space
     * @return the type
     * @throws IllegalArgumentException if the number of set bits is not exactly one, or not inside the flag-space.
     */
    private Type checkValue(Type value, FlagSpace<Type> space) throws IllegalArgumentException {
        long lvalue = value.longValue();
        
        if(lvalue == 0L) throw new IllegalArgumentException("null flag value.");
        
        int bits = 0;
        
        for(int i=space.getBits(); --i >= 0; ) {
            if((lvalue & (1L<<i)) != 0) bits++;
            if(bits > 1) throw new IllegalArgumentException("flag value specifies more than one bit: " + toHexString());
        }     
        
        if((lvalue & space.getMask()) == 0) 
            throw new IllegalArgumentException("flag value outside of flag-space: " + toHexString() + "in " + space.getName() + ": " + toHexString());
        return value;
    }
   
    /**
     * Sets the value.
     *
     * @param value the new value
     */
    protected abstract void setValue(Type value); 
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Flag)) return false;
        
        Flag<?> f = (Flag<?>) o;
        if(f.value() != value()) return false;
        if(f.letterCode != letterCode) return false;
        return true;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() { return super.hashCode() ^ HashCode.from(value().longValue()) ^ HashCode.from(letterCode); }
    
    /**
     * Gets the flag space to which this flag belongs to.
     *
     * @return the flag space
     */
    public final FlagSpace<Type> getFlagSpace() { return space; }
    
    /**
     * Gets the integer value associated with this flag, which has exactly one bit set.
     *
     * @return the flag value
     */
    public abstract Type value();
   
    /**
     * Gets the unique letter code that identifies this flag in the flag-space it belongs to.
     *
     * @return the unique letter code (ASCII).
     */
    public final char letterCode() { return letterCode; }
    
    /**
     * Gets the human-readable descriptive name that specifies the 'meaning' of this flag.
     *
     * @return the name of this flag.
     */
    public final String name() { return name; }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "'" + letterCode + "' - 0x" + toHexString() + " - " + name;
    }

    /**
     * To hex string.
     *
     * @return the string
     */
    protected abstract String toHexString();
    
    
    /**
     * The Class Byte.
     */
    public static class Byte extends Flag<java.lang.Byte> {
        
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 579083092124948222L;
        
        /** The value. */
        private byte value;
        
        /**
         * Instantiates a new byte.
         *
         * @param space the space
         * @param value the value
         * @param letterCode the letter code
         * @param name the name
         * @throws IllegalArgumentException the illegal argument exception
         * @throws FlagConflictException the flag conflict exception
         */
        protected Byte(FlagSpace<java.lang.Byte> space, byte value, char letterCode, String name) throws IllegalArgumentException {
            super(space, value, letterCode, name);
        }

        /* (non-Javadoc)
         * @see jnum.util.Flag#setValue(java.lang.Number)
         */
        @Override
        protected void setValue(java.lang.Byte value) {
            this.value = value;
        }

        /* (non-Javadoc)
         * @see jnum.util.Flag#value()
         */
        @Override
        public java.lang.Byte value() {
            return value;
        }

        /* (non-Javadoc)
         * @see jnum.util.Flag#toHexString()
         */
        @Override
        protected String toHexString() {
            return "0x" + java.lang.Integer.toHexString(value);
        }
        
    }
    
    /**
     * The Class Short.
     */
    public static class Short extends Flag<java.lang.Short> {  
        
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 5971322398411509726L;
        
        /** The value. */
        private short value;
        
        /**
         * Instantiates a new short.
         *
         * @param space the space
         * @param value the value
         * @param letterCode the letter code
         * @param name the name
         * @throws IllegalArgumentException the illegal argument exception
         * @throws FlagConflictException the flag conflict exception
         */
        protected Short(FlagSpace<java.lang.Short> space, short value, char letterCode, String name) throws IllegalArgumentException {
            super(space, value, letterCode, name);
        }

        /* (non-Javadoc)
         * @see jnum.util.Flag#setValue(java.lang.Number)
         */
        @Override
        protected void setValue(java.lang.Short value) {
            this.value = value;
        }

        /* (non-Javadoc)
         * @see jnum.util.Flag#value()
         */
        @Override
        public java.lang.Short value() {
            return value;
        }

        /* (non-Javadoc)
         * @see jnum.util.Flag#toHexString()
         */
        @Override
        protected String toHexString() {
            return "0x" + java.lang.Integer.toHexString(value);
        }
        
    }
    
    /**
     * The Class Integer.
     */
    public static class Integer extends Flag<java.lang.Integer> {
        
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = -6037190445957697347L;

        /** The value. */
        private int value;
        
        /**
         * Instantiates a new integer.
         *
         * @param space the space
         * @param value the value
         * @param letterCode the letter code
         * @param name the name
         * @throws IllegalArgumentException the illegal argument exception
         * @throws FlagConflictException the flag conflict exception
         */
        protected Integer(FlagSpace<java.lang.Integer> space, int value, char letterCode, String name) throws IllegalArgumentException {
            super(space, value, letterCode, name);
        }

        /* (non-Javadoc)
         * @see jnum.util.Flag#setValue(java.lang.Number)
         */
        @Override
        protected void setValue(java.lang.Integer value) {
            this.value = value;
        }

        /* (non-Javadoc)
         * @see jnum.util.Flag#value()
         */
        @Override
        public java.lang.Integer value() {
            return value;
        }

        /* (non-Javadoc)
         * @see jnum.util.Flag#toHexString()
         */
        @Override
        protected String toHexString() {
            return "0x" + java.lang.Integer.toHexString(value);
        }
        
    }
    
    /**
     * The Class Long.
     */
    public static class Long extends Flag<java.lang.Long> {
        
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 7872780970527048382L;
        
        /** The value. */
        private long value;
        
        /**
         * Instantiates a new long.
         *
         * @param space the space
         * @param value the value
         * @param letterCode the letter code
         * @param name the name
         * @throws IllegalArgumentException the illegal argument exception
         * @throws FlagConflictException the flag conflict exception
         */
        protected Long(FlagSpace<java.lang.Long> space, long value, char letterCode, String name) throws IllegalArgumentException {
            super(space, value, letterCode, name);
        }

        /* (non-Javadoc)
         * @see jnum.util.Flag#setValue(java.lang.Number)
         */
        @Override
        protected void setValue(java.lang.Long value) {
            this.value = value;
        }

        /* (non-Javadoc)
         * @see jnum.util.Flag#value()
         */
        @Override
        public java.lang.Long value() {
            return value;
        }

        /* (non-Javadoc)
         * @see jnum.util.Flag#toHexString()
         */
        @Override
        protected String toHexString() {
            return "0x" + java.lang.Long.toHexString(value);
        }
        
    }
    
}
