package com.onyx.jdread.setting.model;

import android.databinding.ObservableField;

import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.model.TitleBarModel;
import com.onyx.jdread.setting.event.BackToHelpFragmentEvent;
import com.onyx.jdread.util.RegularUtil;

import java.util.Observable;

/**
 * Created by hehai on 17-12-29.
 */

public class ContactUsModel extends Observable {
    public final TitleBarModel titleBarModel = new TitleBarModel(SettingBundle.getInstance().getEventBus());

    public final ObservableField<String> feedback = new ObservableField<>();
    public final ObservableField<String> phone = new ObservableField<>();

}
