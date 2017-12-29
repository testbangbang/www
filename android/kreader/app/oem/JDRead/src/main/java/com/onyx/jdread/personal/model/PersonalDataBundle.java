package com.onyx.jdread.personal.model;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.jdread.common.AppBaseInfo;
import com.onyx.jdread.shop.model.BookDetailViewModel;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jackdeng on 2017/12/26.
 */

public class PersonalDataBundle {
    private static PersonalDataBundle personalDataBundle;
    private EventBus eventBus;
    private PersonalViewModel personalViewModel;
    private AppBaseInfo appBaseInfo;
    private DataManager dataManager;

    private PersonalDataBundle() {

    }

    public static PersonalDataBundle getInstance() {
        if (personalDataBundle == null) {
            synchronized (BookDetailViewModel.class) {
                if (personalDataBundle == null) {
                    personalDataBundle = new PersonalDataBundle();
                }
            }
        }
        return personalDataBundle;
    }

    public EventBus getEventBus() {
        if (eventBus == null) {
            eventBus = EventBus.getDefault();
        }
        return eventBus;
    }

    public PersonalViewModel getPersonalViewModel() {
        if (personalViewModel == null) {
            synchronized (PersonalViewModel.class) {
                if (personalViewModel == null) {
                    personalViewModel = new PersonalViewModel(getEventBus());
                }
            }
        }
        return personalViewModel;
    }

    public AppBaseInfo getAppBaseInfo() {
        if (appBaseInfo == null) {
            synchronized (AppBaseInfo.class) {
                if (appBaseInfo == null) {
                    appBaseInfo = new AppBaseInfo();
                }
            }
        }
        return appBaseInfo;
    }

    public DataManager getDataManager() {
        if (dataManager == null) {
            synchronized (DataManager.class) {
                if (dataManager == null) {
                    dataManager = new DataManager();
                }
            }
        }
        return dataManager;
    }
}