/*
 * I18NWriterWriteTest.java
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
@SuppressWarnings({ "nls", "javadoc" })
public class I18NWriterWriteTest {

    private static final Collection<Locale> LOCALES = Collections.unmodifiableCollection(Arrays.asList(Locale.ENGLISH, Locale.GERMAN, Locale.FRENCH));

    private static Properties bundleWithStringFormat;
    private static Properties bundleWithMessageFormat;

    private static I18N.Node i18nWithStringFormat;
    private static I18N.Node i18nWithMessageFormat;
    private static File outputDirBase;

    private final I18N.Writer writer;
    private final boolean publicVisibility;
    private final boolean useMessageFormat;
    private final Properties bundle;
    private final I18N.Node i18n;
    private final File outputDir;

    public I18NWriterWriteTest(int id, boolean publicVisibility, boolean useMessageFormat, String licenseText, Set<String> suppressWarnings) {
        writer = new I18N.Writer(StandardCharsets.UTF_8, publicVisibility, new License(licenseText, null, null), useMessageFormat, suppressWarnings);

        this.publicVisibility = publicVisibility;
        this.useMessageFormat = useMessageFormat;

        bundle = useMessageFormat ? bundleWithMessageFormat : bundleWithStringFormat;
        i18n = useMessageFormat ? i18nWithMessageFormat : i18nWithStringFormat;
        outputDir = new File(outputDirBase, String.format("Test-%03d", id));
    }

    @BeforeClass
    public static void setup() throws IOException {
        bundleWithMessageFormat = new OrderedProperties();
        bundleWithMessageFormat.setProperty("directTextNoArgs", "no args");
        bundleWithMessageFormat.setProperty("directTextOneArgs", "one args: {0}");
        bundleWithMessageFormat.setProperty("directTextTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.setProperty("child1.textNoArgs", "no args");
        bundleWithMessageFormat.setProperty("child1.textOneArgs", "one args: {0}");
        bundleWithMessageFormat.setProperty("child1.textTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.setProperty("child1.child11.textNoArgs", "no args");
        bundleWithMessageFormat.setProperty("child1.child11.textOneArgs", "one args: {0}");
        bundleWithMessageFormat.setProperty("child1.child11.textTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.setProperty("child1.child12.textNoArgs", "no args");
        bundleWithMessageFormat.setProperty("child1.child12.textOneArgs", "one args: {0}");
        bundleWithMessageFormat.setProperty("child1.child12.textTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.setProperty("child1.child13.textNoArgs", "no args");
        bundleWithMessageFormat.setProperty("child1.child13.textOneArgs", "one args: {0}");
        bundleWithMessageFormat.setProperty("child1.child13.textTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.setProperty("child2.textNoArgs", "no args");
        bundleWithMessageFormat.setProperty("child2.textOneArgs", "one args: {0}");
        bundleWithMessageFormat.setProperty("child2.textTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.setProperty("child2.child21.textNoArgs", "no args");
        bundleWithMessageFormat.setProperty("child2.child21.textOneArgs", "one args: {0}");
        bundleWithMessageFormat.setProperty("child2.child21.textTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.setProperty("child2.child22.textNoArgs", "no args");
        bundleWithMessageFormat.setProperty("child2.child22.textOneArgs", "one args: {0}");
        bundleWithMessageFormat.setProperty("child2.child22.textTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.setProperty("child2.child23.textNoArgs", "no args");
        bundleWithMessageFormat.setProperty("child2.child23.textOneArgs", "one args: {0}");
        bundleWithMessageFormat.setProperty("child2.child23.textTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.setProperty("child3.textNoArgs", "no args");
        bundleWithMessageFormat.setProperty("child3.textOneArgs", "one args: {0}");
        bundleWithMessageFormat.setProperty("child3.textTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.setProperty("child3.child31.textNoArgs", "no args");
        bundleWithMessageFormat.setProperty("child3.child31.textOneArgs", "one args: {0}");
        bundleWithMessageFormat.setProperty("child3.child31.textTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.setProperty("child3.child32.textNoArgs", "no args");
        bundleWithMessageFormat.setProperty("child3.child32.textOneArgs", "one args: {0}");
        bundleWithMessageFormat.setProperty("child3.child32.textTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.setProperty("child3.child33.textNoArgs", "no args");
        bundleWithMessageFormat.setProperty("child3.child33.textOneArgs", "one args: {0}");
        bundleWithMessageFormat.setProperty("child3.child33.textTwoArgs", "two args: {0}, {1}");
        i18nWithMessageFormat = new I18N.Parser().parse(bundleWithMessageFormat);

        bundleWithStringFormat = new OrderedProperties();
        for (String key : bundleWithMessageFormat.stringPropertyNames()) {
            String value = bundleWithMessageFormat.getProperty(key).replaceAll("\\{\\d+\\}", "%s");
            bundleWithStringFormat.setProperty(key, value);
        }
        i18nWithStringFormat = new I18N.Parser().parse(bundleWithStringFormat);

        outputDirBase = File.createTempFile("i18n-maven-plugin", null);
        outputDirBase.delete();
        outputDirBase.mkdirs();
    }

    @AfterClass
    public static void cleanup() throws IOException {
        cleanupFileOrDir(outputDirBase);
    }

    private static void cleanupFileOrDir(File file) throws IOException {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                cleanupFileOrDir(child);
            }
        }
        file.delete();
    }

    @Parameters(name = "id: {0}; publicVisibility: {1}; useMessageFormat: {2}; licenseText: {3}; suppressWarnings: {4}")
    public static List<Object[]> getParameters() {

        final boolean[] booleans = { false, true, };
        final String[] licenseTexts = { null, "License\ntext", };
        @SuppressWarnings("unchecked")
        final Set<String>[] suppressWarningsSets = new Set[] { wrap(), wrap("nls"), wrap("nls", "javadoc"), };

        List<Object[]> parameters = new ArrayList<>();

        int id = 0;

        for (boolean publicVisibility : booleans) {
            for (boolean useMessageFormat : booleans) {
                for (String licenseText : licenseTexts) {
                    for (Set<String> suppressWarnings : suppressWarningsSets) {
                        Object[] params = { ++id, publicVisibility, useMessageFormat, licenseText, suppressWarnings, };
                        parameters.add(params);
                    }
                }
            }
        }
        return parameters;
    }

    private static Set<String> wrap(String... suppressWarnings) {
        return new LinkedHashSet<>(Arrays.asList(suppressWarnings));
    }

    @Test
    public void testWriteCompileAndVerify() throws IOException, ReflectiveOperationException {
        generateClass();
        writeBundle();
        compileGeneratedClass();
        validateGeneratedClass();
    }

    private void writeBundle() throws IOException {
        try (OutputStream output = new FileOutputStream(new File(outputDir, "test/test-bundle.properties"))) {
            bundle.store(output, null);
        }
    }

    private void generateClass() throws IOException {
        writer.write(i18n, "test.test-bundle", "test.I18N", outputDir);
    }

    private void compileGeneratedClass() throws IOException {

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, StandardCharsets.UTF_8)) {

            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(new File(outputDir, "test/I18N.java"));

            CompilationTask compilationTask = compiler.getTask(null, fileManager, null, null, null, compilationUnits);

            Boolean compiledSuccessfully = compilationTask.call();
            assertEquals("Failed to compile I18N writer output", Boolean.TRUE, compiledSuccessfully);
        }
    }

    private void validateGeneratedClass() throws IOException, ReflectiveOperationException {
        URL[] urls = { outputDir.toURI().toURL(), };
        try (URLClassLoader classLoader = new URLClassLoader(urls)) {
            Class<?> i18nClass = Class.forName("test.I18N", true, classLoader);

            Set<Object> remainingProperties = new LinkedHashSet<>(bundle.keySet());

            validateGeneratedClass(i18nClass, null, "", remainingProperties);

            assertEquals(Collections.emptySet(), remainingProperties);
        }
    }

    private void validateGeneratedClass(Class<?> cls, Object instance, String path, Set<?> remainingProperties) throws ReflectiveOperationException {
        // public final if cls is the root class, otherwise public static final
        assertModifiers(cls.getModifiers(), cls.getDeclaringClass() != null, true);

        for (Field field : cls.getDeclaredFields()) {
            if (!isSystemField(field)) {
                // public static final if cls is the root class, otherwise public final
                assertModifiers(field.getModifiers(), cls.getDeclaringClass() == null, true);

                field.setAccessible(true);
                Object fieldInstance = field.get(instance);

                // validate the return type
                String fieldPath = path.isEmpty() ? field.getName() : path + "." + field.getName();
                validateGeneratedClass(field.getType(), fieldInstance, fieldPath, remainingProperties);
            }
        }
        Method[] methods = cls.getDeclaredMethods();
        if (bundle.getProperty(path) != null) {
            // a leaf node, must have 2 get methods
            assertEquals(2, methods.length);
            for (Method method : methods) {
                assertEquals("get", method.getName());
                assertEquals(String.class, method.getReturnType());
                // public only
                assertModifiers(method.getModifiers(), false, false);

                method.setAccessible(true);
                validateMethod(method, instance, path);

                remainingProperties.remove(path);
            }
        } else if (cls.getDeclaringClass() != null) {
            // a non-leaf node, must have no methods
            assertEquals(0, methods.length);
        }
        // else the root cls, don't validate its methods
    }

    private void assertModifiers(int modifiers, boolean mustBeStatic, boolean mustBeFinal) {
        assertFalse(Modifier.isPrivate(modifiers));
        assertFalse(Modifier.isProtected(modifiers));
        if (publicVisibility) {
            assertTrue(Modifier.isPublic(modifiers));
        }
        assertEquals(mustBeStatic, Modifier.isStatic(modifiers));
        assertEquals(mustBeFinal, Modifier.isFinal(modifiers));
    }

    private boolean isSystemField(Field field) {
        final int modifiers = field.getModifiers();
        return "BUNDLES".equals(field.getName())
                && field.getType() == Map.class
                && Modifier.isPrivate(modifiers)
                && Modifier.isStatic(modifiers)
                && Modifier.isFinal(modifiers);
    }

    private void validateMethod(Method method, Object instance, String path) throws ReflectiveOperationException {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length > 0 && parameterTypes[0] == Locale.class) {
            validateMethodWithLocales(method, parameterTypes, instance, path);
        } else {
            validateMethodWithoutLocale(method, parameterTypes, instance, path);
        }
    }

    private void validateMethodWithLocales(Method method, Class<?>[] parameterTypes, Object instance, String path)
            throws ReflectiveOperationException {

        Object[] args = getArguments(parameterTypes.length - 1);
        for (Locale locale : LOCALES) {
            Object[] argsWithLocale = new Object[parameterTypes.length];
            argsWithLocale[0] = locale;
            System.arraycopy(args, 0, argsWithLocale, 1, args.length);

            String expected = getExpectedString(path, locale, args);
            String actual = (String) method.invoke(instance, argsWithLocale);
            assertEquals(expected, actual);
        }
    }

    private void validateMethodWithoutLocale(Method method, Class<?>[] parameterTypes, Object instance, String path)
            throws ReflectiveOperationException {

        Object[] args = getArguments(parameterTypes.length);
        String expected = getExpectedString(path, null, args);
        String actual = (String) method.invoke(instance, args);
        assertEquals(expected, actual);
    }

    private Object[] getArguments(int count) {
        Object[] args = new Object[count];
        for (int i = 0; i < count; i++) {
            args[i] = Integer.toString(i + 1);
        }
        return args;
    }

    private String getExpectedString(String path, Locale locale, Object[] args) {
        String formatOrPattern = bundle.getProperty(path);
        assertNotNull(formatOrPattern);
        if (useMessageFormat) {
            MessageFormat format = locale == null ? new MessageFormat(formatOrPattern) : new MessageFormat(formatOrPattern, locale);
            return format.format(args);
        }
        if (locale == null) {
            return String.format(formatOrPattern, args);
        }
        return String.format(locale, formatOrPattern, args);
    }
}
