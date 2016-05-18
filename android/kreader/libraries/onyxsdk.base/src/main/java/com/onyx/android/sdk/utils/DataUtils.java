package com.onyx.android.sdk.utils;

import java.io.ByteArrayOutputStream;
import java.util.Random;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Created by zhuzeng on 12/9/15.
 */
public class DataUtils {
    static public final String UTF8_TAG = "utf-8";

    public static byte[] compress(final byte[] data) {
        ByteArrayOutputStream baos = null;
        Deflater dfl = new Deflater();
        dfl.setLevel(Deflater.BEST_COMPRESSION);
        dfl.setInput(data);
        dfl.finish();
        baos = new ByteArrayOutputStream();
        byte[] tmp = new byte[4*1024];
        try{
            while(!dfl.finished()){
                int size = dfl.deflate(tmp);
                baos.write(tmp, 0, size);
            }
        } catch (Exception ex){

        } finally {
            FileUtils.closeQuietly(baos);
        }
        return baos.toByteArray();
    }

    public static byte[] decompress(final byte[] data) {
        ByteArrayOutputStream baos = null;
        Inflater iflr = new Inflater();
        iflr.setInput(data);
        baos = new ByteArrayOutputStream();
        byte[] tmp = new byte[4*1024];
        try{
            while(!iflr.finished()){
                int size = iflr.inflate(tmp);
                baos.write(tmp, 0, size);
            }
        } catch (Exception ex){

        } finally {
            FileUtils.closeQuietly(baos);
        }
        return baos.toByteArray();
    }

    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
}
