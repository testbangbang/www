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
    private GammaInfo gammaInfo;
    private SettingInfo settingInfo;

    public GammaCorrectionRequest(final Reader reader, GammaInfo gammaInfo,final SettingInfo settingInfo) {
        super(reader);
        this.gammaInfo = gammaInfo;
        this.settingInfo = settingInfo;
    }

    @Override
    public GammaCorrectionRequest call() throws Exception {
        getReader().getReaderHelper().getDocumentOptions().setEmboldenLevel(gammaInfo.getEmboldenLevel());
        getReader().getReaderHelper().getDocumentOptions().setGamma(gammaInfo.getImageGamma());
        getReader().getReaderHelper().getDocumentOptions().setTextGamma(gammaInfo.getTextGamma());
        if (gammaInfo.getEmboldenLevel() >= BaseOptions.minEmboldenLevel()) {
            getReader().getReaderHelper().getDocumentOptions().setEmboldenLevel(gammaInfo.getEmboldenLevel());
        }
        if (getReader().getReaderHelper().getRenderer().getRendererFeatures().supportFontGammaAdjustment()) {
            float gamma = ImageUtils.getGammaCorrectionBySelection(gammaInfo.getTextGamma());
            getReader().getReaderHelper().getRenderer().setTextGamma(gamma);
        }
        getReader().getReaderViewHelper().updatePageView(getReader(),getReaderUserDataInfo(),getReaderViewInfo());
        updateSetting(getReader());
        saveReaderOptions(getReader());
        saveStyleOptions(getReader(),settingInfo);
        return this;
    }
}
