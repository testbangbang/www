package com.onyx.kreader.dataprovider;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.kreader.utils.FileUtils;
import com.onyx.kreader.utils.StringUtils;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.File;
import java.util.List;

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

        public String getName() {
            return name;
        }

        public void setName(final String n) {
            name = n;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(final String t) {
            title = t;
        }

        public String getAuthors() {
            return authors;
        }

        public void setAuthors(final String a) {
            authors = a;
        }

        public List<String> getAuthorList() {
            return StringUtils.split(authors, DELIMITER);
        }

        public String getPublisher() {
            return publisher;
        }

        public void setPublisher(final String p) {
            publisher = p;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(final String l) {
            language = l;
        }

        public String getISBN() {
            return ISBN;
        }

        public void setISBN(final String value) {
            ISBN = value;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(final String d) {
            description = d;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(final String l) {
            location = l;
        }

        public String getNativeAbsolutePath() {
            return nativeAbsolutePath;
        }

        public void setNativeAbsolutePath(final String path) {
            nativeAbsolutePath = path;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long s) {
            size = s;
        }

        public String getEncoding() {
            return encoding;
        }

        public void setEncoding(final String e) {
            encoding = e;
        }

        public String getProgress() {
            return progress;
        }

        public void setProgress(final String p) {
            progress = p;
        }

        public int getFavorite() {
            return favorite;
        }

        public void setFavorite(int f) {
            favorite = f;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int r) {
            rating = r;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(final String t) {
            tags = t;
        }

        public String getSeries() {
            return series;
        }

        public void setSeries(final String s) {
            series = s;
        }

        public String getExtraAttributes() {
            return extraAttributes;
        }

        public void setExtraAttributes(final String e) {
            extraAttributes = e;
        }

        public String getType() {
            return type;
        }

        public void setType(final String t) {
            type = t;
        }

        public String getCloudReference() {
            return cloudReference;
        }

        public void setCloudReference(final String c) {
            cloudReference = c;
        }

        public BaseOptions getBaseOptions() {
            BaseOptions options = BaseOptions.optionsFromJSONString(getExtraAttributes());
            return options;
        }
    }

    public static BaseOptions loadDocumentOptions(final Context context, final String path, String md5) {
        BaseOptions baseOptions = new BaseOptions();
        try {
            if (StringUtils.isNullOrEmpty(md5)) {
                md5 = FileUtils.computeMD5(new File(path));
            }
            final DocumentOptions options = new Select().from(DocumentOptions.class).where(DocumentOptions_Table.md5.is(md5)).querySingle();
            if (options == null) {
                return baseOptions;
            }
            return options.getBaseOptions();
        } catch (Exception e) {
        }
        return baseOptions;
    }

    public static void saveDocumentOptions(final Context context, final String path, String md5, final BaseOptions baseOptions) {
        try {
            if (StringUtils.isNullOrEmpty(md5)) {
                md5 = FileUtils.computeMD5(new File(path));
            }
            DocumentOptions documentOptions = null;
            final DocumentOptions options = new Select().from(DocumentOptions.class).where(DocumentOptions_Table.md5.is(md5)).querySingle();
            if (options == null) {
                documentOptions = new DocumentOptions();
                documentOptions.setMd5(md5);
            } else {
                documentOptions = options;
            }
            documentOptions.setExtraAttributes(baseOptions.toJSONString());
            documentOptions.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
