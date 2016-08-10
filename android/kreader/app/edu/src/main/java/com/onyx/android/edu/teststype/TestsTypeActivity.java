package com.onyx.android.edu.teststype;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.base.BaseActivity;
import com.onyx.android.edu.base.Global;
import com.onyx.android.edu.utils.ActivityUtils;
import com.onyx.android.edu.wrongquestions.WrongQuestionActivity;

import butterknife.Bind;

/**
 * Created by ming on 16/6/24.
 */
public class TestsTypeActivity extends BaseActivity {

    @Bind(R.id.left_title)
    TextView mLeftTitle;
    @Bind(R.id.back)
    ImageView mBack;
    @Bind(R.id.toolbar_title)
    TextView mToolbarTitle;
    @Bind(R.id.right_title)
    TextView mRightTitle;
    @Bind(R.id.toolbar_content)
    RelativeLayout mToolbarContent;
    @Bind(R.id.contentFrame)
    FrameLayout mContentFrame;
    private TestsTypeFragment mTestsTypeFragment;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_tests_type;
    }

    @Override
    protected void initView() {
        mBack.setVisibility(View.GONE);
        mLeftTitle.setVisibility(View.GONE);
        mToolbarTitle.setText(getString(R.string.self_train));
        mRightTitle.setText(getString(R.string.enter_wrong_view));
        mRightTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestsTypeActivity.this, WrongQuestionActivity.class));
            }
        });
    }

    @Override
    protected void initData() {
        mTestsTypeFragment = (TestsTypeFragment) getFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if (mTestsTypeFragment == null) {
            mTestsTypeFragment = TestsTypeFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getFragmentManager(),
                    mTestsTypeFragment, R.id.contentFrame);
        }

        new TestsTypePresenter(mTestsTypeFragment);

        Global.getInstance().loadTestData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
