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

package com.pagegoblin.jurlmap.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pagegoblin.jurlmap.PathSet;
import com.pagegoblin.jurlmap.pattern.HttpMethods;
import com.pagegoblin.jurlmap.property.CopyOnWriteHashMap;

/**
 * This filter does the main work of jurlmap. You must create a subclass of this for your project 
 * and add it to your web.xml file.
 */
abstract public class DispatchFilter implements Filter {
    private String dispatchServlet = null;
    private ServletPathSet pages = new ServletPathSet();
    private Map<Class<?>, PathSet> pathMap = new CopyOnWriteHashMap<Class<?>, PathSet>();
    private ServletContext servletContext;
    
    public DispatchFilter() {
        configure();
        pages.sort();
    }
    
    public void init(FilterConfig config) throws ServletException {
        servletContext = config.getServletContext();
    }

    /**
     * Override this method to add your configuration or rewrite rules.
     */
    abstract protected void configure();    

    /**
     * 
     * @param target When matched will forward to here
     * @param patterns Url patterns to be matched
     */
    public final void forward(String target, String ...patterns) {
        for (String path : patterns) {
            pages.forward(target, path);
        }
    }

    /**
     * 
     * @param target When matched will redirect to here
     * @param patterns Url patterns to be matched
     */
    public final void redirect(String target, String ...patterns) {
        for (String path : patterns) {
            pages.redirect(target, path);
        }
    }
    
    /**
     * 
     * @param clazz Class to be used to handling request. A new instance is created per request. 
     * @param patterns Url patterns to be matched
     */
    public final void deploy(Class<?> clazz, String ...patterns) {
        lookupPaths(clazz);
        pages.deploy(clazz, patterns);
    }

    /**
     * 
     * @param dispatchServlet Path at which you configured the DispatchFilter in your web.xml 
     */
    public final void setDispatchServlet(String dispatchServlet) {
        this.dispatchServlet = dispatchServlet;
    }
    
    /**
     * Set http methods matched by default (when not method is specified in pattern)
     * 
     * @param methods Http methods separated by | for example GET|PUT
     */
    public final void setDefaultHttpMethods(String methods) {
        pages.setDefaultHttpMethods(HttpMethods.parse(methods));
    }

    public void doFilter(ServletRequest _request, ServletResponse _response, FilterChain chain) throws IOException, ServletException {
        if (_request instanceof HttpServletRequest && _response instanceof HttpServletResponse) {
            HttpServletRequest request = (HttpServletRequest)_request;
            HttpServletResponse response = (HttpServletResponse)_response;

            request.setAttribute(AttributeNames.REQUEST_URI, request.getRequestURI());               
            request.setAttribute(AttributeNames.REQUEST_URL, request.getRequestURL().toString());                
            
            String path = request.getRequestURI().substring(request.getContextPath().length());
            request = new MapHttpServletRequest(request);
            Object target = pages.resolve((MapHttpServletRequest)request, path, HttpMethods.getMethod(request.getMethod()));
            
            request.setAttribute(AttributeNames.PATH, path);             
            
            if (target instanceof ServletAction) {
                ((ServletAction)target).invoke(request, response);
                return;
            } else if (target instanceof Page) {
                request.setAttribute(AttributeNames.PATH_SET, lookupPaths(target.getClass()));
                request.setAttribute(AttributeNames.PAGE, target);
                
                if (dispatchServlet == null) {
                    ((Page)target).service(servletContext, request, response);
                } else {
                    request.getRequestDispatcher(dispatchServlet).forward(request, response);
                }
                
                return;
            }
        }
        
        chain.doFilter(_request, _response);
    }

    private PathSet lookupPaths(Class<?> clazz) {
        PathSet paths = pathMap.get(clazz);
        if (paths == null) {
            paths = new PathSet(clazz);
            pathMap.put(clazz, paths);
        }
        
        return paths;
    }
    
    public void destroy() {
    }
}