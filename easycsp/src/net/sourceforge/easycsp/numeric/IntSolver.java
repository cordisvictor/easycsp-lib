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

import net.sourceforge.easycsp.Solver;
import net.sourceforge.easycsp.alg.numeric.IntForwardChecking;

/**
 * IntSolver class extends Solver for IntEasyCSPs.
 *
 * @param <U> variables underlying object class
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.2.0
 * @since 1.2.0
 */
public final class IntSolver<U> extends Solver<U, Integer> {

    /**
     * Creates a new instance with the default algorithm.
     *
     * @param intEasyCSP the int-based CSP to solve
     */
    public IntSolver(IntEasyCSP<U> intEasyCSP) {
        super(new IntForwardChecking<>(intEasyCSP));
    }
}
