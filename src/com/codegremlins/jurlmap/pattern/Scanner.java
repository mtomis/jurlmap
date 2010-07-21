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

import java.io.IOException;

class Scanner {
    private enum Type {NUMBER, STRING, NAME, SYMBOL, TEXT, END};
    
    private PeekReader in;
    private StringBuffer out = new StringBuffer();
    private String token;
    private Type type;
    private boolean cached = false;
    
    public Scanner(PeekReader in) {
        this.in = in;
    }
    
    public void peek() throws IOException {
        if (!cached) {
            next();
        }
        cached = true;
    }
    
    public void read() throws IOException {
        if (!cached) {
            next();
        }
        cached = false;
    }

    private void next() throws IOException {
        nextRun();
    }

    private void nextRun() throws IOException {
        out.setLength(0);

        type = null;
        token = null;

        int c = in.peek();
        if (c == -1) {
            type = Type.END;
            return;
        }
        
        clearSpace();
        c = in.peek();
        
        if (c == -1) {
            type = Type.END;
            return;
        } else if ("/.(){}[]$%|:*?".indexOf(c) > -1) {
            type = Type.SYMBOL;
            out.append((char)in.read());
        } else if (Character.isDigit(c)) {
            type = Type.NUMBER;
            parseNumber();
        } else if (Character.isLetter(c) || c == '_' || c == '.') {
            type = Type.NAME;
            parseName();
        } else {
            error("Invalid character `" + (char)c + "'");
        }
    }
    
    public void error(String text) throws IOException {
        throw new IOException("Line " + in.line() + ": " + text);
    }

    private void clearSpace() throws IOException {
        int c = in.peek();
        while (Character.isWhitespace(c)) {
            in.read();
            c = in.peek();
        }
    }

    private void parseName() throws IOException {
        int c = in.peek();
        while (Character.isLetterOrDigit(c) || c == '_' || c == ':' || c == '.') {
            out.append((char)in.read());
            c = in.peek();
        }
    }

    private void parseNumber() throws IOException {
        int c = in.peek();
        while (Character.isDigit(c) || c == '_') {
            out.append((char)in.read());
            c = in.peek();
        }
    }
    
    public String getToken() {
        if (token == null) {
            token = out.toString();
        }
        return token;
    }
    
    public boolean isNumber() {
        return type == Type.NUMBER;
    }
    
    public boolean isName() {
        return type == Type.NAME;
    }

    public boolean isEnd() {
        return type == Type.END;
    }

    public boolean isSymbol(String text) {
        return type == Type.SYMBOL && getToken().equals(text);
    }

    public int line() {
        return in.line();
    }
}