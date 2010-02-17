package com.codegremlins.jurlmap.pattern;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
public class PartCombinator {
    private List parts = new ArrayList();
    private PartCombinator parent;

    private static class Position {
        public Position(Position parent, int value) {
            this.parent = parent;
            this.value = value;
        }
        
        public Position parent;
        public int value;
    }
    
    public PartCombinator(PeekReader in, PartCombinator parent) {
        this.parent = parent;

        try {
            StringBuilder out = new StringBuilder();
            for (;;) {
                int c = in.read();
                if (c == -1 || c == ')') {
                    break;
                } else if (c == '(') {
                    parts.add(out.toString());
                    out.setLength(0);
                    parts.add(new PartCombinator(in, this));
                } else {
                    out.append((char)c);
                }
            }
            
            parts.add(out.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public Collection<String> values() {
        Set<String> results = new HashSet<String>();
        
        iterate("", 0, results, new Position(null, 0));
        return results;
    }

    public void iterate(String text, int start, Collection<String> list, Position position) {
        int stop = parts.size();
        if (start == stop) {
            if (parent == null) {
                list.add(text);
            } else {
                parent.iterate(text, position.value, list, position.parent);
            }
            return;
        }
        
        Object value = parts.get(start);
        if (value instanceof String) {
            iterate(text + value, start + 1, list, position);
        } else if (value instanceof PartCombinator) {
            iterate(text, start + 1, list, position);
    
            PartCombinator part = (PartCombinator)value;
            part.iterate(text, 0, list, new Position(position, start + 1));
        } else {
            iterate(text, start + 1, list, position);
        }
    }
}