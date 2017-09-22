package com.onyx.android.sdk.scribble.data;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

/**
 * Created by solskjaer49 on 2017/09/22.
 */

@Migration(version = 4, database = ShapeDatabase.class)
public class Migration_4_ShapeModel extends AlterTableMigration<ShapeModel> {


    public Migration_4_ShapeModel(Class<ShapeModel> table) {
        super(table);
    }

    @Override
    public void onPreMigrate() {
        addColumn(SQLiteType.REAL, ShapeModel_Table.orientation.getNameAlias().name());
    }
}


