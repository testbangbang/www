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

    static public class PixelPoint {
        public int x;
        public int y;
        public int state;

        public PixelPoint(int x, int y) {
            this.x = x;
            this.y = y;
            state = 1;
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
            updBuffer = bitmap;
        }
    }

    static public class Lut {
        public ArrayList<PixelPoint> pixels = new ArrayList<>();
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
        public ArrayList<PixelPoint> pixels = new ArrayList<>();
        public Bitmap bitmap;
        public void add(int x, int y) {
            pixels.add(new PixelPoint(x, y));
            boundingRect.union(x, y);
        }

        public Collision(final Bitmap b) {
            bitmap = b;
        }

        public boolean isEmpty() {
            return pixels.isEmpty();
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
        workingBuffer = ImageUtils.create(framebufferList.get(0).buffer);
        workingBuffer.eraseColor(Color.WHITE);

        mcu = ImageUtils.loadBitmapFromFile(pathList.get(0));
        mcu.eraseColor(Color.argb(0xff, 0, 0, MAX_FRAME));

        updRequestList.clear();
        updRequestList.add(new UpdateRequest(getCurrentFramebuffer().buffer));
    }

    public void merge() {
        mergeUpdateRequest();
        mergeCollision();
    }

    public void mergeUpdateRequest() {
        ListIterator<UpdateRequest> it = updRequestList.listIterator();
        while (it.hasNext()) {
            int index = it.nextIndex();
            UpdateRequest updateEntry = it.next();
            Bitmap upd = updateEntry.updBuffer;
            Bitmap originUpd = ImageUtils.create(upd);
            Bitmap originWb = ImageUtils.create(workingBuffer);
            Lut lut = new Lut(originUpd);
            Collision collision = new Collision(originUpd);

            int state = mergeUpdateRequest(upd, workingBuffer, lut, collision, mcu, MAX_FRAME);
            Log.e(TAG, "Frame index: " + currentWaveformFrame +  " merge state: " + state + " update buffer index: " + index);
            if ((state & ImageUtils.SOMETHING_MERGED) > 0) {
                dump(originUpd, originWb, upd, workingBuffer, index);
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

            if (!collision.isEmpty() || !lut.isEmpty()) {
                it.remove();
            }
        }
    }

    public void mergeCollision() {
        ListIterator<Collision> it = collisionList.listIterator();
        while (it.hasNext()) {
            int index = it.nextIndex();
            Collision collisionEntry = it.next();
            Bitmap upd = collisionEntry.bitmap;
            Bitmap originUpd = ImageUtils.create(upd);
            Bitmap originWb = ImageUtils.create(workingBuffer);

            int state = mergeCollision(collisionEntry, workingBuffer, mcu, MAX_FRAME);
            Log.e(TAG, "Frame index: " + currentWaveformFrame +  " merge state: " + state + " update buffer index: " + index);
            if ((state & ImageUtils.SOMETHING_MERGED) > 0) {
                dump(originUpd, originWb, upd, workingBuffer, index);
            }

            if (state == ImageUtils.NOTHING_TO_MERGE) {
                it.remove();
            }
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

    public static int mergeCollision(final Collision collision, final Bitmap workingBuffer, final Bitmap mcu, int maxFrame) {
        int updState = NOTHING_TO_MERGE;
        final Bitmap upd = collision.bitmap;
        for(PixelPoint point : collision.pixels) {
            if (point.state == 0) {
                continue;
            }
            int x = point.x;
            int y = point.y;
            int v1 = upd.getPixel(x, y);
            int v2 = workingBuffer.getPixel(x, y);
            if (v1 == Color.TRANSPARENT || v1 == v2) {
                continue;
            }
            int state = (mcu.getPixel(x, y) & 0xff);
            if (state >= maxFrame) {
                workingBuffer.setPixel(x, y, v1);
                mcu.setPixel(x, y, Color.argb(0xff, 0, 0, 0));
                updState |= SOMETHING_MERGED;
                point.state = 0;
            } else {
                updState |= PENDING;
            }
        }
        return updState;
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

        path = String.format("/mnt/sdcard/result-frame-" + currentWaveformFrame + "-update-buffer-index-" + index + ".png");
        FileUtils.deleteFile(path);
        Bitmap result = ImageUtils.merge(originUpd, originWb, mergedUpd, mergedWb);
        BitmapUtils.saveBitmap(result, path);
        Log.e(TAG, "save result buffer: " + path);

//        Bitmap result = ImageUtils.merge(upd, workingBuffer);
//        path = String.format("/mnt/sdcard/result-" + currentWaveformFrame + "-update-buffer-" + updIndex + ".png");
//        FileUtils.deleteFile(path);
//        BitmapUtils.saveBitmap(result, path);
//        Log.e(TAG, "save result bitmap: " + path);
    }

    public boolean isFinished() {
        return updRequestList.isEmpty() && collisionList.isEmpty() && lutList.isEmpty();
    }

    public void nextWaveformFrame() {
        currentWaveformFrame += waveformFrameStep;
        ListIterator<Lut> iterator = lutList.listIterator();
        while (iterator.hasNext()) {
            Lut lut = iterator.next();
            lut.currentFrame += waveformFrameStep;
            if (lut.isFinished()) {
                Log.e("################", "LUT Finished: ");
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

        updRequestList.clear();
        updRequestList.add(new UpdateRequest(getCurrentFramebuffer().buffer));
        return true;
    }

    public Framebuffer getCurrentFramebuffer() {
        return framebufferList.get(currentFb);
    }

}
