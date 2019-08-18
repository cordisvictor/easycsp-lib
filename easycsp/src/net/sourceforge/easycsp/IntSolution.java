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

/**
 * IntSolution class holds {@linkplain IntVariable}-value assignments which represent int-based CSP solutions.
 *
 * @param <U> variables underlying object class
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.2.1
 * @since 1.2.1
 */
public final class IntSolution<U> extends Solution<U, Integer> {

    private final IntEasyCSP<U> source;

    public IntSolution(IntEasyCSP<U> sourceCSP) {
        super(sourceCSP);
        this.source = sourceCSP;
    }

    /**
     * Gets the original count of variables, i.e. the count of non-auxiliary variables.
     */
    public int originalVariableCount() {
        return this.source.getOriginalVariableCount();
    }

    /**
     * {@inheritDoc}
     */
    public IntVariable<U> variable(int index) {
        return this.source.variableAt(index);
    }

    /**
     * Cascade-assigns the variable at the given index with the given value and also computes and also assigns related auxiliary variables.
     *
     * @param variableIndex of the variable to assign
     * @param value         to assign
     */
    @Override
    public void assign(int variableIndex, Integer value) {
        tryCascadeAssign(variableIndex, value, false);
    }

    /**
     * Cascade-assigns the variable at the given index with the given value and also computes and assigns related auxiliary variables.
     * All assignments are tested for conflicts and the process stops if conflicts are found.
     *
     * @param variableIndex of the variable to assign
     * @param value         to assign
     * @return true if assignments completed, false if conflicts detected
     */
    public boolean assignAndCheck(int variableIndex, Integer value) {
        return tryCascadeAssign(variableIndex, value, true);
    }

    /**
     * Cascade-assigns the variable at the given index with the given value and also computes and assigns related auxiliary variables.
     * Only cascade assignments are tested for conflicts and the process stops if conflicts are found.
     *
     * @param variableIndex of the variable to assign
     * @param value         to assign
     * @return true if cascade-assignments completed, false if conflicts detected
     */
    public boolean assignAndCheckOnlyAuxiliary(int variableIndex, Integer value) {
        super.assign(variableIndex, value);
        return assignAuxiliariesOf(variableIndex, true);
    }

    private boolean tryCascadeAssign(int index, Integer value, boolean check) {
        super.assign(index, value);
        if (check && this.source.hasConflicts(this, index)) {
            return false;
        }
        return assignAuxiliariesOf(index, check);
    }

    private boolean assignAuxiliariesOf(int index, boolean check) {
        final int varCount = source.variableCount();
        for (int i = source.getOriginalVariableCount(); i < varCount; i++) {
            IntVariable iVar = source.variableAt(i);
            if (iVar.relation().involves(index)) {
                if (iVar.isTernaryAuxiliary()) {
                    IntVariable.TernaryRelation relation = (IntVariable.TernaryRelation) iVar.relation();
                    if (isAssigned(relation.getIndexVar0()) && isAssigned(relation.getIndexVar1())) {
                        super.assign(i, relation.compute(value(relation.getIndexVar0()), value(relation.getIndexVar1())));
                        if (check && this.source.hasConflicts(this, i)) {
                            return false;
                        }
                    }
                } else {
                    IntVariable.BinaryRelation relation = (IntVariable.BinaryRelation) iVar.relation();
                    super.assign(i, relation.compute(value(relation.getIndexVar0())));
                    if (check && this.source.hasConflicts(this, i)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Cascade-unassigns the variable at the given index and also unassigns related auxiliary variables.
     *
     * @param variableIndex of the variable to unassign
     */
    @Override
    public void unassign(int variableIndex) {
        super.unassign(variableIndex);
        unassignAuxiliariesOf(variableIndex);
    }

    private void unassignAuxiliariesOf(int index) {
        final int varCount = source.variableCount();
        for (int i = source.getOriginalVariableCount(); i < varCount; i++) {
            if (source.variableAt(i).relation().involves(index)) {
                super.unassign(i);
            }
        }
    }
}
