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
import net.sourceforge.easycsp.alg.BranchAndBound;

import static net.sourceforge.easycsp.Constraints.notEqual;

public class MakespanMain {

    /**
     * Makespan main method solves the formal JobScheduling CSOP(Z,D,C):
     * 5 Machines and 5 Jobs (for assigning more than one Job per Machine, the
     * domain must contain arrays of Jobs).
     */
    public static void main(String[] args) {
        // create CSP(Z,D,C):
        EasyCSP makespan = EasyCSPBuilder.of("Makespan",
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
                        new Machine(4, 7)})
                .constrainEachTwo(notEqual())
                .build();

        // solve:
        BranchAndBound alg = BranchAndBound.minimizationOf(makespan, MakespanMain::estimate, MakespanMain::evaluate);
        Solver solver = new EasyCSPSolver(alg);
        while (solver.solve()) {
            System.out.println(solver.getSolutionCount() + " " + solver.currentSolution() + ", Makespan= " + alg.evaluation());
        }
        System.out.println(solver.getSolutionCount() + " optimal solution(s) in " + solver.getElapsedTime() / 1000.00 + " seconds");
    }

    private static double estimate(Solution<Machine, Job> s, int idx, double score) {
        double ret;
        if (idx == 0) {
            ret = s.value(0).getOperationCount() / s.variable(0).get().getExecutionSpeed();
        } else {
            double prvV = s.value(idx - 1).getOperationCount() / s.variable(idx - 1).get().getExecutionSpeed();
            double crtV = s.value(idx).getOperationCount() / s.variable(idx).get().getExecutionSpeed();
            ret = prvV > crtV ? prvV : crtV;
        }
        double max = Double.NEGATIVE_INFINITY;
        for (int i = idx + 1; i < s.size(); i++) {
            // estimate the best case: the job with smallest OperationCount / Machine ExecutionSpeed
            Variable<Machine, Job> v = s.variable(i);
            double crtValue = v.getDomain().get(0).getOperationCount() / v.get().getExecutionSpeed();
            if (crtValue > max) {
                max = crtValue;
            }
        }
        return ret > max ? ret : max;
    }

    private static double evaluate(Solution<Machine, Job> s, int idx, double score) {
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < s.size(); i++) {
            double crtValue = s.value(i).getOperationCount() / s.variable(i).get().getExecutionSpeed();
            if (crtValue > max) {
                max = crtValue;
            }
        }
        return max;
    }
}
