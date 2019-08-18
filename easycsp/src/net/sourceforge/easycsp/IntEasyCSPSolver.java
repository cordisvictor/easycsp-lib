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

import net.sourceforge.easycsp.alg.IntForwardChecking;

/**
 * IntEasyCSPSolver class extends {@link Solver} for solving {@linkplain IntEasyCSP}s.
 *
 * @param <U> underlying object class
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.2.1
 * @since 1.2.1
 */
public final class IntEasyCSPSolver<U> extends Solver<IntEasyCSP<U>, IntSolution<U>> {

    /**
     * Creates an easy csp solver for the given <code>easyCsp</code>, using the default algorithm.
     *
     * @param easyCSP to solve
     */
    public IntEasyCSPSolver(IntEasyCSP<U> easyCSP) {
        super(new IntForwardChecking<>(easyCSP));
    }

    /**
     * Creates an easy csp solver using the given <code>algorithm</code>.
     *
     * @param algorithm to use
     */
    public IntEasyCSPSolver(Algorithm<IntEasyCSP<U>, IntSolution<U>> algorithm) {
        super(algorithm);
    }
}
