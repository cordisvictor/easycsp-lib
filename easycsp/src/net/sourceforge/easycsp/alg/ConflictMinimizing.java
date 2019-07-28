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

import net.sourceforge.easycsp.Algorithm;
import net.sourceforge.easycsp.AbstractEasyCSP;
import net.sourceforge.easycsp.Variable;

import java.util.Random;

/**
 * ConflictMinimizing class is a stochastic {@link Algorithm}.
 * It seeks an optima, assigning all variables and
 * then trying to minimize the generated conflicts. ConflictMinimizing is
 * recommended for large size problems with possibly over-constrained CSPs which
 * allow only partial solutions.
 *
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.1.0
 * @since 1.0
 */
public final class ConflictMinimizing extends Algorithm {

    private final boolean option;
    private final int[] conflicts;
    private final Random rand;
    private long iterationLimit;

    /**
     * Creates a new instance with the given source.
     * and option. This algorithm search for a total solution.
     *
     * @param source the CSP the new algorithm will run on
     */
    public static ConflictMinimizing searchGlobalOptimaOf(AbstractEasyCSP source) {
        return new ConflictMinimizing(source, true);
    }

    /**
     * Creates a new instance with the given source.
     * and option. This algorithm search for a partial solution.
     *
     * @param source the CSP the new algorithm will run on
     */
    public static ConflictMinimizing searchLocalOptimaOf(AbstractEasyCSP source) {
        return new ConflictMinimizing(source, false);
    }

    private ConflictMinimizing(AbstractEasyCSP source, boolean option) {
        super(source);
        this.option = option;
        this.conflicts = new int[source.variableCount()];
        this.rand = new Random(System.currentTimeMillis());
        if (option) {
            this.initComponents();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void initComponents() {
        int totalSize = 0;
        final int variableCount = this.source.variableCount();
        for (int i = 0; i < variableCount; i++) {
            totalSize += this.source.variableAt(i).getDomain().size();
        }
        this.iterationLimit = 2 * variableCount * totalSize + 2 * this.source.constraintCount();
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
        Object minVal = null;
        for (Object value : this.source.variableAt(index).getDomain()) {
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
        // init position and conflicts:
        final int variableCount = this.source.variableCount();
        try {
            for (int i = 0; i < variableCount; i++) {
                this.solution.assign(i, this.rand.nextInt(this.source.variableAt(i).getDomain().size()));
            }
        } catch (IllegalArgumentException aDomainIsEmpty) {
            this.running = false;
            return;
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
                    this.solution.assign(vi, this.rand.nextInt(v.getDomain().size()));
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
