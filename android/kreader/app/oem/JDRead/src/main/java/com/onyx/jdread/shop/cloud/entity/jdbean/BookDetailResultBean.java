package com.onyx.jdread.shop.cloud.entity.jdbean;


import com.onyx.android.sdk.data.model.Metadata;

/**
 * Created by huxiaomao on 17/3/28.
 */

public class BookDetailResultBean {
    public String code;
    public Detail detail;
    public GetMetadataEntity getMetadataEntity;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Detail getDetail() {
        return detail;
    }

    public void setDetail(Detail detail) {
        this.detail = detail;
    }

    public GetMetadataEntity getGetMetadataEntity() {
        return getMetadataEntity;
    }

    public void setGetBookDetailEntity(GetMetadataEntity getMetadataEntity) {
        this.getMetadataEntity = getMetadataEntity;
    }

    public static class GetMetadataEntity {
        private Metadata metadata = new Metadata();

        public GetMetadataEntity(BookDetailResultBean.Detail detail) {
            metadata.setId(detail.getEbookId());
            metadata.setCoverUrl(detail.getImageUrl());
            metadata.setAuthors(detail.getAuthor());
            metadata.setName(detail.getName());
            metadata.setDescription(detail.getInfo());
            metadata.setType(detail.getFormat());
            metadata.setPublisher(detail.getPublisher());
            metadata.setISBN(detail.getIsbn());
            metadata.setLanguage(detail.getLanguage());
        }

        public Metadata getMetadata() {
            return metadata;
        }

        public void setMetadata(Metadata metadata) {
            this.metadata = metadata;
        }
    }

    public static class Detail {
        private Object tag;
        private boolean EBook;
        private String acreditInst;
        private boolean addToCart;
        private String author;
        private String authorInfo;
        private String catalog;
        private String categoryPath;
        private String currentTime;
        private long ebookId;
        private String editorPick;
        private float fileSize;
        private int firstCatid;
        private String format;
        private int good;
        private String imageUrl;
        private String info;
        private boolean isAlreadyBuy;
        private boolean isBuy;
        private boolean isFluentRead;
        private boolean isFree;
        private boolean isUserCanFluentRead;
        private boolean isUserFluentReadAddToCard;
        private String isbn;
        private float jdPrice;
        private String language;
        private String largeImageUrl;
        private String mediaComments;
        private String name;
        private int paperBookId;
        private boolean pass;
        private float price;
        private String priceMessage;
        private String promotion;
        private String publishTime;
        private String publisher;
        private int secondCatid1;
        private float star;
        private int thirdCatid1;
        private boolean tobFluentRead;
        private String tryEpubDownUrl;
        private String downLoadUrl;
        private boolean tryRead;
        private int type;
        private int wordCount;
        private String edition;
        private String orderId;
        private BookExtraInfoBean bookExtraInfoBean;
        private String key;
        private String random;

        public Object getTag() {
            return tag;
        }

        public void setTag(Object tag) {
            this.tag = tag;
        }

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

        public String getEdition() {
            return edition;
        }

        public void setEdition(String edition) {
            this.edition = edition;
        }

        public BookExtraInfoBean getBookExtraInfoBean() {
            return bookExtraInfoBean;
        }

        public void setBookExtraInfoBean(BookExtraInfoBean bookExtraInfoBean) {
            this.bookExtraInfoBean = bookExtraInfoBean;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getDownLoadUrl() {
            return downLoadUrl;
        }

        public void setDownLoadUrl(String downLoadUrl) {
            this.downLoadUrl = downLoadUrl;
        }

        public boolean isEBook() {
            return EBook;
        }

        public void setEBook(boolean EBook) {
            this.EBook = EBook;
        }

        public String getAcreditInst() {
            return acreditInst;
        }

        public void setAcreditInst(String acreditInst) {
            this.acreditInst = acreditInst;
        }

        public boolean isAddToCart() {
            return addToCart;
        }

        public void setAddToCart(boolean addToCart) {
            this.addToCart = addToCart;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getAuthorInfo() {
            return authorInfo;
        }

        public void setAuthorInfo(String authorInfo) {
            this.authorInfo = authorInfo;
        }

        public String getCatalog() {
            return catalog;
        }

        public void setCatalog(String catalog) {
            this.catalog = catalog;
        }

        public String getCategoryPath() {
            return categoryPath;
        }

        public void setCategoryPath(String categoryPath) {
            this.categoryPath = categoryPath;
        }

        public String getCurrentTime() {
            return currentTime;
        }

        public void setCurrentTime(String currentTime) {
            this.currentTime = currentTime;
        }

        public long getEbookId() {
            return ebookId;
        }

        public void setEbookId(long ebookId) {
            this.ebookId = ebookId;
        }

        public String getEditorPick() {
            return editorPick;
        }

        public void setEditorPick(String editorPick) {
            this.editorPick = editorPick;
        }

        public float getFileSize() {
            return fileSize;
        }

        public void setFileSize(float fileSize) {
            this.fileSize = fileSize;
        }

        public int getFirstCatid() {
            return firstCatid;
        }

        public void setFirstCatid(int firstCatid) {
            this.firstCatid = firstCatid;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public int getGood() {
            return good;
        }

        public void setGood(int good) {
            this.good = good;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public boolean isAlreadyBuy() {
            return isAlreadyBuy;
        }

        public void setAlreadyBuy(boolean alreadyBuy) {
            isAlreadyBuy = alreadyBuy;
        }

        public boolean isBuy() {
            return isBuy;
        }

        public void setBuy(boolean buy) {
            isBuy = buy;
        }

        public boolean isFluentRead() {
            return isFluentRead;
        }

        public void setFluentRead(boolean fluentRead) {
            isFluentRead = fluentRead;
        }

        public boolean isFree() {
            return isFree;
        }

        public void setFree(boolean free) {
            isFree = free;
        }

        public boolean isUserCanFluentRead() {
            return isUserCanFluentRead;
        }

        public void setUserCanFluentRead(boolean userCanFluentRead) {
            isUserCanFluentRead = userCanFluentRead;
        }

        public boolean isUserFluentReadAddToCard() {
            return isUserFluentReadAddToCard;
        }

        public void setUserFluentReadAddToCard(boolean userFluentReadAddToCard) {
            isUserFluentReadAddToCard = userFluentReadAddToCard;
        }

        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        public float getJdPrice() {
            return jdPrice;
        }

        public void setJdPrice(float jdPrice) {
            this.jdPrice = jdPrice;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getLargeImageUrl() {
            return largeImageUrl;
        }

        public void setLargeImageUrl(String largeImageUrl) {
            this.largeImageUrl = largeImageUrl;
        }

        public String getMediaComments() {
            return mediaComments;
        }

        public void setMediaComments(String mediaComments) {
            this.mediaComments = mediaComments;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPaperBookId() {
            return paperBookId;
        }

        public void setPaperBookId(int paperBookId) {
            this.paperBookId = paperBookId;
        }

        public boolean isPass() {
            return pass;
        }

        public void setPass(boolean pass) {
            this.pass = pass;
        }

        public float getPrice() {
            return price;
        }

        public void setPrice(float price) {
            this.price = price;
        }

        public String getPriceMessage() {
            return priceMessage;
        }

        public void setPriceMessage(String priceMessage) {
            this.priceMessage = priceMessage;
        }

        public String getPromotion() {
            return promotion;
        }

        public void setPromotion(String promotion) {
            this.promotion = promotion;
        }

        public String getPublishTime() {
            return publishTime;
        }

        public void setPublishTime(String publishTime) {
            this.publishTime = publishTime;
        }

        public String getPublisher() {
            return publisher;
        }

        public void setPublisher(String publisher) {
            this.publisher = publisher;
        }

        public int getSecondCatid1() {
            return secondCatid1;
        }

        public void setSecondCatid1(int secondCatid1) {
            this.secondCatid1 = secondCatid1;
        }

        public float getStar() {
            return star;
        }

        public void setStar(float star) {
            this.star = star;
        }

        public int getThirdCatid1() {
            return thirdCatid1;
        }

        public void setThirdCatid1(int thirdCatid1) {
            this.thirdCatid1 = thirdCatid1;
        }

        public boolean isTobFluentRead() {
            return tobFluentRead;
        }

        public void setTobFluentRead(boolean tobFluentRead) {
            this.tobFluentRead = tobFluentRead;
        }

        public String getTryEpubDownUrl() {
            return tryEpubDownUrl;
        }

        public void setTryEpubDownUrl(String tryEpubDownUrl) {
            this.tryEpubDownUrl = tryEpubDownUrl;
        }

        public boolean isTryRead() {
            return tryRead;
        }

        public void setTryRead(boolean tryRead) {
            this.tryRead = tryRead;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getWordCount() {
            return wordCount;
        }

        public void setWordCount(int wordCount) {
            this.wordCount = wordCount;
        }
    }
}
