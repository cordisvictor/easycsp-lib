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
package net.sourceforge.easycsp.sample.semaphoresync;

import net.sourceforge.easycsp.*;
import net.sourceforge.easycsp.Algorithm.Fitness;
import net.sourceforge.easycsp.alg.BranchAndBound;

import static net.sourceforge.easycsp.Constraints.notEqual;

public class Main {

    private static final int DIRECTION_COUNT = 6;

    public static void main(String[] args) {
        // create CSP(Z,D,C):
        EasyCSP<Object, Integer> semsync = EasyCSPBuilder.of("SemaphoreSync", DIRECTION_COUNT, new IntDomain(1, DIRECTION_COUNT))
                .constrain(notEqual(), 0, 2)
                .constrain(notEqual(), 0, 3)
                .constrain(notEqual(), 0, 4)
                .constrain(notEqual(), 1, 3)
                .constrain(notEqual(), 1, 5)
                .constrain(notEqual(), 2, 4)
                .constrain(notEqual(), 2, 5)
                .constrain(notEqual(), 3, 4)
                .constrain(notEqual(), 4, 5)
                .build();

        // solve:
        // since estimation and evaluation functions are alike for this csp,
        // the same instance will be used for both estimation and evaluation.
        BranchAndBound alg = BranchAndBound.minimizationOf(semsync, Main::estimate, Main::estimate);
        Solver solver = new EasyCSPSolver<>(alg);
        solver.stream()
                .forEach(solution -> {
                    System.out.println(solution + ", with " + alg.evaluation() + " states");
                });
        System.out.println(solver.getSolutionCount() + " optimal solution(s) in " + solver.getElapsedTime() / 1000.00 + " seconds");
    }

    private static double estimate(Solution<?, Integer> s, int idx, double score) {
        int varIdxVal = s.value(idx);
        for (int i = 0; i < idx; i++) {
            if (s.value(i) == varIdxVal) {
                return score;
            }
        }
        return score + 1;
    }
}
