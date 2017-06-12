package com.onyx.android.edu.adapter;

import android.widget.CompoundButton;

import com.onyx.android.edu.EduApp;
import com.onyx.android.edu.R;
import com.onyx.android.edu.base.Global;
import com.onyx.android.sdk.ui.view.DynamicMultiRadioGroupView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 16/8/17.
 */
public class ResultMultiAdapter extends DynamicMultiRadioGroupView.MultiAdapter{

    private List<String> buttonTexts;
    private List<Integer> rights;
    private int rows;
    private int columns;

    public ResultMultiAdapter(String[] texts, List<Integer> rights, int rows, int columns, int backgroundResId) {
        this.buttonTexts = new ArrayList<>();
        this.rows = rows;
        this.columns = columns;
        this.rights = rights;

        for (int i = 0; i < texts.length; i++) {
            buttonTexts.add(texts[i]);
        }

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
        if (rights.contains(position)){
            button.setTextColor(EduApp.instance().getResources().getColor(R.color.white));
            button.setBackgroundResource(R.drawable.right_oval);
        }else {
            button.setTextColor(EduApp.instance().getResources().getColor(R.color.black));
            button.setBackgroundResource(R.drawable.wrong_oval);
        }
        button.setEnabled(false);
    }

    @Override
    public int getItemCount() {
        return buttonTexts.size();
    }
}
