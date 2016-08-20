package com.onyx.android.edu.db.dataprovider;

import com.onyx.android.edu.base.EntityConfig;
import com.onyx.android.edu.db.model.ChoiceQuiz;
import com.onyx.android.edu.db.model.ChoiceQuiz_Table;
import com.onyx.android.edu.db.model.CompletionQuiz;
import com.onyx.android.edu.db.model.CompletionQuiz_Table;
import com.onyx.android.edu.db.model.ComplexQuiz;
import com.onyx.android.edu.db.model.ComplexQuiz_Table;
import com.onyx.android.edu.db.model.EssayQuiz;
import com.onyx.android.edu.db.model.EssayQuiz_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ming on 16/6/29.
 */
public class QuizDataProvider {

    private static final String TAG = "QuizDataProvider";

    public static List<Object> getQuizList(Map<Long,Integer> quizIds){
        List<Object> quizList = new ArrayList<>();
        for (Long id : quizIds.keySet()) {
            int type = quizIds.get(id);
            Select select = new Select();
            switch (type){
                case EntityConfig.CHOICE_QUIZ:
                    ChoiceQuiz choiceQuiz = select.from(ChoiceQuiz.class).where(ChoiceQuiz_Table.uniqueId.eq(id)).querySingle();
                    quizList.add(choiceQuiz);
                    break;
                case EntityConfig.COMPLETION_QUIZ:
                    CompletionQuiz completionQuiz = select.from(CompletionQuiz.class).where(CompletionQuiz_Table.uniqueId.eq(id)).querySingle();
                    quizList.add(completionQuiz);
                    break;
                case EntityConfig.ESSAY_QUIZ:
                    EssayQuiz essayQuiz = select.from(EssayQuiz.class).where(EssayQuiz_Table.uniqueId.eq(id)).querySingle();
                    quizList.add(essayQuiz);
                    break;
                case EntityConfig.COMPLEX_QUIZ:
                    ComplexQuiz complexQuiz = select.from(ComplexQuiz.class).where(ComplexQuiz_Table.uniqueId.eq(id)).querySingle();
                    quizList.add(complexQuiz);
                    break;
            }
        }
        return quizList;
    }

}
