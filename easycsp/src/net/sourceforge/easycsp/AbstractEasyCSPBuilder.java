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

import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * AbstractEasyCSPBuilder class is used at creating and constraining CSP.
 *
 * @param <U> variables underlying object class
 * @param <T> variables domain values class
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.2.0
 * @since 1.2.0
 */
public abstract class AbstractEasyCSPBuilder<U, T, V extends Variable<U, T>> {

    protected final String name;
    protected final ArrayList<V> variables;
    protected final ArrayList<Constraint> constraints;
    protected int constraintIdSeed;

    protected AbstractEasyCSPBuilder(String name, ArrayList<V> variables) {
        if (variables.isEmpty()) {
            throw new IllegalArgumentException("variables: empty");
        }
        this.name = name;
        this.variables = variables;
        this.constraints = new ArrayList<>();
        this.constraintIdSeed = 0;
    }

    /**
     * Adds a new n-nary constraint with the given condition on the variables at
     * the specified indices to this instance.
     *
     * @param condition condition of the constraint
     * @param indices   the indices of the variables
     */
    public AbstractEasyCSPBuilder<U, T, V> constrain(Predicate<Assignments<U, T>> condition, int... indices) {
        if (indices.length == 0) {
            throw new IllegalArgumentException("indices: empty");
        }
        this.constraints.add(new Constraint(++this.constraintIdSeed, indices, condition));
        return this;
    }

    /**
     * Constrains all variables of this CSP with the given condition.
     *
     * @param unaryCondition condition to constrain all variables with
     */
    public AbstractEasyCSPBuilder<U, T, V> constrainEach(Predicate<Assignments<U, T>> unaryCondition) {
        for (int i = 0; i < this.variables.size(); i++) {
            this.constrain(unaryCondition, i);
        }
        return this;
    }

    /**
     * Constrains all given variables of this CSP with the given condition.
     *
     * @param unaryCondition condition to constrain all variables with
     * @param indices        variables to constrain
     */
    public AbstractEasyCSPBuilder<U, T, V> constrainEach(Predicate<Assignments<U, T>> unaryCondition, int... indices) {
        for (int i = 0; i < indices.length; i++) {
            this.constrain(unaryCondition, indices[i]);
        }
        return this;
    }

    /**
     * Constrains all variables in the given range of this CSP with the given condition.
     *
     * @param unaryCondition condition to constrain all variables with
     * @param start          of range (inclusive)
     * @param end            of range (exclusive)
     */
    public AbstractEasyCSPBuilder<U, T, V> constrainEachInRange(Predicate<Assignments<U, T>> unaryCondition, int start, int end) {
        for (int i = start; i < end; i++) {
            this.constrain(unaryCondition, i);
        }
        return this;
    }

    /**
     * Constrains all variables of this CSP sequentially with the given condition.
     *
     * @param binaryCondition binary condition to constrain all variables with
     */
    public AbstractEasyCSPBuilder<U, T, V> constrainSequentially(Predicate<Assignments<U, T>> binaryCondition) {
        for (int i = 0; i < this.variables.size() - 1; i++) {
            this.constrain(binaryCondition, i, i + 1);
        }
        return this;
    }

    /**
     * Constrains all given variables of this CSP sequentially with the given condition.
     *
     * @param binaryCondition binary condition to constrain all variables with
     * @param indices         variables to constrain
     */
    public AbstractEasyCSPBuilder<U, T, V> constrainSequentially(Predicate<Assignments<U, T>> binaryCondition, int... indices) {
        for (int i = 0; i < indices.length - 1; i++) {
            this.constrain(binaryCondition, indices[i], indices[i + 1]);
        }
        return this;
    }

    /**
     * Constrains all variables in the given range of this CSP sequentially with the given condition.
     *
     * @param binaryCondition binary condition to constrain all variables with
     * @param start           of range (inclusive)
     * @param end             of range (exclusive)
     */
    public AbstractEasyCSPBuilder<U, T, V> constrainSequentiallyInRange(Predicate<Assignments<U, T>> binaryCondition, int start, int end) {
        for (int i = start; i < end - 1; i++) {
            this.constrain(binaryCondition, i, i + 1);
        }
        return this;
    }

    /**
     * Constrains each distinct pairs of variables of this CSP with the given condition.
     *
     * @param binaryCondition binary condition to constrain all variable pairs with
     */
    public AbstractEasyCSPBuilder<U, T, V> constrainEachTwo(Predicate<Assignments<U, T>> binaryCondition) {
        for (int i = 0; i < this.variables.size() - 1; i++) {
            for (int j = i + 1; j < this.variables.size(); j++) {
                this.constrain(binaryCondition, i, j);
            }
        }
        return this;
    }

    /**
     * Constrains each distinct pairs of the given variables of this CSP with the given condition.
     *
     * @param binaryCondition binary condition to constrain all variable pairs with
     * @param indices         variables to constrain
     */
    public AbstractEasyCSPBuilder<U, T, V> constrainEachTwo(Predicate<Assignments<U, T>> binaryCondition, int... indices) {
        for (int i = 0; i < indices.length - 1; i++) {
            for (int j = i + 1; j < indices.length; j++) {
                this.constrain(binaryCondition, indices[i], indices[j]);
            }
        }
        return this;
    }

    /**
     * Constrains each distinct pairs of the variables in the given range of this CSP with the given condition.
     *
     * @param binaryCondition binary condition to constrain all variable pairs with
     * @param start           of range (inclusive)
     * @param end             of range (exclusive)
     */
    public AbstractEasyCSPBuilder<U, T, V> constrainEachTwoInRange(Predicate<Assignments<U, T>> binaryCondition, int start, int end) {
        for (int i = start; i < end - 1; i++) {
            for (int j = i + 1; j < end; j++) {
                this.constrain(binaryCondition, i, j);
            }
        }
        return this;
    }
}
