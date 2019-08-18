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
package net.sourceforge.easycsp.sample.knighttour;

import net.sourceforge.easycsp.*;
import net.sourceforge.easycsp.Algorithm.Fitness;
import net.sourceforge.easycsp.alg.Greedy;

import static net.sourceforge.easycsp.Constraints.is;
import static net.sourceforge.easycsp.Constraints.notEqual;

public class Main {

    private static final int BOARD_SIZE = 8;
    private static final int CELL_COUNT = 64;
    private static final Cell START = new Cell(7, 7);

    public static void main(String[] args) {
        // create CSP(Z,D,C):
        Cell[] cells = new Cell[CELL_COUNT];
        for (int i = 0, cell = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++, cell++) {
                cells[cell] = new Cell(i, j);
            }
        }
        EasyCSP<?, Cell> knighttour = EasyCSPBuilder.of("Knight's Tour", CELL_COUNT, new ObjectDomain<>(cells))
                .constrain(is(START), 0)
                .constrainSequentially(assignments -> {
                    int dX = Math.abs(assignments.value(0).x - assignments.value(1).x);
                    int dY = Math.abs(assignments.value(0).y - assignments.value(1).y);
                    return dX == 1 && dY == 2 || dX == 2 && dY == 1;
                })
                .constrainEachTwo(notEqual())
                .build();

        // solve:
        Solver s = new EasyCSPSolver(new Greedy(knighttour, minDegree()));
        s.stream()
                .findAny()
                .ifPresent(System.out::println);
        System.out.println(s.getSolutionCount() + " solution in " + s.getElapsedTime() / 1000.00 + " seconds");
    }

    private static Fitness<?, Cell> minDegree() {
        return new Fitness<Object, Cell>() {
            private final boolean[][] used = new boolean[BOARD_SIZE][BOARD_SIZE];

            private boolean isValid(int x, int y) {
                if (x < 0 || BOARD_SIZE - 1 < x) {
                    return false;
                }
                if (y < 0 || BOARD_SIZE - 1 < y) {
                    return false;
                }
                return !this.used[x][y];
            }

            @Override
            public double compute(Solution<Object, Cell> sol, int variableIndex, double score) {
                if (variableIndex > 0) {
                    Cell prev = sol.value(variableIndex - 1);
                    this.used[prev.x][prev.y] = true;
                }
                Cell value = sol.value(variableIndex);
                int degree = 0;
                if (this.isValid(value.x - 1, value.y - 2)) {
                    degree++;
                }
                if (this.isValid(value.x - 2, value.y - 1)) {
                    degree++;
                }
                if (this.isValid(value.x - 2, value.y + 1)) {
                    degree++;
                }
                if (this.isValid(value.x - 1, value.y + 2)) {
                    degree++;
                }
                if (this.isValid(value.x + 1, value.y + 2)) {
                    degree++;
                }
                if (this.isValid(value.x + 2, value.y + 1)) {
                    degree++;
                }
                if (this.isValid(value.x + 2, value.y - 1)) {
                    degree++;
                }
                if (this.isValid(value.x + 1, value.y - 2)) {
                    degree++;
                }
                if (degree == 0) {
                    return -9;
                }
                return (-1) * degree;
            }
        };
    }
}
