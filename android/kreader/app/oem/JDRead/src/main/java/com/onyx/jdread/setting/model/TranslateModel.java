package com.onyx.jdread.setting.model;

import android.databinding.ObservableField;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.manager.KSICibaTranslate;
import com.onyx.jdread.setting.ui.TranslateFragment;

import java.util.Observable;

/**
 * Created by hehai on 18-1-15.
 */

public class TranslateModel extends Observable {
    public final TitleBarModel titleBarModel = new TitleBarModel(SettingBundle.getInstance().getEventBus());
    public final ObservableField<String> inputWords = new ObservableField<>();
    public final ObservableField<String> translateResult = new ObservableField<>();

    public TranslateModel() {
        titleBarModel.title.set(JDReadApplication.getInstance().getString(R.string.translation));
        titleBarModel.backEvent.set(new TranslateFragment());
    }

    public void translate() {
        KSICibaTranslate.getInstance().getTranslateResult(inputWords.get(), new KSICibaTranslate.OnyxIKSCibaQueryResult(new KSICibaTranslate.OnyxIKSCibaQueryResult.TranslateResult() {
            @Override
            public void translateResult(String result) {
                translateResult.set(result);
            }
        }));
    }
}
