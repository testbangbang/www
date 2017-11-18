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

public class SaveRecordRequest extends BaseLocalRequest {
    private int taskId;
    private int questionId;
    private String recordPath;
    private long duration;
    private String result;

    public SaveRecordRequest(int taskId, int questionId, String recordPath, long duration) {
        this.taskId = taskId;
        this.questionId = questionId;
        this.recordPath = recordPath;
        this.duration = duration;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        TaskAndAnswerEntity taskAndAnswerEntity = new Select().from(TaskAndAnswerEntity.class).where(TaskAndAnswerEntity_Table.taskId.eq(taskId + ""), TaskAndAnswerEntity_Table.questionId.eq(questionId + "")).querySingle();
        if (taskAndAnswerEntity == null) {
            result = SunApplication.getInstance().getResources().getString(R.string.lose_data);
        } else {
            taskAndAnswerEntity.recordPath = recordPath;
            taskAndAnswerEntity.recordDuration = duration;
            taskAndAnswerEntity.update();
        }
    }

    public String getResult() {
        return result;
    }
}
