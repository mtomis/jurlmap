/*
 *  jurlmap - RESTful URLs for Java.
 *  Copyright (C) 2009, 2010 Manuel Tomis manuel@codegremlins.com
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

class StringElement extends ParameterElement {
    @Override
    public boolean match(PathInput input) {
        if (input.end()) {
            return isOptional();
        } else {
            if (input.isBind()) {
                bind(input, input.value());
            }
            
            input.next();
            return true;
        }
    }

    @Override
    public int priority() {
        return 6;
    }
    
    @Override
    public String toString() {
        return "/$";
    }
}