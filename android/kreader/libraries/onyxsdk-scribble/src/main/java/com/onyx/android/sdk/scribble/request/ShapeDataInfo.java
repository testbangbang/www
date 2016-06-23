package com.onyx.android.sdk.scribble.request;

import android.content.Context;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.data.NotePage;
import com.onyx.android.sdk.scribble.data.ShapeDataProvider;
import com.onyx.android.sdk.scribble.data.ShapeModel;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuzeng on 6/7/16.
 */
public class ShapeDataInfo {

    private Map<String, NotePage> shapePageMap = new HashMap<String, NotePage>();
    public boolean canUndoShape;
    public boolean canRedoShape;

    public final Map<String, NotePage> getShapePageMap() {
        return shapePageMap;
    }

    public final NotePage getShapePage(final String pageName) {
        return shapePageMap.get(pageName);
    }

    public boolean hasShapes() {
        return (shapePageMap.size() > 0);
    }

    public boolean hasShapes(final String pageName) {
        return shapePageMap.containsKey(pageName) && shapePageMap.get(pageName).hasShapes();
    }

    public boolean loadUserShape(final Context context, final String docUniqueId, final List<PageInfo> visiblePages) {
        for(PageInfo pageInfo: visiblePages) {
            final NotePage notePage = NotePage.createPage(context, docUniqueId, pageInfo.getName(), null);
            final List<ShapeModel> modelList = ShapeDataProvider.loadShapeList(context, docUniqueId, pageInfo.getName(), null);
            for(ShapeModel model : modelList) {
                notePage.addShapeFromModel(ShapeFactory.shapeFromModel(model));
            }
            shapePageMap.put(pageInfo.getName(), notePage);
        }
        return true;
    }

}
