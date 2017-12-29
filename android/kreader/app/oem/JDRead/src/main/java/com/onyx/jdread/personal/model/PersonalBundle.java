package com.onyx.jdread.personal.model;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by li on 2017/12/28.
 */

public class PersonalBundle {
    private EventBus eventBus = EventBus.getDefault();
    private static PersonalBundle bundle;
    private PersonalModel personalModel;

    public EventBus getEventBus() {
        return eventBus;
    }

    public PersonalModel getPersonalModel() {
        if (personalModel == null) {
            personalModel = new PersonalModel();
            personalModel.loadPersonalData();
        }
        return personalModel;
    }

    public static PersonalBundle getBundle() {
        if (bundle == null) {
            bundle = new PersonalBundle();
        }
        return bundle;
    }
}
