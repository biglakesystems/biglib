package com.biglakesystems.biglib.quality;

/**
 * {@link Assert} provides utility methods for performing assertions against values and properties on objects.
 * <p/>
 * <strong>Thread Safety:</strong> instances of this class contain no mutable state and are therefore safe for
 * multithreaded access, provided the same is true of all dependencies provided via constructor.
 * <p/>
 * Copyright (c) 2014 Big
 * Lake Systems, LLC. All rights reserved.
 */
public class Assert
{
    /**
     * Construct a {@link Assert} instance.
     */
    private Assert()
    {
        super();
    }


    /**
     * Assert that a named argument, such as a method or constructor argument, cannot be {@code null}.
     *
     * @param name the name of the argument, for inclusion in an assertion failure exception.
     * @param value the value to check.
     * @throws IllegalArgumentException if the {@code value} is {@code null}.
     */
    public static void argumentNotNull(String name, Object value) throws IllegalArgumentException
    {
        if (null == value)
        {
            throw new IllegalArgumentException(String.format("Argument [%s] cannot be null.", name));
        }
    }

    /**
     * Assert that a named property has not been set; in other words, that its value is {@code null}.
     *
     * @param name the name of the property, for inclusion in an assertion failure exception.
     * @param value the value to check.
     * @throws IllegalStateException if the {@code value} is not {@code null}.
     */
    public static void propertyNotAlreadySet(String name, Object value) throws IllegalStateException
    {
        if (null != value)
        {
            throw new IllegalStateException(String.format("Property [%s] has already been set.", name));
        }
    }
}
