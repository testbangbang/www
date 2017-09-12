package com.onyx.einfo.action;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.FileErrorPolicy;
import com.onyx.android.sdk.data.FileReplacePolicy;
import com.onyx.android.sdk.data.common.ContentException;
import com.onyx.android.sdk.data.request.data.fs.FileCopyRequest;
import com.onyx.einfo.R;
import com.onyx.einfo.dialog.DialogFileCopy;
import com.onyx.einfo.holder.LibraryDataHolder;
import com.onyx.android.sdk.data.common.ContentException.*;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/9/12.
 */
public class FileCopyAction extends BaseAction<LibraryDataHolder> {

    private List<File> sourceFileList;
    private File dstDir;
    private boolean isCut;

    private DialogFileCopy dialogFileCopy;
    private FileCopyRequest request = null;
    private BaseCallback requestCallback = null;

    private FragmentManager fragmentManager;
    private BaseCallback baseCallback;

    public FileCopyAction(Activity activity, List<File> sourceFileList, File dstDir, boolean isCut) {
        fragmentManager = activity.getFragmentManager();
        this.sourceFileList = sourceFileList;
        this.dstDir = dstDir;
        this.isCut = isCut;
    }

    @Override
    public void execute(final LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        this.baseCallback = baseCallback;
        showCopyDialog();
        executeCopyProcess(dataHolder, baseCallback);
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

    private void executeCopyProcess(final LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        request = new FileCopyRequest(sourceFileList, dstDir, isCut);
        requestCallback = new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    notifyCopyFinished();
                    return;
                }
                BaseCallback.invoke(baseCallback, request, e);
            }
        };
        dataHolder.getDataManager().submitToMulti(dataHolder.getContext().getApplicationContext(),
                request, requestCallback);
    }

    private void notifyCopyFinished() {
        cancelTask();
        dismissCopyDialog();
        BaseCallback.invoke(baseCallback, request, null);
    }

    private void cancelTask() {
        if (request != null) {
            request.setAbort();
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

    private void reSendRequest(LibraryDataHolder dataHolder) {
        request.setException(null);
        dataHolder.getDataManager().submitToMulti(dataHolder.getContext().getApplicationContext(), request, requestCallback);
    }

    public void showReplaceAlertDialog(final Context activityContext, final LibraryDataHolder dataHolder, File toReplaceFile) {
        final Context appContext = dataHolder.getContext().getApplicationContext();
        final Map<String, FileReplacePolicy> policyMap = getReplacePolicyMap(appContext);
        new AlertDialog.Builder(activityContext).setTitle(appContext.getString(R.string.file_already_exists) + ": " + toReplaceFile)
                .setItems(policyMap.keySet().toArray(new String[0]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileReplacePolicy[] policies = policyMap.values().toArray(new FileReplacePolicy[0]);
                        if (which < policies.length - 1) {
                            request.replacePolicy = policies[which];
                            reSendRequest(dataHolder);
                            return;
                        }
                        notifyCopyFinished();
                    }
                }).show();
    }

    public void showFileErrorAlertDialog(final Context activityContext, final LibraryDataHolder dataHolder) {
        final Map<String, FileErrorPolicy> policyMap = getErrorPolicyMap(dataHolder.getContext());
        final String[] items = policyMap.keySet().toArray(new String[0]);
        new AlertDialog.Builder(activityContext).setTitle(R.string.copy_file_failed).setItems(items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileErrorPolicy[] policies = policyMap.values().toArray(new FileErrorPolicy[0]);
                        if (which < policies.length - 1) {
                            request.errorPolicy = policies[which];
                            reSendRequest(dataHolder);
                            return;
                        }
                        notifyCopyFinished();
                    }
                }).show();
    }

    // for activity invoke to show dialog
    public boolean processFileException(Context context, LibraryDataHolder dataHolder, Exception e) {
        if (e instanceof FileException) {
            if (e instanceof FileAskForReplaceException) {
                ContentException.FileAskForReplaceException exception = (FileAskForReplaceException) e;
                showReplaceAlertDialog(context, dataHolder, exception.to);
            } else if (e instanceof FileDeleteException || e instanceof FileCopyException ||
                    e instanceof FileCreateException) {
                showFileErrorAlertDialog(context, dataHolder);
            }
            return true;
        }
        return false;
    }
}
