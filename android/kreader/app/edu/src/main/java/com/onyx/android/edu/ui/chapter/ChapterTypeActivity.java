package com.onyx.android.edu.ui.chapter;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.edu.R;
import com.onyx.android.edu.base.BaseActivity;
import com.onyx.android.edu.base.Constant;
import com.onyx.android.edu.utils.ActivityUtils;
import com.onyx.libedu.model.BookNode;
import com.onyx.libedu.model.ChooseQuestionVariable;
import com.onyx.libedu.model.KnowledgePoint;

import java.util.List;

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
        return R.layout.activity_general;
    }

    @Override
    protected void initView() {
        mToolbarTitle.setVisibility(View.GONE);
        mRightTitle.setVisibility(View.GONE);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        String book = getIntent().getStringExtra(Constant.BOOK_NODE);
        String variable = getIntent().getStringExtra(Constant.CHOOSE_QUESTION_VARIABLE);
        String knowledgePoint = getIntent().getStringExtra(Constant.KNOW_LEDGE_POINT);

        List<BookNode> bookNodes = JSON.parseObject(book, new TypeReference<List<BookNode>>(){});
        List<KnowledgePoint> knowledgePoints = JSON.parseObject(knowledgePoint, new TypeReference<List<KnowledgePoint>>(){});
        ChooseQuestionVariable chooseQuestionVariable = JSON.parseObject(variable, ChooseQuestionVariable.class);
        String text = chooseQuestionVariable.getStage().getStageName() + " " + chooseQuestionVariable.getTextbook().getBookName();
        mLeftTitle.setText(text);
        mChapterTypeFragment = (ChapterTypeFragment) getFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if (mChapterTypeFragment == null) {
            mChapterTypeFragment = ChapterTypeFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getFragmentManager(),
                    mChapterTypeFragment, R.id.contentFrame);
        }

        ChapterTypePresenter chapterTypePresenter = new ChapterTypePresenter(mChapterTypeFragment);
        chapterTypePresenter.setBookNodes(bookNodes);
        chapterTypePresenter.setKnowledgePoints(knowledgePoints);
        chapterTypePresenter.setChooseQuestionVariable(chooseQuestionVariable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
