/*
 * GenerateMojo.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Generate an I18N class from an I18N resource file.
 *
 * @author Rob Spoor
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresProject = false, threadSafe = true)
public class GenerateMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    /**
     * The bundle name to generate an I18N class for. The matching bundle file will be resolved relative to the configured source directory.
     * @since 1.0
     */
    @Parameter(property = "i18n.bundleName", required = true)
    String bundleName;

    /**
     * The source directory where the I18N resource file can be found, without the package structure.
     * @since 1.0
     */
    @Parameter(property = "i18n.sourceDirectory", defaultValue = "${project.build.resources[0].directory}", required = true)
    File sourceDirectory;

    /**
     * The fully qualified name of the I18N class to generate. If not specified, the class name will be based on the bundle name.
     * @since 1.0
     */
    @Parameter(property = "i18n.className")
    String className;

    /**
     * The output directory where the I18N class will be written to, without the package structure.
     * This directory will be added as a project source root if needed.
     * @since 1.0
     */
    @Parameter(property = "i18n.outputDirectory", defaultValue = "${project.build.directory}/generated-sources/resource-bundles", required = true)
    private File outputDirectory;

    /**
     * The scope of the project source root, {@code compile} or {@code test}.
     * @since 1.0
     */
    @Parameter(property = "i18n.scope", defaultValue = "compile")
    private Scope scope;

    /**
     * The input encoding.
     * @since 1.0
     */
    @Parameter(property = "i18n.inputEncoding", defaultValue = "${project.build.sourceEncoding}")
    String inputEncoding;

    /**
     * The output encoding.
     * @since 1.0
     */
    @Parameter(property = "i18n.outputEncoding", defaultValue = "${project.build.sourceEncoding}")
    String outputEncoding;

    /**
     * The license text. This can be specified in a few ways:
     * <ul>
     *   <li>Using one of the pre-defined licenses:
     *     <ul>
     *       <li><a href="https://opensource.org/licenses/Apache-2.0">Apache-2.0</a>: the Apache License 2.0</li>
     *       <li><a href="https://opensource.org/licenses/BSD-2-Clause">BSD-2-Clause</a>: the 2-Clause BSD License / FreeBSD License /
     *           Simplified BSD License</li>
     *       <li><a href="https://opensource.org/licenses/EPL-1.0">EPL-1.0</a>: the Eclipse Public License</li>
     *       <li><a href="https://opensource.org/licenses/GPL-2.0">GPL-2.0</a>: the GNU General Public License version 2</li>
     *       <li><a href="https://opensource.org/licenses/GPL-3.0">GPL-3.0</a>: the GNU General Public License version 3</li>
     *       <li><a href="https://opensource.org/licenses/LGPL-2.0">LGPL-2.0</a>: the GNU Library General Public License version 2</li>
     *       <li><a href="https://opensource.org/licenses/LGPL-2.1">LGPL-2.1</a>: the GNU Lesser General Public License version 2.1</li>
     *       <li><a href="https://opensource.org/licenses/MIT">MIT</a>: the MIT License</li>
     *       <li><a href="https://opensource.org/licenses/MPL-2.0">MPL-2.0</a>: the Mozilla Public License 2.0</li>
     *     </ul>
     *   </li>
     *   <li>As a relative or absolute path to the file containing the license text.</li>
     *   <li>As a URL to the file containing the license text.</li>
     *   <li>As a literal license text.</li>
     * </ul>
     * Inside the license text three place holders can be used:
     * <ul>
     *   <li><code>${fileName}</code> for the file name.</li>
     *   <li><code>${copyrightYear}</code> for the copyright year.</li>
     *   <li><code>${copyrightHolder}</code> for the copyright holder.</li>
     * </ul>
     * @since 1.0
     */
    @Parameter(property = "i18n.license.text")
    String licenseText;

    /**
     * The copyright year to be inserted into the license text. If not configured the current year will be used.
     * @since 1.0
     */
    @Parameter(property = "i18n.license.copyrightYear")
    private String licenseCopyrightYear;

    /**
     * The copyright holder to be inserted into the license text.
     * @since 1.0
     */
    @Parameter(property = "i18n.license.copyrightHolder", defaultValue = "${user.name}")
    private String licenseCopyrightHolder;

    /**
     * If {@code true}, generated classes and methods will have public visibility; otherwise they will be package private.
     * @since 1.0
     */
    @Parameter(property = "i18n.publicVisibility", defaultValue = "true")
    private boolean publicVisibility;

    /**
     * Warnings that will be suppressed using {@link SuppressWarnings}.
     * @since 1.0
     */
    @Parameter(property = "i18n.suppressWarnings")
    private Set<String> suppressWarnings = Collections.emptySet();

    /**
     * If {@code true}, {@link MessageFormat} will be used to format messages;
     * otherwise {@link String#format(java.util.Locale, String, Object...)} will be used.
     * @since 1.0
     */
    @Parameter(property = "i18n.useMessageFormat", defaultValue = "false")
    private boolean useMessageFormat;

    @Override
    public void execute() throws MojoExecutionException {
        I18N.Node i18n = readI18N();

        Charset outputCharset = getOutputCharset();
        License license = new License(getLicenseText(), licenseCopyrightYear, licenseCopyrightHolder);
        I18N.Writer writer = new I18N.Writer(outputCharset, publicVisibility, license, useMessageFormat, suppressWarnings);

        String i18nClassName = getI18NClassName();
        try {
            getLog().info(Messages.generatingClass.get(i18nClassName, bundleName));
            writer.write(i18n, bundleName, i18nClassName, outputDirectory);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }

        appendSourcePath();
    }

    private I18N.Node readI18N() throws MojoExecutionException {
        Properties properties = readProperties();
        return new I18N.Parser().parse(properties);
    }

    Properties readProperties() throws MojoExecutionException {
        Charset charset = getInputCharset();
        File sourceFile = getSourceFile();
        try (Reader input = new InputStreamReader(new FileInputStream(sourceFile), charset)) {
            return OrderedProperties.fromReader(input);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private File getSourceFile() {
        return new File(sourceDirectory, bundleName.replace('.', '/') + ".properties"); //$NON-NLS-1$
    }

    Charset getInputCharset() {
        if (inputEncoding == null) {
            Charset defaultCharset = Charset.defaultCharset();
            getLog().warn(Messages.noInputEncoding.get(defaultCharset));
            return defaultCharset;
        }
        return Charset.forName(inputEncoding);
    }

    Charset getOutputCharset() {
        if (outputEncoding == null) {
            Charset defaultCharset = Charset.defaultCharset();
            getLog().warn(Messages.noOutputEncoding.get(defaultCharset));
            return defaultCharset;
        }
        return Charset.forName(outputEncoding);
    }

    String getLicenseText() throws MojoExecutionException {
        if (licenseText == null) {
            return null;
        }
        try {
            URL resourceURL = GenerateMojo.class.getResource("licenses/" + licenseText); //$NON-NLS-1$
            if (resourceURL != null) {
                return getLicenseText(resourceURL);
            }
            File licenseFile = new File(licenseText);
            if (licenseFile.exists()) {
                return getLicenseText(licenseFile);
            }
            URL licenseURL = createURL(licenseText);
            if (licenseURL != null) {
                return getLicenseText(licenseURL);
            }
            return licenseText;
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private URL createURL(String url) {
        try {
            return new URL(url);
        } catch (@SuppressWarnings("unused") MalformedURLException e) {
            return null;
        }
    }

    private String getLicenseText(URL licenseURL) throws IOException {
        try (Reader input = new InputStreamReader(licenseURL.openStream(), StandardCharsets.UTF_8)) {
            return getLicenseText(input);
        }
    }

    private String getLicenseText(File licenseFile) throws IOException {
        try (Reader input = new InputStreamReader(new FileInputStream(licenseFile), StandardCharsets.UTF_8)) {
            return getLicenseText(input);
        }
    }

    private String getLicenseText(Reader input) throws IOException {
        StringBuilder result = new StringBuilder();
        char[] buffer = new char[4096];
        int len;
        while ((len = input.read(buffer)) != -1) {
            result.append(buffer, 0, len);
        }
        return result.toString();
    }

    String getI18NClassName() {
        if (className != null) {
            return className;
        }
        int index = bundleName.lastIndexOf('.');
        if (index == -1) {
            return capitalize(bundleName);
        }
        String packageName = bundleName.substring(0, index);
        String simpleClassName = capitalize(bundleName.substring(index + 1));
        return packageName + '.' + simpleClassName;
    }

    private String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private void appendSourcePath() {
        List<String> sourceRoots = scope == Scope.compile ? project.getCompileSourceRoots() : project.getTestCompileSourceRoots();
        String newSourcePath = outputDirectory.getPath();
        String newAbsoluteSourcePath = outputDirectory.getAbsolutePath();

        if (!sourceRoots.contains(newSourcePath) && !sourceRoots.contains(newAbsoluteSourcePath)) {
            sourceRoots.add(newAbsoluteSourcePath);
        }
    }
}
