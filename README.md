# XSet - Extended Sets for Java

[![Build Status](https://travis-ci.com/vbartacek/xset-java.svg?branch=develop)](https://travis-ci.com/vbartacek/xset-java)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.spoledge.xset%3Axset&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.spoledge.xset%3Axset)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.spoledge.xset%3Axset&metric=coverage)](https://sonarcloud.io/dashboard?id=com.spoledge.xset%3Axset)


## Overview

Extended set - `XSet` is a set that can be either a standard finite set or a complement of a standard finite set.
The standard set operations are supported: complement, subtraction, instersection and union.
`XSet` is a generic type.

Examples:

```java
XSet<String> weekend = XSet.of("Sat", "Sun");
XSet<String> weekdays = weekend.complement();

assert weekdays.equals(XSet.complementOf("Sat", "Sun"));
assert weekdays.complement().equals(weekend);
assert weekdays.union(weekend).equals(XSet.full());
assert weekdays.intersect(weekend).equals(XSet.empty());
assert weekend.subtract(XSet.of("Sat", "Mon")).equals(XSet.of("Sun"));
```

TODO - WORK IN PROGRESS
