package com.onyx.android.edu.adapter;

import android.widget.CompoundButton;

import com.onyx.android.sdk.ui.view.DynamicMultiRadioGroupView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 16/8/17.
 */
public class ChooseMultiAdapter extends DynamicMultiRadioGroupView.MultiAdapter{

    private List<String> buttonTexts;
    private int rows;
    private int columns;
    private int backgroundResId;

    public ChooseMultiAdapter(String[] texts, int rows, int columns, int backgroundResId) {
        this.buttonTexts = new ArrayList<>();
        this.rows = rows;
        this.columns = columns;
        this.backgroundResId = backgroundResId;

        for (int i = 0; i < texts.length; i++) {
            buttonTexts.add(texts[i]);
        }

        setPadding(0,10,0,10);
        setMargin(10,20,20,10);
        setMultiCheck(false);
        setBackgroundResId(backgroundResId);
        setButtonTexts(buttonTexts);
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

    }

    @Override
    public int getItemCount() {
        return buttonTexts.size();
    }
}
