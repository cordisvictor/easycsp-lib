/*
 * Copyright 2011 Victor Cordis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Please contact the author ( cordis.victor@gmail.com ) if you need additional
 * information or have any questions.
 */
package net.sourceforge.easycsp;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Domain class that represents the domain of a {@linkplain Variable}.
 *
 * @param <T> values class
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.1.0
 * @since 1.0
 */
public interface Domain<T> extends Iterable<T> {

    /**
     * Returns the size of this instance.
     *
     * @return the size
     */
    int size();

    /**
     * Returns true if this instance is empty, false otherwise.
     *
     * @return true if empty, false otherwise
     */
    default boolean isEmpty() {
        return this.size() == 0;
    }

    /**
     * Gets the value at the given index.
     *
     * @param index of the value to get
     * @return the value at index
     */
    T get(int index);

    /**
     * Returns true if this instance contains the given value, false otherwise.
     *
     * @param value whose presence is to be tested
     * @return true if this instance contains the given value, false otherwise
     */
    boolean contains(T value);

    /**
     * Returns the index of the given value in this instance.
     *
     * @param value whose index is searched
     * @return index of the given value, -1 if the value is not contained in
     * this instance
     */
    int indexOf(T value);

    /**
     * Adds the given value to this instance.
     *
     * @param value to be added
     */
    void add(T value);

    /**
     * Adds all the values of the given domain to this instance.
     */
    default void addAll(Domain<T> domain) {
        for (T o : domain) {
            add(o);
        }
    }

    /**
     * Removes the given value from this instance and returns true. If the value
     * is not found then false is returned.
     *
     * @param value to be removed
     * @return true if removed, false otherwise
     */
    boolean remove(T value);

    /**
     * Removes and returns the value at the specified index from this instance.
     *
     * @param index of the value to be removed
     * @return the removed value
     */
    T removeAt(int index);

    /**
     * Clears all values of this instance.
     */
    void clear();

    /**
     * Clones this instance.
     *
     * @return a clone of this instance
     */
    Domain<T> clone();

    /**
     * Returns a stream of over this domain.
     *
     * @return a lazy stream of objects
     */
    default Stream<T> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), false);
    }

    /**
     * Returns a new iterator for this instance.
     *
     * @return the new iterator
     */
    default Iterator<T> iterator() {
        return domainIterator();
    }

    /**
     * Returns a new domain iterator for this instance.
     *
     * @return the new iterator
     */
    DomainIterator<T> domainIterator();

    /**
     * DomainIterator extends the {@linkplain Iterator} interface
     * with domain specific functionality.
     * The implementation of this interface should be the most effective way
     * of iterating on {@link Domain} instances.
     *
     * @param <T> domain values class
     */
    interface DomainIterator<T> extends Iterator<T> {

        /**
         * Returns the current index of this instance in the form of a
         * zero-based index.
         *
         * @return the current index
         */
        int currentIndex();

        /**
         * Resets this instance, setting it to the before first index.
         */
        void reset();
    }
}
