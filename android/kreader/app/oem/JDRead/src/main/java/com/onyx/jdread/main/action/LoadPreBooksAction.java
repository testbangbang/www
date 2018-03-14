package com.onyx.jdread.main.action;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.library.request.RxLoadPreBooksRequest;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.model.MainBundle;

/**
 * Created by hehai on 18-3-14.
 */

public class LoadPreBooksAction extends BaseAction<MainBundle> {

    @Override
    public void execute(MainBundle dataBundle, RxCallback baseCallback) {
        if (JDPreferenceManager.getBooleanValue(R.string.preload_books_key, false)) {
            return;
        }
        JDPreferenceManager.setBooleanValue(R.string.preload_books_key, true);
        RxLoadPreBooksRequest request = new RxLoadPreBooksRequest(new DataManager(), Constants.SYSTEM_PRE_BOOKS_DIR);
        RxLoadPreBooksRequest.setAppContext(dataBundle.getAppContext());
        request.execute(baseCallback);
    }
}
