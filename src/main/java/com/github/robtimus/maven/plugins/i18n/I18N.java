/*
 * I18N.java
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * A wrapper class for I18N content and all related classes.
 * This allows classes {@link Node} and {@link VTLHelper} to be included in Velocity, which only supports methods and properties of public classes,
 * while still keeping them package private.
 *
 * @author Rob Spoor
 */
final class I18N {

    @SuppressWarnings("nls")
    private I18N() {
        throw new IllegalStateException("cannot create instances of " + getClass().getName());
    }

    public static final class Node {

        private final String name;
        private final String path;
        private final Map<String, Node> children;
        private boolean isLeaf;
        private String value;

        Node(String name, String path, boolean isLeaf, String value) {
            this.name = name;
            this.path = path;
            this.children = new LinkedHashMap<>();
            this.isLeaf = isLeaf;
            this.value = value;
        }

        public String name() {
            return name;
        }

        public String path() {
            return path;
        }

        public Collection<Node> children() {
            return children.values();
        }

        public boolean isLeaf() {
            return isLeaf;
        }

        public String getValue() {
            return value;
        }

        void addChild(Node child) {
            children.put(child.name, child);
        }

        void setLeaf(String value) {
            isLeaf = true;
            this.value = value;
        }
    }

    static final class Parser {

        private static final String ROOT_PATH = ""; //$NON-NLS-1$

        Node parse(Properties properties) {
            Node root = new Node(ROOT_PATH, ROOT_PATH, false, null);

            Map<String, Node> nodes = new HashMap<>();
            nodes.put(ROOT_PATH, root);

            for (Map.Entry<?, ?> entry : properties.entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                addNodeIfNotExists(key, nodes, true, value);
            }
            return root;
        }

        private void addNodeIfNotExists(String path, Map<String, Node> nodes, boolean isLeaf, String value) {
            Node existing = nodes.get(path);
            if (existing == null) {
                String parentPath;
                String name;

                int index = path.lastIndexOf('.');
                if (index == -1) {
                    parentPath = ROOT_PATH;
                    name = path;
                } else {
                    parentPath = path.substring(0, index);
                    name = path.substring(index + 1);
                }

                Node node = new Node(name, path, isLeaf, value);
                nodes.put(path, node);

                addNodeIfNotExists(parentPath, nodes, false, null);
                Node parent = nodes.get(parentPath);
                parent.addChild(node);

            } else if (isLeaf) {
                existing.setLeaf(value);
            }
        }
    }

    static final class Writer {

        private final VelocityEngine engine;

        private final Charset encoding;

        private final boolean publicVisibility;
        private final License license;
        private final boolean useMessageFormat;
        private final Set<String> suppressWarnings;

        @SuppressWarnings("nls")
        Writer(Charset encoding, boolean publicVisibility, License license, boolean useMessageFormat, Set<String> suppressWarnings) {
            Properties properties = new Properties();
            properties.put(RuntimeConstants.RESOURCE_LOADER, "class");
            properties.put("class.resource.loader.class", ClasspathResourceLoader.class.getName());
            properties.put(RuntimeConstants.VM_ARGUMENTS_STRICT, "true");
            properties.put(RuntimeConstants.RUNTIME_REFERENCES_STRICT, "true");

            engine = new VelocityEngine(properties);

            this.encoding = encoding;

            this.publicVisibility = publicVisibility;
            this.license = license;
            this.useMessageFormat = useMessageFormat;
            this.suppressWarnings = suppressWarnings;
        }

        @SuppressWarnings("nls")
        void write(Node i18n, String bundleName, String i18nClassName, File outputDir) throws IOException {
            String packageName;
            String simpleClassName;

            int index = i18nClassName.lastIndexOf('.');
            if (index == -1) {
                packageName = null;
                simpleClassName = i18nClassName;
            } else {
                packageName = i18nClassName.substring(0, index);
                simpleClassName = i18nClassName.substring(index + 1);
            }

            Context context = new VelocityContext();
            context.put("visibility", publicVisibility ? "public " : "");
            context.put("packageName", packageName);
            context.put("simpleClassName", simpleClassName);
            context.put("bundleName", bundleName);

            context.put("useMessageFormat", useMessageFormat);
            context.put("argumentTypesFinder", useMessageFormat ? new MessageFormatArgumentTypesFinder() : new StringFormatArgumentTypesFinder());

            context.put("suppressWarnings", suppressWarnings);

            context.put("i18n", i18n);
            context.put("helper", new VTLHelper());

            File packageDir = packageName == null ? outputDir : new File(outputDir, packageName.replace('.', '/'));
            packageDir.mkdirs();

            String i18nClassFileName = simpleClassName + ".java";
            File i18nClassFile = new File(packageDir, i18nClassFileName);
            context.put("licenseText", getLicenseText(i18nClassFileName));
            write("com/github/robtimus/maven/plugins/i18n/templates/I18N.vtl", context, i18nClassFile);
        }

        String getLicenseText(String fileName) {
            if (license == null || license.getText() == null || license.getText().isEmpty()) {
                return null;
            }

            String copyrightYear = getLicenseCopyrightYear();
            String copyrightHolder = getLicenseCopyrightHolder();

            return license.getText()
                    .replace("${fileName}", fileName) //$NON-NLS-1$
                    .replace("${copyrightYear}", copyrightYear) //$NON-NLS-1$
                    .replace("${copyrightHolder}", copyrightHolder) //$NON-NLS-1$
                    ;
        }

        String getLicenseCopyrightYear() {
            assert license != null;

            return license.getCopyrightYear() != null ? license.getCopyrightYear() : String.format("%tY", new Date()); //$NON-NLS-1$
        }

        String getLicenseCopyrightHolder() {
            assert license != null;

            return license.getCopyrightHolder() != null ? license.getCopyrightHolder() : System.getProperty("user.name"); //$NON-NLS-1$
        }

        private void write(String templateName, Context context, File file) throws IOException {
            try (java.io.Writer writer = new OutputStreamWriter(new FileOutputStream(file), encoding)) {
                engine.mergeTemplate(templateName, encoding.name(), context, writer);
            } catch (VelocityException e) {
                throw new IOException(e);
            }
        }
    }

    public interface ArgumentTypesFinder {

        // OBJECT is effectively immutable, as it will always only contain one type : Object.class
        ArgumentTypes OBJECT = new ArgumentTypes(Object.class);

        List<ArgumentTypes> findArgumentTypes(String formatOrPattern);

        final class ArgumentTypes {
            private final Set<Class<?>> types = new LinkedHashSet<>();

            ArgumentTypes(Class<?>... types) {
                this(Arrays.asList(types));
            }

            ArgumentTypes(Collection<Class<?>> types) {
                this.types.addAll(types);
            }

            public Set<Class<?>> getTypes() {
                return Collections.unmodifiableSet(types);
            }

            void retainTypes(ArgumentTypes types) {
                retainTypes(types.types);
            }

            void retainTypes(Collection<Class<?>> types) {
                this.types.retainAll(types);
                if (this.types.isEmpty()) {
                    this.types.add(Object.class);
                }
            }
        }
    }

    public static final class VTLHelper {

        @SuppressWarnings("nls")
        static final Collection<String> KEYWORDS = Collections.unmodifiableCollection(Arrays.asList(
                "abstract", "assert",
                "boolean", "break", "byte",
                "case", "catch", "char", "class", "const", "continue",
                "default", "do", "double",
                "else", "enum", "extends",
                "final", "finally", "float", "for",
                "goto",
                "if", "implements", "import", "instanceof", "int", "interface",
                "long",
                "native", "new",
                "package", "private", "protected", "public",
                "return",
                "short", "static", "strictfp", "super", "switch", "synchronized",
                "this", "throw", "throws", "transient", "try",
                "void", "volatile",
                "while"
        ));
        @SuppressWarnings("nls")
        static final Collection<String> LITERALS = Collections.unmodifiableCollection(Arrays.asList(
                "true", "false", "null"
        ));

        public String varName(Node node) {
            String name = node.name();
            if (!canUseName(name)) {
                name = '_' + name;
            }
            return name;
        }

        public String className(Node node, Stack<String> classNames) {
            String className = className(node);
            while (classNames.contains(className)) {
                className += '_';
            }
            return className;
        }

        private String className(Node node) {
            String name = node.name();
            // always add a trailing _ to support classes for names that are already capitalised
            name = Character.toUpperCase(name.charAt(0)) + name.substring(1) + '_';
            if (!canUseName(node.name())) {
                name = '_' + name;
            }
            return name;
        }

        private boolean canUseName(String name) {
            return Character.isJavaIdentifierStart(name.charAt(0)) && !KEYWORDS.contains(name) && !LITERALS.contains(name);
        }

        public <T> Stack<T> stack() {
            return new Stack<>();
        }

        public String stripLast(String s, int count) {
            return s.substring(0, s.length() - count);
        }

        public String trimRight(String s) {
            int end = s.length();
            while (end > 0 && Character.isWhitespace(s.charAt(end - 1))) {
                end--;
            }
            return s.substring(0, end);
        }

        public List<String> splitLines(String text) {
            return text == null ? Collections.<String>emptyList() : Arrays.asList(text.split("\r?\n")); //$NON-NLS-1$
        }
    }

    public static final class Stack<T> {

        private final LinkedList<T> stack;

        private Stack() {
            stack = new LinkedList<>();
        }

        public T push(T value) {
            stack.addLast(value);
            return value;
        }

        public T pop() {
            return stack.removeLast();
        }

        public T peek() {
            return stack.peekLast();
        }

        private boolean contains(T object) {
            return stack.contains(object);
        }
    }
}
