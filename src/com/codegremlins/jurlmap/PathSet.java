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

package com.codegremlins.jurlmap;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import com.codegremlins.jurlmap.pattern.AbstractPathSet;
import com.codegremlins.jurlmap.pattern.HttpMethods;
import com.codegremlins.jurlmap.pattern.PathInput;
import com.codegremlins.jurlmap.pattern.PathPattern;

@SuppressWarnings("unchecked")
public class PathSet extends AbstractPathSet<BoundMethod, Method> {
    public PathSet(Class clazz) {
        addClass(clazz);
        sort();
    }
    
    private void addClass(Class clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            addMethod(method);
        }
        
        Class[] ifaces = clazz.getInterfaces();
        if (ifaces != null) {
            for (Class iface : ifaces) {
                addClass(iface);
            }
        }
    }
    
    private void addMethod(Method method) {
        Deploy deploy = (Deploy)method.getAnnotation(Deploy.class);
        if (deploy != null) {
            for (String path : deploy.value()) {
                addMethodPath(path, method);
            }
        }
    }

    private void addMethodPath(String path, Method method) {
        method.setAccessible(true);
        addPath(path, method);
    }
    
    protected BoundMethod match(PathPattern<Method> pattern, PathInput input, Object self) {
        Method method = pattern.getTarget();
        if (method != null) {
            input.bind(method);
            pattern.match(input);

            return new BoundMethod(self, method, input.getParameters());
        } else {
            return null;
        }
    }   
    
    public Object dispatch(Object self, String path) {
        BoundMethod method = resolve(self, path, HttpMethods.ALL);
        if (method != null) {
            return method.invoke();
        } else {
            return null;
        }
    }
    
    public Object dispatch(Object self, String path, HttpServletRequest request) {
        BoundMethod method = resolve(self, path, HttpMethods.getMethod(request.getMethod()));
        if (method != null) {
            return method.invoke();
        } else {
            return null;
        }
    }   
}