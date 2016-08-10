package com.onyx.cloud.model;

import com.onyx.cloud.OnyxCloudDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by zhuzeng on 12/14/15.
 */
@Table(database = OnyxCloudDatabase.class)
public class Dictionary extends Product {

    @Column
    public String md5;
    @Column
    public String sourceLanguage;
    @Column
    public String targetLanguage;

    public void simpleUpdate(final Dictionary another) {
        title = another.title;
        name = another.name;
        description = another.description;
        company = another.company;
        summary = another.summary;

        distributeChannel = another.distributeChannel;
        publishers = another.publishers;
        authors = another.authors;
        tags = another.tags;
        domains = another.domains;
        category = another.category;

        md5 = another.md5;
        sourceLanguage = another.sourceLanguage;
        targetLanguage = another.targetLanguage;
    }

}
