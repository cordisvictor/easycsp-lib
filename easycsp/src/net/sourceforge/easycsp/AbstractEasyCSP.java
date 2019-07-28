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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * AbstractEasyCSP class represents an abstract CSPs.
 * This class holds the graph of a CSP(Z,D,C).
 *
 * @param <U> variables underlying object class
 * @param <T> variables domain values class
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.2.0
 * @since 1.2.0
 */
public abstract class AbstractEasyCSP<U, T> implements Iterable<Constraint<U, T>> {

    protected final String name;
    protected final Variable<U, T>[] variables;
    protected final Constraint<U, T>[] constraints;
    protected final Constraint<U, T>[][] variableArcs;

    protected AbstractEasyCSP(String name, Variable<U, T>[] variables, Constraint<U, T>[] constraints) {
        this.name = name;
        this.variables = variables;
        this.constraints = constraints;
        this.variableArcs = indexVariableConstraints(variables, constraints);
    }

    private static Constraint[][] indexVariableConstraints(Variable[] variables, Constraint[] constraints) {
        List<Constraint>[] indexes = IntStream.range(0, variables.length)
                .mapToObj(i -> new ArrayList<>())
                .toArray(List[]::new);

        for (Constraint c : constraints) {
            for (int i = 0; i < c.variableIndexes.length; i++) {
                indexes[c.variableIndexes[i]].add(c);
            }
        }

        return Stream.of(indexes)
                .map(arcs -> arcs.toArray(new Constraint[0]))
                .toArray(Constraint[][]::new);
    }


    /**
     * Returns the name of this CSP.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the number of variables of this instance.
     *
     * @return number of variables
     */
    public int variableCount() {
        return this.variables.length;
    }

    /**
     * Returns the variable at the specified index of this instance.
     *
     * @param index the index of the variable
     * @return the variable at the given index
     */
    public Variable<U, T> variableAt(int index) {
        return this.variables[index];
    }

    /**
     * Returns the degree of the variable at the specified index,i.e. in how
     * many constraints is the variable involved.
     *
     * @param index index of the variable
     * @return the degree of the variable at the specified index
     */
    public int degreeOfVariableAt(int index) {
        return this.variableArcs[index].length;
    }

    /**
     * Returns the number of constraints of this instance.
     *
     * @return number of constraints
     */
    public int constraintCount() {
        return this.constraints.length;
    }

    /**
     * Returns true if the given solution satisfies this instance, i.e. if it is
     * complete and if does not contain any conflicts w.r.t. the constraints of
     * this instance.
     *
     * @param s the solution to test
     * @return true if the given solution is satisfactory, false otherwise
     */
    public boolean isSatisfied(Solution<U, T> s) {
        if (!s.isComplete()) {
            return false;
        }
        for (int i = 0; i < this.constraints.length; i++) {
            if (this.constraints[i].isViolated(s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the given solution has conflicts with this instance.
     *
     * @param s the solution to test
     * @return true if the given solution generates conflicts, false otherwise
     */
    public boolean hasConflicts(Solution<U, T> s) {
        for (int i = 0; i < this.constraints.length; i++) {
            if (this.constraints[i].isViolated(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the give solution has conflicts with the constraints
     * related to the variable at the given index of this instance.
     *
     * @param s             the solution to test
     * @param variableIndex of the variable
     * @return true if the give solution generates conflicts with the related
     * constraints, false otherwise
     */
    public boolean hasConflicts(Solution<U, T> s, int variableIndex) {
        for (int i = 0; i < this.variableArcs[variableIndex].length; i++) {
            if (this.variableArcs[variableIndex][i].isViolated(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Counts the number of conflicts generated by the given solution.
     *
     * @param s the solution to test
     * @return the number of conflicts generated by the given solution
     */
    public int countConflicts(Solution<U, T> s) {
        int count = 0;
        for (int i = 0; i < this.constraints.length; i++) {
            if (this.constraints[i].isViolated(s)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the number of conflicts generated by the given solution testing
     * only the constraints related to the variable at the specified index.
     *
     * @param s             the solution to test
     * @param variableIndex index of the variable
     * @return the number of conflicts generated by the given solution
     */
    public int countConflicts(Solution<U, T> s, int variableIndex) {
        int count = 0;
        for (int i = 0; i < this.variableArcs[variableIndex].length; i++) {
            if (this.variableArcs[variableIndex][i].isViolated(s)) {
                count++;
            }
        }
        return count;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Iterator<Constraint<U, T>> iterator() {
        return Arrays.asList(this.constraints).iterator();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        return new StringBuilder(this.name)
                .append(Arrays.toString(this.variables))
                .append(Arrays.toString(this.constraints))
                .toString();
    }
}
