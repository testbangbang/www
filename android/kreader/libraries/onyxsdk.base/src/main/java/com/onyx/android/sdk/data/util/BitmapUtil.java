/**
 * 
 */
package com.onyx.android.sdk.data.util;

import java.io.*;
import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.onyx.android.sdk.data.GAdapter;

/**
 * @author joy
 *
 */
public class BitmapUtil
{
    private static final String TAG = "BitmapUtil";
    
    public static byte[] compressToByteArray(Bitmap bmp, Bitmap.CompressFormat format)
    {
        Log.d(TAG, "compressToByteArray: " + format);

        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ProfileUtil.start(TAG, "compress bitmap");
            bmp.compress(format, 100, os);
            return os.toByteArray();
        }
        finally {
            try {
                os.close();
            }
            catch (IOException e) {
                Log.e(TAG, "exception", e);
            }
            ProfileUtil.end(TAG, "compress bitmap");
        }
    }
    
    /**
     * never return null
     * 
     * @param bmp
     * @return
     */
    public static int[] getPixels(Bitmap bmp)
    {
        try {
            ProfileUtil.start(TAG, "getPixels");
            final int width = bmp.getWidth();
            final int height = bmp.getHeight();

            int buf[] = new int[width * height];
            bmp.getPixels(buf, 0, width, 0, 0, width, height);  

            return buf;
        }
        finally {
            ProfileUtil.end(TAG, "getPixels");
        }
    }
    
    /**
     * never return null
     * 
     * @param bmp
     * @return
     */
    public static byte[] getPixelsInByte(Bitmap bmp)
    {
        try {
            ProfileUtil.start(TAG, "getPixelsInByte");
            final int width = bmp.getWidth();
            final int height = bmp.getHeight();
            ByteBuffer byte_buf = ByteBuffer.allocate(width * height * 4);
            bmp.copyPixelsToBuffer(byte_buf);

            return byte_buf.array();
        }
        finally {
            ProfileUtil.end(TAG, "getPixelsInByte");
        }
    }
    
    public static Bitmap createBitmap(byte[] pixelsInByte, int width, int height)
    {
        Bitmap bmp = Bitmap.createBitmap(width, height, Config.RGB_565);
        ByteBuffer byte_buf = ByteBuffer.wrap(pixelsInByte);
        bmp.copyPixelsFromBuffer(byte_buf);

        return bmp;
    }
    
    public static Bitmap convert(Bitmap bitmap, Bitmap.Config config)
    {
        try {
            ProfileUtil.start(TAG, "convert");
            Bitmap converted = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);

            Canvas canvas = new Canvas(converted);
            Paint paint = new Paint();
            canvas.drawBitmap(bitmap, 0, 0, paint);

            return converted;
        }
        finally {
            ProfileUtil.start(TAG, "convert");
        }
    }
    
    public static ParcelFileDescriptor compressMemoryFile(Bitmap bmp)
    {
        MemoryFile mf = null;
        try {
            byte[] pixels = BitmapUtil.compressToByteArray(bmp, Bitmap.CompressFormat.JPEG);

            mf = new MemoryFile(null, pixels.length);
            mf.allowPurging(false);

            ProfileUtil.start(TAG, "write MemoryFile");
            OutputStream os = null;
            try {
                os = mf.getOutputStream();
                os.write(pixels);
            }
            finally {
                ProfileUtil.end(TAG, "write MemoryFile");
                if (os != null) {
                    os.close();
                }
            }
        } catch (Throwable e) {
            Log.w(TAG, e);
            if (mf != null) {
                mf.close();
            }
            return null;
        }
        
        ParcelFileDescriptor fd = MemoryFileUtil.getParcelFileDescriptor(mf);
        if (fd == null) {
            mf.close();
            return null;
        }
        
        return fd;
    }
    
    public static Bitmap decompressMemoryFile(ParcelFileDescriptor fd)
    {
        try {
            ProfileUtil.start(TAG, "decompressMemoryFile");
            return BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor());
        }
        finally {
            ProfileUtil.end(TAG, "decompressMemoryFile");
        }
    }
    
    public static ParcelFileDescriptor createLosslessMemoryFile(Bitmap bmp)
    {
        MemoryFile mf = null;
        try {
            byte[] pixels = BitmapUtil.getPixelsInByte(bmp);
            
            ByteBuffer buf = ByteBuffer.allocate(8 + pixels.length);
            buf.putInt(bmp.getWidth());
            buf.putInt(bmp.getHeight());
            buf.put(pixels);

            mf = new MemoryFile(null, buf.capacity());
            mf.allowPurging(false);

            ProfileUtil.start(TAG, "write MemoryFile");
            OutputStream os = null;
            try {
                os = mf.getOutputStream();
                os.write(buf.array());
            }
            finally {
                ProfileUtil.end(TAG, "write MemoryFile");
                if (os != null) {
                    os.close();
                }
            }
        } catch (Throwable e) {
            Log.w(TAG, e);
            if (mf != null) {
                mf.close();
            }
            return null;
        }

        
        ParcelFileDescriptor fd = MemoryFileUtil.getParcelFileDescriptor(mf);
        if (fd == null) {
            mf.close();
            return null;
        }
        
        return fd;
    }
    
    public static Bitmap readLosslessMemoryFile(ParcelFileDescriptor fd)
    {
        FileInputStream fis = null;
        try {
            ProfileUtil.start(TAG, "readLosslessMemoryFile");
            fis = new FileInputStream(fd.getFileDescriptor());
            
            byte[] head = new byte[8];
            int n = fis.read(head);
            Log.d(TAG, "read head: " + n);
            assert(n == 8);

            ByteBuffer buf = ByteBuffer.wrap(head);
            int width = buf.getInt(0);
            int height = buf.getInt(4);
            Log.d(TAG, "width: " + width + ", height: " + height);
            
            byte [] data = new byte [width * height * 4];
            n = fis.read(data);
            Log.d(TAG, "data length: " + n);
            
            return BitmapUtil.createBitmap(data, width, height);
        } catch (IOException e){
            Log.e(TAG, "Exception", e);
        }
        finally {
            ProfileUtil.end(TAG, "readLosslessMemoryFile");
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    Log.e(TAG, "Exception", e);
                }
            }
        }
        
        return null;
    }

    public static void recycleAllBitmap(int visibleBegin, int visibleEnd, int size, int sizePerPage, GAdapter adapter) {
        for(int i = 0; i < visibleBegin - sizePerPage; ++i) {
            adapter.get(i).recycleBitmap(GAdapterUtil.TAG_THUMBNAIL);
        }
        for(int i = visibleEnd + sizePerPage; i < size; ++i) {
            adapter.get(i).recycleBitmap(GAdapterUtil.TAG_THUMBNAIL);
        }
    }

    public static void recycleAllBitmap(final GAdapter adapter) {
        for(int i = 0; i < adapter.size(); ++i) {
            adapter.get(i).recycleBitmap(GAdapterUtil.TAG_THUMBNAIL);
        }
    }

    public static Bitmap loadBitmap(final String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }
}
