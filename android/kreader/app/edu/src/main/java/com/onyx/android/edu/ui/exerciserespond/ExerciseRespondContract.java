package com.onyx.android.edu.ui.exerciserespond;


import com.onyx.android.edu.base.BasePresenter;
import com.onyx.android.edu.base.BaseQuestionView;
import com.onyx.android.edu.base.BaseView;
import com.onyx.android.edu.bean.PaperResult;
import com.onyx.android.edu.db.model.Chapter;

import java.util.List;

/**
 * Created by ming on 16/6/24.
 */
public interface ExerciseRespondContract {

    interface ExerciseRespondView extends BaseView<ExerciseRespondPresenter> {
        void showPaper(Chapter chapter, boolean showAnswer);
    }

    interface ExerciseRespondPresenter extends BasePresenter {
        void loadPapers();
        PaperResult getPaperResult(List<BaseQuestionView> selectViewList);
    }
}
