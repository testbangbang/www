package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.common.ReaderDrawContext;
import com.onyx.kreader.host.math.PositionSnapshot;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.utils.ImageUtils;


/**
 * Created by zhuzeng on 2/16/16.
 */
public class PreRenderRequest extends BaseReaderRequest {

    private boolean forward;

    public PreRenderRequest(boolean next) {
        super();
        forward = next;
        setSaveOptions(false);
    }

    public void execute(final Reader reader) throws Exception {
//        ImageUtils.isValidPage();

        if (!reader.getReaderLayoutManager().getCurrentLayoutProvider().supportPreRender()) {
            return;
        }

        // do not save position in history list.
        reader.getReaderLayoutManager().setSavePosition(false);
        final ReaderDrawContext drawContext = ReaderDrawContext.create(true);
        final PositionSnapshot snapshot = reader.getReaderLayoutManager().getCurrentLayoutProvider().saveSnapshot();
        if (forward && reader.getReaderLayoutManager().nextScreen()) {
            drawVisiblePages(reader, drawContext);
            reader.getReaderLayoutManager().getCurrentLayoutProvider().restoreBySnapshot(snapshot);
        } else if (!forward && reader.getReaderLayoutManager().prevScreen()) {
            drawVisiblePages(reader, drawContext);
            reader.getReaderLayoutManager().getCurrentLayoutProvider().restoreBySnapshot(snapshot);
        }
        setTransferBitmap(false);
    }
}
