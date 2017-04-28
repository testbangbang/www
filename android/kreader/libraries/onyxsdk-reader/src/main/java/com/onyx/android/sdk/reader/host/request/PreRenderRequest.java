package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.common.ReaderDrawContext;
import com.onyx.android.sdk.reader.host.math.PositionSnapshot;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.utils.ImageUtils;


/**
 * Created by zhuzeng on 2/16/16.
 */
public class PreRenderRequest extends BaseReaderRequest {

    private boolean forward;
    private boolean transferBitmap = false;
    private boolean checkIsValidPage = true;

    public PreRenderRequest(boolean next) {
        super();
        forward = next;
        setSaveOptions(false);
    }

    public PreRenderRequest(boolean forward, boolean transferBitmap, boolean checkIsValidPage) {
        this(forward);
        this.transferBitmap = transferBitmap;
        this.checkIsValidPage = checkIsValidPage;
    }

    public void execute(final Reader reader) throws Exception {
        if (checkIsValidPage) {
            ImageUtils.isValidPage();
        }

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
        setTransferBitmap(transferBitmap);
    }
}
