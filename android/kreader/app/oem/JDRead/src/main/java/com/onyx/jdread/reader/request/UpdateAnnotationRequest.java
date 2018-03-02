package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.reader.dataprovider.ContentSdkDataUtils;
import com.onyx.jdread.reader.data.NoteInfo;
import com.onyx.jdread.reader.data.Reader;

/**
 * Created by huxiaomao on 2018/1/21.
 */

public class UpdateAnnotationRequest extends ReaderBaseRequest {
    private Reader reader;
    private Annotation annotation;
    private NoteInfo noteInfo;

    public UpdateAnnotationRequest(Reader reader, Annotation annotation, NoteInfo noteInfo) {
        super(reader);
        this.reader = reader;
        this.annotation = annotation;
        this.noteInfo = noteInfo;
    }

    @Override
    public UpdateAnnotationRequest call() throws Exception {
        annotation.setNote(noteInfo.newNote);
        annotation.setQuote(noteInfo.srcNote);
        ContentSdkDataUtils.getDataProvider().updateAnnotation(annotation);
        updateSetting(reader);
        reloadAnnotation();
        return this;
    }

    private void reloadAnnotation(){
        String displayName = reader.getReaderHelper().getPlugin().displayName();
        String md5 = reader.getReaderHelper().getDocumentMd5();

        getReaderUserDataInfo().loadDocumentAnnotations(reader.getReaderHelper().getContext(), displayName, md5);
    }
}
