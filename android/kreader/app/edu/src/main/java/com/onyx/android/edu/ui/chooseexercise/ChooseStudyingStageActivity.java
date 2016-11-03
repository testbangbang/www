package com.onyx.android.edu.ui.chooseexercise;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.adapter.ChooseMultiAdapter;
import com.onyx.android.edu.base.BaseActivity;
import com.onyx.android.sdk.ui.view.DynamicMultiRadioGroupView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by ming on 16/8/18.
 */
public class ChooseStudyingStageActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.back)
    ImageButton back;
    @Bind(R.id.left_title)
    TextView leftTitle;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.right_title)
    TextView rightTitle;
    @Bind(R.id.right_arrow)
    ImageButton rightArrow;
    @Bind(R.id.toolbar_content)
    RelativeLayout toolbarContent;
    @Bind(R.id.divider_line)
    View dividerLine;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.studying_stage)
    DynamicMultiRadioGroupView studyingStage;
    @Bind(R.id.studying_term)
    DynamicMultiRadioGroupView studyingTerm;
    @Bind(R.id.btn_ok)
    Button btnOk;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_choose_studying_stage;
    }

    @Override
    protected void initView() {
        leftTitle.setText(getString(R.string.app_name));
        toolbarTitle.setText(getString(R.string.select_studying_stage));
        rightArrow.setVisibility(View.INVISIBLE);

        back.setOnClickListener(this);
        btnOk.setOnClickListener(this);

        initStudyingStageView();
        initStudyingTermView();
    }

    private void initStudyingStageView(){
        List<String> names = new ArrayList<>();
        names.add("小学");
        names.add("初中");
        names.add("初中");
        studyingStage.setMultiAdapter(new ChooseMultiAdapter(names, 1, 3, R.drawable.oval_radio));
        studyingStage.setOnCheckedChangeListener(new DynamicMultiRadioGroupView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
                if (isChecked) {

                }
            }
        });
    }

    private void initStudyingTermView(){
        List<String> names = new ArrayList<>();
        names.add("高一上");
        names.add("高一下");
        names.add("高二上");
        names.add("高二下");
        names.add("高三上");
        names.add("高三下");
        studyingTerm.setMultiAdapter(new ChooseMultiAdapter(names, 2, 4, R.drawable.rectangle_radio));
        studyingTerm.setOnCheckedChangeListener(new DynamicMultiRadioGroupView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
                if (isChecked) {

                }
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(back)){
            finish();
        }
    }
}
