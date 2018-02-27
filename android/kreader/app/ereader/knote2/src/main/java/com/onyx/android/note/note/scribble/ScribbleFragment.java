package com.onyx.android.note.note.scribble;

import android.databinding.DataBindingUtil;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.note.NoteDataBundle;
import com.onyx.android.note.R;
import com.onyx.android.note.action.CreateDocumentAction;
import com.onyx.android.note.common.base.BaseFragment;
import com.onyx.android.note.databinding.FragmentScribbleBinding;
import com.onyx.android.note.handler.HandlerManager;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.event.PauseRawDrawingEvent;
import com.onyx.android.sdk.scribble.data.DocumentOptionArgs;

import java.util.UUID;

/**
 * Created by lxm on 2018/2/2.
 */

public class ScribbleFragment extends BaseFragment {

    private FragmentScribbleBinding binding;
    private SurfaceHolder.Callback surfaceCallback;

    public static ScribbleFragment newInstance() {
        return new ScribbleFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scribble, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setup();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setup() {
        getNoteBundle().getHandlerManager().activeProvider(HandlerManager.EPD_SHAPE_PROVIDER);
        initSurfaceView();
    }

    private void initSurfaceView() {
        if (surfaceCallback == null) {
            surfaceCallback = new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    clearSurfaceView();
                    getNoteBundle().getNoteManager().start(binding.surfaceView);
                    createTestDocument();
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    holder.removeCallback(surfaceCallback);
                    surfaceCallback = null;
                }
            };
        }
        binding.surfaceView.setZOrderOnTop(true);
        binding.surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        binding.surfaceView.getHolder().addCallback(surfaceCallback);
    }

    private void clearSurfaceView() {
        Rect rect = getViewportSize();
        EpdController.resetUpdateMode(getScribbleView());
        Canvas canvas = getScribbleView().getHolder().lockCanvas(rect);
        if (canvas == null) {
            return;
        }

        Paint paint = new Paint();
        cleanup(canvas, paint, rect);
        getScribbleView().getHolder().unlockCanvasAndPost(canvas);
    }

    private SurfaceView getScribbleView() {
        return binding.surfaceView;
    }

    private void cleanup(final Canvas canvas, final Paint paint, final Rect rect) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    private Rect getViewportSize() {
        return new Rect(0, 0, getScribbleView().getWidth(), getScribbleView().getHeight());
    }

    private void createTestDocument() {
        String docId = UUID.randomUUID().toString();
        String parentId = UUID.randomUUID().toString();
        new CreateDocumentAction(getNoteManager())
                .setDocumentUniqueId(docId)
                .setParentUniqueId(parentId)
                .setOptionArgs(DocumentOptionArgs.create())
                .execute(null);
    }

    @Override
    public boolean onBackPressedSupport() {
        getNoteManager().post(new PauseRawDrawingEvent());
        getNoteManager().quit();
        return false;
    }

    private NoteDataBundle getNoteBundle() {
        return NoteDataBundle.getInstance();
    }

    private NoteManager getNoteManager() {
        return getNoteBundle().getNoteManager();
    }
}
