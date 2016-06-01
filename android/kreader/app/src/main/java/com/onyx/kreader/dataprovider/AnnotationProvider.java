package com.onyx.kreader.dataprovider;

import android.content.Context;
import android.graphics.Rect;
import com.onyx.kreader.dataprovider.request.BaseDataProviderRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhuzeng on 5/27/16.
 */
public class AnnotationProvider {

    static public class Annotation extends BaseData {

        private String quote = null;
        private String locationBegin = null;
        private String locationEnd = null;
        private String note = null;
        private String application = null;
        private String position = null;
        private List<Rect> rects = new ArrayList<Rect>();

        public void setQuote(final String q) {
            quote = q;
        }

        public String getQuote() {
            return quote;
        }

        public void setLocationBegin(final String l) {
            locationBegin = l;
        }

        public String getLocationBegin() {
            return locationBegin;
        }

        public void setLocationEnd(final String l) {
            locationEnd = l;
        }

        public String getLocationEnd() {
            return locationBegin;
        }

        public void setNote(final String n) {
            note = n;
        }

        public final String getNote() {
            return note;
        }

        public void setApplication(final String app) {
            application = app;
        }

        public String getApplication() {
            return application;
        }

        public void setPosition(final String p) {
            position = p;
        }

        public String getPosition() {
            return position;
        }

        public void setRects(final List<Rect> rectList) {
            rects.addAll(rectList);
        }

        public List<Rect> getRects() {
            return rects;
        }
    }

    public static final List<Annotation> loadAnnotations(final Context context, final String path) {
        return null;
    }

    public static boolean addAnnotation(final Context context, final Annotation annotation){
        return false;
    }

    public static void deleteAnnotation(final Context context, final Annotation annotation) {
    }

    public static boolean hasAnnotation(final Context context, final String pageName) {
        return false;
    }

}
