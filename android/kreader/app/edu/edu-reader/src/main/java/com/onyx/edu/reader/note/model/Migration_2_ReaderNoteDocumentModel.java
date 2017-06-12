package com.onyx.edu.reader.note.model;

import com.onyx.android.sdk.data.db.OnyxStatisticsDatabase;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

/**
 * Created by ming on 2017/2/20.
 */

@Migration(version = 2, database = ReaderNoteDatabase.class)
public class Migration_2_ReaderNoteDocumentModel extends AlterTableMigration<ReaderNoteDocumentModel> {

    public Migration_2_ReaderNoteDocumentModel(Class<ReaderNoteDocumentModel> table) {
        super(table);
    }

    @Override
    public void onPreMigrate() {
        addColumn(SQLiteType.INTEGER, ReaderNoteDocumentModel_Table.reviewStatus.getNameAlias().name());
    }
}


