package com.onyx.android.edu.ui.respondresult;


import com.onyx.android.edu.base.BasePresenter;
import com.onyx.android.edu.base.BaseView;
import com.onyx.android.edu.bean.PaperResult;

/**
 * Created by ming on 16/6/28.
 */
public interface RespondResultContract {
    interface View extends BaseView<Presenter> {
        void showResult(PaperResult paperResult);
    }

    interface Presenter extends BasePresenter {

        void updateExaminationPaper(Float score, int count,int errorCount);
    }
}
