package com.onyx.android.edu.view;

import android.content.Context;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.edu.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 16/6/30.
 */
public class TestsTypeItemView extends LinearLayout {

    private TextView mItemName;
    private LinearLayout mMultiLineGroup;
    private Context mContext;
    private Attribute mAttribute;
    private List<CheckBox> mCheckBoxList;

    public TestsTypeItemView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    private void initView(){
        inflate(mContext, R.layout.view_tests_type_item,this);
        mItemName = (TextView) findViewById(R.id.item_name);
        mMultiLineGroup = (LinearLayout) findViewById(R.id.multi_group);
        mCheckBoxList = new ArrayList<>();
    }

    private CheckBox getCheckButton(String text) {
        CheckBox checkBox = new CheckBox(mContext);
        checkBox.setText(text);
        checkBox.setButtonDrawable(R.color.transparent);
        int drawableRes = mAttribute.isOval ? R.drawable.oval_radio : R.drawable.type_radio;
        checkBox.setBackgroundDrawable(mContext.getResources().getDrawable(drawableRes));
        checkBox.setGravity(Gravity.CENTER);
        checkBox.setTextSize(20);
        checkBox.setPadding(20, 10, 20, 10);
        LayoutParams layoutParams = new LayoutParams(0, LayoutParams.MATCH_PARENT);
        if (mAttribute.checkBoxWidth != 0){
            layoutParams.width = mAttribute.checkBoxWidth;
        }else {
            layoutParams.weight = 1;
        }

        layoutParams.setMargins(20,0,20,0);
        checkBox.setLayoutParams(layoutParams);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    clearCheckBox((CheckBox) buttonView);
                }
            }
        });
        mCheckBoxList.add(checkBox);
        return checkBox;
    }

    private void clearCheckBox(CheckBox checkBox){
        for (CheckBox box : mCheckBoxList) {
            if (box != checkBox){
                box.setChecked(false);
            }
        }
    }

    public void setTypeData(Attribute attribute,String name,String[] checkBoxTexts){
        mAttribute = attribute;
        mItemName.setText(name);

        initMultiLineGroup();
        addNewLineCheckBox(checkBoxTexts);
    }

    private void initMultiLineGroup(){
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(mAttribute.groupLeftMargin,
                mAttribute.groupTopMargin,
                mAttribute.groupRightMargin,
                mAttribute.groupBottomMargin);
        mMultiLineGroup.setLayoutParams(layoutParams);
    }

    private void addNewLineCheckBox(String[] checkBoxTexts){
        int row = checkBoxTexts.length < mAttribute.countOfLine ? 1 : checkBoxTexts.length / mAttribute.countOfLine;
        for (int i = 0; i < row; i++) {
            LinearLayout linearLayout = generateNewLinearLayout();
            int count = checkBoxTexts.length < mAttribute.countOfLine ? checkBoxTexts.length : mAttribute.countOfLine;
            for (int b = 0; b < count ; b++) {
                CheckBox button = getCheckButton(checkBoxTexts[b + i*count]);
                linearLayout.addView(button);
            }
            mMultiLineGroup.addView(linearLayout);
        }
    }

    private LinearLayout generateNewLinearLayout(){
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        layoutParams.weight = 1;
        layoutParams.setMargins(0,0,0,20);
        linearLayout.setLayoutParams(layoutParams);
        return linearLayout;
    }

    public static class Attribute{
        public int countOfLine = 5;
        public int checkBoxWidth = 0;
        public int groupLeftMargin = 5;
        public int groupRightMargin = 5;
        public int groupTopMargin = 5;
        public int groupBottomMargin = 5;
        public boolean isOval = false;
    }

}
