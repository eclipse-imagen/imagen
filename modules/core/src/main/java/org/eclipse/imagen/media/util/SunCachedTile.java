/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.eclipse.imagen.media.util;

import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import org.eclipse.imagen.CachedTile;
import org.eclipse.imagen.PlanarImage;

/**
 * Information associated with a cached tile.
 *
 * <p>This class is used by SunTileCache to create an object that includes all the information associated with a tile,
 * and is put into the tile cache.
 *
 * <p>It also serves as a double linked list.
 *
 * @see SunTileCache
 */
final class SunCachedTile implements CachedTile {

    // Soft or Weak references need to be used, or the objects
    // never get garbage collected.   The OpImage finalize
    // method calls removeTiles().  It was suggested, that
    // the owner be a weak reference.
    Raster tile; // the tile to be cached
    WeakReference owner; // the RenderedImage this tile belongs to

    int tileX; // tile X index
    int tileY; // tile Y index

    Object tileCacheMetric; // Metric for weighting tile computation cost
    long timeStamp; // the last time this tile is accessed

    Object key; // the key used to hash this tile
    long memorySize; // the memory used by this tile in bytes

    SunCachedTile previous; // the SunCachedTile before this tile
    SunCachedTile next; // the SunCachedTile after this tile

    int action = 0; // add, remove, update from tile cache

    /**
     * Constructor that takes a tile cache metric
     *
     * @since 1.1
     */
    SunCachedTile(RenderedImage owner, int tileX, int tileY, Raster tile, Object tileCacheMetric) {

        this.owner = new WeakReference(owner);
        this.tile = tile;
        this.tileX = tileX;
        this.tileY = tileY;

        this.tileCacheMetric = tileCacheMetric; // may be null

        key = hashKey(owner, tileX, tileY);

        // tileMemorySize(Raster tile) inlined for performance
        DataBuffer db = tile.getDataBuffer();
        memorySize = db.getDataTypeSize(db.getDataType()) / 8L * db.getSize() * db.getNumBanks();
    }

    /**
     * Returns the hash table "key" as a <code>Object</code> for this tile. For <code>PlanarImage</code> and <code>
     * SerializableRenderedImage</code>, the key is generated by the method <code>ImageUtilgenerateID(Object) </code>.
     * For the other cases, a <code>Long</code> object is returned. The upper 32 bits for this <code>Long</code> is the
     * tile owner's hash code, and the lower 32 bits is the tile's index.
     */
    static Object hashKey(RenderedImage owner, int tileX, int tileY) {
        long idx = tileY * (long) owner.getNumXTiles() + tileX;

        BigInteger imageID = null;
        if (owner instanceof PlanarImage) imageID = (BigInteger) ((PlanarImage) owner).getImageID();

        if (imageID != null) {
            byte[] buf = imageID.toByteArray();
            int length = buf.length;
            byte[] buf1 = new byte[length + 8];
            System.arraycopy(buf, 0, buf1, 0, length);
            for (int i = 7, j = 0; i >= 0; i--, j += 8) buf1[length++] = (byte) (idx >> j);
            return new BigInteger(buf1);
        }

        idx = idx & 0x00000000ffffffffL;
        return new Long(((long) owner.hashCode() << 32) | idx);
    }

    /**
     * Special version of hashKey for use in SunTileCache.removeTiles(). Minimizes the overhead of repeated calls to
     * hashCode and getNumTiles(). Note that this causes a linkage between the CachedTile and SunTileCache classes in
     * that SunTileCache now has to understand how the tileIndex is calculated.
     */
    /*
        static Long hashKey(int ownerHashCode,
                            int tileIndex) {
            long idx = (long)tileIndex;
            idx = idx & 0x00000000ffffffffL;
            return new Long(((long)ownerHashCode << 32) | idx);
        }
    */
    /** Returns the owner's hash code. */
    /*    static long getOwnerHashCode(Long key) {
            return key.longValue() >>> 32;
        }
    */
    /** Returns a string representation of the class object. */
    public String toString() {
        RenderedImage o = (RenderedImage) getOwner();
        String ostring = o == null ? "null" : o.toString();

        Raster t = getTile();
        String tstring = t == null ? "null" : t.toString();

        return getClass().getName() + "@" + Integer.toHexString(hashCode()) + ": owner = "
                + ostring + " tileX = "
                + Integer.toString(tileX) + " tileY = "
                + Integer.toString(tileY) + " tile = "
                + tstring + " key = "
                + ((key instanceof Long) ? Long.toHexString(((Long) key).longValue()) : key.toString())
                + " memorySize = "
                + Long.toString(memorySize) + " timeStamp = "
                + Long.toString(timeStamp);
    }

    /** Returns the cached tile. */
    public Raster getTile() {
        return tile;
    }

    /** Returns the owner of the cached tile. */
    public RenderedImage getOwner() {
        return (RenderedImage) owner.get();
    }

    /** Returns the current time stamp */
    public long getTileTimeStamp() {
        return timeStamp;
    }

    /** Returns the tileCacheMetric object */
    public Object getTileCacheMetric() {
        return tileCacheMetric;
    }

    /** Returns the tile memory size */
    public long getTileSize() {
        return memorySize;
    }

    /** Returns information about the method that triggered the notification event. */
    public int getAction() {
        return action;
    }
}
