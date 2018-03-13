package com.onyx.jdread.setting.common;

import android.support.v4.app.FragmentActivity;

import com.evernote.client.android.EvernoteSession;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.ZipUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.manager.EvernoteManager;
import com.onyx.jdread.personal.action.ExportNoteAction;
import com.onyx.jdread.personal.cloud.entity.jdbean.ExportNoteBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.ExportNoteResultBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.NoteBean;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.setting.event.AssociatedEmailToolsEvent;
import com.onyx.jdread.setting.view.AssociatedEmailDialog;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

/**
 * Created by li on 2018/3/8.
 */

public class ExportHelper {
    public static final int TYPE_NATIVE = 1;
    public static final int TYPE_EMAIL = 2;
    public static final int TYPE_EVERNOTE = 3;
    private EventBus eventBus;
    private FragmentActivity context;

    public ExportHelper(FragmentActivity context, EventBus eventBus) {
        this.context = context;
        this.eventBus = eventBus;
    }

    public void exportNote(int exportType, List<NoteBean> data) {
        if (!confirmAssociate(exportType)) {
            return;
        }
        receiveData(exportType, data);
        if (exportType == TYPE_EMAIL) {
            sendEmail();
        }
    }

    private boolean confirmAssociate(int exportType) {
        switch (exportType) {
            case TYPE_EMAIL:
                if (!Utils.isNetworkConnected(JDReadApplication.getInstance())) {
                    ToastUtil.showToast(ResManager.getString(R.string.wifi_no_connected));
                    return false;
                }
                String email = JDPreferenceManager.getStringValue(R.string.email_address_key, null);
                if (StringUtils.isNullOrEmpty(email)) {
                    eventBus.post(new AssociatedEmailToolsEvent());
                    return false;
                }
                break;
            case TYPE_EVERNOTE:
                if (!EvernoteSession.getInstance().isLoggedIn()) {
                    EvernoteManager.getEvernoteSession(JDReadApplication.getInstance()).authenticate(context);
                    return false;
                }
                break;
        }
        return true;
    }

    public void receiveData(int exportType, List<NoteBean> data) {
        for (NoteBean bean : data) {
            if (bean.checked) {
                switch (exportType) {
                    case TYPE_NATIVE:
                        saveToLocal(bean);
                        break;
                    case TYPE_EMAIL:
                        saveToTemp(bean);
                        break;
                    case TYPE_EVERNOTE:
                        EvernoteManager.createNote(bean.ebook.name, bean.ebook.info);
                        break;
                }
            }
        }
    }

    private void saveToLocal(NoteBean bean) {
        File nativePath = new File(Constants.NATIVIE_DIR);
        if (!nativePath.exists()) {
            nativePath.mkdirs();
        }
        File file = new File(nativePath, "<<" + bean.ebook.name + ">>" +
                ResManager.getString(R.string.read_note) + ".txt");
        if (!file.exists()) {
            file.delete();
        }
        ToastUtil.showToast(FileUtils.saveContentToFile(bean.ebook.info, file) ?
                ResManager.getString(R.string.native_export_success) :
                ResManager.getString(R.string.export_failed));
    }

    private void saveToTemp(NoteBean bean) {
        File tempDir = new File(Constants.EMAIL_DIR);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        File file = new File(tempDir, "<<" + bean.ebook.name + ">>" +
                ResManager.getString(R.string.read_note) + ".txt");
        FileUtils.saveContentToFile(bean.ebook.info, file);
    }

    private void sendEmail() {
        final File temp = new File(Constants.EMAIL_DIR);
        if (temp.exists()) {
            File[] files = temp.listFiles();
            if (files != null && files.length > 0) {
                File zipFile = new File(temp, Constants.ZIP_NAME + ".zip");
                if (ZipUtils.compress(files, zipFile)) {
                    ExportNoteBean noteBean = new ExportNoteBean();
                    noteBean.file = zipFile;
                    noteBean.sendEmail = JDPreferenceManager.getStringValue(R.string.email_address_key, null);
                    noteBean.fileSize = String.valueOf(zipFile.length() * 1.0f / 1024);
                    noteBean.fileName = Constants.ZIP_NAME;
                    noteBean.fileCount = String.valueOf(files.length);
                    final ExportNoteAction action = new ExportNoteAction(noteBean);
                    action.execute(PersonalDataBundle.getInstance(), new RxCallback() {
                        @Override
                        public void onNext(Object o) {
                            ExportNoteResultBean resultBean = action.getResultBean();
                            if (resultBean.result_code == 0) {
                                ToastUtil.showToast(ResManager.getString(R.string.email_export_success));
                                FileUtils.deleteFile(temp, false);
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            ToastUtil.showToast(ResManager.getString(R.string.export_failed));
                        }
                    });
                }
            }
        }
    }
}
