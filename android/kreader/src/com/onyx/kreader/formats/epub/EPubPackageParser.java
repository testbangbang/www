package com.onyx.kreader.formats.epub;

import android.util.Log;
import com.onyx.kreader.formats.model.BookMetadata;
import com.onyx.kreader.formats.model.zip.ZipFileEntry;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by zengzhu on 3/17/16.
 * To parse OPF package
 * <package xmlns:ibooks="http://www.idpf.org/2007/opf" xmlns="http://www.idpf.org/2007/opf" unique-identifier="BookId" version="3.0" prefix="ibooks: http://www.idpf.org/2007/opf">
 <metadata xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:opf="http://www.idpf.org/2007/opf">
 <dc:title>test4</dc:title>
 <dc:creator id="creator">Zeng Zhu</dc:creator>
 <meta refines="#creator" property="role" scheme="marc:relators">aut</meta>
 <dc:contributor id="contributor">Pages v5.5.3</dc:contributor>
 <meta refines="#contributor" property="role" scheme="marc:relators">bkp</meta>
 <dc:date>2016-03-15</dc:date>
 <dc:identifier id="BookId">DA4BED3C-B6F1-41BD-95DC-AA6F56081984</dc:identifier>
 <dc:language>en</dc:language>
 <meta property="dcterms:modified">2016-03-15T16:53:36Z</meta>
 <meta property="ibooks:specified-fonts">true</meta>
 </metadata>
 <manifest>
 <item id="toc" href="toc.xhtml" media-type="application/xhtml+xml" properties="nav"/>
 <item id="chapter-1" href="chapter-1.xhtml" media-type="application/xhtml+xml"/>
 <item id="chapter-2" href="chapter-2.xhtml" media-type="application/xhtml+xml"/>
 <item id="dataItem1" href="images/image.png" media-type="image/png"/>
 <item id="dataItem2" href="images/image-1.png" media-type="image/png"/>
 <item id="dataItem3" href="images/image-2.png" media-type="image/png"/>
 <item id="dataItem4" href="images/image-3.png" media-type="image/png"/>
 <item id="stylesheet" href="css/book.css" media-type="text/css"/>
 <item id="ncx" href="epb.ncx" media-type="application/x-dtbncx+xml"/>
 </manifest>
 <spine toc="ncx">
 <itemref idref="chapter-1" linear="yes"/>
 <itemref idref="chapter-2" linear="yes"/>
 </spine>
 <guide>
 <reference type="text" title="African Wildlife" href="chapter-1.xhtml"/>
 </guide>
 </package>
 */
public class EPubPackageParser {

    private ZipFileEntry entry;
    static private final String TAG = EPubPackageParser.class.getSimpleName();
    private String currentEntry;
    private boolean inMetadata;
    private boolean inManifest;
    private boolean inSpine;
    private boolean inGuide;
    private EPubPackage ePubPackage = new EPubPackage();

    public static final String METADATA_TAG = "metadata";
    public static final String MANIFEST_TAG = "manifest";
    public static final String SPINE_TAG = "spine";
    public static final String GUIDE_TAG = "guide";


    public EPubPackageParser(final ZipFileEntry e) {
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
                    if (localName.equals(METADATA_TAG)) {
                        enterMetadata();
                    } else if (localName.equals(MANIFEST_TAG)) {
                        enterManifest();
                    } else if (localName.equals(SPINE_TAG)) {
                        enterSpine();
                    } else if (localName.equals(GUIDE_TAG)) {
                        enterGuide();
                    } else {
                        onReceivedTag(uri, localName, qName, attributes);
                    }
                }

                @Override
                public void endElement(String uri, String localName, String qName)
                        throws SAXException {
                    if (localName.equals(METADATA_TAG)) {
                        exitMetadata();
                    } else if (localName.equals(MANIFEST_TAG)) {
                        exitManifest();
                    } else if (localName.equals(SPINE_TAG)) {
                        exitSpine();
                    } else if (localName.equals(GUIDE_TAG)) {
                        exitSpine();
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

    private void enterMetadata() {
        inMetadata = true;
    }

    private void exitMetadata() {
        inMetadata = false;
    }

    private boolean inMetadata() {
        return inMetadata;
    }

    private void enterManifest() {
        inManifest = true;
    }

    private void exitManifest() {
        inManifest = false;
    }

    private boolean inManifest() {
        return inManifest;
    }

    private void enterSpine() {
        inSpine = true;
    }

    private void exitSpine() {
        inSpine = false;
    }

    private boolean inSpine() {
        return inSpine;
    }

    private void enterGuide() {
        inGuide = true;
    }

    private void exitGuide() {
        inGuide = false;
    }

    private boolean inGuide() {
        return inGuide;
    }

    private void onReceivedTag(String uri, String localName, String qName, Attributes attributes) {
        if (inMetadata()) {
            onMetadataTag(uri, localName, qName, attributes);
        } else if (inManifest()) {
            onManifestTag(uri, localName, qName, attributes);
        } else if (inSpine()) {
            onSpineTag(uri, localName, qName, attributes);
        } else if (inGuide()) {
            onGuideTag(uri, localName, qName, attributes);
        }
    }

    private void onReceivedValue(final String string) {
        if (inMetadata()) {
            onMetadataValue(string);
        } else if (inManifest()) {
            onManifestValue(string);
        }
    }

    private void onMetadataTag(String uri, String localName, String qName, Attributes attributes) {
        currentEntry = localName;
    }

    private void onMetadataValue(final String value) {
        ePubPackage.addMetadataItem(currentEntry, value);
    }

    private void onManifestTag(String uri, String localName, String qName, Attributes attributes) {
        ePubPackage.addManifestItem(attributes.getValue("id"), attributes.getValue("href"), attributes.getValue("media-type"));
    }

    private void onManifestValue(final String value) {

    }

    // <itemref idref="chapter-1" linear="yes"/>
    private void onSpineTag(String uri, String localName, String qName, Attributes attributes) {
        ePubPackage.addSpineItem(attributes.getValue("idref"), attributes.getValue("linear"));
    }

    // <reference type="text" title="African Wildlife" href="chapter-1.xhtml"/>
    private void onGuideTag(String uri, String localName, String qName, Attributes attributes) {
        ePubPackage.addGuideItem(attributes.getValue("type"), attributes.getValue("title"), attributes.getValue("href"));
    }

    public final EPubPackage getEPubPackage() {
        return ePubPackage;
    }

}
