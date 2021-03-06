package com.biglakesystems.biglib.impl.quality;

import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * {@link WeakIdentityHashMapTest} provides unit test coverage for {@link WeakIdentityHashMap}.
 * <p/>
 * Copyright 2014 Big Lake Systems, LLC.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
public class WeakIdentityHashMapTest
{
    /**
     * Construct a {@link WeakIdentityHashMapTest} instance.
     */
    public WeakIdentityHashMapTest()
    {
        super();
    }

    /**
     * Test the implementation of {@link Map}.
     */
    @Test
    public void testMap()
    {
        /* Test basic mutations. */
        final String firstA = new String(new char[]{'A'});
        final String secondA = new String(new char[]{'A'});
        assertEquals(firstA, secondA);
        assertFalse(firstA == secondA);
        final Map<String, String> instance = new WeakIdentityHashMap<String, String>();
        assertTrue(instance.isEmpty());
        instance.put(firstA, "firstAValue");
        assertFalse(instance.isEmpty());
        instance.put(secondA, "secondAValue");
        assertTrue(instance.values().containsAll(Arrays.asList("firstAValue", "secondAValue")));
        assertEquals(2, instance.size());
        assertEquals("firstAValue", instance.get(firstA));
        assertEquals("secondAValue", instance.get(secondA));
        instance.put(firstA, "newFirstAValue");
        assertTrue(instance.values().containsAll(Arrays.asList("newFirstAValue", "secondAValue")));
        assertEquals(2, instance.size());
        assertEquals("newFirstAValue", instance.get(firstA));
        assertEquals("secondAValue", instance.get(secondA));
        assertFalse(instance.containsKey("A"));
        assertNull(instance.remove("A"));
        assertEquals("newFirstAValue", instance.remove(firstA));
        assertEquals(1, instance.size());
        instance.clear();
        assertTrue(instance.isEmpty());
        assertEquals(0, instance.size());

        /* Try to coerce the GC into collecting a key. We'll continue until we witness correct behavior, or until we run
        out of memory. Whichever comes first. */
        boolean verifiedCollectedKey = false;
        for (int i = 1; true; i += 1)
        {
            String anotherA = new String(new char[]{'A'});
            assertNull(instance.put(anotherA, "anotherAValue"));
            if (instance.size() < i)
            {
                /* At least one garbage collected key has been removed from the map; test passed. */
                verifiedCollectedKey = true;
                break;
            }
            if (0 == i % 1000)
            {
                /* Periodically request a GC run. */
                System.gc();
            }
        }
        assertTrue(verifiedCollectedKey);
    }
}
