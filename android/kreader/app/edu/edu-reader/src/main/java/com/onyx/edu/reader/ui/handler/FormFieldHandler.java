package com.onyx.edu.reader.ui.handler;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.onyx.android.sdk.reader.api.ReaderFormField;
import com.onyx.android.sdk.scribble.formshape.FormValue;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;
import com.onyx.edu.reader.R;
import com.onyx.edu.reader.note.actions.FlushFormShapesAction;
import com.onyx.edu.reader.note.data.ReaderShapeFactory;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
import com.onyx.edu.reader.note.model.ReaderNoteDataProvider;
import com.onyx.edu.reader.ui.actions.ShowReaderMenuAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 2017/6/5.
 */

public class FormFieldHandler extends ReadingHandler {

    public List<View> formFieldControls;

    public FormFieldHandler(HandlerManager parent) {
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
        if (initialState == null) {
            return;
        }
        this.formFieldControls = initialState.formFieldControls;
        handleFormFieldControls();
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
        }else if (view instanceof RadioGroup) {
            processRadioGroupForm((RadioGroup) view);
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
                flushFormShapes(field, ReaderShapeFactory.SHAPE_FORM_MULTIPLE_SELECTION,  value);
            }
        });
        ReaderFormShapeModel formShapeModel = ReaderNoteDataProvider.loadFormShape(getContext(), getDocumentUniqueId(), field.getName());
        if (formShapeModel != null) {
            FormValue value = formShapeModel.getFormValue();
            checkBox.setChecked(value.isCheck());
        }
    }

    private void flushFormShapes(ReaderFormField field, int formType, FormValue value) {
        Shape shape = ReaderShapeFactory.createFormShape(getDocumentUniqueId(),
                getReaderDataHolder().getFirstPageInfo(),
                field.getName(),
                formType,
                field.getRect(),
                value);
        List<Shape> shapes = new ArrayList<>();
        shapes.add(shape);
        new FlushFormShapesAction(shapes).execute(getReaderDataHolder(), null);
    }

    private ReaderFormField getReaderFormField(View view) {
        return (ReaderFormField) view.getTag();
    }

    private void processRadioGroupForm(RadioGroup radioGroup) {
        final ReaderFormField field = getReaderFormField(radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                FormValue value = FormValue.create(checkedId);
                flushFormShapes(field, ReaderShapeFactory.SHAPE_FORM_SINGLE_SELECTION,  value);
            }
        });
        ReaderFormShapeModel formShapeModel = ReaderNoteDataProvider.loadFormShape(getContext(), getDocumentUniqueId(), field.getName());
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
                flushFormShapes(field, ReaderShapeFactory.SHAPE_FORM_FILL,  value);
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

}

