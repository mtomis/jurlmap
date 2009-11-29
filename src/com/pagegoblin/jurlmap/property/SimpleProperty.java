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

package com.pagegoblin.jurlmap.property;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.pagegoblin.jurlmap.property.Reflection;

@SuppressWarnings("unchecked")
final class SimpleProperty implements Property {
    private Method readMethod;
    private Method writeMethod;
    private Field field;
    private Class type;
    private String name = "";
    private boolean isBoolean  = false;
    
    public SimpleProperty(Class clazz, String name) {
        this.name = name;
        
        Class[] readParams = {};
        readMethod = Reflection.getMethod(clazz, "get" + name, readParams);
        if (readMethod == null) {
            readMethod = Reflection.getMethod(clazz, "is" + name, readParams);
        }
        
        if (readMethod == null) {
            String fieldName = Character.toLowerCase(name.charAt(0)) + name.substring(1);
            
            field = Reflection.getField(clazz, fieldName);
            
            if (field == null) {
//                throw new RuntimeException("Missing Method: get" + name + " " + fieldName + " " + clazz);
            } else {
                field.setAccessible(true);
                type = field.getType();
            }
        } else {
            type = readMethod.getReturnType();
        }

        if (type == null && writeMethod != null) {
            type = writeMethod.getReturnType();
        }
        
        Class[] writeParams = {type};
        writeMethod = Reflection.getMethod(clazz, "set" + name, writeParams);

        if (writeMethod == null && field == null) {
//            throw new RuntimeException("Missing Method: set" + name);
        }
        
        if (type != null) {
            isBoolean = type.equals(Boolean.class) || type.equals(boolean.class);
        }
    }

    public void setValue(Object self, Object value) {
        if (isBoolean && value instanceof Number) {
            value = ((Number)value).longValue() > 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        
        try {
            if (writeMethod != null) {
                writeMethod.invoke(self, new Object[]{value});
            } else if (field != null) {
                field.set(self, value);
            } else {
                throw new RuntimeException("Field or field setter missing: " + name);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Cannot set value of field: " + name, ex);
        }
    }
 
    public Object getValue(Object self) {
        try {
            Object[] params = {};
            if (readMethod != null) {
                return readMethod.invoke(self, params);
            } else if (field != null) {
                return field.get(self);
            } else {
                throw new RuntimeException("Field or field getter missing: " + name);
            }
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        return type;
    }
}