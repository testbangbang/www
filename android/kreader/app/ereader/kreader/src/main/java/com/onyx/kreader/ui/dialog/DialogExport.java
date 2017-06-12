package com.onyx.kreader.ui.dialog;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.ui.dialog.OnyxBaseDialog;
import com.onyx.android.sdk.ui.view.DynamicMultiRadioGroupView;
import com.onyx.kreader.R;
import com.onyx.kreader.ui.requests.ExportNotesRequest;
import com.onyx.kreader.ui.actions.ExportNotesActionChain;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.android.sdk.utils.ExportUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 16/9/26.
 */
public class DialogExport extends OnyxBaseDialog implements CompoundButton.OnCheckedChangeListener {

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
    @Bind(R.id.export_location)
    TextView exportLocation;

    private ReaderDataHolder readerDataHolder;

    public DialogExport(ReaderDataHolder readerDataHolder) {
        super(readerDataHolder.getContext(), R.style.dialog_no_title);

        setContentView(R.layout.dialog_export);
        ButterKnife.bind(this);
        initBrushStrokeColor();
        setupListener();

        this.readerDataHolder = readerDataHolder;
        annotationCheckbox.setChecked(SingletonSharedPreference.isExportWithAnnotation());
        scribbleCheckbox.setChecked(SingletonSharedPreference.isExportWithScribble());
        mergedLayout.check(SingletonSharedPreference.isExportAllPages() ? R.id.merged_all : R.id.merged_part);

        String location;
        try {
            location = String.format(getContext().getString(R.string.export_location_explain), ExportUtils.getExportPdfPath(readerDataHolder.getReader().getDocumentPath()));
        }catch (IOException e){
            location =  getContext().getString(R.string.fail_create_export_directory) + e.getMessage();
            e.printStackTrace();
        }
        exportLocation.setText(location);
    }

    private void initBrushStrokeColor() {
        int[] colorStrIds = {R.string.Original, R.string.Red, R.string.Black, R.string.Green, R.string.White, R.string.Blue};
        ColorAdapter colorAdapter = new ColorAdapter(getContext(), colorStrIds);
        colorGroup.setMultiAdapter(colorAdapter);
        colorAdapter.setItemChecked(true, SingletonSharedPreference.getExportScribbleColor().ordinal());
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
                    SingletonSharedPreference.setExportScribbleColor(ExportNotesRequest.BrushColor.values()[position]);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ExportNotesActionChain(SingletonSharedPreference.isExportWithScribble(),
                        SingletonSharedPreference.isExportWithAnnotation()).execute(readerDataHolder, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        String text = getContext().getString(e == null ? R.string.export_success : R.string.export_fail);
                        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                        if (e == null) {
                            dismiss();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.equals(annotationCheckbox)) {
            SingletonSharedPreference.setExportWithAnnotation(isChecked);
        } else if (buttonView.equals(scribbleCheckbox)) {
            SingletonSharedPreference.setExportWithScribble(isChecked);
        } else if (buttonView.equals(mergedAll)) {
            SingletonSharedPreference.setExportAllPages(isChecked);
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
            int margin = (int) context.getResources().getDimension(R.dimen.dialog_export_margin);
            setMargin(0, margin, 0, 0);
            float textSize = context.getResources().getDimension(R.dimen.dialog_export_text_size);
            setTextSize(textSize);
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
            int gravity;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                gravity = Gravity.LEFT | Gravity.CENTER;
            } else {
                gravity = Gravity.START | Gravity.CENTER;
            }
            button.setGravity(gravity);
            button.setTextColor(ColorStateList.valueOf(Color.BLACK));
        }
    }
}
