package com.onyx.edu.homework.data;

import com.onyx.edu.homework.receiver.OnyxNotificationReceiver;

/**
 * Created by lxm on 2018/1/19.
 */

public enum NotificationType {

    OTHER, HOMEWORK, HOMEWORK_READER_ACTIVE, HOMEWORK_END_TIME;

    public static NotificationType getNotificationType(String type) {
        switch (type) {
            case OnyxNotificationReceiver.TYPE_NOTIFY_HOMEWORK:
                return HOMEWORK;
            case OnyxNotificationReceiver.TYPE_NOTIFY_HOMEWORK_READ_ACTIVE:
                return HOMEWORK_READER_ACTIVE;
            case OnyxNotificationReceiver.TYPE_NOTIFY_HOMEWORK_END_TIME:
                return HOMEWORK_END_TIME;
        }
        return OTHER;
    }

    public boolean isHomeWorkNotifyType() {
        return this != OTHER;
    }

}
