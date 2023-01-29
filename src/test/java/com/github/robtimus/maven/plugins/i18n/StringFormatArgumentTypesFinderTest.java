/*
 * StringFormatArgumentTypesFinderTest.java
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import com.github.robtimus.maven.plugins.i18n.I18N.ArgumentTypesFinder;
import com.github.robtimus.maven.plugins.i18n.I18N.ArgumentTypesFinder.ArgumentTypes;

@SuppressWarnings("nls")
class StringFormatArgumentTypesFinderTest {

    private final ArgumentTypesFinder argumentTypesFinder = new StringFormatArgumentTypesFinder();

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = { "%b", "%B" })
    void testBoolean(String conversion) {
        String format = String.format("prefix %s postfix", conversion);

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(boolean.class, Boolean.class));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = { "%h", "%H" })
    void testHash(String conversion) {
        String format = String.format("prefix %s postfix", conversion);

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(Object.class));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = { "%s", "%S" })
    void testObject(String conversion) {
        String format = String.format("prefix %s postfix", conversion);

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(Object.class));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = { "%c", "%C" })
    void testChar(String conversion) {
        String format = String.format("prefix %s postfix", conversion);

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(char.class, Character.class));
    }

    @ParameterizedTest(name = "{0}")
    // decimal, octal, 2x hexadecimal
    @ValueSource(strings = { "%d", "%o", "%x", "%X" })
    void testInteger(String conversion) {
        String format = String.format("prefix %s postfix", conversion);

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(byte.class, Byte.class, short.class, Short.class,
                int.class, Integer.class, long.class, Long.class, BigInteger.class));
    }

    @ParameterizedTest(name = "{0}")
    // decimal, 2x computerized scientific, 2x general scientific
    @ValueSource(strings = { "%f", "%e", "%E", "%g", "%G" })
    void testDecimal(String conversion) {
        String format = String.format("prefix %s postfix", conversion);

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(),
                Matchers.<Class<?>>containsInAnyOrder(float.class, Float.class, double.class, Double.class, BigDecimal.class));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = { "%a", "%A" })
    void testHexadecimalExponential(String conversion) {
        String format = String.format("prefix %s postfix", conversion);

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(float.class, Float.class, double.class, Double.class));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = { "%tc", "%tc" })
    void testDateTime(String conversion) {
        String format = String.format("prefix %s postfix", conversion);

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(long.class, Long.class, Date.class, Calendar.class));
    }

    @Test
    void testPercent() {
        String format = "prefix %% postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(Collections.emptyList(), argumentTypesList);
    }

    @Test
    void testLineBreak() {
        String format = "prefix %n postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(Collections.emptyList(), argumentTypesList);
    }

    @Test
    void testComplexFormat() {
        String format = "%tF %<tT %tF %<tT %s %<s %s %<d %d %<d %<d %2$b%n";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(5, argumentTypesList.size());

        // %tF and %<tT
        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(long.class, Long.class, Date.class, Calendar.class));

        // %tF, %<tT and %2$b - only Object as common match
        argumentTypes = argumentTypesList.get(1);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(Object.class));

        // %s and %<s
        argumentTypes = argumentTypesList.get(2);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(Object.class));

        // %s and %<d - only Object as common match
        argumentTypes = argumentTypesList.get(3);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(Object.class));

        // %d, %<d and %<d
        argumentTypes = argumentTypesList.get(4);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(byte.class, Byte.class, short.class, Short.class,
                int.class, Integer.class, long.class, Long.class, BigInteger.class));
    }
}
