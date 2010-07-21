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

package com.codegremlins.jurlmap.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles the actual servicing of Page objects after a match from DispatchFilter.
 * <p>
 * If you want to use Page objects, rather that just forwarding to servlets/jsp
 * you must add DispatchServlet to your web.xml
 */
public final class DispatchServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String method = request.getMethod();
        
        // TODO: maybe handle them correctly in the future
        if ("OPTIONS".equalsIgnoreCase(method) || "TRACE".equalsIgnoreCase(method)) {
            super.service(request, response);
            return;
        }
        
        Object page = request.getAttribute(AttributeNames.PAGE);
        
        if (page instanceof Page) {
            ((Page)page).service(getServletContext(), request, response);
        } else {
            response.sendError(404);
        }
    }
}