/*
 * I18NWriterTest.java
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
import static org.junit.Assert.assertNull;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Collections;
import org.junit.Test;

@SuppressWarnings({ "nls", "javadoc" })
public class I18NWriterTest {

    private static final String LICENSE_FILE_NAME = "TestFileName.java";
    private static final String LICENSE_COPYRIGHT_YEAR = "2017";
    private static final String LICENSE_COPYRIGHT_HOLDER = "Test User";

    @Test
    public void testGetLicenseTextNotSet() {
        I18N.Writer writer = createWriter(null);

        assertNull(writer.getLicenseText(LICENSE_FILE_NAME));
    }

    @Test
    public void testGetLicenseTextApache2() throws IOException {
        testGetLicenseTextPredefined("Apache-2.0");
    }

    @Test
    public void testGetLicenseTextBSD2Clause() throws IOException {
        testGetLicenseTextPredefined("BSD-2-Clause");
    }

    @Test
    public void testGetLicenseTextEPL10() throws IOException {
        testGetLicenseTextPredefined("EPL-1.0");
    }

    @Test
    public void testGetLicenseTextGPL20() throws IOException {
        testGetLicenseTextPredefined("GPL-2.0");
    }

    @Test
    public void testGetLicenseTextGPL30() throws IOException {
        testGetLicenseTextPredefined("GPL-3.0");
    }

    @Test
    public void testGetLicenseTextLGPL20() throws IOException {
        testGetLicenseTextPredefined("LGPL-2.0");
    }

    @Test
    public void testGetLicenseTextLGPL21() throws IOException {
        testGetLicenseTextPredefined("LGPL-2.1");
    }

    @Test
    public void testGetLicenseTextMIT() throws IOException {
        testGetLicenseTextPredefined("MIT");
    }

    @Test
    public void testGetLicenseTextMPL20() throws IOException {
        testGetLicenseTextPredefined("MPL-2.0");
    }

    private void testGetLicenseTextPredefined(String predefined) throws IOException {
        final String textTemplate = readResource("licenses/" + predefined);
        final String expected = readResource("licenses/" + predefined + "-filled");

        I18N.Writer writer = createWriter(new License(textTemplate, LICENSE_COPYRIGHT_YEAR, LICENSE_COPYRIGHT_HOLDER));

        String licenseText = writer.getLicenseText(LICENSE_FILE_NAME);
        assertEquals(expected, licenseText);
    }

    @Test
    public void testGetLicenseCopyrightHolder() {
      I18N.Writer writer = createWriter(new License(null, null, "Test User"));

      assertEquals("Test User", writer.getLicenseCopyrightHolder());

      writer = createWriter(new License(null, null, null));

      assertEquals(System.getProperty("user.name"), writer.getLicenseCopyrightHolder());
    }

    @Test
    public void testGetLicenseCopyrightYear() {
      I18N.Writer writer = createWriter(new License(null, "2016", null));

      assertEquals("2016", writer.getLicenseCopyrightYear());

      writer = createWriter(new License(null, null, null));

      int year = Calendar.getInstance().get(Calendar.YEAR);
      assertEquals(Integer.toString(year), writer.getLicenseCopyrightYear());
    }

    private I18N.Writer createWriter(License license) {
        return new I18N.Writer(StandardCharsets.UTF_8, true, license, false, Collections.<String>emptySet());
    }

    private String readResource(String resource) throws IOException {
        try (Reader input = new InputStreamReader(getClass().getResourceAsStream(resource), StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[4096];
            int len;
            while ((len = input.read(buffer)) != -1) {
                sb.append(buffer, 0, len);
            }
            return sb.toString();
        }
    }
}
