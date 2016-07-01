package com.onyx.android.sdk.ui.data;

import android.content.Context;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.data.ReaderMenuItem;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joy on 6/28/16.
 */
public class ReaderLayerMenuItem extends ReaderMenuItem {
    private String title;
    private int drawableResourceId;

    public ReaderLayerMenuItem(ItemType itemType, URI uri, ReaderLayerMenuItem parent, String title, int drawableResourceId) {
        super(itemType, uri, parent);
        this.title = title;
        this.drawableResourceId = drawableResourceId;
    }

    public static List<ReaderLayerMenuItem> createFromJSON(Context context, JSONArray array) {
        return createFromJSON(context, null, array);
    }

    private static List<ReaderLayerMenuItem> createFromJSON(Context context, ReaderLayerMenuItem parent, JSONArray array) {
        List<ReaderLayerMenuItem> items = new ArrayList<>();
        for (Object object : array) {
            try {
                ReaderLayerMenuItem item = createFromJSON(context, parent, (JSONObject)object);
                items.add(item);
                if (parent != null) {
                    ((List<ReaderLayerMenuItem>)parent.getChildren()).add(item);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return items;
    }

    private static ReaderLayerMenuItem createFromJSON(Context context, ReaderLayerMenuItem parent, JSONObject json) throws URISyntaxException {
        String id = json.getString("id");
        String type = json.getString("type");
        String title = json.getString("title");
        String iconResourceId = json.getString("iconResourceId");
        URI uri;
        if (parent == null) {
            uri = new URI("/" + id);
        } else {
            uri = new URI(parent.getURI().getRawPath() + "/" + id);
        }

        int resId = iconResourceId == null ? -1 : context.getResources().getIdentifier(getNameOfResource(iconResourceId), "drawable", context.getPackageName());

        ItemType itemType = Enum.valueOf(ItemType.class, type);
        ReaderLayerMenuItem item = new ReaderLayerMenuItem(itemType, uri, parent, title, resId);
        if (itemType == ItemType.Group) {
            JSONArray array = json.getJSONArray("children");
            if (array != null) {
                createFromJSON(context, item, array);
            }
        }
        return item;
    }

    /**
     * get resource name of resource id in the form "R.drawable.xxx"
     * @param resourceId
     * @return
     */
    private static String getNameOfResource(String resourceId) {
        int idx = resourceId.lastIndexOf('.');
        return resourceId.substring(idx + 1);
    }

    public String getTitle() {
        return title;
    }

    public int getDrawableResourceId() {
        return drawableResourceId;
    }

}
