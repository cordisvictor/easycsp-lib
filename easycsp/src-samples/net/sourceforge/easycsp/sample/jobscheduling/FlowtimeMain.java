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
package net.sourceforge.easycsp.sample.jobscheduling;

import net.sourceforge.easycsp.*;
import net.sourceforge.easycsp.Algorithm.Fitness;
import net.sourceforge.easycsp.alg.BranchAndBound;

import static net.sourceforge.easycsp.Constraints.notEqual;

public class FlowtimeMain {

    /**
     * Flowtime main method solves the formal JobScheduling CSOP(Z,D,C):
     * 5 Machines and 5 Jobs (for assigning more than one Job per Machine, the
     * domain must contain arrays of Jobs).
     */
    public static void main(String[] args) {
        // create CSP(Z,D,C):
        EasyCSP flowtime = EasyCSPBuilder.of("Flowtime",
                new ObjectDomain<>(
                        new Job(0, 3),
                        new Job(1, 6),
                        new Job(2, 5),
                        new Job(3, 10),
                        new Job(4, 9)),
                new Machine[]{
                        new Machine(0, 3),
                        new Machine(1, 12),
                        new Machine(2, 8),
                        new Machine(3, 6),
                        new Machine(4, 7)
                })
                .constrainEachTwo(notEqual())
                .build();

        // solve:
        BranchAndBound alg = BranchAndBound.minimizationOf(flowtime, FlowtimeMain::estimate, FlowtimeMain::evaluate);
        Solver solver = new EasyCSPSolver(alg);
        while (solver.solve()) {
            System.out.println(solver.getSolutionCount() + " " + solver.currentSolution() + ", Flowtime= " + alg.evaluation());
        }
        System.out.println(solver.getSolutionCount() + " optimal solution(s) in " + solver.getElapsedTime() / 1000.00 + " seconds");
    }

    private static double estimate(Solution<Machine, Job> s, int idx, double score) {
        if (idx == 0) {
            double totalEstim = s.value(0).getOperationCount() / s.variable(0).get().getExecutionSpeed();
            for (int i = 1; i < s.size(); i++) {
                // estimate the best case: the job with smallest OperationCount / Machine ExecutionSpeed:
                Variable<Machine, Job> v = s.variable(i);
                totalEstim += v.getDomain().get(0).getOperationCount() / v.get().getExecutionSpeed();
            }
            return totalEstim;
        } else {
            Variable<Machine, Job> v = s.variable(idx);
            // undo last estimation:
            double time = score - (v.getDomain().get(0).getOperationCount() / v.get().getExecutionSpeed());
            // add the last element:
            return time + (s.value(idx).getOperationCount() / v.get().getExecutionSpeed());
        }
    }

    private static double evaluate(Solution<Machine, Job> s, int idx, double score) {
        Variable<Machine, Job> v = s.variable(idx);
        // undo last estimation:
        double time = score - (v.getDomain().get(0).getOperationCount() / v.get().getExecutionSpeed());
        // add the last element:
        return time + (s.value(idx).getOperationCount() / v.get().getExecutionSpeed());
    }
}
