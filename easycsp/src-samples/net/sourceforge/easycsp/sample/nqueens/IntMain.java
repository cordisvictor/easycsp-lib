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

import net.sourceforge.easycsp.IntDomain;
import net.sourceforge.easycsp.numeric.IntEasyCSP;
import net.sourceforge.easycsp.numeric.IntEasyCSPBuilder;
import net.sourceforge.easycsp.numeric.IntSolver;

import static net.sourceforge.easycsp.Constraints.notEqual;

public class IntMain {

    private static final int BOARD_SIZE = 124;

    public static void main(String[] args) {
        // create CSP(Z,D,C):
        IntEasyCSPBuilder<?> nqueensBuilder = IntEasyCSPBuilder.of("NQueens", BOARD_SIZE, new IntDomain(1, BOARD_SIZE));
        for (int i = 0; i < BOARD_SIZE; i++) {
            nqueensBuilder.constrainVar(i).plus(i);
        }
        for (int i = 0; i < BOARD_SIZE; i++) {
            nqueensBuilder.constrainVar(i).minus(i);
        }
        nqueensBuilder.constrainEachTwoInRange(notEqual(), 0, BOARD_SIZE);
        nqueensBuilder.constrainEachTwoInRange(notEqual(), BOARD_SIZE, 2 * BOARD_SIZE);
        nqueensBuilder.constrainEachTwoInRange(notEqual(), 2 * BOARD_SIZE, 3 * BOARD_SIZE);
        IntEasyCSP nqueens = nqueensBuilder.build();

        // solve:
        IntSolver<?> solver = new IntSolver<>(nqueens);
        solver.stream()
                .limit(100)
                .map(sol -> sol.toStringFirst(BOARD_SIZE))
                .forEach(System.out::println);
        System.out.println(solver.getSolutionCount() + " solution(s) in " + solver.getElapsedTime() / 1000.00 + " seconds");
    }
}
