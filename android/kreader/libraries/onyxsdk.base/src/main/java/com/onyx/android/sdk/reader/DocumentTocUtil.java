package com.onyx.android.sdk.reader;

import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.JSONWriter;
import com.onyx.android.sdk.data.util.FileUtil;
import com.onyx.android.sdk.data.util.MemoryFileUtil;
import com.onyx.android.sdk.ui.data.DirectoryItem;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by joy on 7/10/14.
 */
public class DocumentTocUtil {
    private static final String TAG = DocumentTocUtil.class.getSimpleName();

    private static final String TAG_TITLE = "title";
    private static final String TAG_PAGE = "page";

    public static ParcelFileDescriptor saveTocList(Collection<DirectoryItem> tocList) {
        JSONWriter writer = null;
        MemoryFile mf = null;
        try {
            JSONArray jsonArray = new JSONArray();
            for (DirectoryItem item : tocList) {
                org.json.JSONObject obj = new org.json.JSONObject();
                obj.put(TAG_TITLE, item.getTitle());
                obj.put(TAG_PAGE, item.getPage());
                jsonArray.put(obj);
            }
            String str = jsonArray.toString();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            OutputStreamWriter sw = new OutputStreamWriter(bos);
            sw.write(str);
            sw.flush();

            mf = new MemoryFile(null, 4 + bos.size());
            mf.allowPurging(false);
            OutputStream os = null;
            try {
                os = mf.getOutputStream();
                ByteBuffer buf = ByteBuffer.allocate(4);
                buf.putInt(bos.size());
                os.write(buf.array());
                bos.writeTo(os);
            }
            finally {
                if (os != null) {
                    os.close();
                }
            }

            ParcelFileDescriptor fd = MemoryFileUtil.getParcelFileDescriptor(mf);
            if (fd == null) {
                mf.close();
                return null;
            }
            return fd;
        } catch (Throwable tr) {
            if (mf != null) {
                mf.close();
            }
            tr.printStackTrace();
            return null;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static ArrayList<DirectoryItem> readTocList(ParcelFileDescriptor fd) {
        JSONReader reader = null;
        try {
            FileInputStream fis = new FileInputStream(fd.getFileDescriptor());
            byte[] head = new byte[4];
            fis.read(head);
            ByteBuffer buf = ByteBuffer.wrap(head);
            int length = buf.getInt();

            byte[] data = new byte[length];
            fis.read(data);

            String str = new String(data);
            JSONArray jsonArray = new JSONArray(str);
            ArrayList<DirectoryItem> tocList = new ArrayList<DirectoryItem>();
            for (int i = 0; i < jsonArray.length(); i++) {
                org.json.JSONObject obj = jsonArray.getJSONObject(i);
                String title = obj.getString(TAG_TITLE);
                String page = obj.getString(TAG_PAGE);
                tocList.add(new DirectoryItem(title, page, null));
            }
            return tocList;
        } catch (Throwable tr) {
            tr.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
