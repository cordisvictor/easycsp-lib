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
package net.sourceforge.easycsp.numeric;

import net.sourceforge.easycsp.IntDomain;
import net.sourceforge.easycsp.Variable;

import java.util.function.IntBinaryOperator;

/**
 * IntVariable class extends {@linkplain Variable} for the specific case of {@linkplain IntDomain}s.
 *
 * @param <U> underlying object class
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.2.0
 * @since 1.2.0
 */
public final class IntVariable<U> extends Variable<U, Integer> {

    private static final int UNSPECIFIED_VAR_IDX = -1;

    private final int indexVar0;
    private final int indexVar1;
    private final IntBinaryOperator operator;

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
        this.indexVar0 = UNSPECIFIED_VAR_IDX;
        this.indexVar1 = UNSPECIFIED_VAR_IDX;
        this.operator = null;
    }

    IntVariable(int id, IntDomain domain, int indexVar0, IntBinaryOperator operator) {
        this(id, domain, indexVar0, UNSPECIFIED_VAR_IDX, operator);
    }

    IntVariable(int id, IntDomain domain, int indexVar0, int indexVar1, IntBinaryOperator operator) {
        super(id, null, domain);
        this.indexVar0 = indexVar0;
        this.indexVar1 = indexVar1;
        this.operator = operator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntDomain getDomain() {
        return (IntDomain) super.getDomain();
    }

    /**
     * Gets the index of the 1st variable, if this is an auxiliary variable.
     */
    public int getIndexVar0() {
        return indexVar0;
    }

    /**
     * Gets the index of the 2nd variable, if this is an auxiliary ternary variable.
     */
    public int getIndexVar1() {
        return indexVar1;
    }

    /**
     * Returns true if this is an auxiliary/working variable.
     */
    public boolean isAuxiliary() {
        return indexVar0 != UNSPECIFIED_VAR_IDX;
    }

    /**
     * Returns true if this is a ternary auxiliary variable, else false,
     * meaning this is a binary auxiliary variable.
     */
    public boolean isTernary() {
        return indexVar1 != UNSPECIFIED_VAR_IDX;
    }

    /**
     * Computes the needed value, for ternary variables.
     */
    public int compute(int value0, int value1) {
        return operator.applyAsInt(value0, value1);
    }

    /**
     * Computes the needed value, for binary variables.
     */
    public int compute(int val0) {
        return compute(val0, 0);
    }
}
