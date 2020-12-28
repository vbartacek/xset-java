/*
 * Copyright (c) 2020 Vaclav Bartacek
 * MIT License
 */

package com.spoledge.xset;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


/**
 * Extended set that can hold also complements.
 * <p>
 * This class is an extension of a finite set.
 * It can hold both finite sets and those inifite ones that can be represented as a complement of finite sets.
 * <p>
 * The implementation maintains a finite {@link Set} of {@code items} and a {@code boolean} {@code complementary} flag.
 * <p>
 * This class is immutable - once created its content cannot be changed.
 * It also means that this class is thread-safe.
 *
 * @author Vaclav Bartacek
 * @param E - the type of elements maintained by this extended set
 */
public final class XSet<E> {

    private static final XSet EMPTY = new XSet<>(Collections.emptySet(), false);

    private static final XSet FULL = new XSet<>(Collections.emptySet(), true);

    private final Set<E> items;

    private final boolean complementary;

    private XSet(final Set<E> items, final boolean complementary) {
        this.items = items;
        this.complementary = complementary;
    }

    /**
     * Convenient method for obtaining an empty extended set.
     *
     * @param <T> the element type
     * @return empty extended set
     */
    @SuppressWarnings("unchecked")
    public static <T> XSet<T> empty() {
        return EMPTY;
    }

    /**
     * Convenient method for obtaining a full extended set.
     *
     * @param <T> the element type
     * @return full extended set = a complement of an empty extended set
     */
    @SuppressWarnings("unchecked")
    public static <T> XSet<T> full() {
        return FULL;
    }

    /**
     * Creates a new finite extended set.
     * <p>
     * This method creates a standard set containing exactly the items being specified.
     *
     * @param items the items of the finite set
     * @param <T> the element type
     * @return the finite extended set
     */
    @SafeVarargs
    public static <T> XSet<T> of(final T... items) {
        Objects.requireNonNull(items, "items must not be null");

        return of(Arrays.asList(items));
    }

    /**
     * Creates a new finite extended set.
     * <p>
     * This method creates a standard set containing exactly the items being specified.
     *
     * @param collection the collection of items of the finite set
     * @param <T> the element type
     * @return the finite extended set
     */
    public static <T> XSet<T> of(final Collection<T> collection) {
        Objects.requireNonNull(collection, "collection must not be null");

        return toXSet(collection, false);
    }

    /**
     * Creates a new complementary extended set.
     *
     * @param items the complementary items of the result set
     * @param <T> the element type
     * @return the complement of the finite extended set
     */
    @SafeVarargs
    public static <T> XSet<T> complementOf(final T... items) {
        Objects.requireNonNull(items, "items must not be null");

        return complementOf(Arrays.asList(items));
    }

    /**
     * Creates a new complementary extended set.
     *
     * @param collection the collection of complementary items of the result set
     * @param <T> the element type
     * @return the complement of the finite extended set
     */
    public static <T> XSet<T> complementOf(final Collection<T> collection) {
        Objects.requireNonNull(collection, "collection must not be null");

        return toXSet(collection, true);
    }

    private static <T> XSet<T> toXSet(final Collection<T> collection, final boolean complementary) {
        if (collection.isEmpty()) {
            return complementary ? full() : empty();
        } else {
            final Set<T> items = nonEmptyCollectionToSet(collection);
            return new XSet<>(items, complementary);
        }
    }

    private static <T> Set<T> nonEmptyCollectionToSet(final Collection<T> collection) {
        if (collection.size() == 1) {
            return singleton(collection);
        } else {
            final Set<T> result = newHashSet(collection);
            // be aware of potential duplicity items that are being collapsed to singleton:
            return result.size() == 1 ? singleton(collection) : Collections.unmodifiableSet(result);
        }
    }

    /**
     * Returns the set of {@code items} without the {@code complementary} flag.
     * <p>
     * For finite sets this method returns the items belonging to the set.
     * For complementary sets this method returns the complementary items.
     *
     * @return the items
     */
    public Set<E> getItems() {
        return items;
    }

    /**
     * Returns {@code true} if this set is a complement of a finite set.
     *
     * @return complementary flag
     */
    public boolean isComplementary() {
        return complementary;
    }

    /**
     * Returns true if this set is a finite set.
     *
     * @return finite flag
     */
    public boolean isFinite() {
        return !complementary;
    }

    /**
     * Returns true if this set is an empty set.
     *
     * @return empty flag
     * @see #isEmpty()
     * @see #isTrivial()
     */
    public boolean isEmpty() {
        return !complementary && items.isEmpty();
    }

    /**
     * Returns true if this set is a complement of an empty set.
     *
     * @return full flag
     * @see #isEmpty()
     * @see #isTrivial()
     */
    public boolean isFull() {
        return complementary && items.isEmpty();
    }

    /**
     * Returns true if this set is trivial - either empty or full.
     *
     * @return trivial flag
     * @see #isEmpty()
     * @see #isFull()
     */
    public boolean isTrivial() {
        return items.isEmpty();
    }

    private static <T> Set<T> singleton(final Collection<T> collection) {
        return Collections.singleton(collection.iterator().next());
    }

    private static <T> Set<T> newHashSet(final Collection<T> collection) {
        return new HashSet<>(collection);
    }

}