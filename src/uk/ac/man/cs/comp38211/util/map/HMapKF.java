WeChat: cstutorcs
QQ: 749389476
Email: tutorcs@163.com
/*
 * @(#)HashMap.java 1.73 07/03/13 Copyright 2006 Sun Microsystems, Inc. All
 * rights reserved. SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license
 * terms.
 */

package uk.ac.man.cs.comp38211.util.map;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Hash-based implementation of the <tt>MapKF</tt> interface. {@link MapKF} is a
 * specialized variant the standard Java {@link Map} interface, except that the
 * values are hard coded as floats for efficiency reasons (keys can be arbitrary
 * objects). This implementation was adapted from {@link HashMap} version 1.73,
 * 03/13/07. See <a href="{@docRoot} /../content/map.html">this benchmark</a>
 * for an efficiency comparison.
 * 
 * @param <K>
 *            the type of keys maintained by this map
 */

public class HMapKF<K extends Comparable<?>> implements MapKF<K>, Cloneable,
        Serializable
{

    /**
     * The default initial capacity - MUST be a power of two.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 1024;

    /**
     * The maximum capacity, used if a higher value is implicitly specified by
     * either of the constructors with arguments. MUST be a power of two <=
     * 1<<30.
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load factor used when none specified in constructor.
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * The table, resized as necessary. Length MUST Always be a power of two.
     */
    transient Entry<K>[] table;

    /**
     * The number of key-value mappings contained in this map.
     */
    transient int size;

    /**
     * The next size value at which to resize (capacity * load factor).
     * 
     * @serial
     */
    int threshold;

    /**
     * The load factor for the hash table.
     * 
     * @serial
     */
    final float loadFactor;

    /**
     * The number of times this HMapKF has been structurally modified Structural
     * modifications are those that change the number of mappings in the HMapKF
     * or otherwise modify its internal structure (e.g., rehash). This field is
     * used to make iterators on Collection-views of the HMapKF fail-fast. (See
     * ConcurrentModificationException).
     */
    transient volatile int modCount;

    /**
     * Constructs an empty <tt>HMapKF</tt> with the specified initial capacity
     * and load factor.
     * 
     * @param initialCapacity
     *            the initial capacity
     * @param loadFactor
     *            the load factor
     * @throws IllegalArgumentException
     *             if the initial capacity is negative or the load factor is
     *             nonpositive
     */
    @SuppressWarnings("unchecked")
    public HMapKF(int initialCapacity, float loadFactor)
    {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: "
                    + initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: "
                    + loadFactor);

        // Find a power of 2 >= initialCapacity
        int capacity = 1;
        while (capacity < initialCapacity)
            capacity <<= 1;

        this.loadFactor = loadFactor;
        threshold = (int) (capacity * loadFactor);
        table = new Entry[capacity];
        init();
    }

    /**
     * Constructs an empty <tt>HMapKF</tt> with the specified initial capacity
     * and the default load factor (0.75).
     * 
     * @param initialCapacity
     *            the initial capacity.
     * @throws IllegalArgumentException
     *             if the initial capacity is negative.
     */
    public HMapKF(int initialCapacity)
    {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs an empty <tt>HMapKF</tt> with the default initial capacity
     * (1024) and the default load factor (0.75).
     */
    @SuppressWarnings("unchecked")
    public HMapKF()
    {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        table = new Entry[DEFAULT_INITIAL_CAPACITY];
        init();
    }

    /**
     * Constructs a new <tt>HMapKF</tt> with the same mappings as the specified
     * <tt>MapKF</tt>. The <tt>HMapKF</tt> is created with default load factor
     * (0.75) and an initial capacity sufficient to hold the mappings in the
     * specified <tt>MapKF</tt>.
     * 
     * @param m
     *            the map whose mappings are to be placed in this map
     * @throws NullPointerException
     *             if the specified map is null
     */
    public HMapKF(MapKF<? extends K> m)
    {
        this(Math.max((int) (m.size() / DEFAULT_LOAD_FACTOR) + 1,
                DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);
        putAllForCreate(m);
    }

    // internal utilities

    /**
     * Initialization hook for subclasses. This method is called in all
     * constructors and pseudo-constructors (clone, readObject) after HMapKF has
     * been initialized but before any entries have been inserted. (In the
     * absence of this method, readObject would require explicit knowledge of
     * subclasses.)
     */
    void init()
    {
    }

    /**
     * Applies a supplemental hash function to a given hashCode, which defends
     * against poor quality hash functions. This is critical because HMapKF uses
     * power-of-two length hash tables, that otherwise encounter collisions for
     * hashCodes that do not differ in lower bits. Note: Null keys always map to
     * hash 0, thus index 0.
     */
    static int hash(int h)
    {
        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    /**
     * Returns index for hash code h.
     */
    static int indexFor(int h, int length)
    {
        return h & (length - 1);
    }

    // doc copied from interface
    public int size()
    {
        return size;
    }

    // doc copied from interface
    public boolean isEmpty()
    {
        return size == 0;
    }

    // doc copied from interface
    public float get(K key)
    {
        if (key == null) return getForNullKey();
        int hash = hash(key.hashCode());
        for (Entry<K> e = table[indexFor(hash, table.length)]; e != null; e = e.next)
        {
            Object k;
            if (e.hash == hash && ((k = e.key) == key || key.equals(k)))
                return e.value;
        }

        return DEFAULT_VALUE;
    }

    /**
     * Offloaded version of get() to look up null keys. Null keys map to index
     * 0. This null case is split out into separate methods for the sake of
     * performance in the two most commonly used operations (get and put), but
     * incorporated with conditionals in others.
     */
    private float getForNullKey()
    {
        for (Entry<K> e = table[0]; e != null; e = e.next)
        {
            if (e.key == null) return e.value;
        }

        return DEFAULT_VALUE;
    }

    // doc copied from interface
    public boolean containsKey(K key)
    {
        return getEntry(key) != null;
    }

    /**
     * Returns the entry associated with the specified key in the HMapKF.
     * Returns null if the HMapKF contains no mapping for the key.
     */
    final Entry<K> getEntry(Object key)
    {
        int hash = (key == null) ? 0 : hash(key.hashCode());
        for (Entry<K> e = table[indexFor(hash, table.length)]; e != null; e = e.next)
        {
            Object k;
            if (e.hash == hash
                    && ((k = e.key) == key || (key != null && key.equals(k))))
                return e;
        }
        return null;
    }

    // doc copied from interface
    public float put(K key, float value)
    {
        if (key == null)
        {
            return putForNullKey(value);
        }
        int hash = hash(key.hashCode());
        int i = indexFor(hash, table.length);
        for (Entry<K> e = table[i]; e != null; e = e.next)
        {
            Object k;
            if (e.hash == hash && ((k = e.key) == key || key.equals(k)))
            {
                float oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }

        modCount++;
        addEntry(hash, key, value, i);
        return DEFAULT_VALUE;
    }

    /**
     * Offloaded version of put for null keys
     */
    private float putForNullKey(float value)
    {
        for (Entry<K> e = table[0]; e != null; e = e.next)
        {
            if (e.key == null)
            {
                float oldValue = value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }

        modCount++;
        addEntry(0, null, value, 0);
        return DEFAULT_VALUE;
    }

    /**
     * This method is used instead of put by constructors and pseudoconstructors
     * (clone, readObject). It does not resize the table, check for
     * comodification, etc. It calls createEntry rather than addEntry.
     */
    private void putForCreate(K key, float value)
    {
        int hash = (key == null) ? 0 : hash(key.hashCode());
        int i = indexFor(hash, table.length);

        /**
         * Look for preexisting entry for key. This will never happen for clone
         * or deserialize. It will only happen for construction if the input Map
         * is a sorted map whose ordering is inconsistent w/ equals.
         */
        for (Entry<K> e = table[i]; e != null; e = e.next)
        {
            Object k;
            if (e.hash == hash
                    && ((k = e.key) == key || (key != null && key.equals(k))))
            {
                e.value = value;
                return;
            }
        }

        createEntry(hash, key, value, i);
    }

    private void putAllForCreate(MapKF<? extends K> m)
    {
        for (Iterator<? extends MapKF.Entry<? extends K>> i = m.entrySet()
                .iterator(); i.hasNext();)
        {
            MapKF.Entry<? extends K> e = i.next();
            putForCreate(e.getKey(), e.getValue());
        }
    }

    /**
     * Rehashes the contents of this map into a new array with a larger
     * capacity. This method is called automatically when the number of keys in
     * this map reaches its threshold.
     * 
     * If current capacity is MAXIMUM_CAPACITY, this method does not resize the
     * map, but sets threshold to Integer.MAX_VALUE. This has the effect of
     * preventing future calls.
     * 
     * @param newCapacity
     *            the new capacity, MUST be a power of two; must be greater than
     *            current capacity unless current capacity is MAXIMUM_CAPACITY
     *            (in which case value is irrelevant).
     */
    @SuppressWarnings("unchecked")
    void resize(int newCapacity)
    {
        Entry<K>[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY)
        {
            threshold = Integer.MAX_VALUE;
            return;
        }

        Entry<K>[] newTable = new Entry[newCapacity];
        transfer(newTable);
        table = newTable;
        threshold = (int) (newCapacity * loadFactor);
    }

    /**
     * Transfers all entries from current table to newTable.
     */
    void transfer(Entry<K>[] newTable)
    {
        Entry<K>[] src = table;
        int newCapacity = newTable.length;
        for (int j = 0; j < src.length; j++)
        {
            Entry<K> e = src[j];
            if (e != null)
            {
                src[j] = null;
                do
                {
                    Entry<K> next = e.next;
                    int i = indexFor(e.hash, newCapacity);
                    e.next = newTable[i];
                    newTable[i] = e;
                    e = next;
                } while (e != null);
            }
        }
    }

    // doc copied from interface
    public void putAll(MapKF<? extends K> m)
    {
        int numKeysToBeAdded = m.size();
        if (numKeysToBeAdded == 0) return;

        /*
         * Expand the map if the map if the number of mappings to be added is
         * greater than or equal to threshold. This is conservative; the obvious
         * condition is (m.size() + size) >= threshold, but this condition could
         * result in a map with twice the appropriate capacity, if the keys to
         * be added overlap with the keys already in this map. By using the
         * conservative calculation, we subject ourself to at most one extra
         * resize.
         */
        if (numKeysToBeAdded > threshold)
        {
            int targetCapacity = (int) (numKeysToBeAdded / loadFactor + 1);
            if (targetCapacity > MAXIMUM_CAPACITY)
                targetCapacity = MAXIMUM_CAPACITY;
            int newCapacity = table.length;
            while (newCapacity < targetCapacity)
                newCapacity <<= 1;
            if (newCapacity > table.length) resize(newCapacity);
        }

        for (Iterator<? extends MapKF.Entry<? extends K>> i = m.entrySet()
                .iterator(); i.hasNext();)
        {
            MapKF.Entry<? extends K> e = i.next();
            put(e.getKey(), e.getValue());
        }
    }

    /**
     * Increments the key by some value. If the key does not exist in the map,
     * its value is set to the parameter value.
     * 
     * @param key
     *            key to increment
     * @param value
     *            increment value
     */
    public void increment(K key, float value)
    {
        if (this.containsKey(key))
        {
            this.put(key, (float) this.get(key) + value);
        }
        else
        {
            this.put(key, value);
        }
    }

    // doc copied from interface
    public float remove(K key)
    {
        Entry<K> e = removeEntryForKey(key);
        if (e != null) return e.value;

        throw new NoSuchElementException();
    }

    /**
     * Removes and returns the entry associated with the specified key in the
     * HMapKF. Returns null if the HMapKF contains no mapping for this key.
     */
    final Entry<K> removeEntryForKey(Object key)
    {
        int hash = (key == null) ? 0 : hash(key.hashCode());
        int i = indexFor(hash, table.length);
        Entry<K> prev = table[i];
        Entry<K> e = prev;

        while (e != null)
        {
            Entry<K> next = e.next;
            Object k;
            if (e.hash == hash
                    && ((k = e.key) == key || (key != null && key.equals(k))))
            {
                modCount++;
                size--;
                if (prev == e)
                    table[i] = next;
                else
                    prev.next = next;
                e.recordRemoval(this);
                return e;
            }
            prev = e;
            e = next;
        }

        return e;
    }

    /**
     * Special version of remove for EntrySet.
     */
    @SuppressWarnings("unchecked")
    final Entry<K> removeMapping(Object o)
    {
        if (!(o instanceof Map.Entry)) return null;

        MapKF.Entry<K> entry = (MapKF.Entry<K>) o;
        Object key = entry.getKey();
        int hash = (key == null) ? 0 : hash(key.hashCode());
        int i = indexFor(hash, table.length);
        Entry<K> prev = table[i];
        Entry<K> e = prev;

        while (e != null)
        {
            Entry<K> next = e.next;
            if (e.hash == hash && e.equals(entry))
            {
                modCount++;
                size--;
                if (prev == e)
                    table[i] = next;
                else
                    prev.next = next;
                e.recordRemoval(this);
                return e;
            }
            prev = e;
            e = next;
        }

        return e;
    }

    // doc copied from interface
    public void clear()
    {
        modCount++;
        Entry<K>[] tab = table;
        for (int i = 0; i < tab.length; i++)
            tab[i] = null;
        size = 0;
    }

    // doc copied from interface
    public boolean containsValue(float value)
    {
        Entry<K>[] tab = table;
        for (int i = 0; i < tab.length; i++)
            for (Entry<K> e = tab[i]; e != null; e = e.next)
                if (value == e.value) return true;
        return false;
    }

    /**
     * Returns a shallow copy of this <tt>HMapKF</tt> instance: the keys and
     * values themselves are not cloned.
     * 
     * @return a shallow copy of this map
     */
    @SuppressWarnings("unchecked")
    public Object clone()
    {
        HMapKF<K> result = null;
        try
        {
            result = (HMapKF<K>) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            // assert false;
        }
        result.table = new Entry[table.length];
        result.entrySet = null;
        result.modCount = 0;
        result.size = 0;
        result.init();
        result.putAllForCreate(this);

        return result;
    }

    static class Entry<K> implements MapKF.Entry<K>
    {
        final K key;
        float value;
        Entry<K> next;
        final int hash;

        /**
         * Creates new entry.
         */
        Entry(int h, K k, float v, Entry<K> n)
        {
            value = v;
            next = n;
            key = k;
            hash = h;
        }

        public final K getKey()
        {
            return key;
        }

        public final float getValue()
        {
            return value;
        }

        public final float setValue(float newValue)
        {
            float oldValue = value;
            value = newValue;
            return oldValue;
        }

        @SuppressWarnings("unchecked")
        public final boolean equals(Object o)
        {
            if (!(o instanceof Map.Entry)) return false;
            MapKF.Entry<K> e = (MapKF.Entry<K>) o;
            K k1 = getKey();
            K k2 = e.getKey();
            if (k1 == k2 || (k1 != null && k1.equals(k2)))
            {
                float v1 = getValue();
                float v2 = e.getValue();
                if (v1 == v2) return true;
            }
            return false;
        }

        public final int hashCode()
        {
            return (key == null ? 0 : key.hashCode()) ^ ((int) value);
        }

        public final String toString()
        {
            return getKey() + "=" + getValue();
        }

        /**
         * This method is invoked whenever the value in an entry is overwritten
         * by an invocation of put(k,v) for a key k that's already in the
         * HMapKF.
         */
        void recordAccess(MapKF<K> m)
        {
        }

        /**
         * This method is invoked whenever the entry is removed from the table.
         */
        void recordRemoval(MapKF<K> m)
        {
        }
    }

    /**
     * Adds a new entry with the specified key, value and hash code to the
     * specified bucket. It is the responsibility of this method to resize the
     * table if appropriate.
     * 
     * Subclass overrides this to alter the behavior of put method.
     */
    void addEntry(int hash, K key, float value, int bucketIndex)
    {
        Entry<K> e = table[bucketIndex];
        table[bucketIndex] = new Entry<K>(hash, key, value, e);
        if (size++ >= threshold) resize(2 * table.length);
    }

    /**
     * Like addEntry except that this version is used when creating entries as
     * part of Map construction or "pseudo-construction" (cloning,
     * deserialization). This version needn't worry about resizing the table.
     * 
     * Subclass overrides this to alter the behavior of HMapKF(Map), clone, and
     * readObject.
     */
    void createEntry(int hash, K key, float value, int bucketIndex)
    {
        Entry<K> e = table[bucketIndex];
        table[bucketIndex] = new Entry<K>(hash, key, value, e);
        size++;
    }

    private abstract class HashIterator<E> implements Iterator<E>
    {
        Entry<K> next; // next entry to return
        int expectedModCount; // For fast-fail
        int index; // current slot
        Entry<K> current; // current entry

        HashIterator()
        {
            expectedModCount = modCount;
            if (size > 0)
            { // advance to first entry
                Entry<K>[] t = table;
                while (index < t.length && (next = t[index++]) == null)
                    ;
            }
        }

        public final boolean hasNext()
        {
            return next != null;
        }

        final Entry<K> nextEntry()
        {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            Entry<K> e = next;
            if (e == null) throw new NoSuchElementException();

            if ((next = e.next) == null)
            {
                Entry<K>[] t = table;
                while (index < t.length && (next = t[index++]) == null)
                    ;
            }
            current = e;
            return e;
        }

        public void remove()
        {
            if (current == null) throw new IllegalStateException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            Object k = current.key;
            current = null;
            HMapKF.this.removeEntryForKey(k);
            expectedModCount = modCount;
        }

    }

    private final class ValueIterator extends HashIterator<Float>
    {
        public Float next()
        {
            return nextEntry().value;
        }
    }

    private final class KeyIterator extends HashIterator<K>
    {
        public K next()
        {
            return nextEntry().getKey();
        }
    }

    private final class EntryIterator extends HashIterator<MapKF.Entry<K>>
    {
        public MapKF.Entry<K> next()
        {
            return nextEntry();
        }
    }

    // Subclass overrides these to alter behavior of views' iterator() method
    Iterator<K> newKeyIterator()
    {
        return new KeyIterator();
    }

    Iterator<Float> newValueIterator()
    {
        return new ValueIterator();
    }

    Iterator<MapKF.Entry<K>> newEntryIterator()
    {
        return new EntryIterator();
    }

    // Views

    private transient Set<MapKF.Entry<K>> entrySet = null;

    /**
     * Each of these fields are initialized to contain an instance of the
     * appropriate view the first time this view is requested. The views are
     * stateless, so there's no reason to create more than one of each.
     */
    transient volatile Set<K> keySet = null;
    transient volatile Collection<Float> values = null;

    // doc copied from interface
    public Set<K> keySet()
    {
        Set<K> ks = keySet;
        return (ks != null ? ks : (keySet = new KeySet()));
    }

    private final class KeySet extends AbstractSet<K>
    {
        @Override
        public Iterator<K> iterator()
        {
            return newKeyIterator();
        }

        @Override
        public int size()
        {
            return size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object o)
        {
            return containsKey((K) o);
        }
    }

    // doc copied from interface
    public Collection<Float> values()
    {
        Collection<Float> vs = values;
        return (vs != null ? vs : (values = new Values()));
    }

    private final class Values extends AbstractCollection<Float>
    {
        @Override
        public Iterator<Float> iterator()
        {
            return newValueIterator();
        }

        @Override
        public int size()
        {
            return size;
        }

        @Override
        public boolean contains(Object o)
        {
            return containsValue((Float) o);
        }
    }

    // doc copied from interface
    public Set<MapKF.Entry<K>> entrySet()
    {
        return entrySet0();
    }

    private Set<MapKF.Entry<K>> entrySet0()
    {
        Set<MapKF.Entry<K>> es = entrySet;
        return es != null ? es : (entrySet = new EntrySet());
    }

    private final class EntrySet extends AbstractSet<MapKF.Entry<K>>
    {
        @Override
        public Iterator<MapKF.Entry<K>> iterator()
        {
            return newEntryIterator();
        }

        @Override
        public int size()
        {
            return size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object o)
        {
            MapKF.Entry<K> e = (MapKF.Entry<K>) o;
            Entry<K> candidate = getEntry(e.getKey());
            return candidate != null && candidate.equals(e);
        }
    }

    /**
     * Save the state of the <tt>HMapKF</tt> instance to a stream (i.e.,
     * serialize it).
     * 
     * @serialData The <i>capacity</i> of the HMapKF (the length of the bucket
     *             array) is emitted (int), followed by the <i>size</i> (an int,
     *             the number of key-value mappings), followed by the key
     *             (Object) and value (Object) for each key-value mapping. The
     *             key-value mappings are emitted in no particular order.
     */
    private void writeObject(java.io.ObjectOutputStream s) throws IOException
    {
        Iterator<MapKF.Entry<K>> i = (size > 0) ? entrySet0().iterator() : null;

        // Write out the threshold, loadfactor, and any hidden stuff
        s.defaultWriteObject();

        // Write out number of buckets
        s.writeInt(table.length);

        // Write out size (number of Mappings)
        s.writeInt(size);

        // Write out keys and values (alternating)
        if (i != null)
        {
            while (i.hasNext())
            {
                MapKF.Entry<K> e = i.next();
                s.writeObject(e.getKey());
                s.writeFloat(e.getValue());
            }
        }
    }

    private static final long serialVersionUID = 362498820763181265L;

    /**
     * Reconstitute the <tt>HMapKF</tt> instance from a stream (i.e.,
     * deserialize it).
     */
    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream s) throws IOException,
            ClassNotFoundException
    {
        // Read in the threshold, loadfactor, and any hidden stuff
        s.defaultReadObject();

        // Read in number of buckets and allocate the bucket array;
        int numBuckets = s.readInt();
        table = new Entry[numBuckets];

        init(); // Give subclass a chance to do its thing.

        // Read in size (number of Mappings)
        int size = s.readInt();

        // Read the keys and values, and put the mappings in the HMapKF
        for (int i = 0; i < size; i++)
        {
            K key = (K) s.readObject();
            float value = s.readFloat();
            putForCreate(key, value);
        }
    }

    // These methods are used when serializing HashSets
    int capacity()
    {
        return table.length;
    }

    float loadFactor()
    {
        return loadFactor;
    }

    public String toString()
    {
        Iterator<MapKF.Entry<K>> i = entrySet().iterator();
        if (!i.hasNext()) return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;)
        {
            MapKF.Entry<K> e = i.next();
            K key = e.getKey();
            float value = e.getValue();
            sb.append(key);
            sb.append('=');
            sb.append(value);
            if (!i.hasNext()) return sb.append('}').toString();
            sb.append(", ");
        }
    }

    // methods not part of a standard HashMap

    /**
     * Adds values of keys from another map to this map.
     * 
     * @param m
     *            the other map
     */
    public void plus(MapKF<K> m)
    {
        for (MapKF.Entry<K> e : m.entrySet())
        {
            K key = e.getKey();

            if (this.containsKey(key))
            {
                this.put(key, this.get(key) + e.getValue());
            }
            else
            {
                this.put(key, e.getValue());
            }
        }
    }

    /**
     * Computes the dot product of this map with another map.
     * 
     * @param m
     *            the other map
     */
    public float dot(MapKF<K> m)
    {
        float s = 0.0f;

        for (MapKF.Entry<K> e : m.entrySet())
        {
            K key = e.getKey();

            if (this.containsKey(key))
            {
                s += this.get(key) * e.getValue();
            }
        }

        return s;
    }

    /**
     * Returns the length of the vector represented by this map.
     * 
     * @return length of the vector represented by this map
     */
    public float length()
    {
        float s = 0.0f;

        for (MapKF.Entry<K> e : this.entrySet())
        {
            s += e.getValue() * e.getValue();
        }

        return (float) Math.sqrt(s);
    }

    /**
     * Normalizes values such that the vector represented by this map has unit
     * length.
     */
    public void normalize()
    {
        float l = this.length();

        for (K f : this.keySet())
        {
            this.put(f, this.get(f) / l);
        }

    }

    /**
     * Returns entries sorted by descending value. Ties broken by the key.
     * 
     * @return entries sorted by descending value
     */
    @SuppressWarnings("unchecked")
    public MapKF.Entry<K>[] getEntriesSortedByValue()
    {
        if (this.size() == 0) return null;

        // for storing the entries
        MapKF.Entry<K>[] entries = new Entry[this.size()];
        int i = 0;
        Entry<K> next = null;

        int index = 0;
        // advance to first entry
        while (index < table.length && (next = table[index++]) == null)
            ;

        while (next != null)
        {
            // current entry
            Entry<K> e = next;

            // advance to next entry
            next = e.next;
            if ((next = e.next) == null)
            {
                while (index < table.length && (next = table[index++]) == null)
                    ;
            }

            // add entry to array
            entries[i++] = e;
        }

        // sort the entries
        Arrays.sort(entries, new Comparator<MapKF.Entry<K>>()
        {
            public int compare(MapKF.Entry<K> e1, MapKF.Entry<K> e2)
            {
                if (e1.getValue() > e2.getValue())
                {
                    return -1;
                }
                else if (e1.getValue() < e2.getValue())
                {
                    return 1;
                }

                if (e1.getKey() == e2.getKey()) return 0;

                return ((Comparable<K>) e1.getKey()).compareTo(e2.getKey());
            }
        });

        return entries;
    }

    /**
     * Returns top <i>n</i> entries sorted by descending value. Ties broken by
     * the key.
     * 
     * @param n
     *            number of entries to return
     * @return top <i>n</i> entries sorted by descending value
     */
    public MapKF.Entry<K>[] getEntriesSortedByValue(int n)
    {
        MapKF.Entry<K>[] entries = getEntriesSortedByValue();

        if (entries == null) return null;

        if (entries.length < n) return entries;

        return Arrays.copyOfRange(entries, 0, n);
    }
}
