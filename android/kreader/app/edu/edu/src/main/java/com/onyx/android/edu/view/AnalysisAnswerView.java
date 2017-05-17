package com.onyx.android.edu.view;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.onyx.android.edu.R;

/**
 * Created by ming on 16/7/1.
 */
public class AnalysisAnswerView extends RelativeLayout{

    private Button mAnalysisButton;
    private LinearLayout mAnalysisView;

    public AnalysisAnswerView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context){
        View.inflate(context, R.layout.view_analysis_question,this);

        mAnalysisButton = (Button)findViewById(R.id.analysis_button);
        mAnalysisView = (LinearLayout) findViewById(R.id.analysis_view);

        mAnalysisButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAnalysisView.getVisibility() == View.VISIBLE){
                    mAnalysisView.setVisibility(INVISIBLE);
                }else{
                    mAnalysisView.setVisibility(VISIBLE);
                }
                updateButtonText();
            }
        });
        mAnalysisView.setVisibility(INVISIBLE);
        updateButtonText();
    }

    private void updateButtonText(){
        mAnalysisButton.setText(mAnalysisView.getVisibility() == View.VISIBLE ? "收起解析" : "查看解析");
    }
}
