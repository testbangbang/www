package com.onyx.kreader.note.request;

import android.graphics.Bitmap;

import com.onyx.kreader.note.NoteManager;

/**
 * Created by ming on 16/9/23.
 */
public class GetScribbleBitmapRequest extends ReaderBaseNoteRequest{

    private String page;
    private Bitmap scribbleBitmap;

    public void execute(final NoteManager noteManager) throws Exception {
//        final RectF origin = reader.getDocument().getPageOriginSize(page);
//        PageInfo pageInfo = new PageInfo(page, origin.width(), origin.height());

    }
}
