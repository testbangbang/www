package com.onyx.libedu.request.cloud;

import com.onyx.libedu.BaseEduRequest;
import com.onyx.libedu.EduCloudManager;
import com.onyx.libedu.db.PaperQuestionAndAnswer;

/**
 * Created by li on 2017/8/10.
 */

public class InsertUserAnswerRequest extends BaseEduRequest {
    private long id;
    private String userAnswer;
    private String score;

    public InsertUserAnswerRequest(long id, String userAnswer, String score) {
        this.id = id;
        this.userAnswer = userAnswer;
        this.score = score;
    }

    @Override
    public void execute(EduCloudManager parent) throws Exception {
        PaperQuestionAndAnswer.updateAnswerById(getContext(), id, userAnswer, score);
    }
}
