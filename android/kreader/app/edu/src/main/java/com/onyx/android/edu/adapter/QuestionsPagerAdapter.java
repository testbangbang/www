package com.onyx.android.edu.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.edu.base.BaseQuestionView;

import java.util.List;

/**
 * Created by ming on 16/6/24.
 */
public class QuestionsPagerAdapter extends PagerAdapter {

    private List<BaseQuestionView> mViewList;

    public QuestionsPagerAdapter(List<BaseQuestionView> viewList){
        mViewList = viewList;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mViewList.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public List<BaseQuestionView> getViewList() {
        return mViewList;
    }
}
