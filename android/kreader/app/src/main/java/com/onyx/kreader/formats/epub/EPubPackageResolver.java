package com.onyx.kreader.formats.epub;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.formats.model.zip.ZipFileEntry;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by zengzhu on 3/17/16.
 * To find the package file.
 *
 * <container xmlns="urn:oasis:names:tc:opendocument:xmlns:container" version="1.0">
 <rootfiles>
 <rootfile full-path="OPS/epb.opf" media-type="application/oebps-package+xml"/>
 </rootfiles>
 </container>
 */
public class EPubPackageResolver {

    private ZipFileEntry entry;
    private static String defaultResult = "OEBPS/content.opf";
    private String fullPath;

    public EPubPackageResolver(final ZipFileEntry e) {
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
                    if (localName.equals("rootfile")) {
                        fullPath = attributes.getValue("full-path");
                    }
                }

                @Override
                public void endElement(String uri, String localName, String qName)
                        throws SAXException {
                }

                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                }
            });
        } catch (Exception e) {

        }
    }

    public final String getFullPath() {
        if (StringUtils.isNullOrEmpty(fullPath)) {
            return defaultResult;
        }
        return fullPath;
    }
}
