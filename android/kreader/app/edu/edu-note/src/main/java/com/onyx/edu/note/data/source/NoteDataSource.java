package com.onyx.edu.note.data.source;

import android.support.annotation.NonNull;

import com.onyx.edu.note.data.NoteModel;

import java.util.List;

/**
 * Main entry point for accessing Notes data.
 */
public interface NoteDataSource {

    interface LoadNotesCallback {

        void onNotesLoaded(List<NoteModel> Notes);

        void onDataNotAvailable();
    }

    interface GetNoteCallback {

        void onNoteLoaded(NoteModel Note);

        void onDataNotAvailable();
    }

    //TODO:Initial Add here,should check if need to distinguish note/folder or not.

    void getNotes(@NonNull LoadNotesCallback callback);

    void getNote(@NonNull String NoteId, @NonNull GetNoteCallback callback);

    void saveNote(@NonNull NoteModel Note);

    void deleteAllNotes();

    void deleteNote(@NonNull String NoteId);
}
