package com.onyx.jdread.setting.model;

import android.databinding.ObservableField;
import android.graphics.Bitmap;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.setting.event.BackToDeviceConfigEvent;
import com.onyx.jdread.setting.request.RxLockPswFindQrCodeRequest;
import com.onyx.jdread.shop.common.CloudApiContext;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by suicheng on 2018/2/8.
 */

public class PswFindModel {
    public final TitleBarModel titleBarModel = new TitleBarModel(SettingBundle.getInstance().getEventBus());
    public final ObservableField<String> qrCodeTip = new ObservableField<>();
    public final ObservableField<Bitmap> qrCodeImage = new ObservableField<>();
    private final EventBus eventBus;

    public PswFindModel(EventBus eventBus) {
        this.eventBus = eventBus;
        titleBarModel.title.set(JDReadApplication.getInstance().getString(R.string.password_find));
        titleBarModel.backEvent.set(new BackToDeviceConfigEvent());
    }

    public void onRefreshClick() {
        bindQrCodeImage(null);
        final RxLockPswFindQrCodeRequest request = new RxLockPswFindQrCodeRequest(
                CloudApiContext.ONYX_EINK_HOST,
                ResManager.getDimens(R.dimen.fragment_find_password_qr_image_size),
                ResManager.getDimens(R.dimen.fragment_find_password_qr_image_size));
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                bindQrCodeImage(request.getQrImage());
            }

            @Override
            public void onError(Throwable throwable) {
                ToastUtil.showToast(R.string.import_fail);
            }
        });
    }

    private void bindQrCodeImage(Bitmap imageBitmap) {
        qrCodeTip.set(imageBitmap == null ? ResManager.getString(R.string.find_password_qr_generating) :
                ResManager.getString(R.string.find_password_qr_scan));
        qrCodeImage.set(imageBitmap);
    }

    public void onUnlockClick() {
        eventBus.post(new BackToDeviceConfigEvent());
    }
}
