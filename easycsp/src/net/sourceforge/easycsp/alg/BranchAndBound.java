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

import net.sourceforge.easycsp.AbstractEasyCSP;
import net.sourceforge.easycsp.Algorithm;
import net.sourceforge.easycsp.Algorithm.Exhaustive;
import net.sourceforge.easycsp.Algorithm.Optimization;
import net.sourceforge.easycsp.Domain.DomainIterator;

/**
 * BranchAndBound class is an exhaustive {@link Algorithm}.
 * This algorithm seeks all optimal solutions, building them
 * starting from variables[0] to variables[n-1], where n is the number of
 * variables. BranchAndBound will seek minimal or maximal score solutions
 * depending on the <code>option</code> value. The option constants are located
 * in the {@linkplain Optimization} interface.
 *
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.1.0
 * @see Exhaustive
 * @see Optimization
 * @since 1.0
 */
public final class BranchAndBound<U, T> extends Algorithm<U, T> implements Exhaustive, Optimization {

    // backtracking components:
    private DomainIterator<T>[] domains;
    private int index;
    // solution score components:
    private final Fitness<U, T> estimation;
    private final Fitness<U, T> evaluation;
    private final byte option;
    private double[] scoreStack;
    private double bestScore;

    /**
     * Creates a new instance with the given minimization CSP and fitness functions.
     * The estimation function will receive partial solutions while
     * the evaluation function will receive solutions.
     *
     * @param source     the CSP the algorithm will run on
     * @param estimation the function used to estimate a partial solution
     * @param evaluation the function used to evaluate a solution
     */
    public static <U, T> BranchAndBound<U, T> minimizationOf(AbstractEasyCSP<U, T> source, Fitness<U, T> estimation, Fitness<U, T> evaluation) {
        return new BranchAndBound(source, false, estimation, evaluation);
    }

    /**
     * Creates a new instance with the given maximization CSP and fitness functions.
     * The estimation function will receive partial solutions while
     * the evaluation function will receive solutions.
     *
     * @param source     the CSP the algorithm will run on
     * @param estimation the function used to estimate a partial solution
     * @param evaluation the function used to evaluate a solution
     */
    public static <U, T> BranchAndBound<U, T> maximizationOf(AbstractEasyCSP<U, T> source, Fitness<U, T> estimation, Fitness<U, T> evaluation) {
        return new BranchAndBound(source, true, estimation, evaluation);
    }

    private BranchAndBound(AbstractEasyCSP<U, T> source, boolean option, Fitness<U, T> estimation, Fitness<U, T> evaluation) {
        super(source);
        if (estimation == null) {
            throw new IllegalArgumentException("estimation: null");
        }
        if (evaluation == null) {
            throw new IllegalArgumentException("evaluation: null");
        }
        this.option = (byte) (option ? 1 : -1);
        this.estimation = estimation;
        this.evaluation = evaluation;
        this.initComponents();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void initComponents() {
        this.index = 0;
        this.domains = new DomainIterator[this.source.variableCount()];
        for (int i = 0; i < this.domains.length; i++) {
            this.domains[i] = this.source.variableAt(i).getDomain().domainIterator();
        }
        this.scoreStack = new double[this.source.variableCount()];
        this.bestScore = Double.NEGATIVE_INFINITY;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void run() {
        this.running = true;
        this.successful = false;
        while (this.running && this.index > -1) {
            if (this.domains[this.index].hasNext()) {
                this.solution.assign(this.index, this.domains[this.index].next());
                if (!this.source.hasConflicts(this.solution, this.index)) {
                    if (this.index == this.domains.length - 1) {
                        double eval = this.option * this.evaluation.compute(this.solution, this.index, this.scoreStack[this.index]);
                        if (eval > this.bestScore) {
                            this.bestScore = eval;
                            this.successful = true;
                            this.running = false;
                            return;
                        }
                    } else {
                        double esti = this.estimation.compute(this.solution, this.index, this.scoreStack[this.index]);
                        if ((this.option * esti) > this.bestScore) {
                            this.scoreStack[this.index + 1] = esti;
                            this.index++;
                        }
                    }
                }
            } else {
                this.domains[this.index].reset();
                this.solution.unassign(this.index);
                this.index--;
            }
        }
        this.running = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean inFinalState() {
        return this.index == -1;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isMinimize() {
        return this.option != 1;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isMaximize() {
        return this.option == 1;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double evaluation() {
        return this.option * this.bestScore;
    }
}
