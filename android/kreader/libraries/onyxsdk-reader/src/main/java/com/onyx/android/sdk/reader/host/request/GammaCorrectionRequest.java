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

    private int gamma = BaseOptions.getGlobalDefaultGamma();
    private int emboldenLevel = 0;

    public GammaCorrectionRequest(final int gamma, final int emboldenLevel) {
        this.gamma = gamma;
        this.emboldenLevel = emboldenLevel;
    }

    public void execute(final Reader reader) throws Exception {
        setSaveOptions(true);
        reader.getDocumentOptions().setGamma(gamma);
        reader.getDocumentOptions().setTextGamma(gamma);
        if (emboldenLevel >= BaseOptions.minEmboldenLevel()) {
            reader.getDocumentOptions().setEmboldenLevel(emboldenLevel);
        }
        if (reader.getRenderer().getRendererFeatures().supportFontGammaAdjustment()) {
            float textGamma = ImageUtils.getGammaCorrectionBySelection(gamma);
            reader.getRenderer().setTextGamma(textGamma);
        }
        drawVisiblePages(reader);
    }

}
