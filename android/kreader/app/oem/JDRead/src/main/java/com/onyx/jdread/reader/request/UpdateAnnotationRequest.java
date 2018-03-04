package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.reader.dataprovider.ContentSdkDataUtils;
import com.onyx.jdread.reader.data.NoteInfo;
import com.onyx.jdread.reader.data.Reader;

import java.util.List;

/**
 * Created by huxiaomao on 2018/1/21.
 */

public class UpdateAnnotationRequest extends ReaderBaseRequest {
    private Annotation annotation;
    public String newNote;
    public String srcNote;
    public int srcNoteState;

    public UpdateAnnotationRequest(Reader reader, Annotation annotation, String newNote,String srcNote,int srcNoteState) {
        super(reader);
        this.annotation = annotation;
        this.newNote = newNote;
        this.srcNote = srcNote;
        this.srcNoteState = srcNoteState;
    }

    @Override
    public UpdateAnnotationRequest call() throws Exception {
        String key = annotation.getKey();
        List<Annotation> annotations = getReaderUserDataInfo().loadDocumentKeyAnnotations(getReader().getReaderHelper().getContext(),
                getReader().getReaderHelper().getPlugin().displayName(),
                key);

        for(Annotation a:annotations) {
            a.setNote(newNote);
            a.setQuote(srcNote);
            a.setQuoteState(srcNoteState);
            ContentSdkDataUtils.getDataProvider().updateAnnotation(a);
        }
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
