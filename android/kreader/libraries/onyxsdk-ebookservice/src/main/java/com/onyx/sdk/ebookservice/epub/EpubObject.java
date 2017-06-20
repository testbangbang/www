package com.onyx.sdk.ebookservice.epub;

import android.graphics.Bitmap;

import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.sdk.ebookservice.Constant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;

/**
 * Created by suicheng on 2017/2/11.
 */
public class EpubObject {

    private Book book;
    private String title;
    private Bitmap coverImageBitmap;
    private List<String> chapterHtmlPathList;
    private List<String> imagePathList;

    private String filePath;

    public EpubObject(String title, Bitmap coverImageBitmap, List<String> chapterHtmlPathList, List<String> imagePathList) {
        this.title = title;
        this.coverImageBitmap = coverImageBitmap;
        this.chapterHtmlPathList = chapterHtmlPathList;
        this.imagePathList = imagePathList;
    }

    private InputStream getResource(String path) throws Exception {
        return new FileInputStream(new File(path));
    }

    private Resource getResource(String path, String href) throws Exception {
        return new Resource(getResource(path), href);
    }

    private Resource getResource(byte[] data, String href) throws Exception {
        return new Resource(data, href);
    }

    public String getOutputFilePath() {
        return filePath;
    }

    public void generateEPUBBook(String outputFilePath) throws Exception {
        filePath = outputFilePath;
        book = new Book();
        Metadata metadata = book.getMetadata();
        if (StringUtils.isNotBlank(title)) {
            metadata.addTitle(title);
        }
        addBookCoverImage();
        addBookChapterSection();
        addBookContentImage();
        EpubWriter epubWriter = new EpubWriter();
        epubWriter.write(book, new FileOutputStream(filePath));
    }

    private void addBookCoverImage() {
        if (coverImageBitmap != null) {
            try {
                book.setCoverImage(getResource(getBitmapByteArray(coverImageBitmap), Constant.IMG_HREF + Constant.COVER_NAME));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addBookContentImage() {
        try {
            if (CollectionUtils.isNullOrEmpty(imagePathList)) {
                return;
            }
            for (String path : imagePathList) {
                book.getResources().add(getResource(path, Constant.IMG_HREF + FileUtils.getFileName(path)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addBookChapterSection() throws Exception {
        for (int i = 0; i < chapterHtmlPathList.size(); i++) {
            String title = String.format(Constant.CHAPTER_PATTERN, (i + 1));
            book.addSection(title, getResource(chapterHtmlPathList.get(i), title + Constant.XHTML_EXTENSION));
        }
    }

    private byte[] getBitmapByteArray(Bitmap bitmap) {
        if (bitmap != null && bitmap.getWidth() > 0 && bitmap.getHeight() > 0) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            return os.toByteArray();
        }
        return null;
    }
}
