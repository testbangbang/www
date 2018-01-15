package com.onyx.jdread.setting.model;

import android.databinding.ObservableField;

import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.main.util.RegularUtil;
import com.onyx.jdread.setting.event.BackToHelpFragmentEvent;

import org.acra.ACRA;
import org.acra.ErrorReporter;

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

        ErrorReporter reporter = ACRA.getErrorReporter();
        reporter.putCustomData(phone.get(), feedback.get());
        reporter.handleException(null);
    }
}
