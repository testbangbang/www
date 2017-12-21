package com.onyx.edu.homework.action;

import android.content.Context;
import android.text.Spanned;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.model.Question;
import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;
import com.onyx.android.sdk.utils.Base64ImageParser;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.homework.base.BaseAction;
import com.onyx.edu.homework.ui.AnalysisDialog;
import com.onyx.edu.homework.utils.TextUtils;

/**
 * Created by lxm on 2017/12/21.
 */

public class ShowAnalysisAction extends BaseAction{

    private Question question;

    public ShowAnalysisAction(Question question) {
        this.question = question;
    }

    @Override
    public void execute(Context context, BaseCallback baseCallback) {
        if (question == null) {
            return;
        }
        new AnalysisDialog(context, question).show();
    }
}
