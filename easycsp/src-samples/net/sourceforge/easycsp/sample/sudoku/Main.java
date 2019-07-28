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
package net.sourceforge.easycsp.sample.sudoku;

import net.sourceforge.easycsp.*;

import java.util.Arrays;

import static net.sourceforge.easycsp.Constraints.equal;
import static net.sourceforge.easycsp.Constraints.notEqual;

public class Main {

    // Sudoku generated by GNOME Sudoku,
    // as shown in the Sudoku.png
    // difficulty: very hard
    private static final Integer[][] BOARD = {
            {0, 0, 9, 3, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 0, 4, 5, 6, 3},
            {4, 0, 3, 0, 0, 6, 0, 0, 1},
            {7, 0, 0, 4, 0, 5, 0, 1, 0},
            {0, 0, 1, 0, 0, 0, 2, 0, 0},
            {0, 6, 0, 1, 0, 2, 0, 0, 7},
            {8, 0, 0, 5, 0, 0, 3, 0, 6},
            {3, 9, 6, 8, 0, 0, 0, 0, 2},
            {0, 0, 0, 0, 0, 3, 8, 0, 0}
    };

    public static void main(String[] args) {
        // create CSP(Z,D,C):
        Variable<Integer, Integer>[] vars = Arrays.stream(BOARD)
                .flatMap(row -> Arrays.stream(row))
                .map(cell -> new Variable<>(0, cell, new IntDomain(1, 9)))
                .toArray(Variable[]::new);
        EasyCSPBuilder sudoku = EasyCSPBuilder.of("Sudoku", vars);

        for (int i = 0; i < 9; i++) {
            int rowOffset = i * 9;
            sudoku.constrainEachTwoInRange(notEqual(), rowOffset, rowOffset + 9); // row
            sudoku.constrainEachTwo(notEqual(), i, 9 + i, 2 * 9 + i, 3 * 9 + i, 4 * 9 + i, 5 * 9 + i, 6 * 9 + i, 7 * 9 + i, 8 * 9 + i); // column
        }
        for (int i = 0; i < 9; i += 3) {
            int rowOffset = i * 9;
            for (int j = 0; j < 9; j += 3) {
                sudoku.constrainEachTwo(notEqual(), rowOffset + j, rowOffset + 1 + j, rowOffset + 2 + j, rowOffset + 9 + j, rowOffset + 10 + j, rowOffset + 11 + j, rowOffset + 18 + j, rowOffset + 19 + j, rowOffset + 20 + j); // square
            }
        }
        for (int i = 0; i < vars.length; i++) {
            int predefinedCell = vars[i].get();
            if (predefinedCell != 0) {
                sudoku.constrain(equal(predefinedCell), i); // predefined cell values from BOARD
            }
        }
        // solve:
        Solver<?, Integer> solver = new Solver(sudoku.build());
        solver.stream()
                .limit(100)
                .forEach(Main::prettyPrint);
        System.out.println(solver.getSolutionCount() + " solution(s) in " + solver.getElapsedTime() / 1000.00 + " seconds");
    }

    private static void prettyPrint(Solution solution) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(solution.value(i * 9 + j) + " ");
            }
            System.out.println();
        }
        System.out.println("------------------");
    }
}