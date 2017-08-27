/*
 * GenerateMojoTest.java
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Properties;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Test;

@SuppressWarnings({ "nls", "javadoc" })
public class GenerateMojoTest {

    @Test
    public void testReadProperties() throws MojoExecutionException {
        GenerateMojo mojo = new GenerateMojo();
        mojo.sourceDirectory = new File("src/main/resources");
        mojo.bundleName = "com.github.robtimus.maven.plugins.i18n.i18n";
        mojo.inputEncoding = "UTF-8";

        Properties properties = mojo.readProperties();
        Enumeration<?> keys = properties.keys();

        assertTrue(keys.hasMoreElements());
        assertEquals("noInputEncoding", keys.nextElement());

        assertTrue(keys.hasMoreElements());
        assertEquals("noOutputEncoding", keys.nextElement());

        assertTrue(keys.hasMoreElements());
        assertEquals("generatingClass", keys.nextElement());

        assertFalse(keys.hasMoreElements());
    }

    @Test
    public void testGetInputCharsetNotSet() {
        GenerateMojo mojo = new GenerateMojo();
        Log log = mock(Log.class);
        mojo.setLog(log);

        Charset defaultCharset = Charset.defaultCharset();

        assertEquals(defaultCharset, mojo.getInputCharset());

        verify(log).warn(Messages.noInputEncoding.get(defaultCharset));
    }

    @Test
    public void testGetInputCharsetSet() {
        GenerateMojo mojo = new GenerateMojo();
        Log log = mock(Log.class);
        mojo.setLog(log);

        mojo.inputEncoding = "UTF-8";
        assertEquals(StandardCharsets.UTF_8, mojo.getInputCharset());

        verify(log, never()).warn(any(CharSequence.class));

        mojo.inputEncoding = "US-ASCII";
        assertEquals(StandardCharsets.US_ASCII, mojo.getInputCharset());

        verify(log, never()).warn(any(CharSequence.class));
    }

    @Test
    public void testGetOutputCharsetNotSet() {
        GenerateMojo mojo = new GenerateMojo();
        Log log = mock(Log.class);
        mojo.setLog(log);

        Charset defaultCharset = Charset.defaultCharset();

        assertEquals(defaultCharset, mojo.getOutputCharset());

        verify(log).warn(Messages.noOutputEncoding.get(defaultCharset));
    }

    @Test
    public void testGetOutputCharsetSet() {
        GenerateMojo mojo = new GenerateMojo();
        Log log = mock(Log.class);
        mojo.setLog(log);

        mojo.outputEncoding = "UTF-8";
        assertEquals(StandardCharsets.UTF_8, mojo.getOutputCharset());

        verify(log, never()).warn(any(CharSequence.class));

        mojo.outputEncoding = "US-ASCII";
        assertEquals(StandardCharsets.US_ASCII, mojo.getOutputCharset());

        verify(log, never()).warn(any(CharSequence.class));
    }

    @Test
    public void testGetLicenseTextNotSet() throws MojoExecutionException {
        GenerateMojo mojo = new GenerateMojo();

        assertNull(mojo.getLicenseText());
    }

    @Test
    public void testGetLicenseTextApache2() throws MojoExecutionException, IOException {
        testGetLicenseTextPredefined("Apache-2.0");
    }

    @Test
    public void testGetLicenseTextBSD2Clause() throws MojoExecutionException, IOException {
        testGetLicenseTextPredefined("BSD-2-Clause");
    }

    @Test
    public void testGetLicenseTextEPL10() throws MojoExecutionException, IOException {
        testGetLicenseTextPredefined("EPL-1.0");
    }

    @Test
    public void testGetLicenseTextGPL20() throws MojoExecutionException, IOException {
        testGetLicenseTextPredefined("GPL-2.0");
    }

    @Test
    public void testGetLicenseTextGPL30() throws MojoExecutionException, IOException {
        testGetLicenseTextPredefined("GPL-3.0");
    }

    @Test
    public void testGetLicenseTextLGPL20() throws MojoExecutionException, IOException {
        testGetLicenseTextPredefined("LGPL-2.0");
    }

    @Test
    public void testGetLicenseTextLGPL21() throws MojoExecutionException, IOException {
        testGetLicenseTextPredefined("LGPL-2.1");
    }

    @Test
    public void testGetLicenseTextMIT() throws MojoExecutionException, IOException {
        testGetLicenseTextPredefined("MIT");
    }

    @Test
    public void testGetLicenseTextMPL20() throws MojoExecutionException, IOException {
        testGetLicenseTextPredefined("MPL-2.0");
    }

    private void testGetLicenseTextPredefined(String predefined) throws MojoExecutionException, IOException {
        GenerateMojo mojo = new GenerateMojo();
        mojo.licenseText = predefined;

        assertEquals(readResource("licenses/" + predefined), mojo.getLicenseText());
    }

    @Test
    public void testGetLicenseTextFromFile() throws MojoExecutionException, IOException {
        GenerateMojo mojo = new GenerateMojo();
        mojo.licenseText = "src/test/resources/com/github/robtimus/maven/plugins/i18n/test-license";

        assertEquals(readResource("test-license"), mojo.getLicenseText());
    }

    @Test
    public void testGetLicenseTextFromURL() throws MojoExecutionException, IOException {
        GenerateMojo mojo = new GenerateMojo();
        // use a URL to a file
        mojo.licenseText = new File("src/test/resources/com/github/robtimus/maven/plugins/i18n/test-license").toURI().toURL().toString();

        assertEquals(readResource("test-license"), mojo.getLicenseText());
    }

    @Test
    public void testGetLicenseTextFromText() throws MojoExecutionException {
        GenerateMojo mojo = new GenerateMojo();
        mojo.licenseText = "Dummy license text";

        assertEquals(mojo.licenseText, mojo.getLicenseText());
    }

    @Test
    public void testGetI18NClassNameFromPackagedBundle() {
        GenerateMojo mojo = new GenerateMojo();
        mojo.bundleName = "com.github.robtimus.maven.plugins.i18n.i18n";

        assertEquals("com.github.robtimus.maven.plugins.i18n.I18n", mojo.getI18NClassName());
    }

    @Test
    public void testGetI18NClassNameFromNonPackagedBundle() {
        GenerateMojo mojo = new GenerateMojo();
        mojo.bundleName = "i18n";

        assertEquals("I18n", mojo.getI18NClassName());
    }

    @Test
    public void testGetI18NClassNameExplicitlySet() {
        GenerateMojo mojo = new GenerateMojo();
        mojo.className = "test.I18N";

        assertEquals(mojo.className, mojo.getI18NClassName());
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
