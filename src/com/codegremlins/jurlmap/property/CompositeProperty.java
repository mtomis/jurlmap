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

package com.codegremlins.jurlmap.property;

@SuppressWarnings("unchecked")
final class CompositeProperty implements Property {
    private String name = "";
    private Property property;
    private Property valueProperty;
    
    public CompositeProperty(Class clazz, String name) {
        this.name = name;
        
        int index = name.indexOf('.');
        String first = name.substring(0, index);
        String last  = name.substring(index + 1);
        
        
        property = Properties.getProperty(clazz, first);
        
        valueProperty = Properties.getProperty(property.getType(), last); 
    }

    public void setValue(Object self, Object value) {
        Object field = property.getValue(self);
        if (field == null) {
            try {
                field = property.getType().newInstance();
                property.setValue(self, field);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }
        
        valueProperty.setValue(field, value);
    }
 
    public Object getValue(Object self) {
        Object value = property.getValue(self);
        if (value == null) {
            return null;
        }
        return valueProperty.getValue(value);
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        return property.getType();
    }
}