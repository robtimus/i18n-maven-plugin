/*
 * StringFormatArgumentTypesFinder.java
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.github.robtimus.maven.plugins.i18n.I18N.ArgumentTypesFinder;

final class StringFormatArgumentTypesFinder implements ArgumentTypesFinder {

    // %[argument_index$][flags][width][.precision][t]conversion
    private static final String FORMAT_SPECIFIER = "%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])"; //$NON-NLS-1$
    private static final Pattern FORMAT_SPECIFIER_PATTERN = Pattern.compile(FORMAT_SPECIFIER);

    @Override
    public List<ArgumentTypes> findArgumentTypes(String formatOrPattern) {
        List<FormatSpecifier> formatSpecifiers = parse(formatOrPattern);

        List<ArgumentTypes> result = new ArrayList<>(formatSpecifiers.size());

        int lastIndex = -1;
        int lastOrdinaryIndex = -1;

        for (FormatSpecifier fs : formatSpecifiers) {
            switch (fs.index) {
            case FormatSpecifier.LITERAL_INDEX:
                break;
            case FormatSpecifier.RELATIVE_INDEX:
                // if lastIndex < 0, let formatting itself trigger an error
                if (lastIndex >= 0) {
                    setArgumentTypes(result, lastIndex, fs.types);
                }
                break;
            case FormatSpecifier.ORDINARY_INDEX:
                lastOrdinaryIndex++;
                lastIndex = lastOrdinaryIndex;
                setArgumentTypes(result, lastIndex, fs.types);
                break;
            default:
                lastIndex = fs.index - 1;
                setArgumentTypes(result, lastIndex, fs.types);
                break;
            }
        }

        for (ListIterator<ArgumentTypes> i = result.listIterator(); i.hasNext(); ) {
            ArgumentTypes types = i.next();
            if (types == null) {
                i.set(OBJECT);
            }
        }

        return result;
    }

    private List<FormatSpecifier> parse(String format) {
        List<FormatSpecifier> result = new ArrayList<>();

        Matcher matcher = FORMAT_SPECIFIER_PATTERN.matcher(format);
        while (matcher.find()) {
            FormatSpecifier argumentType = new FormatSpecifier(matcher);
            result.add(argumentType);
        }
        return result;
    }

    private void setArgumentTypes(List<ArgumentTypes> result, int index, ArgumentTypes types) {
        ensureIndex(result, index);
        ArgumentTypes oldTypes = result.get(index);
        if (oldTypes == null) {
            result.set(index, types);
        } else {
            oldTypes.retainTypes(types);
        }
    }

    private void ensureIndex(List<?> list, int index) {
        while (list.size() <= index) {
            list.add(null);
        }
    }

    private static final class FormatSpecifier {
        private static final int LITERAL_INDEX = -2;
        private static final int RELATIVE_INDEX = -1;
        private static final int ORDINARY_INDEX = 0;

        private final String format;
        private final int index;
        private final ArgumentTypes types;

        private FormatSpecifier(Matcher matcher) {
            format = matcher.group();
            char c = matcher.group(6).charAt(0);
            index = getIndex(matcher, c);
            types = getTypes(matcher, c);
        }

        private int getIndex(Matcher matcher, char c) {
            if (isText(c)) {
                return LITERAL_INDEX;
            }
            String s = matcher.group(1);
            if (s != null) {
                try {
                    return Integer.parseInt(s.substring(0, s.length() - 1));
                } catch (@SuppressWarnings("unused") NumberFormatException e) {
                    assert false;
                }
            }
            String f = matcher.group(2);
            if (f != null && f.indexOf('<') != -1) {
                return RELATIVE_INDEX;
            }
            return ORDINARY_INDEX;
        }

        private ArgumentTypes getTypes(Matcher matcher, char c) {
            String tT = matcher.group(5);
            boolean datetime = tT != null;
            if (datetime) {
                return new ArgumentTypes(long.class, Long.class, Calendar.class, Date.class);
            }
            return getTypes(c);
        }

        private ArgumentTypes getTypes(char c) {
            if (isText(c)) {
                // will be ignored
                return OBJECT;
            }
            switch (c) {
            case 'b':
            case 'B':
                return new ArgumentTypes(boolean.class, Boolean.class);
            case 'h':
            case 'H':
            case 's':
            case 'S':
                return OBJECT;
            case 'c':
            case 'C':
                return new ArgumentTypes(char.class, Character.class);
            case 'd':
            case 'o':
            case 'x':
            case 'X':
                return new ArgumentTypes(byte.class, Byte.class, short.class, Short.class, int.class, Integer.class, long.class, Long.class,
                        BigInteger.class);
            case 'e':
            case 'E':
            case 'g':
            case 'G':
            case 'f':
                return new ArgumentTypes(float.class, Float.class, double.class, Double.class, BigDecimal.class);
            case 'a':
            case 'A':
                return new ArgumentTypes(float.class, Float.class, double.class, Double.class);
            default:
                return OBJECT;
            }
        }

        private static boolean isText(char c) {
            return c == 'n' || c == '%';
        }

        @Override
        public String toString() {
            return format;
        }
    }
}
