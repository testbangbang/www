package com.onyx.android.edu.ui.wrongquestions;

import android.os.Bundle;
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
public class WrongQuestionActivity extends BaseActivity {

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
    @Bind(R.id.contentFrame)
    FrameLayout mContentFrame;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_general;
    }

    @Override
    protected void initView() {
        WrongQuestionFragment wrongQuestionFragment = (WrongQuestionFragment) getFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if (wrongQuestionFragment == null) {
            wrongQuestionFragment = WrongQuestionFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getFragmentManager(),
                    wrongQuestionFragment, R.id.contentFrame);
        }

        new WrongQuestionPresenter(wrongQuestionFragment);

        mLeftTitle.setText(getString(R.string.wrong_view));
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
