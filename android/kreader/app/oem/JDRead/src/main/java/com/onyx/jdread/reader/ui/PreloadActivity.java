package com.onyx.jdread.reader.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.main.action.LoadPreBooksAction;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.model.MainBundle;
import com.onyx.jdread.manager.ManagerActivityUtils;

/**
 * Created by huxiaomao on 2017/12/7.
 */

public class PreloadActivity extends ReaderActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        checkGuide();
        super.onCreate(savedInstanceState);
    }

    private void checkGuide() {
        String flag = JDPreferenceManager.getStringValue(Constants.IS_GUIDE, "");
        if (StringUtils.isNullOrEmpty(flag)) {
            setGuide(true);
            ManagerActivityUtils.startStartActivity(this);
            loadPreBooks();
        }
    }

    private void loadPreBooks() {
        LoadPreBooksAction action = new LoadPreBooksAction();
        action.execute(MainBundle.getInstance(), null);
    }
}
