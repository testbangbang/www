package com.onyx.android.eschool.action;

import android.content.Context;

import com.onyx.android.eschool.dialog.DialogMultiMessage;
import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.eschool.manager.PushMessageHandler;
import com.onyx.android.eschool.model.MessageInfo;
import com.onyx.android.eschool.request.PushMessageFetchRequest;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.List;

/**
 * Created by suicheng on 2018/1/27.
 */
public class PushMessageProcessAction extends BaseAction<LibraryDataHolder> {

    private PushMessageHandler pushMessageHandler;

    public PushMessageProcessAction(PushMessageHandler handler) {
        this.pushMessageHandler = handler;
    }

    @Override
    public void execute(LibraryDataHolder dataHolder, BaseCallback baseCallback) {
        fetchMessageInfoList(dataHolder);
    }

    private void fetchMessageInfoList(LibraryDataHolder dataHolder) {
        final PushMessageFetchRequest obtainRequest = new PushMessageFetchRequest(pushMessageHandler);
        dataHolder.getCloudManager().submitRequest(dataHolder.getContext().getApplicationContext(), obtainRequest,
                new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        showDialogMultiMessage(request.getContext(), obtainRequest.geMessageInfoList());
                    }
                });
    }

    private void showDialogMultiMessage(final Context context, List<MessageInfo> list) {
        if (CollectionUtils.isNullOrEmpty(list)) {
            return;
        }
        final DialogMultiMessage dialog = new DialogMultiMessage(context.getApplicationContext(), list);
        dialog.setItemClickListener(new DialogMultiMessage.OnItemClickListener() {
            @Override
            public void OnItemClick(int index) {
                dialog.removeIndex(index);
            }
        });
        dialog.show();
    }
}
