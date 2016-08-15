package com.onyx.android.edu.chapter;

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
public class ChapterTypeActivity extends BaseActivity {

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
    private ChapterTypeFragment mChapterTypeFragment;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_tests_type;
    }

    @Override
    protected void initView() {
        mToolbarTitle.setVisibility(View.GONE);
        mRightTitle.setVisibility(View.GONE);
        mLeftTitle.setText("数学  九年级上");
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        mChapterTypeFragment = (ChapterTypeFragment) getFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if (mChapterTypeFragment == null) {
            mChapterTypeFragment = ChapterTypeFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getFragmentManager(),
                    mChapterTypeFragment, R.id.contentFrame);
        }

        new ChapterTypePresenter(mChapterTypeFragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
