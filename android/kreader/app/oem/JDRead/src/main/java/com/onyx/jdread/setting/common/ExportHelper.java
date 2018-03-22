package com.onyx.jdread.setting.common;

import android.support.v4.app.FragmentActivity;

import com.evernote.client.android.EvernoteSession;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.ZipUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.manager.EvernoteManager;
import com.onyx.jdread.personal.action.DeleteFileAction;
import com.onyx.jdread.personal.action.ExportNoteAction;
import com.onyx.jdread.personal.action.SaveContentAction;
import com.onyx.jdread.personal.cloud.entity.jdbean.BookBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.ExportNoteBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.ExportNoteResultBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.NoteBean;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.reader.menu.common.ReaderConfig;
import com.onyx.jdread.setting.event.AssociatedEmailToolsEvent;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
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
        if (!Utils.isNetworkConnected(JDReadApplication.getInstance())) {
            ToastUtil.showToast(ResManager.getString(R.string.wifi_no_connected));
            return false;
        }
        switch (exportType) {
            case TYPE_EMAIL:
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
        switch (exportType) {
            case TYPE_NATIVE:
                saveToLocal(data);
                break;
            case TYPE_EMAIL:
                saveToTemp(data);
                break;
            case TYPE_EVERNOTE:
                //EvernoteManager.createNote(bean.ebook.name, bean.ebook.info);
                break;
        }
    }

    private void saveToLocal(List<NoteBean> noteBeans) {
        if(CollectionUtils.isNullOrEmpty(noteBeans)){
            return;
        }
        File nativePath = new File(Constants.NATIVIE_DIR);
        if (!nativePath.exists()) {
            nativePath.mkdirs();
        }
        String name = noteBeans.get(0).ebook.name;
        File file = new File(nativePath, "<<" + name + ">>" +
                ResManager.getString(R.string.read_note) + ".txt");

        final SaveContentAction action = new SaveContentAction(file, noteBeans);
        action.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                ToastUtil.showToast(action.getResult() ?
                        ResManager.getString(R.string.native_export_success) :
                        ResManager.getString(R.string.export_failed));
            }
        });
    }

    private void saveToTemp(List<NoteBean> noteBeans) {
        if(CollectionUtils.isNullOrEmpty(noteBeans)){
            return;
        }
        File tempDir = new File(Constants.EMAIL_DIR);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        String name = noteBeans.get(0).ebook.name;
        File file = new File(tempDir, "<<" + name + ">>" +
                ResManager.getString(R.string.read_note) + ".txt");
        SaveContentAction action = new SaveContentAction(file, noteBeans);
        action.execute(PersonalDataBundle.getInstance(), null);
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
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            ToastUtil.showToast(ResManager.getString(R.string.export_failed));
                        }

                        @Override
                        public void onFinally() {
                            super.onFinally();
                            deleteTempFile(temp);
                        }
                    });
                }
            }
        }
    }

    private void deleteTempFile(File file) {
        DeleteFileAction action = new DeleteFileAction(file);
        action.execute(PersonalDataBundle.getInstance(), null);
    }

    public static List<NoteBean> getNoteBean(List<Annotation> annotationList,String bookName,long ebookId){
        List<NoteBean> result = new ArrayList<>();
        for(Annotation annotation : annotationList){
            NoteBean noteBean = new NoteBean();
            noteBean.ebook = new BookBean();
            noteBean.ebook.ebook_id = ebookId + "";
            noteBean.ebook.author = "";
            String noteTitle = ResManager.getString(R.string.reader_note);
            String quoteTitle = ResManager.getString(R.string.reader_content);
            noteTitle += ReaderConfig.BR;
            noteTitle += annotation.getNote();
            quoteTitle += ReaderConfig.BR;
            quoteTitle += annotation.getQuote();
            noteBean.ebook.info = noteTitle + quoteTitle;
            noteBean.ebook.name = bookName;
            noteBean.checked = true;
            result.add(noteBean);
        }
        return result;
    }
}
