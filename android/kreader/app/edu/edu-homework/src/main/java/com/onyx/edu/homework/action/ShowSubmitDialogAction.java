package com.onyx.edu.homework.action;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.model.homework.Question;
import com.onyx.edu.homework.base.BaseAction;
import com.onyx.edu.homework.ui.SubmitDialog;

import java.util.List;

/**
 * Created by lxm on 2018/2/1.
 */

public class ShowSubmitDialogAction extends BaseAction {

    private List<Question> questions;

    public ShowSubmitDialogAction(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public void execute(Context context, BaseCallback baseCallback) {
        SubmitDialog dialog = new SubmitDialog(context, questions);
        dialog.show();
    }
}
