package com.onyx.kreader.formats.epub;

import android.app.ListActivity;
import android.util.Log;
import com.onyx.kreader.formats.model.zip.ZipFileEntry;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

    public static final String HEAD_TAG = "head";
    public static final String TITLE_TAG = "docTitle";
    public static final String AUTHOR_TAG = "docAuthor";
    public static final String NAV_MAP_TAG = "navMap";
    public static final String NAV_POINT_TAG = "navPoint";
    public static final String NAV_LABEL_TAG = "navLabel";
    public static final String CONTENT_TAG = "content";
    public static final String TEXT_TAG = "text";


    public EPubNCXParser(final ZipFileEntry e) {
        entry = e;
    }

    public void parse() {
        try {
            // create output stream to save data read from zip file.
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte data[] = new byte[10 * 1024];
            final InputStream inputStream = entry.getInputStream();
            while (inputStream.read(data) > 0) {
                byteArrayOutputStream.write(data);
            }

            // create input stream from data of output stream.
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(byteArrayInputStream);
            parseTitle(doc);
            parseAuthor(doc);
            parseNavMap(doc);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final String getText(final Document document, final String parentTag, final String childTag) {
        final NodeList list = document.getElementsByTagName(parentTag);
        if (list == null || list.getLength() <= 0) {
            return null;
        }
        final NodeList child = list.item(0).getChildNodes();
        for(int i = 0; i < child.getLength(); ++i) {
            final Node node = child.item(i);
            if (node.getNodeName().equals(childTag)) {
                return node.getTextContent();
            }
        }
        return null;
    }



    private void parseTitle(final Document doc) {
        final String title = getText(doc, TITLE_TAG, TEXT_TAG);
        ePubPackage.addMetadataItem(TITLE_TAG, title);
    }

    private void parseAuthor(final Document doc) {
        final String value = getText(doc, AUTHOR_TAG, TEXT_TAG);
        ePubPackage.addMetadataItem(AUTHOR_TAG, value);
    }

    /**
     * <navMap>
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
     * @param doc
     */
    private void parseNavMap(final Document doc) {
        final NodeList nodeList = doc.getElementsByTagName(NAV_MAP_TAG);
        if (nodeList.getLength() <= 0) {
            return;
        }
        final Node navMap  = nodeList.item(0);
        final NodeList childNodes = navMap.getChildNodes();
        for(int i = 0; i < childNodes.getLength(); ++i) {
            final Node child = childNodes.item(i);
            ePubPackage.addNavPoint(parseNavPoint(child, null));
        }
    }

    private EPubPackage.NavPoint parseNavPoint(final Node node, final EPubPackage.NavPoint parent) {
        EPubPackage.NavPoint navPoint = new EPubPackage.NavPoint();
        navPoint.parent = parent;

        final Node label = getFirstChildByName(node, NAV_LABEL_TAG);
        final Node textNode = getFirstChildByName(label, TEXT_TAG);
        navPoint.label = getTextContent(textNode);
        navPoint.id = getAttribute(node, "id");
        navPoint.playOrder = getAttribute(node, "playOrder");

        final Node contentNode = getFirstChildByName(node, CONTENT_TAG);
        navPoint.content = getAttribute(contentNode, "src");
        List<Node> list = getChildNodesByName(node, NAV_POINT_TAG);
        for(Node n : list) {
            navPoint.child.add(parseNavPoint(n, navPoint));
        }
        return navPoint;

    }

    private final String getTextContent(final Node node) {
        if (node != null) {
            return node.getTextContent();
        }
        return null;
    }


    private final String getAttribute(final Node node, final String name) {
        if (node == null) {
            return null;
        }
        final NamedNodeMap attributes = node.getAttributes();
        if (attributes == null) {
            return null;
        }
        final Node item = attributes.getNamedItem(name);
        if (item == null) {
            return null;
        }
        return item.getTextContent();
    }

    private final Node getFirstChildByName(final Node node, final String name) {
        if (node == null) {
            return null;
        }
        final NodeList childNodes = node.getChildNodes();
        for(int i = 0; i < childNodes.getLength(); ++i) {
            final Node child = childNodes.item(i);
            String localName = child.getNodeName();
            if (localName != null && localName.equals(name)) {
                return child;
            }
        }
        return null;
    }

    private final List<Node> getChildNodesByName(final Node node, final String name) {
        if (node == null) {
            return null;
        }
        List<Node> list = new ArrayList<Node>();
        final NodeList childNodes = node.getChildNodes();
        for(int i = 0; i < childNodes.getLength(); ++i) {
            final Node child = childNodes.item(i);
            String localName = child.getNodeName();
            if (localName != null && localName.equals(name)) {
                list.add(child);
            }
        }
        return list;
    }
}
