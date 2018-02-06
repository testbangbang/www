package com.onyx.jdread.manager;

import android.content.Context;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.type.Note;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ToastUtil;

import java.util.List;

/**
 * Created by hehai on 18-1-9.
 */

public class EvernoteManager {
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;
    private static final String CONSUMER_KEY = "sfqfirst-4844";
    private static final String CONSUMER_SECRET = "fe205ec7c67ab783";
    private static EvernoteSession evernoteSession;

    public static EvernoteSession getEvernoteSession(Context appContext) {
        if (evernoteSession == null) {
            evernoteSession = new EvernoteSession.Builder(appContext)
                    .setEvernoteService(EVERNOTE_SERVICE)
                    .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                    .setLocale(appContext.getResources().getConfiguration().locale)
                    .build(CONSUMER_KEY, CONSUMER_SECRET)
                    .asSingleton();
        }
        return evernoteSession;
    }

    public static void createNotes(List<Note> notes) {
        if (!EvernoteSession.getInstance().isLoggedIn()) {
            ToastUtil.showToast(R.string.unassociated_impression_notes);
            return;
        }

        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
        for (Note note : notes) {
            noteStoreClient.createNoteAsync(note, new EvernoteCallback<Note>() {
                @Override
                public void onSuccess(Note result) {

                }

                @Override
                public void onException(Exception exception) {

                }
            });
        }
    }
}
