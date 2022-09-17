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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("nls")
class I18NWriterWriteTest {

    private static final Collection<Locale> LOCALES = Collections.unmodifiableCollection(Arrays.asList(Locale.ENGLISH, Locale.GERMAN, Locale.FRENCH));

    private static Map<String, String> bundleWithStringFormat;
    private static Map<String, String> bundleWithMessageFormat;

    private static I18N.Node i18nWithStringFormat;
    private static I18N.Node i18nWithMessageFormat;

    @TempDir
    private static File outputDirBase;

    @BeforeAll
    static void setup() {
        bundleWithMessageFormat = new LinkedHashMap<>();
        bundleWithMessageFormat.put("directTextNoArgs", "no args");
        bundleWithMessageFormat.put("directTextOneArgs", "one args: {0}");
        bundleWithMessageFormat.put("directTextTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.put("child1.textNoArgs", "no args");
        bundleWithMessageFormat.put("child1.textOneArgs", "one args: {0}");
        bundleWithMessageFormat.put("child1.textTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.put("child1.child11.textNoArgs", "no args");
        bundleWithMessageFormat.put("child1.child11.textOneArgs", "one args: {0}");
        bundleWithMessageFormat.put("child1.child11.textTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.put("child1.child12.textNoArgs", "no args");
        bundleWithMessageFormat.put("child1.child12.textOneArgs", "one args: {0}");
        bundleWithMessageFormat.put("child1.child12.textTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.put("child1.child13.textNoArgs", "no args");
        bundleWithMessageFormat.put("child1.child13.textOneArgs", "one args: {0}");
        bundleWithMessageFormat.put("child1.child13.textTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.put("child2.textNoArgs", "no args");
        bundleWithMessageFormat.put("child2.textOneArgs", "one args: {0}");
        bundleWithMessageFormat.put("child2.textTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.put("child2.child21.textNoArgs", "no args");
        bundleWithMessageFormat.put("child2.child21.textOneArgs", "one args: {0}");
        bundleWithMessageFormat.put("child2.child21.textTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.put("child2.child22.textNoArgs", "no args");
        bundleWithMessageFormat.put("child2.child22.textOneArgs", "one args: {0}");
        bundleWithMessageFormat.put("child2.child22.textTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.put("child2.child23.textNoArgs", "no args");
        bundleWithMessageFormat.put("child2.child23.textOneArgs", "one args: {0}");
        bundleWithMessageFormat.put("child2.child23.textTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.put("child3.textNoArgs", "no args");
        bundleWithMessageFormat.put("child3.textOneArgs", "one args: {0}");
        bundleWithMessageFormat.put("child3.textTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.put("child3.child31.textNoArgs", "no args");
        bundleWithMessageFormat.put("child3.child31.textOneArgs", "one args: {0}");
        bundleWithMessageFormat.put("child3.child31.textTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.put("child3.child32.textNoArgs", "no args");
        bundleWithMessageFormat.put("child3.child32.textOneArgs", "one args: {0}");
        bundleWithMessageFormat.put("child3.child32.textTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.put("child3.child33.textNoArgs", "no args");
        bundleWithMessageFormat.put("child3.child33.textOneArgs", "one args: {0}");
        bundleWithMessageFormat.put("child3.child33.textTwoArgs", "two args: {0}, {1}");
        bundleWithMessageFormat.put("leafWithChildren", "leaf");
        bundleWithMessageFormat.put("leafWithChildren.child", "nested child");
        i18nWithMessageFormat = new I18N.Parser().parse(bundleWithMessageFormat);

        bundleWithStringFormat = new LinkedHashMap<>();
        for (String key : bundleWithMessageFormat.keySet()) {
            String value = bundleWithMessageFormat.get(key).replaceAll("\\{\\d+\\}", "%s");
            bundleWithStringFormat.put(key, value);
        }
        i18nWithStringFormat = new I18N.Parser().parse(bundleWithStringFormat);
    }

    static List<Object[]> getParameters() {

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

    @ParameterizedTest(name = "id: {0}; publicVisibility: {1}; useMessageFormat: {2}; licenseText: {3}; suppressWarnings: {4}")
    @MethodSource("getParameters")
    void testWriteCompileAndVerify(int id, boolean publicVisibility, boolean useMessageFormat, String licenseText, Set<String> suppressWarnings)
            throws IOException, ReflectiveOperationException {

        TestExecutor executor = new TestExecutor(id, publicVisibility, useMessageFormat, licenseText, suppressWarnings);
        executor.generateClass();
        executor.writeBundle();
        executor.compileGeneratedClass();
        executor.validateGeneratedClass();
    }

    private static final class TestExecutor {

        private final I18N.Writer writer;
        private final boolean publicVisibility;
        private final boolean useMessageFormat;
        private final Map<String, String> bundle;
        private final I18N.Node i18n;
        private final File outputDir;

        TestExecutor(int id, boolean publicVisibility, boolean useMessageFormat, String licenseText, Set<String> suppressWarnings) {
            License license = new License(licenseText, null, null);
            writer = new I18N.Writer(StandardCharsets.UTF_8, publicVisibility, license, useMessageFormat, suppressWarnings);

            this.publicVisibility = publicVisibility;
            this.useMessageFormat = useMessageFormat;

            bundle = useMessageFormat ? bundleWithMessageFormat : bundleWithStringFormat;
            i18n = useMessageFormat ? i18nWithMessageFormat : i18nWithStringFormat;
            outputDir = new File(outputDirBase, String.format("Test-%03d", id));
        }

        private void writeBundle() throws IOException {
            try (PrintStream output = new PrintStream(new FileOutputStream(new File(outputDir, "test/test-bundle.properties")))) {
                for (Map.Entry<String, String> entry : bundle.entrySet()) {
                    output.printf("%s = %s%n", entry.getKey(), entry.getValue());
                }
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
                assertEquals(Boolean.TRUE, compiledSuccessfully, "Failed to compile I18N writer output");
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

        private void validateGeneratedClass(Class<?> cls, Object instance, String path, Set<?> remainingProperties)
                throws ReflectiveOperationException {

            // final if cls is the root class, otherwise static final
            assertModifiers(cls.getModifiers(), cls.getDeclaringClass() != null, true);

            for (Field field : cls.getDeclaredFields()) {
                if (!isSystemField(field) && !field.isSynthetic()) {
                    // static final if cls is the root class, otherwise final
                    assertModifiers(field.getModifiers(), cls.getDeclaringClass() == null, true);

                    field.setAccessible(true);
                    Object fieldInstance = field.get(instance);

                    // validate the return type
                    String fieldPath = path.isEmpty() ? field.getName() : path + "." + field.getName();
                    validateGeneratedClass(field.getType(), fieldInstance, fieldPath, remainingProperties);
                }
            }

            // the root class must have 3 private methods (getResourceBundle, getString, nonNull), all nested classes may have none
            long privateMethodCount = Arrays.stream(cls.getDeclaredMethods())
                    .filter(method -> !method.isSynthetic())
                    .filter(method -> Modifier.isPrivate(method.getModifiers()))
                    .count();
            if (cls.getDeclaringClass() == null) {
                assertEquals(3, privateMethodCount);
            } else {
                assertEquals(0, privateMethodCount);
            }

            Method[] methods = Arrays.stream(cls.getDeclaredMethods())
                    .filter(method -> !method.isSynthetic())
                    .filter(method -> !Modifier.isPrivate(method.getModifiers()))
                    .sorted(Comparator.comparing(Method::getName).thenComparing(Method::getParameterCount))
                    .toArray(Method[]::new);

            // validate methods per pair
            assertEquals(0, methods.length % 2);
            String previous = "";
            for (int i = 0; i < methods.length; i += 2) {
                Method withoutLocale = methods[i];
                Method withLocale = methods[i + 1];

                // only two methods with the same name may exist
                assertNotEquals(previous, withoutLocale.getName());
                // the pair must have the same name
                assertEquals(withoutLocale.getName(), withLocale.getName());
                // the second must have the first's parameter types plus a leading Locale parameter
                assertEquals(withoutLocale.getParameterCount() + 1, withLocale.getParameterCount());
                assertEquals(Locale.class, withLocale.getParameterTypes()[0]);
                assertArrayEquals(withoutLocale.getParameterTypes(),
                        Arrays.copyOfRange(withLocale.getParameterTypes(), 1, withLocale.getParameterCount()));
                // both methods must return String
                assertEquals(String.class, withoutLocale.getReturnType());
                assertEquals(String.class, withLocale.getReturnType());
                // both methods must have the right modifiers - static if cls is the root class, otherwise non-static
                assertModifiers(withoutLocale.getModifiers(), cls.getDeclaringClass() == null, false);
                assertEquals(withoutLocale.getModifiers(), withLocale.getModifiers());

                // the methods must be callable
                String methodPath = path.isEmpty() ? withoutLocale.getName() : path + "." + withoutLocale.getName();
                withoutLocale.setAccessible(true);
                validateMethodWithoutLocale(withoutLocale, instance, methodPath);
                withLocale.setAccessible(true);
                validateMethodWithLocales(withLocale, instance, methodPath);

                remainingProperties.remove(methodPath);
            }
        }

        private void assertModifiers(int modifiers, boolean mustBeStatic, boolean mustBeFinal) {
            assertFalse(Modifier.isPrivate(modifiers));
            assertFalse(Modifier.isProtected(modifiers));
            assertEquals(publicVisibility, Modifier.isPublic(modifiers));
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

        private void validateMethodWithLocales(Method method, Object instance, String path) throws ReflectiveOperationException {
            Class<?>[] parameterTypes = method.getParameterTypes();
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

        private void validateMethodWithoutLocale(Method method, Object instance, String path) throws ReflectiveOperationException {
            Class<?>[] parameterTypes = method.getParameterTypes();
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
            String formatOrPattern = bundle.get(path);
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
}
