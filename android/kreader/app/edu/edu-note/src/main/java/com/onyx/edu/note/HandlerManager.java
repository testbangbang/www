package com.onyx.edu.note;

import android.content.Context;
import android.text.SpannableStringBuilder;

import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeSpan;
import com.onyx.edu.note.handler.BaseHandler;
import com.onyx.edu.note.handler.ScribbleHandler;
import com.onyx.edu.note.handler.SpanTextHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandlerManager {
    public static final String TAG = HandlerManager.class.getSimpleName();
    public static final String SCRIBBLE_PROVIDER = "scribble";
    public static final String SPAN_TEXT_PROVIDER = "span_text";


    private String activeProviderName;
    private Context mContext;
    private Map<String, BaseHandler> providerMap = new HashMap<>();

    public HandlerManager(Context context) {
        mContext = context;
        initProviderMap();
    }

    private void initProviderMap() {
        NoteManager manager = NoteManager.sharedInstance(mContext);
        providerMap.put(SCRIBBLE_PROVIDER, new ScribbleHandler(manager));
        providerMap.put(SPAN_TEXT_PROVIDER, new SpanTextHandler(manager, new SpanTextHandler.Callback() {
            @Override
            public void OnFinishedSpan(SpannableStringBuilder builder, List<Shape> spanShapeList, ShapeSpan lastShapeSpan) {

            }
        }));
        activeProviderName = SCRIBBLE_PROVIDER;
    }

    public void resetToDefaultProvider() {
        setActiveProvider(SCRIBBLE_PROVIDER);
    }

    public void setActiveProvider(final String providerName) {
        getActiveProvider().onDeactivate();
        activeProviderName = providerName;
        getActiveProvider().onActivate();
    }

    public BaseHandler getActiveProvider() {
        return providerMap.get(activeProviderName);
    }

    public String getActiveProviderName() {
        return activeProviderName;
    }
}
