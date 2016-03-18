package com.onyx.kreader.formats.model.zip;

import com.onyx.kreader.formats.encodings.Decoder;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by zengzhu on 3/17/16.
 */
public class ZipFileEntry {

    private ZipFile zipFile;
    private FileHeader fileHeader;

    public ZipFileEntry(final ZipFile parent, final FileHeader header) {
        zipFile = parent;
        fileHeader = header;
    }

    public final InputStream getInputStream() {
        InputStream inputStream = null;
        try {
            inputStream = zipFile.getInputStream(fileHeader);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return inputStream;
        }
    }

}
