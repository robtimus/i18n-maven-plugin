/*
 * License.java
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

class License {

    private final String text;
    private final String copyrightYear;
    private final String copyrightHolder;

    License(String text, String copyrightYear, String copyrightHolder) {
        this.text = text;
        this.copyrightYear = copyrightYear;
        this.copyrightHolder = copyrightHolder;
    }

    String getText() {
        return text;
    }

    String getCopyrightYear() {
        return copyrightYear;
    }

    String getCopyrightHolder() {
        return copyrightHolder;
    }
}
