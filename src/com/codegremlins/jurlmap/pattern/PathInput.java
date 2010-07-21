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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unchecked")
public final class PathInput {
    private Object[] values; 
    private String[] items;
    private Class[] types;
    private int position;
    private boolean bind = false;
    private String remaining = "";
    private int httpMethod = 0;
    
    public PathInput(String text) {
        this(text, HttpMethods.GET);
    }
    
    public PathInput(String text, int httpMethod) {
        this.httpMethod = httpMethod;
        
        if (text == null) {
            items = new String[0];
        } else {
            String[] _items = text.split("/");
            
            List<String> ls = new ArrayList<String>();
            for (String item : _items) {
                if (item != null && item.trim().length() > 0) {
                    ls.add(item.trim());
                }
            }
            
            this.items = ls.toArray(new String[ls.size()]);
        }
    }

    void rewind() {
        position = 0;
    }
    
    void consume() {
        position = items.length;
    }

    void next() {
        position++;
    }

    boolean end() {
        return position >= items.length;
    }
    
    public String value() {
        if (position >= 0 && position < items.length) {
            return items[position];
        } else {
            return null;
        }
    }

    String remaining() {
        if (position >= 0 && position < items.length) {
            StringBuilder out = new StringBuilder();
            for (int i = position; i < items.length; i++) {
                if (out.length() > 0) {
                    out.append("/");
                }
                out.append(items[i]);
            }
            return remaining = out.toString();
        } else {
            return null;
        }
    }

    void setParameter(int position, Object value) {
        if (position >= 0 && position < values.length) {
            values[position] = value;
        }
    }
    
    Object getParameter(int position) {
        if (position >= 0 && position < values.length) {
            if (values[position] == null) {
                try {
                    values[position] = types[position].newInstance();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
            
            return values[position];
        } else {
            return null;
        }
    }

    boolean isBind() {
        return bind;
    }

    public void bind(Object target) {
        if (target instanceof Method) {
            this.bind = true;
            types = ((Method)target).getParameterTypes();       
            this.values = new Object[types.length]; 
        } else {
            this.bind = true;
            types = new Class[] {target.getClass()};        
            this.values = new Object[1];
            this.values[0] = target;
        }
    }

    public Object[] getParameters() {
        return values;
    }
    
    public String getRemaining() {
        return remaining;
    }

    int getHttpMethod() {
        return httpMethod;
    }
    
    public String toString() {
        StringBuilder out = new StringBuilder();

        if (items != null) {
            for (String s : items) {
                out.append(s);
                out.append("/");
            }
        }
        
        return out.toString();
    }
}