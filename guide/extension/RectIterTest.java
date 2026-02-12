import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.SampleModel;
import java.util.Random;
import org.eclipse.imagen.*;
import org.eclipse.imagen.iterator.*;

class RectIterTest {

    int width = 10;
    int height = 10;
    int tileWidth = 4;
    int tileHeight = 4;

    public static void main(String[] args) {
        new RectIterTest();
    }

    public RectIterTest() {
        Random rand = new Random(1L);
        Rectangle rect = new Rectangle();

        int[] bandOffsets = { 2, 1, 0 };
        SampleModel sampleModel =
            new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE,
                                            tileWidth, tileHeight,
                                            3, 3*tileWidth,
                                            bandOffsets);
        ColorModel colorModel = null;

        TiledImage im = new TiledImage(0, 0, width, height, 0, 0,
                                       sampleModel,
                                       colorModel);

        int[][][] check = new int[width][height][3];
        int x, y, b;

        for (int i = 0; i < 10; i++) {
            rect.x = rand.nextInt(width);
            rect.width = rand.nextInt(width - rect.x) + 1;

            rect.y = rand.nextInt(height);
            rect.height = rand.nextInt(height - rect.y) + 1;

            System.out.println("Filling rect " + rect + " with " + i);

            WritableRectIter witer = RectIterFactory.createWritable(im,
                                                                    rect);

            b = 0;
            witer.startBands();
            while (!witer.finishedBands()) {
                y = rect.y;
                witer.startLines();
                while (!witer.finishedLines()) {
                    x = rect.x;
                    witer.startPixels();
                    while (!witer.finishedPixels()) {
                        witer.setSample(i);
                        check[x][y][b] = i;

                        ++x;
                        witer.nextPixel();
                    }

                    ++y;
                    witer.nextLine();
                }

                ++b;
                witer.nextBand();
            }
        }

        rect.x = 0;
        rect.y = 0;
        rect.width = width;
        rect.height = height;
        RectIter iter = RectIterFactory.createWritable(im, rect);

        b = 0;
        iter.startBands();
        while (!iter.finishedBands()) {
            System.out.println();

            y = 0;
            iter.startLines();
            while (!iter.finishedLines()) {

                x = 0;
                iter.startPixels();
                while (!iter.finishedPixels()) {
                    int val = iter.getSample();
                    System.out.print(val);

                    if (val != check[x][y][b]) {
                    System.out.print("(" + check[x][y][b] + ")  ");
                    } else {
                        System.out.print("     ");
                    }

                    ++x;
                    iter.nextPixel();
                }

                ++y;
                iter.nextLine();
                System.out.println();
            }

            ++b;
            iter.nextBand();
        }
    }
}