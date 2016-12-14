package com.onyx.android.sdk.scribble.data;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

/**
 * Created by ming on 2016/12/14.
 */

@Migration(version = 2, database = ShapeDatabase.class)
public class Migration_2_ShapeModel extends AlterTableMigration<ShapeModel> {


    public Migration_2_ShapeModel(Class<ShapeModel> table) {
        super(table);
    }

    @Override
    public void onPreMigrate() {
        addColumn(SQLiteType.TEXT, ShapeModel_Table.groupId.getNameAlias().name());
        addColumn(SQLiteType.INTEGER, ShapeModel_Table.layoutType.getNameAlias().name());
    }
}


