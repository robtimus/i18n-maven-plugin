# I18N Maven Plugin
[![Maven Central](https://img.shields.io/maven-central/v/com.github.robtimus/i18n-maven-plugin)](https://search.maven.org/artifact/com.github.robtimus/i18n-maven-plugin)
[![Build Status](https://github.com/robtimus/i18n-maven-plugin/actions/workflows/build.yml/badge.svg)](https://github.com/robtimus/i18n-maven-plugin/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.robtimus%3Ai18n-maven-plugin&metric=alert_status)](https://sonarcloud.io/summary/overall?id=com.github.robtimus%3Ai18n-maven-plugin)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.github.robtimus%3Ai18n-maven-plugin&metric=coverage)](https://sonarcloud.io/summary/overall?id=com.github.robtimus%3Ai18n-maven-plugin)
[![Known Vulnerabilities](https://snyk.io/test/github/robtimus/i18n-maven-plugin/badge.svg)](https://snyk.io/test/github/robtimus/i18n-maven-plugin)

The I18N Maven Plugin allows you to generate I18N classes from I18N resource files. Such I18N classes provides easy and safe access to the keys in their backing I18N resource files, without risking any [MissingResourceExceptions](https://docs.oracle.com/javase/8/docs/api/java/util/MissingResourceException.html) or getting the number of place holders wrong.

### Goals Overview

* [i18n:generate](https://robtimus.github.io/i18n-maven-plugin/generate-mojo.html) generate an I18N class from an I18N resource file.

### Usage

Instructions on how to use the I18N Plugin can be found on the [usage page](https://robtimus.github.io/i18n-maven-plugin/usage.html).

### Examples

* [Specify a custom class name](https://robtimus.github.io/i18n-maven-plugin/examples/custom-class-name.html)
* [Add a license](https://robtimus.github.io/i18n-maven-plugin/examples/license.html)
* [Make generated classes package-private](https://robtimus.github.io/i18n-maven-plugin/examples/package-private.html)
* [Suppress warnings](https://robtimus.github.io/i18n-maven-plugin/examples/suppress-warnings.html)
* [Use MessageFormat for formatting messages](https://robtimus.github.io/i18n-maven-plugin/examples/use-message-format.html)
