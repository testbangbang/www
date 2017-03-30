package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.FontInfo;
import com.onyx.android.sdk.reader.host.request.GetFontsRequest;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.List;

/**
 * Created by ming on 2016/11/18.
 */

public class GetFontsAction extends BaseAction{

    private List<Object> disableFontsList;
    private List<FontInfo> fonts;
    private String currentFont;
    private DeviceUtils.FontType fontType;

    public GetFontsAction(final String currentFont, final DeviceUtils.FontType fontType, final List<Object> disableFontsList) {
        this.currentFont = currentFont;
        this.fontType = fontType;
        this.disableFontsList = disableFontsList;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback callback) {

        final GetFontsRequest getFontsRequest = new GetFontsRequest(currentFont, fontType, disableFontsList);
        readerDataHolder.getReader().submitRequest(readerDataHolder.getContext(), getFontsRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                fonts = getFontsRequest.getFonts();
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

    public List<FontInfo> getFonts() {
        return fonts;
    }
}
