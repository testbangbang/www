package com.onyx.android.plato.requests.local;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.data.database.TaskAndAnswerEntity;
import com.onyx.android.plato.data.database.TaskAndAnswerEntity_Table;
import com.onyx.android.plato.requests.requestTool.BaseLocalRequest;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;
import com.raizlabs.android.dbflow.sql.language.Select;

/**
 * Created by li on 2017/11/18.
 */

public class GetRecordRequest extends BaseLocalRequest {
    private String taskId;
    private String questionId;
    private long recordDuration;

    public GetRecordRequest(String taskId, String questionId) {
        this.taskId = taskId;
        this.questionId = questionId;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        TaskAndAnswerEntity taskAndAnswerEntity = new Select().from(TaskAndAnswerEntity.class).where(TaskAndAnswerEntity_Table.taskId.eq(taskId), TaskAndAnswerEntity_Table.questionId.eq(questionId)).querySingle();
        if (taskAndAnswerEntity != null) {
            recordDuration = taskAndAnswerEntity.recordDuration;
        } else {
            recordDuration = -1;
        }
    }

    public long getRecordDuration() {
        return recordDuration;
    }
}
