package com.onyx.android.edu.ui.respondresult;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
public class RespondResultActivity extends BaseActivity {

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
        return R.layout.activity_general;
    }

    @Override
    protected void initView() {

        mRightTitle.setVisibility(View.GONE);
        mLeftTitle.setText(getString(R.string.train_result));
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
//        if (!TextUtils.isEmpty(message)) {
        if (true) {
            PaperResult paperResult = JsonUtils.toBean(message, PaperResult.class);

            ExaminationResultFragment respondResultFragment = (ExaminationResultFragment) getFragmentManager()
                    .findFragmentById(R.id.contentFrame);
            if (respondResultFragment == null) {
                respondResultFragment = ExaminationResultFragment.newInstance();
                ActivityUtils.addFragmentToActivity(getFragmentManager(),
                        respondResultFragment, R.id.contentFrame);
            }

            RespondResultPresenter respondResultPresenter = new RespondResultPresenter(respondResultFragment);
            respondResultPresenter.setPaperResult(paperResult);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
