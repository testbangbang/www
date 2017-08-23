package com.onyx.edu.reader.note.request;

import com.onyx.edu.reader.note.NoteManager;
import com.onyx.edu.reader.note.data.ReaderNoteDocument;
import com.onyx.edu.reader.note.data.ReaderShapeFactory;
import com.onyx.edu.reader.note.model.ReaderNoteDataProvider;
import com.onyx.edu.reader.note.model.ReaderNoteDocumentModel;

/**
 * Created by lxm on 2017/8/21.
 */

public class LockNoteDocumentRequest extends ReaderBaseNoteRequest {

    @Override
    public void execute(NoteManager noteManager) throws Exception {
        super.execute(noteManager);
        noteManager.getNoteDocument().lockNoteDocument();
        noteManager.getNoteDocument().save(getContext());
        getNoteDataInfo().setContentRendered(renderVisiblePages(noteManager));
    }
}
