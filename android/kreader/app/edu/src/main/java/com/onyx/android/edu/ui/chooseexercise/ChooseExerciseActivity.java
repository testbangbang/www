package com.onyx.android.edu.ui.chooseexercise;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.base.BaseActivity;
import com.onyx.android.edu.utils.ActivityUtils;
import com.onyx.android.sdk.ui.compat.AppCompatImageViewCollection;
import com.onyx.android.sdk.utils.StringUtils;

import butterknife.Bind;

/**
 * Created by ming on 16/6/24.
 */
public class ChooseExerciseActivity extends BaseActivity {

    private static final String TAG = "ChooseExerciseActivity";
    private static final String EDU_INTENT_CONTENT_TYPE = "contentType";
    private static final String EDU_INTENT_CONTENT_TYPE_EXAM = "examination";
    private static final String EDU_INTENT_CONTENT_TYPE_PRACTISE= "practice";
    
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
    @Bind(R.id.right_arrow)
    ImageView rightArrow;
    @Bind(R.id.divider_line)
    View dividerLine;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private ChooseExerciseFragment chooseExerciseFragment;
    private ChooseExerciseColorFragment chooseExerciseColorFragment;
    private ChooseExercisePresenter presenter;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_general;
    }

    @Override
    protected void initView() {
        mBack.setVisibility(View.INVISIBLE);
        mToolbarTitle.setText(R.string.app_name);
    }

    @Override
    protected void initData() {
        if (!AppCompatImageViewCollection.isAlignView()) {
            chooseExerciseFragment = (ChooseExerciseFragment) getFragmentManager()
                    .findFragmentById(R.id.contentFrame);
            if (chooseExerciseFragment == null) {
                chooseExerciseFragment = ChooseExerciseFragment.newInstance();
                ActivityUtils.addFragmentToActivity(getFragmentManager(),
                        chooseExerciseFragment, R.id.contentFrame);
            }

            presenter = new ChooseExercisePresenter(chooseExerciseFragment);
        }else {
            toolbar.setVisibility(View.GONE);
            chooseExerciseColorFragment = (ChooseExerciseColorFragment) getFragmentManager()
                    .findFragmentById(R.id.contentFrame);
            if (chooseExerciseColorFragment == null) {
                chooseExerciseColorFragment = ChooseExerciseColorFragment.newInstance();
                ActivityUtils.addFragmentToActivity(getFragmentManager(),
                        chooseExerciseColorFragment, R.id.contentFrame);
            }

            presenter = new ChooseExercisePresenter(chooseExerciseColorFragment);

            Intent intent = getIntent();
            String contentType = intent.getStringExtra(EDU_INTENT_CONTENT_TYPE);
            boolean isPractice = !StringUtils.isNullOrEmpty(contentType) && contentType.equals(EDU_INTENT_CONTENT_TYPE_PRACTISE);
            presenter.setPractice(isPractice);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                if (chooseExerciseColorFragment != null) {
//                    chooseExerciseColorFragment.changeSubjectView();
                }
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
