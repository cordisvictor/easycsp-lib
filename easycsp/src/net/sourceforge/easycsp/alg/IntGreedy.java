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
import net.sourceforge.easycsp.IntEasyCSP;
import net.sourceforge.easycsp.IntSolution;

/**
 * IntGreedy class implements a variation of Greedy for int-based CSPs.
 * This algorithm builds the solution, starting from variables[0] to
 * variables[n-1], by assigning the variables with the domain value with the
 * highest score given by the heuristic function.
 *
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.2.1
 * @since 1.2.1
 */
public final class IntGreedy<U> extends Algorithm<IntEasyCSP<U>, IntSolution<U>> {

    private final Fitness heuristic;

    /**
     * Creates a new instance with the given constraint graph and heuristic. The
     * heuristic function implementation will receive a partial solution, the
     * index of the current variable that needs to be assigned and the domain
     * value that needs to be evaluated. The current variable will be assigned
     * the highest heuristic evaluated value.
     *
     * @param source    the constraint graph the new algorithm will run on
     * @param heuristic the heuristic function used by this algorithm
     * @throws IllegalArgumentException if <code>heuristic == null</code>
     */
    public IntGreedy(IntEasyCSP<U> source, Fitness<U, Integer> heuristic) {
        super(source, IntSolution::new);
        if (heuristic == null) {
            throw new IllegalArgumentException("heuristic: null");
        }
        this.heuristic = heuristic;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void run() {
        this.running = true;
        this.successful = false;
        final int originalVarCount = this.source.getOriginalVariableCount();
        for (int variableIndex = 0; variableIndex < originalVarCount; variableIndex++) {
            if (!this.running) {
                return; // safe stopping point.
            }
            double max = Double.NEGATIVE_INFINITY;
            Integer maxValue = null;
            for (Integer value : this.source.variableAt(variableIndex).getDomain()) {
                if (solution.assignAndCheck(variableIndex, value)) {
                    double eval = this.heuristic.compute(this.solution, variableIndex, max);
                    if (eval > max) {
                        max = eval;
                        maxValue = value;
                    }
                }
            }
            if (maxValue == null) {
                this.solution.unassign(variableIndex);
                this.running = false;
                return;
            }
            this.solution.assign(variableIndex, maxValue);
        }
        this.successful = true;
        this.running = false;
    }
}
