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

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

/**
 * ObjectDomain implements {@link Domain} through a discrete domain of objects.
 * This implementation can contain nulls.
 *
 * @param <T> domain values class
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.1.2
 * @since 1.0
 */
public final class ObjectDomain<T> implements Domain<T>, RandomAccess {

    private Object[] values;
    private int size;

    /**
     * Creates a new empty instance.
     */
    public ObjectDomain() {
        this.values = new Object[5];
        this.size = 0;
    }

    /**
     * Creates a new instance with the given singleton value.
     *
     * @param singleton the single value
     */
    public ObjectDomain(T singleton) {
        this.values = new Object[]{singleton};
        this.size = 1;
    }

    /**
     * Creates a new instance containing the given values.
     *
     * @param values initial values
     */
    public ObjectDomain(T... values) {
        this.values = Arrays.copyOf(values, values.length);
        this.size = values.length;
    }

    private ObjectDomain(ObjectDomain other) {
        this.values = Arrays.copyOf(other.values, other.values.length);
        this.size = other.size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return this.size;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public T get(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("index: " + index);
        }
        return (T) this.values[index];
    }

    /**
     * Sets the value at the specified index with the given value.
     *
     * @param index where to set
     * @param value to set
     */
    public void set(int index, T value) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("index: " + index);
        }
        this.values[index] = value;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean contains(T value) {
        return this.findIndexOf(value) >= 0;
    }

    private int findIndexOf(T value) {
        if (value == null) {
            for (int i = 0; i < this.size; i++) {
                if (this.values[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < this.size; i++) {
                if (value.equals(this.values[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int indexOf(T value) {
        return this.findIndexOf(value);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void add(T value) {
        if (this.size == this.values.length) {
            Object[] newvalues = new Object[(this.values.length * 3) / 2 + 1];
            System.arraycopy(this.values, 0, newvalues, 0, this.size);
            this.values = newvalues;
        }
        this.values[this.size] = value;
        this.size++;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean remove(T value) {
        int index = this.findIndexOf(value);
        if (index >= 0) {
            System.arraycopy(this.values, index + 1, this.values, index, this.size - index - 1);
            this.size--;
            this.values[this.size] = null;
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public T removeAt(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("index: " + index);
        }
        T ret = (T) this.values[index];
        System.arraycopy(this.values, index + 1, this.values, index, this.size - index - 1);
        this.size--;
        this.values[this.size] = null;
        return ret;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void clear() {
        Arrays.fill(this.values, 0, this.size, null);
        this.size = 0;
    }

    /**
     * Reduces the storage capacity of this instance to it's size.
     */
    public void trimToSize() {
        if (this.size < this.values.length) {
            this.values = Arrays.copyOf(this.values, this.size);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int ret = 17 * this.size;
        for (int i = 0; i < this.size; i++) {
            ret += 31 * (this.values[i] == null ? 0 : this.values[i].hashCode());
        }
        return ret;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object o) {
        if (o != null && o.getClass().equals(this.getClass())) {
            ObjectDomain other = (ObjectDomain) o;
            if (this.size != other.size) {
                return false;
            }
            for (int i = 0; i < this.size; i++) {
                if (this.values[i] == null) {
                    if (other.values[i] != null) {
                        return false;
                    }
                } else {
                    if (!this.values[i].equals(other.values[i])) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "[]";
        }
        StringBuilder builder = new StringBuilder("[")
                .append(this.values[0]);
        for (int i = 1; i < this.size; i++) {
            builder.append(", ")
                    .append(this.values[i]);
        }
        return builder.append(']').toString();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ObjectDomain<T> clone() {
        return new ObjectDomain<>(this);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DomainIterator domainIterator() {
        return new DomainIterator() {
            private int index = -1;

            @Override
            public boolean hasNext() {
                return this.index < size - 1;
            }

            @Override
            public T next() {
                this.index++;
                if (this.index == size) {
                    throw new NoSuchElementException();
                }
                return (T) values[this.index];
            }

            @Override
            public void remove() {
                if (this.index == -1) {
                    throw new IllegalStateException("before first");
                }
                System.arraycopy(values, this.index + 1, values, this.index, size - this.index - 1);
                size--;
                values[size] = null;
                this.index--;
            }

            @Override
            public int currentIndex() {
                return this.index;
            }

            @Override
            public void reset() {
                this.index = -1;
            }
        };
    }
}
