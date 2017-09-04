package com.onyx.android.sample.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by john on 2/9/2017.
 */

public class EpdManager {

    private static final String TAG = EpdManager.class.getSimpleName();
    private static int MAX_FRAME = 30;

    public static int SOMETHING_MERGED  = 0x01;
    public static int PENDING           = 0x02;
    public static int NOTHING_TO_MERGE  = 0x04;


    public static int STATE_NORMAL = 0;
    public static int STATE_REMOVED = 1;

    static public class PixelPoint {
        public int x;
        public int y;
        public int state = STATE_NORMAL;

        public PixelPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static public class Framebuffer {
        public Rect rect = new Rect();
        public Bitmap buffer;
        public Framebuffer(final Bitmap bitmap) {
            buffer = bitmap;
        }
    }

    static public class UpdateRequest {
        public Rect rect = new Rect();
        public Bitmap updBuffer;
        public UpdateRequest(final Bitmap bitmap) {
            updBuffer = ImageUtils.create(bitmap);
        }
    }

    static public class Lut {
        public List<PixelPoint> pixels = new ArrayList<>();
        public Rect boundingRect = new Rect();
        public int currentFrame;
        public int maxFrame = MAX_FRAME;
        public Bitmap bitmap;
        public Lut(final Bitmap b) {
            bitmap = b;
        }

        public void add(int x, int y) {
            pixels.add(new PixelPoint(x, y));
            boundingRect.union(x, y);
        }


        public boolean isEmpty() {
            return pixels.isEmpty();
        }

        public boolean isFinished() {
            return currentFrame >= maxFrame;
        }
    }

    public static class Collision {
        public Rect boundingRect = new Rect();
        public List<PixelPoint> pixels = new ArrayList<>();
        public void add(int x, int y) {
            pixels.add(new PixelPoint(x, y));
            boundingRect.union(x, y);
        }

        public Collision() {
        }

        public boolean isEmpty() {
            return pixels.isEmpty();
        }

        public void addAll(final List<PixelPoint> list) {
            for(PixelPoint pixelPoint : list) {
                if (pixelPoint.state == STATE_NORMAL) {
                    pixels.add(pixelPoint);
                }
            }
        }
    }

    private List<Framebuffer> framebufferList = new ArrayList<>();
    private List<UpdateRequest> updRequestList = new ArrayList<>();
    private List<Lut> lutList = new ArrayList<>();
    private List<Collision> collisionList = new ArrayList<>();
    private Bitmap workingBuffer;
    private Bitmap mcu;

    private int currentWaveformFrame = 0;
    private int waveformFrameStep = 1;
    private int currentFb = 0;
    private int fbDuration = 50;
    private int mcuDuration = 30;

    public void init(final List<String> pathList) {
        currentFb = 0;
        for(String path : pathList) {
            framebufferList.add(new Framebuffer(ImageUtils.loadBitmapFromFile(path)));
        }
        workingBuffer = ImageUtils.create(getCurrentFramebuffer().buffer);

        mcu = ImageUtils.loadBitmapFromFile(pathList.get(0));
        mcu.eraseColor(Color.argb(0xff, 0, 0, MAX_FRAME));

        updRequestList.clear();
        updRequestList.add(new UpdateRequest(getCurrentFramebuffer().buffer));
    }

    public void merge() {
        mergeUpdateRequest();
        mergeCollisionList();
    }

    public void mergeUpdateRequest() {
        ListIterator<UpdateRequest> it = updRequestList.listIterator();
        while (it.hasNext()) {
            int index = it.nextIndex();
            UpdateRequest updateEntry = it.next();
            Bitmap upd = updateEntry.updBuffer;
            Bitmap originWb = ImageUtils.create(workingBuffer);
            Lut lut = new Lut(upd);
            Collision collision = new Collision();

            int state = mergeUpdateRequest(upd, workingBuffer, lut, collision, mcu, MAX_FRAME);
            Log.e(TAG, "Waveform index: " + currentWaveformFrame +  " merge state: " + state + " update buffer index: " + index);
            if ((state & ImageUtils.SOMETHING_MERGED) > 0) {
                dumpStateToBitmap(upd, lut, collision, originWb, workingBuffer, index);
            }

            if (state == ImageUtils.NOTHING_TO_MERGE) {
                Log.e(TAG, "removed upd request: " + index);
            }

            if (!collision.isEmpty()) {
                collisionList.add(collision);
            }
            if (!lut.isEmpty()) {
                lutList.add(lut);
            }
            it.remove();
        }
    }

    // try to reduce all collisions and merge the final collision with wb.
    // it can also be merged with update request rect
    public void mergeCollisionList() {
        Collision mergedCollision = new Collision();
        ListIterator<Collision> it = collisionList.listIterator();
        while (it.hasNext()) {
            Collision collisionEntry = it.next();
            mergedCollision.addAll(collisionEntry.pixels);
        }
        collisionList.clear();
        collisionList.add(mergedCollision);

        Bitmap framebuffer = getCurrentFramebuffer().buffer;
        final Bitmap collision = createByIndex(framebuffer, mergedCollision.pixels);
        Bitmap originWb = ImageUtils.create(workingBuffer);

        int state = mergeCollision(mergedCollision, framebuffer, workingBuffer, mcu, MAX_FRAME);
        Log.e(TAG, "Collision merged wf frame: " + currentWaveformFrame +  " merge state: " + state + " collision index: " + 0);
        if ((state & ImageUtils.SOMETHING_MERGED) > 0) {
            dump(framebuffer, collision, originWb, workingBuffer, 0);
        }

        if (state == NOTHING_TO_MERGE) {
            collisionList.clear();
        }
    }

    public static int mergeUpdateRequest(final Bitmap upd, final Bitmap workingBuffer, final Lut lut, final Collision collision, final Bitmap mcu, int maxFrame) {
        int updState = NOTHING_TO_MERGE;
        for(int y = 0; y < upd.getHeight(); ++y) {
            for(int x = 0; x < upd.getWidth(); ++x) {
                int v1 = upd.getPixel(x, y);
                int v2 = workingBuffer.getPixel(x, y);
                if (v1 == Color.TRANSPARENT || v1 == v2) {
                    continue;
                }
                int state = (mcu.getPixel(x, y) & 0xff);
                if (state >= maxFrame) {
                    workingBuffer.setPixel(x, y, v1);
                    lut.add(x, y);
                    mcu.setPixel(x, y, Color.argb(0xff, 0, 0, 0));
                    updState |= SOMETHING_MERGED;
                } else {
                    updState |= PENDING;
                    collision.add(x, y);
                }
            }
        }
        return updState;
    }

    public static int mergeCollision(final Collision collision,
                                     final Bitmap framebuffer,
                                     final Bitmap workingBuffer,
                                     final Bitmap mcu, int maxFrame) {
        int updState = NOTHING_TO_MERGE;
        for(PixelPoint point : collision.pixels) {
            if (point.state == STATE_REMOVED) {
                continue;
            }
            int x = point.x;
            int y = point.y;
            int v1 = framebuffer.getPixel(x, y);
            int v2 = workingBuffer.getPixel(x, y);
            if (v1 == v2) {
                point.state = STATE_REMOVED;
                continue;
            }
            int state = (mcu.getPixel(x, y) & 0xff);
            if (state >= maxFrame) {
                workingBuffer.setPixel(x, y, v1);
                mcu.setPixel(x, y, Color.argb(0xff, 0, 0, 0));
                updState |= SOMETHING_MERGED;
                point.state = STATE_REMOVED;
            } else {
                updState |= PENDING;
            }
        }
        return updState;
    }

    public void dumpStateToBitmap(final Bitmap upd,
                                  final Lut lut,
                                  final Collision collision,
                                  final Bitmap originWB,
                                  final Bitmap workingBuffer,
                                  int index) {
        String path;
        path = String.format("/mnt/sdcard/wf-" + currentWaveformFrame + "-lut-" + index + ".png");
        FileUtils.deleteFile(path);
        Bitmap lutBitmap = createByIndex(lut.bitmap, lut.pixels);
        Bitmap collisionBitmap = createByIndex(upd, collision.pixels);
        Bitmap result = ImageUtils.merge(upd, lutBitmap, collisionBitmap, originWB, workingBuffer);
        BitmapUtils.saveBitmap(result, path);
        Log.e(TAG, "save result buffer: " + path);
    }

    static public Bitmap createByIndex(final Bitmap bitmap, final List<PixelPoint> pixelPoints) {
        Bitmap target = ImageUtils.create(bitmap);
        target.eraseColor(Color.WHITE);
        for (PixelPoint pixelPoint : pixelPoints) {
            if (pixelPoint.state == STATE_NORMAL) {
                target.setPixel(pixelPoint.x, pixelPoint.y, bitmap.getPixel(pixelPoint.x, pixelPoint.y));
            }
        }
        return target;
    }

    public void dump(final Bitmap originUpd, final Bitmap originWb, final Bitmap mergedUpd, final Bitmap mergedWb, int index) {
        String path;

        boolean dumpUpd = false;
        if (dumpUpd) {
            path = String.format("/mnt/sdcard/merged-upd-frame-" + currentWaveformFrame + "-update-buffer-" + index + ".png");
            FileUtils.deleteFile(path);
            BitmapUtils.saveBitmap(mergedUpd, path);
            Log.e(TAG, "save upd buffer: " + path);
        }

        path = String.format("/mnt/sdcard/wf-" + currentWaveformFrame + "-collision-" + index + ".png");
        FileUtils.deleteFile(path);
        Bitmap result = ImageUtils.merge(originUpd, originWb, mergedUpd, mergedWb);
        BitmapUtils.saveBitmap(result, path);
        Log.e(TAG, "save result buffer: " + path);
    }

    public boolean isFinished() {
        return updRequestList.isEmpty() && collisionList.isEmpty() && lutList.isEmpty() && currentFb >= framebufferList.size();
    }

    public void nextWaveformFrame() {
        currentWaveformFrame += waveformFrameStep;
        ListIterator<Lut> iterator = lutList.listIterator();
        while (iterator.hasNext()) {
            int index = iterator.nextIndex();
            Lut lut = iterator.next();
            lut.currentFrame += waveformFrameStep;
            if (lut.isFinished()) {
                Log.e("################", "LUT: " + index + " Finished, with rect: " + lut.boundingRect);
                iterator.remove();
            }
        }
        ImageUtils.nextFrame(mcu, MAX_FRAME, waveformFrameStep);
    }

    public boolean nextFramebuffer() {
        ++currentFb;
        if (currentFb >= framebufferList.size()) {
            return false;
        }

        updRequestList.add(new UpdateRequest(getCurrentFramebuffer().buffer));
        return true;
    }

    public Framebuffer getCurrentFramebuffer() {
        if (currentFb < framebufferList.size()) {
            return framebufferList.get(currentFb);
        }
        return framebufferList.get(framebufferList.size() - 1);
    }

    public int rate() {
        return 5;
    }

}
