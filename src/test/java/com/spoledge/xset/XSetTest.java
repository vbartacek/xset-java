/*
 * Copyright (C) 2020 Vaclav Bartacek
 * MIT License
 */

package com.spoledge.xset;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Stream;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


/**
 * Test suited for {@link XSet}.
 *
 * @author Vaclav Bartacek
 */
class XSetTest {

    @Test
    void testEmpty() {
        assertProperties("empty-set", XSet.empty(), Collections.emptySet(), false);
    }

    @Test
    void testFull() {
        assertProperties("full-set", XSet.full(), Collections.emptySet(), true);
    }

    @Test
    void testCreateNulls() {
        final Collection<String> nullCollection = null;
        final Collection<String> nullItemCollection = Collections.singleton((String) null);

        assertAll(
            () -> assertThrows(NullPointerException.class, () -> XSet.of((String) null), "of single item null"),
            () -> assertThrows(NullPointerException.class, () -> XSet.of("Mon", null), "of one item null"),
            () -> assertThrows(NullPointerException.class, () -> XSet.of(nullCollection), "of collection null"),
            () -> assertThrows(NullPointerException.class, () -> XSet.of(nullItemCollection), "of collection item null"),

            () -> assertThrows(NullPointerException.class, () -> XSet.complementOf((String) null), "complementOf single item null"),
            () -> assertThrows(NullPointerException.class, () -> XSet.complementOf("Mon", null), "complementOf one item null"),
            () -> assertThrows(NullPointerException.class, () -> XSet.complementOf(nullCollection), "complementOf collection null"),
            () -> assertThrows(NullPointerException.class, () -> XSet.complementOf(nullItemCollection), "complementOf collection item null")
        );
    }

    @ParameterizedTest
    @MethodSource("createParameters")
    void testCreate(final String message, final Collection<String> items, final boolean complementary) {
        final XSet<String> tested = complementary ? XSet.complementOf(items) : XSet.of(items);
        assertProperties(message, tested, items, complementary);
    }

    @ParameterizedTest
    @MethodSource("createParameters")
    void testComplement(final String message, final Collection<String> items, final boolean complementary) {
        final XSet<String> original = complementary ? XSet.complementOf(items) : XSet.of(items);
        final XSet<String> tested = original.complement();

        assertAll(
            () -> assertProperties(message, tested, items, !complementary),
            () -> assertThat(message + " - complement of complement", tested.complement(), is(original))
        );
    }

    static Stream<Arguments> createParameters() {
        return Stream.of(
            Arguments.of("emptySet", Collections.emptySet(), false),
            Arguments.of("~emptySet", Collections.emptySet(), true),
            Arguments.of("singleton", Collections.singleton("Mon"), false),
            Arguments.of("~singleton", Collections.singleton("Mon"), true),
            Arguments.of("list[2->1]", Arrays.asList("Mon", "Mon"), true),
            Arguments.of("~list[2->1]", Arrays.asList("Mon", "Mon"), false),
            Arguments.of("list[2]", Arrays.asList("Mon", "Tue"), true),
            Arguments.of("~list[2]", Arrays.asList("Mon", "Tue"), false),
            Arguments.of("list[3->2]", Arrays.asList("Mon", "Tue", "Mon"), true),
            Arguments.of("~list[3->2]", Arrays.asList("Mon", "Tue", "Mon"), false)
        );
    }

    private static void assertProperties(
            final String message,
            final XSet<String> tested,
            final Collection<String> items,
            final boolean complementary) {

        assertAll(message,
            () -> assertThat("items", tested.getItems(), is(new HashSet<>(items))),
            () -> assertThat("complementary", tested.isComplementary(), is(complementary)),
            () -> assertThat("finite", tested.isFinite(), is(!complementary)),
            () -> assertThat("empty", tested.isEmpty(), is(!complementary && items.isEmpty())),
            () -> assertThat("trivial", tested.isTrivial(), is(items.isEmpty())),
            () -> assertThat("full", tested.isFull(), is(complementary && items.isEmpty()))
        );
    }

    @ParameterizedTest
    @MethodSource("equalsHashCodeToStringParameters")
    void testEqualsHashCodeToString(final String message, final XSet<String> set1, final XSet<String> set2) {
        assertAll(message + ": " + set1 + " " + set2,
            () -> assertThat("equals ->", set1.equals(set2), is(true)),
            () -> assertThat("equals <-", set2.equals(set1), is(true)),
            () -> assertThat("hash", set1.hashCode(), is(set2.hashCode())),
            () -> assertThat("toString", set1.toString(), is(set2.toString()))
        );
    }

    static Stream<Arguments> equalsHashCodeToStringParameters() {
        return Stream.of(
            Arguments.of("empty x of[0]", empty(), of()),
            Arguments.of("of[1]", of("Mon"), of("Mon")),
            Arguments.of("of[2]", of("Mon", "Tue"), of("Tue", "Mon")),
            Arguments.of("complementOf[1]", complementOf("Mon"), complementOf("Mon")),
            Arguments.of("complementOf[2]", complementOf("Mon", "Tue"), complementOf("Tue", "Mon")),
            Arguments.of("full x complementOf[0]", full(), complementOf())
        );
    }

    @ParameterizedTest
    @MethodSource("notEqualsParameters")
    void testNotEquals(final XSet<String> set1, final Object set2) {
        assertAll("notEquals " + set1 + " " + set2,
            () -> assertThat("equals ->", set1.equals(set2), is(false)),
            () -> {
                if (set2 != null) {
                    assertThat("equals <-", set2.equals(set1), is(false));
                }
            }
        );
    }

    static Stream<Arguments> notEqualsParameters() {
        return Stream.of(
            Arguments.of(empty(), null),
            Arguments.of(empty(), new Object()),
            Arguments.of(empty(), of("Mon")),
            Arguments.of(empty(), of("Mon", "Tue")),
            Arguments.of(empty(), complementOf()),
            Arguments.of(empty(), complementOf("Mon")),
            Arguments.of(empty(), complementOf("Mon", "Tue")),
            Arguments.of(empty(), full()),

            Arguments.of(of("Mon"), null),
            Arguments.of(of("Mon"), new Object()),
            Arguments.of(of("Mon"), of("Sun")),
            Arguments.of(of("Mon"), of("Sun", "Sat")),
            Arguments.of(of("Mon"), complementOf()),
            Arguments.of(of("Mon"), complementOf("Mon")),
            Arguments.of(of("Mon"), complementOf("Sun")),
            Arguments.of(of("Mon"), complementOf("Sun", "Sat")),
            Arguments.of(of("Mon"), complementOf("Sun", "Mon")),
            Arguments.of(of("Mon"), full()),

            Arguments.of(of("Mon", "Tue"), null),
            Arguments.of(of("Mon", "Tue"), new Object()),
            Arguments.of(of("Mon", "Tue"), of("Sun", "Sat")),
            Arguments.of(of("Mon", "Tue"), of("Sun", "Mon")),
            Arguments.of(of("Mon", "Tue"), of("Sun", "Mon", "Tue")),
            Arguments.of(of("Mon", "Tue"), complementOf()),
            Arguments.of(of("Mon", "Tue"), complementOf("Sun", "Sat")),
            Arguments.of(of("Mon", "Tue"), complementOf("Sun", "Mon")),
            Arguments.of(of("Mon", "Tue"), complementOf("Sun", "Mon", "Tue")),
            Arguments.of(of("Mon", "Tue"), full()),

            Arguments.of(complementOf("Mon"), null),
            Arguments.of(complementOf("Mon"), new Object()),
            Arguments.of(complementOf("Mon"), complementOf()),
            Arguments.of(complementOf("Mon"), complementOf("Sun")),
            Arguments.of(complementOf("Mon"), complementOf("Sun", "Sat")),
            Arguments.of(complementOf("Mon"), complementOf("Sun", "Mon")),
            Arguments.of(complementOf("Mon"), full()),

            Arguments.of(complementOf("Mon", "Tue"), null),
            Arguments.of(complementOf("Mon", "Tue"), new Object()),
            Arguments.of(complementOf("Mon", "Tue"), complementOf()),
            Arguments.of(complementOf("Mon", "Tue"), complementOf("Sun", "Sat")),
            Arguments.of(complementOf("Mon", "Tue"), complementOf("Sun", "Mon")),
            Arguments.of(complementOf("Mon", "Tue"), complementOf("Sun", "Mon", "Tue")),
            Arguments.of(complementOf("Mon", "Tue"), full()),

            Arguments.of(full(), null),
            Arguments.of(full(), new Object())
        );
    }

    @Test
    void testContainsNull() {
        final XSet<String> tested = of("Mon");
        assertThrows(NullPointerException.class, () -> tested.contains(null));
    }

    @ParameterizedTest
    @MethodSource("containsParameters")
    void testContains(final XSet<String> set, final String item, final boolean expected) {
        assertThat("contains " + set + " :: " + item, set.contains(item), is(expected));
    }

    static Stream<Arguments> containsParameters() {
        return Stream.of(
            Arguments.of(empty(), "Mon", false),

            Arguments.of(of("Mon"), "Mon", true),
            Arguments.of(of("Mon"), "Tue", false),

            Arguments.of(of("Mon", "Tue"), "Mon", true),
            Arguments.of(of("Mon", "Tue"), "Tue", true),
            Arguments.of(of("Mon", "Tue"), "Sun", false),

            Arguments.of(complementOf("Mon", "Tue"), "Mon", false),
            Arguments.of(complementOf("Mon", "Tue"), "Tue", false),
            Arguments.of(complementOf("Mon", "Tue"), "Sun", true),

            Arguments.of(complementOf("Mon"), "Mon", false),
            Arguments.of(complementOf("Mon"), "Tue", true),

            Arguments.of(full(), "Mon", true)
        );
    }

    @Test
    void testContainsAllNull() {
        final XSet<String> tested = of("Mon");
        final Collection<String> nullCollection = null;
        final Collection<String> nullItemCollection = Collections.singleton((String) null);

        assertAll(
            () -> assertThrows(NullPointerException.class, () -> tested.containsAll(nullCollection), "null collection"),
            () -> assertThrows(NullPointerException.class, () -> tested.containsAll(nullItemCollection), "null item  collection")
        );
    }

    @ParameterizedTest
    @MethodSource("containsAllParameters")
    void testContainsAll(final XSet<String> set, final Collection<String> items, final boolean expected) {
        assertThat("containsAll " + set + " :: " + items, set.containsAll(items), is(expected));
    }

    static Stream<Arguments> containsAllParameters() {
        return Stream.of(
            Arguments.of(empty(), Collections.emptySet(), true),
            Arguments.of(empty(), Collections.singleton("Mon"), false),
            Arguments.of(empty(), Arrays.asList("Mon", "Tue"), false),

            Arguments.of(of("Mon"), Collections.emptySet(), true),
            Arguments.of(of("Mon"), Collections.singleton("Mon"), true),
            Arguments.of(of("Mon"), Collections.singleton("Tue"), false),
            Arguments.of(of("Mon"), Arrays.asList("Mon", "Tue"), false),
            Arguments.of(of("Mon"), Arrays.asList("Mon", "Sun"), false),

            Arguments.of(of("Mon", "Tue"), Collections.singleton("Mon"), true),
            Arguments.of(of("Mon", "Tue"), Collections.singleton("Tue"), true),
            Arguments.of(of("Mon", "Tue"), Collections.singleton("Sun"), false),
            Arguments.of(of("Mon", "Tue"), Arrays.asList("Mon", "Tue"), true),
            Arguments.of(of("Mon", "Tue"), Arrays.asList("Mon", "Sun"), false),

            Arguments.of(complementOf("Mon", "Tue"), Collections.singleton("Mon"), false),
            Arguments.of(complementOf("Mon", "Tue"), Collections.singleton("Tue"), false),
            Arguments.of(complementOf("Mon", "Tue"), Collections.singleton("Sun"), true),
            Arguments.of(complementOf("Mon", "Tue"), Arrays.asList("Mon", "Tue"), false),
            Arguments.of(complementOf("Mon", "Tue"), Arrays.asList("Mon", "Sun"), false),
            Arguments.of(complementOf("Mon", "Tue"), Arrays.asList("Sat", "Sun"), true),

            Arguments.of(complementOf("Mon"), Collections.emptySet(), true),
            Arguments.of(complementOf("Mon"), Collections.singleton("Mon"), false),
            Arguments.of(complementOf("Mon"), Collections.singleton("Tue"), true),
            Arguments.of(complementOf("Mon"), Arrays.asList("Mon", "Tue"), false),
            Arguments.of(complementOf("Mon"), Arrays.asList("Sat", "Sun"), true),

            Arguments.of(full(), Collections.emptySet(), true),
            Arguments.of(full(), Collections.singleton("Mon"), true),
            Arguments.of(full(), Arrays.asList("Mon", "Tue"), true)
        );
    }

    @Test
    void testContainsAnyNull() {
        final XSet<String> tested = of("Mon");
        final Collection<String> nullCollection = null;
        final Collection<String> nullItemCollection = Collections.singleton((String) null);

        assertAll(
            () -> assertThrows(NullPointerException.class, () -> tested.containsAny(nullCollection), "null collection"),
            () -> assertThrows(NullPointerException.class, () -> tested.containsAny(nullItemCollection), "null item  collection")
        );
    }

    @ParameterizedTest
    @MethodSource("containsAnyParameters")
    void testContainsAny(final XSet<String> set, final Collection<String> items, final boolean expected) {
        assertThat("containsAny " + set + " :: " + items, set.containsAny(items), is(expected));
    }

    static Stream<Arguments> containsAnyParameters() {
        return Stream.of(
            Arguments.of(empty(), Collections.emptySet(), true),
            Arguments.of(empty(), Collections.singleton("Mon"), false),
            Arguments.of(empty(), Arrays.asList("Mon", "Tue"), false),

            Arguments.of(of("Mon"), Collections.emptySet(), true),
            Arguments.of(of("Mon"), Collections.singleton("Mon"), true),
            Arguments.of(of("Mon"), Collections.singleton("Tue"), false),
            Arguments.of(of("Mon"), Arrays.asList("Mon", "Tue"), true),
            Arguments.of(of("Mon"), Arrays.asList("Sun", "Sat"), false),

            Arguments.of(of("Mon", "Tue"), Collections.singleton("Mon"), true),
            Arguments.of(of("Mon", "Tue"), Collections.singleton("Tue"), true),
            Arguments.of(of("Mon", "Tue"), Collections.singleton("Sun"), false),
            Arguments.of(of("Mon", "Tue"), Arrays.asList("Mon", "Tue"), true),
            Arguments.of(of("Mon", "Tue"), Arrays.asList("Mon", "Sun"), true),
            Arguments.of(of("Mon", "Tue"), Arrays.asList("Sat", "Sun"), false),

            Arguments.of(complementOf("Mon", "Tue"), Collections.singleton("Mon"), false),
            Arguments.of(complementOf("Mon", "Tue"), Collections.singleton("Tue"), false),
            Arguments.of(complementOf("Mon", "Tue"), Collections.singleton("Sun"), true),
            Arguments.of(complementOf("Mon", "Tue"), Arrays.asList("Mon", "Tue"), false),
            Arguments.of(complementOf("Mon", "Tue"), Arrays.asList("Mon", "Sun"), true),
            Arguments.of(complementOf("Mon", "Tue"), Arrays.asList("Sat", "Sun"), true),

            Arguments.of(complementOf("Mon"), Collections.singleton("Mon"), false),
            Arguments.of(complementOf("Mon"), Collections.singleton("Tue"), true),
            Arguments.of(complementOf("Mon"), Arrays.asList("Mon", "Tue"), true),
            Arguments.of(complementOf("Mon"), Arrays.asList("Sat", "Sun"), true),

            Arguments.of(full(), Collections.emptySet(), true),
            Arguments.of(full(), Collections.singleton("Mon"), true),
            Arguments.of(full(), Arrays.asList("Mon", "Tue"), true)
        );
    }

    @ParameterizedTest
    @MethodSource("subtractParameters")
    void testSubtract(final XSet<String> set1, final XSet<String> set2, final XSet<String> expected) {
        assertThat("subtract " + set1 + " " + set2, set1.subtract(set2), is(expected));
    }

    static Stream<Arguments> subtractParameters() {
        return Stream.of(
            Arguments.of(empty(), empty(), empty()),
            Arguments.of(empty(), of("Sun"), empty()),
            Arguments.of(empty(), of("Sun", "Mon"), empty()),
            Arguments.of(empty(), complementOf("Sun"), empty()),
            Arguments.of(empty(), complementOf("Sun", "Mon"), empty()),
            Arguments.of(empty(), full(), empty()),

            Arguments.of(of("Mon"), empty(), of("Mon")),
            Arguments.of(of("Mon"), of("Sun"), of("Mon")),
            Arguments.of(of("Mon"), of("Mon"), empty()),
            Arguments.of(of("Mon"), of("Sat", "Sun"), of("Mon")),
            Arguments.of(of("Mon"), of("Sun", "Mon"), empty()),
            Arguments.of(of("Mon"), complementOf("Sun"), empty()),
            Arguments.of(of("Mon"), complementOf("Mon"), of("Mon")),
            Arguments.of(of("Mon"), complementOf("Sat", "Sun"), empty()),
            Arguments.of(of("Mon"), complementOf("Sun", "Mon"), of("Mon")),
            Arguments.of(of("Mon"), full(), empty()),

            Arguments.of(of("Mon", "Tue"), empty(), of("Mon", "Tue")),
            Arguments.of(of("Mon", "Tue"), of("Mon"), of("Tue")),
            Arguments.of(of("Mon", "Tue"), of("Sun"), of("Mon", "Tue")),
            Arguments.of(of("Mon", "Tue"), of("Sat", "Sun"), of("Mon", "Tue")),
            Arguments.of(of("Mon", "Tue"), of("Sun", "Mon"), of("Tue")),
            Arguments.of(of("Mon", "Tue"), of("Mon", "Tue"), empty()),
            Arguments.of(of("Mon", "Tue"), of("Mon", "Tue", "Sun"), empty()),
            Arguments.of(of("Mon", "Tue"), complementOf("Sat", "Sun"), empty()),
            Arguments.of(of("Mon", "Tue"), complementOf("Sun", "Mon"), of("Mon")),
            Arguments.of(of("Mon", "Tue"), complementOf("Mon", "Tue"), of("Mon", "Tue")),
            Arguments.of(of("Mon", "Tue"), complementOf("Mon", "Tue", "Sun"), of("Mon", "Tue")),
            Arguments.of(of("Mon", "Tue"), full(), empty()),

            Arguments.of(complementOf("Mon"), empty(), complementOf("Mon")),
            Arguments.of(complementOf("Mon"), of("Mon"), complementOf("Mon")),
            Arguments.of(complementOf("Mon"), of("Tue"), complementOf("Mon", "Tue")),
            Arguments.of(complementOf("Mon"), of("Mon", "Tue"), complementOf("Mon", "Tue")),
            Arguments.of(complementOf("Mon"), of("Sat", "Sun"), complementOf("Mon", "Sat", "Sun")),
            Arguments.of(complementOf("Mon"), complementOf("Mon"), empty()),
            Arguments.of(complementOf("Mon"), complementOf("Sun"), of("Sun")),
            Arguments.of(complementOf("Mon"), complementOf("Sun", "Mon"), of("Sun")),
            Arguments.of(complementOf("Mon"), full(), empty()),

            Arguments.of(complementOf("Mon", "Tue"), empty(), complementOf("Mon", "Tue")),
            Arguments.of(complementOf("Mon", "Tue"), of("Mon"), complementOf("Mon", "Tue")),
            Arguments.of(complementOf("Mon", "Tue"), of("Sun"), complementOf("Sun", "Mon", "Tue")),
            Arguments.of(complementOf("Mon", "Tue"), of("Mon", "Tue"), complementOf("Mon", "Tue")),
            Arguments.of(complementOf("Mon", "Tue"), of("Sun", "Mon"), complementOf("Sun", "Mon", "Tue")),
            Arguments.of(complementOf("Mon", "Tue"), of("Sun", "Mon", "Tue"), complementOf("Sun", "Mon", "Tue")),
            Arguments.of(complementOf("Mon", "Tue"), complementOf("Mon"), empty()),
            Arguments.of(complementOf("Mon", "Tue"), complementOf("Sun"), of("Sun")),
            Arguments.of(complementOf("Mon", "Tue"), complementOf("Mon", "Tue"), empty()),
            Arguments.of(complementOf("Mon", "Tue"), complementOf("Sun", "Mon"), of("Sun")),
            Arguments.of(complementOf("Mon", "Tue"), complementOf("Sun", "Mon", "Tue"), of("Sun")),
            Arguments.of(complementOf("Mon", "Tue"), full(), empty()),

            Arguments.of(full(), empty(), full()),
            Arguments.of(full(), of("Mon"), complementOf("Mon")),
            Arguments.of(full(), of("Mon", "Tue"), complementOf("Mon", "Tue")),
            Arguments.of(full(), complementOf("Sun"), of("Sun")),
            Arguments.of(full(), complementOf("Sun", "Mon"), of("Sun", "Mon")),
            Arguments.of(full(), full(), empty())
        );
    }

    @ParameterizedTest
    @MethodSource("intersectParameters")
    void testIntersect(final XSet<String> set1, final XSet<String> set2, final XSet<String> expected) {
        assertAll("intersect " + set1 + " " + set2,
            () -> assertThat(" -> ", set1.intersect(set2), is(expected)),
            () -> assertThat(" <- ", set2.intersect(set1), is(expected))
        );
    }

    static Stream<Arguments> intersectParameters() {
        return Stream.of(
            Arguments.of(empty(), empty(), empty()),
            Arguments.of(empty(), of("Sun"), empty()),
            Arguments.of(empty(), of("Sun", "Mon"), empty()),
            Arguments.of(empty(), complementOf("Sun"), empty()),
            Arguments.of(empty(), complementOf("Sun", "Mon"), empty()),
            Arguments.of(empty(), full(), empty()),

            Arguments.of(of("Mon"), of("Sun"), empty()),
            Arguments.of(of("Mon"), of("Mon"), of("Mon")),
            Arguments.of(of("Mon"), of("Sat", "Sun"), empty()),
            Arguments.of(of("Mon"), of("Sun", "Mon"), of("Mon")),
            Arguments.of(of("Mon"), complementOf("Sun"), of("Mon")),
            Arguments.of(of("Mon"), complementOf("Mon"), empty()),
            Arguments.of(of("Mon"), complementOf("Sat", "Sun"), of("Mon")),
            Arguments.of(of("Mon"), complementOf("Sun", "Mon"), empty()),
            Arguments.of(of("Mon"), full(), of("Mon")),

            Arguments.of(of("Mon", "Tue"), of("Sat", "Sun"), empty()),
            Arguments.of(of("Mon", "Tue"), of("Sun", "Mon"), of("Mon")),
            Arguments.of(of("Mon", "Tue"), of("Mon", "Tue"), of("Mon", "Tue")),
            Arguments.of(of("Mon", "Tue"), of("Mon", "Tue", "Sun"), of("Mon", "Tue")),
            Arguments.of(of("Mon", "Tue"), complementOf("Sat", "Sun"), of("Mon", "Tue")),
            Arguments.of(of("Mon", "Tue"), complementOf("Sun", "Mon"), of("Tue")),
            Arguments.of(of("Mon", "Tue"), complementOf("Mon", "Tue"), empty()),
            Arguments.of(of("Mon", "Tue"), complementOf("Mon", "Tue", "Sun"), empty()),
            Arguments.of(of("Mon", "Tue"), full(), of("Mon", "Tue")),

            Arguments.of(complementOf("Mon"), complementOf("Mon"), complementOf("Mon")),
            Arguments.of(complementOf("Mon"), complementOf("Sun"), complementOf("Sun", "Mon")),
            Arguments.of(complementOf("Mon"), complementOf("Sun", "Mon"), complementOf("Sun", "Mon")),
            Arguments.of(complementOf("Mon"), full(), complementOf("Mon")),

            Arguments.of(complementOf("Mon", "Tue"), complementOf("Mon", "Tue"), complementOf("Mon", "Tue")),
            Arguments.of(complementOf("Mon", "Tue"), complementOf("Sun", "Mon"), complementOf("Sun", "Mon", "Tue")),
            Arguments.of(complementOf("Mon", "Tue"), complementOf("Sun", "Mon", "Tue"), complementOf("Sun", "Mon", "Tue")),
            Arguments.of(complementOf("Mon", "Tue"), full(), complementOf("Mon", "Tue")),

            Arguments.of(full(), full(), full())
        );
    }

    @ParameterizedTest
    @MethodSource("unionParameters")
    void testUnion(final XSet<String> set1, final XSet<String> set2, final XSet<String> expected) {
        assertAll("union " + set1 + " " + set2,
            () -> assertThat(" -> ", set1.union(set2), is(expected)),
            () -> assertThat(" <- ", set2.union(set1), is(expected))
        );
    }

    static Stream<Arguments> unionParameters() {
        return Stream.of(
            Arguments.of(empty(), empty(), empty()),
            Arguments.of(empty(), of("Sun"), of("Sun")),
            Arguments.of(empty(), of("Sun", "Mon"), of("Sun", "Mon")),
            Arguments.of(empty(), complementOf("Sun"), complementOf("Sun")),
            Arguments.of(empty(), complementOf("Sun", "Mon"), complementOf("Sun", "Mon")),
            Arguments.of(empty(), full(), full()),

            Arguments.of(of("Mon"), of("Sun"), of("Sun", "Mon")),
            Arguments.of(of("Mon"), of("Mon"), of("Mon")),
            Arguments.of(of("Mon"), of("Sat", "Sun"), of("Sun", "Mon", "Sat")),
            Arguments.of(of("Mon"), of("Sun", "Mon"), of("Sun", "Mon")),
            Arguments.of(of("Mon"), complementOf("Sun"), complementOf("Sun")),
            Arguments.of(of("Mon"), complementOf("Mon"), full()),
            Arguments.of(of("Mon"), complementOf("Sat", "Sun"), complementOf("Sat", "Sun")),
            Arguments.of(of("Mon"), complementOf("Sun", "Mon"), complementOf("Sun")),
            Arguments.of(of("Mon"), full(), full()),

            Arguments.of(of("Mon", "Tue"), of("Sat", "Sun"), of("Sun", "Mon", "Tue", "Sat")),
            Arguments.of(of("Mon", "Tue"), of("Sun", "Mon"), of("Sun", "Mon", "Tue")),
            Arguments.of(of("Mon", "Tue"), of("Mon", "Tue"), of("Mon", "Tue")),
            Arguments.of(of("Mon", "Tue"), of("Mon", "Tue", "Sun"), of("Sun", "Mon", "Tue")),
            Arguments.of(of("Mon", "Tue"), complementOf("Sat", "Sun"), complementOf("Sat", "Sun")),
            Arguments.of(of("Mon", "Tue"), complementOf("Sun", "Mon"), complementOf("Sun")),
            Arguments.of(of("Mon", "Tue"), complementOf("Mon", "Tue"), full()),
            Arguments.of(of("Mon", "Tue"), complementOf("Mon", "Tue", "Sun"), complementOf("Sun")),
            Arguments.of(of("Mon", "Tue"), full(), full()),

            Arguments.of(complementOf("Mon"), complementOf("Mon"), complementOf("Mon")),
            Arguments.of(complementOf("Mon"), complementOf("Sun"), full()),
            Arguments.of(complementOf("Mon"), complementOf("Sun", "Mon"), complementOf("Mon")),
            Arguments.of(complementOf("Mon"), full(), full()),

            Arguments.of(complementOf("Mon", "Tue"), complementOf("Mon", "Tue"), complementOf("Mon", "Tue")),
            Arguments.of(complementOf("Mon", "Tue"), complementOf("Sun", "Mon"), complementOf("Mon")),
            Arguments.of(complementOf("Mon", "Tue"), complementOf("Sun", "Mon", "Tue"), complementOf("Mon", "Tue")),
            Arguments.of(complementOf("Mon", "Tue"), full(), full()),

            Arguments.of(full(), full(), full())
        );
    }

    @ParameterizedTest
    @MethodSource("emptyIsAlwaysSameInstanceParameters")
    void testEmptyIsAlwaysSameInstance(final String message, final XSet<String> tested) {
        assertThat(message, tested, CoreMatchers.sameInstance(empty()));
    }

    static Stream<Arguments> emptyIsAlwaysSameInstanceParameters() {
        return Stream.of(
            Arguments.of("empty", empty()),
            Arguments.of("of[0]", of()),

            Arguments.of("complementOf[0].complement()", complementOf().complement()),
            Arguments.of("full.complement()", full().complement()),

            Arguments.of("empty.subtract(full)", empty().subtract(full())),
            Arguments.of("of.subtract(complementOf)", of("Mon", "Tue").subtract(complementOf("Sun"))),

            Arguments.of("empty.intersect(of)", empty().intersect(of("Mon", "Tue"))),
            Arguments.of("of.intersect(empty)", of("Mon", "Tue").intersect(empty())),
            Arguments.of("of.intersect(of)", of("Mon", "Tue").intersect(of("Sat", "Sun")))
        );
    }

    @ParameterizedTest
    @MethodSource("fullIsAlwaysSameInstanceParameters")
    void testFullIsAlwaysSameInstance(final String message, final XSet<String> tested) {
        assertThat(message, tested, CoreMatchers.sameInstance(full()));
    }

    static Stream<Arguments> fullIsAlwaysSameInstanceParameters() {
        return Stream.of(
            Arguments.of("full", full()),
            Arguments.of("complementOf[0]", complementOf()),

            Arguments.of("of[0].complement()", of().complement()),
            Arguments.of("empty.complement()", empty().complement()),

            Arguments.of("full.subtract(empty)", full().subtract(empty())),

            Arguments.of("full.intersect(full)", full().intersect(full())),

            Arguments.of("of.union(complementOf)", of("Mon", "Tue").union(complementOf("Mon", "Tue"))),
            Arguments.of("full.union(empty)", full().union(empty()))
        );
    }

    private static XSet<String> of(final String... items) {
        return XSet.of(items);
    }

    private static XSet<String> complementOf(final String... items) {
        return XSet.complementOf(items);
    }

    private static XSet<String> empty() {
        return XSet.empty();
    }

    private static XSet<String> full() {
        return XSet.full();
    }
}
