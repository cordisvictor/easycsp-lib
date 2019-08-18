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

import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

/**
 * IntVariable class extends {@linkplain Variable} for the specific case of {@linkplain IntDomain}s.
 *
 * @param <U> underlying object class
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.2.1
 * @since 1.2.0
 */
public final class IntVariable<U> extends Variable<U, Integer> {

    /**
     * Constant indicating binary relation.
     */
    public static final int RELATION_BINARY = 2;
    /**
     * Constant indicating ternary relation.
     */
    public static final int RELATION_TERNARY = 3;

    private final Relation relation;

    /**
     * Creates a new instance with the given id and domain. The underlying will be left null.
     *
     * @param id     of the new instance
     * @param domain of the new instance
     * @throws IllegalArgumentException if domain is null
     */
    public IntVariable(int id, IntDomain domain) {
        this(id, null, domain);
    }

    /**
     * Creates a new instance with the given id, underlying, and domain.
     *
     * @param id         of this instance
     * @param underlying object represented by this instance
     * @param domain     of this instance
     * @throws IllegalArgumentException if domain is null
     */
    public IntVariable(int id, U underlying, IntDomain domain) {
        super(id, underlying, domain);
        this.relation = null;
    }

    IntVariable(int id, int indexVar0, IntUnaryOperator operator) {
        super(id);
        this.relation = new BinaryRelation(indexVar0, operator);
    }

    IntVariable(int id, int indexVar0, int indexVar1, IntBinaryOperator operator) {
        super(id);
        this.relation = new TernaryRelation(indexVar0, indexVar1, operator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntDomain getDomain() {
        if (isAuxiliary()) {
            throw new IllegalStateException("Auxiliary variables don't have domains.");
        }
        return (IntDomain) super.getDomain();
    }

    /**
     * Returns true if this is an auxiliary/working variable.
     */
    public boolean isAuxiliary() {
        return relation != null;
    }

    /**
     * Returns true if this is a binary auxiliary variable, else false.
     */
    public boolean isBinaryAuxiliary() {
        return relation != null && relation.degree() == RELATION_BINARY;
    }

    /**
     * Returns true if this is a ternary auxiliary variable, else false.
     */
    public boolean isTernaryAuxiliary() {
        return relation != null && relation.degree() == RELATION_TERNARY;
    }

    /**
     * Returns the relation of this auxiliary variable, or null if this isn't an auxiliary variable.
     *
     * @return the binary or ternary relation or null
     */
    public Relation relation() {
        return relation;
    }

    public interface Relation {

        /**
         * Degree of the relation involving this aux variable and other variables.
         *
         * @return the degree
         */
        int degree();

        /**
         * Returns true if this relation involves the variable and the given index.
         *
         * @param varIdx index of the variable to test if involved in this relation
         * @return true if this relation involves the variable at the given index, false otherwise.
         */
        boolean involves(int varIdx);
    }

    /**
     * A binary relation, involving an aux variable and one other variable.
     */
    public static final class BinaryRelation implements Relation {
        private final int indexVar0;
        private final IntUnaryOperator operator;

        private BinaryRelation(int indexVar0, IntUnaryOperator operator) {
            this.indexVar0 = indexVar0;
            this.operator = operator;
        }

        /**
         * Gets the index of the 1st variable.
         */
        public int getIndexVar0() {
            return indexVar0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int degree() {
            return RELATION_BINARY;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean involves(int varIdx) {
            return indexVar0 == varIdx;
        }

        /**
         * Computes the needed aux variable value.
         */
        public int compute(int var0Value) {
            return operator.applyAsInt(var0Value);
        }
    }

    /**
     * A ternary relation, involving an aux variable and two other variables.
     */
    public static final class TernaryRelation implements Relation {
        private final int indexVar0;
        private final int indexVar1;
        private final IntBinaryOperator operator;

        private TernaryRelation(int indexVar0, int indexVar1, IntBinaryOperator operator) {
            this.indexVar0 = indexVar0;
            this.indexVar1 = indexVar1;
            this.operator = operator;
        }

        /**
         * Gets the index of the 1st variable.
         */
        public int getIndexVar0() {
            return indexVar0;
        }

        /**
         * Gets the index of the 2nd variable.
         */
        public int getIndexVar1() {
            return indexVar1;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int degree() {
            return RELATION_TERNARY;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean involves(int varIdx) {
            return indexVar0 == varIdx || indexVar1 == varIdx;
        }

        /**
         * Computes the needed aux variable value.
         */
        public int compute(int var0Value, int var1Value) {
            return operator.applyAsInt(var0Value, var1Value);
        }
    }
}
