package com.onyx.android.sdk.reader.common;

import android.graphics.RectF;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.reader.host.math.PageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joy on 7/19/16.
 */
public class PageAnnotation {
    private PageInfo pageInfo;
    private Annotation annotation;
    private ArrayList<RectF> rectangles = new ArrayList<>();

    public PageAnnotation(PageInfo pageInfo, Annotation annotation) {
        this.pageInfo = pageInfo;
        this.annotation = annotation;

        if (annotation.getRectangles() != null) {
            for (RectF rect : annotation.getRectangles()) {
                rectangles.add(new RectF(rect));
            }
            translateToScreen(pageInfo, rectangles);
        }
    }

    private void translateToScreen(PageInfo pageInfo, List<RectF> list) {
        for (RectF rect : list) {
            PageUtils.translate(pageInfo.getDisplayRect().left,
                    pageInfo.getDisplayRect().top,
                    pageInfo.getActualScale(),
                    rect);
        }
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public ArrayList<RectF> getRectangles() {
        return rectangles;
    }

    public PageAnnotation copy(){
        Annotation annotation = new Annotation();
        annotation.setQuote(getAnnotation().getQuote());
        annotation.setQuoteState(getAnnotation().getQuoteState());
        annotation.setKey(getAnnotation().getKey());
        annotation.setNote(getAnnotation().getNote());
        annotation.setChapterName(getAnnotation().getChapterName());
        annotation.setApplication(getAnnotation().getApplication());
        annotation.setLocationBegin(getAnnotation().getLocationBegin());
        annotation.setLocationEnd(getAnnotation().getLocationEnd());
        annotation.setPageNumber(getAnnotation().getPageNumber());
        annotation.setPosition(getAnnotation().getPosition());
        annotation.setRectangles(getAnnotation().getRectangles());
        annotation.setCreatedAt(getAnnotation().getCreatedAt());
        annotation.setGuid(getAnnotation().getGuid());
        annotation.setId(getAnnotation().getId());
        annotation.setIdString(getAnnotation().getIdString());
        annotation.setUpdatedAt(getAnnotation().getUpdatedAt());

        PageAnnotation pageAnnotation = new PageAnnotation(pageInfo,annotation);
        pageAnnotation.rectangles.addAll(getRectangles());
        return pageAnnotation;
    }
}
