package com.onyx.jdread.setting.model;

import android.databinding.ObservableField;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.setting.action.TranslateAction;
import com.onyx.jdread.setting.event.BackToReadingToolsEvent;
import com.onyx.jdread.setting.ui.TranslateFragment;

import java.util.Observable;

/**
 * Created by hehai on 18-1-15.
 */

public class TranslateModel extends Observable {
    public final TitleBarModel titleBarModel = new TitleBarModel(SettingBundle.getInstance().getEventBus());
    public final ObservableField<String> inputWords = new ObservableField<>();
    public final ObservableField<String> translateResult = new ObservableField<>();
    public final ObservableField<String> inputLanguage = new ObservableField<>(JDReadApplication.getInstance().getString(R.string.chinese));
    public final ObservableField<String> targetLanguage = new ObservableField<>(JDReadApplication.getInstance().getString(R.string.english));

    public TranslateModel() {
        titleBarModel.title.set(JDReadApplication.getInstance().getString(R.string.translation));
        titleBarModel.backEvent.set(new BackToReadingToolsEvent());
    }

    public void translate() {
        final TranslateAction action = new TranslateAction(inputWords.get());
        action.execute(SettingBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                translateResult.set(action.getTranslateResult());
            }
        });
    }
}
