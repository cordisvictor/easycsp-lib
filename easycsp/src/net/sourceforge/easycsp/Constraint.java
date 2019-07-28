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
import java.util.function.Predicate;

/**
 * Constraint class represents a {@linkplain Predicate} between {@linkplain Variable}s.
 *
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.1.3
 * @since 1.0
 */
public final class Constraint<U, T> {

    /**
     * PartialSolution interface that defines the way to check which variable is assigned.
     *
     * @param <U> variable's underlying object class
     * @param <T> variable's domain values class
     * @author Cordis Victor ( cordis.victor at gmail.com)
     * @version 1.1.0
     * @since 1.1.0
     */
    public interface PartialSolution<U, T> {

        /**
         * Returns true if the variable at the given index is assigned.
         *
         * @param variableIndex of the variable
         * @return true if assigned, false otherwise
         */
        boolean isAssigned(int variableIndex);

        /**
         * Returns a view over the assignments of interest for the given constraint.
         *
         * @param c the constraint of interest
         * @return the assignments of interest for c
         */
        Assignments<U, T> assignments(Constraint c);
    }

    /**
     * Assignments interface that defines the way to check {@linkplain Variable} currently-assigned values.
     *
     * @param <U> variable's underlying object class
     * @param <T> variable's domain values class
     * @author Cordis Victor ( cordis.victor at gmail.com)
     * @version 1.1.0
     * @since 1.1.0
     */
    public interface Assignments<U, T> {

        /**
         * Returns the variable at the given formal index. The zero-based formal index must within the n-nary condition.
         *
         * @param formalIndex of the variable
         * @return the variable
         */
        Variable<U, T> variable(int formalIndex);

        /**
         * Returns the value of the variable at the given formal index. The zero-based formal index must within the n-nary condition.
         *
         * @param formalIndex of the variable
         * @return the value
         */
        T value(int formalIndex);
    }

    /**
     * Constant indicating unary degree of a constraint.
     */
    public static final int DEGREE_UNARY = 1;
    /**
     * Constant indicating binary degree of a constraint.
     */
    public static final int DEGREE_BINARY = 2;

    private final int id;
    final int[] variableIndexes;
    private final Predicate<Assignments<U, T>> condition;

    Constraint(int id, int[] variableIndexes, Predicate<Assignments<U, T>> condition) {
        this.id = id;
        this.variableIndexes = Arrays.copyOf(variableIndexes, variableIndexes.length);
        this.condition = condition;
    }

    /**
     * Gets the id of this instance.
     *
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets the variable index of a formal index.
     *
     * @param formalIndex the zero-based relative index of the variable
     * @return the variable index
     */
    public int getVariableIndexAt(int formalIndex) {
        return this.variableIndexes[formalIndex];
    }

    /**
     * Returns the degree of this instance.
     *
     * @return degree
     */
    public int degree() {
        return this.variableIndexes.length;
    }

    /**
     * Returns true if the given partial solution violates this instance.
     *
     * @param partialSolution to test for violations
     * @return true if violations, false otherwise
     */
    public boolean isViolated(PartialSolution partialSolution) {
        for (int i = 0; i < this.variableIndexes.length; i++) {
            if (!partialSolution.isAssigned(this.variableIndexes[i])) {
                return false;
            }
        }
        return !this.condition.test(partialSolution.assignments(this));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object o) {
        if (o != null && o.getClass() == this.getClass()) {
            return this.id == ((Constraint) o).id;
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return 445 + this.id;
    }

    @Override
    public String toString() {
        return new StringBuilder("C")
                .append(this.id)
                .append(Arrays.toString(this.variableIndexes))
                .append(':')
                .append(this.condition)
                .toString();
    }
}
