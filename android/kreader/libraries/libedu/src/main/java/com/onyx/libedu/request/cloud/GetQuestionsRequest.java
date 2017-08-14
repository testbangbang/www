package com.onyx.libedu.request.cloud;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.libedu.BaseEduRequest;
import com.onyx.libedu.EduCloudManager;
import com.onyx.libedu.db.PaperQuestionAndAnswer;
import com.onyx.libedu.model.Question;
import com.onyx.libedu.model.QuestionAnalytical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by li on 2017/8/9.
 */

public class GetQuestionsRequest extends BaseEduRequest {
    private String bookId;
    private List<Question> questions;

    public GetQuestionsRequest(String bookId) {
        this.bookId = bookId;
    }

    @Override
    public void execute(EduCloudManager parent) throws Exception {
        questions = queryFromSqlite();
    }

    private List<Question> queryFromSqlite() {
        List<PaperQuestionAndAnswer> questions = PaperQuestionAndAnswer.getPaperQuestionAndAnswerByBookId(getContext(), bookId);
        if(questions == null || questions.size() <= 0) {
            return null;
        }

        ArrayList<Question> list = new ArrayList<>();
        for (PaperQuestionAndAnswer paper : questions) {
            Question question = new Question();
            question.setStem(paper.question);
            question.setId(Long.parseLong(paper.requestionId));
            question.setDifficult(0);

            QuestionAnalytical questionAnalytical = new QuestionAnalytical();
            questionAnalytical.setAnswer(paper.answer);
            question.setQuestionAnalytical(questionAnalytical);

            TreeMap<String, String> options = new TreeMap<>();
            if(!StringUtils.isNullOrEmpty(paper.option1)) {
                options.put("A", paper.option1);
            }

            if(!StringUtils.isNullOrEmpty(paper.option2)){
                options.put("B", paper.option2);
            }

            if(!StringUtils.isNullOrEmpty(paper.option3)){
                options.put("C", paper.option3);
            }

            if(!StringUtils.isNullOrEmpty(paper.option4)){
                options.put("D", paper.option4);
            }

            question.setQuestionOptions(options);
            list.add(question);
        }
        return list;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
