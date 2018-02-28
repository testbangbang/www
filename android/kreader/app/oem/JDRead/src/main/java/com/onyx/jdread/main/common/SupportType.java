package com.onyx.jdread.main.common;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by hehai on 18-1-24.
 */

public class SupportType {
    private static final Set<String> docExtension = new HashSet<String>();
    private static final Set<String> supportThumbnailType = new HashSet<String>();

    public static Set<String> getDocumentExtension() {
        if (docExtension.isEmpty()) {
            docExtension.add("doc");
            docExtension.add("docx");
            docExtension.add("pdf");
            docExtension.add("epub");
            docExtension.add("txt");
            docExtension.add("mobi");
            docExtension.add("png");
            docExtension.add("jpeg");
            docExtension.add("jpg");
        }
        return docExtension;
    }

    public static Set<String> getSupportThumbnailType() {
        if (supportThumbnailType.isEmpty()) {
            supportThumbnailType.add("epub");
        }
        return supportThumbnailType;
    }
}
