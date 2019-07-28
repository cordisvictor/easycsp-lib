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
import net.sourceforge.easycsp.Algorithm.Exhaustive;
import net.sourceforge.easycsp.Domain.DomainIterator;

/**
 * ForwardChecking class is a exhaustive {@link Algorithm}.
 * This algorithm seeks all solutions, building them in the minimum variable heuristic order.
 *
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.1.3
 * @see Exhaustive
 * @since 1.0
 */
public final class ForwardChecking<U, T> extends Algorithm<U, T> implements Exhaustive {

    // backtracking components:
    private int[] stack;
    private int size;
    private DomainIterator<T>[] domains;
    // forward-checking components:
    private IntDomain[] removed;
    private IntDomain[][] undo;

    /**
     * Creates a new instance with the given constraint graph.
     *
     * @param source the constraint graph the new algorithm will run on
     */
    public ForwardChecking(EasyCSP<U, T> source) {
        super(source);
        this.initComponents();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void initComponents() {
        final int variableCount = this.source.variableCount();
        this.stack = new int[variableCount];
        this.size = -1;
        this.domains = new DomainIterator[variableCount];
        this.removed = new IntDomain[variableCount];
        this.undo = new IntDomain[variableCount][];
        for (int i = 0; i < variableCount; i++) {
            this.domains[i] = this.source.variableAt(i).getDomain().domainIterator();
            this.removed[i] = new IntDomain();
            this.undo[i] = new IntDomain[variableCount];
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void run() {
        this.running = true;
        this.successful = false;
        if (this.size == -1) {
            final int firstIndex = this.check0();
            if (firstIndex > -1) {
                this.stack[0] = firstIndex;
                this.size = 1;
            }
        }
        while (this.running && this.size > 0) {
            final int currentIndex = this.stack[this.size - 1];
            if (this.domains[currentIndex].hasNext()) {
                final T value = this.domains[currentIndex].next();
                if (!this.removed[currentIndex].contains(this.domains[currentIndex].currentIndex())) {
                    this.solution.assign(currentIndex, value);
                    if (this.size == this.domains.length) {
                        this.successful = true;
                        this.running = false;
                        return;
                    }
                    final int nextIndex = this.check(currentIndex);
                    if (nextIndex > -1) {
                        this.stack[this.size] = nextIndex;
                        this.size++;
                    } else {
                        this.undo(currentIndex);
                    }
                }
            } else {
                this.domains[currentIndex].reset();
                this.solution.unassign(currentIndex);
                this.size--;
                if (this.size > 0) {
                    this.undo(this.stack[this.size - 1]);
                }
            }
        }
        this.running = false;
    }

    private int check0() {
        // search min variable:
        int minVariable = 0;
        int minSize = this.source.variableAt(0).getDomain().size();
        for (int i = 1; i < this.domains.length; i++) {
            int iDomainSize = this.source.variableAt(i).getDomain().size();
            if (iDomainSize < minSize) {
                minSize = iDomainSize;
                minVariable = i;
            }
        }
        // remove illegal values w.r.t. unary constraints and refresh min variable:
        for (Constraint c : this.source) {
            if (c.degree() == Constraint.DEGREE_UNARY) {
                final int variableIndex = c.getVariableIndexAt(0);
                while (this.domains[variableIndex].hasNext()) {
                    final T value = this.domains[variableIndex].next();
                    if (!this.removed[variableIndex].contains(this.domains[variableIndex].currentIndex())) {
                        this.solution.assign(variableIndex, value);
                        if (c.isViolated(this.solution)) {
                            this.removed[variableIndex].addInt(this.domains[variableIndex].currentIndex());
                        }
                    }
                }
                this.domains[variableIndex].reset();
                this.solution.unassign(variableIndex);
                final int domainSize = this.source.variableAt(variableIndex).getDomain().size() - this.removed[variableIndex].size();
                if (domainSize == 0) {
                    return -1;
                }
                if (domainSize < minSize) {
                    minSize = domainSize;
                    minVariable = variableIndex;
                }
            }
        }
        return minVariable;
    }

    private int check(int index) {
        int minVariable = -1;
        int minSize = -1;
        for (int i = 0; i < this.domains.length; i++) {
            if (!this.solution.isAssigned(i)) {
                int j = 0;
                while (this.domains[i].hasNext()) {
                    final T value = this.domains[i].next();
                    if (!this.removed[i].contains(j)) {
                        this.solution.assign(i, value);
                        if (this.source.hasConflicts(this.solution, i)) {
                            this.removed[i].addInt(j);
                            this.markDomainIndexForUndo(i, index, j);
                        }
                    }
                    j++;
                }
                this.domains[i].reset();
                this.solution.unassign(i);
                final int domainSize = this.source.variableAt(i).getDomain().size() - this.removed[i].size();
                if (domainSize == 0) {
                    return -1;
                }
                if (minVariable == -1 || domainSize < minSize) {
                    minSize = domainSize;
                    minVariable = i;
                }
            }
        }
        return minVariable;
    }

    private void markDomainIndexForUndo(int var, int step, int domainValIdx) {
        if (this.undo[var][step] == null) {
            this.undo[var][step] = new IntDomain(domainValIdx);
        } else {
            this.undo[var][step].addInt(domainValIdx);
        }
    }

    private void undo(int index) {
        for (int i = 0; i < this.domains.length; i++) {
            if (!this.solution.isAssigned(i)) {
                if (this.undo[i][index] != null) {
                    IntDomain id = this.undo[i][index];
                    IntDomain.IntDomainIterator it = id.domainIterator();
                    while (it.hasNext()) {
                        this.removed[i].removeInt(it.nextInt());
                    }
                    id.clear();
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean inFinalState() {
        return this.size == 0;
    }
}
