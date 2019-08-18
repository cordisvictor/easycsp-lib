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

import net.sourceforge.easycsp.Constraint.Assignments;
import net.sourceforge.easycsp.Constraint.PartialSolution;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Solution class holds {@linkplain Variable}-value assignments which represent CSP solutions.
 *
 * @param <U> variables underlying object class
 * @param <T> variables domain values class
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.2.1
 * @since 1.0
 */
public class Solution<U, T> implements PartialSolution, Iterable<T> {

    private static final Object UNASSIGNED = new Object() {
        @Override
        public String toString() {
            return "UNASSIGNED";
        }
    };

    private final AbstractEasyCSP<U, T> source;
    private final T[] values;
    private int assignedCount;
    private AssignmentsImpl assignmentsView;

    /**
     * Creates a new solution for the given <code>sourceCSP</code>,
     * with all the variables unassigned.
     *
     * @param sourceCSP of the solution
     */
    public Solution(AbstractEasyCSP<U, T> sourceCSP) {
        this.source = sourceCSP;
        this.values = (T[]) new Object[sourceCSP.variableCount()];
        Arrays.fill(this.values, UNASSIGNED);
        this.assignedCount = 0;
        this.assignmentsView = new AssignmentsImpl();
    }

    /**
     * Returns the number of assigned variables of this instance.
     *
     * @return the assigned count
     */
    public int getAssignedCount() {
        return this.assignedCount;
    }

    /**
     * Returns the size of this instance, i.e. the number of variables (assigned
     * or unassigned).
     *
     * @return size of this instance
     */
    public int size() {
        return this.values.length;
    }

    /**
     * Returns true if all variables of this instance are assigned.
     *
     * @return true if all variables are assigned, false otherwise
     */
    public boolean isComplete() {
        return this.assignedCount == this.values.length;
    }

    /**
     * Returns the variable at the given index.
     *
     * @param index of the variable
     * @return the variable
     */
    public Variable<U, T> variable(int index) {
        return this.source.variableAt(index);
    }

    /**
     * Returns the value of the variable at the given index.
     *
     * @param variableIndex of the variable
     * @return the value of the variable
     * @throws UnassignedVariableException if the variable is not assigned
     */
    public T value(int variableIndex) {
        if (isAssigned(variableIndex)) {
            return this.values[variableIndex];
        }
        throw new UnassignedVariableException("index: " + variableIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAssigned(int variableIndex) {
        return this.values[variableIndex] != UNASSIGNED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Assignments assignments(Constraint c) {
        this.assignmentsView.currentOfInterest = c;
        return this.assignmentsView;
    }

    /**
     * Assigns the variable at the given index with the given value.
     *
     * @param variableIndex of the variable to assign
     * @param value         to assign
     */
    public void assign(int variableIndex, T value) {
        if (!isAssigned(variableIndex)) {
            this.assignedCount++;
        }
        this.values[variableIndex] = value;
    }

    /**
     * Assigns the variable at the given index with the value at the specified
     * variable domain index.
     *
     * @param variableIndex    of the variable to assign
     * @param domainValueIndex of the variable domain value
     */
    public void assignFromDomain(int variableIndex, int domainValueIndex) {
        assign(variableIndex, this.source.variableAt(variableIndex).getDomain().get(domainValueIndex));
    }

    /**
     * Unassigns the variable at the given index.
     *
     * @param variableIndex of the variable to unassign
     */
    public void unassign(int variableIndex) {
        if (isAssigned(variableIndex)) {
            this.assignedCount--;
        }
        this.values[variableIndex] = (T) UNASSIGNED;
    }

    /**
     * Unassigns all variables of this instance.
     */
    public void clear() {
        Arrays.fill(this.values, UNASSIGNED);
        this.assignedCount = 0;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Iterator<T> iterator() {
        if (!this.isComplete()) {
            throw new IllegalStateException("not all variables are assigned");
        }
        return new Iterator() {
            private int index = -1;

            @Override
            public boolean hasNext() {
                return this.index < values.length - 1;
            }

            @Override
            public T next() {
                this.index++;
                if (this.index == values.length) {
                    throw new NoSuchElementException();
                }
                return values[this.index];
            }
        };
    }

    /**
     * Returns a stream of over the values of this solution.
     *
     * @return a lazy stream of objects
     */
    public Stream<T> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), false);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Solution<U, T> clone() {
        Solution<U, T> ret = new Solution<>(this.source);
        System.arraycopy(this.values, 0, ret.values, 0, this.values.length);
        ret.assignedCount = this.assignedCount;
        return ret;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Solution<?, ?> solution = (Solution<?, ?>) o;
        return assignedCount == solution.assignedCount &&
                Arrays.equals(values, solution.values);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int result = Objects.hash(assignedCount);
        result = 31 * result + Arrays.hashCode(values);
        return result;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        return toStringFirst(this.values.length);
    }

    /**
     * Used to string only the 1st <code>varCount</code> variables, when wanting to avoid printing auxiliary variables.
     */
    public String toStringFirst(int varCount) {
        StringBuilder sb = new StringBuilder("{ ");
        for (int i = 0; i < varCount; i++) {
            sb.append(this.values[i])
                    .append(' ');
        }
        return sb.append('}')
                .toString();
    }

    private final class AssignmentsImpl implements Assignments<U, T> {

        private Constraint currentOfInterest;

        @Override
        public Variable<U, T> variable(int formalIndex) {
            return source.variableAt(currentOfInterest.getVariableIndexAt(formalIndex));
        }

        @Override
        public T value(int formalIndex) {
            return values[currentOfInterest.getVariableIndexAt(formalIndex)];
        }
    }
}
