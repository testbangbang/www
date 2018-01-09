package com.onyx.edu.homework.db;

/**
 * Created by zhuzeng on 6/20/16.
 */

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import java.sql.Date;


/**
 * Created by lxm on 2017/12/8.
 */
@Database(name = HomeworkDatabase.NAME, version = HomeworkDatabase.VERSION)
public class HomeworkDatabase {

    public static final String NAME = "HomeworkDatabase";
    public static final int VERSION = 2;

    @Migration(version = 2, database = HomeworkDatabase.class)
    public static class Version2QuestionModelMigration extends AlterTableMigration<QuestionModel> {
        public Version2QuestionModelMigration(Class<QuestionModel> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.TEXT, QuestionModel_Table.content.getNameAlias().name());
            addColumn(SQLiteType.TEXT, QuestionModel_Table.answerContent.getNameAlias().name());
            addColumn(SQLiteType.TEXT, QuestionModel_Table.analysis.getNameAlias().name());
            addColumn(SQLiteType.INTEGER, QuestionModel_Table.type.getNameAlias().name());
            addColumn(SQLiteType.INTEGER, QuestionModel_Table.difficulty.getNameAlias().name());
            addColumn(SQLiteType.TEXT, QuestionModel_Table.options.getNameAlias().name());
        }
    }

    @Migration(version = 2, database = HomeworkDatabase.class)
    public static class Version2HomeworkModelMigration extends AlterTableMigration<HomeworkModel> {
        public Version2HomeworkModelMigration(Class<HomeworkModel> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.TEXT, HomeworkModel_Table.subject.getNameAlias().name());
            addColumn(SQLiteType.TEXT, HomeworkModel_Table.title.getNameAlias().name());
            addColumn(SQLiteType.INTEGER, HomeworkModel_Table.beginTime.getNameAlias().name());
            addColumn(SQLiteType.INTEGER, HomeworkModel_Table.endTime.getNameAlias().name());
        }
    }
}
