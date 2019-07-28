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

import net.sourceforge.easycsp.IntDomain.IntDomainIterator;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * @author victor
 */
public class IntDomainTest {

    @Test
    public void testEmpty() {
        assertTrue(new IntDomain().isEmpty());

        IntDomain domain = new IntDomain(1);
        domain.removeAt(0);

        assertTrue(domain.isEmpty());
        assertFalse(domain.iterator().hasNext());
    }

    @Test
    public void testSize() {
        assertEquals(5, new IntDomain(1, 5).size());

        assertEquals(4, new IntDomain(-5, -2).size());

        assertEquals(7, new IntDomain(-1, 5).size());
        assertEquals(11, new IntDomain(-5, 5).size());
    }

    @Test
    public void testSingleton() {
        IntDomain domain = new IntDomain(1);

        assertFalse(domain.isEmpty());
        assertEquals(1, domain.getInt(0));
        assertEquals(1, (int) domain.iterator().next());

        IntDomain domainNegative = new IntDomain(-2);

        assertFalse(domainNegative.isEmpty());
        assertEquals(-2, domainNegative.getInt(0));
        assertEquals(-2, (int) domainNegative.iterator().next());
    }

    @Test
    public void testMultiple() {
        IntDomain domain = new IntDomain(1, 3);

        assertEquals(3, domain.size());
        assertEquals(1, domain.getInt(0));
        assertEquals(2, domain.getInt(1));
        assertEquals(3, domain.getInt(2));

        Iterator it = domain.iterator();
        assertEquals(1, it.next());
        assertEquals(2, it.next());
        assertEquals(3, it.next());
    }

    @Test
    public void testContains() {
        IntDomain empty = new IntDomain();

        assertTrue(empty.isEmpty());
        assertFalse(empty.contains(1));

        IntDomain domain = new IntDomain(1, 5);

        assertEquals(5, domain.size());
        assertTrue(domain.contains(3));
        assertFalse(domain.contains(6));
    }

    @Test
    public void testContainsNegative() {
        IntDomain domainNegative = new IntDomain(-3, 5);

        assertEquals(9, domainNegative.size());
        assertTrue(domainNegative.contains(-2));
        assertTrue(domainNegative.contains(5));
        assertFalse(domainNegative.contains(6));
    }

    @Test
    public void testContainsWhenSparse() {
        IntDomain domain = new IntDomain(1, 5);
        domain.add(7);
        domain.add(9);
        domain.add(11);
        domain.add(13);
        domain.add(15);
        domain.add(17);

        assertEquals(11, domain.size());
        assertTrue(domain.contains(7));
        assertTrue(domain.contains(11));
        assertTrue(domain.contains(13));
        assertFalse(domain.contains(12));
        assertTrue(domain.contains(17));
    }

    @Test
    public void testAdd() {
        IntDomain domain = new IntDomain(1, 2);
        domain.add(3);

        assertEquals(3, domain.size());
        Iterator it = domain.iterator();
        assertEquals(1, it.next());
        assertEquals(2, it.next());
        assertEquals(3, it.next());
    }

    @Test
    public void testAddMerge() {
        IntDomain domain = new IntDomain(1, 2);
        domain.add(5);
        domain.add(7);

        assertEquals(4, domain.size());
        assertEquals("[1..2]U{5}U{7}", domain.toString());

        domain.add(4);
        assertEquals("[1..2]U[4..5]U{7}", domain.toString());

        domain.add(6);
        assertEquals("[1..2]U[4..7]", domain.toString());

        domain.add(3);
        assertEquals("[1..7]", domain.toString());

        IntDomain domain2Add1st = new IntDomain(1, 3);
        domain2Add1st.removeInt(2);
        assertEquals("{1}U{3}", domain2Add1st.toString());
        domain2Add1st.addInt(0);
        assertEquals("[0..1]U{3}", domain2Add1st.toString());
    }

    @Test
    public void testAddMergeNegative() {
        IntDomain domain = new IntDomain(-3, 2);
        domain.add(5);

        assertEquals(7, domain.size());
        assertEquals("[-3..2]U{5}", domain.toString());

        domain.add(4);
        assertEquals("[-3..2]U[4..5]", domain.toString());

        domain.add(-5);
        assertEquals("{-5}U[-3..2]U[4..5]", domain.toString());

        domain.add(-4);
        assertEquals("[-5..2]U[4..5]", domain.toString());
    }

    @Test
    public void testAddEnsureCap() {
        IntDomain domain = new IntDomain(1, 7);
        domain.remove(5);
        domain.remove(3);

        assertEquals("[1..2]U{4}U[6..7]", domain.toString());
    }

    @Test
    public void testRemove() {
        IntDomain domain = new IntDomain(1, 3);
        domain.remove(2);

        assertEquals(2, domain.size());
        Iterator it = domain.iterator();
        assertEquals(1, it.next());
        assertEquals(3, it.next());
    }

    @Test
    public void testRemoveAt() {
        IntDomain domain = new IntDomain(1, 3);
        domain.removeAt(1);

        assertEquals(2, domain.size());
        Iterator it = domain.iterator();
        assertEquals(1, it.next());
        assertEquals(3, it.next());
    }

    @Test
    public void testIterator() {
        IntDomain domain = new IntDomain(1, 3);
        IntDomainIterator it = domain.domainIterator();

        assertTrue(it.hasNext());
        assertEquals(1, it.nextInt());
        assertTrue(it.hasNext());
        assertEquals(2, it.nextInt());
        assertEquals(1, it.currentIndex());
        it.remove();
        assertEquals(0, it.currentIndex());
        assertTrue(it.hasNext());
        assertEquals(3, it.nextInt());
        assertFalse(it.hasNext());
        assertEquals("{1}U{3}", domain.toString());
    }

    @Test
    public void testToString() {
        IntDomain domain = new IntDomain(1, 3);
        assertEquals("[1..3]", domain.toString());

        domain.remove(2);
        assertEquals("{1}U{3}", domain.toString());

        domain.add(2);
        assertEquals("[1..3]", domain.toString());

        domain.add(0);
        assertEquals("[0..3]", domain.toString());

        IntDomain empty = new IntDomain(1);
        empty.remove(1);
        assertEquals("[]", empty.toString());
    }

    @Test
    public void testToStringNegative() {
        IntDomain domain = new IntDomain(-4, 3);
        assertEquals("[-4..3]", domain.toString());

        domain.remove(2);
        assertEquals("[-4..1]U{3}", domain.toString());

        domain.add(2);
        assertEquals("[-4..3]", domain.toString());

        domain.add(-5);
        assertEquals("[-5..3]", domain.toString());
    }

    @Test
    public void testEquals() {
        IntDomain domain = new IntDomain(1, 3);

        assertEquals(new IntDomain(1, 3), domain);
    }
}
