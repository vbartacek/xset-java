# XSet - Extended Sets for Java

[![Build Status](https://github.com/vbartacek/xset-java/actions/workflows/build.yml/badge.svg?event=push)](https://github.com/vbartacek/xset-java/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.vbartacek%3Axset&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.vbartacek%3Axset)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.github.vbartacek%3Axset&metric=coverage)](https://sonarcloud.io/dashboard?id=com.github.vbartacek%3Axset)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.vbartacek/xset/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.vbartacek/xset)


## Contents

* [Overview](#overview)
* [Usage](#usage)
    * [XSet](#xset)
* [Build](#build)
* [Release](#release)
* [License](#license)


## Overview

This is a Java library for sets and complementary sets.


## Usage

### XSet

This class is an extension of a finite set.
It can hold both finite sets and those inifite ones that can be represented as a complement of finite sets.

The standard set operations are supported: contains, containsAll, containsAny, complement, subtraction, instersection and union.
`XSet` is a generic type.

The implementation maintains a finite `Set` of `items` and a `boolean` flag `complementary`.
The `items` should hold only a given type of elements - according to the generic type (e.g. "String" or "Long").
The `items` cannot hold `null` elements.

The `XSet` cannot directly implement `java.util.Collection`, because the method `java.util.Collection#contains(Object)`
is not type-safe and we cannot implement such method here.
Instead of that we implement method with the same name, but taking as the parameter only objects of the given type.

This class is immutable - once created its content cannot be changed.
It also means that this class is thread-safe.

Examples:

```java
XSet<String> weekend = XSet.of("Sat", "Sun");
XSet<String> weekdays = weekend.complement();

assert weekend.contains("Sat");
assert !weekend.contains("Mon");

assert weekdays.contains("Mon");
assert !weekdays.contains("Sat");

assert !weekdays.containsAll("Mon", "Sun");
assert weekdays.containsAny("Mon", "Sun");

assert weekdays.equals(XSet.complementOf("Sat", "Sun"));
assert weekdays.complement().equals(weekend);

assert weekdays.union(weekend).equals(XSet.full());

assert weekdays.intersect(weekend).equals(XSet.empty());

assert weekend.subtract(XSet.of("Sat", "Mon")).equals(XSet.of("Sun"));
```


## Build

Maven is used for building this project:

```bash
mvn clean verify
```

To generate the code coverage report in HTML format, then do the following:

```bash
mvn clean verify jacoco:report -Pcoverage
```

To execute static analysis - PMD, Spotbugs and Checkstyle, then do the following:

```bash
mvn pmd:check spotbugs:check checkstyle:check
```


## Release

Release prerequisities:

* install `gpg`
* ensure you have created and published your GPG key
* ensure you have configured your access for `ossrh` server in maven `settings.xml`
* you have committed non-snapshot version to be released

Then build and upload artifacts to staging repository:

```bash
mvn clean deploy -Prelease
```

Verify the staging repository and make the release:

```bash
mvn nexus-staging:release -Prelease
```


## License

This project is licensed under [MIT License](http://opensource.org/licenses/MIT).

