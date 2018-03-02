package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.reader.dataprovider.ContentSdkDataUtils;
import com.onyx.jdread.reader.data.NoteInfo;
import com.onyx.jdread.reader.data.Reader;

/**
 * Created by huxiaomao on 2018/1/21.
 */

public class UpdateAnnotationRequest extends ReaderBaseRequest {
    private Annotation annotation;
    private NoteInfo noteInfo;

    public UpdateAnnotationRequest(Reader reader, Annotation annotation, NoteInfo noteInfo) {
        super(reader);
        this.annotation = annotation;
        this.noteInfo = noteInfo;
    }

    @Override
    public UpdateAnnotationRequest call() throws Exception {
        annotation.setNote(noteInfo.newNote);
        annotation.setQuote(noteInfo.srcNote);
        ContentSdkDataUtils.getDataProvider().updateAnnotation(annotation);
        updateSetting(getReader());
        reloadAnnotation();
        return this;
    }

    private void reloadAnnotation(){
        String displayName = getReader().getReaderHelper().getPlugin().displayName();
        String md5 = getReader().getReaderHelper().getDocumentMd5();

        getReaderUserDataInfo().loadDocumentAnnotations(getReader().getReaderHelper().getContext(), displayName, md5);
    }
}
