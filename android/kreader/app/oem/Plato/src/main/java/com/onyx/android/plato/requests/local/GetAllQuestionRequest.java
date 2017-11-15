package com.onyx.android.plato.requests.local;

import com.onyx.android.plato.data.database.TaskAndAnswerEntity;
import com.onyx.android.plato.data.database.TaskAndAnswerEntity_Table;
import com.onyx.android.plato.requests.requestTool.BaseCloudRequest;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by li on 2017/10/20.
 */

public class GetAllQuestionRequest extends BaseCloudRequest {
    private String taskId;
    private List<TaskAndAnswerEntity> taskList;

    public GetAllQuestionRequest(String taskId) {
        this.taskId = taskId;
    }
    @Override
    public void execute(SunRequestManager helper) throws Exception {
        taskList = new Select().from(TaskAndAnswerEntity.class).where(TaskAndAnswerEntity_Table.taskId.eq(taskId)).queryList();
    }

    public List<TaskAndAnswerEntity> getTaskList() {
        return taskList;
    }
}
