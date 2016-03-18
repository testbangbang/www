package com.onyx.kreader.formats.epub;

import android.util.Log;
import com.onyx.kreader.formats.model.zip.ZipFileEntry;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by zengzhu on 3/17/16.
 * Navigation Center eXtension.
 <ncx xmlns="http://www.daisy.org/z3986/2005/ncx/" version="2005-1">
 <head>
 <meta name="dtb:uid" content="DA4BED3C-B6F1-41BD-95DC-AA6F56081984"/>
 <meta name="epub-creator" content="Pages v5.5.3"/>
 <meta name="dtb:depth" content="1"/>
 <meta name="dtb:totalPageCount" content="0"/>
 <meta name="dtb:maxPageNumber" content="0"/>
 </head>
 <docTitle>
 <text>test4</text>
 </docTitle>
 <docAuthor>
 <text>Zeng Zhu</text>
 </docAuthor>
 <navMap>
 <navPoint id="navpoint-1" playOrder="1">
 <navLabel>
 <text>African Wildlife</text>
 </navLabel>
 <content src="chapter-1.xhtml"/>
 <navPoint id="navpoint-2" playOrder="2">
 <navLabel>
 <text>Lorem ipsum dolor sit amet</text>
 </navLabel>
 <content src="chapter-1.xhtml#chapter-1-sh1"/>
 </navPoint>
 </navPoint>
 <navPoint id="navpoint-3" playOrder="3">
 <navLabel>
 <text>African Wildlife</text>
 </navLabel>
 <content src="chapter-2.xhtml"/>
 <navPoint id="navpoint-4" playOrder="4">
 <navLabel>
 <text>Lorem ipsum dolor sit amet</text>
 </navLabel>
 <content src="chapter-2.xhtml#chapter-2-sh1"/>
 </navPoint>
 </navPoint>
 </navMap>
 </ncx>
 */
public class EPubNCXParser {

    private static final String TAG = EPubNCXParser.class.getSimpleName();
    private ZipFileEntry entry;
    private EPubPackage ePubPackage = new EPubPackage();

    private boolean inHead;
    private boolean inTitle;
    private boolean inNavMap;
    private boolean inAuthor;
    private boolean inNavPoint;
    private boolean inNavLabel;
    private boolean inContent;

    public static final String HEAD_TAG = "head";
    public static final String TITLE_TAG = "docTitle";
    public static final String AUTHOR_TAG = "docAuthor";
    public static final String NAV_MAP_TAG = "navMap";
    public static final String NAV_POINT_TAG = "navPoint";
    public static final String NAV_LABEL_TAG = "navLabel";
    public static final String CONTENT_TAG = "content";


    public EPubNCXParser(final ZipFileEntry e) {
        entry = e;
    }

    public void parse() {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            parser.parse(entry.getInputStream(), new DefaultHandler() {
                @Override
                public void startDocument() throws SAXException {
                }

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    if (localName.equals(HEAD_TAG)) {
                        enterHead();
                    } else if (localName.equals(TITLE_TAG)) {
                        enterDocTitle();
                    } else if (localName.equals(AUTHOR_TAG)) {
                        enterAuthor();
                    } else if (localName.equals(NAV_MAP_TAG)) {
                        enterNavMap();
                    } else if (localName.equals(NAV_POINT_TAG)) {
                        enterNavPoint();
                    } else if (localName.equals(NAV_LABEL_TAG)) {
                        enterNavLabel();
                    } else if (localName.equals(CONTENT_TAG)) {
                        enterContent();
                    } else {
                        onReceivedTag(uri, localName, qName, attributes);
                    }
                }

                @Override
                public void endElement(String uri, String localName, String qName)
                        throws SAXException {
                    if (localName.equals(HEAD_TAG)) {
                        exitHead();
                    } else if (localName.equals(TITLE_TAG)) {
                        exitDocTitle();
                    } else if (localName.equals(AUTHOR_TAG)) {
                        exitAuthor();
                    } else if (localName.equals(NAV_MAP_TAG)) {
                        exitNavMap();
                    } else if (localName.equals(NAV_POINT_TAG)) {
                        exitNavPoint();
                    } else if (localName.equals(NAV_LABEL_TAG)) {
                        exitNavLabel();
                    } else if (localName.equals(CONTENT_TAG)) {
                        exitContent();
                    } else {
                    }
                }

                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                    StringBuilder builder = new StringBuilder();
                    builder.append(ch);
                    onReceivedValue(builder.toString());
                }
            });
        } catch (Exception e) {
        }
    }

    private void enterHead() {
        inHead = true;
    }

    private void exitHead() {
        inHead = false;
    }

    private boolean inHead() {
        return inHead;
    }

    private void enterDocTitle() {
        inTitle = true;
    }

    private void exitDocTitle() {
        inTitle = false;
    }

    private boolean inDocTitle() {
        return inTitle;
    }

    private void enterAuthor() {
        inAuthor = true;
    }

    private void exitAuthor() {
        inAuthor = false;
    }

    private boolean inAuthor() {
        return inAuthor;
    }

    private void enterNavMap() {
        inNavMap = true;
    }

    private void exitNavMap() {
        inNavMap = false;
    }

    private boolean inNavMap() {
        return inNavMap;
    }

    private void enterNavPoint() {
        inNavPoint = true;
    }

    private void exitNavPoint() {
        inNavPoint = false;
    }

    private boolean inNavPoint() {
        return inNavPoint;
    }

    private void enterNavLabel() {
        inNavLabel = true;
    }

    private void exitNavLabel() {
        inNavLabel = false;
    }

    private boolean inNavLabel() {
        return inNavLabel;
    }

    private void enterContent() {
        inContent = true;
    }

    private void exitContent() {
        inContent = false;
    }

    private boolean inContent() {
        return inContent;
    }


    private void onReceivedTag(String uri, String localName, String qName, Attributes attributes) {
        if (inDocTitle()) {
            onReceivedDocTitle(uri, localName, qName, attributes);
        } else if (inAuthor()) {
            onReceivedAuthor(uri, localName, qName, attributes);
        } else if (inNavMap()) {

        } else if (inNavPoint()) {

        }
    }

    private void onReceivedValue(final String string) {
    }

    private void onReceivedDocTitle(String uri, String localName, String qName, Attributes attributes) {

    }


    private void onReceivedAuthor(String uri, String localName, String qName, Attributes attributes) {

    }
}
