package com.onyx.android.edu.ui.chapter;

/**
 * Created by ming on 16/6/28.
 */
public class ChapterTypePresenter implements ChapterTypeContract.ChapterTypePresenter {

    private ChapterTypeContract.ChapterTypeView mChapterTypeView;

    public ChapterTypePresenter(ChapterTypeContract.ChapterTypeView chapterTypeView){
        mChapterTypeView = chapterTypeView;
        mChapterTypeView.setPresenter(this);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unSubscribe() {

    }

}
