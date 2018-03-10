package com.onyx.jdread.main.model;

import com.onyx.jdread.personal.model.PersonalViewModel;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by li on 2018/3/9.
 */

public class StartBundle {
    private static StartBundle startBundle;
    private EventBus eventBus = EventBus.getDefault();
    private PersonalViewModel personalViewModel;

    public static StartBundle getInstance() {
        if (startBundle == null) {
            startBundle = new StartBundle();
        }
        return startBundle;
    }

    public PersonalViewModel getPersonalViewModel() {
        if (personalViewModel == null) {
            personalViewModel = new PersonalViewModel(getEventBus());
        }
        return personalViewModel;
    }

    public EventBus getEventBus() {
        return eventBus;
    }
}
