package com.biglakesystems.common.quality;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * {@link AssertTest} provides unit test coverage for {@link Assert}.
 * <p/>
 * Copyright (c) 2014 Big Lake Systems, LLC. All rights reserved.
 */
public class AssertTest
{
    /**
     * Construct a {@link AssertTest} instance.
     */
    public AssertTest()
    {
        super();
    }

    /**
     * Test the implementation of {@link Assert#argumentNotNull(String, Object)}.
     */
    @Test
    public void testArgumentNotNull()
    {
        Assert.argumentNotNull("testing", "valueOtherThanNull");
        try
        {
            Assert.argumentNotNull("testing", null);
            fail("Invocation with null value did not throw.");
        }
        catch (final IllegalArgumentException e)
        {
            /* Good. */
            assertTrue(e.getMessage().contains("testing"));
        }
    }

    /**
     * Test the implementation of {@link Assert#propertyNotAlreadySet(String, Object)}.
     */
    @Test
    public void testPropertyNotSet()
    {
        Assert.propertyNotAlreadySet("testing", null);
        try
        {
            Assert.propertyNotAlreadySet("testing", "testing");
            fail("Invocation with non-null value did not throw.");
        }
        catch (final IllegalStateException e)
        {
            /* Good. */
            assertTrue(e.getMessage().contains("testing"));
        }
    }
}
