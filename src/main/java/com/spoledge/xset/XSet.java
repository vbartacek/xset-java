package com.spoledge.xset;

import java.util.Collections;
import java.util.Set;


/**
 * Extended set that can hold also complements.
 * <p>
 * This class is an extension of a finite set.
 * It can hold both finite sets and those inifite ones that can be represented as a complement of finite sets.
 * <p>
 * The implementation maintains a finite {@link Set} of {@code items} and a {@code complement} flag.
 * <p>
 * This class is immutable - once created it cannot change its content.
 *
 * @author Vaclav Bartacek
 * @param E - the type of elements maintained by this extended set
 */
public final class XSet<E> {

    private static final XSet EMPTY = new XSet<>(Collections.emptySet(), false);

    private static final XSet FULL = new XSet<>(Collections.emptySet(), true);

    private final Set<E> items;

    private final boolean complement;

    private XSet(final Set<E> items, final boolean complement) {
        this.items = items;
        this.complement = complement;
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
     * @return full extended set = a complement of empty extended set
     */
    @SuppressWarnings("unchecked")
    public static <T> XSet<T> full() {
        return FULL;
    }

}
