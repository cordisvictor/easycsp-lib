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
import net.sourceforge.easycsp.Algorithm.Exhaustive;
import net.sourceforge.easycsp.Constraint;
import net.sourceforge.easycsp.IntDomain;
import net.sourceforge.easycsp.IntDomain.IntDomainIterator;
import net.sourceforge.easycsp.IntEasyCSP;
import net.sourceforge.easycsp.IntSolution;
import net.sourceforge.easycsp.IntVariable;

/**
 * IntForwardChecking class implements a variation of ForwardChecking for int-based CSPs.
 * This algorithm seeks all solutions, building them in the minimum variable heuristic order.
 *
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.2.1
 * @see Exhaustive
 * @since 1.2.0
 */
public final class IntForwardChecking<U> extends Algorithm<IntEasyCSP<U>, IntSolution<U>> implements Exhaustive {

    // backtracking components:
    private int[] stack;
    private int size;
    private IntDomainIterator[] domains;
    // forward-checking components:
    private IntDomain[] removed;
    private IntDomain[][] undo;

    /**
     * Creates a new instance with the given constraint graph.
     *
     * @param source the constraint graph the new algorithm will run on
     */
    public IntForwardChecking(IntEasyCSP<U> source) {
        super(source, IntSolution::new);
        final int originalVariableCount = this.source.getOriginalVariableCount();
        this.stack = new int[originalVariableCount];
        this.size = -1;
        this.domains = new IntDomainIterator[originalVariableCount];
        this.removed = new IntDomain[originalVariableCount];
        this.undo = new IntDomain[originalVariableCount][];
        for (int i = 0; i < originalVariableCount; i++) {
            this.domains[i] = this.source.variableAt(i).getDomain().domainIterator();
            this.removed[i] = new IntDomain();
            this.undo[i] = new IntDomain[originalVariableCount];
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
                final int value = this.domains[currentIndex].nextInt();
                if (!this.removed[currentIndex].contains(this.domains[currentIndex].currentIndex())) {
                    if (this.solution.assignAndCheckOnlyAuxiliary(currentIndex, value)) {
                        if (this.solution.isComplete()) {
                            this.successful = true;
                            this.running = false;
                            return;
                        }
                        final int nextIndex = this.check(currentIndex);
                        if (nextIndex > -1) {
                            this.stack[this.size] = nextIndex;
                            this.size++;
                        } else {
                            this.undoDomainRemoves(currentIndex);
                        }
                    } else {
                        this.solution.unassign(currentIndex);
                    }
                }
            } else {
                this.solution.unassign(currentIndex);
                this.domains[currentIndex].reset();
                this.size--;
                if (this.size > 0) {
                    this.undoDomainRemoves(this.stack[this.size - 1]);
                }
            }
        }
        this.running = false;
    }

    private int check0() {
        // search min variable:
        int minVariable = 0;
        int minSize = this.source.variableAt(minVariable).getDomain().size();
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
                final IntVariable cVar = this.source.variableAt(variableIndex);
                if (!cVar.isAuxiliary()) {
                    while (this.domains[variableIndex].hasNext()) {
                        final int value = this.domains[variableIndex].nextInt();
                        if (!this.removed[variableIndex].contains(this.domains[variableIndex].currentIndex())) {
                            this.solution.assign(variableIndex, value);
                            if (c.isViolated(this.solution)) {
                                this.removed[variableIndex].addInt(this.domains[variableIndex].currentIndex());
                            }
                        }
                    }
                    this.solution.unassign(variableIndex);
                    this.domains[variableIndex].reset();
                    final int domainSize = cVar.getDomain().size() - this.removed[variableIndex].size();
                    if (domainSize == 0) {
                        return -1;
                    }
                    if (domainSize < minSize) {
                        minSize = domainSize;
                        minVariable = variableIndex;
                    }
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
                IntVariable iVar = this.source.variableAt(i);
                int j = 0;
                while (this.domains[i].hasNext()) {
                    final int value = this.domains[i].nextInt();
                    if (!this.removed[i].contains(j)) {
                        if (!this.solution.assignAndCheck(i, value)) {
                            this.removed[i].addInt(j);
                            this.markDomainIndexForUndo(i, index, j);
                        }
                        this.solution.unassign(i);
                    }
                    j++;
                }
                this.solution.unassign(i);
                this.domains[i].reset();
                final int iVarDomainSize = iVar.getDomain().size() - this.removed[i].size();
                if (iVarDomainSize == 0) {
                    return -1;
                }
                if (minVariable == -1 || iVarDomainSize < minSize) {
                    minSize = iVarDomainSize;
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

    private void undoDomainRemoves(int index) {
        for (int i = 0; i < this.domains.length; i++) {
            if (!this.solution.isAssigned(i)) {
                if (this.undo[i][index] != null) {
                    IntDomain id = this.undo[i][index];
                    IntDomainIterator it = id.domainIterator();
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
