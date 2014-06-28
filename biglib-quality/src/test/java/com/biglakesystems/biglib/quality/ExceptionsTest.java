package com.biglakesystems.biglib.quality;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * {@link ExceptionsTest} provides unit test coverage for {@link Exceptions}.
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
public class ExceptionsTest
{
    /**
     * Construct a {@link ExceptionsTest} instance.
     */
    public ExceptionsTest()
    {
        super();
    }

    /**
     * Test the implementation of {@link Exceptions#uniqueId(Throwable)}.
     */
    @Test
    public void testUniqueId()
    {
        /* Verify that repeated calls with the same instance return the same identifier. */
        final Exception testException = new Exception();
        final String testExceptionId = Exceptions.uniqueId(testException);
        assertTrue(StringUtils.isNotBlank(testExceptionId));
        assertEquals(testExceptionId, Exceptions.uniqueId(testException));
        assertFalse(testExceptionId.equals(Exceptions.uniqueId(new Exception())));

        /* Verify that two different exception objects, which are equal per equals()/hashCode(), do not yield the same
        identifier. */
        final Throwable equalException1 = new AlwaysEqualException();
        final Throwable equalException2 = new AlwaysEqualException();
        assertEquals(equalException1, equalException2);
        assertFalse(Exceptions.uniqueId(equalException1).equals(Exceptions.uniqueId(equalException2)));
    }

    /**
     * {@link AlwaysEqualException} is used by {@link #testUniqueId()} to verify that two different exception instances,
     * for which {@link Object#equals(Object)} returns {@code true}, produce different unique IDs. It implements {@link
     * #equals(Object)} and {@link #hashCode()} such that any two instances will be considered equal.
     */
    private static class AlwaysEqualException extends RuntimeException
    {
        /**
         * Construct an {@link AlwaysEqualException} instance.
         */
        public AlwaysEqualException()
        {
            super();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object obj)
        {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode()
        {
            return 1;
        }
    }
}
