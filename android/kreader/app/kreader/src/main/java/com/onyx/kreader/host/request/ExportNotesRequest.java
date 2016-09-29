package com.onyx.kreader.host.request;

import com.onyx.android.sdk.scribble.shape.NormalPencilShape;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.note.NoteManager;
import com.onyx.kreader.note.data.ReaderNotePage;
import com.onyx.kreader.plugins.neopdf.NeoPdfJniWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class ExportNotesRequest extends BaseReaderRequest {

    private NoteManager noteManager;

    public ExportNotesRequest(NoteManager noteManager) {
        this.noteManager = noteManager;
    }

    public void execute(final Reader reader) throws Exception {
        List<Shape> shapeList = new ArrayList<>();
        List<String> pageList = noteManager.getNoteDocument().getPageList();
        for (String page : pageList) {
            ReaderNotePage notePage = noteManager.getNoteDocument().loadPage(getContext(), page, 0);
            if (notePage != null) {
                if (!notePage.isLoaded()) {
                    notePage.loadPage(getContext());
                }
                for (Shape shape : notePage.getShapeList()) {
                    if (shape instanceof NormalPencilShape) {
                        shapeList.add(shape);
                    }
                }
            }
        }
        getReaderUserDataInfo().loadDocumentAnnotations(getContext(), reader);
        reader.getDocument().exportNotes(reader.getDocumentPath(),
                getExportDocPath(reader.getDocumentPath()),
                getReaderUserDataInfo().getAnnotations(),
                shapeList);
    }

    private String getExportDocPath(String sourceDocPath) {
        String parent = FileUtils.getParent(sourceDocPath);
        String baseName = FileUtils.getBaseName(sourceDocPath);
        String ext = FileUtils.getFileExtension(sourceDocPath);
        return new File(parent, baseName + "-Exported." + ext).getAbsolutePath();
    }

}
