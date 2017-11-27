package com.onyx.kcb.action;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;

import com.onyx.android.sdk.data.AppDataInfo;
import com.onyx.android.sdk.data.model.common.AppPreference;
import com.onyx.android.sdk.data.provider.SystemConfigProvider;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxFileOpenWithRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.MimeTypeUtils;
import com.onyx.kcb.R;
import com.onyx.kcb.device.DeviceConfig;
import com.onyx.kcb.dialog.DialogApplicationOpenList;
import com.onyx.kcb.holder.DataBundle;
import com.onyx.kcb.utils.Constant;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.onyx.android.sdk.data.provider.SystemConfigProvider.KEY_APP_PREFERENCE;

/**
 * Created by jackdeng on 2017/11/21.
 */
public class FileOpenWithAction extends BaseAction<DataBundle> {

    private File file;
    private FragmentManager fragmentManager;

    private List<AppDataInfo> dataInfoList = new ArrayList<>();
    private int preferenceIndex = -1;

    public List<AppDataInfo> getAppDataInfoList() {
        return dataInfoList;
    }

    public int getPreferenceIndex() {
        return preferenceIndex;
    }

    public FileOpenWithAction(Activity activity, File file) {
        this.fragmentManager = activity.getFragmentManager();
        this.file = file;
    }

    @Override
    public void execute(DataBundle dataBundle, final RxCallback rxCallback) {
        processOpenWith(dataBundle, rxCallback, file, null);
    }

    private Map<String, String> getDefaultMimeTypeMap(Context context) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(context.getString(R.string.text), MimeTypeUtils.DEFAULT_MIMETYPE_TEXT);
        if (DeviceConfig.sharedInstance(context).hasAudio()) {
            map.put(context.getString(R.string.audio), MimeTypeUtils.DEFAULT_MIMETYPE_AUDIO);
        }
        map.put(context.getString(R.string.image), MimeTypeUtils.DEFAULT_MIMETYPE_IMAGE);
        return map;
    }

    private List<String> getTypeIgnoreList(Context context) {
        List<String> typeIgnoreList = new ArrayList<>();
        if (!DeviceConfig.sharedInstance(context).hasAudio()) {
            typeIgnoreList.add(MimeTypeUtils.AUDIO_PREFIX);
        }
        return typeIgnoreList;
    }

    public void showFileOpenWithDialog(final Context activityContext, final DataBundle dataBundle) {
        final Map<String, String> map = getDefaultMimeTypeMap(activityContext);
        final String[] items = map.keySet().toArray(new String[0]);
        new AlertDialog.Builder(activityContext).setTitle(R.string.open_with).setItems(items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        processOpenWith(dataBundle, new RxCallback<RxFileOpenWithRequest>() {

                            @Override
                            public void onNext(RxFileOpenWithRequest request) {
                                if (request.getAbort()) {
                                    return;
                                }
                                if (CollectionUtils.isNullOrEmpty(request.getAppInfoList())) {
                                    ToastUtils.showToast(dataBundle.getAppContext(), R.string.unable_to_open_this_type_of_file);
                                    return;
                                }
                                showAppListWithDialog(activityContext, file, request.getAppInfoList());
                            }

                        }, file, map.get(items[which]));
                    }
                }).show();
    }

    public void showAppListWithDialog(final Context activityContext, final File file, List<AppDataInfo> list) {
        DialogApplicationOpenList dialog = new DialogApplicationOpenList(list);
        dialog.setOnApplicationSelectedListener(new DialogApplicationOpenList.OnApplicationSelectedListener() {
            @Override
            public void onApplicationSelected(AppDataInfo appDataInfo, boolean makeDefault) {
                if (makeDefault) {
                    setAppAsDefaultPreference(activityContext, file, appDataInfo);
                }
                ActivityUtil.startActivitySafely(activityContext, appDataInfo.intent);
            }
        });
        dialog.show(fragmentManager, Constant.DIALOG_TAG_OPEN_LIST);
    }

    private void setAppAsDefaultPreference(Context context, File file, AppDataInfo appDataInfo) {
        String fileExtension = FilenameUtils.getExtension(file.getName());
        AppPreference appPreference = AppPreference.create(fileExtension,
                appDataInfo.labelName, appDataInfo.packageName, appDataInfo.activityClassName);
        AppPreference.addAppPreferencesToMap(appPreference);
        boolean success = SystemConfigProvider.setStringValue(context, KEY_APP_PREFERENCE,
                JSONObjectParseUtils.toJson(AppPreference.getFileAppPreferList()));
        ToastUtils.showToast(context, success ? R.string.succeedSetting : R.string.failSetting);
    }

    private void processOpenWith(DataBundle dataBundle, final RxCallback rxCallback,
                                 File file, String mimeType) {
        final RxFileOpenWithRequest openWithRequest = new RxFileOpenWithRequest(dataBundle.getDataManager(), file,
                DeviceConfig.sharedInstance(dataBundle.getAppContext()).getAppsIgnoreListMap(),
                getTypeIgnoreList(dataBundle.getAppContext()));
        openWithRequest.setDefaultMimeType(mimeType);
        openWithRequest.setCustomizedIconAppsMap(DeviceConfig.sharedInstance(dataBundle.getAppContext()).getCustomizedIconApps());
        openWithRequest.execute(new RxCallback<RxFileOpenWithRequest>() {
            @Override
            public void onNext(RxFileOpenWithRequest request) {
                if (request.getAbort()) {
                    return;
                }
                dataInfoList = openWithRequest.getAppInfoList();
                preferenceIndex = openWithRequest.getPreferenceIndex();
                rxCallback.onNext(request);
            }
        });
    }
}