package com.onyx.sdk.ebookservice.epub;

import android.graphics.Bitmap;

import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.sdk.ebookservice.Constant;
import com.onyx.sdk.ebookservice.CoverWrapper;
import com.onyx.sdk.ebookservice.HtmlParser;
import com.onyx.sdk.ebookservice.Parser;
import com.onyx.sdk.ebookservice.utils.CoverImageUtils;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;
import org.htmlcleaner.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by suicheng on 2017/2/11.
 */
public class EpubParser implements Parser<EpubObject> {
    private static final String NEW_LINE_CHAR = "\n";
    private static final int HTML_URL_TIMEOUT_MILLIS = 15 * 1000;
    private static final int IMAGE_URL_TIMEOUT_MILLIS = 5 * 1000;

    private String cacheDir;
    private String url;
    private String baseUrl;

    private String title;
    private String filePath;
    private CoverWrapper coverWrapper;
    private String cleanHtml;
    private Map<String, String> imageUrlMap = new HashMap<>();

    private boolean enableClearResource = true;

    public EpubParser(String cacheDir, String url, CoverWrapper coverWrapper) {
        this.cacheDir = cacheDir;
        this.url = url;
        this.coverWrapper = coverWrapper;
        initCacheDir();
    }

    private String parseHtmlContent() throws Exception {
        HtmlParser htmlParser = new HtmlParser(new URL(url), HTML_URL_TIMEOUT_MILLIS);
        htmlParser.init();
        return htmlParser.outerHtml();
    }

    private String getBaseUrl() {
        if (StringUtils.isNotBlank(baseUrl)) {
            return baseUrl;
        }
        String prefix = null;
        if (url.startsWith(Constant.HTTP_PREFIX)) {
            prefix = Constant.HTTP_PREFIX;
        } else if (url.startsWith(Constant.HTTPS_PREFIX)) {
            prefix = Constant.HTTPS_PREFIX;
        }
        if (StringUtils.isNullOrEmpty(prefix)) {
            return null;
        }
        String suffix = "/";
        String tmp = url.replaceFirst(prefix, "");
        int index = tmp.indexOf(suffix);
        if (index < 0) {
            baseUrl = prefix + tmp + suffix;
        } else {
            baseUrl = prefix + tmp.substring(0, index) + suffix;
        }
        return baseUrl;
    }

    private String getContentImagePath() {
        return new File(cacheDir, UUID.randomUUID().toString() + Constant.PNG_EXTENSION).getAbsolutePath();
    }

    private String getContentImageFullUrlPath(String imgUrl) {
        if (imgUrl.startsWith(Constant.HTTP_PREFIX) || imgUrl.startsWith(Constant.HTTPS_PREFIX)) {
            return imgUrl;
        }
        String baseUrl = getBaseUrl();
        if (StringUtils.isNullOrEmpty(baseUrl)) {
            return imgUrl;
        }
        return baseUrl + imgUrl;
    }

    private String getImgAttributeHref(String hrefName) {
        return Constant.IMG_HREF + hrefName;
    }

    private String cleanHtmlContent(String htmlContent) {
        CleanerProperties props = new CleanerProperties();
        props.setAdvancedXmlEscape(true);
        props.setTransResCharsToNCR(true);

        HtmlCleaner cleaner = new HtmlCleaner(props);
        TagNode node = cleaner.clean(htmlContent);
        node.traverse(new TagNodeVisitor() {
            public boolean visit(TagNode tagNode, HtmlNode htmlNode) {
                if (htmlNode instanceof TagNode) {
                    TagNode tag = (TagNode) htmlNode;
                    String tagName = tag.getName();
                    if (Constant.TITLE_TAG.equals(tagName)) {
                        if (StringUtils.isNullOrEmpty(title)) {
                            title = tag.getText().toString();
                        }
                    } else if (Constant.IMG_TAG.equals(tagName)) {
                        String imageUrl = tag.getAttributeByName(Constant.SRC_ATTRIBUTE);
                        if (StringUtils.isNotBlank(imageUrl)) {
                            String imgSrcFilePath = getContentImagePath();
                            imageUrlMap.put(getContentImageFullUrlPath(imageUrl), imgSrcFilePath);
                            Map<String, String> map = tag.getAttributes();
                            map.put(Constant.SRC_ATTRIBUTE, getImgAttributeHref(FileUtils.getFileName(imgSrcFilePath)));
                            tag.setAttributes(map);
                        }
                    }
                }
                return true;
            }
        });
        downloadContentImageList();
        XmlSerializer serializer = new PrettyXmlSerializer(props);
        return serializer.getAsString(node);
    }

    private void downloadContentImageList() {
        Set<String> keySet = imageUrlMap.keySet();
        if (!CollectionUtils.isNullOrEmpty(keySet)) {
            String[] urls = keySet.toArray(new String[0]);
            for (String url : urls) {
                downloadImage(url, imageUrlMap.get(url));
            }
        }
    }

    private void downloadImage(String url, String filePath) {
        InputStream is = null;
        OutputStream os = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(IMAGE_URL_TIMEOUT_MILLIS);
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = conn.getInputStream();
                File file = new File(filePath);
                os = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
                os.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtils.deleteFile(filePath);
        } finally {
            FileUtils.closeQuietly(is);
            FileUtils.closeQuietly(os);
        }
    }

    @Override
    public String cleanHtmlContent() throws Exception {
        String htmlContent = parseHtmlContent();
        return cleanHtml = cleanHtmlContent(htmlContent);
    }

    private String getTitle() {
        if (StringUtils.isNullOrEmpty(title)) {
            title = UUID.randomUUID().toString();
        }
        return title;
    }

    @Override
    public EpubObject generateOutPutObject() throws Exception {
        if (StringUtils.isNullOrEmpty(cleanHtml)) {
            return null;
        }
        EpubObject epubObject;
        try {
            File file = getXhtmlFile();
            writeContentToFile(cleanHtml, file);
            List<String> list = new ArrayList<>();
            list.add(file.getAbsolutePath());
            String outputFilePath = file.getAbsolutePath().replace(Constant.XHTML_EXTENSION, Constant.EPUB_EXTENSION);
            epubObject = new EpubObject(getTitle(), getCoverBitmap(), list, getContentImagePathList());
            epubObject.generateEPUBBook(outputFilePath);
        } catch (Exception e) {
            throw e;
        } finally {
            clearResource();
        }
        return epubObject;
    }

    private Bitmap getCoverBitmap() {
        if (coverWrapper != null && coverWrapper.bitmap != null) {
            coverWrapper.text = getTitle();
            return CoverImageUtils.drawTextToCenter(coverWrapper);
        }
        return null;
    }

    private List<String> getContentImagePathList() {
        List<String> imageList = new ArrayList<>();
        if (!CollectionUtils.isNullOrEmpty(imageUrlMap)) {
            String[] imageUrls = imageUrlMap.values().toArray(new String[0]);
            imageList.addAll(Arrays.asList(imageUrls));
        }
        return imageList;
    }

    private void writeContentToFile(String htmlContent, File file) throws Exception {
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(htmlContent.getBytes());
    }

    private void initCacheDir() {
        try {
            File dir = new File(cacheDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
        }
    }

    private File getXhtmlFile() {
        if (StringUtils.isNotBlank(filePath)) {
            return new File(filePath);
        }
        String fileName = title + Constant.XHTML_EXTENSION;
        String newName = FileUtils.fixNotAllowFileName(fileName);
        if (newName != null) {
            fileName = newName.replaceAll(NEW_LINE_CHAR, "").trim();
            if (!fileName.contains(title)) {
                title = fileName.replace(Constant.XHTML_EXTENSION, "");
            }
        }
        File file = new File(cacheDir, fileName);
        filePath = file.getAbsolutePath();
        return file;
    }

    private void clearResource() {
        if (!enableClearResource) {
            return;
        }
        try {
            FileUtils.deleteFile(getXhtmlFile().getAbsolutePath());
            List<String> list = getContentImagePathList();
            for (String path : list) {
                FileUtils.deleteFile(path);
            }
        } catch (Exception e) {
        }
    }
}
