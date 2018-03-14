package com.onyx.edu.homework.request;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.homework.Homework;
import com.onyx.android.sdk.data.model.homework.Question;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.scribble.data.ShapeDataProvider;
import com.onyx.android.sdk.scribble.data.ShapeModel;
import com.onyx.android.sdk.scribble.data.ShapeState;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.edu.homework.data.HomeworkState;
import com.onyx.edu.homework.db.DBDataProvider;
import com.onyx.edu.homework.db.HomeworkModel;

import java.util.List;

/**
 * Created by lxm on 2018/1/18.
 */

public class UpdateHomeworkModelRequest extends BaseDataRequest {

    private Homework homework;
    private String personalHomeworkId;

    public UpdateHomeworkModelRequest(Homework homework, String personalHomeworkId) {
        this.homework = homework;
        this.personalHomeworkId = personalHomeworkId;
    }

    public UpdateHomeworkModelRequest(Homework homework) {
        this.homework = homework;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        HomeworkModel model = DBDataProvider.loadHomework(personalHomeworkId);
        if (model == null) {
            model = HomeworkModel.create(personalHomeworkId);
        }
        model.loadFromHomework(homework);
        HomeworkState homeworkState = HomeworkState.getHomeworkState(model.getState());
        if (homeworkState == HomeworkState.SUBMITTED
                && homework.isPublishedAnswer()
                && !homework.hasReview()) {
            updateShapeState(homework.questions);
        }
        model.save();
    }

    private void updateShapeState(List<Question> questions) {
        if (CollectionUtils.isNullOrEmpty(questions)) {
            return;
        }
        for (Question question : questions) {
            if (question.isChoiceQuestion()) {
                continue;
            }
            List<ShapeModel> shapeList = ShapeDataProvider.loadShapeList(getContext(), question.uniqueId);
            if (!CollectionUtils.isNullOrEmpty(shapeList)) {
                for (ShapeModel shapeModel : shapeList) {
                    if (shapeModel.getState() == ShapeState.NORMAL) {
                        shapeModel.setState(ShapeState.REVIEWED);
                    }
                }
                ShapeDataProvider.saveShapeList(getContext(), shapeList);
            }
        }
    }
}
