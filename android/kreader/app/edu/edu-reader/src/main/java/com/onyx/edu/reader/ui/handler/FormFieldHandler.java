package com.onyx.edu.reader.ui.handler;

import android.content.Context;
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
import com.onyx.edu.reader.note.actions.FlushFormShapesAction;
import com.onyx.edu.reader.note.data.ReaderShapeFactory;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
import com.onyx.edu.reader.note.model.ReaderNoteDataProvider;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 2017/6/5.
 */

public class FormFieldHandler extends BaseHandler {

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

    private String getDocumentUniqueId() {
        return getReaderDataHolder().getReader().getDocumentMd5();
    }

    private ReaderDataHolder getReaderDataHolder() {
        return getParent().getReaderDataHolder();
    }

    private Context getContext() {
        return getReaderDataHolder().getContext();
    }
}

