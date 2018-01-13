package com.onyx.android.eschool.manager;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.Log;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.action.ActionContext;
import com.onyx.android.eschool.events.HomeworkEvent;
import com.onyx.android.eschool.utils.BroadcastHelper;
import com.onyx.android.eschool.utils.DialogHelper;
import com.onyx.android.eschool.utils.IntentUtils;
import com.onyx.android.sdk.data.model.v2.Homework;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.im.data.Message;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by suicheng on 2017/8/2.
 */
public class PushManager {

    public static final String TYPE_NOTIFY_HOMEWORK = "homework";

    public interface PushCallback {
        void onMessage(Message message);
    }

    private ActionContext actionContext;
    private Map<String, PushCallback> pushTypeFilterMap = new HashMap<>();

    public PushManager(ActionContext actionContext) {
        this.actionContext = actionContext;
    }

    public void addPushTypeFilterCallback(@NonNull String type, @NonNull PushCallback callback) {
        pushTypeFilterMap.put(type, callback);
    }

    public void removePushTypeFilterCallback(@NonNull String type) {
        pushTypeFilterMap.remove(type);
    }

    public void processMessage(Message message) {
        if (message == null || StringUtils.isNullOrEmpty(message.getType())) {
            return;
        }
        if (processPushTypeFilter(message)) {
            return;
        }
        String type = message.getType();
        switch (type) {
            case TYPE_NOTIFY_HOMEWORK:
                processHomework(message);
                break;
            case Message.TYPE_NOTIFICATION:
            case Message.TYPE_TEXT:
                processText(message);
                break;
            default:
                break;
        }
    }

    private boolean processPushTypeFilter(Message message) {
        if (CollectionUtils.isNullOrEmpty(pushTypeFilterMap)) {
            return false;
        }
        String type = message.getType();
        if (pushTypeFilterMap.containsKey(type)) {
            pushTypeFilterMap.get(type).onMessage(message);
            return true;
        }
        for (String eachKey : pushTypeFilterMap.keySet()) {
            if (eachKey.contains(type)) {
                pushTypeFilterMap.get(type).onMessage(message);
                return true;
            }
        }
        return false;
    }

    private void processHomework(Message message) {
        final Homework homework = JSONObjectParseUtils.parseObject(message.getContent(), Homework.class);
        if (!Homework.checkValid(homework)) {
            dumpInvalidMessage(Homework.class);
            return;
        }
        BroadcastHelper.sendNotificationBroadcast(actionContext.context, BroadcastHelper.NOTIFICATION_TYPE_HOMEWORK,
                message.getContent());
        EventBus.getDefault().post(new HomeworkEvent(homework));
        if (homework.checked) {
            return;
        }
        showHomeworkNotificationDialog(actionContext.context, homework);
    }

    private void showHomeworkNotificationDialog(final Context context, final Homework homework) {
        String title = String.format(context.getString(R.string.homework_format), homework.child.getSubjectName());
        String content = String.format(context.getString(R.string.homework_set_format), homework.child.title);
        DialogHelper.getAlertDialog(actionContext.context, title, content, context.getString(R.string.goto_look),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean success = IntentUtils.startToHomework(actionContext.context, homework);
                        if (!success) {
                            ToastUtils.showToast(actionContext.context, R.string.app_no_installed);
                            return;
                        }
                        dialog.dismiss();
                    }
                }).show();
    }

    private void processText(Message message) {
        DialogHelper.getAlertDialog(actionContext.context,
                actionContext.context.getString(R.string.notification), message.getContent(),
                actionContext.context.getString(android.R.string.ok), null).show();
    }

    private void dumpInvalidMessage(Class clazz) {
        Log.e(getClass().getSimpleName(), "detect the invalid " + clazz.getSimpleName());
    }
}
