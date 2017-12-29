package com.onyx.jdread;

import android.databinding.ObservableField;

import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.model.TitleBarModel;

import java.util.Observable;

/**
 * Created by hehai on 17-12-29.
 */

public class FeedbackModel extends Observable {
    public final TitleBarModel titleBarModel = new TitleBarModel();

    public final ObservableField<String> feedback = new ObservableField<>();
    public final ObservableField<String> phone = new ObservableField<>();

    public void commitFeedback() {
        if (StringUtils.isNullOrEmpty(feedback.get())||feedback.get().length() < 5 || feedback.get().length() > 200) {
            ToastUtils.showToast(JDReadApplication.getInstance(), R.string.feedback_format_error);
        }
    }
}
