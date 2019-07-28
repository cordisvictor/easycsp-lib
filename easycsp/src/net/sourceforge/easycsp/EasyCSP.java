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

import net.sourceforge.easycsp.Domain.DomainIterator;

/**
 * EasyCSP class represents an object-based CSPs.
 * This class holds the graph of a CSP(Z,D,C) and offers {@link Domain} reduction
 * and {@linkplain Variable} ordering methods.
 *
 * <b>NOTE: do not share variable domains between CSPs when EasyCSP problem reduction is applied.</b>
 *
 * @param <U> variables underlying object class
 * @param <T> variables domain values class
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.2.0
 * @since 1.0
 */
public final class EasyCSP<U, T> extends AbstractEasyCSP<U, T> {

    EasyCSP(String name, Variable<U, T>[] variables, Constraint<U, T>[] constraints) {
        super(name, variables, constraints);
    }

    /**
     * Achieves node consistency by removing the values from variable domains
     * which are illegal w.r.t. the unary constraints of this instance. Unary
     * constraints are also removed from this instance as they become redundant.
     * If this instance is over-constrained then it remains unchanged.
     *
     * @throws OverconstrainedCSPException if this instance is over-constrained
     */
    public void achieveNodeConsistency() throws OverconstrainedCSPException {
        this.ensureNoSharedDomains();
        // mark illegal domain values:
        IntDomain[] illegals = this.markIllegalsForNode();
        // remove illegal domain values and all unary constraints:
        this.removeIllegalsAndUnaryConstraints(illegals);
    }

    private void ensureNoSharedDomains() {
        for (int i = 0; i < this.variables.length - 1; i++) {
            for (int j = i + 1; j < this.variables.length; j++) {
                if (this.variables[i].getDomain() == this.variables[j].getDomain()) {
                    throw new IllegalStateException("same domain instance for variables at: " + i + ", " + j);
                }
            }
        }
    }

    private IntDomain[] markIllegalsForNode() throws OverconstrainedCSPException {
        final IntDomain[] illegals = new IntDomain[this.variables.length];
        for (int i = 0; i < illegals.length; i++) {
            illegals[i] = new IntDomain();
        }
        final Solution workSolution = new Solution(this);
        for (Constraint c : this.constraints) {
            if (c.degree() == Constraint.DEGREE_UNARY) {
                final int cVIdx = c.variableIndexes[0];
                final Domain cVD = this.variables[cVIdx].getDomain();
                final DomainIterator cVDI = cVD.domainIterator();
                while (cVDI.hasNext()) {
                    final Object value = cVDI.next();
                    if (!illegals[cVIdx].contains(cVDI.currentIndex())) {
                        workSolution.assign(cVIdx, value);
                        if (c.isViolated(workSolution)) {
                            illegals[cVIdx].addInt(cVDI.currentIndex());
                        }
                    }
                }
                if (illegals[cVIdx].size() == cVD.size()) {
                    throw new OverconstrainedCSPException("variable at: " + cVIdx);
                }
            }
        }
        return illegals;
    }

    private void removeIllegalsAndUnaryConstraints(IntDomain[] illegals) {
        // remove illegal domain values:
        for (int i = 0; i < this.variables.length; i++) {
            final DomainIterator vDI = this.variables[i].getDomain().domainIterator();
            int vDICurrentIdx = -1;
            while (vDI.hasNext()) {
                vDI.next();
                vDICurrentIdx++;
                if (illegals[i].contains(vDICurrentIdx)) {
                    vDI.remove();
                }
            }
        }
    }

    /**
     * Achieves arc consistency by first achieving node consistency and then
     * removing illegal domain values w.r.t. the binary constraints of this
     * instance. If this instance is over-constrained then it remains unchanged.
     *
     * @throws OverconstrainedCSPException if this instance is over-constrained
     */
    public void achieveArcConsistency() throws OverconstrainedCSPException {
        this.ensureNoSharedDomains();
        // mark illegal domain values:
        final IntDomain[] illegals = this.markIllegalsForNode();

        final Solution workSolution = new Solution(this);
        for (Constraint c : this.constraints) {
            if (c.degree() == Constraint.DEGREE_BINARY) {
                this.markIllegalsForArc(workSolution, c, illegals[c.variableIndexes[0]], illegals[c.variableIndexes[1]]);
            }
        }
        // remove all illegal domain values and unary constraints:
        this.removeIllegalsAndUnaryConstraints(illegals);
    }

    private void markIllegalsForArc(Solution workSolution, Constraint binary, IntDomain illegals0, IntDomain illegals1)
            throws OverconstrainedCSPException {
        final Variable v0 = this.variables[binary.variableIndexes[0]];
        final DomainIterator iter0 = v0.getDomain().domainIterator();
        final Variable v1 = this.variables[binary.variableIndexes[1]];
        final DomainIterator iter1 = v1.getDomain().domainIterator();
        // mark domain of v0:
        while (iter0.hasNext()) {
            final Object value0 = iter0.next();
            if (!illegals0.contains(iter0.currentIndex())) {
                workSolution.assign(binary.variableIndexes[0], value0);
                boolean usedValue0 = false;
                while (iter1.hasNext()) {
                    final Object value1 = iter1.next();
                    if (!illegals1.contains(iter1.currentIndex())) {
                        workSolution.assign(binary.variableIndexes[1], value1);
                        if (!binary.isViolated(workSolution)) {
                            usedValue0 = true;
                            break;
                        }
                    }
                }
                iter1.reset();
                if (!usedValue0) {
                    illegals0.addInt(iter0.currentIndex());
                    if (illegals0.size() == v0.getDomain().size()) {
                        throw new OverconstrainedCSPException("variable at: " + binary.variableIndexes[0]);
                    }
                }
            }
        }
        iter0.reset();
        // mark domain of v1:
        while (iter1.hasNext()) {
            final Object value1 = iter1.next();
            if (!illegals1.contains(iter1.currentIndex())) {
                workSolution.assign(binary.variableIndexes[1], value1);
                boolean usedValue1 = false;
                while (iter0.hasNext()) {
                    final Object value0 = iter0.next();
                    if (!illegals0.contains(iter0.currentIndex())) {
                        workSolution.assign(binary.variableIndexes[0], value0);
                        if (!binary.isViolated(workSolution)) {
                            usedValue1 = true;
                            break;
                        }
                    }
                }
                iter0.reset();
                if (!usedValue1) {
                    illegals1.addInt(iter1.currentIndex());
                    if (illegals1.size() == v1.getDomain().size()) {
                        throw new OverconstrainedCSPException("variable at: " + binary.variableIndexes[1]);
                    }
                }
            }
        }
        iter1.reset();
    }

    /**
     * Achieves minimal width ordering of the variables of this EasyCSP.
     */
    public void achieveMinimalWidth() {
        this.descendingQuicksort(0, this.variables.length - 1);
    }

    private void descendingQuicksort(int lo, int hi) {
        int i = lo;
        int j = hi;
        int x = this.variableArcs[(lo + hi) / 2].length;
        do {
            while (this.variableArcs[i].length > x) {
                i++;
            }
            while (this.variableArcs[j].length < x) {
                j--;
            }
            if (i <= j) {
                if (this.variableArcs[i].length < this.variableArcs[j].length) {
                    this.swapVariables(i, j);
                }
                i++;
                j--;
            }
        } while (i <= j);
        if (lo < j) {
            descendingQuicksort(lo, j);
        }
        if (i < hi) {
            descendingQuicksort(i, hi);
        }
    }

    private void swapVariables(int i0, int i1) {
        // apply changes to constraint indexes:
        Constraint[] iList = this.variableArcs[i0];
        for (Constraint c : iList) {
            for (int i = 0; i < c.variableIndexes.length; i++) {
                if (c.variableIndexes[i] == i0) {
                    c.variableIndexes[i] = i1;
                }
            }
        }
        iList = this.variableArcs[i1];
        for (Constraint c : iList) {
            for (int i = 0; i < c.variableIndexes.length; i++) {
                if (c.variableIndexes[i] == i1) {
                    c.variableIndexes[i] = i0;
                }
            }
        }
        // swap variable arcs:
        Constraint[] auxArcList = this.variableArcs[i0];
        this.variableArcs[i0] = this.variableArcs[i1];
        this.variableArcs[i1] = auxArcList;
        // swap variables:
        Variable auxVar = this.variables[i0];
        this.variables[i0] = this.variables[i1];
        this.variables[i1] = auxVar;
    }
}
