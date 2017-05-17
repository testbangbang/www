package com.onyx.android.edu.ui.chapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.onyx.android.edu.R;
import com.onyx.android.edu.adapter.ChapterExpandListAdapter;
import com.onyx.android.edu.base.BaseFragment;
import com.onyx.android.edu.base.Constant;
import com.onyx.android.edu.ui.exerciserespond.ExerciseRespondActivity;
import com.onyx.libedu.model.BookNode;
import com.onyx.libedu.model.KnowledgePoint;
import com.onyx.libedu.model.Question;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 16/6/28.
 */
public class ChapterTypeFragment extends BaseFragment implements ChapterTypeContract.ChapterTypeView {
    private static final String TAG = "ChapterTypeFragment";

    @Bind(R.id.chapter_list)
    ExpandableListView chapterList;

    private ChapterExpandListAdapter mChapterExpandListAdapter;
    private ChapterTypeContract.ChapterTypePresenter mPresenter;

    public static ChapterTypeFragment newInstance() {
        return new ChapterTypeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chapter_type;
    }


    protected void initView() {
        mPresenter.subscribe();
        boolean useBookNode = mPresenter.getBookNodes() != null && mPresenter.getBookNodes().size() > 0;
        mChapterExpandListAdapter = new ChapterExpandListAdapter(chapterList, useBookNode, mPresenter.getBookNodes(), mPresenter.getKnowledgePoints());
        chapterList.setAdapter(mChapterExpandListAdapter);
        chapterList.setGroupIndicator(null);
        chapterList.setDivider(null);

        chapterList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        if (useBookNode) {
            mChapterExpandListAdapter.setOnBookNodeClickListener(new ChapterExpandListAdapter.OnBookNodeClickListener() {
                @Override
                public void onItemClick(BookNode bookNode1, BookNode bookNode2) {
                    mPresenter.loadChapterQuestions(bookNode1, bookNode2);
                }
            });
        }else {
            mChapterExpandListAdapter.setOnKnowPointClickListener(new ChapterExpandListAdapter.OnKnowPointClickListener() {
                @Override
                public void onItemClick(KnowledgePoint knowledgePoint1, KnowledgePoint knowledgePoint2) {
                    mPresenter.loadKnowledgePointQuestions(knowledgePoint1, knowledgePoint2);
                }
            });
        }
    }

    @Override
    public void setPresenter(ChapterTypeContract.ChapterTypePresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unSubscribe();
        ButterKnife.unbind(this);
    }

    @Override
    public void openQuestions(List<Question> questions) {
        if (questions == null || questions.size() <=0) {
            Toast.makeText(getActivity(), getString(R.string.no_find_questions), Toast.LENGTH_SHORT).show();
            return;
        }
//        Log.d(TAG, "openQuestions: " + JSON.toJSONString(questions));
        Intent intent = new Intent(getActivity(),ExerciseRespondActivity.class);
        intent.putExtra(Constant.QUESTION, JSON.toJSONString(questions));
        intent.putExtra(Constant.CHOOSE_QUESTION_VARIABLE, JSON.toJSONString(mPresenter.getChooseQuestionVariable()));
        startActivity(intent);
    }
}
