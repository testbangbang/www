package com.onyx.android.edu.ui.chapter;


import com.onyx.android.edu.base.BasePresenter;
import com.onyx.android.edu.base.BaseView;
import com.onyx.libedu.model.BookNode;
import com.onyx.libedu.model.ChooseQuestionVariable;
import com.onyx.libedu.model.KnowledgePoint;
import com.onyx.libedu.model.Question;

import java.util.List;

/**
 * Created by ming on 16/6/28.
 */
public interface ChapterTypeContract {

    interface ChapterTypeView extends BaseView<ChapterTypePresenter> {
        void openQuestions(List<Question> questions);
    }

    interface ChapterTypePresenter extends BasePresenter {
        List<BookNode> getBookNodes();
        List<KnowledgePoint> getKnowledgePoints();
        ChooseQuestionVariable getChooseQuestionVariable();
        void loadChapterQuestions(BookNode bookNode1, BookNode bookNode2);
        void loadKnowledgePointQuestions(KnowledgePoint knowledgePoint1, KnowledgePoint knowledgePoint2);
    }

}
