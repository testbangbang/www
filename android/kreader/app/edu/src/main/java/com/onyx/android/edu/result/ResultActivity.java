package com.onyx.android.edu.result;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.base.BaseActivity;
import com.onyx.android.edu.bean.PaperResult;
import com.onyx.android.edu.utils.ActivityUtils;
import com.onyx.android.edu.utils.JsonUtils;

import butterknife.Bind;

/**
 * Created by ming on 16/6/24.
 */
public class ResultActivity extends BaseActivity {

    public static final String RESULT = "result";
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
        return R.layout.activity_result;
    }

    @Override
    protected void initView() {

        mLeftTitle.setVisibility(View.GONE);
        mRightTitle.setVisibility(View.GONE);
        mToolbarTitle.setText(getString(R.string.train_result));
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        String message = intent.getStringExtra(RESULT);
        if (!TextUtils.isEmpty(message)) {
            PaperResult paperResult = JsonUtils.toBean(message, PaperResult.class);

            ResultFragment resultFragment = (ResultFragment) getFragmentManager()
                    .findFragmentById(R.id.contentFrame);
            if (resultFragment == null) {
                resultFragment = ResultFragment.newInstance();
                ActivityUtils.addFragmentToActivity(getFragmentManager(),
                        resultFragment, R.id.contentFrame);
            }

            ResultPresenter resultPresenter = new ResultPresenter(resultFragment);
            resultPresenter.setPaperResult(paperResult);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
