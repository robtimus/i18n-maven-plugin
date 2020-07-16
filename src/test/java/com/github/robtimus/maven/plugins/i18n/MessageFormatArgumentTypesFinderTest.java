/*
 * MessageFormatArgumentTypesFinderTest.java
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
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.github.robtimus.maven.plugins.i18n.I18N.ArgumentTypesFinder;
import com.github.robtimus.maven.plugins.i18n.I18N.ArgumentTypesFinder.ArgumentTypes;

@SuppressWarnings("nls")
class MessageFormatArgumentTypesFinderTest {

    private final ArgumentTypesFinder argumentTypesFinder = new MessageFormatArgumentTypesFinder();

    @Test
    void testNoArguments() {
        String pattern = "prefix postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(pattern);

        assertEquals(Collections.emptyList(), argumentTypesList);
    }

    @Test
    void testMultipleArgumentsInOrder() {
        String pattern = "prefix {0} infix {1} postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(pattern);

        assertEquals(2, argumentTypesList.size());

        assertThat(argumentTypesList, everyItem(is(ArgumentTypesFinder.OBJECT)));
    }

    @Test
    void testMultipleArgumentsOutOfOrder() {
        String pattern = "prefix {1} infix {0} postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(pattern);

        assertEquals(2, argumentTypesList.size());

        assertThat(argumentTypesList, everyItem(is(ArgumentTypesFinder.OBJECT)));
    }

    @Test
    void testMultipleArgumentsWithGap() {
        String pattern = "prefix {5} infix {2} postfix";

        List<ArgumentTypes> argumentTypesList = argumentTypesFinder.findArgumentTypes(pattern);

        assertEquals(6, argumentTypesList.size());

        assertThat(argumentTypesList, everyItem(is(ArgumentTypesFinder.OBJECT)));
    }
}
