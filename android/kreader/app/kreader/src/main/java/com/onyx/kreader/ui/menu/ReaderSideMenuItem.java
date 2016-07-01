package com.onyx.kreader.ui.menu;

import android.content.Context;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.data.ReaderMenuItem;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joy on 2016/4/19.
 */
public class ReaderSideMenuItem extends ReaderMenuItem {
    private String title;
    private int drawableResourceId;

    public ReaderSideMenuItem(ItemType itemType, URI uri, ReaderSideMenuItem parent, String title, int drawableResourceId) {
        super(itemType, uri, parent);
        this.title = title;
        this.drawableResourceId = drawableResourceId;
    }

    public static List<ReaderSideMenuItem> createFromJSON(Context context, JSONArray array) {
        return createFromJSON(context, null, array);
    }

    private static List<ReaderSideMenuItem> createFromJSON(Context context, ReaderSideMenuItem parent, JSONArray array) {
        List<ReaderSideMenuItem> items = new ArrayList<>();
        for (Object object : array) {
            try {
                ReaderSideMenuItem item = createFromJSON(context, parent, (JSONObject)object);
                items.add(item);
                if (parent != null) {
                    ((List<ReaderSideMenuItem>)parent.getChildren()).add(item);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return items;
    }

    private static ReaderSideMenuItem createFromJSON(Context context, ReaderSideMenuItem parent, JSONObject json) throws URISyntaxException {
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

        int resId = context.getResources().getIdentifier(getNameOfResource(iconResourceId), "drawable", context.getPackageName());

        ItemType itemType = Enum.valueOf(ItemType.class, type);
        ReaderSideMenuItem item = new ReaderSideMenuItem(itemType, uri, parent, title, resId);
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
