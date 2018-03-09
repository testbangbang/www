package com.onyx.jdread.reader.highlight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.common.PageAnnotation;
import com.onyx.android.sdk.utils.RectUtils;
import com.onyx.jdread.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.onyx.jdread.reader.menu.common.ReaderConfig.FONT_SIZE_LARGE;
import static com.onyx.jdread.reader.menu.common.ReaderConfig.FONT_SIZE_MEDIUM;
import static com.onyx.jdread.reader.menu.common.ReaderConfig.FONT_SIZE_SMALL;
import static com.onyx.jdread.reader.menu.common.ReaderConfig.FONT_SIZE_XX_LARGE;
import static com.onyx.jdread.reader.menu.common.ReaderConfig.FONT_SIZE_X_LARGE;
import static com.onyx.jdread.reader.menu.common.ReaderConfig.FONT_SIZE_X_SMALL;

/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 4/6/14
 * Time: 3:00 PM
 * * update selection
 * * move by user
 * * move by caller
 * * draw
 */
public class ReaderSelectionHelper {
    private Map<String, SelectionInfo> readerSelectionInfos = new HashMap<>();
    private int currentFontSize = FONT_SIZE_MEDIUM;
    private int chooseLeftIcon = R.mipmap.ic_read_word_left_3;
    private int chooseRightIcon = R.mipmap.ic_read_word_right_3;

    public ReaderSelectionHelper() {
        super();
    }

    public void setCurrentFontSize(int currentFontSize) {
        this.currentFontSize = currentFontSize;
    }

    public ReaderSelection getCurrentSelection(String pagePosition) {
        SelectionInfo readerSelectionInfo = readerSelectionInfos.get(pagePosition);
        if (readerSelectionInfo != null) {
            return readerSelectionInfo.getCurrentSelection();
        }
        return null;
    }

    public SelectionInfo getReaderSelectionInfo(String pagePosition) {
        return readerSelectionInfos.get(pagePosition);
    }

    public HighlightCursor getHighlightCursor(String pagePosition, int index) {
        SelectionInfo readerSelectionInfo = readerSelectionInfos.get(pagePosition);
        if (readerSelectionInfo != null) {
            if (index >= 0 && index < readerSelectionInfo.getCursors().size()) {
                return readerSelectionInfo.getCursors().get(index);
            }
        }
        return null;
    }

    public String getSelectText() {
        String result = "";
        for (SelectionInfo readerSelectionInfo : readerSelectionInfos.values()) {
            if (readerSelectionInfo.getCurrentSelection() != null) {
                result += readerSelectionInfo.getCurrentSelection().getText();
            }
        }
        return result;
    }

    public boolean normalize(String pagePosition) {
        SelectionInfo readerSelectionInfo = readerSelectionInfos.get(pagePosition);
        if (readerSelectionInfo != null) {
            RectF begin = readerSelectionInfo.getCursors().get(0).getHotPoint();
            RectF end = readerSelectionInfo.getCursors().get(1).getHotPoint();
            if ((begin.top > end.top) || (begin.top == end.top && begin.left > end.left)) {
                HighlightCursor beginCursor = readerSelectionInfo.getCursors().get(0);
                beginCursor.setCursorType(HighlightCursor.Type.END_CURSOR);
                HighlightCursor endCursor = readerSelectionInfo.getCursors().get(1);
                endCursor.setCursorType(HighlightCursor.Type.BEGIN_CURSOR);
                readerSelectionInfo.getCursors().clear();
                readerSelectionInfo.getCursors().add(endCursor);
                readerSelectionInfo.getCursors().add(beginCursor);
                return true;
            }
        }
        return false;
    }

    public int tracking(String pagePosition, final float sx, final float sy, final float ex, final float ey) {
        SelectionInfo readerSelectionInfo = readerSelectionInfos.get(pagePosition);
        if (readerSelectionInfo != null) {
            for (int i = 0; i < readerSelectionInfo.getCursors().size(); ++i) {
                if (readerSelectionInfo.getCursors().get(i).tracking(sx, sy, ex, ey)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void draw(String pagePosition, Canvas canvas, Paint paint) {
        SelectionInfo readerSelectionInfo = readerSelectionInfos.get(pagePosition);
        if (readerSelectionInfo != null) {
            for (HighlightCursor cursor : readerSelectionInfo.getCursors()) {
                if (cursor.getShowState()) {
                    cursor.draw(canvas, paint);
                }
            }
        }
    }

    public void clear() {
        readerSelectionInfos.clear();
    }

    private boolean getCurrentFontSizeChooseIcon(ReaderTextStyle style) {
        if (style == null) {
            chooseLeftIcon = R.mipmap.ic_read_word_left_3;
            chooseRightIcon = R.mipmap.ic_read_word_right_3;
            return false;
        }
        int fontSize = (int) style.getFontSize().getValue();
        if (currentFontSize == fontSize) {
            return false;
        }
        currentFontSize = fontSize;
        switch (fontSize) {
            case FONT_SIZE_X_SMALL:
                chooseLeftIcon = R.mipmap.ic_read_word_left_1;
                chooseRightIcon = R.mipmap.ic_read_word_right_1;
                break;
            case FONT_SIZE_SMALL:
                chooseLeftIcon = R.mipmap.ic_read_word_left_2;
                chooseRightIcon = R.mipmap.ic_read_word_right_2;
                break;
            case FONT_SIZE_MEDIUM:
                chooseLeftIcon = R.mipmap.ic_read_word_left_3;
                chooseRightIcon = R.mipmap.ic_read_word_right_3;
                break;
            case FONT_SIZE_LARGE:
                chooseLeftIcon = R.mipmap.ic_read_word_left_4;
                chooseRightIcon = R.mipmap.ic_read_word_right_4;
                break;
            case FONT_SIZE_X_LARGE:
                chooseLeftIcon = R.mipmap.ic_read_word_left_5;
                chooseRightIcon = R.mipmap.ic_read_word_right_5;
                break;
            case FONT_SIZE_XX_LARGE:
                chooseLeftIcon = R.mipmap.ic_read_word_left_6;
                chooseRightIcon = R.mipmap.ic_read_word_right_6;
                break;
            default:
                chooseLeftIcon = R.mipmap.ic_read_word_left_3;
                chooseRightIcon = R.mipmap.ic_read_word_right_3;
                break;
        }
        return true;
    }

    public boolean update(String pagePosition, final Context context,
                                       ReaderSelection readerSelection, PointF lastPoint,
                                       PageInfo pageInfo,
                                       ReaderTextStyle style,List<PageAnnotation> pageAnnotations) {
        SelectionInfo readerSelectionInfo = readerSelectionInfos.get(pagePosition);
        if (readerSelectionInfo == null || readerSelectionInfo.getCurrentSelection() == null) {
            readerSelectionInfo = addPageSelection(pagePosition, readerSelection, pageInfo,pageAnnotations);
        } else {
            readerSelectionInfo.setCurrentSelection(readerSelection, pageInfo,pageAnnotations);
        }
        readerSelectionInfo.setTouchPoint(lastPoint);
        List<RectF> rects = readerSelectionInfo.getCurrentSelection().getRectangles();
        if (rects == null || rects.size() <= 0) {
            return false;
        }
        if (getCurrentFontSizeChooseIcon(style) || readerSelectionInfo.getCursors().size() <= 0) {
            readerSelectionInfo.getCursors().clear();
            readerSelectionInfo.getCursors().add(new HighlightCursor(context, chooseLeftIcon, chooseRightIcon, HighlightCursor.Type.BEGIN_CURSOR));
            readerSelectionInfo.getCursors().add(new HighlightCursor(context, chooseLeftIcon, chooseRightIcon, HighlightCursor.Type.END_CURSOR));
        }
        HighlightCursor cursor = readerSelectionInfo.getCursors().get(0);
        float fontHeight = rects.get(0).bottom - rects.get(0).top;
        cursor.setFontHeight(fontHeight);
        PointF beginBottom = RectUtils.getBeginTop(rects);
        cursor.setOriginPosition(beginBottom.x, beginBottom.y + (currentFontSize / 5));
        cursor.setCursorType(HighlightCursor.Type.BEGIN_CURSOR);
        cursor = readerSelectionInfo.getCursors().get(1);
        PointF endBottom = RectUtils.getEndRight(rects);
        cursor.setFontHeight(fontHeight);
        cursor.setOriginPosition(endBottom.x, endBottom.y + (currentFontSize / 5));
        cursor.setCursorType(HighlightCursor.Type.END_CURSOR);
        return true;
    }

    private SelectionInfo addPageSelection(String pagePosition, ReaderSelection readerSelection, PageInfo pageInfo,List<PageAnnotation> pageAnnotations) {
        SelectionInfo readerSelectionInfo = new SelectionInfo();
        readerSelectionInfo.setCurrentSelection(readerSelection, pageInfo,pageAnnotations);
        readerSelectionInfo.setPagePosition(pagePosition);
        readerSelectionInfos.put(pagePosition, readerSelectionInfo);
        return readerSelectionInfo;
    }

    public void deletePageSelection(String pagePosition) {
        readerSelectionInfos.remove(pagePosition);
    }

    public void updateDisplayPosition(String pagePosition) {
        SelectionInfo readerSelectionInfo = readerSelectionInfos.get(pagePosition);
        if (readerSelectionInfo != null) {
            for (HighlightCursor cursor : readerSelectionInfo.getCursors()) {
                cursor.updateDisplayPosition();
            }
        }
    }

    public void setEnable(String pagePosition, boolean enable) {
        SelectionInfo readerSelectionInfo = readerSelectionInfos.get(pagePosition);
        if (readerSelectionInfo != null) {
            for (HighlightCursor cursor : readerSelectionInfo.getCursors()) {
                cursor.setEnable(enable);
            }
        }
    }

    public boolean isEnable(String pagePosition) {
        SelectionInfo readerSelectionInfo = readerSelectionInfos.get(pagePosition);
        if (readerSelectionInfo != null) {
            for (HighlightCursor cursor : readerSelectionInfo.getCursors()) {
                if (cursor != null) {
                    return cursor.isEnable();
                }
            }
        }
        return false;
    }

    public Map<String, SelectionInfo> getReaderSelectionInfos() {
        return readerSelectionInfos;
    }
}
