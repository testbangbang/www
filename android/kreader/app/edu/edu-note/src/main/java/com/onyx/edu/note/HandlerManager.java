package com.onyx.edu.note;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeSpan;
import com.onyx.edu.note.handler.BaseHandler;
import com.onyx.edu.note.handler.ScribbleHandler;
import com.onyx.edu.note.handler.SpanTextHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandlerManager {
    public interface Callback {
        void onActiveProviderChanged(HandlerManager handlerManager);
    }

    public static final String TAG = HandlerManager.class.getSimpleName();
    public static final String SCRIBBLE_PROVIDER = "scribble";
    public static final String SPAN_TEXT_PROVIDER = "span_text";


    private String activeProviderName;
    private Context mContext;
    private Map<String, BaseHandler> providerMap = new HashMap<>();
    private Callback mCallback;

    public HandlerManager(Context context, Callback callback) {
        mContext = context;
        mCallback = callback;
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
        resetToDefaultProvider();
    }

    public void resetToDefaultProvider() {
        setActiveProvider(SCRIBBLE_PROVIDER);
    }

    public void setActiveProvider(final String providerName) {
        if (getActiveProvider() != null) {
            getActiveProvider().onDeactivate();
        }
        activeProviderName = providerName;
        getActiveProvider().onActivate();
        if (mCallback != null) {
            mCallback.onActiveProviderChanged(this);
        }
    }

    @Nullable
    public BaseHandler getActiveProvider() {
        if (TextUtils.isEmpty(activeProviderName)) {
            return null;
        }
        return providerMap.get(activeProviderName);
    }

    public String getActiveProviderName() {
        return activeProviderName;
    }

    //TODO:temp solution for 2 handler only situation.
    public void switchProvider(){
        setActiveProvider(activeProviderName.equals(SCRIBBLE_PROVIDER) ? SPAN_TEXT_PROVIDER : SCRIBBLE_PROVIDER);
    }

    public List<Integer> getMainMenuFunctionIDList() {
        if (getActiveProvider() == null) {
            return null;
        }
        return getActiveProvider().getMainMenuFunctionIDList();
    }
}
