package com.onyx.android.dr.util;


import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.database.BookDetailEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 17-1-10.
 */

public class Scanner {
    private static List<String> defaultType;
    private static Scanner scanner;

    private Scanner() {
    }

    public static Scanner instance() {
        if (scanner == null) {
            synchronized (Scanner.class) {
                if (scanner == null) {
                    scanner = new Scanner();
                    loadType();
                }
            }
        }
        return scanner;
    }

    private static void loadType() {
        defaultType = new ArrayList<>();
        defaultType.add(".txt");
        defaultType.add(".pdf");
        defaultType.add(".cbz");
        defaultType.add(".epub");
        defaultType.add(".fb2");
        defaultType.add(".djvu");
        defaultType.add(".abf");
        defaultType.add(".bmp");
        defaultType.add(".chm");
        defaultType.add(".doc");
        defaultType.add(".azw");
        defaultType.add(".azw3");
        defaultType.add(".cbr");
        defaultType.add(".doc");
        defaultType.add(".docm");
        defaultType.add(".docx");
        defaultType.add(".fbz");
        defaultType.add(".mobi");
        defaultType.add(".odt");
        defaultType.add(".pdb");
        defaultType.add(".prc");
        defaultType.add(".rtf");
        defaultType.add(".sxw");
        defaultType.add(".trc");
    }

    public List<BookDetailEntity> getLocalLibrary() {
        ArrayList<BookDetailEntity> books = new ArrayList<>();
        File file = new File(Constants.LOCAL_BOOK_DIRECTORY);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                String path = f.getAbsolutePath();
                if (f.isFile() && path.lastIndexOf(".") != -1 && defaultType.contains(path.substring(path.lastIndexOf(".")).toLowerCase())) {
                    BookDetailEntity entity = new BookDetailEntity();
                    entity.bookName = path.substring(path.lastIndexOf("/") + 1);
                    entity.localPath = path;
                    entity.bookDir = Constants.LOCAL_LIBRARY;
                    entity.format = path.substring(path.lastIndexOf(".") + 1).toUpperCase();
                    entity.bookId = path.hashCode();
                    entity.isTryRead = true;
                    entity.state = FileDownloadStatus.completed;
                    entity.isLocal = true;
                    entity.percentage = Constants.MAX_PERCENTAGE;
                    books.add(entity);
                }
            }
        }
        return books;
    }
}
