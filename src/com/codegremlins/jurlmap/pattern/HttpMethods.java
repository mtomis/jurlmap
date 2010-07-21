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

public class HttpMethods {
    public static final int GET = 0x1;
    public static final int POST = 0x2;
    public static final int PUT = 0x4;
    public static final int DELETE = 0x8;
    public static final int HEAD = GET;

    public static final int ALL = 0xff;
    
    public static int getMethod(String item) {
        if ("GET".equalsIgnoreCase(item)) {
            return GET;
        } else if ("POST".equalsIgnoreCase(item)) {
            return POST;
        } else if ("PUT".equalsIgnoreCase(item)) {
            return PUT;
        } else if ("DELETE".equalsIgnoreCase(item)) {
            return DELETE;
        } else if ("HEAD".equalsIgnoreCase(item)) {
            return HEAD;
        } else {
            return 0;
        }
    }
    
    public static int parse(String methods) {
        int httpMethods = 0;
        String[] items = methods.split("\\|");
        for (String item : items) {
            item = item.trim();
            httpMethods = httpMethods | HttpMethods.getMethod(item);
        }

        return httpMethods;
    }
}