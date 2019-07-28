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
package net.sourceforge.easycsp;

import java.util.Objects;
import java.util.function.Predicate;

import net.sourceforge.easycsp.Constraint.Assignments;

/**
 * Constraints class is a factory for standard and thread-safe constraint condition {@link Predicate}s.
 *
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.1.0
 * @since 1.1.0
 */
public final class Constraints {

    /**
     * Returns an unary equal condition.
     *
     * @return the condition
     */
    public static <U, T> Predicate<Assignments<U, T>> equal(Object value) {
        return assignments -> Objects.equals(value, assignments.value(0));
    }

    /**
     * Returns a unary non-equals condition.
     *
     * @return the condition
     */
    public static <U, T> Predicate<Assignments<U, T>> notEqual(Object value) {
        return assignments -> !Objects.equals(value, assignments.value(0));
    }

    /**
     * Returns a unary is-null condition.
     *
     * @return the condition
     */
    public static <U, T> Predicate<Assignments<U, T>> isNull() {
        return assignments -> assignments.value(0) == null;
    }

    /**
     * Returns a unary non-null condition.
     *
     * @return the condition
     */
    public static <U, T> Predicate<Assignments<U, T>> notNull() {
        return assignments -> assignments.value(0) != null;
    }

    /**
     * Returns a binary equal condition.
     *
     * @return the condition
     */
    public static <U, T> Predicate<Assignments<U, T>> equal() {
        return assignments -> Objects.equals(assignments.value(0), assignments.value(1));
    }

    /**
     * Returns a binary not-equal condition.
     *
     * @return the condition
     */
    public static <U, T> Predicate<Assignments<U, T>> notEqual() {
        return assignments -> !Objects.equals(assignments.value(0), assignments.value(1));
    }

    private Constraints() {
    }
}
