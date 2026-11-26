/* Copyright (c) 2026 Jody Garnett and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at
 * http://www.opensource.org/licenses/apache2.0.php.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.imagen.media.util;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.awt.image.BandedSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import org.eclipse.imagen.ImageLayout;
import org.eclipse.imagen.PlanarImage;
import org.eclipse.imagen.TileComputationListener;
import org.eclipse.imagen.TileRequest;
import org.junit.Test;

public class SunTileSchedulerTest {

    private static final SampleModel SAMPLE_MODEL = new BandedSampleModel(DataBuffer.TYPE_BYTE, 10, 10, 1);

    @Test
    public void cancelTilesIgnoresIndicesNotInRequest() {
        SunTileScheduler scheduler = new SunTileScheduler(1, Thread.NORM_PRIORITY, 0, Thread.MIN_PRIORITY);
        BlockingImage image = new BlockingImage();
        List<Point> cancelled = new CopyOnWriteArrayList<>();
        try {
            TileRequest request = scheduler.scheduleTiles(
                    image,
                    new Point[] {new Point(0, 0), new Point(1, 0)},
                    new TileComputationListener[] {new CancelListener(cancelled)});

            scheduler.cancelTiles(request, new Point[] {new Point(1, 0), new Point(5, 5)});

            assertEquals(TileRequest.TILE_STATUS_CANCELLED, request.getTileStatus(1, 0));
            assertEquals(List.of(new Point(1, 0)), cancelled);
        } finally {
            image.release.countDown();
            scheduler.setParallelism(0);
        }
    }

    private static class BlockingImage extends PlanarImage {
        final CountDownLatch release = new CountDownLatch(1);

        BlockingImage() {
            super(new ImageLayout(0, 0, 20, 10, 0, 0, 10, 10, SAMPLE_MODEL, null), null, null);
        }

        @Override
        public Raster getTile(int tileX, int tileY) {
            try {
                release.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return Raster.createWritableRaster(SAMPLE_MODEL, new Point(tileXToX(tileX), tileYToY(tileY)));
        }
    }

    private static class CancelListener implements TileComputationListener {
        final List<Point> cancelled;

        CancelListener(List<Point> cancelled) {
            this.cancelled = cancelled;
        }

        @Override
        public void tileComputed(
                Object eventSource, TileRequest[] requests, PlanarImage image, int tileX, int tileY, Raster tile) {}

        @Override
        public void tileCancelled(Object eventSource, TileRequest[] requests, PlanarImage image, int tileX, int tileY) {
            cancelled.add(new Point(tileX, tileY));
        }

        @Override
        public void tileComputationFailure(
                Object eventSource,
                TileRequest[] requests,
                PlanarImage image,
                int tileX,
                int tileY,
                Throwable situation) {}
    }
}
