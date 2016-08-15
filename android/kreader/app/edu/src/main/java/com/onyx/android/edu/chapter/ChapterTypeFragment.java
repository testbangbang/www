package com.onyx.android.edu.chapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.adapter.ChapterExpandListAdapter;
import com.onyx.android.edu.base.BaseFragment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;

/**
 * Created by ming on 16/6/28.
 */
public class ChapterTypeFragment extends BaseFragment implements ChapterTypeContract.ChapterTypeView {


    @Bind(R.id.chapter_list)
    ExpandableListView mChapterList;

    private ChapterExpandListAdapter mChapterExpandListAdapter;
    private ChapterTypeContract.ChapterTypePresenter mPresenter;

    public static ChapterTypeFragment newInstance() {
        return new ChapterTypeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chapter_type;
    }

    @Override
    protected void initView(View root, Bundle savedInstanceState) {
        mPresenter.subscribe();
    }

    @Override
    protected void initData() {

        LinkedHashMap<String, List<String>> dataMap = new LinkedHashMap<>();
        List<String> content = new ArrayList<>();
        content.add("正式的原则");
        content.add("正式的原则");
        content.add("正式的原则");
        dataMap.put("第一章  丰富的图形世界",content);
        dataMap.put("第二章  丰富的图形世界",content);
        dataMap.put("第三章  丰富的图形世界",content);

        mChapterExpandListAdapter = new ChapterExpandListAdapter(dataMap);
        mChapterList.setAdapter(mChapterExpandListAdapter);
        mChapterList.setGroupIndicator(null);
        mChapterList.setDivider(null);

        mChapterList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public void setPresenter(ChapterTypeContract.ChapterTypePresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unSubscribe();
    }
}
