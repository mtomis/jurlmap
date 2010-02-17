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

package com.codegremlins.jurlmap.pattern;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class AbstractPathSet<T, U> {
    private Map<String, List<PathPattern<U>>> map = new HashMap<String, List<PathPattern<U>>>();
    
    public void sort() {
        for (String key : map.keySet()) {
            List<PathPattern<U>> list = map.get(key);
        
            if (list != null) {
                Collections.sort(list);
            }
        }
    }
    
    protected void addPath(String path, U target) {
        addPath(path, target, false, HttpMethods.GET);
    }
    
    protected void addPath(String path, U target, boolean named, int defaultHttpMethods) {
        if (path != null && path.indexOf('(') >= 0) {
            for (String item : new PartCombinator(new PeekReader(new StringReader(path)), null).values()) {
                addIndividualPath(item, target, named, defaultHttpMethods);
            }
        } else {
            addIndividualPath(path, target, named, defaultHttpMethods);
        }
    }
    
    protected void addIndividualPath(String path, U target, boolean named, int defaultHttpMethods) {
        PathPattern<U> pattern = new PathPattern<U>(target, path, named, defaultHttpMethods);
        String key = pattern.getKey();
        
        List<PathPattern<U>> list = map.get(key);
        if (list == null) {
            list = new ArrayList<PathPattern<U>>();
            map.put(key, list);
        }
        
        list.add(pattern);
    }

    public T resolve(Object self, String path) {
        return resolve(self, path, HttpMethods.ALL);
    }
    
    public T resolve(Object self, String path, int httpMethod) {
        PathInput input = new PathInput(path, httpMethod);
        String key = input.value();
        boolean topLevel = false;
        
        if (key == null) {
            key = "";
            topLevel = true;
        }
        
        List<PathPattern<U>> list = map.get(key);
        
        if (list == null) {
            list = map.get("");
            topLevel = true;
        }

        if (list == null) {
            return null;
        }
        
        for (PathPattern<U> pattern : list) {
            if (pattern.match(input)) {
                T action = match(pattern, input, self);
                if (action != null) {
                    return action;
                }
            }
        }

        if (!topLevel) {
            list = map.get("");

            if (list == null) {
                return null;
            }
            
            for (PathPattern<U> pattern : list) {
                if (pattern.match(input)) {
                    T action = match(pattern, input, self);
                    if (action != null) {
                        return action;
                    }
                }
            }
        }
        
        return null;
    }
    
    abstract protected T match(PathPattern<U> pattern, PathInput input, Object self);
}