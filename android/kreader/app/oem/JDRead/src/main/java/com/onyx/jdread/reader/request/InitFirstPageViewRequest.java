package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.menu.common.ReaderConfig;

/**
 * Created by huxiaomao on 2017/12/22.
 */

public class InitFirstPageViewRequest extends ReaderBaseRequest {
    private Reader reader;
    private int width;
    private int height;

    public InitFirstPageViewRequest(Reader reader) {
        this.reader = reader;
    }

    @Override
    public InitFirstPageViewRequest call() throws Exception {
        updateView();
        initPosition();
        restoreReaderTextStyle();
        reader.getReaderViewHelper().updatePageView(reader, getReaderUserDataInfo(),getReaderViewInfo());
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
        String position = PreferenceManager.getStringValue(reader.getReaderHelper().getContext(), bookPath, "0");
        reader.getReaderHelper().gotoPosition(position);
        restoreScale(reader, position);
    }

    private void restoreReaderTextStyle() throws ReaderException {
        BaseOptions baseOptions = reader.getReaderHelper().getDocumentOptions();

        String fontFace = getFontFace();
        ReaderTextStyle.SPUnit spUnit = ReaderTextStyle.SPUnit.create(getFontSize());
        ReaderTextStyle.Percentage lineSpacing = ReaderTextStyle.Percentage.create(getLineSpacing());
        ReaderTextStyle.CharacterIndent characterIndent = ReaderTextStyle.CharacterIndent.create((int) baseOptions.getParagraphIndent());
        ReaderTextStyle.Percentage leftMargin = ReaderTextStyle.Percentage.create(getLeftMarin());
        ReaderTextStyle.Percentage rightMarin = ReaderTextStyle.Percentage.create(getRightMarin());
        ReaderTextStyle.Percentage topMargin = ReaderTextStyle.Percentage.create(getTopMarin());
        ReaderTextStyle.Percentage BottomMarin = ReaderTextStyle.Percentage.create(getBottomMarin());

        ReaderTextStyle style = ReaderTextStyle.create(fontFace, spUnit, lineSpacing, leftMargin, topMargin, rightMarin, BottomMarin);
        reader.getReaderHelper().getReaderLayoutManager().setStyle(style);
    }

    private int getTopMarin(){
        int percent = reader.getReaderHelper().getDocumentOptions().getTopMargin();
        if(percent <= 0){
            percent = ReaderConfig.PageLeftAndRightSpacing.DEFAULT_LEFT_AND_RIGHT_SPACING;
        }
        return percent;
    }

    private int getBottomMarin(){
        int percent = reader.getReaderHelper().getDocumentOptions().getBottomMargin();
        if(percent <= 0){
            percent = ReaderConfig.PageLeftAndRightSpacing.DEFAULT_LEFT_AND_RIGHT_SPACING;
        }
        return percent;
    }

    private int getRightMarin(){
        int percent = reader.getReaderHelper().getDocumentOptions().getRightMargin();
        if(percent <= 0){
            percent = ReaderConfig.PageLeftAndRightSpacing.DEFAULT_LEFT_AND_RIGHT_SPACING;
        }
        return percent;
    }

    private int getLeftMarin(){
        int percent = reader.getReaderHelper().getDocumentOptions().getLeftMargin();
        if(percent <= 0){
            percent = ReaderConfig.PageLeftAndRightSpacing.DEFAULT_LEFT_AND_RIGHT_SPACING;
        }
        return percent;
    }

    private int getLineSpacing(){
        int lineSpacing = reader.getReaderHelper().getDocumentOptions().getLineSpacing();
        if(lineSpacing <= 0){
            lineSpacing = ReaderConfig.PageLineSpacing.DEFAULT_LINE_SPACING;
        }
        return lineSpacing;
    }

    private float getFontSize(){
        float fontSize = reader.getReaderHelper().getDocumentOptions().getFontSize();
        if(fontSize <= 0.0f){
            fontSize = ReaderConfig.FontSize.DEFAULT_FONT_SIZE;
        }
        return fontSize;
    }

    private String getFontFace() {
        String fontFace = reader.getReaderHelper().getDocumentOptions().getFontFace();
        if (StringUtils.isNullOrEmpty(fontFace)) {
            fontFace = ReaderConfig.Typeface.DEFAULT_TYPEFACE;
        }
        return fontFace;
    }
}
