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

import java.util.Map;

@SuppressWarnings("unchecked")
public final class Properties {
    private static Map<String, Property> propertiesCache = new CopyOnWriteHashMap<String, Property>(); // Maps names to properties
    private static Map<String, Boolean> searchCache = new CopyOnWriteHashMap<String, Boolean>(); // Results of checking for property
    
    private Properties() {
    }
    
    public static Property getProperty(Class clazz, String name) {
        if (name.indexOf('.') != -1) {
            String key = clazz.getName() + "#" + name;
            Property property = propertiesCache.get(key);
            
            if (property == null) {
                property = new CompositeProperty(clazz, name);
                addProperty(key, property);
                return property;
            } else {
                return property;
            }
        } else {
            String key = clazz.getName() + "#" + name;
            Property property = propertiesCache.get(key);
            
            if (property == null) {
                property = new SimpleProperty(clazz, name);
                addProperty(key, property);
                return property;
            } else {
                return property;
            }
        }
    }

    private static void addProperty(String key, Property property) {
        propertiesCache.put(key, property);
        propertiesCache.put(key.toUpperCase(), property);
    }
    
    public static boolean hasProperty(Class clazz, String name) {
        String key = clazz.getName() + "#" + name;
        if (searchCache.containsKey(key)) {
            return searchCache.get(key).booleanValue();
        } else {
            boolean found = true;
            try {
                getProperty(clazz, name);
            } catch (Throwable ex) {
                found = false;
            }
            
            searchCache.put(key, found);
            return found;
        }
    }
}