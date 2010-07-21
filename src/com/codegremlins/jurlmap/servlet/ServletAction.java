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

package com.codegremlins.jurlmap.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.codegremlins.jurlmap.pattern.PathInput;
import com.codegremlins.jurlmap.pattern.PathPattern;
import com.codegremlins.jurlmap.pattern.PatternException;

class ServletAction {
    public enum Type {FORWARD, REDIRECT, RELOCATE, DEPLOY};
    
    private Class<? extends Page> pageClass;
    private String target;
    private Type type;

    public ServletAction(String target, Type type) {
        this.target = target;
        this.type = type;
    }
    
    public ServletAction(Class<? extends Page> page) {
        this.pageClass = page;
        this.type = Type.DEPLOY;
    }

    @SuppressWarnings("unchecked")
    public Object invoke(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (type == Type.FORWARD) {
            request.getRequestDispatcher(target).forward(request, response);
        } else if (type == Type.REDIRECT || type == Type.RELOCATE) {
            StringBuilder out = new StringBuilder(target);
            boolean first = target.indexOf('?') == -1;
            
            Map<String, String[]> map = (Map<String, String[]>)request.getParameterMap();
            for (String key : map.keySet()) {
                String[] values = map.get(key);
                for (String value : values) {
                    if (first) {
                        first = false;
                        out.append("?");
                    } else {
                        out.append("&");
                    }
                    
                    out.append(URLEncoder.encode(key, "utf-8"));
                    out.append("=");
                    out.append(URLEncoder.encode(value, "utf-8"));
                }
            }
            
            if (type == Type.REDIRECT) {
                response.sendRedirect(out.toString());
            } else {
                response.setHeader("Location", out.toString());
                response.setHeader("Connection", "close");
                response.setStatus(301);
            }
        } else if (type == Type.DEPLOY) {
            try {
                Page handler = pageClass.newInstance();
                request.setAttribute(AttributeNames.PAGE, handler);              
                request.getRequestDispatcher(target).forward(request, response);
            } catch (Exception ex) {
                throw new ServletException(ex);
            }
        }
        
        return null;
    }
    
    public Object match(PathPattern<ServletAction> pattern, PathInput input, MapHttpServletRequest request) {
        if (type == Type.DEPLOY) {
            try {
                Page page = pageClass.newInstance();
                input.bind(page);
                pattern.match(input);
                request.setAttribute(AttributeNames.EXTRA_PATH, input.getRemaining());               
                return page;
            } catch (Exception ex) {
                throw new PatternException("Exception during invocation of page `" + pageClass + "`", ex);
            }
        } else {
            input.bind(request.getMutableMap());
            pattern.match(input);
            request.setAttribute(AttributeNames.EXTRA_PATH, input.getRemaining());               
            
            return this;
        }
    }
}