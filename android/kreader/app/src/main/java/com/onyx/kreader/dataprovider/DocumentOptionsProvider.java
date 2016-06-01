package com.onyx.kreader.dataprovider;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.kreader.utils.FileUtils;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by zhuzeng on 5/27/16.
 * serves as proxy between request and function provider. it may forward request to real impl provider like
 * onyx android sdk or new sdk.
 */
public class DocumentOptionsProvider {

    @Table(database = ReaderDatabase.class)
    static public class DocumentOptions extends BaseData {

        @Column
        private String name = null;

        @Column
        private String title = null;

        @Column
        private String authors = null;

        @Column
        private String publisher = null;

        @Column
        private String language = null;

        @Column
        private String ISBN = null;

        @Column
        private String description = null;

        @Column
        private String location = null;

        @Column
        private String nativeAbsolutePath = null;

        @Column
        private long size = 0;

        @Column
        private String encoding = null;

        @Column
        private String progress = null;

        @Column
        private int favorite = 0;

        @Column
        private int rating = 0;

        @Column
        private String tags = null;

        @Column
        private String series = null;

        @Column
        private String extraAttributes = null;

        @Column
        private String type = null;

        @Column
        private String cloudReference;

        public BaseOptions toBaseOptions() {
            BaseOptions options = JSON.parseObject(extraAttributes, BaseOptions.class);
            return options;
        }

    }

    public static BaseOptions loadDocumentOptions(final Context context, final String path) {
        BaseOptions baseOptions = new BaseOptions();
        try {
            final String md5 = FileUtils.computeMD5(new File(path));
            List<DocumentOptions> options = new Select().from(DocumentOptions.class).where(DocumentOptions_Table.md5.is(md5)).queryList();
            if (options == null || options.size() <= 0) {
                return baseOptions;
            }
            final DocumentOptions first = options.get(0);
            return first.toBaseOptions();
        } catch (Exception e) {
        }
        return baseOptions;
    }

    public static void saveDocumentOptions(final Context context, final String path, final BaseOptions options) {

    }

}
