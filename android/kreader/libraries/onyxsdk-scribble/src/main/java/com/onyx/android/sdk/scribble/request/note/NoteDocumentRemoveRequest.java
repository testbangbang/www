package com.onyx.android.sdk.scribble.request.note;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteDocument;
import com.onyx.android.sdk.scribble.data.ShapeDataProvider;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by zhuzeng on 6/27/16.
 */
public class NoteDocumentRemoveRequest extends BaseNoteRequest {
    private volatile String documentUniqueId;

    public NoteDocumentRemoveRequest(final String id) {
        documentUniqueId = id;
    }

    public void execute(final NoteViewHelper parent) throws Exception {
        String uniqueId = parent.getNoteDocument().getDocumentUniqueId();
        if (StringUtils.isNotBlank(documentUniqueId)) {
            uniqueId = documentUniqueId;
        }
        parent.getNoteDocument().cleanDocument(getContext());
        ShapeDataProvider.removeAllShapeOfDocument(getContext(), uniqueId);
        NoteDataProvider.remove(getContext(), uniqueId);
    }

}
