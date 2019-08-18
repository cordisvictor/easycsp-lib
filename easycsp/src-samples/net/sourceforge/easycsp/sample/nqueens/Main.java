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
package net.sourceforge.easycsp.sample.nqueens;

import net.sourceforge.easycsp.*;

import java.util.stream.IntStream;

public class Main {

    private static final int BOARD_SIZE = 131;

    public static void main(String[] args) {
        // create CSP(Z,D,C):
        EasyCSP<Integer, Integer> nqueens = EasyCSPBuilder.of("NQueens",
                new IntDomain(1, BOARD_SIZE),
                IntStream.rangeClosed(1, BOARD_SIZE).mapToObj(Integer::valueOf).toArray(Integer[]::new))
                .constrainEachTwo(assignments ->
                        !assignments.value(0).equals(assignments.value(1))
                                && Math.abs(assignments.variable(0).get() - assignments.variable(1).get())
                                != Math.abs(assignments.value(0) - assignments.value(1)))
                .build();

        // solve:
        Solver solver = new EasyCSPSolver(nqueens);
        solver.stream()
                .limit(100)
                .forEach(System.out::println);
        System.out.println(solver.getSolutionCount() + " solution(s) in " + solver.getElapsedTime() / 1000.00 + " seconds");
    }
}
