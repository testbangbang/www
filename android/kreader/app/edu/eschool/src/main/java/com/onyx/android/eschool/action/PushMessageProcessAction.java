package com.onyx.android.eschool.action;

import android.content.Context;

import com.onyx.android.eschool.dialog.DialogMultiMessage;
import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.eschool.model.MessageInfo;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.List;

/**
 * Created by suicheng on 2018/1/27.
 */
public class PushMessageProcessAction extends BaseAction<LibraryDataHolder> {

    private List<MessageInfo> list;

    public PushMessageProcessAction(List<MessageInfo> list) {
        this.list = list;
    }

    @Override
    public void execute(LibraryDataHolder dataHolder, BaseCallback baseCallback) {
        showDialogMultiMessage(dataHolder.getContext(), list);
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
