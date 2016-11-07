package com.onyx.android.edu.db.model;

import com.onyx.android.edu.db.manage.AppDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by ming on 16/7/7.
 */
//@ModelContainer
//@Table(database = AppDatabase.class)
public class Textbook extends BaseDbModel{
    @Column
    public String Name;
}
