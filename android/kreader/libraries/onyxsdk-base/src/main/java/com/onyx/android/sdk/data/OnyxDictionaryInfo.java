/**
 * 
 */
package com.onyx.android.sdk.data;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * @author joy
 *
 */
public class OnyxDictionaryInfo
{
    private static final OnyxDictionaryInfo PREDEFINED_DICTS[] = {
        new OnyxDictionaryInfo("NeoDict", "Neo Dictionary", "com.onyx.dict", "com.onyx.dict.activity.DictMainActivity", Intent.ACTION_SEARCH, 0),
        new OnyxDictionaryInfo("OnyxDict", "Onyx Dictionary", "com.onyx.android.dict", "com.onyx.android.dict.ui.DictActivity", Intent.ACTION_SEARCH, 0),
        new OnyxDictionaryInfo("QuickDic", "QuickDic Dictionary", "com.hughes.android.dictionary", "com.hughes.android.dictionary.DictionaryManagerActivity", Intent.ACTION_SEARCH, 0),
        new OnyxDictionaryInfo("ColorDict", "ColorDict", "com.socialnmobile.colordict", "com.socialnmobile.colordict.activity.Main", Intent.ACTION_SEARCH, 0),
        new OnyxDictionaryInfo("Fora", "Fora Dictionary", "com.ngc.fora", "com.ngc.fora.ForaDictionary", Intent.ACTION_SEARCH, 0),
        new OnyxDictionaryInfo("FreeDictionary.org", "Free Dictionary.org","org.freedictionary", "org.freedictionary.MainActivity", Intent.ACTION_VIEW, 0),
        new OnyxDictionaryInfo("Lingvo", "Lingvo", "com.abbyy.mobile.lingvo.market", "com.abbyy.mobile.lingvo.wordlist.WordListActivity", Intent.ACTION_SEARCH, 0),
    };
    
    public final String id;
    public final String name;
    public final String packageName;
    public final String className;
    public final String action;
    public final Integer internal;
    public String dataKey = SearchManager.QUERY;
    
    private OnyxDictionaryInfo(String id, String name, String packageName, String className, String action, Integer internal ) 
    {
        this.id = id;
        this.name = name;
        this.packageName = packageName;
        this.className = className;
        this.action = action;
        this.internal = internal;
    }
    
    public static OnyxDictionaryInfo[] getDictionaryList()
    {
        return PREDEFINED_DICTS;
    }
    
    /**
     * return null if not found
     * 
     * @param dictId
     * @return
     */
    public static OnyxDictionaryInfo findDict(String dictId)
    {
        for (OnyxDictionaryInfo di : PREDEFINED_DICTS) {
            if (di.id.equalsIgnoreCase(dictId)) {
                return di;
            }
        }
        
        return null;
    }
    
    public static OnyxDictionaryInfo getDefaultDictionary()
    {
        assert(PREDEFINED_DICTS.length > 0);
        return PREDEFINED_DICTS[0];
    }
    
    public static boolean isDictionaryAvailable(Context context, OnyxDictionaryInfo dict)
    {
        try {
            ActivityInfo app_info = context.getPackageManager().getActivityInfo(
                            new ComponentName(dict.packageName, dict.className),
                            0);
            return app_info != null;
        }
        catch (NameNotFoundException e) {
            return false;
        }
    }
    
    public static boolean isDictionaryAvailable(Context context, String dictId)
    {
        OnyxDictionaryInfo dict = findDict(dictId);
        if (dict == null) {
            return false;
        }
        
        return isDictionaryAvailable(context, dict);
    }
}
