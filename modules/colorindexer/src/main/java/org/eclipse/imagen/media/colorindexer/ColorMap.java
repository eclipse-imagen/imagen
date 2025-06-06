/* JAI-Ext - OpenSource Java Advanced Image Extensions Library
*    http://www.geo-solutions.it/
*    Copyright 2014 GeoSolutions


* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at

* http://www.apache.org/licenses/LICENSE-2.0

* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.eclipse.imagen.media.colorindexer;

import static org.eclipse.imagen.media.colorindexer.ColorUtils.*;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.eclipse.imagen.media.colorindexer.ColorMap.ColorEntry;

/**
 * A {@link HashMap} replacement especially designed to map an (eventually packed) color to a non negative integer
 * value, which can be in our use cases a count or a palette index.
 *
 * <p>It uses significant less resources than a normal {@link HashMap} as it avoids the usage of object wrappers and
 * other redundant information that we don't need in this particular application
 *
 * @author Andrea Aime - GeoSolutions
 */
public final class ColorMap implements Iterable<ColorEntry> {

    /** The default initial capacity - MUST be a power of two. */
    static final int DEFAULT_INITIAL_CAPACITY = 1024;

    /** The load factor */
    static final float DEFAULT_LOAD_FACTOR = 0.5f;

    /** The bucket array */
    ColorEntry[] table;

    /** When we reach this entry count the bucket array needs to be expanded */
    int threshold;

    /** The current amount of values */
    int size;

    /** Used to check for modifications during iteration */
    int modificationCount;

    /** Stats */
    long accessCount = 0;

    long scanCount = 0;

    public ColorMap(int initialCapacity) {
        // Find a power of 2 >= initialCapacity, if we don't use powers of two the
        // values in the table might end up being non well distributed
        int capacity = 1;
        while (capacity < initialCapacity) capacity <<= 1;

        table = new ColorEntry[capacity];
        threshold = (int) (capacity * DEFAULT_LOAD_FACTOR);
        this.size = 0;
    }

    public ColorMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Increments the counter associated to the specified color by one, or sets the count of such color to one if
     * missing
     */
    public void increment(int red, int green, int blue, int alpha) {
        increment(red, green, blue, alpha, 1);
    }

    /** Increments the counter associated to the specified color by one */
    public void increment(int r, int g, int b, int a, int increment) {
        int color = color(r, g, b, a);
        int index = indexFor(hash(color), table.length);

        // see if we already have this color, if so, increment its count
        accessCount++;
        for (ColorEntry e = table[index]; e != null; e = e.next) {
            scanCount++;
            if (e.color == color) {
                e.value++;
                return;
            }
        }

        // nope, we need to add a new one, add at the beginning of the list for that bucket
        addEntry(color, increment, index);
    }

    private void addEntry(int color, int value, int index) {
        ColorEntry entry = new ColorEntry(color, value, table[index]);
        table[index] = entry;
        size++;
        modificationCount++;

        // do we need to rehash?
        if (size > threshold) {
            rehash(2 * table.length);
            threshold = (int) (table.length * DEFAULT_LOAD_FACTOR);
        }
    }

    /**
     * Returns the value for the specified color, or -1 if the color is not found
     *
     * @param r
     * @param g
     * @param b
     * @param a
     * @return
     */
    public int get(int r, int g, int b, int a) {
        int color = color(r, g, b, a);
        int index = indexFor(hash(color), table.length);

        // see if we already have this color, if so, increment its count
        accessCount++;
        for (ColorEntry e = table[index]; e != null; e = e.next) {
            scanCount++;
            if (e.color == color) {
                return e.value;
            }
        }

        return -1;
    }

    /**
     * Associates the specified value with a color
     *
     * @param r
     * @param g
     * @param b
     * @param a
     * @return The old value associated with the color, or -1 if no old value was found
     */
    public int put(int r, int g, int b, int a, int value) {
        if (value < 0) {
            throw new IllegalArgumentException("By contract only positive numbers can be used");
        }

        int color = color(r, g, b, a);
        int index = indexFor(hash(color), table.length);

        // see if we already have this color, if so, replace it
        accessCount++;
        for (ColorEntry e = table[index]; e != null; e = e.next) {
            scanCount++;
            if (e.color == color) {
                int oldValue = e.value;
                e.value = value;
                return oldValue;
            }
        }

        // nope, we need to add a new one, add at the beginning of the list for that bucket
        addEntry(color, value, index);
        return -1;
    }

    /**
     * Removes the specified color from the map
     *
     * @param r
     * @param g
     * @param b
     * @param a
     * @return
     */
    public boolean remove(int r, int g, int b, int a) {
        int color = color(r, g, b, a);
        int index = indexFor(hash(color), table.length);

        ColorEntry prev = null;
        for (ColorEntry e = table[index]; e != null; e = e.next) {
            if (e.color == color) {
                if (prev == null) {
                    table[index] = null;
                } else {
                    prev.next = e.next;
                }
                size--;
                modificationCount++;
                return true;
            } else {
                prev = e;
            }
        }

        return false;
    }

    /**
     * Builds a new bucket array and redistributes the color entries among it
     *
     * @param newLength
     */
    private void rehash(int newLength) {
        ColorEntry[] oldTable = table;
        this.table = new ColorEntry[newLength];

        for (ColorEntry bucketStart : oldTable) {
            for (ColorEntry e = bucketStart; e != null; e = e.next) {
                // no need for fancy checks, we know each color is unique in the table
                int index = indexFor(hash(e.color), table.length);
                ColorEntry newEntry = new ColorEntry(e.color, e.value, table[index]);
                table[index] = newEntry;
            }
        }
    }

    /** Returns index for the specified color */
    static int indexFor(int h, int length) {
        return h & (length - 1);
    }

    /** A optimized hash function coming from Java own hash map */
    int hash(int color) {
        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        color ^= (color >>> 20) ^ (color >>> 12);
        return color ^ (color >>> 7) ^ (color >>> 4);
    }

    int size() {
        return size;
    }

    public Iterator<ColorEntry> iterator() {
        return new ColorEntryIterator(modificationCount);
    }

    /**
     * Reset its own status to the one of the other color map. The {@link ColorEntry} are shared, so the other color map
     * should not be used anymore after this call
     */
    public void reset(ColorMap other) {
        this.modificationCount = other.modificationCount;
        this.size = other.size;
        this.table = other.table;
        this.threshold = other.threshold;
    }

    /**
     * Prints out statistics about the color map, number of buckes, empty buckets count, number of entries per bucket,
     * number of access operations and number of average color entries accessed each time
     */
    public void printStats() {
        int empty = 0;
        int largest = 0;
        int sum = 0;
        for (int i = 0; i < table.length; i++) {
            if (table[i] == null) {
                empty++;
            } else {
                ColorEntry ce = table[i];
                int count = 0;
                while (ce != null) {
                    count++;
                    ce = ce.next;
                }
                if (count > largest) {
                    largest = count;
                }
                sum += count;
            }
        }
        System.out.println("Bins " + table.length + ", empty: " + empty + " largest: " + largest + " avg: "
                + sum * 1.0 / (table.length - empty));
        System.out.println("Accesses: " + accessCount + ", scans: " + scanCount + ", scan per access: "
                + (scanCount * 1.0 / accessCount));
        accessCount = 0;
        scanCount = 0;
    }

    /** Class representing a single {@link ColorMap} entry */
    public static final class ColorEntry {
        int color;

        int value;

        private ColorEntry next;

        public ColorEntry(int color, int value, ColorEntry next) {
            this.color = color;
            this.value = value;
            this.next = next;
        }

        @Override
        public String toString() {
            return "ColorEntry [color=" + color + ", value=" + value + "]";
        }
    }

    /** {@link Iterator} implementation for the {@link ColorMap} entries */
    final class ColorEntryIterator implements Iterator<ColorEntry> {

        int idx = 0;

        ColorEntry current;

        int reference;

        public ColorEntryIterator(int reference) {
            this.reference = reference;
        }

        public boolean hasNext() {
            if (reference != modificationCount) {
                throw new ConcurrentModificationException("The map entry count has been modified during the iteration");
            }

            if (current == null) {
                // move to the next bucket
                while (idx < table.length && table[idx] == null) {
                    idx++;
                }

                if (idx == table.length) {
                    return false;
                }

                current = table[idx];
                idx++;
            }

            return current != null;
        }

        public ColorEntry next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            ColorEntry result = current;
            current = result.next;
            return result;
        }

        public void remove() {
            throw new UnsupportedOperationException("Removal is not supported in this iterator");
        }
    }
}
