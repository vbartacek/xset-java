/*
 * Copyright (C) 2020 Vaclav Bartacek
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
 * The standard set operations are supported: contains, containsAll, containsAny, complement, subtraction, instersection and union.
 * {@code XSet} is a generic type.
 * <p>
 * The implementation maintains a finite {@link Set} of {@code items} and a {@code boolean} flag {@code complementary}.
 * The {@code items} should hold only a given type of elements.
 * The {@code items} cannot hold {@code null} elements.
 * <p>
 * The {@code XSet} cannot directly implement {@link java.util.Collection}, because the method {@link java.util.Collection#contains(Object)}
 * is not type-safe and we cannot implement such method here.
 * Instead of that we implement method with the same name, but taking as the parameter only objects of the given type.
 * <p>
 * This class is immutable - once created its content cannot be changed.
 * It also means that this class is thread-safe.
 *
 * @author Vaclav Bartacek
 * @param <E> - the type of elements maintained by this extended set
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
     * @throws NullPointerException if any of the specified items is {@code null}
     */
    @SafeVarargs
    public static <T> XSet<T> of(final T... items) {
        requireNonNull(items);

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
     * @throws NullPointerException if the collection or any of the specified items is {@code null}
     */
    public static <T> XSet<T> of(final Collection<T> collection) {
        requireNonNull(collection);

        return toXSet(collection, false);
    }

    /**
     * Creates a new complementary extended set.
     *
     * @param items the complementary items of the result set
     * @param <T> the element type
     * @return the complement of the finite extended set
     * @throws NullPointerException if any of the specified items is {@code null}
     */
    @SafeVarargs
    public static <T> XSet<T> complementOf(final T... items) {
        requireNonNull(items);

        return complementOf(Arrays.asList(items));
    }

    /**
     * Creates a new complementary extended set.
     *
     * @param collection the collection of complementary items of the result set
     * @param <T> the element type
     * @return the complement of the finite extended set
     * @throws NullPointerException if the collection or any of the specified items is {@code null}
     */
    public static <T> XSet<T> complementOf(final Collection<T> collection) {
        requireNonNull(collection);

        return toXSet(collection, true);
    }

    private static <T> XSet<T> toXSet(final Collection<T> collection, final boolean complementary) {
        if (collection.isEmpty()) {
            return complementary ? full() : empty();
        }
        else {
            final Set<T> items = nonEmptyCollectionToSet(collection);
            return new XSet<>(items, complementary);
        }
    }

    private static <T> Set<T> nonEmptyCollectionToSet(final Collection<T> collection) {
        requireNonNullItems(collection);

        if (collection.size() == 1) {
            return singleton(collection);
        }
        else {
            final Set<T> result = newHashSet(collection);
            // be aware of potential duplicity items that are being collapsed to singleton:
            return result.size() == 1 ? singleton(collection) : Collections.unmodifiableSet(result);
        }
    }

    private static <T> void requireNonNullItems(final Collection<T> collection) {
        // we cannot call contains(null) because the underlying collection might not support nulls
        final boolean containsNullItem = collection.stream()
            .anyMatch(Objects::isNull);

        if (containsNullItem) {
            throw new NullPointerException("items must not be null");
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

    /**
     * Returns {@code true} if this extended set contains the specified element.
     * <p>
     * More formally, returns {@code true} if and only if this set:
     * <ul>
     * <li>is a finite set and it contains at least one element {@code e} such that {@code Objects.equals(item, e)}</li>
     * <li>is a complementary set to a finite set {@code F} and {@code !F.contains(item)}</li>
     * </ul>
     *
     * @param item element whose presence in this collection is to be tested
     * @return true if this extended set contains the element
     * @throws NullPointerException if the specified element is {@code null}
     */
    public boolean contains(final E item) {
        requireNonNull(item);

        final boolean containsItem = items.contains(item);
        return complementary ? !containsItem : containsItem;
    }

    /**
     * Returns {@code true} if this extended set contains all elements of the specified collection.
     * <p>
     * More formally, returns {@code true} if and only if: for each element {@code e} of the {@code otherItems}
     * the expression {@code this.contains(e)} returns {@code true}.
     * <p>
     * That also means that this method returns {@code true} for empty {@code otherItems} collections.
     *
     * @param otherItems collection of elements whose presence in this collection is to be tested
     * @return true if this extended set contains all the elements
     * @throws NullPointerException if the specified collection or any element of the collection is {@code null}
     * @see #contains(Object)
     * @see #containsAny(Collection)
     */
    public boolean containsAll(final Collection<E> otherItems) {
        requireNonNull(otherItems);
        requireNonNullItems(otherItems);

        if (otherItems.isEmpty()) {
            return true;
        }
        else if (complementary) {
            return otherItems.stream()
                .noneMatch(items::contains);
        }
        else {
            return items.containsAll(otherItems);
        }
    }

    /**
     * Returns {@code true} if this extended set contains at least one element of the specified collection or the collection is empty.
     * <p>
     * More formally, returns {@code false} if and only if: for each element {@code e} of the {@code otherItems}
     * the expression {@code this.contains(e)} returns {@code false}.
     * <p>
     * That also means that this method returns {@code true} for empty {@code otherItems} collections.
     *
     * @param otherItems collection of elements whose presence in this collection is to be tested
     * @return true if this extended set contains all the elements or the specified collection is empty
     * @throws NullPointerException if the specified collection or any element of the collection is {@code null}
     * @see #contains(Object)
     * @see #containsAll(Collection)
     */
    public boolean containsAny(final Collection<E> otherItems) {
        requireNonNull(otherItems);
        requireNonNullItems(otherItems);

        if (otherItems.isEmpty()) {
            return true;
        }
        else if (complementary) {
            return !items.containsAll(otherItems);
        }
        else {
            return otherItems.stream()
                .anyMatch(items::contains);
        }
    }

    /**
     * Returns the complement of this extended set.
     * <p>
     * This operation inverts the {@code complementary} flag.
     *
     * @return a complementary set to this one
     */
    public XSet<E> complement() {
        if (items.isEmpty()) {
            return complementary ? empty() : full();
        }
        else {
            return new XSet<>(items, !complementary);
        }
    }

    /**
     * Returns the subtraction of the other set from this set.
     *
     * @param other the other extended set
     * @return the subtraction
     * @throws NullPointerException if the other set is {@code null}
     */
    public XSet<E> subtract(final XSet<E> other) {
        requireNonNull(other);

        return intersect(other.complement());
    }

    /**
     * Returns the intersection of this and the other extended sets.
     *
     * @param other the other extended set
     * @return the intersection
     * @throws NullPointerException if the other set is {@code null}
     */
    public XSet<E> intersect(final XSet<E> other) {
        requireNonNull(other);

        if (this.items.isEmpty()) {
            return this.complementary ? other : this;
        }
        else if (other.items.isEmpty()) {
            return other.complementary ? this : other;
        }
        else {
            final Set<E> resultItems = intersectItems(other);
            final boolean resultComplementary = this.complementary && other.complementary;
            return canonicalXSet(resultItems, resultComplementary);
        }
    }

    private Set<E> intersectItems(final XSet<E> other) {
        final Set<E> resultItems;

        if (this.complementary) {
            if (other.complementary) {
                resultItems = union(this.items, other.items);
            }
            else {
                resultItems = minus(other.items, this.items);
            }
        }
        else if (other.complementary) {
            resultItems = minus(this.items, other.items);
        }
        else {
            resultItems = intersect(this.items, other.items);
        }

        return resultItems;
    }

    /**
     * Returns the union of this and the other extended sets.
     *
     * @param other the other extended set
     * @return the union
     * @throws NullPointerException if the other set is {@code null}
     */
    public XSet<E> union(final XSet<E> other) {
        requireNonNull(other);

        if (this.items.isEmpty()) {
            return this.complementary ? this : other;
        }
        else if (other.items.isEmpty()) {
            return other.complementary ? other : this;
        }
        else {
            final Set<E> resultItems = unionItems(other);
            final boolean resultComplement = this.complementary || other.complementary;
            return canonicalXSet(resultItems, resultComplement);
        }
    }

    private Set<E> unionItems(final XSet<E> other) {
        final Set<E> resultItems;

        if (this.complementary) {
            if (other.complementary) {
                resultItems = intersect(this.items, other.items);
            }
            else {
                resultItems = minus(this.items, other.items);
            }
        }
        else if (other.complementary) {
            resultItems = minus(other.items, this.items);
        }
        else {
            resultItems = union(this.items, other.items);
        }

        return resultItems;
    }

    private static <T> Set<T> intersect(final Set<T> set1, final Set<T> set2) {
        final Set<T> result = newHashSet(set1);
        result.retainAll(set2);
        return result;
    }

    private static <T> Set<T> union(final Set<T> set1, final Set<T> set2) {
        final Set<T> result = newHashSet(set1);
        result.addAll(set2);
        return result;
    }

    private static <T> Set<T> minus(final Set<T> set1, final Set<T> set2) {
        final Set<T> result = newHashSet(set1);
        result.removeAll(set2);
        return result;
    }

    private static <T> XSet<T> canonicalXSet(final Set<T> items, final boolean complementary) {
        if (items.isEmpty()) {
            return complementary ? full() : empty();
        }
        else if (items.size() == 1) {
            return new XSet<>(singleton(items), complementary);
        }
        else {
            return new XSet<>(Collections.unmodifiableSet(items), complementary);
        }
    }

    private static <T> Set<T> singleton(final Collection<T> collection) {
        return Collections.singleton(collection.iterator().next());
    }

    private static <T> Set<T> newHashSet(final Collection<T> collection) {
        return new HashSet<>(collection);
    }

    private static <T> void requireNonNull(final XSet<T> other) {
        Objects.requireNonNull(other, "other XSet must not be null");
    }

    private static void requireNonNull(final Object input) {
        Objects.requireNonNull(input, "input must not be null");
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, complementary);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final XSet that = (XSet) other;

        return this.complementary == that.complementary && this.items.equals(that.items);
    }

    @Override
    public String toString() {
        return "XSet{" + (complementary ? "~" : "") + items + '}';
    }

}
