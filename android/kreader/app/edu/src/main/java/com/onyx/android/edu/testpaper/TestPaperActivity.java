package com.onyx.android.edu.testpaper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.base.BaseActivity;
import com.onyx.android.edu.utils.ActivityUtils;

import butterknife.Bind;


/**
 * Created by ming on 16/6/24.
 */
public class TestPaperActivity extends BaseActivity {

    public static final String SHOW_ANSWER = "show_answer";

    @Bind(R.id.contentFrame)
    FrameLayout mContentFrame;
    @Bind(R.id.back)
    ImageView mBack;
    @Bind(R.id.left_title)
    TextView mLeftTitle;
    @Bind(R.id.toolbar_title)
    TextView mToolbarTitle;
    @Bind(R.id.right_title)
    TextView mRightTitle;
    @Bind(R.id.toolbar_content)
    RelativeLayout mToolbarContent;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_test_paper;
    }

    @Override
    protected void initView() {

        TestPaperFragment testPaperFragment = (TestPaperFragment) getFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if (testPaperFragment == null) {
            testPaperFragment = TestPaperFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getFragmentManager(),
                    testPaperFragment, R.id.contentFrame);
        }

        Intent intent = getIntent();
        boolean showAnswer = intent.getBooleanExtra(SHOW_ANSWER, false);

        new TestPaperPresenter(testPaperFragment, showAnswer);

        mToolbarContent.setBackgroundResource(R.color.gray);
        mToolbarTitle.setVisibility(View.GONE);
        mRightTitle.setVisibility(View.GONE);
        mLeftTitle.setText("科目：数学 分册：高一上册 共有10道习题");
        mLeftTitle.setTextSize(20);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
