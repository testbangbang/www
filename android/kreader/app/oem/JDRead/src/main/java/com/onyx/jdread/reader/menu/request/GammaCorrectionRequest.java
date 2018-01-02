package com.onyx.jdread.reader.menu.request;


import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.utils.ImageUtils;
import com.onyx.jdread.reader.common.GammaInfo;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by zhuzeng on 10/5/15.
 * redraw request
 */
public class GammaCorrectionRequest extends ReaderBaseRequest {
    private ReaderDataHolder readerDataHolder;
    private GammaInfo gammaInfo;

    public GammaCorrectionRequest(final ReaderDataHolder readerDataHolder, GammaInfo gammaInfo) {
        this.gammaInfo = gammaInfo;
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public GammaCorrectionRequest call() throws Exception {
        if (readerDataHolder.getReader().getReaderHelper().getRenderer().getRendererFeatures().supportFontGammaAdjustment()) {
            float gamma = ImageUtils.getGammaCorrectionBySelection(gammaInfo.getTextGamma());
            readerDataHolder.getReader().getReaderHelper().getRenderer().setTextGamma(gamma);
        }
        readerDataHolder.getReaderViewHelper().updatePageView(readerDataHolder);
        return this;
    }
}
