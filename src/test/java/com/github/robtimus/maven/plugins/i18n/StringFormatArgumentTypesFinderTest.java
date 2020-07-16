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
import com.github.robtimus.maven.plugins.i18n.I18N.ArgumentTypesFinder;
import com.github.robtimus.maven.plugins.i18n.I18N.ArgumentTypesFinder.ArgumentTypes;

@SuppressWarnings("nls")
class StringFormatArgumentTypesFinderTest {

    private final ArgumentTypesFinder argumentTypesFinder = new StringFormatArgumentTypesFinder();

    @Test
    void testBooleanLowerCase() {
        String format = "prefix %b postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(boolean.class, Boolean.class));
    }

    @Test
    void testBooleanUpperCase() {
        String format = "prefix %B postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(boolean.class, Boolean.class));
    }

    @Test
    void testHashLowerCase() {
        String format = "prefix %h postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(Object.class));
    }

    @Test
    void testHashUpperCase() {
        String format = "prefix %H postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(Object.class));
    }

    @Test
    void testObjectLowerCase() {
        String format = "prefix %s postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(Object.class));
    }

    @Test
    void testObjectUpperCase() {
        String format = "prefix %s postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(Object.class));
    }

    @Test
    void testCharLowerCase() {
        String format = "prefix %c postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(char.class, Character.class));
    }

    @Test
    void testCharUpperCase() {
        String format = "prefix %C postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(char.class, Character.class));
    }

    @Test
    void testDecimalInteger() {
        String format = "prefix %d postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(byte.class, Byte.class, short.class, Short.class,
                int.class, Integer.class, long.class, Long.class, BigInteger.class));
    }

    @Test
    void testOctalInteger() {
        String format = "prefix %o postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(byte.class, Byte.class, short.class, Short.class,
                int.class, Integer.class, long.class, Long.class, BigInteger.class));
    }

    @Test
    void testHexadecimalIntegerLowerCase() {
        String format = "prefix %x postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(byte.class, Byte.class, short.class, Short.class,
                int.class, Integer.class, long.class, Long.class, BigInteger.class));
    }

    @Test
    void testHexadecimalIntegerUpperCase() {
        String format = "prefix %X postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(byte.class, Byte.class, short.class, Short.class,
                int.class, Integer.class, long.class, Long.class, BigInteger.class));
    }

    @Test
    void testComputerizedScientificLowerCase() {
        String format = "prefix %e postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(),
                Matchers.<Class<?>>containsInAnyOrder(float.class, Float.class, double.class, Double.class, BigDecimal.class));
    }

    @Test
    void testComputerizedScientificUpperCase() {
        String format = "prefix %E postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(),
                Matchers.<Class<?>>containsInAnyOrder(float.class, Float.class, double.class, Double.class, BigDecimal.class));
    }

    @Test
    void testGeneralScientificLowerCase() {
        String format = "prefix %g postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(),
                Matchers.<Class<?>>containsInAnyOrder(float.class, Float.class, double.class, Double.class, BigDecimal.class));
    }

    @Test
    void testGeneralScientificUpperCase() {
        String format = "prefix %G postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(),
                Matchers.<Class<?>>containsInAnyOrder(float.class, Float.class, double.class, Double.class, BigDecimal.class));
    }

    @Test
    void testDecimal() {
        String format = "prefix %f postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(),
                Matchers.<Class<?>>containsInAnyOrder(float.class, Float.class, double.class, Double.class, BigDecimal.class));
    }

    @Test
    void testHexadecimalExponentialLowerCase() {
        String format = "prefix %a postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(float.class, Float.class, double.class, Double.class));
    }

    @Test
    void testHexadecimalExponentialUpperCase() {
        String format = "prefix %A postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(float.class, Float.class, double.class, Double.class));
    }

    @Test
    void testDateTimeLowerCase() {
        String format = "prefix %tc postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(format);

        assertEquals(1, argumentTypesList.size());

        ArgumentTypes argumentTypes = argumentTypesList.get(0);
        assertThat(argumentTypes.getTypes(), Matchers.<Class<?>>containsInAnyOrder(long.class, Long.class, Date.class, Calendar.class));
    }

    @Test
    void testDateTimeUpperCase() {
        String format = "prefix %tc postfix";

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
