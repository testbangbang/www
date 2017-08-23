package com.onyx.android.eschool.manager;

import android.content.Context;
import android.content.Intent;

import com.onyx.android.eschool.SchoolApp;
import com.onyx.android.eschool.action.AuthTokenAction;
import com.onyx.android.eschool.events.PushNotificationEvent;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.action.ActionContext;
import com.onyx.android.sdk.data.action.push.PushFileDownloadAction;
import com.onyx.android.sdk.data.action.push.PushLibraryClearAction;
import com.onyx.android.sdk.data.action.push.PushProductDownloadAction;
import com.onyx.android.sdk.data.model.BaseData;
import com.onyx.android.sdk.data.model.v2.PushFileEvent;
import com.onyx.android.sdk.data.model.v2.PushLibraryClearEvent;
import com.onyx.android.sdk.data.model.v2.PushNotification;
import com.onyx.android.sdk.data.model.v2.PushProductEvent;
import com.onyx.android.sdk.data.model.v2.PushTextEvent;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.im.data.Message;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.raizlabs.android.dbflow.annotation.NotNull;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by suicheng on 2017/8/2.
 */
public class PushManager {

    public interface PushCallback {
        void onMessage(Message message);
    }

    private ActionContext actionContext;
    private Map<String, PushCallback> pushTypeFilterMap = new HashMap<>();

    public PushManager(ActionContext actionContext) {
        this.actionContext = actionContext;
    }

    public void addPushTypeFilterCallback(@NotNull String type, @NotNull PushCallback callback) {
        pushTypeFilterMap.put(type, callback);
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
            case Message.TYPE_TEXT:
                processTextMessage(message);
                break;
            case Message.TYPE_FILE_DOWNLOAD:
                processFileDownloadMessage(message);
                break;
            case Message.TYPE_PRODUCT_DOWNLOAD:
                processProductDownloadMessage(message);
                break;
            case Message.TYPE_LIBRARY_CLEAR:
                processLibraryClear(message);
                break;
            case Message.TYPE_LOGIN:
                processLogin(message);
                break;
            case Message.TYPE_NOTIFICATION:
                processNotification(message);
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
        return false;
    }

    private void processNotification(Message message) {
        PushNotification pushNotification = JSONObjectParseUtils.parseObject(message.getContent(), PushNotification.class);
        BaseData.asyncSave(pushNotification);
        if (pushNotification != null) {
            EventBus.getDefault().post(new PushNotificationEvent(pushNotification));
        }
    }

    private void processLogin(Message message) {
        AuthTokenAction authTokenAction = new AuthTokenAction();
        authTokenAction.setLoadOnlyFromCloud(true);
        authTokenAction.execute(SchoolApp.getLibraryDataHolder(), null);
    }

    private void processLibraryClear(Message message) {
        PushLibraryClearEvent libraryClear = JSONObjectParseUtils.parseObject(message.getContent(), PushLibraryClearEvent.class);
        PushLibraryClearAction clearAction = new PushLibraryClearAction(libraryClear);
        clearAction.execute(actionContext, null);
    }

    private void processTextMessage(Message message) {
        PushTextEvent pushText = JSONObjectParseUtils.parseObject(message.getContent(), PushTextEvent.class);
        BaseData.asyncSave(pushText);
    }

    private void processFileDownloadMessage(Message message) {
        final PushFileEvent pushFile = JSONObjectParseUtils.parseObject(message.getContent(), PushFileEvent.class);
        if (pushFile != null) {
            String fileName = FileUtils.fixNotAllowFileName(pushFile.getFileNameWithExtension());
            if (StringUtils.isNotBlank(fileName)) {
                pushFile.filePath = new File(PushFileDownloadAction.getDefaultPushDir(), fileName).getAbsolutePath();
            }
        }
        final PushFileDownloadAction fileDownloadAction = new PushFileDownloadAction(pushFile);
        fileDownloadAction.execute(actionContext, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null || !fileDownloadAction.isMd5CheckSuccess()) {
                    return;
                }
                if (!PushFileEvent.checkOpenAfterLoaded(pushFile)) {
                    return;
                }
                openDownloadedFile(actionContext.context, pushFile.filePath);
            }
        });
    }

    private void processProductDownloadMessage(final Message message) {
        final PushProductEvent pushProduct = JSONObjectParseUtils.parseObject(message.getContent(), PushProductEvent.class);
        final PushProductDownloadAction fileDownloadAction = new PushProductDownloadAction(pushProduct);
        fileDownloadAction.execute(actionContext, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null || !fileDownloadAction.isMd5CheckSuccess()) {
                    return;
                }
                if (!PushProductEvent.checkOpenAfterLoaded(pushProduct)) {
                    return;
                }
                openDownloadedFile(actionContext.context, pushProduct.metadata.getNativeAbsolutePath());
            }
        });
    }

    public static void openDownloadedFile(Context context, String filePath) {
        Intent intent = ViewDocumentUtils.viewActionIntentWithMimeType(new File(filePath),
                Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityUtil.startActivitySafely(context, intent,
                ViewDocumentUtils.getEduReaderComponentName(context));
    }
}
