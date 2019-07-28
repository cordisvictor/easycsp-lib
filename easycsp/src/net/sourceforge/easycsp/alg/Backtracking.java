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
import net.sourceforge.easycsp.Algorithm.Exhaustive;
import net.sourceforge.easycsp.Domain.DomainIterator;
import net.sourceforge.easycsp.EasyCSP;

/**
 * Backtracking class is an exhaustive {@link Algorithm}.
 * This algorithm seeks all solutions, building them starting from variables[0]
 * to variables[n-1], where n is the number of variables.
 *
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.1.0
 * @see Exhaustive
 * @since 1.0
 */
public final class Backtracking extends Algorithm implements Exhaustive {

    private DomainIterator[] domains;
    private int index;

    /**
     * Creates a new instance with the given constraint graph.
     *
     * @param source the constraint graph the new algorithm will run on
     */
    public Backtracking(EasyCSP source) {
        super(source);
        this.initComponents();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initComponents() {
        this.domains = new DomainIterator[this.source.variableCount()];
        for (int i = 0; i < this.domains.length; i++) {
            this.domains[i] = this.source.variableAt(i).getDomain().domainIterator();
        }
        this.index = 0;
    }

    /**
     * {@inheritDoc}
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
                        this.successful = true;
                        this.running = false;
                        return;
                    } else {
                        this.index++;
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
}
