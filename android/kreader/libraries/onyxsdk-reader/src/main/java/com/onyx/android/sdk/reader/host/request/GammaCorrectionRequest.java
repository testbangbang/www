package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.utils.ImageUtils;

/**
 * Created by zhuzeng on 10/5/15.
 * redraw request
 */
public class GammaCorrectionRequest extends BaseReaderRequest {

    private int globalGamma = BaseOptions.getGlobalDefaultGamma();
    private int imageGamma = BaseOptions.getGlobalDefaultGamma();
    private int textGamma = BaseOptions.getGlobalDefaultGamma();
    private int emboldenLevel = 0;

    public GammaCorrectionRequest(final int globalGamma, final int imageGamma, final int textGamma, final int emboldenLevel) {
        this.globalGamma = globalGamma;
        this.imageGamma = imageGamma;
        this.textGamma = textGamma;
        this.emboldenLevel = emboldenLevel;
    }

    public void execute(final Reader reader) throws Exception {
        setSaveOptions(true);
        reader.getDocumentOptions().setGamma(imageGamma);
        reader.getDocumentOptions().setTextGamma(textGamma);
        if (emboldenLevel >= BaseOptions.minEmboldenLevel()) {
            reader.getDocumentOptions().setEmboldenLevel(emboldenLevel);
        }
        if (reader.getRenderer().getRendererFeatures().supportFontGammaAdjustment()) {
            float gamma = ImageUtils.getGammaCorrectionBySelection(textGamma);
            reader.getRenderer().setTextGamma(gamma);
        }
        drawVisiblePages(reader);
    }

}
