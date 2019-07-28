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
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/**
 * IntDomain implements {@link Domain} through a union of integer intervals.
 * This implementation cannot contain nulls.
 *
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.1.3
 * @since 1.0
 */
public final class IntDomain implements Domain<Integer> {

    private static final class Interval {

        private int lowerBound;
        private int upperBound;

        private Interval(int singleton) {
            this.lowerBound = singleton;
            this.upperBound = singleton;
        }

        private Interval(int lowerBound, int upperBound) {
            if (upperBound < lowerBound) {
                throw new IllegalArgumentException("upperBound: " + upperBound + " smaller than lowerBound " + lowerBound);
            }
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

        private Interval(Interval other) {
            this.lowerBound = other.lowerBound;
            this.upperBound = other.upperBound;
        }

        public int size() {
            if (this.lowerBound >= 0) {
                return this.upperBound - this.lowerBound + 1;
            }
            return -(this.lowerBound - this.upperBound) + 1;
        }

        public boolean contains(int value) {
            return this.lowerBound <= value && value <= this.upperBound;
        }

        public int get(int index) {
            return this.lowerBound + index;
        }

        public int indexOf(int value) {
            return this.contains(value) ? value - this.lowerBound : -1;
        }

        @Override
        protected Interval clone() {
            return new Interval(this);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || o.getClass() != this.getClass()) {
                return false;
            }
            final Interval other = (Interval) o;
            return this.lowerBound == other.lowerBound && this.upperBound == other.upperBound;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 11 * hash + this.lowerBound;
            hash = 11 * hash + this.upperBound;
            return hash;
        }

        @Override
        public String toString() {
            if (this.lowerBound == this.upperBound) {
                return "{" + this.lowerBound + '}';
            }
            return "[" + this.lowerBound + ".." + this.upperBound + ']';
        }
    }

    private static final int DEFAULT_COUNT = 5;

    private Interval[] intervals;
    private int intervalCount;
    private int size;

    /**
     * Creates a new empty instance.
     */
    public IntDomain() {
        this.intervals = new Interval[DEFAULT_COUNT];
        this.intervalCount = 0;
        this.size = 0;
    }

    /**
     * Creates a new instance with the given singleton value.
     *
     * @param singleton the single value
     */
    public IntDomain(int singleton) {
        this(new Interval(singleton));
    }

    private IntDomain(Interval initial) {
        this.intervals = new Interval[DEFAULT_COUNT];
        this.intervals[0] = initial;
        this.intervalCount = 1;
        this.size = initial.size();
    }

    /**
     * Creates a new instance with the given lower and upper bounds, including
     * the lower and upper bounds.
     *
     * @param lowerBound the lower bound of the domain
     * @param upperBound the upper bound of the domain
     */
    public IntDomain(int lowerBound, int upperBound) {
        this(new Interval(lowerBound, upperBound));
    }

    private IntDomain(IntDomain other) {
        this.intervals = new Interval[other.intervals.length];
        for (int i = 0; i < other.intervals.length; i++) {
            this.intervals[i] = other.intervals[i].clone();
        }
        this.intervalCount = other.intervalCount;
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
     * Returns the minimum value of this instance. If this instance is empty
     * then an exception is thrown.
     *
     * @return the minimum value
     * @throws NoSuchElementException if empty
     */
    public int min() {
        checkHasElements();
        return this.min(0);
    }

    private void checkHasElements() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
    }

    private int min(int interval) {
        return this.intervals[interval].lowerBound;
    }

    /**
     * Returns the maximum value of this instance. If this instance is empty
     * then an exception is thrown.
     *
     * @return the maximum value
     * @throws NoSuchElementException if empty
     */
    public int max() {
        checkHasElements();
        return this.max(this.intervalCount - 1);
    }

    private int max(int interval) {
        return this.intervals[interval].upperBound;
    }

    /**
     * Gets the int value at the given index.
     *
     * @param index of the value to get
     * @return the int at index
     */
    public int getInt(int index) {
        checkInRange(index);
        int offset = 0;
        int intervalIdx = 0;
        while (intervalIdx < this.intervalCount) {
            int relativeIndex = index - offset;
            int intervalSize = this.intervals[intervalIdx].size();
            if (relativeIndex < intervalSize) {
                return this.intervals[intervalIdx].get(relativeIndex);
            }
            offset += intervalSize;
            intervalIdx++;
        }
        throw new ConcurrentModificationException();
    }

    private void checkInRange(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + this.size);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Integer get(int index) {
        return getInt(index);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean contains(Integer value) {
        return contains(value.intValue());
    }

    /**
     * Returns true if this instance contains the given int value, false otherwise.
     *
     * @param value whose presence is to be tested
     * @return true if this instance contains the given int value, false otherwise
     */
    public boolean contains(int value) {
        return this.intervalIndexOfValue(value) >= 0;
    }

    private int intervalIndexOfValue(int value) {
        int interval = this.binarySearchInterval(value);
        return interval >= 0 && this.intervals[interval].contains(value) ? interval : -1;
    }

    private int binarySearchInterval(int value) {
        int left = 0;
        int right = intervalCount - 1;
        while (left < right) {
            int pivot = (left + right) / 2;
            if (this.max(pivot) < value) {
                left = pivot + 1;
            } else {
                right = pivot;
            }
        }
        return right;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int indexOf(Integer value) {
        return indexOf(value.intValue());
    }

    /**
     * Returns the index of the given int value in this instance.
     *
     * @param value whose index is searched
     * @return index of the given value, -1 if the int value is not contained in this instance
     */
    public int indexOf(int value) {
        if (this.isEmpty() || !isBetweenMinAndMax(value)) {
            return -1;
        }
        int offset = 0;
        int intervalIdx = 0;
        while (intervalIdx < this.intervalCount) {
            int relativeIdx = this.intervals[intervalIdx].indexOf(value);
            if (relativeIdx >= 0) {
                return offset + relativeIdx;
            }
            offset += this.intervals[intervalIdx].size();
            intervalIdx++;
        }
        return -1;
    }

    private boolean isBetweenMinAndMax(int value) {
        return this.min(0) <= value && value <= this.max(this.intervalCount - 1);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void add(Integer value) {
        addInt(value.intValue());
    }

    /**
     * Adds the given int value to this instance.
     *
     * @param value to be added
     */
    public void addInt(int value) {
        // binary search the potential interval and then start from right before it
        // in order to correctly apply the merge intervals logic (except if we insert a new min):
        int foundInterval = this.binarySearchInterval(value);
        int startInterval = foundInterval > 0 ? foundInterval - 1 : 0;
        for (int i = startInterval; i < this.intervalCount; i++) {
            Interval crt = this.intervals[i];
            // value is before the current interval:
            if (value < crt.lowerBound) {
                // value right before the current interval:
                if (value == crt.lowerBound - 1) {
                    // expand interval's lowerBound:
                    crt.lowerBound--;
                } else {
                    // add a new interval:
                    this.insertIntervalAt(new Interval(value), i);
                }
                this.size++;
                return;
            }
            // value is contained in the current interval:
            if (crt.contains(value)) {
                return;
            }
            // value is right after the current interval:
            if (value - 1 == crt.upperBound) {
                // expand the current interval's upperBound:
                crt.upperBound++;
                // if there is another interval right next to the current interval, merge them:
                int nexti = i + 1;
                if (nexti < this.intervalCount && crt.upperBound + 1 == this.intervals[nexti].lowerBound) {
                    crt.upperBound = this.intervals[nexti].upperBound;
                    deleteIntervalAt(nexti);
                }
                this.size++;
                return;
            }
        }
        // add max value as a new interval, possibly in empty domain:
        this.insertIntervalAt(new Interval(value), this.intervalCount);
        this.size++;
    }

    private void insertIntervalAt(Interval interval, int idx) {
        if (this.intervalCount == this.intervals.length) {
            Interval[] newIntervals = new Interval[(this.intervals.length * 3) / 2 + 1];
            System.arraycopy(this.intervals, 0, newIntervals, 0, idx);
            System.arraycopy(this.intervals, idx, newIntervals, idx + 1, this.intervalCount - idx);
            this.intervals = newIntervals;
        } else {
            System.arraycopy(this.intervals, idx, this.intervals, idx + 1, this.intervalCount - idx);
        }
        this.intervals[idx] = interval;
        this.intervalCount++;
    }

    private void deleteIntervalAt(int idx) {
        System.arraycopy(this.intervals, idx + 1, this.intervals, idx, this.intervalCount - idx - 1);
        this.intervals[this.intervalCount - 1] = null;
        this.intervalCount--;
    }

    /**
     * Adds all the int values of the given domain to this instance.
     */
    public void addAll(IntDomain domain) {
        IntDomainIterator iterator = domain.domainIterator();
        while (iterator.hasNext()) {
            addInt(iterator.nextInt());
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean remove(Integer value) {
        return removeInt(value.intValue());
    }

    /**
     * Removes the given int value from this instance and returns true. If the int
     * is not found then false is returned.
     *
     * @param value to be removed
     * @return true if removed, false otherwise
     */
    public boolean removeInt(int value) {
        int intervalIdx = this.intervalIndexOfValue(value);
        if (intervalIdx >= 0) {
            removeValue(value, intervalIdx);
            return true;
        }
        return false;
    }

    private RemoveValueIntervalImpact removeValue(int value, int intervalIdx) {
        this.size--;
        Interval found = this.intervals[intervalIdx];
        // if the interval contains only 1 value:
        if (found.size() == 1) {
            // remove the interval:
            this.deleteIntervalAt(intervalIdx);
            return RemoveValueIntervalImpact.DELETE;
        }
        if (found.lowerBound == value) {
            // value is the interval's lowerBound:
            found.lowerBound++;
            return RemoveValueIntervalImpact.SHRINK_LOWERBOUND;
        }
        if (found.upperBound == value) {
            // value is the interval's upperBound:
            found.upperBound--;
            return RemoveValueIntervalImpact.SHRINK_UPPERBOUND;
        }

        // split the interval in two:
        insertIntervalAt(new Interval(value + 1, found.upperBound), intervalIdx + 1);
        found.upperBound = value - 1;
        return RemoveValueIntervalImpact.INSERT;
    }

    private enum RemoveValueIntervalImpact {
        DELETE, SHRINK_LOWERBOUND, SHRINK_UPPERBOUND, INSERT;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Integer removeAt(int index) {
        checkInRange(index);
        int offset = 0;
        int intervalIdx = 0;
        while (intervalIdx < this.intervalCount) {
            int relativeIndex = index - offset;
            int intervalSize = this.intervals[intervalIdx].size();
            if (relativeIndex < intervalSize) {
                // remove here:
                int ret = this.intervals[intervalIdx].get(relativeIndex);
                removeValue(ret, intervalIdx);
                return ret;
            }
            offset += intervalSize;
            intervalIdx++;
        }
        throw new ConcurrentModificationException();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void clear() {
        Arrays.fill(this.intervals, 0, this.intervalCount, null);
        this.intervalCount = 0;
        this.size = 0;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int ret = 173 + this.size;
        for (int i = 0; i < this.intervalCount; i++) {
            ret = 5 * ret + this.intervals[i].hashCode();
        }
        return ret;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object o) {
        if (o != null && o.getClass() == this.getClass()) {
            IntDomain other = (IntDomain) o;
            if (this.size != other.size || this.intervalCount != other.intervalCount) {
                return false;
            }
            for (int i = 0; i < this.intervalCount; i++) {
                if (!this.intervals[i].equals(other.intervals[i])) {
                    return false;
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
        StringBuilder builder = new StringBuilder()
                .append(this.intervals[0]);
        for (int i = 1; i < this.intervalCount; i++) {
            builder.append('U')
                    .append(this.intervals[i]);
        }
        return builder.toString();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public IntDomain clone() {
        return new IntDomain(this);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public IntDomainIterator domainIterator() {
        return new IntDomainIterator() {
            private int index = -1;
            private int intervalIdx = -1;
            private int relativeIdx = -1;

            @Override
            public boolean hasNext() {
                return this.index < size - 1;
            }

            @Override
            public Integer next() {
                return nextInt();
            }

            @Override
            public int nextInt() {
                this.index++;
                if (this.index >= size) {
                    throw new NoSuchElementException();
                }
                this.relativeIdx++;
                if (this.intervalIdx == -1 || this.relativeIdx == intervals[this.intervalIdx].size()) {
                    this.intervalIdx++;
                    this.relativeIdx = 0;
                }
                return intervals[this.intervalIdx].get(this.relativeIdx);
            }

            @Override
            public void remove() {
                if (this.index < 0) {
                    throw new IllegalStateException("before first");
                }
                int crtValue = intervals[this.intervalIdx].get(this.relativeIdx);
                RemoveValueIntervalImpact impact = removeValue(crtValue, this.intervalIdx);
                if (impact == RemoveValueIntervalImpact.INSERT) {
                    // this.intervalIdx remains the same but we move on its last element:
                    this.relativeIdx = relativeIdxOnIntervalLastElement();
                } else if (impact == RemoveValueIntervalImpact.SHRINK_LOWERBOUND || impact == RemoveValueIntervalImpact.DELETE) {
                    this.intervalIdx--;
                    this.relativeIdx = relativeIdxOnIntervalLastElement();
                }
                this.index--;
            }

            private int relativeIdxOnIntervalLastElement() {
                return this.intervalIdx >= 0 ? intervals[this.intervalIdx].size() - 1 : -1;
            }

            @Override
            public int currentIndex() {
                return this.index;
            }

            @Override
            public void reset() {
                this.index = -1;
                this.intervalIdx = -1;
                this.relativeIdx = -1;
            }
        };
    }

    /**
     * IntDomainIterator extends the {@linkplain Domain.DomainIterator} interface
     * with int domain specific functionality.
     */
    public interface IntDomainIterator extends DomainIterator<Integer> {

        /**
         * Returns the next int element in the iteration.
         *
         * @return the next int in the iteration
         * @throws NoSuchElementException if the iteration has no more ints
         */
        int nextInt();
    }
}
