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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class GenerateMojoTest {

    @Test
    void testReadProperties() throws MojoExecutionException, IOException {
        GenerateMojo mojo = new GenerateMojo();
        mojo.sourceDirectory = new File("src/main/resources");
        mojo.bundleName = "com.github.robtimus.maven.plugins.i18n.i18n";
        mojo.inputEncoding = "UTF-8";

        Map<String, String> properties = mojo.readProperties();

        Iterator<String> keyIterator = properties.keySet().iterator();

        assertTrue(keyIterator.hasNext());
        assertEquals("noInputEncoding", keyIterator.next());

        assertTrue(keyIterator.hasNext());
        assertEquals("noOutputEncoding", keyIterator.next());

        assertTrue(keyIterator.hasNext());
        assertEquals("generatingClass", keyIterator.next());

        assertFalse(keyIterator.hasNext());

        Properties expected = new Properties();
        try (InputStream input = getClass().getResourceAsStream("i18n.properties")) {
            expected.load(input);
        }

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            assertEquals(expected.getProperty(entry.getKey()), entry.getValue());
        }
    }

    @Nested
    class GetInputCharset {

        @Test
        void testNotSet() {
            GenerateMojo mojo = new GenerateMojo();
            Log log = mock(Log.class);
            mojo.setLog(log);

            Charset defaultCharset = Charset.defaultCharset();

            assertEquals(defaultCharset, mojo.getInputCharset());

            verify(log).warn(Messages.noInputEncoding(defaultCharset));
        }

        @Test
        void testSet() {
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
    }

    @Nested
    class GetOutputCharset {

        @Test
        void testNotSet() {
            GenerateMojo mojo = new GenerateMojo();
            Log log = mock(Log.class);
            mojo.setLog(log);

            Charset defaultCharset = Charset.defaultCharset();

            assertEquals(defaultCharset, mojo.getOutputCharset());

            verify(log).warn(Messages.noOutputEncoding(defaultCharset));
        }

        @Test
        void testSet() {
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
    }

    @Nested
    class GetLicenseText {

        @Test
        void testNotSet() throws MojoExecutionException {
            GenerateMojo mojo = new GenerateMojo();

            assertNull(mojo.getLicenseText());
        }

        @Test
        void testApache2() throws MojoExecutionException, IOException {
            testPredefined("Apache-2.0");
        }

        @Test
        void testBSD2Clause() throws MojoExecutionException, IOException {
            testPredefined("BSD-2-Clause");
        }

        @Test
        void testEPL10() throws MojoExecutionException, IOException {
            testPredefined("EPL-1.0");
        }

        @Test
        void testGPL20() throws MojoExecutionException, IOException {
            testPredefined("GPL-2.0");
        }

        @Test
        void testGPL30() throws MojoExecutionException, IOException {
            testPredefined("GPL-3.0");
        }

        @Test
        void testLGPL20() throws MojoExecutionException, IOException {
            testPredefined("LGPL-2.0");
        }

        @Test
        void testLGPL21() throws MojoExecutionException, IOException {
            testPredefined("LGPL-2.1");
        }

        @Test
        void testMIT() throws MojoExecutionException, IOException {
            testPredefined("MIT");
        }

        @Test
        void testMPL20() throws MojoExecutionException, IOException {
            testPredefined("MPL-2.0");
        }

        private void testPredefined(String predefined) throws MojoExecutionException, IOException {
            GenerateMojo mojo = new GenerateMojo();
            mojo.licenseText = predefined;

            assertEquals(readResource("licenses/" + predefined), mojo.getLicenseText());
        }

        @Test
        void testFromFile() throws MojoExecutionException, IOException {
            GenerateMojo mojo = new GenerateMojo();
            mojo.licenseText = "src/test/resources/com/github/robtimus/maven/plugins/i18n/test-license";

            assertEquals(readResource("test-license"), mojo.getLicenseText());
        }

        @Test
        void testFromURL() throws MojoExecutionException, IOException {
            GenerateMojo mojo = new GenerateMojo();
            // use a URL to a file
            mojo.licenseText = new File("src/test/resources/com/github/robtimus/maven/plugins/i18n/test-license").toURI().toURL().toString();

            assertEquals(readResource("test-license"), mojo.getLicenseText());
        }

        @Test
        void testFromText() throws MojoExecutionException {
            GenerateMojo mojo = new GenerateMojo();
            mojo.licenseText = "Dummy license text";

            assertEquals(mojo.licenseText, mojo.getLicenseText());
        }
    }

    @Nested
    class GetI18NClassName {

        @Test
        void testFromPackagedBundle() {
            GenerateMojo mojo = new GenerateMojo();
            mojo.bundleName = "com.github.robtimus.maven.plugins.i18n.i18n";

            assertEquals("com.github.robtimus.maven.plugins.i18n.I18n", mojo.getI18NClassName());
        }

        @Test
        void testFromNonPackagedBundle() {
            GenerateMojo mojo = new GenerateMojo();
            mojo.bundleName = "i18n";

            assertEquals("I18n", mojo.getI18NClassName());
        }

        @Test
        void testExplicitlySet() {
            GenerateMojo mojo = new GenerateMojo();
            mojo.className = "test.I18N";

            assertEquals(mojo.className, mojo.getI18NClassName());
        }
    }

    @Nested
    class AppendSourcePath {

        @Test
        void testScopeCompileNotAdded() {
            GenerateMojo mojo = new GenerateMojo();
            mojo.scope = Scope.compile;
            mojo.outputDirectory = new File("target/generated-sources/i18n");
            mojo.project = mock(MavenProject.class);

            List<String> roots = new ArrayList<>();

            when(mojo.project.getCompileSourceRoots()).thenReturn(roots);

            mojo.appendSourcePath();

            assertEquals(Arrays.asList(mojo.outputDirectory.getAbsolutePath()), roots);
        }

        @Test
        void testScopeCompileAddedRelative() {
            GenerateMojo mojo = new GenerateMojo();
            mojo.scope = Scope.compile;
            mojo.outputDirectory = new File("target/generated-sources/i18n");
            mojo.project = mock(MavenProject.class);

            List<String> roots = new ArrayList<>();
            roots.add(mojo.outputDirectory.getPath());

            when(mojo.project.getCompileSourceRoots()).thenReturn(roots);

            mojo.appendSourcePath();

            assertEquals(Arrays.asList(mojo.outputDirectory.getPath()), roots);
        }

        @Test
        void testScopeCompileAddedAbsolute() {
            GenerateMojo mojo = new GenerateMojo();
            mojo.scope = Scope.compile;
            mojo.outputDirectory = new File("target/generated-sources/i18n");
            mojo.project = mock(MavenProject.class);

            List<String> roots = new ArrayList<>();
            roots.add(mojo.outputDirectory.getAbsolutePath());

            when(mojo.project.getCompileSourceRoots()).thenReturn(roots);

            mojo.appendSourcePath();

            assertEquals(Arrays.asList(mojo.outputDirectory.getAbsolutePath()), roots);
        }

        @Test
        void testScopeTestNotAdded() {
            GenerateMojo mojo = new GenerateMojo();
            mojo.scope = Scope.test;
            mojo.outputDirectory = new File("target/generated-sources/i18n");
            mojo.project = mock(MavenProject.class);

            List<String> roots = new ArrayList<>();

            when(mojo.project.getTestCompileSourceRoots()).thenReturn(roots);

            mojo.appendSourcePath();

            assertEquals(Arrays.asList(mojo.outputDirectory.getAbsolutePath()), roots);
        }

        @Test
        void testScopeTestAddedRelative() {
            GenerateMojo mojo = new GenerateMojo();
            mojo.scope = Scope.test;
            mojo.outputDirectory = new File("target/generated-sources/i18n");
            mojo.project = mock(MavenProject.class);

            List<String> roots = new ArrayList<>();
            roots.add(mojo.outputDirectory.getPath());

            when(mojo.project.getTestCompileSourceRoots()).thenReturn(roots);

            mojo.appendSourcePath();

            assertEquals(Arrays.asList(mojo.outputDirectory.getPath()), roots);
        }

        @Test
        void testScopeTestAddedAbsolute() {
            GenerateMojo mojo = new GenerateMojo();
            mojo.scope = Scope.test;
            mojo.outputDirectory = new File("target/generated-sources/i18n");
            mojo.project = mock(MavenProject.class);

            List<String> roots = new ArrayList<>();
            roots.add(mojo.outputDirectory.getAbsolutePath());

            when(mojo.project.getTestCompileSourceRoots()).thenReturn(roots);

            mojo.appendSourcePath();

            assertEquals(Arrays.asList(mojo.outputDirectory.getAbsolutePath()), roots);
        }
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
