package com.onyx.android.edu.adapter;

import android.widget.CompoundButton;

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
    private int backgroundResId;

    public ResultMultiAdapter(String[] texts, List<Integer> rights, int rows, int columns, int backgroundResId) {
        this.buttonTexts = new ArrayList<>();
        this.rows = rows;
        this.columns = columns;
        this.backgroundResId = backgroundResId;
        this.rights = rights;

        for (int i = 0; i < texts.length; i++) {
            buttonTexts.add(texts[i]);
        }

//        setPadding(20,20,20,20);
        setMargin(10,20,20,10);
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
    public int getBackgroundResId() {
        return backgroundResId;
    }

    @Override
    public boolean isRadioButton() {
        return true;
    }

    @Override
    public void bindView(CompoundButton button, int position) {
        if (rights.contains(position)){
            button.setTextColor(Global.getContext().getResources().getColor(R.color.white));
            button.setBackgroundResource(R.drawable.right_oval);
        }else {
            button.setTextColor(Global.getContext().getResources().getColor(R.color.black));
            button.setBackgroundResource(R.drawable.wrong_oval);
        }
        button.setEnabled(false);
    }

    @Override
    public List<String> getButtonTexts() {
        return buttonTexts;
    }
}
