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
            metadata.setCoverUrl(detail.getLargeLogo());
            metadata.setAuthors(detail.getAuthor());
            metadata.setName(detail.getBookName());
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
        private String largeLogo;
        private String author;
        private boolean isTryRead;
        private float orgJdPrice;
        private boolean isCanBuy;
        private String acreditInst;
        private String logo;
        private String newLargeLogo;
        private int canBuyBorrowDays;
        private String catalog;
        private boolean isAddToCart;
        private boolean isCanFreeGet;
        private String priceMessage;
        private boolean isAlreadyUserBuyBorrow;
        private String publishTime;
        private String tryDownLoadUrl;
        private String edition;
        private String adWords;
        private boolean isUserFluentReadAddToCard;
        private int wordCount;
        private String currentTime;
        private boolean isFluentRead;
        private boolean isAlreadyBorrow;
        private boolean isEBook;
        private float star;
        private boolean isBorrow;
        private boolean isBuyBorrow;
        private boolean isFree;
        private boolean isMyWish;
        private float price;
        private long bookId;
        private String format;
        private boolean isBuy;
        private boolean isUserCanFluentRead;
        private float size;
        private long ebookId;
        private boolean received;
        private String promotion;
        private String publisher;
        private String isbn;
        private boolean EBook;
        private float jdPrice;
        private String language;
        private String newLogo;
        private String editorPick;
        private boolean isAlreadyBuy;
        private String bookName;
        private String info;
        private String orderId;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getLargeLogo() {
            return largeLogo;
        }

        public void setLargeLogo(String largeLogo) {
            this.largeLogo = largeLogo;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public boolean isTryRead() {
            return isTryRead;
        }

        public void setTryRead(boolean tryRead) {
            isTryRead = tryRead;
        }

        public float getOrgJdPrice() {
            return orgJdPrice;
        }

        public void setOrgJdPrice(float orgJdPrice) {
            this.orgJdPrice = orgJdPrice;
        }

        public boolean isCanBuy() {
            return isCanBuy;
        }

        public void setCanBuy(boolean canBuy) {
            isCanBuy = canBuy;
        }

        public String getAcreditInst() {
            return acreditInst;
        }

        public void setAcreditInst(String acreditInst) {
            this.acreditInst = acreditInst;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getNewLargeLogo() {
            return newLargeLogo;
        }

        public void setNewLargeLogo(String newLargeLogo) {
            this.newLargeLogo = newLargeLogo;
        }

        public int getCanBuyBorrowDays() {
            return canBuyBorrowDays;
        }

        public void setCanBuyBorrowDays(int canBuyBorrowDays) {
            this.canBuyBorrowDays = canBuyBorrowDays;
        }

        public String getCatalog() {
            return catalog;
        }

        public void setCatalog(String catalog) {
            this.catalog = catalog;
        }

        public boolean isAddToCart() {
            return isAddToCart;
        }

        public void setAddToCart(boolean addToCart) {
            isAddToCart = addToCart;
        }

        public boolean isCanFreeGet() {
            return isCanFreeGet;
        }

        public void setCanFreeGet(boolean canFreeGet) {
            isCanFreeGet = canFreeGet;
        }

        public String getPriceMessage() {
            return priceMessage;
        }

        public void setPriceMessage(String priceMessage) {
            this.priceMessage = priceMessage;
        }

        public boolean isAlreadyUserBuyBorrow() {
            return isAlreadyUserBuyBorrow;
        }

        public void setAlreadyUserBuyBorrow(boolean alreadyUserBuyBorrow) {
            isAlreadyUserBuyBorrow = alreadyUserBuyBorrow;
        }

        public String getPublishTime() {
            return publishTime;
        }

        public void setPublishTime(String publishTime) {
            this.publishTime = publishTime;
        }

        public String getTryDownLoadUrl() {
            return tryDownLoadUrl;
        }

        public void setTryDownLoadUrl(String tryDownLoadUrl) {
            this.tryDownLoadUrl = tryDownLoadUrl;
        }

        public String getEdition() {
            return edition;
        }

        public void setEdition(String edition) {
            this.edition = edition;
        }

        public String getAdWords() {
            return adWords;
        }

        public void setAdWords(String adWords) {
            this.adWords = adWords;
        }

        public boolean isUserFluentReadAddToCard() {
            return isUserFluentReadAddToCard;
        }

        public void setUserFluentReadAddToCard(boolean userFluentReadAddToCard) {
            isUserFluentReadAddToCard = userFluentReadAddToCard;
        }

        public int getWordCount() {
            return wordCount;
        }

        public void setWordCount(int wordCount) {
            this.wordCount = wordCount;
        }

        public String getCurrentTime() {
            return currentTime;
        }

        public void setCurrentTime(String currentTime) {
            this.currentTime = currentTime;
        }

        public boolean isFluentRead() {
            return isFluentRead;
        }

        public void setFluentRead(boolean fluentRead) {
            isFluentRead = fluentRead;
        }

        public boolean isAlreadyBorrow() {
            return isAlreadyBorrow;
        }

        public void setAlreadyBorrow(boolean alreadyBorrow) {
            isAlreadyBorrow = alreadyBorrow;
        }

        public boolean isEBook() {
            return isEBook;
        }

        public void setEBook(boolean EBook) {
            isEBook = EBook;
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

        public String getNewLogo() {
            return newLogo;
        }

        public void setNewLogo(String newLogo) {
            this.newLogo = newLogo;
        }

        public String getEditorPick() {
            return editorPick;
        }

        public void setEditorPick(String editorPick) {
            this.editorPick = editorPick;
        }

        public boolean isAlreadyBuy() {
            return isAlreadyBuy;
        }

        public void setAlreadyBuy(boolean alreadyBuy) {
            isAlreadyBuy = alreadyBuy;
        }

        public String getBookName() {
            return bookName;
        }

        public void setBookName(String bookName) {
            this.bookName = bookName;
        }

        public float getStar() {
            return star;
        }

        public void setStar(int star) {
            this.star = star;
        }

        public boolean isBorrow() {
            return isBorrow;
        }

        public void setBorrow(boolean borrow) {
            isBorrow = borrow;
        }

        public boolean isBuyBorrow() {
            return isBuyBorrow;
        }

        public void setBuyBorrow(boolean buyBorrow) {
            isBuyBorrow = buyBorrow;
        }

        public boolean isFree() {
            return isFree;
        }

        public void setFree(boolean free) {
            isFree = free;
        }

        public boolean isMyWish() {
            return isMyWish;
        }

        public void setMyWish(boolean myWish) {
            isMyWish = myWish;
        }

        public float getPrice() {
            return price;
        }

        public void setPrice(float price) {
            this.price = price;
        }

        public long getBookId() {
            return bookId;
        }

        public void setBookId(long bookId) {
            this.bookId = bookId;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public boolean isBuy() {
            return isBuy;
        }

        public void setBuy(boolean buy) {
            isBuy = buy;
        }

        public boolean isUserCanFluentRead() {
            return isUserCanFluentRead;
        }

        public void setUserCanFluentRead(boolean userCanFluentRead) {
            isUserCanFluentRead = userCanFluentRead;
        }

        public float getSize() {
            return size;
        }

        public void setSize(float size) {
            this.size = size;
        }

        public long getEbookId() {
            return ebookId;
        }

        public void setEbookId(long ebookId) {
            this.ebookId = ebookId;
        }

        public boolean isReceived() {
            return received;
        }

        public void setReceived(boolean received) {
            this.received = received;
        }

        public String getPromotion() {
            return promotion;
        }

        public void setPromotion(String promotion) {
            this.promotion = promotion;
        }

        public String getPublisher() {
            return publisher;
        }

        public void setPublisher(String publisher) {
            this.publisher = publisher;
        }

        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }
    }
}
