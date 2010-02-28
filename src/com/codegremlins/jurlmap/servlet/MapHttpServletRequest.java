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

package com.codegremlins.jurlmap.servlet;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

@SuppressWarnings("unchecked")
class MapHttpServletRequest extends HttpServletRequestWrapper {
    private Map<String, String[]> parameters = new HashMap<String, String[]>();
    private String requestURI = null;

    public MapHttpServletRequest(HttpServletRequest request) {
        super(request);
        parameters.putAll(super.getParameterMap());
    }

    @Override
    public String getParameter(String key) {
        String[] value = parameters.get(key);
        if (value instanceof String[]) {
            return ((String[])value)[0];
        } else {
            return null; 
        }
    }
    
    @Override
    public Map getParameterMap() {
        return Collections.unmodifiableMap(parameters);
    }
    
    public void setParameter(String key, String value) {
        String[] values = parameters.get(key);
        if (values == null) {
            parameters.put(key, new String[] {value});
        } else {
            String[] result = new String[values.length + 1];

            result[0] = value;
            for (int i = 0; i < values.length; i++) {
                result[i + 1] = values[i];
            }
            
            parameters.put(key, result);
        }
    }

    @Override
    public Enumeration getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameters.get(name);
    }

    Map<String, String> getMutableMap() {
        return new Map<String, String>() {
            public void clear() {
                throw new UnsupportedOperationException();
            }

            public boolean containsKey(Object key) {
                throw new UnsupportedOperationException();
            }

            public boolean containsValue(Object value) {
                throw new UnsupportedOperationException();
            }

            public Set<java.util.Map.Entry<String, String>> entrySet() {
                throw new UnsupportedOperationException();
            }

            public String get(Object key) {
                throw new UnsupportedOperationException();
            }

            public boolean isEmpty() {
                throw new UnsupportedOperationException();
            }

            public Set<String> keySet() {
                throw new UnsupportedOperationException();
            }

            public String put(String key, String value) {
                setParameter(key, value);
                return value;
            }

            public void putAll(Map<? extends String, ? extends String> m) {
                throw new UnsupportedOperationException();
            }

            public String remove(Object key) {
                throw new UnsupportedOperationException();
            }

            public int size() {
                throw new UnsupportedOperationException();
            }

            public Collection<String> values() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public String getRequestURI() {
        if (requestURI == null) {
            return super.getRequestURI();
        } else {
            return requestURI;
        }
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }
}