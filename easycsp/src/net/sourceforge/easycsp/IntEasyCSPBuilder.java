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
import java.util.Arrays;
import java.util.List;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * IntEasyCSPBuilder class is used at creating and constraining IntEasyCSPs.
 *
 * @param <U> variables underlying object class
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.2.1
 * @since 1.2.0
 */
public final class IntEasyCSPBuilder<U> extends AbstractEasyCSPBuilder<U, Integer, IntVariable<U>, IntEasyCSP<U>, IntEasyCSPBuilder<U>> {

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
        List<IntVariable<U>> variableList = IntStream.range(0, varCount)
                .mapToObj(i -> new IntVariable<U>(i, sharedDomain))
                .collect(toList());
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
        List<IntVariable<U>> variables = new ArrayList<>(domains.length);
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
        List<IntVariable<U>> variables = new ArrayList<>(varData.length);
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
        return new IntEasyCSPBuilder<>(name, Arrays.stream(variables).collect(toList()));
    }

    private final int originalVariableCount;
    private int variableIdSeed;
    private InfixConstraintInfo<U> previousInfixConstraintInfo;

    private IntEasyCSPBuilder(String name, List<IntVariable<U>> variables) {
        super(name, variables);
        this.originalVariableCount = variables.size();
        this.variableIdSeed = 0;
        this.previousInfixConstraintInfo = null;
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
     * {@inheritDoc}
     */
    @Override
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

        private int binaryAuxVariable(int var0Index, IntUnaryOperator operator) {
            return registerAuxVariable(new IntVariable<>(--variableIdSeed, var0Index, operator));
        }

        private int ternaryAuxVariable(int var0Index, int var1Index, IntBinaryOperator operator) {
            return registerAuxVariable(new IntVariable<>(--variableIdSeed, var0Index, var1Index, operator));
        }

        private void constrainUnary(Predicate<Assignments<U, Integer>> unary) {
            constrain(unary, var0Index);
        }

        private TermImpl constrainBinary(Predicate<Assignments<U, Integer>> binary, IntUnaryOperator operator) {
            int varAuxIndex = binaryAuxVariable(var0Index, operator);
            constrain(binary, var0Index, varAuxIndex);
            return new TermImpl(varAuxIndex);
        }

        private TermImpl constrainTernary(Predicate<Assignments<U, Integer>> ternary, int var1Index, IntBinaryOperator operator) {
            int varAuxIndex = ternaryAuxVariable(var0Index, var1Index, operator);
            constrain(ternary, var0Index, var1Index, varAuxIndex);
            return new TermImpl(varAuxIndex);
        }

        @Override
        public TermImpl plus(int value) {
            return constrainBinary(assignments -> assignments.value(0) + value == assignments.value(1),
                    i0 -> i0 + value);
        }

        @Override
        public TermImpl plusVar(int var1Index) {
            return constrainTernary(
                    assignments -> assignments.value(0) + assignments.value(1) == assignments.value(2), var1Index,
                    (i0, i1) -> i0 + i1);
        }

        @Override
        public TermImpl minus(int value) {
            return constrainBinary(
                    assignments -> assignments.value(0) - value == assignments.value(1),
                    i0 -> i0 - value);
        }

        @Override
        public TermImpl minusVar(int var1Index) {
            return constrainTernary(
                    assignments -> assignments.value(0) - assignments.value(1) == assignments.value(2), var1Index,
                    (i0, i1) -> i0 - i1);
        }

        @Override
        public TermImpl multipliedBy(int value) {
            return constrainBinary(
                    assignments -> assignments.value(0) * value == assignments.value(1),
                    i0 -> i0 * value);
        }

        @Override
        public TermImpl multipliedByVar(int var1Index) {
            return constrainTernary(
                    assignments -> assignments.value(0) * assignments.value(1) == assignments.value(2), var1Index,
                    (i0, i1) -> i0 * i1);
        }

        @Override
        public TermImpl dividedBy(int value) {
            return constrainBinary(
                    assignments -> assignments.value(0) / value == assignments.value(1),
                    i0 -> i0 / value);
        }

        @Override
        public TermImpl dividedByVar(int var1Index) {
            return constrainTernary(
                    assignments -> assignments.value(0) / assignments.value(1) == assignments.value(2), var1Index,
                    (i0, i1) -> i0 / i1);
        }

        @Override
        public TermImpl maxBy(int value) {
            return constrainBinary(
                    assignments -> Math.max(assignments.value(0), value) == assignments.value(1),
                    i0 -> Math.max(i0, value));
        }

        @Override
        public TermImpl maxByVar(int var1Index) {
            return constrainTernary(
                    assignments -> Math.max(assignments.value(0), assignments.value(1)) == assignments.value(2), var1Index,
                    (i0, i1) -> Math.max(i0, i1));
        }

        @Override
        public TermImpl minBy(int value) {
            return constrainBinary(
                    assignments -> Math.min(assignments.value(0), value) == assignments.value(1),
                    i0 -> Math.min(i0, value));
        }

        @Override
        public TermImpl minByVar(int var1Index) {
            return constrainTernary(
                    assignments -> Math.min(assignments.value(0), assignments.value(1)) == assignments.value(2), var1Index,
                    (i0, i1) -> Math.min(i0, i1));
        }

        @Override
        public TermImpl abs() {
            return constrainBinary(
                    assignments -> Math.abs(assignments.value(0)) == assignments.value(1),
                    i0 -> Math.abs(i0));
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
