package com.onyx.edu.reader.ui.handler.form;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.IdRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.onyx.android.sdk.reader.api.ReaderFormField;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.formshape.FormValue;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;
import com.onyx.android.sdk.ui.view.RelativeRadioGroup;
import com.onyx.edu.reader.R;
import com.onyx.edu.reader.note.actions.FlushFormShapesAction;
import com.onyx.edu.reader.note.actions.FlushNoteAction;
import com.onyx.edu.reader.note.actions.ResumeDrawingAction;
import com.onyx.edu.reader.note.actions.StopNoteActionChain;
import com.onyx.edu.reader.note.data.ReaderShapeFactory;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
import com.onyx.edu.reader.note.model.ReaderNoteDataProvider;
import com.onyx.edu.reader.note.request.StartNoteRequest;
import com.onyx.edu.reader.ui.actions.ShowReaderMenuAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.handler.HandlerManager;
import com.onyx.edu.reader.ui.handler.ReadingHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 2017/6/5.
 */

public class FormBaseHandler extends ReadingHandler {

    public List<View> formFieldControls;

    public FormBaseHandler(HandlerManager parent) {
        super(parent);
    }

    public static HandlerInitialState createInitialState(List<View> formFieldControls) {
        HandlerInitialState initialState =  new HandlerInitialState();
        initialState.formFieldControls = formFieldControls;
        return initialState;
    }

    @Override
    public void onActivate(ReaderDataHolder readerDataHolder, HandlerInitialState initialState) {
        super.onActivate(readerDataHolder, initialState);
        if (initialState != null) {
            this.formFieldControls = initialState.formFieldControls;
            handleFormFieldControls();
        }
        if (readerDataHolder.hasScribbleFormField() && !readerDataHolder.hasDialogShowing()) {
            startNoteDrawing(readerDataHolder);
        }
    }

    public void onDeactivate(final ReaderDataHolder readerDataHolder) {
        if (!isEnableNoteDrawing()) {
            return;
        }
        StopNoteActionChain stopNoteActionChain = new StopNoteActionChain(true, true, true, false, false, false);
        stopNoteActionChain.execute(readerDataHolder, null);
        setEnableNoteDrawing(false);
    }

    private void startNoteDrawing(final ReaderDataHolder readerDataHolder) {
        final StartNoteRequest request = new StartNoteRequest(readerDataHolder.getVisiblePages());
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), request, null);
        setEnableNoteDrawing(true);
    }

    private void handleFormFieldControls() {
        if (formFieldControls == null) {
            return;
        }
        for (View formFieldControl : formFieldControls) {
            processFormField(formFieldControl);
        }
    }

    private void processFormField(View view) {
        if (view == null) {
            return;
        }
        if (view instanceof CheckBox) {
            processCheckBoxForm((CheckBox) view);
        }else if (view instanceof RelativeRadioGroup) {
            processRadioGroupForm((RelativeRadioGroup) view);
        }else if (view instanceof EditText) {
            processEditTextForm((EditText) view);
        }
    }

    private void processCheckBoxForm(CheckBox checkBox) {
        final ReaderFormField field = getReaderFormField(checkBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FormValue value = FormValue.create(isChecked);
                flushFormShapes(field.getName(), field.getRect(), ReaderShapeFactory.SHAPE_FORM_MULTIPLE_SELECTION,  value);
            }
        });
        ReaderFormShapeModel formShapeModel = ReaderNoteDataProvider.loadFormShape(getContext(), getDocumentUniqueId(), field.getName());
        if (formShapeModel != null) {
            FormValue value = formShapeModel.getFormValue();
            checkBox.setChecked(value.isCheck());
        }
    }

    private void flushFormShapes(String fieldId, RectF formRect, int formType, FormValue value) {
        Shape shape = ReaderShapeFactory.createFormShape(getDocumentUniqueId(),
                getReaderDataHolder().getFirstPageInfo(),
                fieldId,
                formType,
                formRect,
                value);
        shape.setPageUniqueId(getReaderDataHolder().getFirstPageInfo().getName());
        List<Shape> shapes = new ArrayList<>();
        shapes.add(shape);
        getReaderDataHolder().onFormFieldSelected(fieldId, value);
        new FlushFormShapesAction(shapes, isEnableNoteDrawing()).execute(getReaderDataHolder(), null);
    }

    private ReaderFormField getReaderFormField(View view) {
        return (ReaderFormField) view.getTag();
    }

    private void processRadioGroupForm(final RelativeRadioGroup radioGroup) {
        final ReaderFormField groupField = getReaderFormField(radioGroup);
        radioGroup.setOnCheckedChangeListener(new RelativeRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RelativeRadioGroup group, @IdRes int checkedId) {
                FormValue value = FormValue.create(checkedId);
                value.setCheck(true);
                View view = radioGroup.findViewById(checkedId);
                ReaderFormField field = getReaderFormField(view);
                flushFormShapes(groupField.getName(), field.getRect(), ReaderShapeFactory.SHAPE_FORM_SINGLE_SELECTION,  value);
            }
        });

        ReaderFormShapeModel formShapeModel = ReaderNoteDataProvider.loadFormShape(getContext(), getDocumentUniqueId(), groupField.getName());
        if (formShapeModel != null) {
            FormValue value = formShapeModel.getFormValue();
            radioGroup.check(value.getIndex());
        }

    }

    private void processEditTextForm(final EditText editText) {
        final ReaderFormField field = getReaderFormField(editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                FormValue value = FormValue.create(s.toString());
                flushFormShapes(field.getName(), field.getRect(), ReaderShapeFactory.SHAPE_FORM_FILL,  value);
            }
        });
        ReaderFormShapeModel formShapeModel = ReaderNoteDataProvider.loadFormShape(getContext(), getDocumentUniqueId(), field.getName());
        if (formShapeModel != null) {
            FormValue value = formShapeModel.getFormValue();
            editText.setText(value.getText());
        }
    }

    protected String getDocumentUniqueId() {
        return getReaderDataHolder().getReader().getDocumentMd5();
    }

    protected ReaderDataHolder getReaderDataHolder() {
        return getParent().getReaderDataHolder();
    }

    protected Context getContext() {
        return getReaderDataHolder().getContext();
    }

    @Override
    public void close(ReaderDataHolder readerDataHolder) {
        if (ensurePushedFormData()) {
            super.close(readerDataHolder);
        }
    }

    private boolean ensurePushedFormData() {
        if (ReaderNoteDataProvider.hasUnLockFormShapes(getReaderDataHolder().getContext(),
                getReaderDataHolder().getReader().getDocumentMd5(),
                false)) {
            pushFormData();
            return false;
        }
        return true;
    }

    private void pushFormData() {
        getReaderDataHolder().postDialogUiChangedEvent(true);
        OnyxCustomDialog.getConfirmDialog(getReaderDataHolder().getContext(), getReaderDataHolder().getContext().getString(R.string.exit_submit_form_tips),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ShowReaderMenuAction.preparePushFormData(getReaderDataHolder());
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        postQuitEvent(getReaderDataHolder());
                    }
                })
                .setOnCloseListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (getReaderDataHolder().inNoteWritingProvider()) {
                            getReaderDataHolder().postDialogUiChangedEvent(false);
                        }
                    }
                })
                .setCloseOnTouchOutside(true)
                .setNegativeText(R.string.custom_dialog_exit)
                .setPositiveText(R.string.custom_dialog_submit)
                .show();

    }


    public boolean onScroll(ReaderDataHolder readerDataHolder, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    public boolean onSingleTapUp(ReaderDataHolder readerDataHolder, MotionEvent e) {
        if (!isEnableNoteDrawing()) {
            return super.onSingleTapUp(readerDataHolder, e);
        }
        return readerDataHolder.getNoteManager().inScribbleRect(TouchPoint.create(e));
    }

    public boolean onScaleEnd(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
        return false;
    }

    public boolean onScaleBegin(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
        return false;
    }

    public boolean onScale(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector)  {
        return false;
    }

    public boolean onActionUp(ReaderDataHolder readerDataHolder, final float startX, final float startY, final float endX, final float endY) {
        return false;
    }

    @Override
    public boolean onKeyDown(ReaderDataHolder readerDataHolder, int keyCode, KeyEvent event) {
        if (!isEnableNoteDrawing()) {
            return false;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                return false;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                return false;
            case KeyEvent.KEYCODE_DPAD_UP:
                return false;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                return false;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                return false;
            case KeyEvent.KEYCODE_MENU:
                return true;
            case KEYCDOE_ERASE:
            case KEYCDOE_ERASE_KK:
            case KeyEvent.KEYCODE_ALT_LEFT:
            case KeyEvent.KEYCODE_ALT_RIGHT:
                if (isEnableBigPen()) {
                    getParent().setActiveProvider(HandlerManager.ERASER_PROVIDER);
                }
                return false;
            case KEYCDOE_SCRIBE:
            case KEYCDOE_SCRIBE_KK:
                return false;
            default:
                return super.onKeyDown(readerDataHolder,keyCode,event);
        }
    }

    private boolean isEnableBigPen() {
        return true;
    }

    @Override
    public boolean onTouchEvent(ReaderDataHolder readerDataHolder, MotionEvent e) {
        if (!isEnableNoteDrawing()) {
            return true;
        }
        if (e.getPointerCount() > 1) {
            return false;
        }
        return readerDataHolder.getNoteManager().getNoteEventProcessorManager().onTouchEvent(e);
    }

    public void afterChangePosition(final ReaderDataHolder readerDataHolder) {
        if (!isEnableNoteDrawing()) {
            return;
        }
        final ResumeDrawingAction action = new ResumeDrawingAction(readerDataHolder.getVisiblePages());
        action.execute(readerDataHolder, null);
    }

    @Override
    public void beforeChangePosition(ReaderDataHolder readerDataHolder) {
        if (!isEnableNoteDrawing()) {
            return;
        }
        final FlushNoteAction flushNoteAction = new FlushNoteAction(readerDataHolder.getVisiblePages(), true, true, true, false);
        flushNoteAction.execute(getParent().getReaderDataHolder(), null);
    }

}
