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


    public static final List<Annotation> loadAnnotations(final Context context, final String docUniqueId, final String pageName) {
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
