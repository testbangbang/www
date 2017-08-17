package com.onyx.android.sample.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by john on 17/8/2017.
 */

public class EpdHelper {

    private static final String TAG = EpdHelper.class.getSimpleName();

    private List<Bitmap> updBufferList = new ArrayList<>();
    private List<Bitmap> mergedUpdBufferLst = new ArrayList<>();
    private Bitmap workingBuffer;
    private Bitmap mcu;
    private int maxFrame = 30;
    private int currentFrame = 0;
    private int frameStep = 15;

    public void init(final List<String> pathList) {
        for(String path : pathList) {
            updBufferList.add(ImageUtils.loadBitmapFromFile(path));
        }

        workingBuffer = ImageUtils.loadBitmapFromFile(pathList.get(0));
        mcu = ImageUtils.loadBitmapFromFile(pathList.get(0));
        mcu.eraseColor(Color.argb(0xff, 0, 0, maxFrame));
    }

    public void merge() {
        ListIterator<Bitmap> it = updBufferList.listIterator();
        while (it.hasNext()) {
            int index = it.nextIndex();
            Bitmap upd = it.next();
            Bitmap originUpd = ImageUtils.create(upd);
            Bitmap originWb = ImageUtils.create(workingBuffer);
            int state = ImageUtils.merge(upd, workingBuffer, mcu, maxFrame);
            Log.e(TAG, "merge state: " + state + " update buffer index: " + index);
            if ((state & ImageUtils.SOMETHING_MERGED) > 0) {
                dump(originUpd, originWb, upd, workingBuffer);
                break;
            }
            if (state == ImageUtils.NOTHING_TO_MERGE) {
                mergedUpdBufferLst.add(upd);
                it.remove();
                Log.e(TAG, "removed upd buffer: " + index);
            }
        }
    }

    public boolean isFinished() {
        return updBufferList.isEmpty();
    }

    public void dump(final Bitmap originUpd, final Bitmap originWb, final Bitmap mergedUpd, final Bitmap mergedWb) {
//        String path = String.format("/mnt/sdcard/upd-frame-" + currentFrame + "-update-buffer-" + updIndex + ".png");
//        FileUtils.deleteFile(path);
//        BitmapUtils.saveBitmap(upd, path);
//        Log.e(TAG, "save upd buffer: " + path);

        String path = String.format("/mnt/sdcard/result-frame-" + currentFrame + ".png");
        FileUtils.deleteFile(path);
        Bitmap result = ImageUtils.merge(originUpd, originWb, mergedUpd, mergedWb);
        BitmapUtils.saveBitmap(result, path);
        Log.e(TAG, "save result buffer: " + path);

//        Bitmap result = ImageUtils.merge(upd, workingBuffer);
//        path = String.format("/mnt/sdcard/result-" + currentFrame + "-update-buffer-" + updIndex + ".png");
//        FileUtils.deleteFile(path);
//        BitmapUtils.saveBitmap(result, path);
//        Log.e(TAG, "save result bitmap: " + path);
    }

    public void nextFrame() {
        currentFrame += frameStep;
        ImageUtils.nextFrame(mcu, maxFrame, frameStep);
    }
}
