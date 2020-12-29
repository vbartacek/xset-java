/*
 * Copyright (C) 2020 Vaclav Bartacek
 * MIT License
 */

package com.spoledge.xset;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Stream;

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

    @ParameterizedTest
    @MethodSource("createParameters")
    void testCreate(final String message, final Collection<String> items, final boolean complementary) {
        final XSet<String> tested = complementary ? XSet.complementOf(items) : XSet.of(items);
        assertProperties(message, tested, items, complementary);
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

}
