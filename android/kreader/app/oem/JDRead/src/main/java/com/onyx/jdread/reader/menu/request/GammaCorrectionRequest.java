package com.onyx.jdread.reader.menu.request;


import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.utils.ImageUtils;
import com.onyx.jdread.reader.common.GammaInfo;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.data.SettingInfo;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by zhuzeng on 10/5/15.
 * redraw request
 */
public class GammaCorrectionRequest extends ReaderBaseRequest {
    private Reader reader;
    private GammaInfo gammaInfo;
    private SettingInfo settingInfo;

    public GammaCorrectionRequest(final Reader reader, GammaInfo gammaInfo,final SettingInfo settingInfo) {
        this.gammaInfo = gammaInfo;
        this.reader = reader;
        this.settingInfo = settingInfo;
    }

    @Override
    public GammaCorrectionRequest call() throws Exception {
        reader.getReaderHelper().getDocumentOptions().setEmboldenLevel(gammaInfo.getEmboldenLevel());
        reader.getReaderHelper().getDocumentOptions().setGamma(gammaInfo.getImageGamma());
        reader.getReaderHelper().getDocumentOptions().setTextGamma(gammaInfo.getTextGamma());
        if (gammaInfo.getEmboldenLevel() >= BaseOptions.minEmboldenLevel()) {
            reader.getReaderHelper().getDocumentOptions().setEmboldenLevel(gammaInfo.getEmboldenLevel());
        }
        if (reader.getReaderHelper().getRenderer().getRendererFeatures().supportFontGammaAdjustment()) {
            float gamma = ImageUtils.getGammaCorrectionBySelection(gammaInfo.getTextGamma());
            reader.getReaderHelper().getRenderer().setTextGamma(gamma);
        }
        reader.getReaderViewHelper().updatePageView(reader,getReaderUserDataInfo(),getReaderViewInfo());
        updateSetting(reader);
        saveReaderOptions(reader,settingInfo);
        return this;
    }
}
