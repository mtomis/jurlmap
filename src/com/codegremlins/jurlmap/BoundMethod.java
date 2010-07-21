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

package com.codegremlins.jurlmap;

import java.lang.reflect.Method;

import com.codegremlins.jurlmap.pattern.Lubricant;
import com.codegremlins.jurlmap.pattern.PatternException;

public class BoundMethod {
    private Object self;
    private Method method;
    private Object[] parameters;

    public BoundMethod(Object self, Method method, Object[] parameters) {
        this.self = self;
        this.method = method;
        this.parameters = parameters;
        
        if (method != null && parameters != null) {
            Class[] methodTypes = method.getParameterTypes();
            if (methodTypes.length != parameters.length) {
                throw new PatternException("Parameters don't match for `" + self.getClass() + "#" + method.getName() + "`");
            }
            
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = Lubricant.apply(parameters[i], methodTypes[i]);
            }
        }
    }

    public Object invoke() {
        try {
            return method.invoke(self, parameters);
        } catch (Exception ex) {
            throw new PatternException("Exception during invocation of `" + self.getClass() + "#" + method.getName() + "`", ex);
        }
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getParameters() {
        return parameters;
    }
}