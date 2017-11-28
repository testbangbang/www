package com.onyx.kcb.action;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;

import com.onyx.android.sdk.data.FileErrorPolicy;
import com.onyx.android.sdk.data.FileReplacePolicy;
import com.onyx.android.sdk.data.common.ContentException.FileAskForReplaceException;
import com.onyx.android.sdk.data.common.ContentException.FileCopyException;
import com.onyx.android.sdk.data.common.ContentException.FileCreateException;
import com.onyx.android.sdk.data.common.ContentException.FileDeleteException;
import com.onyx.android.sdk.data.common.ContentException.FileException;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxFileCopyRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.kcb.R;
import com.onyx.kcb.dialog.DialogFileCopy;
import com.onyx.kcb.holder.DataBundle;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by jackdeng on 2017/11/21.
 */
public class FileCopyAction extends BaseAction<DataBundle> {

    private List<File> sourceFileList;
    private File dstDir;
    private boolean isCut;

    private DialogFileCopy dialogFileCopy;
    private RxFileCopyRequest request = null;
    private RxCallback requestCallback = null;

    private FragmentManager fragmentManager;
    private RxCallback rxCallback;

    public FileCopyAction(FragmentManager fragmentManager, List<File> sourceFileList, File dstDir, boolean isCut) {
        this.fragmentManager = fragmentManager;
        this.sourceFileList = sourceFileList;
        this.dstDir = dstDir;
        this.isCut = isCut;
    }

    @Override
    public void execute(final DataBundle dataBundle, final RxCallback rxCallback) {
        this.rxCallback = rxCallback;
        showCopyDialog();
        executeCopyProcess(dataBundle, rxCallback);
    }

    private void showCopyDialog() {
        dialogFileCopy = new DialogFileCopy(isCut, new DialogFileCopy.Callback() {
            @Override
            public void onCopyCancel() {
                notifyCopyFinished();
            }
        });
        dialogFileCopy.show(fragmentManager);
    }

    public void dismissCopyDialog() {
        if (dialogFileCopy != null) {
            dialogFileCopy.dismiss();
            dialogFileCopy = null;
        }
    }

    private void executeCopyProcess(final DataBundle dataBundle, final RxCallback rxCallback) {
        request = new RxFileCopyRequest(dataBundle.getDataManager(), sourceFileList, dstDir, isCut);
        requestCallback = new RxCallback() {
            @Override
            public void onNext(Object o) {
                notifyCopyFinished();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                rxCallback.onError(throwable);
            }
        };
        request.execute(requestCallback);
    }

    private void notifyCopyFinished() {
        cancelTask();
        dismissCopyDialog();
        rxCallback.onNext(request);
    }

    private void cancelTask() {
        if (request != null) {
            request.setAbort(true);
        }
    }

    private Map<String, FileReplacePolicy> getReplacePolicyMap(Context context) {
        Map<String, FileReplacePolicy> map = new LinkedHashMap<>();
        map.put(context.getString(R.string.replace), FileReplacePolicy.Replace);
        map.put(context.getString(R.string.replace_all), FileReplacePolicy.ReplaceAll);
        map.put(context.getString(R.string.skip), FileReplacePolicy.Skip);
        map.put(context.getString(R.string.skip_all), FileReplacePolicy.SkipAll);
        map.put(context.getString(R.string.cancel), FileReplacePolicy.Ask);
        return map;
    }

    private Map<String, FileErrorPolicy> getErrorPolicyMap(Context context) {
        Map<String, FileErrorPolicy> map = new LinkedHashMap<>();
        map.put(context.getString(R.string.retry), FileErrorPolicy.Retry);
        map.put(context.getString(R.string.skip), FileErrorPolicy.Skip);
        map.put(context.getString(R.string.skip_all), FileErrorPolicy.SkipAll);
        map.put(context.getString(R.string.cancel), FileErrorPolicy.Retry);
        return map;
    }

    private void reSendRequest(DataBundle dataBundle) {
        if (request != null) {
            request.execute(requestCallback);
        }
    }

    public void showReplaceAlertDialog(final Context activityContext, final DataBundle dataBundle, File toReplaceFile) {
        final Context appContext = dataBundle.getAppContext();
        final Map<String, FileReplacePolicy> policyMap = getReplacePolicyMap(appContext);
        new AlertDialog.Builder(activityContext).setTitle(appContext.getString(R.string.file_already_exists) + ": " + toReplaceFile)
                .setItems(policyMap.keySet().toArray(new String[0]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileReplacePolicy[] policies = policyMap.values().toArray(new FileReplacePolicy[0]);
                        if (which < policies.length - 1) {
                            request.replacePolicy = policies[which];
                            reSendRequest(dataBundle);
                            return;
                        }
                        notifyCopyFinished();
                    }
                }).show();
    }

    public void showFileErrorAlertDialog(final Context activityContext, final DataBundle dataBundle) {
        final Map<String, FileErrorPolicy> policyMap = getErrorPolicyMap(dataBundle.getAppContext());
        final String[] items = policyMap.keySet().toArray(new String[0]);
        new AlertDialog.Builder(activityContext).setTitle(R.string.copy_file_failed).setItems(items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileErrorPolicy[] policies = policyMap.values().toArray(new FileErrorPolicy[0]);
                        if (which < policies.length - 1) {
                            request.errorPolicy = policies[which];
                            reSendRequest(dataBundle);
                            return;
                        }
                        notifyCopyFinished();
                    }
                }).show();
    }

    public boolean processFileException(Context context, DataBundle dataBundle, Exception e) {
        if (e instanceof FileException) {
            if (e instanceof FileAskForReplaceException) {
                FileAskForReplaceException exception = (FileAskForReplaceException) e;
                showReplaceAlertDialog(context, dataBundle, exception.to);
            }
            else if (e instanceof FileDeleteException || e instanceof FileCopyException ||
                    e instanceof FileCreateException) {
                showFileErrorAlertDialog(context, dataBundle);
            }
            return true;
        }
        dismissCopyDialog();
        return false;
    }
}