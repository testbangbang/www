package com.onyx.edu.note;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

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

    public void setActiveProvider(String providerName) {
        if (!TextUtils.isEmpty(activeProviderName) && providerMap.get(activeProviderName) != null) {
            providerMap.get(activeProviderName).onDeactivate();
        }
        activeProviderName = providerName;
        providerMap.get(activeProviderName).setScribbleViewModel(mViewModel);
        providerMap.get(activeProviderName).onActivate();
    }

    @NonNull
    public BaseHandler getActiveProvider() {
        if (TextUtils.isEmpty(activeProviderName)) {
            resetToDefaultProvider();
            return getActiveProvider();
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

    public void handleSubMenuFunction(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID) {
        getActiveProvider().handleSubMenuFunction(subMenuID);
    }

    public void handleToolBarMenuFunction(@ScribbleToolBarMenuID.ScribbleToolBarMenuDef int toolBarMenuID) {
        getActiveProvider().handleToolBarMenuFunction(toolBarMenuID);
    }

    public void saveDocument(boolean closeAfterSave, BaseCallback callback) {
        getActiveProvider().saveDocument(closeAfterSave, callback);
    }

    public void prevPage() {
        getActiveProvider().prevPage();
    }

    public void nextPage() {
        getActiveProvider().nextPage();
    }

    public void addPage() {
        getActiveProvider().addPage();
    }

    public void deletePage() {
        getActiveProvider().deletePage();
    }
}
