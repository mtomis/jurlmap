/*
 *  jurlmap - RESTful URLs for Java.
 *  Copyright (C) 2009 Manuel Tomis support@pagegoblin.com
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

package com.pagegoblin.jurlmap.pattern;

class OptionsElement extends ParameterElement {
    private String[] values;

    public OptionsElement(String[] values) {
        this.values = values;
    }

    @Override
    public boolean match(PathInput input) {
        String item = input.value();
        
        for (String value : values) {
            if (value.equals(item)) {
                if (input.isBind()) {
                    bind(input, item);
                }

                input.next();
                return true;
            }
        }
        return isOptional();
    }
    
    @Override
    public int priority() {
        return 4;
    }
}