/*
 *  jurlmap - RESTful URLs for Java.
 *  Copyright (C) 2009 Manuel Tomis manuel@codegremlins.com
 *
 *  This library is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.codegremlins.jurlmap.pattern;

/**
 * Lubricates parameters so that they fit into invocation destinations 
 *
 */
public final class Lubricant {
    private Lubricant() {
    }
    
    public final static Object apply(Object value, Class<?> type) {
        if (value instanceof Long) {
            if (type == int.class || type == Integer.class) {
                return ((Long)value).intValue();
            } else if (type == short.class || type == Short.class) {
                return ((Long)value).shortValue();
            } else if (type == byte.class || type == Byte.class) {
                return ((Long)value).byteValue();
            }
        } 
        
        if (!(value instanceof Boolean) && (type == boolean.class || type == Boolean.class)) {
            return value != null;
        }
        
        return value;
    }
}