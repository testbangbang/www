package com.onyx.android.sdk.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 3/16/14
 * Time: 11:56 AM
 * Map<String, Integer> specifies relation between key and ui resource id.
 * object[key] --> layout[id]
 *
 */
public class GAdapter {
    private ArrayList<GObject> list = new ArrayList<GObject>();
    private GObject options = new GObject();
    private Map<String, Integer> mapping;


    public GAdapter() {
        super();
    }

    static public GAdapter createFromStringList(final Collection<String> list, final String tag) {
        GAdapter d = new GAdapter();
        for (String s : list) {
            GObject o = new GObject();
            o.putString(tag, s);
            d.getList().add(o);
        }
        return d;
    }

    static public GAdapter createFromGObjectList(final Collection<GObject> list) {
        GAdapter d = new GAdapter();
        d.list.addAll(list);
        return d;
    }

    public void addObject(final GObject object) {
        list.add(object);
    }

    public void addObject(int index, final GObject object) {
        list.add(index, object);
    }

    public int getGObjectIndex(GObject object){
        return list.indexOf(object);
    }

    public int getIndexByValue(Object value, final String tag) {
        if (list == null) {
            return -1;
        }
        for(int i = 0; i < list.size(); ++i) {
            GObject temp = list.get(i);
            if (temp.getObject(tag).equals(value)) {
                return i;
            }
        }
        return -1;
    }

    public void sortByKey(final String key, boolean ascending) {

    }

    /**
     * never return null
     *
     * @param tag
     * @param pattern
     * @return
     */
    public ArrayList<GObject> searchByTag(String tag, Object pattern) {
        ArrayList<GObject> result = new ArrayList<GObject>();
        for (GObject o : list) {
            if (o.matches(tag, pattern)) {
                result.add(o);
            }
        }
        return result;
    }

    /**
     * return null if not found
     *
     * @param tag
     * @param pattern
     * @return
     */
    public GObject searchFirstByTag(String tag, Object pattern) {
        ArrayList<GObject> result = searchByTag(tag, pattern);
        if (result.size() <= 0) {
            return null;
        }
        return result.get(0);
    }

    /**
     * Define the mapping from tag to resource id.
     * @param map
     */
    public void setMapping(Map<String, Integer> map) {
        mapping = map;
    }

    public final Map<String, Integer> getMapping() {
        return mapping;
    }

    public int size() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public GObject get(int index) {
        if ( list == null || index < 0 || index >= list.size()) {
            return null;
        }
        return list.get(index);
    }

    public boolean setObject(int index, final GObject newObject) {
        if (list == null || index <0 || index >= list.size()){
            return false;
        }
        list.set(index,newObject);
        return true;
    }

    public final ArrayList<GObject> getList() {
        return list;
    }

    public final GObject getOptions() {
        return options;
    }

    public boolean append(final GAdapter another) {
        if (another == null || another.list == null) {
            return false;
        }
        if (list == null) {
            list = another.getList();
            return true;
        }
        list.addAll(another.getList());
        return true;
    }

    public boolean append(int index, final GAdapter another) {
        if (another == null || another.list == null) {
            return false;
        }
        if (list == null) {
            list = another.getList();
            return true;
        }
        list.addAll(index, another.getList());
        return true;
    }

}
