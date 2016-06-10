package com.onyx.kreader.dataprovider;

import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.kreader.utils.StringUtils;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.List;

/**
 * Created by zhuzeng on 6/2/16.
 */
@Table(database = ReaderDatabase.class)
public class DocumentOptions extends BaseData {

    @Column
    String name = null;

    @Column
    String title = null;

    @Column
    String authors = null;

    @Column
    String publisher = null;

    @Column
    String language = null;

    @Column
    String ISBN = null;

    @Column
    String description = null;

    @Column
    String location = null;

    @Column
    String nativeAbsolutePath = null;

    @Column
    long size = 0;

    @Column
    String encoding = null;

    @Column
    String progress = null;

    @Column
    int favorite = 0;

    @Column
    int rating = 0;

    @Column
    String tags = null;

    @Column
    String series = null;

    @Column
    String extraAttributes = null;

    @Column
    String type = null;

    @Column
    String cloudReference;

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
        return StringUtils.split(authors, BaseData.DELIMITER);
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
