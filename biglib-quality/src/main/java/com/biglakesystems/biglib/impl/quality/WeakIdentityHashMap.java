package com.biglakesystems.biglib.impl.quality;

import com.biglakesystems.biglib.quality.Assert;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.*;

/**
 * {@link WeakIdentityHashMap} is an implementation of the {@link Map} interface which combines <em>weak</em> and
 * <em>identity</em> semantics; that is, it does not prevent its keys from being garbage collected, and it uses object
 * identity when performing key comparisons, rather than {@link Object#equals(Object)}. This is useful when associating
 * values with a particular object, when that object may not be unique across all keys in the map using standard map
 * semantics.
 * <p/>
 * <strong>Thread Safety:</strong> instances of this class are not thread-safe. All operations must be performed on a
 * single thread or appropriate publication and synchronization constructs must be applied externally.
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
 *
 * @param <K> the map key type.
 * @param <V> the map value type.
 */
public class WeakIdentityHashMap<K, V> implements Map<K, V>
{
    private final Map<IdentityWeakReference, V> m_contents;
    private final ReferenceQueue<K> m_queue = new ReferenceQueue<K>();

    /**
     * Construct a {@link WeakIdentityHashMap} instance.
     */
    public WeakIdentityHashMap()
    {
        super();
        m_contents = new HashMap<IdentityWeakReference, V>();
    }

    /**
     * Construct a {@link WeakIdentityHashMap} instance.
     *
     * @param initialCapacity the initial map capacity.
     */
    public WeakIdentityHashMap(final int initialCapacity)
    {
        super();
        m_contents = new HashMap<IdentityWeakReference, V>(initialCapacity);
    }

    /**
     * Construct a {@link WeakIdentityHashMap} instance.
     *
     * @param initialCapacity the initial map capacity.
     * @param loadFactor the map load factor.
     */
    public WeakIdentityHashMap(final int initialCapacity, final float loadFactor)
    {
        super();
        m_contents = new HashMap<IdentityWeakReference, V>(initialCapacity, loadFactor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear()
    {
        purge();
        m_contents.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(final Object key)
    {
        purge();
        return m_contents.containsKey(new IdentityWeakReference((K) key));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsValue(final Object value)
    {
        purge();
        boolean result = false;
        if (m_contents.containsValue(value))
        {
            /* We have the value, but verify that its key has not been collected. */
            for (final Map.Entry<IdentityWeakReference, V> nextEntry : m_contents.entrySet())
            {
                if (value.equals(nextEntry.getValue()))
                {
                    if (null != nextEntry.getKey().get())
                    {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V get(final Object key)
    {
        purge();
        return m_contents.get(new IdentityWeakReference((K) key));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty()
    {
        purge();
        boolean result = true;
        if (!m_contents.isEmpty())
        {
            for (final Map.Entry<IdentityWeakReference, V> nextEntry : m_contents.entrySet())
            {
                if (null != nextEntry.getKey().get())
                {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<K> keySet()
    {
        final Set<K> result;
        if (isEmpty())
        {
            result = Collections.emptySet();
        }
        else
        {
            final Set<IdentityWeakReference> keyReferences = m_contents.keySet();
            result = new HashSet<K>(keyReferences.size());
            for (final IdentityWeakReference nextKey : keyReferences)
            {
                final K key = nextKey.get();
                if (null != key)
                {
                    result.add(key);
                }
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V remove(final Object key)
    {
        purge();
        return m_contents.remove(new IdentityWeakReference((K) key));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<V> values()
    {
        final Collection<V> result;
        if (isEmpty())
        {
            result = Collections.emptyList();
        }
        else
        {
            final Set<Map.Entry<IdentityWeakReference, V>> entries = m_contents.entrySet();
            result = new ArrayList<V>(entries.size());
            for (final Map.Entry<IdentityWeakReference, V> nextEntry : entries)
            {
                if (null != nextEntry.getKey().get())
                {
                    result.add(nextEntry.getValue());
                }
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size()
    {
        purge();
        return m_contents.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putAll(final Map<? extends K, ? extends V> all)
    {
        purge();
        for (final Map.Entry<? extends K, ? extends V> nextEntry : all.entrySet())
        {
            put(nextEntry.getKey(), nextEntry.getValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet()
    {
        purge();
        final Set<Map.Entry<K, V>> result;
        if (m_contents.isEmpty())
        {
            result = Collections.emptySet();
        }
        else
        {
            final Set<IdentityWeakReference> keys = m_contents.keySet();
            result = new HashSet<Map.Entry<K, V>>(keys.size());
            for (final IdentityWeakReference nextKeyReference : keys)
            {
                final K key = nextKeyReference.get();
                if (null != key)
                {
                    result.add(new Entry(key));
                }
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V put(final K key, final V value)
    {
        purge();
        return m_contents.put(new IdentityWeakReference(key), value);
    }

    /**
     * Purge the map of any entries whose keys have been garbage collected.
     */
    private void purge()
    {
        Reference<? extends K> next = m_queue.poll();
        while (null != next)
        {
            m_contents.remove(next);
            next = m_queue.poll();
        }
    }

    /**
     * {@link Entry} is the {@link Map.Entry} implementation for {@link WeakIdentityHashMap}.
     */
    private final class Entry implements Map.Entry<K, V>
    {
        private final K m_key;

        /**
         * Construct an {@link Entry} instance.
         *
         * @param key the map key.
         */
        public Entry(final K key)
        {
            super();
            m_key = key;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public K getKey()
        {
            return m_key;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("unchecked")
        public V getValue()
        {
            return m_contents.get(new IdentityWeakReference(m_key));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public V setValue(final V value)
        {
            return m_contents.put(new IdentityWeakReference(m_key), value);
        }
    }

    /**
     * {@link IdentityWeakReference} is an extension of the {@link WeakReference} class which provides an identity-based
     * implementation of {@link #equals(Object)} and {@link #hashCode()}.
     * <p/>
     * <strong>Thread Safety:</strong> instances of this class contain no mutable state and are therefore safe for
     * multithreaded access, provided the same is true of all dependencies provided via constructor.
     */
    private final class IdentityWeakReference extends WeakReference<K>
    {
        private final int m_hashCodeValue;

        /**
         * Construct an {@link IdentityWeakReference} instance.
         *
         * @param referent the referent object.
         */
        public IdentityWeakReference(final K referent)
        {
            super(referent, m_queue);
            Assert.argumentNotNull("referent", referent);
            m_hashCodeValue = System.identityHashCode(referent);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object baseOther)
        {
            final boolean result;
            if (this == baseOther)
            {
                result = true;
            }
            else if (null == baseOther || !getClass().equals(baseOther.getClass()))
            {
                result = false;
            }
            else
            {
                @SuppressWarnings("unchecked")
                final IdentityWeakReference other = (IdentityWeakReference) baseOther;
                result = m_hashCodeValue == other.m_hashCodeValue && get() == other.get();
            }
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode()
        {
            return m_hashCodeValue;
        }
    }
}
