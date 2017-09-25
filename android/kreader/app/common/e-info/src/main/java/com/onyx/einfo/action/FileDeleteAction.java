package com.onyx.einfo.action;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.FileErrorPolicy;
import com.onyx.android.sdk.data.common.ContentException;
import com.onyx.android.sdk.data.request.data.fs.FileDeleteRequest;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.einfo.R;
import com.onyx.einfo.holder.LibraryDataHolder;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/9/20.
 */
public class FileDeleteAction extends BaseAction<LibraryDataHolder> {

    private List<File> sourceFileList;
    private OnyxAlertDialog dialogFileRemoving;
    private FileDeleteRequest request = null;
    private BaseCallback requestCallback = null;

    private FragmentManager fragmentManager;
    private BaseCallback baseCallback;

    public FileDeleteAction(Activity activity, List<File> sourceFileList) {
        fragmentManager = activity.getFragmentManager();
        this.sourceFileList = sourceFileList;
    }

    @Override
    public void execute(LibraryDataHolder dataHolder, BaseCallback callback) {
        baseCallback = callback;
        dialogFileRemoving = showFileRemovingDialog();
        request = new FileDeleteRequest(sourceFileList);
        requestCallback = new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    BaseCallback.invoke(baseCallback, request, e);
                    return;
                }
                notifyFinished();
            }
        };
        dataHolder.getDataManager().submit(dataHolder.getContext().getApplicationContext(),
                request, requestCallback);
    }

    private OnyxAlertDialog showFileRemovingDialog() {
        final OnyxAlertDialog dialog = new OnyxAlertDialog();
        dialog.setParams(new OnyxAlertDialog.Params()
                .setEnableTittle(false)
                .setCustomContentLayoutResID(R.layout.dialog_file_removing)
                .setCustomViewAction(new OnyxAlertDialog.CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        customView.findViewById(R.id.button_cancel).setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        notifyFinished();
                                    }
                                });
                    }
                })
                .setCanceledOnTouchOutside(false)
                .setEnableFunctionPanel(false));
        dialog.setDialogEventsListener(new OnyxAlertDialog.DialogEventsListener() {
            @Override
            public void onCancel(OnyxAlertDialog dialog, DialogInterface dialogInterface) {
            }

            @Override
            public void onDismiss(OnyxAlertDialog dialog, DialogInterface dialogInterface) {
                notifyFinished();
            }
        });
        dialog.show(fragmentManager, "FileRemoving");
        return dialog;
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
        dataHolder.getDataManager().submit(dataHolder.getContext().getApplicationContext(), request, requestCallback);
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
                        notifyFinished();
                    }
                }).show();
    }

    private void notifyFinished() {
        cancelTask();
        dismissDialog();
        BaseCallback.invoke(baseCallback, request, null);
    }

    private void cancelTask() {
        if (request != null) {
            request.setAbort();
        }
    }

    private void dismissDialog() {
        if (dialogFileRemoving != null) {
            dialogFileRemoving.dismiss();
            dialogFileRemoving = null;
        }
    }

    // for activity invoke to show dialog
    public boolean processFileException(Context context, LibraryDataHolder dataHolder, Exception e) {
        if (e instanceof ContentException.FileDeleteException) {
            showFileErrorAlertDialog(context, dataHolder);
            return true;
        }
        dismissDialog();
        return false;
    }
}
