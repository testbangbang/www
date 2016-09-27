package com.onyx.kreader.host.request;

import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.plugins.neopdf.NeoPdfJniWrapper;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class ExportNotesRequest extends BaseReaderRequest {

    public ExportNotesRequest() {
    }

    public void execute(final Reader reader) throws Exception {
        getReaderUserDataInfo().loadDocumentAnnotations(getContext(), reader);
        reader.getDocument().exportNotes(reader.getDocumentPath(),
                getExportDocPath(reader.getDocumentPath()),
                getReaderUserDataInfo().getAnnotations(),
                new ArrayList<Shape>());
    }

    private String getExportDocPath(String sourceDocPath) {
        String parent = FileUtils.getParent(sourceDocPath);
        String baseName = FileUtils.getBaseName(sourceDocPath);
        String ext = FileUtils.getFileExtension(sourceDocPath);
        return new File(parent, baseName + "-Exported." + ext).getAbsolutePath();
    }

}
