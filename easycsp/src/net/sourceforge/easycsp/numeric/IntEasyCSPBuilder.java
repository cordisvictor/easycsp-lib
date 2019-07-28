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

import net.sourceforge.easycsp.AbstractEasyCSPBuilder;
import net.sourceforge.easycsp.Constraint;
import net.sourceforge.easycsp.Constraint.Assignments;
import net.sourceforge.easycsp.IntDomain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.IntBinaryOperator;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toCollection;

/**
 * IntEasyCSPBuilder class is used at creating and constraining IntEasyCSPs.
 *
 * @param <U> variables underlying object class
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.2.0
 * @since 1.2.0
 */
public final class IntEasyCSPBuilder<U> extends AbstractEasyCSPBuilder<U, Integer, IntVariable<U>> {

    /**
     * Creates a builder with the specified name, number of variables and the
     * specified domain, shared between variables.
     *
     * @param name         the name of the problem
     * @param varCount     the number of variables of the problem
     * @param sharedDomain the shared domain of the variables
     * @return the builder
     */
    public static <U> IntEasyCSPBuilder<U> of(String name, int varCount, IntDomain sharedDomain) {
        ArrayList<IntVariable<U>> variableList = IntStream.range(0, varCount)
                .mapToObj(i -> new IntVariable<U>(i, sharedDomain))
                .collect(toCollection(ArrayList::new));
        return new IntEasyCSPBuilder<>(name, variableList);
    }

    /**
     * Creates a builder with the specified name, and variables for the specified domains.
     *
     * @param name    the name of the problem
     * @param domains the domains for the variables
     * @return the builder
     */
    public static <U> IntEasyCSPBuilder<U> of(String name, IntDomain... domains) {
        ArrayList<IntVariable<U>> variables = new ArrayList<>(domains.length);
        for (int i = 0; i < domains.length; i++) {
            variables.add(new IntVariable<>(i, domains[i]));
        }
        return new IntEasyCSPBuilder<>(name, variables);
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
    public static <U> IntEasyCSPBuilder<U> of(String name, IntDomain sharedDomain, U... varData) {
        ArrayList<IntVariable<U>> variables = new ArrayList<>(varData.length);
        for (int i = 0; i < varData.length; i++) {
            variables.add(new IntVariable<>(i, varData[i], sharedDomain));
        }
        return new IntEasyCSPBuilder<>(name, variables);
    }

    /**
     * Creates a builder the given name and variables.
     *
     * @param name      the name of the problem
     * @param variables the variables of the problem
     * @return the builder
     */
    public static <U> IntEasyCSPBuilder<U> of(String name, IntVariable<U>... variables) {
        ArrayList<IntVariable<U>> variableList = Arrays.stream(variables).collect(toCollection(ArrayList::new));
        return new IntEasyCSPBuilder<>(name, variableList);
    }

    private final int originalVariableCount;
    private int variableIdSeed;
    private InfixConstraintInfo<U> previousInfixConstraintInfo;


    private IntEasyCSPBuilder(String name, ArrayList<IntVariable<U>> variables) {
        super(name, variables);
        this.originalVariableCount = variables.size();
        this.variableIdSeed = 0;
        this.previousInfixConstraintInfo = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntEasyCSPBuilder<U> constrain(Predicate<Assignments<U, Integer>> condition, int... indices) {
        super.constrain(condition, indices);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntEasyCSPBuilder<U> constrainEach(Predicate<Assignments<U, Integer>> unaryCondition) {
        super.constrainEach(unaryCondition);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntEasyCSPBuilder<U> constrainEach(Predicate<Assignments<U, Integer>> unaryCondition, int... indices) {
        super.constrainEach(unaryCondition, indices);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntEasyCSPBuilder<U> constrainEachInRange(Predicate<Assignments<U, Integer>> unaryCondition, int start, int end) {
        super.constrainEachInRange(unaryCondition, start, end);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntEasyCSPBuilder<U> constrainSequentially(Predicate<Assignments<U, Integer>> binaryCondition) {
        super.constrainSequentially(binaryCondition);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntEasyCSPBuilder<U> constrainSequentially(Predicate<Assignments<U, Integer>> binaryCondition, int... indices) {
        super.constrainSequentially(binaryCondition, indices);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntEasyCSPBuilder<U> constrainSequentiallyInRange(Predicate<Assignments<U, Integer>> binaryCondition, int start, int end) {
        super.constrainSequentiallyInRange(binaryCondition, start, end);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntEasyCSPBuilder<U> constrainEachTwo(Predicate<Assignments<U, Integer>> binaryCondition) {
        super.constrainEachTwo(binaryCondition);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntEasyCSPBuilder<U> constrainEachTwo(Predicate<Assignments<U, Integer>> binaryCondition, int... indices) {
        super.constrainEachTwo(binaryCondition, indices);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntEasyCSPBuilder<U> constrainEachTwoInRange(Predicate<Assignments<U, Integer>> binaryCondition, int start, int end) {
        super.constrainEachTwoInRange(binaryCondition, start, end);
        return this;
    }

    /**
     * Start a n-ary constraint expression.
     * The expression will be automatically split into smaller, more manageable constraints.
     * This is done by adding auxiliary variables, which are marked with negative ids.
     *
     * @param var0Index the index of the 1st variable in the expression
     */
    public LHSTerm constrainVar(int var0Index) {
        setPreviousInfixConstraint();
        return new TermImpl(var0Index);
    }

    private void setPreviousInfixConstraint() {
        if (this.previousInfixConstraintInfo != null) {
            constrain(this.previousInfixConstraintInfo.binaryCondition, this.previousInfixConstraintInfo.var0Index, this.variables.size() - 1);
            this.previousInfixConstraintInfo = null;
        }
    }

    /**
     * Creates the IntEasyCSP.
     *
     * @return a new int easy csp
     */
    public IntEasyCSP<U> build() {
        setPreviousInfixConstraint();
        return new IntEasyCSP<>(this.name, this.originalVariableCount, this.variables.toArray(new IntVariable[0]), this.constraints.toArray(new Constraint[0]));
    }

    public interface LHSTerm extends RHSTerm {

        @Override
        LHSTerm plus(int value);

        @Override
        LHSTerm plusVar(int var1Index);

        @Override
        LHSTerm minus(int value);

        @Override
        LHSTerm minusVar(int var1Index);

        @Override
        LHSTerm multipliedBy(int value);

        @Override
        LHSTerm multipliedByVar(int var1Index);

        @Override
        LHSTerm dividedBy(int value);

        @Override
        LHSTerm dividedByVar(int var1Index);

        @Override
        LHSTerm maxBy(int value);

        @Override
        LHSTerm maxByVar(int var1Index);

        @Override
        LHSTerm minBy(int value);

        @Override
        LHSTerm minByVar(int var1Index);

        @Override
        LHSTerm abs();

        void equals(int value);

        RHSTerm equalsVar(int var1Index);

        void notEquals(int value);

        RHSTerm notEqualsVar(int var1Index);

        void greaterThat(int value);

        RHSTerm greaterThatVar(int var1Index);

        void greaterOrEquals(int value);

        RHSTerm greaterOrEqualsVar(int var1Index);

        void lessThat(int value);

        RHSTerm lessThatVar(int var1Index);

        void lessOrEquals(int value);

        RHSTerm lessOrEqualsVar(int var1Index);
    }

    public interface RHSTerm {
        RHSTerm plus(int value);

        RHSTerm plusVar(int var1Index);

        RHSTerm minus(int value);

        RHSTerm minusVar(int var1Index);

        RHSTerm multipliedBy(int value);

        RHSTerm multipliedByVar(int var1Index);

        RHSTerm dividedBy(int value);

        RHSTerm dividedByVar(int var1Index);

        RHSTerm maxBy(int value);

        RHSTerm maxByVar(int var1Index);

        RHSTerm minBy(int value);

        RHSTerm minByVar(int var1Index);

        RHSTerm abs();
    }

    private final class TermImpl implements LHSTerm {

        private final int var0Index;

        private TermImpl(int var0Index) {
            this.var0Index = var0Index;
        }

        private int registerAuxVariable(IntVariable<U> auxVar) {
            variables.add(auxVar);
            return variables.size() - 1;
        }

        private int binaryAuxVariable(IntDomain domain, int var0Index, IntBinaryOperator operator) {
            return registerAuxVariable(new IntVariable<>(--variableIdSeed, domain, var0Index, operator));
        }

        private int ternaryAuxVariable(IntDomain domain, int var0Index, int var1Index, IntBinaryOperator operator) {
            return registerAuxVariable(new IntVariable<>(--variableIdSeed, domain, var0Index, var1Index, operator));
        }

        private void constrainUnary(Predicate<Assignments<U, Integer>> unary) {
            constrain(unary, var0Index);
        }

        private TermImpl constrainBinary(Predicate<Assignments<U, Integer>> binary, IntDomain domain, IntBinaryOperator operator) {
            int varAuxIndex = binaryAuxVariable(domain, var0Index, operator);
            constrain(binary, var0Index, varAuxIndex);
            return new TermImpl(varAuxIndex);
        }

        private TermImpl constrainTernary(Predicate<Assignments<U, Integer>> ternary, int var1Index, IntDomain domain, IntBinaryOperator operator) {
            int varAuxIndex = ternaryAuxVariable(domain, var0Index, var1Index, operator);
            constrain(ternary, var0Index, var1Index, varAuxIndex);
            return new TermImpl(varAuxIndex);
        }

        @Override
        public TermImpl plus(int value) {
            IntDomain var0Domain = variables.get(this.var0Index).getDomain();
            return constrainBinary(assignments -> assignments.value(0) + value == assignments.value(1),
                    new IntDomain(var0Domain.min() + value, var0Domain.max() + value),
                    (i0, i1) -> i0 + value);
        }

        @Override
        public TermImpl plusVar(int var1Index) {
            IntDomain var0Domain = variables.get(this.var0Index).getDomain();
            IntDomain var1Domain = variables.get(var1Index).getDomain();
            return constrainTernary(assignments -> assignments.value(0) + assignments.value(1) == assignments.value(2), var1Index,
                    new IntDomain(var0Domain.min() + var1Domain.min(), var0Domain.max() + var1Domain.max()),
                    (i0, i1) -> i0 + i1);
        }

        @Override
        public TermImpl minus(int value) {
            IntDomain var0Domain = variables.get(this.var0Index).getDomain();
            return constrainBinary(assignments -> assignments.value(0) - value == assignments.value(1),
                    new IntDomain(var0Domain.min() - value, var0Domain.max() - value),
                    (i0, i1) -> i0 - value);
        }

        @Override
        public TermImpl minusVar(int var1Index) {
            IntDomain var0Domain = variables.get(this.var0Index).getDomain();
            IntDomain var1Domain = variables.get(var1Index).getDomain();
            return constrainTernary(assignments -> assignments.value(0) - assignments.value(1) == assignments.value(2), var1Index,
                    new IntDomain(var0Domain.min() - var1Domain.max(), var0Domain.max() - var1Domain.min()),
                    (i0, i1) -> i0 - i1);
        }

        @Override
        public TermImpl multipliedBy(int value) {
            IntDomain var0Domain = variables.get(this.var0Index).getDomain();
            int minXval = var0Domain.min() * value;
            int maxXval = var0Domain.max() * value;
            IntDomain varAuxDomain = minXval < maxXval ? new IntDomain(minXval, maxXval) : new IntDomain(maxXval, minXval);
            return constrainBinary(assignments -> assignments.value(0) * value == assignments.value(1), varAuxDomain, (i0, i1) -> i0 * value);
        }

        @Override
        public TermImpl multipliedByVar(int var1Index) {
            IntDomain var0Domain = variables.get(this.var0Index).getDomain();
            IntDomain var1Domain = variables.get(var1Index).getDomain();
            int minXmin = var0Domain.min() * var1Domain.min();
            int minXmax = var0Domain.min() * var1Domain.max();
            int maxXmin = var0Domain.max() * var1Domain.min();
            int maxXmax = var0Domain.max() * var1Domain.max();
            return constrainTernary(assignments -> assignments.value(0) * assignments.value(1) == assignments.value(2), var1Index,
                    new IntDomain(Math.min(Math.min(minXmin, minXmax), Math.min(maxXmin, maxXmax)),
                            Math.max(Math.max(minXmin, minXmax), Math.max(maxXmin, maxXmax))),
                    (i0, i1) -> i0 * i1);
        }

        @Override
        public TermImpl dividedBy(int value) {
            IntDomain var0Domain = variables.get(this.var0Index).getDomain();
            int minDval = var0Domain.min() / value;
            int maxDval = var0Domain.max() / value;
            IntDomain varAuxDomain = minDval < maxDval ? new IntDomain(minDval, maxDval) : new IntDomain(maxDval, minDval);
            return constrainBinary(assignments -> assignments.value(0) / value == assignments.value(1), varAuxDomain, (i0, i1) -> i0 / value);
        }

        @Override
        public TermImpl dividedByVar(int var1Index) {
            IntDomain var0Domain = variables.get(this.var0Index).getDomain();
            IntDomain var1Domain = variables.get(var1Index).getDomain();
            int minDmin = var0Domain.min() / var1Domain.min();
            int minDmax = var0Domain.min() / var1Domain.max();
            int maxDmin = var0Domain.max() / var1Domain.min();
            int maxDmax = var0Domain.max() / var1Domain.max();
            return constrainTernary(assignments -> assignments.value(0) / assignments.value(1) == assignments.value(2), var1Index,
                    new IntDomain(Math.min(Math.min(minDmin, minDmax), Math.min(maxDmin, maxDmax)),
                            Math.max(Math.max(minDmin, minDmax), Math.max(maxDmin, maxDmax))),
                    (i0, i1) -> i0 / i1);
        }

        @Override
        public TermImpl maxBy(int value) {
            IntDomain var0Domain = variables.get(this.var0Index).getDomain();
            return constrainBinary(assignments -> Math.max(assignments.value(0), value) == assignments.value(1),
                    new IntDomain(Math.max(var0Domain.min(), value), Math.max(var0Domain.max(), value)),
                    (i0, i1) -> Math.max(i0, value));
        }

        @Override
        public TermImpl maxByVar(int var1Index) {
            IntDomain var0Domain = variables.get(this.var0Index).getDomain();
            IntDomain var1Domain = variables.get(var1Index).getDomain();
            return constrainTernary(assignments -> Math.max(assignments.value(0), assignments.value(1)) == assignments.value(2), var1Index,
                    new IntDomain(Math.max(var0Domain.min(), var1Domain.min()), Math.max(var0Domain.max(), var1Domain.max())),
                    (i0, i1) -> Math.max(i0, i1));
        }

        @Override
        public TermImpl minBy(int value) {
            IntDomain var0Domain = variables.get(this.var0Index).getDomain();
            return constrainBinary(assignments -> Math.min(assignments.value(0), value) == assignments.value(1),
                    new IntDomain(Math.min(var0Domain.min(), value), Math.min(var0Domain.max(), value)),
                    (i0, i1) -> Math.min(i0, value));
        }

        @Override
        public TermImpl minByVar(int var1Index) {
            IntDomain var0Domain = variables.get(this.var0Index).getDomain();
            IntDomain var1Domain = variables.get(var1Index).getDomain();
            return constrainTernary(assignments -> Math.min(assignments.value(0), assignments.value(1)) == assignments.value(2), var1Index,
                    new IntDomain(Math.min(var0Domain.min(), var1Domain.min()), Math.min(var0Domain.max(), var1Domain.max())),
                    (i0, i1) -> Math.min(i0, i1));
        }

        @Override
        public TermImpl abs() {
            IntDomain var0Domain = variables.get(this.var0Index).getDomain();
            int var0Min = var0Domain.min();
            int var0Max = var0Domain.max();
            IntDomain auxDomain = var0Min >= 0 ? new IntDomain(var0Min, var0Max) :
                    var0Max < 0 ? new IntDomain(-var0Max, -var0Min) :
                            new IntDomain(0, Math.max(-var0Min, var0Max));
            return constrainBinary(assignments -> Math.abs(assignments.value(0)) == assignments.value(1), auxDomain, (i0, i1) -> Math.abs(i0));
        }

        @Override
        public void equals(int value) {
            constrainUnary(assignments -> assignments.value(0) == value);
        }

        @Override
        public TermImpl equalsVar(int var1Index) {
            previousInfixConstraintInfo = new InfixConstraintInfo<>(assignments -> assignments.value(0) == assignments.value(1), this.var0Index);
            return new TermImpl(var1Index);
        }

        @Override
        public void notEquals(int value) {
            constrainUnary(assignments -> assignments.value(0) != value);
        }

        @Override
        public TermImpl notEqualsVar(int var1Index) {
            previousInfixConstraintInfo = new InfixConstraintInfo<>(assignments -> assignments.value(0) != assignments.value(1), this.var0Index);
            return new TermImpl(var1Index);
        }

        @Override
        public void greaterThat(int value) {
            constrainUnary(assignments -> assignments.value(0) > value);
        }

        @Override
        public TermImpl greaterThatVar(int var1Index) {
            previousInfixConstraintInfo = new InfixConstraintInfo<>(assignments -> assignments.value(0) > assignments.value(1), this.var0Index);
            return new TermImpl(var1Index);
        }

        @Override
        public void greaterOrEquals(int value) {
            constrainUnary(assignments -> assignments.value(0) >= value);
        }

        @Override
        public TermImpl greaterOrEqualsVar(int var1Index) {
            previousInfixConstraintInfo = new InfixConstraintInfo<>(assignments -> assignments.value(0) >= assignments.value(1), this.var0Index);
            return new TermImpl(var1Index);
        }

        @Override
        public void lessThat(int value) {
            constrainUnary(assignments -> assignments.value(0) < value);
        }

        @Override
        public TermImpl lessThatVar(int var1Index) {
            previousInfixConstraintInfo = new InfixConstraintInfo<>(assignments -> assignments.value(0) < assignments.value(1), this.var0Index);
            return new TermImpl(var1Index);
        }

        @Override
        public void lessOrEquals(int value) {
            constrainUnary(assignments -> assignments.value(0) <= value);
        }

        @Override
        public TermImpl lessOrEqualsVar(int var1Index) {
            previousInfixConstraintInfo = new InfixConstraintInfo<>(assignments -> assignments.value(0) <= assignments.value(1), this.var0Index);
            return new TermImpl(var1Index);
        }

        @Override
        public boolean equals(Object o) {
            throw new UnsupportedOperationException("only int operations are allowed");
        }

        @Override
        public int hashCode() {
            throw new UnsupportedOperationException("only int operations are allowed");
        }
    }

    private static final class InfixConstraintInfo<U> {
        private final Predicate<Assignments<U, Integer>> binaryCondition;
        private final int var0Index;

        public InfixConstraintInfo(Predicate<Assignments<U, Integer>> binaryCondition, int var0Index) {
            this.binaryCondition = binaryCondition;
            this.var0Index = var0Index;
        }
    }
}
