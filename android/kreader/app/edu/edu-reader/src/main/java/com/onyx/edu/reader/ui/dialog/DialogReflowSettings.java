package com.onyx.edu.reader.ui.dialog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.DynamicMultiRadioGroupView;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.edu.reader.R;
import com.onyx.android.sdk.reader.reflow.ImageReflowSettings;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 16/9/12.
 */
public class DialogReflowSettings extends DialogBase {

    @Bind(R.id.format_text)
    TextView formatText;
    @Bind(R.id.font_size_text)
    TextView fontSizeText;
    @Bind(R.id.font_size_layout)
    DynamicMultiRadioGroupView fontSizeLayout;
    @Bind(R.id.format_recycler)
    PageRecyclerView formatRecycler;
    @Bind(R.id.align_text)
    TextView alignText;
    @Bind(R.id.align_recycler)
    PageRecyclerView alignRecycler;
    @Bind(R.id.upgrade_text)
    TextView upgradeText;
    @Bind(R.id.upgrade_layout)
    DynamicMultiRadioGroupView upgradeLayout;
    @Bind(R.id.textview_title)
    TextView textviewTitle;
    @Bind(R.id.reset_button)
    Button resetButton;
    @Bind(R.id.confirm_button)
    Button confirmButton;
    @Bind(R.id.cancel_button)
    Button cancelButton;
    @Bind(R.id.layout_menu)
    LinearLayout layoutMenu;
    @Bind(R.id.columns_text)
    TextView columnsText;
    @Bind(R.id.columns_layout)
    DynamicMultiRadioGroupView columnsLayout;
    @Bind(R.id.direction_recycler)
    PageRecyclerView directionRecycler;

    static public abstract class ReflowCallback {
        abstract public void onFinished(boolean confirm, final ImageReflowSettings settings);
    }

    private ImageReflowSettings settings;
    private ReflowCallback callback;
    private double[] fontSizeValues = {0.75, 1.0, 1.2, 1.5};
    private int[] autoStraightenValues = {0, 5, 10};
    private int[] justificationValues = {0, 3, 2};
    private int[] columnValues = {1, 2, 3};
    private double[][] formatValues = {{1.0, 0.05}, {1.2, 0.10}, {1.4, 0.15}};

    private final String[] fontSizes = {"1", "2", "3", "4"};
    private final String[] upgradeSizes = {"0", "5", "10",};
    private final String[] columnsSize = {"1", "2", "3"};

    private int[][] directionValues = { {1, 0}, {0, 0}, {1, 90} };

    private int fontSizeDefaultIndex = 0;
    private int autoStraightenDefaultIndex = 0;
    private int justificationDefaultIndex = 1;
    private int formatDefaultIndex = 0;
    private int columnDefaultIndex = 1;
    private int directionDefaultIndex = 0;

    public DialogReflowSettings(ReaderDataHolder readerDataHolder, ImageReflowSettings settings, ReflowCallback callback) {
        super(readerDataHolder.getContext());

        this.settings = settings;
        this.callback = callback;
        setContentView(R.layout.dialog_reflow_settings);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        ReflowMultiAdapter fontSizeAdapter = new ReflowMultiAdapter(getContext(), fontSizes, 1, fontSizes.length, R.drawable.reflow_small_select, R.drawable.reflow_small_select_no_label, true);
        fontSizeLayout.setMultiAdapter(fontSizeAdapter);

        ReflowMultiAdapter upgradeAdapter = new ReflowMultiAdapter(getContext(), upgradeSizes, 1, upgradeSizes.length, R.drawable.reflow_big_select, R.drawable.reflow_big_select_no_label, true);
        upgradeLayout.setMultiAdapter(upgradeAdapter);

        ReflowMultiAdapter columnAdapter = new ReflowMultiAdapter(getContext(), columnsSize, 1, columnsSize.length, R.drawable.reflow_big_select, R.drawable.reflow_big_select_no_label, true);
        columnsLayout.setMultiAdapter(columnAdapter);

        int[] formatResIds = {R.drawable.ic_dialog_reader_reset_format_small, R.drawable.ic_dialog_reader_reset_format_mid, R.drawable.ic_dialog_reader_reset_format_big};
        formatRecycler.setLayoutManager(new DisableScrollGridManager(getContext()));
        ReflowPageAdapter formatAdapter = new ReflowPageAdapter(1, 3, formatResIds);
        formatRecycler.setAdapter(formatAdapter);

        int[] alignResIds = {R.drawable.ic_dialog_reader_reset_align_left, R.drawable.ic_dialog_reader_reset_align_mid, R.drawable.ic_dialog_reader_reset_align_right};
        alignRecycler.setLayoutManager(new DisableScrollGridManager(getContext()));
        ReflowPageAdapter alignAdapter = new ReflowPageAdapter(1, 3, alignResIds);
        alignRecycler.setAdapter(alignAdapter);

        int[] orientationResIds = {R.drawable.ic_dialog_reader_reset_direction_vertical_left, R.drawable.ic_dialog_reader_reset_direction_vertical_right, R.drawable.ic_dialog_reader_reset_direction_across};
        directionRecycler.setLayoutManager(new DisableScrollGridManager(getContext()));
        ReflowPageAdapter orientationAdapter = new ReflowPageAdapter(1, 3, orientationResIds);
        directionRecycler.setAdapter(orientationAdapter);

        setupListeners();
        updateFontSize(settings);
        updateColumns(settings);
        updateAutoStraighten(settings);
        updateJustification(settings);
        updateFormat(settings);
        updateDirection(settings);
    }

    private void updateFontSize(ImageReflowSettings s) {
        int index = fontSizeDefaultIndex;
        for (int i = 0; i < fontSizeValues.length; i++) {
            if (fontSizeValues[i] == s.zoom) {
                index = i;
                break;
            }
        }
        fontSizeLayout.getMultiAdapter().setItemRangeChecked(true, 0, index + 1);
    }

    private void updateAutoStraighten(ImageReflowSettings s) {
        int index = autoStraightenDefaultIndex;
        for (int i = 0; i < autoStraightenValues.length; i++) {
            if (autoStraightenValues[i] == s.straighten) {
                index = i;
                break;
            }
        }
        upgradeLayout.getMultiAdapter().setItemRangeChecked(true, 0, index + 1);
    }

    private void updateColumns(ImageReflowSettings s) {
        int index = columnDefaultIndex;
        for (int i = 0; i < columnValues.length; i++) {
            if (columnValues[i] == s.columns) {
                index = i;
                break;
            }
        }
        columnsLayout.getMultiAdapter().setItemRangeChecked(true, 0, index + 1);
    }

    private void updateJustification(ImageReflowSettings s) {
        int index = justificationDefaultIndex;
        for (int i = 0; i < justificationValues.length; i++) {
            if (justificationValues[i] == s.justification) {
                index = i;
                break;
            }
        }
        alignRecycler.setCurrentFocusedPosition(index);
    }

    private void updateFormat(ImageReflowSettings s) {
        int index = formatDefaultIndex;
        for (int i = 0; i < formatValues.length; i++) {
            if (formatValues[i][0] == s.line_spacing
                    && formatValues[i][1] == s.margin) {
                index = i;
                break;
            }
        }
        formatRecycler.setCurrentFocusedPosition(index);
    }

    private void updateDirection(ImageReflowSettings s) {
        int index = directionDefaultIndex;
        for (int i = 0; i < directionValues.length; i++) {
            if (directionValues[i][0] == s.src_left_to_right
                    && directionValues[i][1] == s.src_rot) {
                index = i;
                break;
            }
        }
        directionRecycler.setCurrentFocusedPosition(index);
    }

    private void syncAutoStraighten(ImageReflowSettings s, int checkIndex) {
        if (checkIndex < autoStraightenValues.length) {
            s.straighten = autoStraightenValues[checkIndex];
        }
    }

    private void syncFontSize(ImageReflowSettings s, int checkIndex) {
        if (checkIndex < fontSizeValues.length) {
            s.zoom = fontSizeValues[checkIndex];
        }
    }

    private void syncColumns(ImageReflowSettings s, int checkIndex) {
        if (checkIndex < columnValues.length) {
            s.columns = columnValues[checkIndex];
        }
    }

    private void syncJustification(ImageReflowSettings s, int focusPosition) {
        if (focusPosition < justificationValues.length) {
            s.justification = justificationValues[focusPosition];
        }
    }

    private void syncFormat(ImageReflowSettings s, int focusPosition) {
        if (focusPosition < formatValues.length) {
            s.line_spacing = formatValues[focusPosition][0];
            s.margin = formatValues[focusPosition][1];
        }
    }

    private void syncDirection(ImageReflowSettings s, int focusPosition) {
        if (focusPosition < directionValues.length) {
            s.src_left_to_right = directionValues[focusPosition][0];
            s.src_rot = directionValues[focusPosition][1];
        }
    }

    private void syncSettings(ImageReflowSettings settings) {
        syncAutoStraighten(settings, getCheckedButtonIndex(upgradeLayout.getCompoundButtonList()));
        syncFontSize(settings, getCheckedButtonIndex(fontSizeLayout.getCompoundButtonList()));
        syncJustification(settings, alignRecycler.getCurrentFocusedPosition());
        syncFormat(settings, formatRecycler.getCurrentFocusedPosition());
        syncColumns(settings, getCheckedButtonIndex(columnsLayout.getCompoundButtonList()));
        syncDirection(settings, directionRecycler.getCurrentFocusedPosition());
    }

    private void resetSettings(ImageReflowSettings settings) {
        upgradeLayout.getMultiAdapter().resetItemChecked(autoStraightenDefaultIndex, fontSizes.length);
        fontSizeLayout.getMultiAdapter().resetItemChecked(fontSizeDefaultIndex, fontSizes.length);
        columnsLayout.getMultiAdapter().resetItemChecked(columnDefaultIndex, columnsSize.length);
        alignRecycler.setCurrentFocusedPosition(justificationDefaultIndex);
        formatRecycler.setCurrentFocusedPosition(formatDefaultIndex);
    }

    private int getCheckedButtonIndex(List<CompoundButton> compoundButtons) {
        int index = -1;
        for (CompoundButton button : compoundButtons) {
            if (button.isChecked()) {
                index++;
            }
        }
        return Math.max(index, 0);
    }

    private void setupListeners() {
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetSettings(settings);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    syncSettings(settings);
                    callback.onFinished(true, settings);
                }
            }
        });

        fontSizeLayout.setOnCheckedChangeListener(new DynamicMultiRadioGroupView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
                if (buttonView.isPressed() || buttonView.isFocused()) {
                    fontSizeLayout.getMultiAdapter().resetItemChecked(position, fontSizes.length);
                }
            }
        });

        upgradeLayout.setOnCheckedChangeListener(new DynamicMultiRadioGroupView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
                if (buttonView.isPressed() || buttonView.isFocused()) {
                    upgradeLayout.getMultiAdapter().resetItemChecked(position, upgradeSizes.length);
                }
            }
        });

        columnsLayout.setOnCheckedChangeListener(new DynamicMultiRadioGroupView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
                if (buttonView.isPressed() || buttonView.isFocused()) {
                    columnsLayout.getMultiAdapter().resetItemChecked(position, columnsSize.length);
                }
            }
        });

    }

    static class ReflowMultiAdapter extends DynamicMultiRadioGroupView.MultiAdapter {

        private List<String> textList = new ArrayList<>();
        private int rows;
        private int columns;
        private int endBackgroundResId;
        private Context context;

        public ReflowMultiAdapter(Context contexts, String[] texts, int rows, int columns, int backgroundResId, int endBackgroundResId, boolean multiCheck) {
            for (String text : texts) {
                textList.add(text);
            }
            this.context = contexts;
            this.rows = rows;
            this.columns = columns;
            this.endBackgroundResId = endBackgroundResId;

            setMultiCheck(multiCheck);
            setBackgroundResId(backgroundResId);
            setButtonTexts(textList);
            setTextSize(contexts.getResources().getDimension(R.dimen.reflow_checkbox_text_size));
        }

        @Override
        public int getRows() {
            return rows;
        }

        @Override
        public int getColumns() {
            return columns;
        }

        @Override
        public void bindView(CompoundButton button, int position) {
            if (position == textList.size() - 1) {
                button.setBackgroundResource(endBackgroundResId);
            }
            button.setPadding(0, (int) context.getResources().getDimension(R.dimen.reflow_checkbox_text_padding_top), 0, 0);
            button.setButtonDrawable(R.color.transparent);
        }

        @Override
        public int getItemCount() {
            return textList.size();
        }
    }

    static class ReflowPageAdapter extends PageRecyclerView.PageAdapter {

        private int rows;
        private int columns;
        private int[] resIds;

        public ReflowPageAdapter(int rows, int columns, int[] resIds) {
            this.rows = rows;
            this.columns = columns;
            this.resIds = resIds;
        }

        @Override
        public int getRowCount() {
            return rows;
        }

        @Override
        public int getColumnCount() {
            return columns;
        }

        @Override
        public int getDataCount() {
            return resIds.length;
        }

        @Override
        public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
            return new ReflowViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.reflow_setting_item_view, parent, false));
        }

        @Override
        public void onPageBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ReflowViewHolder viewHolder = (ReflowViewHolder) holder;
            viewHolder.imageView.setImageResource(resIds[position]);

            if (position == getPageRecyclerView().getCurrentFocusedPosition()) {
                viewHolder.imageView.setActivated(true);
            } else {
                viewHolder.imageView.setActivated(false);
            }
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPageRecyclerView().setCurrentFocusedPosition(position);
                }
            });
        }
    }

    static class ReflowViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.image_view)
        ImageView imageView;

        public ReflowViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
