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

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.codegremlins.jurlmap.BoundMethod;
import com.codegremlins.jurlmap.PathSet;
import com.codegremlins.jurlmap.pattern.HttpMethods;

abstract public class AbstractPage implements Page {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletContext servletContext;
 
    public final void service(ServletContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        this.servletContext = context;
        this.request = request;
        this.response = response;
        
        run();
    }
    
    public void run() throws ServletException, IOException {
        if (!dispatch()) {
            response.sendError(404);
        }
    }
    
    protected boolean dispatch() {
        String path = (String)request.getAttribute(AttributeNames.EXTRA_PATH);
        return dispatch(path);
    }
    
    protected boolean dispatch(String path) {
        PathSet paths = (PathSet)request.getAttribute(AttributeNames.PATH_SET);
        
        if (paths == null || path == null) {
            return false;
        }
        
        BoundMethod method = paths.resolve(this, path, HttpMethods.getMethod(request.getMethod()));
        if (method != null) {
            method.invoke();
            return true;
        } else {
            return false;
        }
    }

    protected HttpServletRequest getRequest() {
        return request;
    }

    protected HttpServletResponse getResponse() {
        return response;
    }

    protected ServletContext getServletContext() {
        return servletContext;
    }

    protected void forward(String target) throws ServletException, IOException {
        request.getRequestDispatcher(target).forward(request, response);
    }

    protected void redirect(String target) throws IOException {
        response.sendRedirect(target);
    }
    
    protected boolean modified(long lastModified) {
        long since = getRequest().getDateHeader("If-Modified-Since");
        if (since < (lastModified / 1000 * 1000)) {
            getResponse().setDateHeader("Last-Modified", lastModified);
            return true;
        } else {
            getResponse().setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return false;
        }
    }
}