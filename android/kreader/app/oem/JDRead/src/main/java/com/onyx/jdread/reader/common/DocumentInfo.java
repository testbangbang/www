package com.onyx.jdread.reader.common;

import android.content.Intent;

import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by huxiaomao on 2017/12/20.
 */

public class DocumentInfo {
    public static final String BOOK_PATH = "bookPath";
    public static final String BOOK_NAME = "bookName";
    public static final String PASSWORD = "password";
    public static final String WHOLE_BOOK_DOWNLOAD = "wholeBookDownLoad";
    public static final String CLOUD_ID = "cloudId";
    public static final String OPEN_TYPE = "openType";
    public static final int OPEN_BOOK = 1;
    public static final int OPEN_BOOK_CATALOG = 2;
    private String bookPath;
    private String bookName;
    private String password;
    private int messageId = Integer.MAX_VALUE;
    private SecurityInfo securityInfo;
    private boolean dryRun = false;
    private boolean isWholeBookDownLoad = true;
    private long cloudId = Integer.MAX_VALUE;
    private int openType = OPEN_BOOK;


    public void setSecurityInfo(SecurityInfo securityInfo) {
        this.securityInfo = securityInfo;
    }

    public SecurityInfo getSecurityInfo() {
        return securityInfo;
    }

    public static class SecurityInfo {
        public static final String KEY = "key";
        public static final String RANDOM = "random";
        public static final String UU_ID = "uuId";
        private String key = "";
        private String random = "";
        private String uuId = "";

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getRandom() {
            return random;
        }

        public void setRandom(String random) {
            this.random = random;
        }

        public String getUuId() {
            return uuId;
        }

        public void setUuId(String uuId) {
            this.uuId = uuId;
        }
    }

    public DocumentInfo() {
        securityInfo = new SecurityInfo();
    }

    public String getBookSingleFlags() {
        return bookPath;
    }

    public String getBookPath() {
        return bookPath;
    }

    public void setBookPath(String bookPath) {
        this.bookPath = bookPath;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryrun) {
        this.dryRun = dryrun;
    }

    public boolean isWholeBookDownLoad() {
        return isWholeBookDownLoad;
    }

    public void setWholeBookDownLoad(boolean wholeBookDownLoad) {
        isWholeBookDownLoad = wholeBookDownLoad;
    }

    public int getOpenType() {
        return openType;
    }

    public void setOpenType(int openType) {
        this.openType = openType;
    }

    public long getCloudId() {
        return cloudId;
    }

    public void setCloudId(long cloudId) {
        this.cloudId = cloudId;
    }

    public void documentInfoToIntent(Intent intent){
        if(StringUtils.isNullOrEmpty(bookPath)){
            return;
        }
        intent.putExtra(BOOK_PATH,bookPath);
        if(StringUtils.isNotBlank(bookName)) {
            intent.putExtra(BOOK_NAME, bookName);
        }
        if(StringUtils.isNotBlank(password)) {
            intent.putExtra(PASSWORD, password);
        }
        if(StringUtils.isNotBlank(securityInfo.key)){
            intent.putExtra(SecurityInfo.KEY,securityInfo.key);
        }
        if(StringUtils.isNotBlank(securityInfo.random)){
            intent.putExtra(SecurityInfo.RANDOM,securityInfo.random);
        }
        if(StringUtils.isNotBlank(securityInfo.uuId)){
            intent.putExtra(SecurityInfo.UU_ID,securityInfo.uuId);
        }
        intent.putExtra(WHOLE_BOOK_DOWNLOAD,isWholeBookDownLoad);
        intent.putExtra(CLOUD_ID,cloudId);
        intent.putExtra(OPEN_TYPE,openType);
    }
}
