package com.onyx.android.sdk.scribble.data;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

/**
 * Created by ming on 2016/12/14.
 */

@Migration(version = 3, database = ShapeDatabase.class)
public class Migration_3_NoteModel extends AlterTableMigration<NoteModel> {


    public Migration_3_NoteModel(Class<NoteModel> table) {
        super(table);
    }

    @Override
    public void onPreMigrate() {
        addColumn(SQLiteType.INTEGER, NoteModel_Table.position.getNameAlias().name());
    }
}


