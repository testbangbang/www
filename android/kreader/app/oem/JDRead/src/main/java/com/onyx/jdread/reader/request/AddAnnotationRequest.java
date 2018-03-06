package com.onyx.jdread.reader.request;

import android.graphics.RectF;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.dataprovider.ContentSdkDataUtils;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.highlight.SelectionInfo;
import com.onyx.jdread.reader.menu.common.ReaderConfig;
import com.onyx.jdread.reader.utils.MapKeyComparator;
import com.onyx.jdread.reader.utils.ReaderViewUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by huxiaomao on 2018/1/20.
 */

public class AddAnnotationRequest extends ReaderBaseRequest {
    private Map<String, SelectionInfo> readerSelectionInfos;
    private String newNote;
    private String srcNote;
    private int srcNoteState;

    public AddAnnotationRequest(Reader reader, Map<String, SelectionInfo> readerSelectionInfos,String newNote,String srcNote,int srcNoteState) {
        super(reader);
        this.readerSelectionInfos = readerSelectionInfos;
        this.srcNote = srcNote;
        this.newNote = newNote;
        this.srcNoteState = srcNoteState;
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
        if(srcNoteState == ReaderConfig.QUOTE_STATE_NOT_CHANGED) {
            srcNote = getSelectText();
        }
        for (SelectionInfo readerSelectionInfo : readerSelectionInfos.values()) {
            ReaderSelection selection = readerSelectionInfo.getCurrentSelection();
            Annotation annotation = createAnnotation(getReader(),key,readerSelectionInfo.pageInfo,
                    selection.getStartPosition(), selection.getEndPosition(),
                    selection.getRectangles(), srcNote, newNote,readerSelectionInfo.pageInfo.getChapterName(),srcNoteState);

            ContentSdkDataUtils.getDataProvider().addAnnotation(annotation);
        }
    }

    public String getSelectText(){
        String result = "";
        List<Map.Entry<String, SelectionInfo>> list = new ArrayList<Map.Entry<String, SelectionInfo>>(readerSelectionInfos.entrySet());
        Collections.sort(list, new MapKeyComparator());

        for (int i = 0; i < list.size(); i++) {
            Map.Entry<String, SelectionInfo> stringStringEntry = list.get(i);
            SelectionInfo readerSelectionInfo = stringStringEntry.getValue();
            result += readerSelectionInfo.getCurrentSelection().getText();
        }
        return result;
    }

    public static Annotation createAnnotation(Reader reader,String key,PageInfo pageInfo, String locationBegin, String locationEnd,
                                        List<RectF> rects, String quote, String note,String chapterName,int quoteState) {
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
        annotation.setQuoteState(quoteState);
        return annotation;
    }
}
