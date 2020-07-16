/*
 * OrderedPropertiesTest.java
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class OrderedPropertiesTest {

    private static final String EXISTING_KEY = "generatingClass";
    private static final String MISSING_KEY = UUID.randomUUID().toString();

    private static final String EXISTING_VALUE = "Generating I18N class %s from I18N bundle %s";
    private static final String MISSING_VALUE = UUID.randomUUID().toString();

    private Properties ordered;
    private Properties regular;

    @BeforeEach
    void readProperties() {
        ordered = readOrdered();
        regular = readRegular();

        assertTrue(regular.containsKey(EXISTING_KEY));
        assertTrue(regular.containsValue(EXISTING_VALUE));
    }

    @Test
    void testSetProperty() {
        assertEquals(regular.setProperty(EXISTING_KEY, "value1"), ordered.setProperty(EXISTING_KEY, "value1"));
        assertEquals(regular.setProperty(MISSING_KEY, "value2"), ordered.setProperty(MISSING_KEY, "value2"));

        assertEquals(regular.getProperty(EXISTING_KEY), ordered.getProperty(EXISTING_KEY));
        assertEquals(regular.getProperty(MISSING_KEY), ordered.getProperty(MISSING_KEY));
    }

    @Test
    void testGetProperty() {
        assertEquals(regular.getProperty(EXISTING_KEY), ordered.getProperty(EXISTING_KEY));
        assertEquals(regular.getProperty(MISSING_KEY), ordered.getProperty(MISSING_KEY));

        assertEquals(regular.getProperty(EXISTING_KEY, "value1"), ordered.getProperty(EXISTING_KEY, "value1"));
        assertEquals(regular.getProperty(MISSING_KEY, "value2"), ordered.getProperty(MISSING_KEY, "value2"));

        assertEquals(regular.getProperty(EXISTING_KEY, null), ordered.getProperty(EXISTING_KEY, null));
        assertEquals(regular.getProperty(MISSING_KEY, null), ordered.getProperty(MISSING_KEY, null));
    }

    @Test
    void testPropertyNames() {
        testEnumeration(ordered.propertyNames(), regular.propertyNames());
    }

    @Test
    void testStringPropertyNames() {
        assertEquals(regular.stringPropertyNames(), ordered.stringPropertyNames());

        ordered.stringPropertyNames().remove(EXISTING_KEY);
        regular.stringPropertyNames().remove(EXISTING_KEY);

        assertEquals(regular.stringPropertyNames(), ordered.stringPropertyNames());

        ordered.remove(EXISTING_KEY);
        regular.remove(EXISTING_KEY);

        assertEquals(regular.stringPropertyNames(), ordered.stringPropertyNames());

        ordered.setProperty(EXISTING_KEY, "value1");
        regular.setProperty(EXISTING_KEY, "value1");

        assertEquals(regular.stringPropertyNames(), ordered.stringPropertyNames());

        ordered.clear();
        regular.clear();

        assertEquals(regular.stringPropertyNames(), ordered.stringPropertyNames());
    }

    @Test
    void testSize() {
        assertEquals(regular.size(), ordered.size());

        ordered.remove(EXISTING_KEY);
        regular.remove(EXISTING_KEY);

        assertEquals(regular.size(), ordered.size());

        ordered.setProperty(EXISTING_KEY, "value1");
        regular.setProperty(EXISTING_KEY, "value1");

        assertEquals(regular.size(), ordered.size());

        ordered.clear();
        regular.clear();

        assertEquals(regular.size(), ordered.size());
    }

    @Test
    void testIsEmpty() {
        assertEquals(regular.isEmpty(), ordered.isEmpty());

        ordered.remove(EXISTING_KEY);
        regular.remove(EXISTING_KEY);

        assertEquals(regular.isEmpty(), ordered.isEmpty());

        ordered.setProperty(EXISTING_KEY, "value1");
        regular.setProperty(EXISTING_KEY, "value1");

        assertEquals(regular.isEmpty(), ordered.isEmpty());

        ordered.clear();
        regular.clear();

        assertEquals(regular.isEmpty(), ordered.isEmpty());
    }

    @Test
    void testKeys() {
        testEnumeration(ordered.keys(), regular.keys());
    }

    @Test
    void testElements() {
        testEnumeration(ordered.elements(), regular.elements());
    }

    @Test
    void testContains() {
        assertEquals(regular.contains(EXISTING_KEY), ordered.contains(EXISTING_KEY));
        assertEquals(regular.contains(MISSING_KEY), ordered.contains(MISSING_KEY));
    }

    @Test
    void testContainsKey() {
        assertEquals(regular.containsKey(EXISTING_KEY), ordered.containsKey(EXISTING_KEY));
        assertEquals(regular.containsKey(MISSING_KEY), ordered.containsKey(MISSING_KEY));
    }

    @Test
    void testContainsValue() {
        assertEquals(regular.containsValue(EXISTING_VALUE), ordered.containsValue(EXISTING_VALUE));
        assertEquals(regular.containsValue(MISSING_VALUE), ordered.containsValue(MISSING_VALUE));
    }

    @Test
    void testGet() {
        assertEquals(regular.get(EXISTING_KEY), ordered.get(EXISTING_KEY));
        assertEquals(regular.get(MISSING_KEY), ordered.get(MISSING_KEY));

        assertEquals(regular.get(1), ordered.get(1));
    }

    @Test
    void testPut() {
        assertEquals(regular.put(EXISTING_KEY, "value1"), ordered.put(EXISTING_KEY, "value1"));
        assertEquals(regular.put(MISSING_KEY, "value2"), ordered.put(MISSING_KEY, "value2"));

        assertEquals(regular.get(EXISTING_KEY), ordered.get(EXISTING_KEY));
        assertEquals(regular.get(MISSING_KEY), ordered.get(MISSING_KEY));
    }

    @Test
    void testRemove() {
        assertEquals(regular.remove(EXISTING_KEY), ordered.remove(EXISTING_KEY));
        assertEquals(regular.remove(MISSING_KEY), ordered.remove(MISSING_KEY));

        assertEquals(regular.get(EXISTING_KEY), ordered.get(EXISTING_KEY));
        assertEquals(regular.get(MISSING_KEY), ordered.get(MISSING_KEY));
    }

    @Test
    void testClear() {
        ordered.clear();
        regular.clear();

        assertEquals(regular.isEmpty(), ordered.isEmpty());
    }

    @Test
    void testClone() {
        Properties orderedClone = (Properties) ordered.clone();
        Properties regularClone = (Properties) regular.clone();

        assertEquals(regularClone.keySet(), orderedClone.keySet());
    }

    private Properties readOrdered() {
        try (InputStream input = getClass().getResourceAsStream("i18n.properties")) {
            OrderedProperties properties = new OrderedProperties();
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Properties readRegular() {
        try (InputStream input = getClass().getResourceAsStream("i18n.properties")) {
            Properties properties = new Properties();
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void testEnumeration(Enumeration<?> orderedEnumeration, Enumeration<?> regularEnumeration) {
        Set<Object> orderedElements = new HashSet<>();
        Set<Object> regularElements = new HashSet<>();

        while (regularEnumeration.hasMoreElements()) {
            assertTrue(orderedEnumeration.hasMoreElements());

            orderedElements.add(orderedEnumeration.nextElement());
            regularElements.add(regularEnumeration.nextElement());
        }
        assertFalse(orderedEnumeration.hasMoreElements());

        assertEquals(regularElements, orderedElements);
    }
}
