package com.onyx.android.sdk.data.dict;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.onyx.android.sdk.data.cms.OnyxCmsCenter;
import com.onyx.android.sdk.data.util.CursorUtil;
import com.onyx.android.sdk.data.util.SerializationUtil;

import java.util.Date;

/**
 * Created by solskjaer49 on 16/1/6 12:42.
 */
public class OnyxVocabulary implements Parcelable {
    private static final String TAG = OnyxVocabulary.class.getSimpleName();

    public static final String DB_TABLE_NAME = "dict_vocabulary";
    public static final Uri CONTENT_URI = Uri.parse("content://" + OnyxCmsCenter.PROVIDER_AUTHORITY + "/" + DB_TABLE_NAME);
    private static final int INVALID_ID = -1;

    private long id = INVALID_ID;

    public OnyxVocabulary() {
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(String appInfo) {
        this.appInfo = appInfo;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    private String word = null;
    private Date createTime = null;
    private Date updateTime = null;
    private String explanation = null;
    private String appInfo = null;
    private String extraInfo = null;

    public static class Columns implements BaseColumns {
        public static String WORD = "WORD";
        public static String CREATE_TIME = "CREATE_TIME";
        public static String UPDATE_TIME = "UPDATE_TIME";
        public static String EXPLANATION = "EXPLANATION";
        public static String APP_INFO = "APP_INFO";
        public static String EXTRA_INFO = "EXTRA_INFO";

        private static boolean sColumnIndexesInitialized = false;
        private static int columnIndexID = -1;
        private static int columnIndexWord = -1;
        private static int columnIndexCreateTime = -1;
        private static int columnIndexUpdateTime = -1;
        private static int columnIndexExplanation = -1;
        private static int columnIndexAppInfo = -1;
        private static int columnIndexExtraInfo = -1;

        public static ContentValues createColumnData(OnyxVocabulary vocabulary) {
            ContentValues values = new ContentValues();
            values.put(WORD, vocabulary.getWord());
            values.put(EXPLANATION, vocabulary.getExplanation());
            values.put(CREATE_TIME, SerializationUtil.dateToString(vocabulary.getCreateTime()));
            values.put(APP_INFO, vocabulary.getAppInfo());
            values.put(EXTRA_INFO, vocabulary.getExtraInfo());
            return values;
        }

        public static void readColumnData(Cursor c, OnyxVocabulary vocabulary) {
            if (!sColumnIndexesInitialized) {
                columnIndexID = c.getColumnIndex(_ID);
                columnIndexWord = c.getColumnIndex(WORD);
                columnIndexExplanation = c.getColumnIndex(EXPLANATION);
                columnIndexAppInfo = c.getColumnIndex(APP_INFO);
                columnIndexCreateTime = c.getColumnIndex(CREATE_TIME);
                columnIndexUpdateTime = c.getColumnIndex(UPDATE_TIME);
                columnIndexExtraInfo = c.getColumnIndex(EXTRA_INFO);
                sColumnIndexesInitialized = true;
            }

            long id = CursorUtil.getLong(c, columnIndexID);
            String word = CursorUtil.getString(c, columnIndexWord);
            String appInfo = CursorUtil.getString(c, columnIndexAppInfo);
            String extraInfo = CursorUtil.getString(c, columnIndexExtraInfo);
            String explanation = CursorUtil.getString(c, columnIndexExplanation);
            String createTime = CursorUtil.getString(c, columnIndexCreateTime);
            String updateTime = CursorUtil.getString(c, columnIndexUpdateTime);

            vocabulary.setId(id);
            vocabulary.setWord(word);
            vocabulary.setAppInfo(appInfo);
            vocabulary.setExtraInfo(extraInfo);
            vocabulary.setExplanation(explanation);
            vocabulary.setCreateTime(SerializationUtil.dateFromString(createTime));
            vocabulary.setUpdateTime(SerializationUtil.dateFromString(updateTime));
        }

        public static OnyxVocabulary readColumnData(Cursor c) {
            OnyxVocabulary vocabulary = new OnyxVocabulary();
            readColumnData(c, vocabulary);
            return vocabulary;
        }

    }

    protected OnyxVocabulary(Parcel in) {
        readFromParcel(in);
    }

    public static final Creator<OnyxVocabulary> CREATOR = new Creator<OnyxVocabulary>() {
        @Override
        public OnyxVocabulary createFromParcel(Parcel in) {
            return new OnyxVocabulary(in);
        }

        @Override
        public OnyxVocabulary[] newArray(int size) {
            return new OnyxVocabulary[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(word);
        dest.writeString(explanation);
        dest.writeString(SerializationUtil.dateToString(createTime));
        dest.writeString(SerializationUtil.dateToString(updateTime));
        dest.writeString(appInfo);
        dest.writeString(extraInfo);
    }

    public void readFromParcel(Parcel source) {
        id = source.readLong();
        word = source.readString();
        explanation = source.readString();
        createTime = SerializationUtil.dateFromString(source.readString());
        updateTime = SerializationUtil.dateFromString(source.readString());
        appInfo = source.readString();
        extraInfo = source.readString();
    }


}
