package com.onyx.android.cropimage;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.opengl.GLES10;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.onyx.android.cropimage.data.CropArgs;

/**
 * Created by joy on 3/2/17.
 */

public class CropImageDialog extends Dialog {

    private static final int SIZE_DEFAULT = 2048;
    private static final int SIZE_LIMIT = 4096;

    private final Handler handler = new Handler();

    private int sampleSize;
    private RotateBitmap rotateBitmap;
    private CropImageView imageView;
    private HighlightView cropView;
    private boolean shortPress = false;

    private Bitmap image;
    private CropArgs cropArgs;
    private CropImageResultCallback callback;

    public CropImageDialog(Context context, Bitmap image, CropArgs cropArgs, CropImageResultCallback callback) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        setContentView(R.layout.crop__dialog_crop);

        this.image = image;
        this.cropArgs = cropArgs;
        this.callback = callback;
        initComponents();
    }

    private void initComponents() {
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (rotateBitmap != null) {
                    rotateBitmap.recycle();
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        initViews();
        imageView.setCropPage(cropArgs.manualCropPage());
        imageView.setSplitSubScreens(cropArgs.manualSplitPage());
        setupBitmap();
    }

    @Override
    public void show() {
        super.show();
        if (rotateBitmap != null) {
            startCrop();
        }
    }

    private void initViews() {
        imageView = (CropImageView) findViewById(R.id.crop_image);
        imageView.context = getContext();
        imageView.setRecycler(new ImageViewTouchBase.Recycler() {
            @Override
            public void recycle(Bitmap b) {
                b.recycle();
                System.gc();
            }
        });

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });

        findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onSaveClicked();
            }
        });
    }

    private void setupBitmap() {
        sampleSize = calculateBitmapSampleSize(image);
        Matrix matrix=new Matrix();
        float scale = 1.0f / sampleSize;
        matrix.postScale(scale, scale);
        rotateBitmap= new RotateBitmap(Bitmap.createBitmap(image,
                0,0, image.getWidth(), image.getHeight(),matrix,true)
                , ExifInterface.ORIENTATION_UNDEFINED);
    }

    private int calculateBitmapSampleSize(Bitmap bitmap){
        int maxSize = getMaxImageSize();
        int sampleSize = 1;
        while (bitmap.getHeight() / sampleSize > maxSize || bitmap.getWidth() / sampleSize > maxSize) {
            sampleSize = sampleSize << 1;
        }
        return sampleSize;
    }

    private int getMaxImageSize() {
        int textureLimit = getMaxTextureSize();
        if (textureLimit == 0) {
            return SIZE_DEFAULT;
        } else {
            return Math.min(textureLimit, SIZE_LIMIT);
        }
    }

    private int getMaxTextureSize() {
        // The OpenGL texture size is the maximum size that can be drawn in an ImageView
        int[] maxSize = new int[1];
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
        return maxSize[0];
    }

    private void startCrop() {
        imageView.setImageRotateBitmapResetBase(rotateBitmap, true);
        if (imageView.getScale() == 1F) {
            imageView.center(true, true);
        }
        new CropImageDialog.Cropper().crop();
    }

    private class Cropper {

        private void makeDefault() {
            if (rotateBitmap == null) {
                return;
            }

            HighlightView hv = new HighlightView(imageView);
            final int width = rotateBitmap.getWidth();
            final int height = rotateBitmap.getHeight();

            Rect imageRect = new Rect(0, 0, width, height);

            // Make the default size about 4/5 of the width or height
            int cropWidth = Math.min(width, height) * 4 / 5;
            @SuppressWarnings("SuspiciousNameCombination")
            int cropHeight = cropWidth;

            int x = (width - cropWidth) / 2;
            int y = (height - cropHeight) / 2;

            RectF cropRect = cropArgs.manualCropPage() ? new RectF(x, y, x + cropWidth, y + cropHeight) : new RectF(imageRect);
            hv.setup(imageView.getUnrotatedMatrix(), imageRect, cropRect, false, cropArgs);
            imageView.add(hv);
        }

        public void crop() {
            handler.post(new Runnable() {
                public void run() {
                    makeDefault();
                    imageView.invalidate();
                    if (imageView.highlightViews.size() == 1) {
                        cropView = imageView.highlightViews.get(0);
                        cropView.setFocus(true);
                    }
                }
            });
        }
    }

    /*
     * TODO
     * This should use the decode/crop/encode single step API so that the whole
     * (possibly large) Bitmap doesn't need to be read into memory
     */
    private void onSaveClicked() {
        if (cropView == null) {
            return;
        }
        cropArgs.selectionRect = new Rect(cropView.getScaledCropRect(sampleSize).left,
                cropView.getScaledCropRect(sampleSize).top,
                cropView.getScaledCropRect(sampleSize).right,
                cropView.getScaledCropRect(sampleSize).bottom);
        cropArgs.pointMatrixList = cropView.getPointMatrix();
        if (callback != null) {
            callback.onSelectionFinished(cropArgs);
        }
        dismiss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_UP ||
                keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                event.startTracking();
                if (event.getRepeatCount() == 0) {
                    shortPress = true;
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_UP ||
                keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (shortPress) {
                int edge;
                switch (keyCode){
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        edge = HighlightView.GROW_LEFT_EDGE;
                        cropView.setMotionEdge(edge);
                        cropView.handleMotion(edge, -10, 0, 0, 0, false);
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        edge = HighlightView.GROW_RIGHT_EDGE;
                        cropView.setMotionEdge(edge);
                        cropView.handleMotion(edge, 10, 0, 0, 0, false);
                        break;
                    case KeyEvent.KEYCODE_DPAD_UP:
                        edge = HighlightView.GROW_TOP_EDGE;
                        cropView.setMotionEdge(edge);
                        cropView.handleMotion(edge, 0, -10, 0, 0, false);
                        break;
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        edge = HighlightView.GROW_BOTTOM_EDGE;
                        cropView.setMotionEdge(edge);
                        cropView.handleMotion(edge, 0, 10, 0, 0, false);
                        break;
                    default:
                        edge = HighlightView.GROW_NONE;
                }
                imageView.ensureVisible(cropView);
            } else {
                //Don't handle longpress here, because the user will have to get his finger back up first
            }
            shortPress = false;
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_UP ||
                keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            shortPress = false;
            int edge;
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    edge = HighlightView.GROW_LEFT_EDGE;
                    cropView.setMotionEdge(edge);
                    cropView.handleMotion(edge, 30, 0, 0, 0, false);
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    edge = HighlightView.GROW_RIGHT_EDGE;
                    cropView.setMotionEdge(edge);
                    cropView.handleMotion(edge, -30, 0, 0, 0, false);
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    edge = HighlightView.GROW_TOP_EDGE;
                    cropView.setMotionEdge(edge);
                    cropView.handleMotion(edge, 0, 30, 0, 0, false);
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    edge = HighlightView.GROW_BOTTOM_EDGE;
                    cropView.setMotionEdge(edge);
                    cropView.handleMotion(edge, 0, -30, 0, 0, false);
                    break;
                default:
                    edge = HighlightView.GROW_NONE;
            }
            imageView.ensureVisible(cropView);
            return true;
        }
        return false;
    }
}
