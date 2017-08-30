package com.onyx.edu.note;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.data.ScribbleMode;
import com.onyx.edu.note.data.ScribbleFunctionBarMenuID;
import com.onyx.edu.note.data.ScribbleSubMenuID;
import com.onyx.edu.note.data.ScribbleToolBarMenuID;
import com.onyx.edu.note.handler.BaseHandler;
import com.onyx.edu.note.handler.PicEditHandler;
import com.onyx.edu.note.handler.ScribbleHandler;
import com.onyx.edu.note.handler.ShapeTransformHandler;
import com.onyx.edu.note.handler.SpanTextHandler;
import com.onyx.edu.note.scribble.ScribbleViewModel;

import java.util.HashMap;
import java.util.Map;

public class HandlerManager {

    public static final String TAG = HandlerManager.class.getSimpleName();
    public static final String SCRIBBLE_PROVIDER = "scribble";
    public static final String SPAN_TEXT_PROVIDER = "span_text";
    public static final String SHAPE_TRANSFORM_PROVIDER = "shape_transform";
    public static final String PIC_EDIT_PROVIDER = "pic_edit";

    private String activeProviderName;
    private Map<String, BaseHandler> providerMap = new HashMap<>();
    private ScribbleViewModel mViewModel;

    public HandlerManager(ScribbleViewModel viewModel) {
        mViewModel = viewModel;
        initProviderMap();
    }

    private void initProviderMap() {
        NoteManager manager = NoteApplication.getInstance().getNoteManager();
        providerMap.put(SCRIBBLE_PROVIDER, new ScribbleHandler(manager));
        providerMap.put(SPAN_TEXT_PROVIDER, new SpanTextHandler(manager));
        providerMap.put(SHAPE_TRANSFORM_PROVIDER, new ShapeTransformHandler(manager));
        providerMap.put(PIC_EDIT_PROVIDER, new PicEditHandler(manager));
    }

    public void resetToDefaultProvider() {
        setActiveProvider(SCRIBBLE_PROVIDER);
    }

    public void setActiveProvider(String providerName, Uri... bgUri) {
        if (!TextUtils.isEmpty(activeProviderName) && providerMap.get(activeProviderName) != null) {
            providerMap.get(activeProviderName).onDeactivate();
        }
        activeProviderName = providerName;
        if (activeProviderName.equalsIgnoreCase(PIC_EDIT_PROVIDER)) {
            ((PicEditHandler) providerMap.get(activeProviderName)).setBgUri(bgUri[0]);
        }
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

    public void changeScribbleMode(@ScribbleMode.ScribbleModeDef int targetMode , Uri... bgUri) {
        String targetProviderName = null;
        switch (targetMode) {
            case ScribbleMode.MODE_NORMAL_SCRIBBLE:
                targetProviderName = SCRIBBLE_PROVIDER;
                break;
            case ScribbleMode.MODE_SPAN_SCRIBBLE:
                targetProviderName = SPAN_TEXT_PROVIDER;
                break;
            case ScribbleMode.MODE_SHAPE_TRANSFORM:
                targetProviderName = SHAPE_TRANSFORM_PROVIDER;
                break;
            case ScribbleMode.MODE_PIC_EDIT:
                targetProviderName = PIC_EDIT_PROVIDER;
                break;
        }
        setActiveProvider(targetProviderName, bgUri);
    }

    public void handleSubMenuFunction(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID) {
        getActiveProvider().handleSubMenuFunction(subMenuID);
    }

    public void handleToolBarMenuFunction(@ScribbleToolBarMenuID.ScribbleToolBarMenuDef int toolBarMenuID) {
        getActiveProvider().handleToolBarMenuFunction(mViewModel.getCurrentDocumentUniqueID(), mViewModel.mNoteTitle.get(),
                toolBarMenuID);
    }

    public void handleFunctionBarMenuFunction(@ScribbleFunctionBarMenuID.ScribbleFunctionBarMenuDef int functionBarMenuID) {
        getActiveProvider().handleFunctionBarMenuFunction(functionBarMenuID);
    }

    public void saveDocument(boolean closeAfterSave, BaseCallback callback) {
        getActiveProvider().saveDocument(mViewModel.getCurrentDocumentUniqueID(), mViewModel.mNoteTitle.get(),
                closeAfterSave, callback);
    }

    public void quit(){
        getActiveProvider().onDeactivate();
    }
}
