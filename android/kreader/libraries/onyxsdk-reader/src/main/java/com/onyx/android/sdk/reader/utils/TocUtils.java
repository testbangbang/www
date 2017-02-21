package com.onyx.android.sdk.reader.utils;

import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContentEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 2017/2/21.
 */

public class TocUtils {

    public static List<Integer> buildChapterNodeList(final ReaderDocumentTableOfContent toc) {
        List<Integer> tocChapterNodeList = new ArrayList<>();
        ReaderDocumentTableOfContentEntry rootEntry = toc.getRootEntry();
        if (rootEntry.getChildren() != null) {
            buildChapterNode(rootEntry.getChildren(), tocChapterNodeList);
        }
        return tocChapterNodeList;
    }

    public static void buildChapterNode(final List<ReaderDocumentTableOfContentEntry> entries, final List<Integer> tocChapterNodeList) {
        for (ReaderDocumentTableOfContentEntry entry : entries) {
            if (entry.getChildren() != null) {
                buildChapterNode(entry.getChildren(), tocChapterNodeList);
            } else {
                int position = Integer.valueOf(entry.getPosition());
                if (!tocChapterNodeList.contains(position)) {
                    tocChapterNodeList.add(Integer.valueOf(entry.getPosition()));
                }
            }
        }
    }


}
