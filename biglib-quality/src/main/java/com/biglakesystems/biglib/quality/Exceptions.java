package com.biglakesystems.biglib.quality;

import com.biglakesystems.biglib.impl.quality.WeakIdentityHashMap;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * {@link Exceptions} provides utility methods for reporting exceptions.
 * <p/>
 * <strong>Thread Safety:</strong> instances of this class contain no mutable state and are therefore safe for
 * multithreaded access, provided the same is true of all dependencies provided via constructor.
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
public class Exceptions
{
    /**
     * Construct a {@link Exceptions} instance.
     */
    private Exceptions()
    {
        super();
    }

    /**
     * Get a string uniquely identifying an exception. The returned identifier will be the same each time this method is
     * invoked for a given exception instance.  This can be used to correlate an exception between a log file and user
     * error message, for example.
     *
     * @param exception the exception to identify.
     * @return {@link String} exception identifier.
     */
    public static String uniqueId(final Throwable exception)
    {
        Assert.argumentNotNull("exception", exception);

        /* First check for an existing ID for this exception. */
        final String result;
        final String existing = s_idsByException.get(exception);
        if (null != existing)
        {
            /* Already identified this exception; use that identifier. */
            result = existing;
        }
        else
        {
            /* Haven't already identified this exception; generate a new identifier. Exceptions generally shouldn't be
            crossing thread boundaries, but we do handle the case where another thread allocates an identifier first. */
            final String created = newUniqueId(exception);
            final String previous = s_idsByException.put(exception, created);
            result = StringUtils.defaultString(previous, created);
        }
        return result;
    }

    /**
     * Generate a unique identifier for an exception. Combines the exception class name, identity hash code, and an
     * ever-incrementing sequence value into a string and then performs a SHA-1 hash of that string, returning the hex
     * hash.
     *
     * @param exception the exception.
     * @return {@link String} unique identifier.
     */
    private static String newUniqueId(final Throwable exception)
    {
        final String content = String.format("%s_%08x_%08x", exception.getClass().getName(),
                System.identityHashCode(exception), s_nextIdSequence.getAndIncrement());
        return DigestUtils.sha1Hex(content);
    }

    /**
     * Weak map correlating exceptions for which {@link #uniqueId(Throwable)} has been called, with the identifiers
     * assigned to those exceptions.
     */
    private static final Map<Throwable, String> s_idsByException =
            Collections.synchronizedMap(new WeakIdentityHashMap<Throwable, String>());

    /**
     * Entropy sequence used when generating exception identifiers.
     */
    private static final AtomicLong s_nextIdSequence = new AtomicLong(0L);
}
