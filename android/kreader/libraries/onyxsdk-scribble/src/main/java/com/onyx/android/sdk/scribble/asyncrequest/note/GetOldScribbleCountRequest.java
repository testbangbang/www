package com.onyx.android.sdk.scribble.asyncrequest.note;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;

/**
 * Created by ming on 2017/3/21.
 */

public class GetOldScribbleCountRequest extends AsyncBaseNoteRequest {

    private Context context;
    private int count;

    public GetOldScribbleCountRequest(Context context) {
        this.context = context;
    }

    @Override
    public void execute(NoteManager helper) throws Exception {
        Cursor cursor = null;
        try {
            Uri uri = Uri.parse(ImportScribbleRequest.OLD_SCRIBBLE_URL);
            cursor = context.getContentResolver().query(
                    uri, null, null, null,
                    null);
            if (cursor == null) {
                return;
            }

            count = cursor.getCount();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public int getCount() {
        return count;
    }
}
