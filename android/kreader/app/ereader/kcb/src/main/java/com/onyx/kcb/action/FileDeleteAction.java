package com.onyx.kcb.action;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.sdk.data.FileErrorPolicy;
import com.onyx.android.sdk.data.common.ContentException;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxFileDeleteRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.kcb.R;
import com.onyx.kcb.holder.DataBundle;
import com.onyx.kcb.utils.Constant;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jackdeng on 2017/11/21.
 */
public class FileDeleteAction extends BaseAction<DataBundle> {

    private List<File> sourceFileList;
    private OnyxAlertDialog dialogFileRemoving;
    private RxFileDeleteRequest request = null;
    private RxCallback requestCallback = null;

    private FragmentManager fragmentManager;
    private RxCallback rxCallback;

    public FileDeleteAction(Activity activity, List<File> sourceFileList) {
        fragmentManager = activity.getFragmentManager();
        this.sourceFileList = sourceFileList;
    }

    @Override
    public void execute(DataBundle dataHolder, RxCallback callback) {
        rxCallback = callback;
        dialogFileRemoving = showFileRemovingDialog();
        request = new RxFileDeleteRequest(dataHolder.getDataManager(), sourceFileList);
        requestCallback = new RxCallback() {
            @Override
            public void onNext(Object o) {
                notifyFinished();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (throwable != null) {
                    rxCallback.onNext(throwable);
                }
            }
        };
        request.execute(requestCallback);
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
        dialog.show(fragmentManager, Constant.DIALOG_TAG_FILE_REMOVING);
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

    private void reSendRequest(DataBundle dataHolder) {
        if (request != null) {
            request.execute(requestCallback);
        }
    }

    public void showFileErrorAlertDialog(final Context activityContext, final DataBundle dataHolder) {
        final Map<String, FileErrorPolicy> policyMap = getErrorPolicyMap(dataHolder.getAppContext());
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
        rxCallback.onNext(request);
    }

    private void cancelTask() {
        if (request != null) {
            request.setAbort(true);
        }
    }

    private void dismissDialog() {
        if (dialogFileRemoving != null) {
            dialogFileRemoving.dismiss();
            dialogFileRemoving = null;
        }
    }

    public boolean processFileException(Context context, DataBundle dataHolder, Exception e) {
        if (e instanceof ContentException.FileDeleteException) {
            showFileErrorAlertDialog(context, dataHolder);
            return true;
        }
        dismissDialog();
        return false;
    }
}