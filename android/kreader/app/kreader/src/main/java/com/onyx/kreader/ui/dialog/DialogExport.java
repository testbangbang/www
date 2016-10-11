package com.onyx.kreader.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.ui.view.DynamicMultiRadioGroupView;
import com.onyx.kreader.R;
import com.onyx.kreader.ui.actions.ExportNotesAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 16/9/26.
 */
public class DialogExport extends Dialog implements CompoundButton.OnCheckedChangeListener {


    @Bind(R.id.annotation_checkbox)
    CheckBox annotationCheckbox;
    @Bind(R.id.scribble_checkbox)
    CheckBox scribbleCheckbox;
    @Bind(R.id.merged_all)
    RadioButton mergedAll;
    @Bind(R.id.merged_part)
    RadioButton mergedPart;
    @Bind(R.id.color_group)
    DynamicMultiRadioGroupView colorGroup;
    @Bind(R.id.btn_cancel)
    Button btnCancel;
    @Bind(R.id.btn_ok)
    Button btnOk;
    @Bind(R.id.merged_layout)
    RadioGroup mergedLayout;

    private ReaderDataHolder readerDataHolder;
    private boolean annotationMerge = true;
    private boolean scribbleMerge = true;
    private boolean isMergedAll = true;
    private BrushColor brushColor = BrushColor.Original;

    private enum BrushColor {Original, Red, Black, Green, White, Blue}

    public DialogExport(ReaderDataHolder readerDataHolder) {
        super(readerDataHolder.getContext(), R.style.dialog_no_title);

        setContentView(R.layout.dialog_export);
        ButterKnife.bind(this);
        initBrushStrokeColor();
        setupListener();

        this.readerDataHolder = readerDataHolder;
        annotationCheckbox.setChecked(annotationMerge);
        scribbleCheckbox.setChecked(scribbleMerge);
        mergedLayout.check(isMergedAll ? R.id.merged_all : R.id.merged_part);
    }

    private void initBrushStrokeColor() {
        int[] colorStrIds = {R.string.Original, R.string.Red, R.string.Black, R.string.Green, R.string.White, R.string.Blue};
        ColorAdapter colorAdapter = new ColorAdapter(getContext(), colorStrIds);
        colorGroup.setMultiAdapter(colorAdapter);
        colorAdapter.setItemChecked(true, brushColor.ordinal());
    }

    private void setupListener() {
        annotationCheckbox.setOnCheckedChangeListener(this);
        scribbleCheckbox.setOnCheckedChangeListener(this);
        mergedAll.setOnCheckedChangeListener(this);
        mergedPart.setOnCheckedChangeListener(this);

        colorGroup.setOnCheckedChangeListener(new DynamicMultiRadioGroupView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
                if (isChecked) {
                    brushColor = BrushColor.values()[position];
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogExport.this.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ExportNotesAction().execute(readerDataHolder, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {

                    }
                });
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.equals(annotationCheckbox)) {
            annotationMerge = isChecked;
        } else if (buttonView.equals(scribbleCheckbox)) {
            scribbleMerge = isChecked;
        } else if (buttonView.equals(mergedAll)) {
            isMergedAll = isChecked;
        }
    }

    public static class ColorAdapter extends DynamicMultiRadioGroupView.MultiAdapter {

        List<String> colorList;
        Context context;

        public ColorAdapter(Context context, int[] colorStrIds) {
            this.context = context;
            colorList = new ArrayList<>();
            for (int i = 0; i < colorStrIds.length; i++) {
                colorList.add(context.getString(colorStrIds[i]));
            }
            setButtonTexts(colorList);
            setMultiCheck(false);
        }

        @Override
        public int getRows() {
            return 3;
        }

        @Override
        public int getColumns() {
            return 2;
        }

        @Override
        public int getItemCount() {
            return colorList.size();
        }

        @Override
        public void bindView(CompoundButton button, int position) {
            button.setGravity(Gravity.START | Gravity.CENTER);
        }
    }
}
