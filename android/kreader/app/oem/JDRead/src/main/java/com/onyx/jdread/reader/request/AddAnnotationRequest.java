package com.onyx.jdread.reader.request;

import android.graphics.RectF;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.dataprovider.ContentSdkDataUtils;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.highlight.SelectionInfo;
import com.onyx.jdread.reader.utils.ReaderViewUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by huxiaomao on 2018/1/20.
 */

public class AddAnnotationRequest extends ReaderBaseRequest {
    private Map<String, SelectionInfo> readerSelectionInfos;
    private String note;

    public AddAnnotationRequest(Reader reader, Map<String, SelectionInfo> readerSelectionInfos,String note) {
        super(reader);
        this.readerSelectionInfos = readerSelectionInfos;
        this.note = note;
    }

    @Override
    public AddAnnotationRequest call() throws Exception {
        saveAnnotation();
        getReader().getReaderViewHelper().updatePageView(getReader(), getReaderUserDataInfo(), getReaderViewInfo());
        updateSetting(getReader());
        reloadAnnotation();
        return this;
    }

    private void reloadAnnotation(){
        String displayName = getReader().getReaderHelper().getPlugin().displayName();
        String md5 = getReader().getReaderHelper().getDocumentMd5();

        getReaderUserDataInfo().loadDocumentAnnotations(getReader().getReaderHelper().getContext(), displayName, md5);
    }

    private void saveAnnotation() {
        String key = ReaderViewUtil.getListKey(readerSelectionInfos);
        for (SelectionInfo readerSelectionInfo : readerSelectionInfos.values()) {
            ReaderSelection selection = readerSelectionInfo.getCurrentSelection();
            Annotation annotation = createAnnotation(getReader(),key,readerSelectionInfo.pageInfo,
                    selection.getStartPosition(), selection.getEndPosition(),
                    selection.getRectangles(), selection.getText(), note,readerSelectionInfo.pageInfo.getChapterName());

            ContentSdkDataUtils.getDataProvider().addAnnotation(annotation);
        }
    }

    public static Annotation createAnnotation(Reader reader,String key,PageInfo pageInfo, String locationBegin, String locationEnd,
                                        List<RectF> rects, String quote, String note,String chapterName) {
        Annotation annotation = new Annotation();
        annotation.setIdString(reader.getReaderHelper().getDocumentMd5());
        annotation.setApplication(reader.getReaderHelper().getPlugin().displayName());
        annotation.setPosition(pageInfo.getPosition());
        annotation.setPageNumber(PagePositionUtils.getPageNumber(pageInfo.getName()));
        annotation.setLocationBegin(locationBegin);
        annotation.setLocationEnd(locationEnd);
        annotation.setQuote(quote);
        annotation.setNote(note);
        annotation.setRectangles(rects);
        annotation.setChapterName(chapterName);
        annotation.setKey(key);
        return annotation;
    }
}
