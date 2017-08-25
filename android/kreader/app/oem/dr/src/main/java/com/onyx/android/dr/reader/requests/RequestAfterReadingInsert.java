package com.onyx.android.dr.reader.requests;


import com.onyx.android.dr.reader.data.AfterReadingEntity;
import com.onyx.android.dr.reader.data.AfterReadingEntity_Table;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

/**
 * Created by hehai on 17-1-19.
 */
public class RequestAfterReadingInsert extends BaseDataRequest {
    private AfterReadingEntity noteEntity;

    public RequestAfterReadingInsert(AfterReadingEntity noteEntity) {
        this.noteEntity = noteEntity;
    }

    @Override
    public void execute(DataManager helper) throws Exception {
        insertNote();
    }

    private void insertNote() {
        AfterReadingEntity oldNoteEntity = new Select().from(AfterReadingEntity.class).where(AfterReadingEntity_Table.bookName.eq(this.noteEntity.bookName)).querySingle();
        if (oldNoteEntity == null) {
            noteEntity.time = TimeUtils.getTime(System.currentTimeMillis(), TimeUtils.DEFAULT_DATE_FORMAT);
            noteEntity.insert();
        } else {
            update(oldNoteEntity);
        }
    }

    private void update(AfterReadingEntity oldNoteEntity) {
        noteEntity.id = oldNoteEntity.id;
        noteEntity.time = TimeUtils.getTime(System.currentTimeMillis(), TimeUtils.DEFAULT_DATE_FORMAT);
        noteEntity.update();
    }
}
