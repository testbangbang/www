package com.onyx.jdread.setting.model;

import android.databinding.ObservableField;

import com.onyx.android.sdk.data.model.LogCollection;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.InputMethodUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.main.util.RegularUtil;
import com.onyx.jdread.setting.event.BackToHelpFragmentEvent;
import com.onyx.jdread.util.BroadcastHelper;

import java.util.Observable;

/**
 * Created by hehai on 17-12-29.
 */

public class FeedbackModel extends Observable {
    public final TitleBarModel titleBarModel = new TitleBarModel(SettingBundle.getInstance().getEventBus());

    public final ObservableField<String> feedback = new ObservableField<>();
    public final ObservableField<String> phone = new ObservableField<>();

    public FeedbackModel() {
        titleBarModel.backEvent.set(new BackToHelpFragmentEvent());
        titleBarModel.title.set(JDReadApplication.getInstance().getString(R.string.feedback));
        titleBarModel.rightTitle.set(JDReadApplication.getInstance().getString(R.string.feedback_history));
    }

    public void commitFeedback() {
        if (StringUtils.isNullOrEmpty(feedback.get()) || feedback.get().length() < 5 || feedback.get().length() > 200) {
            ToastUtils.showToast(JDReadApplication.getInstance(), R.string.feedback_format_error);
            return;
        }

        if (StringUtils.isNullOrEmpty(phone.get()) || !RegularUtil.isMobile(phone.get())) {
            ToastUtils.showToast(JDReadApplication.getInstance(), R.string.phone_number_format_error);
            return;
        }

        submitCommitFeedback(feedback.get(), phone.get());
        titleBarModel.back();
    }

    private void submitCommitFeedback(String desc, String phone) {
        LogCollection logCollection = new LogCollection();
        logCollection.desc = desc;
        logCollection.phone = phone;

        BroadcastHelper.sendFeedbackBroadcast(JDReadApplication.getInstance(),
                String.valueOf(JSONObjectParseUtils.toJson(logCollection)));
        ToastUtils.showToast(JDReadApplication.getInstance(), R.string.feedback_success);
    }
}
