package com.onyx.android.edu.ui.chooseexercise;


import com.onyx.android.edu.base.BasePresenter;
import com.onyx.android.edu.base.BaseView;
import com.onyx.libedu.model.BookNode;
import com.onyx.libedu.model.ChooseQuestionVariable;
import com.onyx.libedu.model.Difficult;
import com.onyx.libedu.model.Document;
import com.onyx.libedu.model.DocumentType;
import com.onyx.libedu.model.KnowledgePoint;
import com.onyx.libedu.model.QuestionType;
import com.onyx.libedu.model.Stage;
import com.onyx.libedu.model.Subject;
import com.onyx.libedu.model.Textbook;
import com.onyx.libedu.model.Version;

import java.util.List;

/**
 * Created by ming on 16/6/28.
 */
public interface ChooseExerciseContract {

    interface ChooseExerciseView extends BaseView<ChooseExercisePresenter> {
        void showSubjects(List<Subject> subjects);
        void showVersions(List<Version> versions);
        void showTextbooks(List<Textbook> textbooks);
        void showBookNodes(List<BookNode> bookNodes);
        void showKnowledgePoints(List<KnowledgePoint> knowledgePoints);
        void showQuestionType(List<QuestionType> questionTypes);
        void showDocuments(List<Document> documents);
    }

    interface ChooseExercisePresenter extends BasePresenter {
        void loadQuestionType(Subject subject);
        void loadSubjects(Stage stage);
        void loadVersions(Subject subject);
        void loadTextbooks(Version version);
        void loadBookNodes();
        void loadKnowledgePoints();
        void loadDocuments();

        void chooseTextbook(Textbook textbook);
        void chooseQuestionType(QuestionType questionType);
        void chooseDifficult(Difficult difficult);
        void chooseDocumentType(DocumentType type);
        ChooseQuestionVariable getChooseQuestionVariable();
        boolean isPractice();
    }

}
