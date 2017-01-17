/**
 * 
 */
package com.onyx.android.sdk.reader.utils;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @author dxw
 *
 */
public class RawResourceUtil
{
   
    public static String contentOfRawResource(Context context, int rawResourceId) {
        BufferedReader breader = null;
        InputStream is = null;
        try {
             is = context.getResources().openRawResource(rawResourceId);
             breader = new BufferedReader(new InputStreamReader(is));
             StringBuilder total = new StringBuilder();
             String line = null;
             while ((line = breader.readLine()) != null) {
                 total.append(line);
             }
             return total.toString();
        }
        catch (Exception e) {
            //e.printStackTrace();
        }
         finally {
            closeQuietly(breader);
            closeQuietly(is);
        }
        return null;
    }
    
    public static Map<String, List<Integer>> integerMapFromRawResource(Context context, int rawResourceId) {
        String content = contentOfRawResource(context, rawResourceId);
        return JSON.parseObject(content, new TypeReference<Map<String, List<Integer>>>(){});
    }

    public static GObject objectFromRawResource(Context context, int rawResourceId) {
        String content = contentOfRawResource(context, rawResourceId);
        try {
            Map<String, Object> map = JSON.parseObject(content);
            if (map == null) {
                return null;
            }
            GObject object = new GObject();
            for(Map.Entry<String, Object> entry : map.entrySet()) {
                object.putObject(entry.getKey(), entry.getValue());
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    static public void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null)
                closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
