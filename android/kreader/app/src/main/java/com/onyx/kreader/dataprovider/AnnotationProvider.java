package com.onyx.kreader.dataprovider;

import android.content.Context;

import java.util.Date;
import java.util.List;

/**
 * Created by zhuzeng on 5/27/16.
 */
public class AnnotationProvider {

    static public class Annotation {

        private static final int INVALID_ID = -1;
        private long mId = INVALID_ID;
        private String mMD5 = null;
        private String mQuote = null;
        private String mLocation = null;
        private Date mUpdateTime = null;
        private String mApplication = null;
        private String mPosition = null;
    }

    public static final List<Annotation> loadAnnotations(final Context context, final String path) {
        return null;
    }

    public static void deleteAnnotation(final Context context, final Annotation annotation) {
    }

    public static boolean hasAnnotation(final Context context, final String pageName) {
        return false;
    }

}
