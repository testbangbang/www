package com.onyx.android.sdk.data.model;

import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by zhuzeng on 6/2/16.
 */
@Table(database = ContentDatabase.class)
public class Metadata extends BaseData {

    public static class ReadingStatus {
        public static int NEW = 0;
        public static int READING = 1;
        public static int FINISHED = 2;
    }

    public static class FetchSource {
        public static int LOCAL = 0;
        public static int CLOUD = 1;
    }

    public static final String PROGRESS_DIVIDER = "/";

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
    private Date lastAccess = null;

    @Column
    private Date lastModified = null;

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
    private String cloudId;

    @Column
    private String parentId;

    @Column
    private int readingStatus = 0;

    @Column
    private String hashTag;

    @Column
    private String storageId;

    @Column
    private int fetchSource;

    @Column
    private String coverUrl;

    @Column
    private String currency;

    @Column
    private double price;

    @Column
    private float discount;

    @Column
    private boolean paid;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public void setFetchSource(int fetchSource) {
        this.fetchSource = fetchSource;
    }

    public int getFetchSource() {
        return fetchSource;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

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

    public Date getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(Date lastAccess) {
        this.lastAccess = lastAccess;
    }

    public void updateLastAccess() {
        setLastAccess(new Date());
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(final String p) {
        progress = p;
    }

    public void setProgress(int currentPage, int totalPage) {
        setProgress(String.format(Locale.getDefault(), "%d" + PROGRESS_DIVIDER + "%d", currentPage, totalPage));
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

    public List<String> getTagList() {
        return StringUtils.split(getTags(), DELIMITER);
    }

    public void setTags(final String t) {
        tags = t;
    }

    public String getSeries() {
        return series;
    }

    public List<String> getSerieList() {
        return StringUtils.split(getSeries(), DELIMITER);
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

    public String getCloudId() {
        return cloudId;
    }

    public void setCloudId(final String c) {
        cloudId = c;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(final String id) {
        this.parentId = id;
    }

    public int getReadingStatus() {
        return readingStatus;
    }

    public void setReadingStatus(int status) {
        this.readingStatus = status;
    }

    public String getHashTag() {
        return hashTag;
    }

    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }

    public void setStorageId(String storageId) {
        this.storageId = storageId;
    }

    public String getStorageId() {
        return storageId;
    }

    public static Metadata createFromFile(String path) {
        return createFromFile(new File(path));
    }

    public static Metadata createFromFile(File file) {
        return createFromFile(file, true);
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public static Metadata createFromMetadataPath(Metadata metadata, boolean computeMd5) {
        File file = new File(metadata.getNativeAbsolutePath());
        try {
            if (computeMd5) {
                String md5 = FileUtils.computeMD5(file);
                metadata.setHashTag(md5);
            }
            getBasicMetadataFromFile(metadata, file);
            return metadata;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Metadata createFromFile(File file, boolean computeMd5) {
        final Metadata data = new Metadata();
        data.setNativeAbsolutePath(file.getAbsolutePath());
        return createFromMetadataPath(data, computeMd5);
    }

    public static void getBasicMetadataFromFile(final Metadata data, File file) {
        data.setName(file.getName());
        data.setIdString(file.getAbsolutePath());
        data.setLocation(file.getAbsolutePath());
        data.setNativeAbsolutePath(file.getAbsolutePath());
        data.setSize(file.length());
        data.setLastModified(new Date(FileUtils.getLastChangeTime(file)));
        data.setType(FileUtils.getFileExtension(file.getName()));
    }

    private int parseProgress(String progress) {
        try {
            return Integer.parseInt(progress);
        } catch (Exception e) {
        }
        return 0;
    }

    public int getProgressPercent() {
        if (StringUtils.isNullOrEmpty(progress)) {
            return 0;
        }
        String[] progressSplit = progress.split(PROGRESS_DIVIDER);
        if (progressSplit.length != 2) {
            return 0;
        }
        int readingProgress = parseProgress(progressSplit[0]);
        int totalProgress = parseProgress(progressSplit[1]);
        if (totalProgress == 0) {
            return 0;
        }
        return readingProgress * 100 / totalProgress;
    }

    public boolean internalProgressEqual(String progress) {
        String[] progressSplit = progress.split(PROGRESS_DIVIDER);
        if (progressSplit.length != 2) {
            return false;
        }
        return progressSplit[0].equals(progressSplit[1]);
    }

    public String getAssociationId() {
        return getHashTag();
    }

    public boolean isFinished() {
        return readingStatus == ReadingStatus.FINISHED;
    }

    public boolean isReading() {
        return readingStatus == ReadingStatus.READING;
    }

    public boolean isNew() {
        return readingStatus == ReadingStatus.NEW;
    }

}
