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

import java.util.function.Supplier;

/**
 * Variable class represents any object instance and it's {@link Domain} of
 * assignable values.
 *
 * @param <U> underlying object class
 * @param <T> domain values class
 * @author Cordis Victor ( cordis.victor at gmail.com)
 * @version 1.2.1
 * @since 1.0
 */
public class Variable<U, T> implements Supplier<U> {

    private final int id;
    private final U underlying;
    private final Domain<T> domain;

    /**
     * Creates a new instance with the given id and domain. The underlying will be left null.
     *
     * @param id     of the new instance
     * @param domain of the new instance
     * @throws IllegalArgumentException if domain is null
     */
    public Variable(int id, Domain<T> domain) {
        this(id, null, domain);
    }

    /**
     * Creates a new instance with the given id, underlying, and domain.
     *
     * @param id         of this instance
     * @param underlying object represented by this instance
     * @param domain     of this instance
     * @throws IllegalArgumentException if domain is null
     */
    public Variable(int id, U underlying, Domain<T> domain) {
        if (domain == null) {
            throw new IllegalArgumentException("domain: null");
        }
        this.id = id;
        this.underlying = underlying;
        this.domain = domain;
    }

    /**
     * Creates a new instance with just the given id. Should be used just for sub-classing.
     */
    protected Variable(int id) {
        this.id = id;
        this.underlying = null;
        this.domain = null;
    }

    /**
     * Gets the id of this instance.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the underlying object of this instance.
     *
     * @return the underlying
     */
    public U getUnderlying() {
        return underlying;
    }

    /**
     * Gets the domain of this instance.
     *
     * @return the domain
     */
    public Domain<T> getDomain() {
        return domain;
    }

    /**
     * Gets the underlying object of this instance.
     */
    @Override
    public U get() {
        return getUnderlying();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Variable) {
            return this.id == ((Variable) o).id;
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return 257 + this.id;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        String ret = "V" + this.id + "{ ";
        if (this.underlying != null) {
            ret += this.underlying + ": ";
        }
        return ret + this.domain + '}';
    }
}
