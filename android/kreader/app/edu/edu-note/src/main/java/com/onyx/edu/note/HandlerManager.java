package com.onyx.edu.note;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeSpan;
import com.onyx.edu.note.data.ScribbleSubMenuID;
import com.onyx.edu.note.data.ScribbleToolBarMenuID;
import com.onyx.edu.note.handler.BaseHandler;
import com.onyx.edu.note.handler.ScribbleHandler;
import com.onyx.edu.note.handler.SpanTextHandler;
import com.onyx.edu.note.scribble.ScribbleViewModel;

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
    private ScribbleViewModel mViewModel;

    public HandlerManager(Context context, ScribbleViewModel viewModel) {
        mContext = context;
        mViewModel = viewModel;
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
        getActiveProvider().setScribbleViewModel(mViewModel);
        getActiveProvider().onActivate();
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
    public void switchProvider() {
        setActiveProvider(activeProviderName.equals(SCRIBBLE_PROVIDER) ? SPAN_TEXT_PROVIDER : SCRIBBLE_PROVIDER);
    }

    public List<Integer> getFunctionBarMenuFunctionIDList() {
        if (getActiveProvider() == null) {
            return null;
        }
        return getActiveProvider().getFunctionBarMenuFunctionIDList();
    }

    public List<Integer> getToolBarMenuFunctionIDList() {
        if (getActiveProvider() == null) {
            return null;
        }
        return getActiveProvider().getToolBarMenuFunctionIDList();
    }

    public void handleSubMenuFunction(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID) {
        if (getActiveProvider() != null) {
            getActiveProvider().handleSubMenuFunction(subMenuID);
        } else {
            Log.e(TAG, "Empty activity provider.");
        }
    }

    public void handleToolBarMenuFunction(@ScribbleToolBarMenuID.ScribbleToolBarMenuDef int toolBarMenuID) {
        if (getActiveProvider() != null) {
            getActiveProvider().handleToolBarMenuFunction(toolBarMenuID);
        } else {
            Log.e(TAG, "Empty activity provider.");
        }
    }

    public void saveDocument(boolean closeAfterSave, BaseCallback callback){
        if (getActiveProvider() != null) {
            getActiveProvider().saveDocument(closeAfterSave, callback);
        } else {
            Log.e(TAG, "Empty activity provider.");
        }
    }

    public void prevPage() {
        if (getActiveProvider() != null) {
            getActiveProvider().prevPage();
        } else {
            Log.e(TAG, "Empty activity provider.");
        }
    }

    public void nextPage() {
        if (getActiveProvider() != null) {
            getActiveProvider().nextPage();
        } else {
            Log.e(TAG, "Empty activity provider.");
        }
    }

    public void addPage() {
        if (getActiveProvider() != null) {
            getActiveProvider().addPage();
        } else {
            Log.e(TAG, "Empty activity provider.");
        }
    }

    public void deletePage() {
        if (getActiveProvider() != null) {
            getActiveProvider().deletePage();
        } else {
            Log.e(TAG, "Empty activity provider.");
        }
    }
}
