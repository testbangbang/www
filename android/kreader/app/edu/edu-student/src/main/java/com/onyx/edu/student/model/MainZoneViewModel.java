package com.onyx.edu.student.model;

import android.databinding.BaseObservable;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.graphics.Bitmap;

import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

/**
 * Created by suicheng on 2017/10/25.
 */

public class MainZoneViewModel extends BaseObservable {
    public final ObservableField<NeoAccountBase> account = new ObservableField<>();
    public final ObservableField<String> name = new ObservableField<>();
    public final ObservableField<String> group = new ObservableField<>();
    public final ObservableField<String> currentDate = new ObservableField<>();
    public final ObservableField<String> currentWeek = new ObservableField<>();
    public final ObservableField<Bitmap> avatarImage = new ObservableField<>();
    public final ObservableField<Bitmap> qrCodeImage = new ObservableField<>();

    private final EventBus eventBus;

    public MainZoneViewModel(EventBus eventBus) {
        this.eventBus = eventBus;
        account.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                updateAccountInfo(getAccount());
                updateDateAndWeek();
            }
        });
        updateDateAndWeek();
    }

    private void updateAccountInfo(NeoAccountBase account) {
        if (account == null) {
            return;
        }
        name.set(account.getName());
        String theGroup = account.getFirstGroup();
        if (StringUtils.isNullOrEmpty(theGroup)) {
            theGroup = account.orgName;
        }
        group.set(theGroup);
    }

    public void updateDateAndWeek() {
        currentDate.set(DateTimeUtil.DATE_FORMAT_YYYYMMDD_2.format(new Date()));
        currentWeek.set(DateTimeUtil.DATE_FORMAT_WEEK.format(new Date()));
    }

    public void updateAvatar(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        avatarImage.set(bitmap);
    }

    public void updateQrCodeImage(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        qrCodeImage.set(bitmap);
    }

    public NeoAccountBase getAccount() {
        return account.get();
    }

    public void setAccount(NeoAccountBase account) {
        this.account.set(account);
    }
}
