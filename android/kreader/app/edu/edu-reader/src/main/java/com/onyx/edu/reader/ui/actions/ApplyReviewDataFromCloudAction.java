package com.onyx.edu.reader.ui.actions;

import android.content.Context;
import android.widget.Toast;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.reader.R;
import com.onyx.edu.reader.note.actions.SaveReviewDataAction;
import com.onyx.edu.reader.ui.data.ExceptionMessage;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 2017/6/16.
 */

public class ApplyReviewDataFromCloudAction {

    public static void apply(final ReaderDataHolder readerDataHolder, final boolean showTips) {
        if (readerDataHolder.getReader() == null || readerDataHolder.getReader().getDocument() == null) {
            return;
        }
        if (!readerDataHolder.hasFormField()) {
            return;
        }

        final GetDocumentDataFromCloudChain cloudChain = new GetDocumentDataFromCloudChain();
        cloudChain.execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                String errorMessage = cloudChain.getErrorMessage();
                if (!StringUtils.isNullOrEmpty(errorMessage) || e!=null) {
                    if (showTips) {
                        Toast.makeText(readerDataHolder.getContext(), readerDataHolder.getContext().getString(R.string.update_fail), Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                String reviewDocumentData = cloudChain.getReviewDocumentData();
                new SaveReviewDataAction(reviewDocumentData, readerDataHolder.getReader().getDocumentMd5()).execute(readerDataHolder, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        processSaveReviewException(readerDataHolder, showTips, e);
                    }
                });
            }
        });
    }


    private static void processSaveReviewException(final ReaderDataHolder readerDataHolder, final boolean showTips, final Throwable e) {
        if (showTips) {
            if (e != null) {
                if (e instanceof ReaderException) {
                    ReaderException exception = (ReaderException) e;
                    String message = ExceptionMessage.getString(readerDataHolder.getContext(), exception.getCode());
                    if (!StringUtils.isNullOrEmpty(message)) {
                        Toast.makeText(readerDataHolder.getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                }
            }else {
                Toast.makeText(readerDataHolder.getContext(), readerDataHolder.getContext().getString(R.string.synchronization_success), Toast.LENGTH_SHORT).show();
            }

        }
    }

}
