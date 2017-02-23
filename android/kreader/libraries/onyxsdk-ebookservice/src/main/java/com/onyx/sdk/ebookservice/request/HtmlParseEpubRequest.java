package com.onyx.sdk.ebookservice.request;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.sdk.ebookservice.CoverWrapper;
import com.onyx.sdk.ebookservice.HtmlManager;
import com.onyx.sdk.ebookservice.Parser;
import com.onyx.sdk.ebookservice.epub.EpubObject;
import com.onyx.sdk.ebookservice.epub.EpubParser;

/**
 * Created by suicheng on 2017/2/11.
 */
public class HtmlParseEpubRequest extends BaseHtmlRequest {

    private String url;
    private String cacheDir;
    private CoverWrapper coverWrapper;
    private String outputFilePath;
    private EpubObject epubObject;

    public HtmlParseEpubRequest(String cacheDir, String url, CoverWrapper coverWrapper) {
        this.cacheDir = cacheDir;
        this.url = url;
        this.coverWrapper = coverWrapper;
    }

    public EpubObject getEpubObject() {
        return epubObject;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    @Override
    public void execute(HtmlManager parent) throws Exception {
        if (StringUtils.isNullOrEmpty(url)) {
            setException(new Throwable("url is nullOrEmpty"));
            return;
        }
        Parser<EpubObject> parser = new EpubParser(cacheDir, url, coverWrapper);
        parser.cleanHtmlContent();
        epubObject = parser.generateOutPutObject();
        if (epubObject != null) {
            outputFilePath = epubObject.getOutputFilePath();
        }
    }
}
