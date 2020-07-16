/*
 * I18NParserTest.java
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
import java.util.Iterator;
import java.util.Properties;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class I18NParserTest {

    @Test
    void testParse() {
        Properties properties = new OrderedProperties();
        properties.setProperty("test.value", "value1");
        properties.setProperty("test.node", "value2");
        properties.setProperty("test.node.sub", "value3");

        I18N.Node root = new I18N.Parser().parse(properties);
        assertNode(root, "", null, false, 1);

        I18N.Node test = root.children().iterator().next();
        assertNode(test, "test", null, false, 2);

        Iterator<I18N.Node> iterator = test.children().iterator();
        I18N.Node value = iterator.next();
        I18N.Node node = iterator.next();

        assertNode(value, "value", "value1", true, 0);

        assertNode(node, "node", "value2", true, 1);

        I18N.Node sub = node.children().iterator().next();
        assertNode(sub, "sub", "value3", true, 0);
    }

    private static void assertNode(I18N.Node node, String name, String value, boolean isLeaf, int childCount) {
        assertEquals(name, node.name());
        assertEquals(value, node.getValue());
        assertEquals(isLeaf, node.isLeaf());
        assertEquals(childCount, node.children().size());
    }
}
