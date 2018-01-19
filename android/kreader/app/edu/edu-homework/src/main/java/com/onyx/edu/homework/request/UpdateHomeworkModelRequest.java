package com.onyx.edu.homework.request;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.homework.Homework;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.edu.homework.db.DBDataProvider;
import com.onyx.edu.homework.db.HomeworkModel;
/**
 * Created by lxm on 2018/1/18.
 */

public class UpdateHomeworkModelRequest extends BaseDataRequest {

    private Homework homework;

    public UpdateHomeworkModelRequest(Homework homework) {
        this.homework = homework;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        HomeworkModel model = DBDataProvider.loadHomework(homework._id);
        if (model == null) {
            model = HomeworkModel.create(homework._id);
        }
        model.loadFromHomework(homework);
        model.save();
    }
}
