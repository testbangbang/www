package com.onyx.android.sdk.reader.plugins.netnovel;

import android.content.Context;
import android.os.Build;

import com.alibaba.fastjson.JSONObject;
import com.neverland.engbook.forpublic.AlFileDecrypt;
import com.neverland.engbook.forpublic.TAL_RESULT;
import com.neverland.engbook.level1.AlFilesBypassNative;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by joy on 3/20/18.
 */

public class AlFileDecryptNetNovel extends AlFileDecrypt {
    private Context context;
    private String path;

    private byte[] novelContent;

    private int size;
    private int pos;

    public AlFileDecryptNetNovel(Context context, String path) {
        this.context = context;
        this.path = path;
    }

    public final int getSize() {
        return size;
    }

    public final int open(String fileName, int mode) {
        String json = FileUtils.readContentOfFile(path);
        if (StringUtils.isNullOrEmpty(json)) {
            return TAL_RESULT.ERROR;
        }

        JSONObject jsonObject = JSONObject.parseObject(json);
        if (!jsonObject.containsKey("content") || !jsonObject.containsKey("pin") ||
                !jsonObject.containsKey("time")) {
            return TAL_RESULT.ERROR;
        }

        String content = jsonObject.getString("content");
        String pin = jsonObject.getString("pin");
        String time = jsonObject.getString("time");

        String part = FileUtils.computeMD5(pin + time + Build.SERIAL);
        EncryptHelper.setNetBookDecryptKeyPath(context, EncryptHelper.getNetBookDecryptKey(part));
        content = EncryptHelper.getDecryptContent(content);
        if (content == null) {
            content = "";
        }
        novelContent = content.getBytes();
        size = novelContent.length;

        return TAL_RESULT.OK;
    }

    public final int seek(int newPos) {
        if (pos >= 0 && pos < size)
            pos = newPos;
        return pos;
    }

    public void close() {
    }

    public int read(byte[] dst, int start, int cnt) {
        int res = 0;

        try {
            for (int i = 0; i < cnt && pos + i < size; i++) {
                dst[start + res] = (byte)novelContent[pos + i];
                res++;
            }
        } catch (Exception e) {
            res = -1;
        }

        return res;
    }

    public String getOutFileExt() {
        return ".гтлтщцт";
    }

    public String getFileMD5() {
        return AlFilesBypassNative.getMd5(path.getBytes());
    }
}
