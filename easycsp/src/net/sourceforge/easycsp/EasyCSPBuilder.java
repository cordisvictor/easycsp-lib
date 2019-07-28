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
 * EasyCSPBuilder class is used at creating and constraining EasyCSPs.
 *
 * @param <U> variables underlying object class
 * @param <T> variables domain values class
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.2.0
 * @since 1.2.0
 */
public final class EasyCSPBuilder<U, T> extends AbstractEasyCSPBuilder<U, T, Variable<U, T>> {

    /**
     * Creates a builder with the specified name, number of variables and the
     * specified domain, shared between variables.
     *
     * @param name         the name of the problem
     * @param varCount     the number of variables of the problem
     * @param sharedDomain the shared domain of the variables
     * @return the builder
     */
    public static <U, T> EasyCSPBuilder<U, T> of(String name, int varCount, Domain<T> sharedDomain) {
        ArrayList<Variable<U, T>> variables = new ArrayList<>(varCount);
        for (int i = 0; i < varCount; i++) {
            variables.add(new Variable<>(i, sharedDomain));
        }
        return new EasyCSPBuilder<>(name, variables);
    }

    /**
     * Creates a builder with the specified name, and variables for the specified domains.
     *
     * @param name    the name of the problem
     * @param domains the domains for the variables
     * @return the builder
     */
    public static <U, T> EasyCSPBuilder<U, T> of(String name, Domain<T>... domains) {
        ArrayList<Variable<U, T>> variables = new ArrayList<>(domains.length);
        for (int i = 0; i < domains.length; i++) {
            variables.add(new Variable<>(i, domains[i]));
        }
        return new EasyCSPBuilder<>(name, variables);
    }

    /**
     * Creates a builder with the given name, a variable for each data and the
     * specified domain, shared between variables.
     *
     * @param name         the name of the problem
     * @param sharedDomain the shared domain of the variables
     * @param varData      the data represented by the CSPs variables
     * @return the builder
     */
    public static <U, T> EasyCSPBuilder<U, T> of(String name, Domain<T> sharedDomain, U... varData) {
        ArrayList<Variable<U, T>> variables = new ArrayList<>(varData.length);
        for (int i = 0; i < varData.length; i++) {
            variables.add(new Variable<>(i, varData[i], sharedDomain));
        }
        return new EasyCSPBuilder<>(name, variables);
    }

    /**
     * Creates a builder the given name and variables.
     *
     * @param name      the name of the problem
     * @param variables the variables of the problem
     * @return the builder
     */
    public static <U, T> EasyCSPBuilder<U, T> of(String name, Variable<U, T>... variables) {
        ArrayList<Variable<U, T>> variableList = new ArrayList<>(variables.length);
        for (int i = 0; i < variables.length; i++) {
            variableList.add(variables[i]);
        }
        return new EasyCSPBuilder<>(name, variableList);
    }

    private EasyCSPBuilder(String name, ArrayList<Variable<U, T>> variables) {
        super(name, variables);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EasyCSPBuilder<U, T> constrain(Predicate<Assignments<U, T>> condition, int... indices) {
        super.constrain(condition, indices);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EasyCSPBuilder<U, T> constrainEach(Predicate<Assignments<U, T>> unaryCondition) {
        super.constrainEach(unaryCondition);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EasyCSPBuilder<U, T> constrainEach(Predicate<Assignments<U, T>> unaryCondition, int... indices) {
        super.constrainEach(unaryCondition, indices);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EasyCSPBuilder<U, T> constrainEachInRange(Predicate<Assignments<U, T>> unaryCondition, int start, int end) {
        super.constrainEachInRange(unaryCondition, start, end);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EasyCSPBuilder<U, T> constrainSequentially(Predicate<Assignments<U, T>> binaryCondition) {
        super.constrainSequentially(binaryCondition);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EasyCSPBuilder<U, T> constrainSequentially(Predicate<Assignments<U, T>> binaryCondition, int... indices) {
        super.constrainSequentially(binaryCondition, indices);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EasyCSPBuilder<U, T> constrainSequentiallyInRange(Predicate<Assignments<U, T>> binaryCondition, int start, int end) {
        super.constrainSequentiallyInRange(binaryCondition, start, end);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EasyCSPBuilder<U, T> constrainEachTwo(Predicate<Assignments<U, T>> binaryCondition) {
        super.constrainEachTwo(binaryCondition);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EasyCSPBuilder<U, T> constrainEachTwo(Predicate<Assignments<U, T>> binaryCondition, int... indices) {
        super.constrainEachTwo(binaryCondition, indices);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EasyCSPBuilder<U, T> constrainEachTwoInRange(Predicate<Assignments<U, T>> binaryCondition, int start, int end) {
        super.constrainEachTwoInRange(binaryCondition, start, end);
        return this;
    }

    /**
     * Creates the EasyCSP.
     *
     * @return a new easy csp
     */
    public EasyCSP<U, T> build() {
        return new EasyCSP<>(this.name, this.variables.toArray(new Variable[0]), this.constraints.toArray(new Constraint[0]));
    }
}
