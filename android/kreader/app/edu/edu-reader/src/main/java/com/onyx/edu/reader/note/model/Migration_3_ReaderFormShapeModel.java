package com.onyx.edu.reader.note.model;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

/**
 * Created by ming on 2017/2/20.
 */

@Migration(version = 3, database = ReaderNoteDatabase.class)
public class Migration_3_ReaderFormShapeModel extends AlterTableMigration<ReaderFormShapeModel> {

    public Migration_3_ReaderFormShapeModel(Class<ReaderFormShapeModel> table) {
        super(table);
    }

    @Override
    public void onPreMigrate() {
        addColumn(SQLiteType.INTEGER, ReaderFormShapeModel_Table.revision.getNameAlias().name());
    }
}


