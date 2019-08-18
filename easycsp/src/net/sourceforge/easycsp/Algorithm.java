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

import java.util.function.Function;

/**
 * Algorithm class takes a csp as input and is used by a {@linkplain Solver} for solving that CSP.
 * <p>
 * This class is a {@linkplain Runnable}, in order to ease multi-threading scenario.
 * It also manages the running and successful flags.
 * If {@linkplain #interrupt()} is invoked on this instance then the run
 * method must stop in a safely manner.
 * <ul>
 * <li>NOTE:</li>
 * <li>This class is not thread-safe and cannot be shared between solvers</li>
 * <li>If the source CSP is modified then the algorithm must be reset-ed.</li>
 * <li>Algorithms used in parallel solving must not alter the CSP they run on.</li>
 * </ul>
 *
 * @param <P> the csp problem class
 * @param <S> the csp solution class
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.2.1
 * @since 1.0
 */
public abstract class Algorithm<P extends AbstractEasyCSP, S extends Solution> implements Runnable {

    /**
     * Exhaustive interface that should be implemented by
     * {@linkplain Algorithm}s which are exhaustive, i.e. not stochastic.
     * Exhaustive algorithms are stateful and must be able to find the
     * incremental solution based on it's current state. All solutions are found
     * by sequentially invoking the {@linkplain Algorithm#run()} method.
     * <p>
     * <li>NOTE: iteration will give all solutions if and only if the exhaustive
     * algorithm is in it's initial state</li>
     * </ul>
     *
     * @author Cordis Victor ( cordis.victor at gmail.com)
     * @version 1.1.0
     * @since 1.0
     */
    public interface Exhaustive {

        /**
         * Returns true if this instance is in it's final state, i.e. has been
         * run until the search space was exhausted, false otherwise.
         *
         * @return true if in final state, false otherwise
         */
        boolean inFinalState();
    }

    /**
     * Optimization interface that should be implemented by optimization
     * {@linkplain Algorithm}s.
     *
     * @author Cordis Victor ( cordis.victor at gmail.com)
     * @version 1.1.0
     * @since 1.0
     */
    public interface Optimization {

        /**
         * Returns true if this algorithm's objective is minimal evaluation.
         *
         * @return the objective specifying if minimal search
         */
        boolean isMinimize();

        /**
         * Returns true if this algorithm's objective is maximal evaluation.
         *
         * @return the objective specifying if maximal search
         */
        boolean isMaximize();

        /**
         * This method should return the evaluation of the current solution.
         *
         * @return the score of the current solution
         */
        double evaluation();
    }

    /**
     * IncrementalFitness interface used to define estimation and evaluation heuristics for CSOPs.
     *
     * @author Cordis Victor ( cordis.victor at gmail.com)
     * @version 1.1.0
     * @since 1.0
     */
    @FunctionalInterface
    public interface Fitness<U, T> {

        /**
         * Computes incrementally the score for the given partial solution.
         *
         * @param s             the solution
         * @param variableIndex the index of the currently assigned variable
         * @param score         the current fitness score
         * @return the score including the last variable assignment
         */
        double compute(Solution<U, T> s, int variableIndex, double score);
    }

    /**
     * The source CSP this instance runs on.
     */
    protected final P source;
    /**
     * The solution to be built.
     */
    protected final S solution;
    /**
     * The flag indicating if a solution is available.
     */
    protected boolean successful;
    /**
     * The flag indicating if this instance is running.
     */
    protected boolean running;

    protected Algorithm(P source, Function<P, S> createSolution) {
        this.source = source;
        this.solution = createSolution.apply(source);
        this.successful = false;
        this.running = false;
    }

    /**
     * Gets the solution that this algorithm produced.
     *
     * @return the solution containing the solution values
     */
    public S getSolution() {
        if (!this.successful) {
            throw new IllegalStateException("not successful");
        }
        return this.solution;
    }

    /**
     * Gets the successful flag.
     *
     * @return the successful flag indicating if a solution is available
     */
    public boolean isSuccessful() {
        return this.successful;
    }

    /**
     * Gets the running flag.
     *
     * @return the running flag indicating if this instance is running
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * Signals this instance to interrupt as soon as possible.
     */
    public void interrupt() {
        this.running = false;
    }
}
