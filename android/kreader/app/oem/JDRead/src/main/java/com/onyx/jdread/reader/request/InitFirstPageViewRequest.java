package com.onyx.jdread.reader.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.api.ReaderChineseConvertType;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.reader.common.GammaInfo;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.data.SettingInfo;
import com.onyx.jdread.reader.menu.common.ReaderConfig;

/**
 * Created by huxiaomao on 2017/12/22.
 */

public class InitFirstPageViewRequest extends ReaderBaseRequest {
    private Reader reader;
    private int width;
    private int height;
    private GammaInfo gammaInfo;
    private SettingInfo settingInfo;

    public InitFirstPageViewRequest(Reader reader) {
        super(reader);
        this.reader = reader;
    }

    @Override
    public InitFirstPageViewRequest call() throws Exception {
        updateView();
        initPosition();
        restoreReaderTextStyle();
        reader.getReaderViewHelper().updatePageView(reader, getReaderUserDataInfo(), getReaderViewInfo());
        updateSetting(reader);
        return this;
    }

    private void restoreScale(final Reader reader, String position) throws Exception {
        BaseOptions baseOptions = reader.getReaderHelper().getDocumentOptions();
        if (PageConstants.isSpecialScale(baseOptions.getSpecialScale())) {
            setSpecialScale(reader, baseOptions, position);
        } else {
            setActualScale(reader, baseOptions, position);
        }
    }

    private void setSpecialScale(final Reader reader, final BaseOptions baseOptions, final String position) throws Exception {
        if (PageConstants.isScaleToPage(baseOptions.getSpecialScale())) {
            reader.getReaderHelper().getReaderLayoutManager().scaleToPage(position);
        } else if (PageConstants.isScaleToWidth(baseOptions.getSpecialScale())) {
            reader.getReaderHelper().getReaderLayoutManager().scaleToWidth(position);
        } else if (PageConstants.isScaleToHeight(baseOptions.getSpecialScale())) {
            reader.getReaderHelper().getReaderLayoutManager().scaleToHeight(position);
        } else if (PageConstants.isScaleToPageContent(baseOptions.getSpecialScale())) {
            reader.getReaderHelper().getReaderLayoutManager().scaleToPageContent(position);
        } else if (PageConstants.isWidthCrop(baseOptions.getSpecialScale())) {
            reader.getReaderHelper().getReaderLayoutManager().scaleToWidthContent(position);
        } else {
            reader.getReaderHelper().getReaderLayoutManager().scaleToPage(position);
        }
    }

    private void setActualScale(final Reader reader, final BaseOptions baseOptions, final String position) throws Exception {
        float scale = baseOptions.getActualScale();
        if (scale > PageConstants.SCALE_INVALID && baseOptions.getActualScale() < PageConstants.MAX_SCALE) {
            reader.getReaderHelper().getReaderLayoutManager().setScale(position, baseOptions.getActualScale(), 0, 0);
        } else {
            reader.getReaderHelper().getReaderLayoutManager().scaleToPage(position);
        }
    }

    private void updateView() throws Exception {
        width = reader.getReaderViewHelper().getContentWidth();
        height = reader.getReaderViewHelper().getContentHeight();
        reader.getReaderHelper().updateViewportSize(width, height);
    }

    private void initPosition() throws Exception {
        String bookPath = reader.getDocumentInfo().getBookPath();
        String position = reader.getReaderHelper().getDocumentOptions().getCurrentPage();
        if(StringUtils.isNotBlank(position)){
            int pagePosition = PagePositionUtils.getPosition(position);
            if(pagePosition < 0){
                position = "";
            }
        }
        if (StringUtils.isNullOrEmpty(position)) {
            position = reader.getReaderHelper().getNavigator().getInitPosition();
        }
        reader.getReaderHelper().gotoPosition(position);
        restoreScale(reader, position);
    }

    private void restoreReaderTextStyle() throws ReaderException {
        BaseOptions baseOptions = reader.getReaderHelper().getDocumentOptions();
        settingInfo = new SettingInfo();
        String fontFace = getFontFace();
        settingInfo.settingType = ReaderConfig.getSettingType();
        settingInfo.settingStyle = ReaderConfig.getSettingStyle();
        ReaderTextStyle style = ReaderConfig.presetStyle.get(settingInfo.settingStyle);
        style.setFontFace(fontFace);
        if (settingInfo.settingType != ReaderConfig.SETTING_TYPE_PRESET) {
            ReaderTextStyle customStyle = ReaderConfig.presetStyle.get(ReaderConfig.CUSTOM_STYLE_KEY);
            customStyle.setFontSize(style.getFontSize());
            customStyle.setFontFace(fontFace);

            settingInfo.customLineSpacing = ReaderConfig.getCustomLineSpacing();
            customStyle.getLineSpacing().setPercent(ReaderConfig.customLineSpacing.get(settingInfo.customLineSpacing));

            settingInfo.customLeftAndRightMargin = ReaderConfig.getCustomLeftAndRightMargin();
            ReaderConfig.LeftAndRight leftAndRight = ReaderConfig.getCustomLeftAndRightMargin(settingInfo.settingStyle,settingInfo.customLeftAndRightMargin);
            customStyle.getPageMargin().setLeftMargin(ReaderTextStyle.Percentage.create(leftAndRight.left));
            customStyle.getPageMargin().setRightMargin(ReaderTextStyle.Percentage.create(leftAndRight.right));

            settingInfo.customTopAndBottomMargin = ReaderConfig.getCustomTopAndBottomMargin();
            ReaderConfig.TopAndBottom topAndBottom = ReaderConfig.getCustomTopAndBottomMargin(settingInfo.settingStyle,settingInfo.customTopAndBottomMargin);
            customStyle.getPageMargin().setTopMargin(ReaderTextStyle.Percentage.create(topAndBottom.top));
            customStyle.getPageMargin().setBottomMargin(ReaderTextStyle.Percentage.create(topAndBottom.bottom));

            settingInfo.customParagraphSpacing = ReaderConfig.getCustomParagraphSpacing();
            customStyle.getParagraphSpacing().setPercent(ReaderConfig.customParagraphSpacing.get(settingInfo.customParagraphSpacing));
            style = customStyle;
        }

        reader.getReaderHelper().getReaderLayoutManager().setStyle(style);
        restoreContrast();
        setChineseConvertType();
    }

    private void restoreReaderTextStyleTest() throws ReaderException {
        BaseOptions baseOptions = reader.getReaderHelper().getDocumentOptions();

        String stringStyle = FileUtils.readContentOfFile("/sdcard/style.txt");
        JSONObject styleObj = JSON.parseObject(stringStyle);

        String fontFace = getFontFace();

        ReaderTextStyle.SPUnit spUnit = ReaderTextStyle.SPUnit.create(styleObj.getInteger("font_size"));
        ReaderTextStyle.Percentage lineSpacing = ReaderTextStyle.Percentage.create(styleObj.getInteger("line_spacing"));
        ReaderTextStyle.Percentage paragraphSpacing = ReaderTextStyle.Percentage.create(styleObj.getInteger("paragraph_spacing"));

        ReaderTextStyle.CharacterIndent characterIndent = ReaderTextStyle.CharacterIndent.create((int) baseOptions.getParagraphIndent());

        ReaderTextStyle.Percentage leftMargin = ReaderTextStyle.Percentage.create(styleObj.getInteger("margin_left"));
        ReaderTextStyle.Percentage rightMarin = ReaderTextStyle.Percentage.create(styleObj.getInteger("margin_right"));
        ReaderTextStyle.Percentage topMargin = ReaderTextStyle.Percentage.create(styleObj.getInteger("margin_top"));
        ReaderTextStyle.Percentage BottomMarin = ReaderTextStyle.Percentage.create(styleObj.getInteger("margin_bottom"));

        ReaderTextStyle style = ReaderTextStyle.create(fontFace, spUnit, lineSpacing, leftMargin, topMargin, rightMarin, BottomMarin, paragraphSpacing);
        reader.getReaderHelper().getReaderLayoutManager().setStyle(style);
        restoreContrast();
        setChineseConvertType();
    }

    private String getFontFace() {
        String fontFace = JDPreferenceManager.getStringValue(ReaderConfig.READER_FONTFACE_KEY, null);
        if (StringUtils.isNullOrEmpty(fontFace)) {
            fontFace = ReaderConfig.Typeface.DEFAULT_TYPEFACE;
        }
        return fontFace;
    }

    private void restoreContrast() {
        gammaInfo = new GammaInfo();
        gammaInfo.setEmboldenLevel(JDPreferenceManager.getIntValue(ReaderConfig.READER_EMBOLDENLEVEL_KEY, 0));
        reader.getReaderHelper().getDocumentOptions().setEmboldenLevel(gammaInfo.getEmboldenLevel());
    }

    private void setChineseConvertType() {
        ReaderChineseConvertType convertType = ReaderConfig.getReaderChineseConvertType();
        reader.getReaderHelper().getDocumentOptions().setChineseConvertType(convertType);
        reader.getReaderHelper().getRenderer().setChineseConvertType(convertType);
    }

    public GammaInfo getGammaInfo() {
        return gammaInfo;
    }

    public SettingInfo getSettingInfo() {
        return settingInfo;
    }
}
