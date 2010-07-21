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

import java.util.Map;

import com.codegremlins.jurlmap.property.Properties;
import com.codegremlins.jurlmap.property.Property;

class ParameterElement extends Element {
    private int parameter = -1;
    private String properties;
    private boolean optional;
    
    public int getParameter() {
        return parameter;
    }
    
    public void setParameter(int parameter) {
        this.parameter = parameter;
    }
    
    public String getProperties() {
        return properties;
    }
    
    public void setProperties(String properties) {
        if (properties != null && properties.length() != 0) {
            this.properties = properties;
        }
    }
    
    public boolean isOptional() {
        return optional;
    }
    
    public void setOptional(boolean optional) {
        this.optional = optional;
    }
    
    @SuppressWarnings("unchecked")
    public void bind(PathInput input, Object value) {
        if (parameter < 0) {
            return;
        }

        if (properties != null) {
            Object object = input.getParameter(parameter);
            
            if (object instanceof Map) {
                ((Map)object).put(properties, value == null ? null : value.toString());
            } else {
                Property property = Properties.getProperty(object.getClass(), properties);
                if (property != null) {
                    property.setValue(object, Lubricant.apply(value, property.getType()));
                }
            }
        } else {
            input.setParameter(parameter, value);
        }
    }
    
    @Override
    public int priority() {
        return 5;
    }
}