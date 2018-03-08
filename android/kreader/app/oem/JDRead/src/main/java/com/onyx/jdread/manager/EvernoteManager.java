package com.onyx.jdread.manager;

import android.content.Context;
import android.util.Log;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ToastUtil;

import java.util.List;

/**
 * Created by hehai on 18-1-9.
 */

public class EvernoteManager {
    private final static String TAG = EvernoteManager.class.getSimpleName();
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

    public static Note makeNote(NoteStoreClient noteStore, String noteTitle, String noteBody, Notebook parentNotebook) {

        String nBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        nBody += "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">";
        nBody += "<en-note>" + noteBody + "</en-note>";

        Note ourNote = new Note();
        ourNote.setTitle(noteTitle);
        ourNote.setContent(nBody);

        if (parentNotebook != null && parentNotebook.isSetGuid()) {
            ourNote.setNotebookGuid(parentNotebook.getGuid());
        }

        Note note = null;
        try {
            note = noteStore.createNote(ourNote);
        } catch (EDAMUserException edue) {
            // Something was wrong with the note data
            // See EDAMErrorCode enumeration for error code explanation
            // http://dev.evernote.com/documentation/reference/Errors.html#Enum_EDAMErrorCode
            Log.e(TAG, "EDAMUserException: ", edue);
        } catch (EDAMNotFoundException ednfe) {
            Log.i(TAG, "EDAMNotFoundException: Invalid parent notebook GUID");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return note;
    }

    public static void createNote(String title, String content) {
        if (!EvernoteSession.getInstance().isLoggedIn()) {
            ToastUtil.showToast(R.string.unassociated_impression_notes);
            return;
        }

        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();

        Note note = new Note();
        note.setTitle(title);
        note.setContent(EvernoteUtil.NOTE_PREFIX + content + EvernoteUtil.NOTE_SUFFIX);

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
