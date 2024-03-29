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
package net.sourceforge.easycsp.alg;

import net.sourceforge.easycsp.*;

import java.util.Random;

/**
 * ConflictMinimizing class is a stochastic {@link Algorithm}.
 * It seeks an optima, assigning all variables and
 * then trying to minimize the generated conflicts. ConflictMinimizing is
 * recommended for large size problems with possibly over-constrained CSPs which
 * allow only partial solutions.
 *
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.2.1
 * @since 1.0
 */
public final class ConflictMinimizing<U, T> extends Algorithm<EasyCSP<U, T>, Solution<U, T>> {

    private final boolean option;
    private int[] conflicts;
    private Random rand;
    private long iterationLimit;

    /**
     * Creates a new instance with the given source.
     * and option. This algorithm search for a total solution.
     *
     * @param source the CSP the new algorithm will run on
     */
    public static <U, T> ConflictMinimizing<U, T> searchGlobalOptimaOf(EasyCSP<U, T> source) {
        return new ConflictMinimizing(source, true);
    }

    /**
     * Creates a new instance with the given source.
     * and option. This algorithm search for a partial solution.
     *
     * @param source the CSP the new algorithm will run on
     */
    public static <U, T> ConflictMinimizing<U, T> searchLocalOptimaOf(EasyCSP<U, T> source) {
        return new ConflictMinimizing(source, false);
    }

    private ConflictMinimizing(EasyCSP<U, T> source, boolean option) {
        super(source, Solution::new);
        this.option = option;
        this.conflicts = new int[source.variableCount()];
        this.rand = new Random(System.currentTimeMillis());
        long allSizes = 0;
        final int variableCount = this.source.variableCount();
        for (int i = 0; i < variableCount; i++) {
            allSizes += this.source.variableAt(i).getDomain().size();
        }
        this.iterationLimit = 2 * variableCount * allSizes + 2 * this.source.constraintCount();
    }

    private boolean initConflicts() {
        boolean unchanged = true;
        for (int i = 0; i < this.conflicts.length; i++) {
            int count = this.source.countConflicts(this.solution, i);
            unchanged &= (this.conflicts[i] == count);
            this.conflicts[i] = count;
        }
        return unchanged;
    }

    private int nextVariable() {
        int index = -1, max = 0;
        for (int i = 0; i < this.conflicts.length; i++) {
            if (this.conflicts[i] > max) {
                max = this.conflicts[i];
                index = i;
            }
        }
        return index;
    }

    private void assignVariable(int index) {
        int min = Integer.MAX_VALUE;
        T minVal = null;
        for (T value : this.source.variableAt(index).getDomain()) {
            this.solution.assign(index, value);
            int count = this.source.countConflicts(this.solution, index);
            if (count < min) {
                min = count;
                minVal = value;
            }
        }
        this.solution.assign(index, minVal);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void run() {
        this.running = true;
        this.successful = false;
        // init assignments and conflicts:
        final int variableCount = this.source.variableCount();
        for (int i = 0; i < variableCount; i++) {
            Domain<T> iVarDomain = this.source.variableAt(i).getDomain();
            if (iVarDomain.isEmpty()) {
                this.running = false;
                return;
            }
            this.solution.assignFromDomain(i, this.rand.nextInt(iVarDomain.size()));
        }
        this.initConflicts();
        // minimize conflicts:
        if (this.option) { // search global optima for the given CSP:
            long iterationCount = 0;
            int vi;
            while (this.running && (vi = this.nextVariable()) != -1) {
                iterationCount++;
                if (iterationCount > this.iterationLimit) {
                    this.running = false;
                    return;
                }
                this.assignVariable(vi);
                while (this.initConflicts()) {
                    vi = this.rand.nextInt(variableCount);
                    Variable v = this.source.variableAt(vi);
                    this.solution.assignFromDomain(vi, this.rand.nextInt(v.getDomain().size()));
                }
            }
        } else { // search local optima for the given CSP:
            int vi;
            while (this.running && (vi = this.nextVariable()) != -1) {
                this.assignVariable(vi);
                if (this.initConflicts()) {
                    this.running = false;
                    return;
                }
            }
        }
        if (this.running) {
            this.successful = true;
        }
        this.running = false;
    }
}
