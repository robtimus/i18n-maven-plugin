/*
 * Messages.java
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

// This file has been generated by i18n-maven-plugin
// (https://robtimus.github.io/i18n-maven-plugin/)

package com.github.robtimus.maven.plugins.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@SuppressWarnings("nls")
final class Messages {

    private static final Map<Locale, ResourceBundle> BUNDLES = new HashMap<>();

    private Messages() {
        throw new IllegalStateException("cannot create instances of " + getClass().getName());
    }

    private static synchronized ResourceBundle getResourceBundle(Locale locale) {
        Locale l = nonNull(locale);
        ResourceBundle bundle = BUNDLES.get(l);
        if (bundle == null) {
            bundle = ResourceBundle.getBundle("com.github.robtimus.maven.plugins.i18n.i18n", l);
            BUNDLES.put(l, bundle);
        }
        return bundle;
    }

    private static String getString(Locale locale, String key) {
        ResourceBundle bundle = getResourceBundle(locale);
        return bundle.getString(key);
    }

    private static Locale nonNull(Locale locale) {
        return locale != null ? locale : Locale.getDefault(Locale.Category.FORMAT);
    }

    static final NoInputEncoding_ noInputEncoding = new NoInputEncoding_();

    static final class NoInputEncoding_ {

        private NoInputEncoding_() {
            super();
        }

        String get(Object arg) {
            return get(null, arg);
        }

        String get(Locale locale, Object arg) {
            Locale l = nonNull(locale);
            String s = getString(l, "noInputEncoding");
            return String.format(l, s, arg);
        }
    }

    static final NoOutputEncoding_ noOutputEncoding = new NoOutputEncoding_();

    static final class NoOutputEncoding_ {

        private NoOutputEncoding_() {
            super();
        }

        String get(Object arg) {
            return get(null, arg);
        }

        String get(Locale locale, Object arg) {
            Locale l = nonNull(locale);
            String s = getString(l, "noOutputEncoding");
            return String.format(l, s, arg);
        }
    }

    static final GeneratingClass_ generatingClass = new GeneratingClass_();

    static final class GeneratingClass_ {

        private GeneratingClass_() {
            super();
        }

        String get(
                Object arg1,
                Object arg2) {

            return get(null,
                    arg1,
                    arg2
            );
        }

        String get(Locale locale,
                Object arg1,
                Object arg2) {

            Locale l = nonNull(locale);
            String s = getString(l, "generatingClass");
            return String.format(l, s,
                    arg1,
                    arg2
            );
        }
    }
}
