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

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Solver class that uses an {@link Algorithm} and solves a CSP.
 * If {@linkplain Solver#solve()} is successful then
 * {@linkplain Solver#currentSolution()} will return the current
 * {@linkplain Solution}.
 *
 * @param <P> the csp problem class
 * @param <S> the csp solution class
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.2.1
 * @since 1.0
 */
public abstract class Solver<P extends AbstractEasyCSP, S extends Solution> implements Iterable<S> {

    private final Algorithm<P, S> algorithm;
    private long elapsedTime;
    private long solutionCount;

    protected Solver(Algorithm<P, S> a) {
        if (a == null) {
            throw new IllegalArgumentException("a: null");
        }
        this.algorithm = a;
        this.elapsedTime = 0;
        this.solutionCount = 0;
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
    public S currentSolution() {
        return this.algorithm.getSolution();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<S> iterator() {
        return new Iterator<S>() {
            @Override
            public boolean hasNext() {
                return solve();
            }

            @Override
            public S next() {
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
    public Iterator<S> iterator(long solveMaxTime) {
        return new Iterator<S>() {
            @Override
            public boolean hasNext() {
                return solveIn(solveMaxTime);
            }

            @Override
            public S next() {
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
    public Stream<S> stream() {
        return streamIterator(iterator());
    }

    private static <S> Stream<S> streamIterator(Iterator<S> i) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(i, Spliterator.ORDERED), false);
    }

    /**
     * Returns a stream of over the solutions found by this solver.
     * The stream is finite if the used {@linkplain Algorithm} is {@linkplain Algorithm.Exhaustive}.
     *
     * @param solveMaxTime max time to find each solution
     * @return a lazy stream of solutions
     */
    public Stream<S> stream(long solveMaxTime) {
        return streamIterator(iterator(solveMaxTime));
    }
}
