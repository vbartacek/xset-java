# XSet - Extended Sets for Java

[![Build Status](https://travis-ci.com/vbartacek/xset-java.svg?branch=develop)](https://travis-ci.com/vbartacek/xset-java)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.spoledge.xset%3Axset&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.spoledge.xset%3Axset)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.spoledge.xset%3Axset&metric=coverage)](https://sonarcloud.io/dashboard?id=com.spoledge.xset%3Axset)


## Overview

This is a Java library that allows working with sets and complementary sets.

TODO - WORK IN PROGRESS


## Core Classes

### XSet

This class is an extension of a finite set.
It can hold both finite sets and those inifite ones that can be represented as a complement of finite sets.

The standard set operations are supported: contains, containsAll, containsAny, complement, subtraction, instersection and union.
`XSet` is a generic type.

The implementation maintains a finite `Set` of `items` and a `boolean` flag `complementary`.
The `items` should hold only a given type of elements - according to the generic type (e.g. "String" or "Long").
The `items` cannot hold `null` elements.

The `XSet` cannot directly implement `java.util.Collection`, because the method `java.util.Collection#contains(Object}`
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

