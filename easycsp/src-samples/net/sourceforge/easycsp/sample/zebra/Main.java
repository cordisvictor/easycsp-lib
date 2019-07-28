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
package net.sourceforge.easycsp.sample.zebra;

import net.sourceforge.easycsp.*;
import net.sourceforge.easycsp.Constraint.Assignments;

import java.util.function.Predicate;

import static net.sourceforge.easycsp.Constraints.equal;
import static net.sourceforge.easycsp.Constraints.notEqual;

public class Main {

    private static final int HOUSE_COUNT = 5; // There are five houses.

    public static void main(String[] args) {
        // create CSP(Z,D,C):
        Variable<String, Integer>[] vars = new Variable[]{
                new Variable(0, "Norwegian", new IntDomain(1, HOUSE_COUNT)),
                new Variable(1, "Ukrainian", new IntDomain(1, HOUSE_COUNT)),
                new Variable(2, "Englishman", new IntDomain(1, HOUSE_COUNT)),
                new Variable(3, "Spaniard", new IntDomain(1, HOUSE_COUNT)),
                new Variable(4, "Japanese", new IntDomain(1, HOUSE_COUNT)),

                new Variable(5, "Kools", new IntDomain(1, HOUSE_COUNT)),
                new Variable(6, "Chesterfield", new IntDomain(1, HOUSE_COUNT)),
                new Variable(7, "Old Gold", new IntDomain(1, HOUSE_COUNT)),
                new Variable(8, "Lucky Strike", new IntDomain(1, HOUSE_COUNT)),
                new Variable(9, "Parliament", new IntDomain(1, HOUSE_COUNT)),

                new Variable(10, "Water", new IntDomain(1, HOUSE_COUNT)),
                new Variable(11, "Tea", new IntDomain(1, HOUSE_COUNT)),
                new Variable(12, "Milk", new IntDomain(1, HOUSE_COUNT)),
                new Variable(13, "Orange juice", new IntDomain(1, HOUSE_COUNT)),
                new Variable(14, "Coffee", new IntDomain(1, HOUSE_COUNT)),

                new Variable(15, "Fox", new IntDomain(1, HOUSE_COUNT)),
                new Variable(16, "Horse", new IntDomain(1, HOUSE_COUNT)),
                new Variable(17, "Snails", new IntDomain(1, HOUSE_COUNT)),
                new Variable(18, "Dog", new IntDomain(1, HOUSE_COUNT)),
                new Variable(19, "Zebra", new IntDomain(1, HOUSE_COUNT)),

                new Variable(20, "Yellow", new IntDomain(1, HOUSE_COUNT)),
                new Variable(21, "Blue", new IntDomain(1, HOUSE_COUNT)),
                new Variable(22, "Red", new IntDomain(1, HOUSE_COUNT)),
                new Variable(23, "Ivory", new IntDomain(1, HOUSE_COUNT)),
                new Variable(24, "Green", new IntDomain(1, HOUSE_COUNT)),
        };
        Predicate<Assignments<String, Integer>> diffEq1 = assignments -> assignments.value(0) - assignments.value(1) == 1;
        Predicate<Assignments<String, Integer>> absDiffEq1 = assignments -> Math.abs(assignments.value(0) - assignments.value(1)) == 1;

        EasyCSP<String, Integer> zebra = EasyCSPBuilder.of("Zebra", vars)
                .constrainEachTwoInRange(notEqual(), 0, 5)
                .constrainEachTwoInRange(notEqual(), 5, 10)
                .constrainEachTwoInRange(notEqual(), 10, 15)
                .constrainEachTwoInRange(notEqual(), 15, 20)
                .constrainEachTwoInRange(notEqual(), 20, 25)
                .constrain(equal(), 2, 22) // The Englishman lives in the red house.
                .constrain(equal(), 3, 18) // The Spaniard owns the dog.
                .constrain(equal(), 14, 24) // Coffee is drunk in the green house.
                .constrain(equal(), 1, 11) // The Ukrainian drinks tea.
                .constrain(diffEq1, 24, 23) // The green house is immediately to the right of the ivory house.
                .constrain(equal(), 7, 17) // The Old Gold smoker owns snails.
                .constrain(equal(), 5, 20) // Kools are smoked in the yellow house.
                .constrain(equal(3), 12) // Milk is drunk in the middle house.
                .constrain(equal(1), 0) // The Norwegian lives in the first house.
                .constrain(absDiffEq1, 6, 15) // The man who smokes Chesterfields lives in the house next to the man with the fox.
                .constrain(absDiffEq1, 5, 16) // Kools are smoked in the house next to the house where the horse is kept.
                .constrain(equal(), 8, 13) // The Lucky Strike smoker drinks orange juice.
                .constrain(equal(), 4, 9) // The Japanese smokes Parliaments.
                .constrain(absDiffEq1, 0, 21) // The Norwegian lives next to the blue house.
                .build();
        // solve:
        try {

            zebra.achieveArcConsistency();

            Solver<String, Integer> solver = new Solver(zebra);
            solver.stream()
                    .findFirst()
                    .ifPresent(Main::prettyPrint);

        } catch (OverconstrainedCSPException ex) {
            System.out.println("No solutions: " + ex.getMessage());
        }
    }

    private static void prettyPrint(Solution<String, Integer> solution) {
        for (int i = 1; i <= HOUSE_COUNT; i++) {
            System.out.print("House" + i + ": ");
            for (int j = 0; j < solution.size(); j++) {
                if (solution.value(j) == i) {
                    System.out.print(solution.variable(j).get() + ", ");
                }
            }
            System.out.println();
        }
    }
}
