package com.onyx.android.sun.requests.local;

import com.onyx.android.sun.cloud.bean.QuestionViewBean;
import com.onyx.android.sun.data.database.TaskAndAnswerEntity;
import com.onyx.android.sun.data.database.TaskAndAnswerEntity_Table;
import com.onyx.android.sun.requests.requestTool.BaseLocalRequest;
import com.onyx.android.sun.requests.requestTool.SunRequestManager;
import com.raizlabs.android.dbflow.sql.language.Select;

/**
 * Created by li on 2017/10/20.
 */

public class FillAnswerRequest extends BaseLocalRequest {
    private String answer;
    private String type;
    private String content;
    private String questionId;
    private String taskId;

    public FillAnswerRequest(String taskId, String questionId, String questionContent, String questionType, String answer) {
        this.taskId = taskId;
        this.questionId = questionId;
        this.content = questionContent;
        this.type = questionType;
        this.answer = answer;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        TaskAndAnswerEntity taskAndAnswerEntity = new Select().from(TaskAndAnswerEntity.class).where(TaskAndAnswerEntity_Table.taskId.eq(taskId), TaskAndAnswerEntity_Table.questionId.eq(questionId)).querySingle();
        if (taskAndAnswerEntity == null) {
            taskAndAnswerEntity = new TaskAndAnswerEntity();
            taskAndAnswerEntity.taskId = taskId;
            taskAndAnswerEntity.question = content;
            taskAndAnswerEntity.type = type;
            taskAndAnswerEntity.questionId = questionId;
            taskAndAnswerEntity.userAnswer = answer;
            taskAndAnswerEntity.insert();
        } else {
            taskAndAnswerEntity.userAnswer = answer;
            taskAndAnswerEntity.update();
        }
    }
}
