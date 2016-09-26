package com.onyx.kreader.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.onyx.android.sdk.ui.view.DynamicMultiRadioGroupView;
import com.onyx.android.sdk.utils.DimenUtils;
import com.onyx.kreader.R;
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
    @Bind(R.id.highlight_checkbox)
    CheckBox highlightCheckbox;
    @Bind(R.id.scribble_checkbox)
    CheckBox scribbleCheckbox;
    @Bind(R.id.scribble_full_document_checkbox)
    CheckBox scribbleFullDocumentCheckbox;
    @Bind(R.id.color_group)
    DynamicMultiRadioGroupView colorGroup;
    @Bind(R.id.btn_cancel)
    Button btnCancel;
    @Bind(R.id.btn_ok)
    Button btnOk;

    private boolean annotationExport = true;
    private boolean highlightExport = false;
    private boolean scribbleExport = false;
    private boolean scribbleFullDocumentExport = false;
    private BrushColor brushColor = BrushColor.Original;

    private enum BrushColor{Original, Red, Black, Green, White, Blue}

    public DialogExport(ReaderDataHolder readerDataHolder) {
        super(readerDataHolder.getContext(), R.style.dialog_no_title);

        setContentView(R.layout.dialog_export);
        ButterKnife.bind(this);
        initBrushStrokeColor();
        setupListener();

        annotationCheckbox.setChecked(annotationExport);
        highlightCheckbox.setChecked(highlightExport);
        scribbleCheckbox.setChecked(scribbleExport);
        scribbleFullDocumentCheckbox.setChecked(scribbleFullDocumentExport);
    }

    private void initBrushStrokeColor() {
        int[] colorStrIds = {R.string.Original, R.string.Red, R.string.Black, R.string.Green, R.string.White, R.string.Blue};
        ColorAdapter colorAdapter = new ColorAdapter(getContext(), colorStrIds);
        colorGroup.setMultiAdapter(colorAdapter);
        colorAdapter.setItemChecked(true, brushColor.ordinal());
    }

    private void setupListener(){
        annotationCheckbox.setOnCheckedChangeListener(this);
        highlightCheckbox.setOnCheckedChangeListener(this);
        scribbleCheckbox.setOnCheckedChangeListener(this);
        scribbleFullDocumentCheckbox.setOnCheckedChangeListener(this);

        colorGroup.setOnCheckedChangeListener(new DynamicMultiRadioGroupView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
                if (isChecked){
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
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.equals(annotationCheckbox)){
            annotationExport = isChecked;
        }else if (buttonView.equals(highlightCheckbox)){
            highlightExport = isChecked;
        }else if (buttonView.equals(scribbleCheckbox)){
            scribbleExport = isChecked;
        }else if (buttonView.equals(scribbleFullDocumentCheckbox)){
            scribbleFullDocumentExport = isChecked;
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
            int margin = DimenUtils.dip2px(context, 5);
            setMargin(0, margin, 0, margin);
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
