package com.onyx.jdread.main.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.library.request.ClearNotBelongsToMyBooksRequest;
import com.onyx.jdread.main.common.ClientUtils;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.model.MainBundle;

import jd.wjlogin_sdk.common.WJLoginHelper;

/**
 * Created by hehai on 18-3-16.
 */

public class ClearNotBelongsToMyBooksAction extends BaseAction<MainBundle> {
    @Override
    public void execute(final MainBundle dataBundle, RxCallback baseCallback) {
        WJLoginHelper wjLoginHelper = ClientUtils.getWJLoginHelper();
        if (JDPreferenceManager.getBooleanValue(R.string.login_success_key, false)
                && StringUtils.isNotBlank(wjLoginHelper.getPin())) {
            ClearNotBelongsToMyBooksRequest request = new ClearNotBelongsToMyBooksRequest(dataBundle.getDataManager(), wjLoginHelper.getPin());
            request.execute(new RxCallback() {
                @Override
                public void onNext(Object o) {

                }

                @Override
                public void onFinally() {
                    super.onFinally();
                    hideLoadingDialog(dataBundle);
                }
            });
            showLoadingDialog(dataBundle, R.string.jd_loading);
        }
    }
}
