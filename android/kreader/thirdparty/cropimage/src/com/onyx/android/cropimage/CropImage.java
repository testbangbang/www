package com.onyx.android.cropimage;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.onyx.android.cropimage.data.CropArgs;
import com.onyx.android.cropimage.util.VisibleForTesting;


/**
 * Builder for crop Intents and utils for handling result
 */
public class CropImage {

    public static abstract class SelectionCallback {
        public void onSelectionFinished(final CropArgs args) {

        }
    }

    public static final String SAVE_TO_FILE_BOOLEAN_KEY="save to file";
    public static final String DELETE_ORIGINAL_FILE_KEY="delete original file";
    public static final String BOTH_WAY_STRETCH_KEY="both way stretch";
    public static final String CROP_ARGS = "crop_args";
    public static final String CROP_PAGE_KEY="crop page";
    public static final String SPLIT_SUB_SCREENS_KEY="split sub screens";
    public static final String ROWS_AND_COLUMNS_KEY="rows and columns";
    public static final String INTENT_ACTION_SELECT_ZOOM_RECT="com.onyx.reader.intent.action.select_zoom_rect";
    public static final String INTENT_ACTION_SELECT_ZOOM_RECT_VALUE_KEY="zoom rect";
    public static final String INTENT_ACTION_SELECT_ZOOM_RECT_SPLIT_POINTS_KEY ="split points";

    public static final int REQUEST_CROP = 6709;
    public static final int REQUEST_PICK = 9162;
    public static final int RESULT_ERROR = 404;
    public static Bitmap tempBitmap;

    static interface Extra {
        String ASPECT_X = "aspect_x";
        String ASPECT_Y = "aspect_y";
        String MAX_X = "max_x";
        String MAX_Y = "max_y";
        String ERROR = "error";
    }

    private Intent cropIntent;

    /**
     * Create a crop Intent builder with source image
     *
     * @param bitmap Source Bitmap
     */
    public CropImage(Bitmap bitmap) {
        cropIntent = new Intent();
        tempBitmap=bitmap;
    }

    /**
     * Create a crop Intent builder with source image
     *
     * @param source Source image URI
     */
    public CropImage(Uri source) {
        cropIntent = new Intent();
        cropIntent.setData(source);
    }

    /**
     * Set output URI where the cropped image will be saved
     *
     * @param output Output image URI
     */
    public CropImage output(Uri output) {
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, output);
        return this;
    }

    /**
     * Set fixed aspect ratio for crop area
     *
     * @param x Aspect X
     * @param y Aspect Y
     */
    public CropImage withAspect(int x, int y) {
        cropIntent.putExtra(Extra.ASPECT_X, x);
        cropIntent.putExtra(Extra.ASPECT_Y, y);
        return this;
    }

    /**
     * CropImage area with fixed 1:1 aspect ratio
     */
    public CropImage asSquare() {
        cropIntent.putExtra(Extra.ASPECT_X, 1);
        cropIntent.putExtra(Extra.ASPECT_Y, 1);
        return this;
    }

    /**
     * Set maximum crop size
     *
     * @param width Max width
     * @param height Max height
     */
    public CropImage withMaxSize(int width, int height) {
        cropIntent.putExtra(Extra.MAX_X, width);
        cropIntent.putExtra(Extra.MAX_Y, height);
        return this;
    }

    /**
     * Send the crop Intent!
     *
     * @param activity Activity that will receive result
     */
    public void start(Activity activity,boolean needToSaveToFile, boolean deleteOriginalFile,boolean bothWayStretch, final CropArgs args) {
        activity.startActivityForResult(getIntent(activity, needToSaveToFile, deleteOriginalFile, bothWayStretch, args), REQUEST_CROP);
    }

    /**
     * Send the crop Intent!
     *
     * @param context Context
     * @param fragment Fragment that will receive result
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void start(Context context, Fragment fragment,boolean needToSaveToFile,boolean deleteOriginalFile,boolean bothWayStretch, final CropArgs args) {
        fragment.startActivityForResult(getIntent(context,needToSaveToFile,deleteOriginalFile,bothWayStretch, args), REQUEST_CROP);
    }

    @VisibleForTesting
    Intent getIntent(Context context,boolean needToSaveToFile,boolean deleteOriginalFile,boolean bothWayStretch, final CropArgs args) {
        cropIntent.setClass(context, CropImageActivity.class);
        cropIntent.putExtra(SAVE_TO_FILE_BOOLEAN_KEY, needToSaveToFile);
        cropIntent.putExtra(DELETE_ORIGINAL_FILE_KEY,deleteOriginalFile);
        cropIntent.putExtra(BOTH_WAY_STRETCH_KEY,bothWayStretch);
        cropIntent.putExtra(CROP_ARGS, JSON.toJSONString(args));
        return cropIntent;
    }

    /**
     * Retrieve URI for cropped image, as set in the Intent builder
     *
     * @param result Output Image URI
     */
    public static Uri getOutput(Intent result) {
        return result.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
    }

    /**
     * Retrieve error that caused crop to fail
     *
     * @param result Result Intent
     * @return Throwable handled in CropImageActivity
     */
    public static Throwable getError(Intent result) {
        return (Throwable) result.getSerializableExtra(Extra.ERROR);
    }

    /**
     * Utility method that starts an image picker since that often precedes a crop
     *
     * @param activity Activity that will receive result
     */
    public static void pickImage(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
        try {
            activity.startActivityForResult(intent, REQUEST_PICK);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, R.string.crop__pick_error, Toast.LENGTH_SHORT).show();
        }
    }

}
