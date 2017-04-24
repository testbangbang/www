/**
 *
 */
package com.onyx.kreader.tagus;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

/**
 * Created by jim on 17-4-20.
 *
 */
public class TagusDocumentCrypto {

    public static class Columns implements BaseColumns
    {
        public static final String MD5 = "md5";
        public static final String ENC_TYPE = "enc_type";
        public static final String PARAM_ONE = "param_one";
        public static final String PARAM_TWO = "param_two";
        public static final String PARAM_EXTRA = "param_extra";

        private static boolean sColumnIndexesInitialized = false;
        private static int sColumnMD5 = -1;
        private static int sColumnEncType = -1;
        private static int sColumnParamOne = -1;
        private static int sColumnParamTwo = -1;
        private static int sColumnParamExtra = -1;

        public static ContentValues createColumnData(String fileMD5, String encType, String paramOne,
                                                     String paramTwo, String paramExtra)
        {
            ContentValues values = new ContentValues();
            values.put(MD5, fileMD5);
            values.put(ENC_TYPE, encType);
            values.put(PARAM_ONE, paramOne);
            values.put(PARAM_TWO, paramTwo);
            values.put(PARAM_EXTRA, paramExtra);
            return values;
        }

        public static void readColumnData(Cursor c, TagusDocumentCrypto data)
        {
            if (!sColumnIndexesInitialized) {
                sColumnMD5 = c.getColumnIndex(MD5);
                sColumnEncType = c.getColumnIndex(ENC_TYPE);
                sColumnParamOne = c.getColumnIndex(PARAM_ONE);
                sColumnParamTwo = c.getColumnIndex(PARAM_TWO);
                sColumnParamExtra = c.getColumnIndex(PARAM_EXTRA);

                sColumnIndexesInitialized = true;
            }
            String md5 = c.getString(sColumnMD5);
            String encType = c.getString(sColumnEncType);
            String paramOne = c.getString(sColumnParamOne);
            String paramTwo = c.getString(sColumnParamTwo);
            String paramExtra = c.getString(sColumnParamExtra);

            data.setMD5(md5);
            data.setEncType(encType);
            data.setParamOne(paramOne);
            data.setParamTwo(paramTwo);
            data.setParamExtra(paramExtra);
        }

        public static TagusDocumentCrypto readColumnData(Cursor c)
        {
            TagusDocumentCrypto data = new TagusDocumentCrypto();
            readColumnData(c, data);
            return data;
        }
    }


    private String mMD5 = null;
    private String mEncType = null;
    private String mParamOne = null;
    private String mParamTwo = null;
    private String mParamExtra = null;

    public TagusDocumentCrypto()
    {
    }

    /**
     * @return the mMD5
     */
    public String getMD5() {
        return mMD5;
    }

    /**
     * @param md5 the md5 to set
     */
    public void setMD5(String md5) {
        this.mMD5 = md5;
    }

    /**
     * @return the encType
     */
    public String getEncType() {
        return mEncType;
    }

    /**
     * @param encType the encType to set
     */
    public void setEncType(String encType) {
        this.mEncType = encType;
    }

    /**
     * @return the mParamOne
     */
    public String getParamOne() {
        return mParamOne;
    }

    /**
     * @param paramOne the paramOne to set
     */
    public void setParamOne(String paramOne) {
        this.mParamOne = paramOne;
    }

    /**
     * @return the mParamTwo
     */
    public String getParamTwo() {
        return mParamTwo;
    }

    /**
     * @param paramTwo the paramTwo to set
     */
    public void setParamTwo(String paramTwo) {
        this.mParamTwo = paramTwo;
    }

    /**
     * @return the mParamExtra
     */
    public String getParamExtra() {
        return mParamExtra;
    }

    /**
     * @param paramExtra the paramExtra to set
     */
    public void setParamExtra(String paramExtra) {
        this.mParamExtra = paramExtra;
    }

}
