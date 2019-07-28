/*
 * Copyright 2012 Victor Cordis
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

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * @author victor
 */
public class ObjectDomainTest {

    @Test
    public void testEmpty() {
        ObjectDomain domain = new ObjectDomain();

        assertTrue(domain.isEmpty());
        assertFalse(domain.iterator().hasNext());
    }

    @Test
    public void testSingleton() {
        ObjectDomain domain = new ObjectDomain(1);

        assertFalse(domain.isEmpty());
        assertEquals(1, domain.get(0));
        assertEquals(1, domain.iterator().next());
    }

    @Test
    public void testMultiple() {
        ObjectDomain domain = new ObjectDomain(1, 2, 3);

        assertEquals(3, domain.size());
        assertEquals(1, domain.get(0));
        assertEquals(2, domain.get(1));
        assertEquals(3, domain.get(2));

        Iterator it = domain.iterator();
        assertEquals(1, it.next());
        assertEquals(2, it.next());
        assertEquals(3, it.next());
    }

    @Test
    public void testAdd() {
        ObjectDomain domain = new ObjectDomain(1, 2);
        domain.add(3);

        assertEquals(3, domain.size());
        Iterator it = domain.iterator();
        assertEquals(1, it.next());
        assertEquals(2, it.next());
        assertEquals(3, it.next());
    }

    @Test
    public void testRemove() {
        ObjectDomain domain = new ObjectDomain(1, 2, 3);
        domain.remove(2);

        assertEquals(2, domain.size());
        Iterator it = domain.iterator();
        assertEquals(1, it.next());
        assertEquals(3, it.next());
    }

    @Test
    public void testRemoveAt() {
        ObjectDomain domain = new ObjectDomain(1, 2, 3);
        domain.removeAt(1);

        assertEquals(2, domain.size());
        Iterator it = domain.iterator();
        assertEquals(1, it.next());
        assertEquals(3, it.next());
    }

    @Test
    public void testToString() {
        ObjectDomain domain = new ObjectDomain(1, 2, 3);
        assertEquals("[1, 2, 3]", domain.toString());

        ObjectDomain empty = new ObjectDomain();
        assertEquals("[]", empty.toString());
    }

    @Test
    public void testEquals() {
        ObjectDomain domain = new ObjectDomain(1, 2, 3);

        assertEquals(new ObjectDomain(1, 2, 3), domain);
    }
}
