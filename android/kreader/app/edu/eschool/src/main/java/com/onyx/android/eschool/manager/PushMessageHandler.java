package com.onyx.android.eschool.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.action.ActionContext;
import com.onyx.android.eschool.events.HomeworkEvent;
import com.onyx.android.eschool.model.MessageInfo;
import com.onyx.android.eschool.utils.BroadcastHelper;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.Homework;
import com.onyx.android.sdk.data.model.v2.Homework_Table;
import com.onyx.android.sdk.data.request.cloud.v2.HomeworkUpdateRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.im.data.Message;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/8/2.
 */
public class PushMessageHandler {

    public static final String TYPE_NOTIFY_HOMEWORK = "homework";
    public static final String TYPE_NOTIFY_HOMEWORK_READ_ACTIVE = "homework_readActive";
    public static final String TYPE_NOTIFY_HOMEWORK_END_TIME = "homework_endTime";

    public interface PushCallback {
        void onMessage(Message message);
    }

    private ActionContext actionContext;
    private Map<String, PushCallback> pushTypeFilterMap = new HashMap<>();
    private List<MessageInfo> messageInfoList = Collections.synchronizedList(new ArrayList<MessageInfo>());

    public PushMessageHandler(ActionContext actionContext) {
        this.actionContext = actionContext;
    }

    public void cleanUp() {
    }

    public void addPushTypeFilterCallback(@NonNull String type, @NonNull PushCallback callback) {
        pushTypeFilterMap.put(type, callback);
    }

    public void removePushTypeFilterCallback(@NonNull String type) {
        pushTypeFilterMap.remove(type);
    }

    public synchronized List<MessageInfo> getMessageInfoList() {
        return messageInfoList;
    }

    public synchronized List<MessageInfo> getAndRemoveMessageInfoList() {
        List<MessageInfo> list = new ArrayList<>();
        list.addAll(getMessageInfoList());
        getMessageInfoList().clear();
        return list;
    }

    public void processMessage(Message message) {
        if (message == null || StringUtils.isNullOrEmpty(message.getType())) {
            return;
        }
        if (processPushTypeFilter(message)) {
            return;
        }
        String type = message.getType();
        boolean success = false;
        switch (type) {
            case TYPE_NOTIFY_HOMEWORK:
                success = processHomework(message);
                break;
            case TYPE_NOTIFY_HOMEWORK_READ_ACTIVE:
                success = processHomeworkReadActive(message);
                break;
            case TYPE_NOTIFY_HOMEWORK_END_TIME:
                success = processHomeworkEndTime(message);
                break;
            case Message.TYPE_NOTIFICATION:
            case Message.TYPE_TEXT:
                success = processText(message);
                break;
            default:
                break;
        }
        if (success) {
            BroadcastHelper.sendStatusBarMessageShowBroadcast(getContext());
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

    private void updateHomeworkAndBroadcast(String id, SQLOperator[] sqlOperators,
                                            final String broadcastType, final String broadcastData,
                                            final BaseCallback baseCallback) {
        HomeworkUpdateRequest homeworkUpdateRequest = new HomeworkUpdateRequest(id, sqlOperators);
        getCloudManager().submitRequest(getContext(), homeworkUpdateRequest,
                new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        BroadcastHelper.sendNotificationBroadcast(getContext(), broadcastType,
                                broadcastData);
                        BaseCallback.invoke(baseCallback, request, e);
                    }
                });
    }

    private boolean processHomeworkEndTime(final Message message) {
        final Homework homework = getHomeworkFromMessage(message);
        if (homework == null) {
            return false;
        }
        String content = String.format(getContext().getString(R.string.homework_delay_format),
                homework.child.title, DateTimeUtil.formatGMTDate(homework.endTime,
                        DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMM));
        addMessageInfo(MessageInfo.create(getHomeworkInfoTitle(homework), content, message));
        updateHomeworkAndBroadcast(homework._id,
                new SQLOperator[]{Homework_Table.endTime.eq(homework.endTime)},
                TYPE_NOTIFY_HOMEWORK_END_TIME, message.getContent(), null);
        return true;
    }

    private boolean processHomeworkReadActive(final Message message) {
        final Homework homework = getHomeworkFromMessage(message);
        if (homework == null) {
            return false;
        }
        String content = String.format(getContext().getString(R.string.homework_answer_format),
                homework.child.title);
        addMessageInfo(MessageInfo.create(getHomeworkInfoTitle(homework), content, message));
        updateHomeworkAndBroadcast(homework._id,
                new SQLOperator[]{Homework_Table.readActive.eq(homework.readActive)},
                TYPE_NOTIFY_HOMEWORK_READ_ACTIVE, message.getContent(), null);
        return true;
    }

    private boolean processHomework(final Message message) {
        final Homework homework = getHomeworkFromMessage(message);
        if (homework == null) {
            return false;
        }
        EventBus.getDefault().post(new HomeworkEvent(homework));

        int resId = homework.checked ? R.string.homework_check_format : R.string.homework_set_format;
        String content = String.format(getContext().getString(resId), homework.child.title);
        addMessageInfo(MessageInfo.create(getHomeworkInfoTitle(homework), content, message));

        if (homework.checked) {
            updateHomeworkAndBroadcast(homework._id,
                    new SQLOperator[]{Homework_Table.checked.eq(true)},
                    TYPE_NOTIFY_HOMEWORK, message.getContent(), null);
        }
        return true;
    }

    private boolean processText(Message message) {
        MessageInfo info = MessageInfo.create(getContext().getString(R.string.notification),
                message.getContent(), message);
        messageInfoList.add(info);
        return true;
    }

    private Context getContext() {
        return actionContext.context;
    }

    private CloudManager getCloudManager() {
        return actionContext.cloudManager;
    }

    private String getHomeworkInfoTitle(final Homework homework) {
        return String.format(getContext().getString(R.string.homework_format), homework.child.getSubjectName());
    }

    private void addMessageInfo(MessageInfo messageInfo) {
        getMessageInfoList().add(messageInfo);
    }

    private Homework getHomeworkFromMessage(Message message) {
        final Homework homework = JSONObjectParseUtils.parseObject(message.getContent(), Homework.class);
        if (!Homework.checkValid(homework)) {
            dumpInvalidMessage(Homework.class);
            return null;
        }
        return homework;
    }

    private void dumpInvalidMessage(Class clazz) {
        Log.e(getClass().getSimpleName(), "detect the invalid " + clazz.getSimpleName());
    }
}
