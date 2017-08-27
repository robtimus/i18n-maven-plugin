/*
 * I18NVTLHelperTest.java
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

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.github.robtimus.maven.plugins.i18n.I18N.Node;

@SuppressWarnings({ "nls", "javadoc" })
public class I18NVTLHelperTest {

    private final I18N.VTLHelper helper = new I18N.VTLHelper();

    @Test
    public void testVarName() {
        for (String keyWord : I18N.VTLHelper.KEYWORDS) {
            testVarName(keyWord, "_" + keyWord);
        }
        for (String literal : I18N.VTLHelper.LITERALS) {
            testVarName(literal, "_" + literal);
        }

        testVarName("123", "_123");
        testVarName("_123", "_123");
        testVarName("a123", "a123");
    }

    private void testVarName(String nodeName, String expected) {
        I18N.Node node = createNamedNode(nodeName);
        assertEquals(expected, helper.varName(node));
    }

    @Test
    public void testClassName() {
        for (String keyWord : I18N.VTLHelper.KEYWORDS) {
            String capitalizedKeyword = capitalize(keyWord);
            testClassName(keyWord, "_" + capitalizedKeyword + "_", "_" + capitalizedKeyword + "__", "_" + capitalizedKeyword + "___");
        }
        for (String literal : I18N.VTLHelper.LITERALS) {
            String capitalizedLiteral = capitalize(literal);
            testClassName(literal, "_" + capitalizedLiteral + "_", "_" + capitalizedLiteral + "__", "_" + capitalizedLiteral + "___");
        }

        testClassName("123", "_123_", "_123__", "_123___");
        testClassName("123", "_123_", "_123__", "_123___");
        testClassName("a123", "A123_", "A123__", "A123___");
    }

    private void testClassName(String nodeName, String expectedFirst, String expectedSecond, String expectedThird) {
        Node node = createNamedNode(nodeName);
        I18N.Stack<String> classNames = helper.stack();

        String className = helper.className(node, classNames);
        assertEquals(expectedFirst, className);

        classNames.push(className);
        className = helper.className(node, classNames);
        assertEquals(expectedSecond, className);

        classNames.push(className);
        className = helper.className(node, classNames);
        assertEquals(expectedThird, className);
    }

    private static I18N.Node createNamedNode(String name) {
        return new I18N.Node(name, name, true, name);
    }

    private static String capitalize(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}
