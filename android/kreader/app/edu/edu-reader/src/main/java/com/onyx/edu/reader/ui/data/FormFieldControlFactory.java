package com.onyx.edu.reader.ui.data;

import android.graphics.Color;
import android.graphics.RectF;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.onyx.android.sdk.reader.api.ReaderFormCheckbox;
import com.onyx.android.sdk.reader.api.ReaderFormField;
import com.onyx.android.sdk.reader.api.ReaderFormPushButton;
import com.onyx.android.sdk.reader.api.ReaderFormRadioButton;
import com.onyx.android.sdk.reader.api.ReaderFormRadioGroup;
import com.onyx.android.sdk.reader.api.ReaderFormScribble;
import com.onyx.android.sdk.reader.api.ReaderFormText;
import com.onyx.android.sdk.ui.view.AutoFitButton;
import com.onyx.android.sdk.ui.view.RelativeRadioGroup;
import com.onyx.edu.reader.R;

/**
 * Created by joy on 5/25/17.
 */

public class FormFieldControlFactory {
    private static View createEditInput(RelativeLayout parentView, ReaderFormText textField) {
        EditText editText = new EditText(parentView.getContext());
        editText.setBackgroundColor(Color.LTGRAY);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)textField.getRect().width(),
                (int)textField.getRect().height());
        params.leftMargin = (int)textField.getRect().left;
        params.topMargin = (int)textField.getRect().top;

        editText.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        editText.setTextSize(18.0f);
        editText.setLayoutParams(params);
        return editText;
    }

    private static CheckBox createCheckBox(RelativeLayout parentView, ReaderFormCheckbox checkboxField) {
        CheckBox checkBox = new CheckBox(parentView.getContext());
        checkBox.setBackgroundColor(Color.TRANSPARENT);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)checkboxField.getRect().width() + 10,
                (int)checkboxField.getRect().height());
        params.leftMargin = (int)checkboxField.getRect().left;
        params.topMargin = (int)checkboxField.getRect().top;

        checkBox.setLayoutParams(params);
        return checkBox;
    }

    private static RelativeRadioGroup createRadioGroup(RelativeLayout parentView, ReaderFormRadioGroup groupField) {
        if (groupField.getButtons().size() <= 0) {
            return null;
        }

        RectF bound = new RectF(groupField.getButtons().get(0).getRect());
        for (ReaderFormRadioButton button : groupField.getButtons()) {
            bound.union(button.getRect());
        }

        RelativeRadioGroup radioGroup = new RelativeRadioGroup(parentView.getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = (int)bound.left;
        params.topMargin = (int)bound.top;

        int index = 0;
        for (ReaderFormRadioButton buttonField : groupField.getButtons()) {
            RadioButton button = new RadioButton(parentView.getContext());
            button.setBackgroundColor(Color.TRANSPARENT);
            RelativeRadioGroup.LayoutParams buttonParams = new RelativeRadioGroup.LayoutParams((int) buttonField.getRect().width() + 10, (int) buttonField.getRect().height() + 10);
            button.setTag(buttonField);
            buttonParams.leftMargin = (int) buttonField.getRect().left - (int)bound.left;
            buttonParams.topMargin = (int) buttonField.getRect().top - (int)bound.top;
            radioGroup.addView(button, buttonParams);
            button.setId(index);
            index++;
        }

        radioGroup.setLayoutParams(params);
        return radioGroup;
    }

    private static View createScribbleRegion(RelativeLayout parentView, ReaderFormScribble scribbleField) {
        SurfaceView view = new SurfaceView(parentView.getContext());
        view.setBackgroundColor(Color.LTGRAY);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)scribbleField.getRect().width(),
                (int)scribbleField.getRect().height());
        params.leftMargin = (int)scribbleField.getRect().left;
        params.topMargin = (int)scribbleField.getRect().top;

        view.setLayoutParams(params);
        return view;
    }

    private static View createFormButton(RelativeLayout parentView, ReaderFormPushButton buttonField) {
        Button button = new AutoFitButton(parentView.getContext());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)buttonField.getRect().width(),
                (int)buttonField.getRect().height());
        params.leftMargin = (int)buttonField.getRect().left;
        params.topMargin = (int)buttonField.getRect().top;
        button.setText(buttonField.getCaption());
        button.setBackgroundResource(R.drawable.fillet_view_bg);

        button.setLayoutParams(params);
        return button;
    }

    public static View createFormControl(RelativeLayout parentView, ReaderFormField field) {
        View view;
        if (field instanceof ReaderFormText) {
            view = createEditInput(parentView, (ReaderFormText)field);
        } else if (field instanceof ReaderFormCheckbox) {
            view = createCheckBox(parentView, (ReaderFormCheckbox)field);
        } else if (field instanceof ReaderFormRadioGroup) {
            view = createRadioGroup(parentView, (ReaderFormRadioGroup)field);
        } else if (field instanceof ReaderFormScribble) {
            view = createScribbleRegion(parentView, (ReaderFormScribble)field);
        } else if (field instanceof ReaderFormPushButton) {
            view = createFormButton(parentView, (ReaderFormPushButton)field);
        } else {
            view = null;
        }
        if (view != null) {
            view.setTag(field);
        }
        return view;
    }
}
