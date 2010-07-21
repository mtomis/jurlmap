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

package com.codegremlins.jurlmap.property;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CopyOnWriteHashMap<K, V> implements Map<K, V> {
    private volatile HashMap<K, V> map = new HashMap<K, V>();
    private final Object lock = new Object();

    public void clear() {
        synchronized (lock) {
            map = new HashMap<K, V>();
        }
    }

    public Object clone() {
        return map.clone();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    public boolean equals(Object o) {
        return map.equals(o);
    }

    public V get(Object key) {
        return map.get(key);
    }

    public int hashCode() {
        return map.hashCode();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public Set<K> keySet() {
        return map.keySet();
    }

    public V put(K key, V value) {
        synchronized (lock) {
            HashMap<K, V> _map = new HashMap<K, V>(map);
            V result = _map.put(key, value);
            map = _map;
            return result;
        }
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        synchronized (lock) {
            HashMap<K, V> _map = new HashMap<K, V>(map);
            _map.putAll(m);
            map = _map;
        }
    }

    public V remove(Object key) {
        synchronized (lock) {
            HashMap<K, V> _map = new HashMap<K, V>(map);
            V result = _map.remove(key);
            map = _map;
            return result;
        }
    }

    public int size() {
        return map.size();
    }

    public String toString() {
        return map.toString();
    }

    public Collection<V> values() {
        return map.values();
    }
}