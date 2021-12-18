/*
 * OrderedProperties.java
 * Copyright 2017 Rob Spoor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.robtimus.maven.plugins.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

class OrderedProperties extends Properties {

    private static final long serialVersionUID = -6380793892360859605L;

    private Map<String, String> properties = new LinkedHashMap<>();

    static OrderedProperties fromInputStream(InputStream input) throws IOException {
        OrderedProperties properties = new OrderedProperties();
        properties.load(input);
        return properties;
    }

    static OrderedProperties fromReader(Reader input) throws IOException {
        OrderedProperties properties = new OrderedProperties();
        properties.load(input);
        return properties;
    }

    @Override
    public synchronized Object setProperty(String key, String value) {
        return properties.put(key, value);
    }

    @Override
    public String getProperty(String key) {
        return properties.get(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        String value = properties.get(key);
        return value == null ? defaultValue : value;
    }

    @Override
    public Enumeration<?> propertyNames() {
        return enumeration(properties.keySet());
    }

    @Override
    public Set<String> stringPropertyNames() {
        return new LinkedHashSet<>(properties.keySet());
    }

    @Override
    public synchronized int size() {
        return properties.size();
    }

    @Override
    public synchronized boolean isEmpty() {
        return properties.isEmpty();
    }

    @Override
    public synchronized Enumeration<Object> keys() {
        return enumeration(properties.keySet());
    }

    @Override
    public synchronized Enumeration<Object> elements() {
        return enumeration(properties.values());
    }

    private Enumeration<Object> enumeration(final Collection<?> collection) {
        return new Enumeration<Object>() {
            private final Iterator<?> iterator = collection.iterator();

            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public Object nextElement() {
                return iterator.next();
            }
        };
    }

    @Override
    public synchronized boolean contains(Object value) {
        return properties.containsValue(value);
    }

    @Override
    public boolean containsValue(Object value) {
        return properties.containsValue(value);
    }

    @Override
    public synchronized boolean containsKey(Object key) {
        return properties.containsKey(key);
    }

    @Override
    @SuppressWarnings("nls")
    public synchronized Object get(Object key) {
        Objects.requireNonNull(key, "key must not be null");
        return properties.get(key);
    }

    @Override
    @SuppressWarnings("nls")
    public synchronized Object put(Object key, Object value) {
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(value, "value must not be null");
        return properties.put((String) key, (String) value);
    }

    @Override
    @SuppressWarnings("nls")
    public synchronized Object remove(Object key) {
        Objects.requireNonNull(key, "key must not be null");
        return properties.remove(key);
    }

    @Override
    public synchronized void clear() {
        properties.clear();
    }

    @Override
    public synchronized Object clone() {
        OrderedProperties clone = (OrderedProperties) super.clone();
        clone.properties = new LinkedHashMap<>(properties);
        return clone;
    }

    @Override
    public synchronized String toString() {
        return properties.toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<Object> keySet() {
        // the set does not support adding, so it's safe to cast
        return (Set<Object>) (Set<?>) properties.keySet();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<Map.Entry<Object, Object>> entrySet() {
        // the set does not support adding, but this may cause errors if the entries have their values changed
        return (Set<Map.Entry<Object, Object>>) (Set<?>) properties.entrySet();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Object> values() {
        // the set does not support adding, so it's safe to cast
        return (Collection<Object>) (Collection<?>) properties.values();
    }

    @Override
    public synchronized boolean equals(Object o) {
        return this == o
                || o instanceof Map<?, ?> && properties.equals(o);
    }

    @Override
    public synchronized int hashCode() {
        return properties.hashCode();
    }
}
