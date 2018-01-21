package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.dataprovider.ContentSdkDataUtils;
import com.onyx.jdread.reader.data.NoteInfo;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.highlight.ReaderSelectionInfo;

/**
 * Created by huxiaomao on 2018/1/21.
 */

public class UpdateAnnotationRequest extends ReaderBaseRequest {
    private Reader reader;
    private ReaderSelectionInfo readerSelectionInfo;
    private NoteInfo noteInfo;

    public UpdateAnnotationRequest(Reader reader, ReaderSelectionInfo readerSelectionInfo, NoteInfo noteInfo) {
        this.reader = reader;
        this.readerSelectionInfo = readerSelectionInfo;
        this.noteInfo = noteInfo;
    }

    @Override
    public UpdateAnnotationRequest call() throws Exception {

        ReaderSelection selection = readerSelectionInfo.getCurrentSelection();
        Annotation annotation = AddAnnotationRequest.createAnnotation(reader, readerSelectionInfo.pageInfo,
                selection.getStartPosition(), selection.getEndPosition(),
                selection.getRectangles(), noteInfo.srcNote, noteInfo.newNote);

        ContentSdkDataUtils.getDataProvider().addAnnotation(annotation);
        return this;
    }
}
