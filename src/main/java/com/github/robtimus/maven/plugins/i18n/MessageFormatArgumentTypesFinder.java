/*
 * MessageFormatArgumentTypesFinder.java
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

import java.text.Format;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import com.github.robtimus.maven.plugins.i18n.I18N.ArgumentTypesFinder;

final class MessageFormatArgumentTypesFinder implements ArgumentTypesFinder {

    @Override
    public List<ArgumentTypes> findArgumentTypes(String formatOrPattern) {
        // Use MessageFormat itself to get the number of arguments
        MessageFormat format = new MessageFormat(formatOrPattern);
        Format[] formats = format.getFormatsByArgumentIndex();
        int argumentCount = formats.length;

        List<ArgumentTypes> argumentTypes = new ArrayList<>(argumentCount);
        // can't make a distinction between types, just use Object for all
        for (int i = 0; i < argumentCount; i++) {
            argumentTypes.add(ArgumentTypes.OBJECT);
        }

        return argumentTypes;
    }
}
