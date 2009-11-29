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

package com.pagegoblin.jurlmap.pattern;

import java.io.IOException;
import java.io.Reader;

class PeekReader {
    private Reader in;
    private boolean cached = false;
    private int value;
    private int line = 1;
    
    public PeekReader(Reader in) {
        this.in = in;
    }
    
    private int next() throws IOException {
        int c = in.read();
        if (c == '\n') {
            line++;
        }
        
        return c;
    }

    public int read() throws IOException {
        if (cached) {
            cached = false;
            return value;
        } else {
            return next();
        }
    }

    public int peek() throws IOException {
        if (!cached) {
            cached = true;
            value = next();
        }
        return value;
    }
    
    public int line() {
        return line;
    }
}