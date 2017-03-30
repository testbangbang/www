package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.data.FontInfo;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.utils.DeviceUtils;

import java.util.List;

/**
 * Created by ming on 2016/11/18.
 */

public class GetFontsRequest extends BaseReaderRequest {

    private List<Object> list;
    private List<FontInfo> fonts;
    private String currentFont;
    private DeviceUtils.FontType fontType;

    public GetFontsRequest(String currentFont, DeviceUtils.FontType fontType, List<Object> list) {
        this.currentFont = currentFont;
        this.fontType = fontType;
        this.list = list;
    }

    @Override
    public void execute(Reader reader) throws Exception {
        fonts = DeviceUtils.buildFontItemAdapter(reader.getPluginOptions().getFontDirectories(),
                currentFont, null, fontType, list);
    }

    public List<FontInfo> getFonts() {
        return fonts;
    }
}
