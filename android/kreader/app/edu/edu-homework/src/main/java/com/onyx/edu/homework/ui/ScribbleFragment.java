package com.onyx.edu.homework.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Question;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.homework.DataBundle;
import com.onyx.edu.homework.R;
import com.onyx.edu.homework.action.note.ChangePenStateAction;
import com.onyx.edu.homework.action.note.DocumentCheckAction;
import com.onyx.edu.homework.action.note.DocumentFlushAction;
import com.onyx.edu.homework.action.note.DocumentOpenAction;
import com.onyx.edu.homework.action.note.DocumentSaveAction;
import com.onyx.edu.homework.action.note.RemoveByPointListAction;
import com.onyx.edu.homework.base.BaseFragment;
import com.onyx.edu.homework.data.Constant;
import com.onyx.edu.homework.databinding.FragmentScribbleBinding;
import com.onyx.edu.homework.event.DoneAnswerEvent;
import com.onyx.edu.homework.event.RequestFinishedEvent;
import com.onyx.edu.homework.event.ResumeNoteEvent;
import com.onyx.edu.homework.event.SaveNoteEvent;
import com.onyx.edu.homework.event.StopNoteEvent;
import com.onyx.edu.homework.event.UpdatePagePositionEvent;
import com.onyx.edu.homework.receiver.DeviceReceiver;
import com.onyx.edu.homework.request.DocumentCheckRequest;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxm on 2017/12/6.
 */

public class ScribbleFragment extends BaseFragment {

    private static final String TAG = "ScribbleFragment";

    private FragmentScribbleBinding binding;
    private DeviceReceiver deviceReceiver = new DeviceReceiver();
    private boolean fullUpdate = false;
    private TouchPoint erasePoint = null;
    private SurfaceHolder.Callback surfaceCallback;
    private Question question;

    public static ScribbleFragment newInstance(Question question) {
        ScribbleFragment fragment = new ScribbleFragment();
        fragment.setQuestion(question);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBundle.getInstance().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scribble, container, false);
        registerDeviceReceiver();
        initSurfaceView();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        DataBundle.getInstance().unregister(this);
        unregisterDeviceReceiver();
        changePenState(false, false, null);
        super.onDestroy();
    }

    private void initSurfaceView() {
        if (surfaceCallback == null) {
            surfaceCallback = new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    clearSurfaceView();
                    getNoteViewHelper().setView(getActivity(), getScribbleView(), inputCallback());
                    getNoteViewHelper().setEnableTouchEvent(getDataBundle().isDoing());
                    String homeworkId = DataBundle.getInstance().getHomeworkId();
                    checkDocument(question.getUniqueId(), homeworkId, question.getQuestionId());
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
        binding.scribbleView.getHolder().addCallback(surfaceCallback);
    }

    private void checkDocument(final String uniqueId, final String parentUniqueId, final String groupId) {
        new DocumentCheckAction(uniqueId, parentUniqueId).execute(getNoteViewHelper(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                DocumentCheckRequest checkRequest = (DocumentCheckRequest) request;
                openDocument(uniqueId, parentUniqueId, groupId, !checkRequest.isHasNote());
            }
        });
    }

    private void openDocument(final String uniqueId, final String parentUniqueId, final String groupId, boolean create) {
        new DocumentOpenAction(uniqueId, parentUniqueId, groupId, create).execute(getNoteViewHelper(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                changePenState(shouldResume(), false, null);
            }
        });
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public NoteViewHelper getNoteViewHelper() {
        return getDataBundle().getNoteViewHelper();
    }

    private NoteViewHelper.InputCallback inputCallback() {
        return new NoteViewHelper.InputCallback() {
            @Override
            public void onBeginRawData() {

            }

            @Override
            public void onRawTouchPointListReceived(final Shape shape, TouchPointList pointList) {

            }

            @Override
            public void onBeginErasing() {
                ScribbleFragment.this.onBeginErasing();
            }

            @Override
            public void onErasing(final MotionEvent touchPoint) {
                ScribbleFragment.this.onErasing(touchPoint);
            }

            @Override
            public void onEraseTouchPointListReceived(TouchPointList pointList) {
                ScribbleFragment.this.onFinishErasing(pointList);
            }

            @Override
            public void onBeginShapeSelect() {

            }

            @Override
            public void onShapeSelecting(MotionEvent motionEvent) {

            }

            @Override
            public void onShapeSelectTouchPointListReceived(TouchPointList pointList) {

            }

            @Override
            public void onDrawingTouchDown(final MotionEvent motionEvent, final Shape shape) {
                if (!shape.supportDFB()) {
                    drawPage();
                }
            }

            @Override
            public void onDrawingTouchMove(final MotionEvent motionEvent, final Shape shape, boolean last) {
                if (last && !shape.supportDFB()) {
                    drawPage();
                }
            }

            @Override
            public void onDrawingTouchUp(final MotionEvent motionEvent, final Shape shape) {
                if (!shape.supportDFB()) {
                    drawPage();
                }
            }

        };
    }

    @Subscribe
    public void onRequestFinishedEvent(RequestFinishedEvent event) {
        updateDataInfo(event.request);
        if (event.request.isAbort()) {
            return;
        }
        if (event.updatePage) {
            drawPage();
        }
    }

    protected void updateDataInfo(final BaseNoteRequest request) {
        getNoteViewHelper().setShapeDataInfo(request.getShapeDataInfo());
        int currentVisualPageIndex = getShapeDataInfo().getCurrentPageIndex() + 1;
        //TODO:avoid change shapedatainfo structure,simple detect here.
        int totalPageCount = getShapeDataInfo().getPageCount() == 0 ? 1 : getShapeDataInfo().getPageCount();
        DataBundle.getInstance().post(new UpdatePagePositionEvent(currentVisualPageIndex + File.separator + totalPageCount));
    }

    public void drawPage() {
        Rect rect = getViewportSize();
        Canvas canvas = beforeDraw(rect);
        if (canvas == null) {
            resetFullUpdate();
            return;
        }

        Paint paint = new Paint();
        cleanup(canvas, paint, rect);
        drawContent(canvas, paint);
        drawStashShape(canvas, paint);
        afterDraw(canvas);
    }

    private void drawContent(final Canvas canvas, final Paint paint) {
        Bitmap bitmap = getNoteViewHelper().getViewBitmap();
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }
    }

    private void drawStashShape(final Canvas canvas, final Paint paint) {
        final RenderContext renderContext = RenderContext.create(canvas, paint, null);
        final List<Shape> stash = new ArrayList<>();
        //TODO:use add all to avoid dirty stash get detach when iterating.
        stash.addAll(getNoteViewHelper().getDirtyStash());
        for (Shape shape : stash) {
            shape.render(renderContext);
        }
    }

    private void clearSurfaceView() {
        Rect rect = getViewportSize();
        Canvas canvas = beforeDraw(rect);
        if (canvas == null) {
            return;
        }

        Paint paint = new Paint();
        cleanup(canvas, paint, rect);
        afterDraw(canvas);
    }

    private void cleanup(final Canvas canvas, final Paint paint, final Rect rect) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(rect, paint);
    }

    private Rect getViewportSize() {
        return new Rect(0, 0, getScribbleView().getWidth(), getScribbleView().getHeight());
    }

    private Canvas beforeDraw(final Rect rect) {
        if (isFullUpdate()) {
            EpdController.setViewDefaultUpdateMode(getScribbleView(), UpdateMode.GC);
        } else {
            EpdController.resetUpdateMode(getScribbleView());
        }
        return getScribbleView().getHolder().lockCanvas(rect);
    }

    private void afterDraw(final Canvas canvas) {
        getScribbleView().getHolder().unlockCanvasAndPost(canvas);
        resetFullUpdate();
    }

    public boolean isFullUpdate() {
        return fullUpdate;
    }

    public void resetFullUpdate() {
        this.fullUpdate = false;
    }

    private SurfaceView getScribbleView() {
        return binding.scribbleView;
    }

    protected void onBeginErasing() {
        flushDocument(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                erasePoint = new TouchPoint();
            }
        });
    }

    protected void onErasing(final MotionEvent touchPoint) {
        if (erasePoint == null) {
            return;
        }
        erasePoint.x = touchPoint.getX();
        erasePoint.y = touchPoint.getY();
    }

    protected void onFinishErasing(TouchPointList pointList) {
        erasePoint = null;
        final List<Shape> stash = getNoteViewHelper().detachStash();
        RemoveByPointListAction removeByPointListAction = new RemoveByPointListAction(pointList, stash, binding.scribbleView);
        removeByPointListAction.execute(getNoteViewHelper(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                drawPage();
            }
        });
    }

    public void flushDocument(boolean render,
                                 boolean resume,
                                 final BaseCallback callback) {
        final List<Shape> stash = getNoteViewHelper().detachStash();
        final DocumentFlushAction action = new DocumentFlushAction(stash,
                render,
                resume,
                getShapeDataInfo().getDrawingArgs());
        action.execute(getNoteViewHelper(), callback);
    }

    public void changePenState(boolean resume, boolean render, BaseCallback callback) {
        new ChangePenStateAction(resume, render).execute(getNoteViewHelper(), callback);
    }

    private void registerDeviceReceiver() {
        deviceReceiver.setSystemUIChangeListener(new DeviceReceiver.SystemUIChangeListener() {
            @Override
            public void onSystemUIChanged(String type, boolean open) {
                if (!isVisible()) {
                    return;
                }
                if (open) {
                    onSystemUIOpened();
                } else {
                    onSystemUIClosed();
                }
            }

            @Override
            public void onHomeClicked() {
                getNoteViewHelper().enableScreenPost(true);
                getActivity().finish();
            }

            @Override
            public void onScreenShot(Intent intent, boolean end) {

            }
        });
        deviceReceiver.registerReceiver(getActivity());
    }

    protected void unregisterDeviceReceiver() {
        deviceReceiver.unregisterReceiver(getActivity());
    }

    protected void onSystemUIOpened() {
        if (isRunning()) {
            flushDocument(true, false, null);
        }
    }

    protected void onSystemUIClosed() {
        if (isRunning()) {
            flushDocument(true, shouldResume(), null);
        }
    }

    @Subscribe
    public void onSaveNoteEvent(SaveNoteEvent event) {
        saveDocument(event.finishAfterSave, shouldResume(), true, true, null);
    }

    @Subscribe
    public void onStopNoteEvent(StopNoteEvent event) {
        saveDocument(event.finishAfterSave, false, true,false, null);
    }

    @Subscribe
    public void onResumeNoteEvent(ResumeNoteEvent event) {
        flushDocument(event.render, shouldResume(), null);
    }

    public void saveDocument(final boolean finishAfterSave,
                             final boolean resumeDrawing,
                             final boolean render,
                             final boolean showLoading,
                             final BaseCallback callback) {
        if (!getDataBundle().isDoing()) {
            return;
        }
        getNoteViewHelper().flushTouchPointList();
        flushDocument(render, resumeDrawing, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                saveDocumentImpl(finishAfterSave, resumeDrawing, showLoading, callback);
            }
        });
    }

    private void saveDocumentImpl(final boolean finishAfterSave,
                              final boolean resumeDrawing,
                              boolean showLoading,
                              final BaseCallback callback) {
        String documentUniqueId = getShapeDataInfo().getDocumentUniqueId();
        if (StringUtils.isNullOrEmpty(documentUniqueId)) {
            return;
        }
        final DocumentSaveAction saveAction = new
                DocumentSaveAction(getActivity(),
                documentUniqueId,
                Constant.NOTE_TITLE,
                finishAfterSave,
                resumeDrawing,
                showLoading);
        saveAction.execute(getNoteViewHelper(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                DataBundle.getInstance().post(new DoneAnswerEvent(question));
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

    public ShapeDataInfo getShapeDataInfo() {
        return getNoteViewHelper().getShapeDataInfo();
    }

    public DataBundle getDataBundle() {
        return DataBundle.getInstance();
    }

    public boolean shouldResume() {
        return !getNoteViewHelper().inUserErasing()
                && ShapeFactory.isDFBShape(getShapeDataInfo().getCurrentShapeType())
                && getDataBundle().isDoing()
                && isRunning();
    }
}
