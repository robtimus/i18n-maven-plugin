<#include "license.ftl">
// This file has been generated by i18n-maven-plugin
// (https://robtimus.github.io/i18n-maven-plugin/)

<#include "package.ftl">
<#include "imports.ftl">
<#include "suppressWarnings.ftl">
<#macro i18nClass node, className, classNameStack, indent>

${indent}${visibility}static final class ${className} {

${indent}    private ${className}() {
${indent}        super();
${indent}    }
    <#if node.isLeaf()>

        <#assign argumentTypes = argumentTypesFinder.findArgumentTypes(node.getValue())>
        <#if argumentTypes?size == 0>
${indent}    ${visibility}String get() {
${indent}        return get(null);
${indent}    }

${indent}    ${visibility}String get(Locale locale) {
${indent}        Locale l = nonNull(locale);
${indent}        return getString(l, "${node.path()}");
${indent}    }
        <#elseif argumentTypes?size == 1>
${indent}    ${visibility}String get(Object arg) {
${indent}        return get(null, arg);
${indent}    }

${indent}    ${visibility}String get(Locale locale, Object arg) {
${indent}        Locale l = nonNull(locale);
${indent}        String s = getString(l, "${node.path()}");
            <#if useMessageFormat>
${indent}        Object[] args = {arg};
${indent}        return new MessageFormat(s, l).format(args);
            <#else>
${indent}        return String.format(l, s, arg);
            </#if>
${indent}    }
        <#else>
${indent}    ${visibility}String get(
            <#list argumentTypes as argType>
                <#assign argIndex = useMessageFormat?then(argType?index, argType?index + 1)>
                <#assign postfix = (argType?index == argumentTypes?size - 1)?then(') {', ',')>
${indent}            Object arg${argIndex}${postfix}
            </#list>

${indent}        return get(null,
            <#list argumentTypes as argType>
                <#assign argIndex = useMessageFormat?then(argType?index, argType?index + 1)>
                <#assign postfix = (argType?index == argumentTypes?size - 1)?then('', ',')>
${indent}                arg${argIndex}${postfix}
            </#list>
${indent}        );
${indent}    }

${indent}    ${visibility}String get(Locale locale,
            <#list argumentTypes as argType>
                <#assign argIndex = useMessageFormat?then(argType?index, argType?index + 1)>
                <#assign postfix = (argType?index == argumentTypes?size - 1)?then(') {', ',')>
${indent}            Object arg${argIndex}${postfix}
            </#list>

${indent}        Locale l = nonNull(locale);
${indent}        String s = getString(l, "${node.path()}");
            <#if useMessageFormat>
${indent}        Object[] args = {
                <#list argumentTypes as argType>
                    <#assign argIndex = argType?index>
${indent}                arg${argIndex},
                </#list>
${indent}        };
${indent}        return new MessageFormat(s, l).format(args);
            <#else>
${indent}        return String.format(l, s,
                <#list argumentTypes as argType>
                    <#assign argIndex = argType?index + 1>
                    <#assign postfix = (argType?index == argumentTypes?size - 1)?then('', ',')>
${indent}                arg${argIndex}${postfix}
                </#list>
${indent}        );
           </#if>
${indent}    }
        </#if>
    </#if>
    <#list node.children() as childNode>
        <#assign childClassNameStack = classNameStack + [ className ]>
        <#assign childClassName = helper.className(childNode, childClassNameStack)>

${indent}    ${visibility}final ${childClassName} ${helper.varName(childNode)} = new ${childClassName}();
        <@i18nClass childNode childClassName childClassNameStack "${indent}    " />
    </#list>
${indent}}
</#macro>
<#-- I18N class -->
${visibility}final class ${simpleClassName} {

    private static final Map<Locale, ResourceBundle> BUNDLES = new HashMap<>();

    private ${simpleClassName}() {
        throw new IllegalStateException("cannot create instances of " + getClass().getName());
    }

    private static synchronized ResourceBundle getResourceBundle(Locale locale) {
        Locale l = nonNull(locale);
        ResourceBundle bundle = BUNDLES.get(l);
        if (bundle == null) {
            bundle = ResourceBundle.getBundle("${bundleName}", l);
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
    <#list i18n.children() as node>
        <#assign classNameStack = []>
        <#assign className = helper.className(node, classNameStack)>

    ${visibility}static final ${className} ${helper.varName(node)} = new ${className}();
        <@i18nClass node className classNameStack "    " />
    </#list>
}
