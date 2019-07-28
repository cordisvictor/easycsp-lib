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

import net.sourceforge.easycsp.alg.ForwardChecking;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Solver class that uses an {@link Algorithm} for CSP solving. If
 * {@linkplain Solver#solve()} is successful then
 * {@linkplain Solver#currentSolution()} will return the current
 * {@linkplain Solution}.
 *
 * @param <U> variables underlying object class
 * @param <T> variables domain values class
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.2.0
 * @since 1.0
 */
public class Solver<U, T> implements Iterable<Solution<U, T>> {

    private final Algorithm<U, T> algorithm;
    private long elapsedTime;
    private long solutionCount;

    /**
     * Creates a new instance with the default algorithm.
     *
     * @param easyCSP the CSP to solve
     */
    public Solver(EasyCSP<U, T> easyCSP) {
        this(new ForwardChecking<>(easyCSP));
    }

    /**
     * Creates a new instance with the given algorithm.
     *
     * @param a the algorithm to be used for solving
     */
    public Solver(Algorithm<U, T> a) {
        if (a == null) {
            throw new IllegalArgumentException("a: null");
        }
        this.algorithm = a;
        this.elapsedTime = 0;
        this.solutionCount = 0;
    }

    /**
     * Returns the algorithm used by this solver.
     *
     * @return the algorithm
     */
    public <A extends Algorithm<U, T>> A getAlgorithm() {
        return (A) algorithm;
    }

    /**
     * Gets the elapsed time.
     *
     * @return the elapsed time for the algorithm run
     */
    public long getElapsedTime() {
        return this.elapsedTime;
    }

    /**
     * Gets the solution count.
     *
     * @return the solution count for the algorithm run
     */
    public long getSolutionCount() {
        return this.solutionCount;
    }

    /**
     * Solves the CSP by running the algorithm. If the algorithm is successful
     * then the elapsed time is increased with the algorithm's run time, the
     * solution count is incremented and true is returned.
     *
     * @return true if the algorithm is successful, false otherwise
     */
    public boolean solve() {
        long startTime = System.currentTimeMillis();
        this.algorithm.run();
        this.elapsedTime += System.currentTimeMillis() - startTime;
        if (this.algorithm.successful) {
            this.solutionCount++;
            return true;
        }
        return false;
    }

    /**
     * Solves the CSP by running the algorithm within the given time. If the
     * algorithm is successful then the elapsed time is increased with the
     * algorithm's run time, the solution count is incremented and true is
     * returned.
     *
     * @param time available to run the algorithm, 0 for unlimited
     * @return true if the algorithm is successful, false otherwise
     */
    public boolean solveIn(long time) {
        final Thread solver = new Thread(() -> {
            long startTime = System.currentTimeMillis();
            algorithm.run();
            elapsedTime += System.currentTimeMillis() - startTime;
        });
        try {
            solver.start();
            solver.join(time);
            this.algorithm.interrupt();
            if (this.algorithm.successful) {
                this.solutionCount++;
                return true;
            }
        } catch (InterruptedException ex) {
            this.algorithm.interrupt();
        }
        return false;
    }

    /**
     * Returns the current solution obtained by running the algorithm.
     *
     * @return the solution currently obtained by running the algorithm
     */
    public Solution<U, T> currentSolution() {
        return this.algorithm.getSolution();
    }

    /**
     * Resets the algorithm, elapsed time, and solution count.
     */
    public void reset() {
        this.algorithm.reset();
        this.elapsedTime = 0;
        this.solutionCount = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Solution<U, T>> iterator() {
        return new Iterator<Solution<U, T>>() {
            @Override
            public boolean hasNext() {
                return solve();
            }

            @Override
            public Solution<U, T> next() {
                return currentSolution();
            }
        };
    }

    /**
     * Same as {@linkplain #iterator()}, but one can also specify a maximum time
     * for finding a solution.
     *
     * @param solveMaxTime max time to find the next solution
     */
    public Iterator<Solution<U, T>> iterator(long solveMaxTime) {
        return new Iterator<Solution<U, T>>() {
            @Override
            public boolean hasNext() {
                return solveIn(solveMaxTime);
            }

            @Override
            public Solution<U, T> next() {
                return currentSolution();
            }
        };
    }

    /**
     * Returns a stream of over the solutions found by this solver.
     * The stream is finite if the used {@linkplain Algorithm} is {@linkplain Algorithm.Exhaustive}.
     *
     * @return a lazy stream of solutions
     */
    public Stream<Solution<U, T>> stream() {
        return streamIterator(iterator());
    }

    private static <U, T> Stream<Solution<U, T>> streamIterator(Iterator<Solution<U, T>> i) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(i, Spliterator.ORDERED), false);
    }

    /**
     * Returns a stream of over the solutions found by this solver.
     * The stream is finite if the used {@linkplain Algorithm} is {@linkplain Algorithm.Exhaustive}.
     *
     * @param solveMaxTime max time to find each solution
     * @return a lazy stream of solutions
     */
    public Stream<Solution<U, T>> stream(long solveMaxTime) {
        return streamIterator(iterator(solveMaxTime));
    }
}
