/*
 *  jurlmap - RESTful URLs for Java.
 *  Copyright (C) 2009, 2010 Manuel Tomis support@codegremlins.com
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

/**
 * Names of attributes that are set by DispatchFilter and can be retrieved by using 
 * ServletRequest.getAttribute.
 */
public interface AttributeNames {
    /**
     *  Original request uri
     */
    public final String REQUEST_URI = "com.codegremlins.jurlmap.DispatchFilter.REQUEST_URI";
    
    /**
     * Original request url
     */
    public final String REQUEST_URL = "com.codegremlins.jurlmap.DispatchFilter.REQUEST_URL";
    
    /**
     * Path that was matched against pattern
     */
    public final String PATH = "com.codegremlins.jurlmap.DispatchFilter.PATH";
    
    /**
     * Extra part at end of matched path (if path ends with /*)
     */
    public final String EXTRA_PATH = "com.codegremlins.jurlmap.DispatchFilter.EXTRA_PATH";
    
    public final String PAGE = "com.codegremlins.jurlmap.DispatchFilter.PAGE";
    public final String PATH_SET = "com.codegremlins.jurlmap.DispatchFilter.PATH_SET";

    /**
     * 
     */
    public final String PAGING = "com.codegremlins.jurlmap.DispatchFilter.PAGING";
    public final String PAGING_SIZE = "com.codegremlins.jurlmap.DispatchFilter.PAGING_SIZE";
    public final String DISPOSITION = "com.codegremlins.jurlmap.DispatchFilter.DISPOSITION";
}
