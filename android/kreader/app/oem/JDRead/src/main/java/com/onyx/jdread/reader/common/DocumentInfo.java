package com.onyx.jdread.reader.common;

/**
 * Created by huxiaomao on 2017/12/20.
 */

public class DocumentInfo {
    public static final String BOOK_PATH = "bookPath";
    public static final String BOOK_NAME = "bookName";
    public static final String PASSWORD = "password";
    private String bookPath;
    private String bookName;
    private String password;
    private int messageId = Integer.MAX_VALUE;
    private SecurityInfo securityInfo;

    public static class SecurityInfo {
        public static final String KEY = "key";
        public static final String RANDOM = "random";
        public static final String UU_ID = "uuId";
        private String key;
        private String random;
        private String uuId;

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
}
