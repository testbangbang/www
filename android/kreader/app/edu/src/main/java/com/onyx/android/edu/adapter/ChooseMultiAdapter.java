package com.onyx.android.edu.adapter;

import android.graphics.Color;
import android.widget.CompoundButton;

import com.onyx.android.sdk.data.model.BaseData;
import com.onyx.android.sdk.ui.view.DynamicMultiRadioGroupView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 16/8/17.
 */
public class ChooseMultiAdapter<T extends BaseData> extends DynamicMultiRadioGroupView.MultiAdapter{

    private List<String> buttonTexts;
    private List<T> datas;
    private int rows;
    private int columns;
    private int backgroundResId;

    public ChooseMultiAdapter(List<String> texts, int rows, int columns, int backgroundResId) {
        this.buttonTexts = new ArrayList<>();
        this.rows = rows;
        this.columns = columns;
        this.backgroundResId = backgroundResId;

        buttonTexts = texts;
        setPadding(0,10,0,10);
        setMargin(10,20,20,10);
        setMultiCheck(false);
        setBackgroundResId(backgroundResId);
        setButtonTexts(buttonTexts);
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
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
        button.setTextColor(button.isChecked() ? Color.WHITE : Color.BLACK);
    }

    @Override
    public int getItemCount() {
        return buttonTexts.size();
    }
}
