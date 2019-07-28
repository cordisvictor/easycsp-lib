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

import net.sourceforge.easycsp.AbstractEasyCSP;
import net.sourceforge.easycsp.Constraint;

/**
 * IntEasyCSP class that is used to define int-based CSPs.
 * This class holds the graph of a CSP(Z,D,C).
 *
 * @param <U> variables underlying object class
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.2.0
 * @since 1.2.0
 */
public final class IntEasyCSP<U> extends AbstractEasyCSP<U, Integer> {

    private final int originalVariableCount;

    IntEasyCSP(String name, int originalVariableCount, IntVariable<U>[] variables, Constraint<U, Integer>[] constraints) {
        super(name, variables, constraints);
        this.originalVariableCount = originalVariableCount;
    }

    /**
     * {@inheritDoc}
     */
    public IntVariable<U> variableAt(int index) {
        return (IntVariable<U>) super.variableAt(index);
    }

    /**
     * Gets the original count of variables, i.e. the variable count minus the auxiliary variables.
     */
    public int getOriginalVariableCount() {
        return originalVariableCount;
    }
}
