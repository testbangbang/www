package com.onyx.edu.reader.note.request;

import com.onyx.edu.reader.note.NoteManager;

import java.util.List;

/**
 * Created by ming on 16/9/24.
 */
public class GetNotePageListRequest extends ReaderBaseNoteRequest{

    private List<String> pageList;

    @Override
    public void execute(NoteManager noteManager) throws Exception {
        pageList = noteManager.getNoteDocument().getNoEmptyPageList(getContext());
    }

    public List<String> getPageList() {
        return pageList;
    }
}
