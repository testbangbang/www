package com.onyx.jdread.data.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by hehai on 17-3-7.
 */
@Table(database = JDBookStoreDatabase.class)
public class BookDetailEntity extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    public long id;
    @Column
    public long bookId;
    @Column
    public boolean isUserFluentReadAddToCard;
    @Column
    public boolean isEBook;
    @Column
    public String paperBookId;
    @Column
    public String imageUrl;
    @Column
    public String largeImageUrl;
    @Column
    public String newImageUrl;
    @Column
    public String bookName = "";
    @Column
    public String info;
    @Column
    public float star;
    @Column
    public String author;
    @Column
    public String format;
    @Column
    public double size;
    @Column
    public double price;
    @Column
    public float jdPrice;
    @Column
    public boolean isFree;
    @Column
    public boolean isBuy;
    @Column
    public boolean isBorrow;
    @Column
    public boolean isFluentRead;
    @Column
    public boolean isUserCanFluentRead;
    @Column
    public boolean isTryRead;
    @Column
    public boolean isCanBuy;
    @Column
    public boolean isCanFreeGet;
    @Column
    public boolean isAlreadyBuy;
    @Column
    public String orderId;
    @Column
    public boolean isAlreadyBorrow;
    @Column
    public String priceMessage;
    @Column
    public String catalog;
    @Column
    public String authorInfo;
    @Column
    public String promotion;
    @Column
    public String tryDownLoadUrl;
    @Column
    public String publisher;
    @Column
    public String name;
    @Column
    public String categoryPath;
    @Column
    public float fileSize;
    @Column
    public String good;
    @Column
    public int orderMode;
    @Column
    public int orderStatus;
    @Column
    public String orderStatusName;
    @Column
    public String orderTime;
    @Column
    public boolean readcard;
    @Column
    public int sentStatus;
    @Column
    public long joinTime;
    @Column
    public long readingTime;
    @Column
    public String localPath;
    @Column
    public int state = 0;
    @Column
    public int reference = 0;
    @Column
    public long percentage = 0;
    @Column
    public int readPages = 0;
    @Column
    public int totalPages = 1;
    @Column
    public String key;
    @Column
    public String random;
    @Column
    public String UU_ID;
    @Column
    public String bookDir = "";
    @Column
    public String downloadUrl = "";
    @Column
    public float orgJdPrice;
    @Column
    public String edition;
    @Column
    public String isbn;
    @Column
    public String publishTime;
    @Column
    public boolean isWholeBook;
    public boolean isChecked;
    public boolean isLocal;
}