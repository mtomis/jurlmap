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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class PathPattern<T> implements Comparable<PathPattern<T>> {
    private final String SPECIAL = "$%[{(*";

    private List<Element> elements = new ArrayList<Element>();
    private int httpMethods;
    
    private String key = "";
    private T target;
    private boolean named = false;

    public PathPattern(T target, String text, boolean named, int defaultHttpMethods) {
        this.target = target;
        this.named = named;

        if (text == null) {
            return;
        }
        
        String[] items = text.split(";");
        
        if (items.length > 1) {
            httpMethods = HttpMethods.parse(items[1]);
        } else {
            httpMethods = defaultHttpMethods;
        }
        
        try {
            parseAllElement(items[0]);
        } catch (IOException ex) {
            throw new PatternException(ex.getMessage() + ". Input was: " + text);
        }
    }
    
    private void parseAllElement(String path) throws IOException {
        String[] items = path.split("/");
        
        boolean first = true;
        
        for (String item : items) {
            item = item.trim();
            if (item.length() > 0) {
                if (SPECIAL.indexOf(item.charAt(0)) != -1) {
                    Scanner in = new Scanner(new PeekReader(new StringReader(item)));
                    elements.add(parseElement(in));
                } else {
                    if (first) {
                        key = item;
                    }
                    
                    elements.add(new StaticElement(item));
                }
                
                first = false;
            }
        }
    }
    
    private Element parseElement(Scanner in) throws IOException {
        in.read();
        
        if (in.isSymbol("%")) {
            return parseReference(in, new IntegerElement());
        } else if (in.isSymbol("$")) {
            return parseReference(in, new StringElement());
        } else if (in.isSymbol("*")) {
            return parseReference(in, new StarElement());
        } else if (in.isSymbol("[")) {
            List<String> ls = new ArrayList<String>();
            for (;;) {
                in.read();
                if (in.isName()) {
                    ls.add(in.getToken());
                } else if (in.isSymbol("]")) {
                    break;
                } else if (!in.isSymbol("|")) {
                    in.error("Expected | or ] instead of `" + in.getToken() + "'");
                }
            }
            
            String[] values = ls.toArray(new String[ls.size()]);
            return parseReference(in, new OptionsElement(values));
//      } else if (in.isSymbol("{")) {
//          parseReference(in);
//      } else if (in.isSymbol("(")) {
//          parseReference(in);
        }
        
        return null;
    }
    
    private <U extends ParameterElement> U parseReference(Scanner in, U element) throws IOException {
        in.peek();
        
        if (in.isEnd()) {
            return element; 
        } else if ((named && in.isName()) || in.isNumber()) {
            in.read();
            String parameter = in.getToken();
            StringBuilder out = new StringBuilder();
            
            if (named) {
                String s = in.getToken();
                out.append(s);
                element.setProperties(s);
                element.setParameter(0);
            } else {
                int v = Integer.parseInt(parameter);
                element.setParameter(v - 1);
            }
            
            in.peek();
            if (in.isSymbol(".")) {
                for (;;) {
                    in.peek();
                    if (in.isSymbol(".")) {
                        in.read();
                        if (out.length() > 0) {
                            out.append(".");
                        }
                    } else {
                        break;
                    }
                    
                    in.read();
                    if (in.isName()) {
                        out.append(in.getToken());
                    } else {
                        in.error("Expected NAME instead of `" + in.getToken() + "'");
                    }
                }
                
                element.setProperties(out.toString());
            }
        } else if (!in.isSymbol("?")) {
            if (named) {
                in.error("Expected NAME instead of `" + in.getToken() + "'");
            } else {
                in.error("Expected PARAMETER NUMBER instead of `" + in.getToken() + "'");
            }
        }

        in.peek();
        
        if (in.isSymbol("?")) {
            in.read();
            element.setOptional(true);
        }
        
        return element;
    }
    
    public boolean match(PathInput input) {
        input.rewind();
        
        if ((httpMethods & input.getHttpMethod()) == 0) {
            return false;
        }
        
        for (Element element : elements) {
            if (element == null) {
                continue;
            }
            
            if (!element.match(input)) {
                return false;
            }
        }
        
        return input.end();
    }
    
    public int compareTo(PathPattern<T> pattern) {
        int sizeDiff = elements.size() - pattern.elements.size();
        
        Iterator<Element> i = elements.iterator();
        Iterator<Element> j = pattern.elements.iterator();
        
        for (;;) {
            if (!i.hasNext() || !j.hasNext()) {
                return sizeDiff;
            }
            
            Element elemA = i.next();
            Element elemB = j.next();
            
            int result = elemA.priority() - elemB.priority();
            if (result != 0) {
                return result;
            }
        }
    }
    
    public String getKey() {
        return key;
    }

    public T getTarget() {
        return target;
    }
}