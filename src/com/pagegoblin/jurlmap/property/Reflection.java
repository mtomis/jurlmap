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
import java.lang.reflect.Method;

@SuppressWarnings("unchecked")
public class Reflection {
    public static Field getField(Class<?> clazz, String name) {
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getName().equals(name)) {
                    field.setAccessible(true);
                    return field;
                }
            }

            clazz = clazz.getSuperclass();
        }

        return null;
    }
    
    public static Method getMethod(Class<?> self, String name, Class[] params) {
        Method method = null;

        for (int i = 0; i < params.length; i++) {
            if (params[i] != null) {
                if (params[i].equals(Integer.class)) {
                    params[i] = int.class;
                } else if (params[i].equals(Double.class)) {
                    params[i] = double.class;
                } else if (params[i].equals(Long.class)) {
                    params[i] = long.class;
                } else if (params[i].equals(Boolean.class)) {
                    params[i] = boolean.class;
                }
            }
        }

        try {
            method = self.getMethod(name, params);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException ex) {
            while (self != null) {
                Method[] methods = self.getDeclaredMethods();

                // FIXME: Later change to find best match
                for (int i = 0; i < methods.length; i++) {
                    if (!methods[i].getName().equals(name)) {
                        continue;
                    }

                    Class[] targetParams = methods[i].getParameterTypes();

                    if (isAssignable(targetParams, params)) {
                        methods[i].setAccessible(true);
                        return methods[i];
                    }
                }

                self = self.getSuperclass();
            }
        }

        return method;
    }

    private static boolean isAssignable(Class<?>[] formal, Class<?>[] actual) {
        if (formal.length != actual.length) {
            return false;
        }

        for (int i = 0; i < formal.length; i++) {
            if (actual[i] == null) {
                if ((formal[i].equals(int.class))
                    || (formal[i].equals(double.class))
                    || (formal[i].equals(boolean.class))) {
                    return false;
                }
                
                continue;
            }

            if (!formal[i].isAssignableFrom(actual[i])) {
                return false;
            }
        }

        return true;
    }
}