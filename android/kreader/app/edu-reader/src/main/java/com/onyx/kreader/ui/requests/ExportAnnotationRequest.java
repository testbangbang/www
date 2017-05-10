package com.onyx.kreader.ui.requests;

import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.utils.ExportUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.R;

import java.io.File;
import java.util.List;

/**
 * Created by ming on 2016/10/28.
 */

public class ExportAnnotationRequest extends BaseReaderRequest {

    private List<Annotation> annotations;
    private boolean append = false;

    public ExportAnnotationRequest(final List<Annotation> annotations, final boolean append) {
        this.annotations = annotations;
        this.append = append;
    }

    @Override
    public void execute(Reader reader) throws Exception {
        StringBuilder content = new StringBuilder();
        for (Annotation annotation : annotations) {
            String item = getContext().getString(R.string.annotation_export_format, annotation.getPageNumber() + 1, annotation.getNote(), annotation.getQuote());
            content.append(item);
        }

        File file = new File(ExportUtils.getExportAnnotationPath(reader.getDocumentPath()));
        if (append) {
            FileUtils.appendContentToFile(content.toString(), file);
        }else {
            FileUtils.saveContentToFile(content.toString(), file);
        }
    }
}
