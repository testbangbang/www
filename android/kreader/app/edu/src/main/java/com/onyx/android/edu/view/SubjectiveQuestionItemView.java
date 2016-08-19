package com.onyx.android.edu.view;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.edu.R;

/**
 * Created by ming on 16/6/29.
 */
public class SubjectiveQuestionItemView extends LinearLayout {

    private TextView mItemIndex;
    private EditText mItemContent;

    public SubjectiveQuestionItemView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context){
        View.inflate(context, R.layout.view_fill_question_item,this);
        mItemIndex = (TextView) findViewById(R.id.item_index);
        mItemContent = (EditText) findViewById(R.id.item_content);
    }

    public void setIndex(int postion){
        mItemIndex.setText(postion + " ");
    }

    public String getContent(){
        return mItemContent.getText().toString();
    }
}
