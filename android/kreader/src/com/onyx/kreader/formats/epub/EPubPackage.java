package com.onyx.kreader.formats.epub;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengzhu on 3/18/16.
 * <item id="chapter-1" href="chapter-1.xhtml" media-type="application/xhtml+xml"/>
 * <itemref idref="chapter-1" linear="yes"/>
 */
public class EPubPackage {

    static public class MetadataItem {
        public String name;
        public String value;

        public MetadataItem(final String n, final String v) {
            name = n;
            value = v;
        }
    }

    static public class ManifestItem {
        public String id;
        public String href;
        public String mediaType;

        public ManifestItem(final String i, final String h, final String m) {
            id = i;
            href = h;
            mediaType = m;
        }
    }

    static public class SpineItem {
        public String idref;
        public String linear;

        public SpineItem(final String id, final String l) {
            idref = id;
            linear = l;
        }
    }

    static public class GuideItem {
        public String type;
        public String title;
        public String href;

        public GuideItem(final String t, final String ti, final String h) {
            type = t;
            title = ti;
            href = h;
        }
    }

    static public class NavPoint {
        public String id;
        public String playOrder;
        public String label;
        public String content;
        public NavPoint parent;
        public List<NavPoint> child = new ArrayList<NavPoint>();

        public NavPoint() {
        }

        public NavPoint(final String i, final String p, final String n, final String c) {
            id = i;
            playOrder = p;
            label = n;
            content = c;
        }
    }

    private List<MetadataItem> metadataItemList = new ArrayList<MetadataItem>();
    private List<ManifestItem> manifestItemList = new ArrayList<ManifestItem>();
    private List<SpineItem> spineItemList = new ArrayList<SpineItem>();
    private List<GuideItem> guideItemList = new ArrayList<GuideItem>();
    private List<NavPoint> navMap = new ArrayList<NavPoint>();

    public void addMetadataItem(final String name, final String value) {
        metadataItemList.add(new MetadataItem(name, value));
    }

    public void addManifestItem(final String id, final String href, final String mediaType) {
        manifestItemList.add(new ManifestItem(id, href, mediaType));
    }

    public void addSpineItem(final String idRef, final String l) {
        spineItemList.add(new SpineItem(idRef, l));
    }

    public void addGuideItem(final String type, final String title, final String href) {
        guideItemList.add(new GuideItem(type, title, href));
    }

    public void addNavPoint(final NavPoint navPoint) {
        navMap.add(navPoint);
    }

    public final String getNcxHref() {
        for(ManifestItem item: manifestItemList) {
            if (item.href.endsWith("ncx")) {
                return item.href;
            }
        }
        return null;
    }


}
