/**
 * 
 */
package com.onyx.android.sdk.data.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import android.content.Context;
import android.util.Log;

import com.onyx.android.sdk.jni.NativeFileUtil;
import com.onyx.android.sdk.R;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * @author joy
 *
 */
public class FileUtil
{
    private final static String TAG = "FileUtil";
    
    /**
     * return file extension without heading dot
     * The substring will automatically check range.
     * @param fileName
     * @return
     */
    public static String getFileExtension(String fileName) {
        if (StringUtils.isNullOrEmpty(fileName)) {
            return "";
        }
        int dotPosition = fileName.lastIndexOf('.');
        if (dotPosition >= 0) {
            return fileName.substring(dotPosition + 1).toLowerCase(Locale.getDefault());
        }
        
        return "";
    }
    public static String getFileExtension(File file)
    {
        return getFileExtension(file.getName());
    }
    
    public static String getFileNameWithoutExtension(String fileName)
    {
        int dot_pos = fileName.lastIndexOf('.');
        if (dot_pos < 0) {
            return fileName;
        }
        
        return fileName.substring(0, dot_pos);
    }

    public static String appendName(final String path, final String append) {
        int pos = path.lastIndexOf('.');
        if (0  > pos) {
            return path + append;
        }
        String result = path.substring(0, pos);
        result += append;
        result += path.substring(pos);
        return result;
    }
    
    public static String getFileNameByPath(String filePath) {
    	int dot_pos = filePath.lastIndexOf(File.separator);
        if (dot_pos < 0) {
            return filePath;
        }
        
        return filePath.substring(dot_pos + 1, filePath.length());
    }
    
    public static String getFilePathFromUri(String uri)
    {
        final String PREFIX = "file://";
        return uri.substring(PREFIX.length());
    }
    
    /**
     * File.lastModified() can only get last modified time, this method will also check the creation time,
     * and return the larger one of these two
     * 
     * @param file
     * @param result
     * @return
     */
    public static long getLastChangeTime(File file)
    {
        long ct = 0;
        long mt = file.lastModified();
        
        try {
            ct = NativeFileUtil.getChangeTimestamp(file.getAbsolutePath()) * 1000;
            if (ct == -1) {
                return mt;
            }
            
            return Math.max(ct, mt);
        }
        catch (Throwable tr) {
            Log.e(TAG, "exception", tr);
        }
        
        return mt;
    }

    public static String computeMD5(byte[] buf) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(buf);
            byte[] out = md.digest();

            final char hex_digits[] = {
                    '0', '1', '2', '3',
                    '4', '5', '6', '7',
                    '8', '9', 'a', 'b',
                    'c', 'd', 'e', 'f'};

            char str[] = new char[out.length * 2];
            for (int i = 0; i < out.length; i++) {
                int j = i << 1;
                str[j] = hex_digits[(out[i] >> 4) & 0x0F];
                str[j + 1] = hex_digits[out[i] & 0x0F];
            }

            return String.valueOf(str);
        } catch (Throwable tr) {
            Log.e(TAG, "", tr);
        }

        final String EMPTY_MD5 = "d41d8cd98f00b204e9800998ecf8427e";
        return EMPTY_MD5;
    }
    
    public static String computeMD5(File file) throws IOException, NoSuchAlgorithmException
    {
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        
        if (!file.isFile()) {
            throw new IllegalArgumentException();
        }
        
        byte[] digest_buffer = getDigestBuffer(file);
        
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(digest_buffer);
        byte[] out = md.digest();
        
        final char hex_digits[] = {
                '0', '1', '2', '3', 
                '4', '5', '6', '7',
                '8', '9', 'a', 'b', 
                'c', 'd', 'e', 'f' }; 
        
        char str[] = new char[out.length * 2];
        for (int i = 0; i < out.length; i++) {
            int j = i << 1;
            str[j] = hex_digits[(out[i] >> 4) & 0x0F];
            str[j + 1] = hex_digits[out[i] & 0x0F];
        }

        return String.valueOf(str);
    }

    public static String computeFileContentMD5(File file) throws IOException, NoSuchAlgorithmException
    {
        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        if (!file.isFile()) {
            throw new IllegalArgumentException();
        }

        byte[] digest_buffer = getDigestBuffer(file);

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(digest_buffer);
        byte[] out = md.digest();

        final char hex_digits[] = {
                '0', '1', '2', '3',
                '4', '5', '6', '7',
                '8', '9', 'a', 'b',
                'c', 'd', 'e', 'f' };

        char str[] = new char[out.length * 2];
        for (int i = 0; i < out.length; i++) {
            int j = i << 1;
            str[j] = hex_digits[(out[i] >> 4) & 0x0F];
            str[j + 1] = hex_digits[out[i] & 0x0F];
        }

        return String.valueOf(str);
    }

    public static String computeMD5Checksum(File file) throws IOException, NoSuchAlgorithmException {
        InputStream fis =  new FileInputStream(file);
        try {
            byte[] buffer = new byte[64 * 1024];
            MessageDigest md = MessageDigest.getInstance("MD5");
            int numRead;
            do {
                numRead = fis.read(buffer);
                if (numRead > 0) {
                    md.update(buffer, 0, numRead);
                }
            } while (numRead != -1);

            return hexToString(md.digest());
        } finally {
            FileUtils.closeQuietly(fis);
        }
    }

    public static String hexToString(byte[] out) {
        final char hex_digits[] = {
                '0', '1', '2', '3',
                '4', '5', '6', '7',
                '8', '9', 'a', 'b',
                'c', 'd', 'e', 'f' };

        char str[] = new char[out.length * 2];
        for (int i = 0; i < out.length; i++) {
            int j = i << 1;
            str[j] = hex_digits[(out[i] >> 4) & 0x0F];
            str[j + 1] = hex_digits[out[i] & 0x0F];
        }
        return String.valueOf(str);
    }
    
    /**
     * never return null
     * 
     * @param file
     * @return
     * @throws IOException
     */
    private static byte[] getDigestBuffer(File file) throws IOException
    {
        final int digest_block_length = 512;

        byte[] digest_buffer = null;
        
        RandomAccessFile rf = null;
        
        try {
        rf = new RandomAccessFile(file, "r");
        
        long file_size = rf.length();
        
        // TODO: what about an empty file?
        if (file_size <= (digest_block_length * 3)) { 
            digest_buffer = new byte[(int)file_size];
            rf.read(digest_buffer);
        } 
        else {
            // 3 digest blocks, head, mid, end
            digest_buffer = new byte[3 * digest_block_length];
            rf.seek(0);
            rf.read(digest_buffer, 0, digest_block_length); 
            rf.seek((file_size / 2) - (digest_block_length / 2));
            rf.read(digest_buffer, digest_block_length, digest_block_length);
            rf.seek(file_size - digest_block_length);
            rf.read(digest_buffer, 2 * digest_block_length, digest_block_length);
        }
        }
        finally {
            if (rf != null) {
                rf.close();
            }
        }
        
        assert(digest_buffer != null);
        return digest_buffer;
    }
    
    public static void deleteDirectory(String folderPath) {
    	try {
    	     delAllFile(folderPath);
    	     File myFilePath = new File(folderPath);
    	     myFilePath.delete();
    	} catch (Exception e) {
    	     e.printStackTrace(); 
    	}
        }
    	
        public static boolean delAllFile(String path) {
            boolean flag = false;
            File file = new File(path);
            if (!file.exists()) {
                return flag;
            }
            if (!file.isDirectory()) {
                return flag;
            }
            String[] tempList = file.list();
            File temp = null;
            for (int i = 0; i < tempList.length; i++) {
                if (path.endsWith(File.separator)) {
                    temp = new File(path + tempList[i]);
                } else {
                    temp = new File(path + File.separator + tempList[i]);
                }
                if (temp.isFile()) {
                    temp.delete();
                }
                if (temp.isDirectory()) {
                    delAllFile(path + File.separator + tempList[i]);
                    deleteDirectory(path + File.separator + tempList[i]);
                    flag = true;
                }
            }
            return flag;
        }

    /**
     * Because file.renameTo Cannot use while crossing 2 different sd.For example Internal Flash to Removable SD.
     * So use the method to judge if source and target files were on same sd.
     * This method may only use in storage,so we assume file is only on internal flash or removable sd.
     * @param a
     * @param b
     * @return is on same SDCard or not.
     */
    public static boolean onSameSDCard(File a, File b) {
        if (a.getAbsolutePath().contains(EnvironmentUtil.getExternalStorageDirectory().getAbsolutePath())) {
            if (b.getAbsolutePath().contains(EnvironmentUtil.getExternalStorageDirectory().getAbsolutePath())) {
                return true;
            }
        } else {
            if (!b.getAbsolutePath().contains(EnvironmentUtil.getExternalStorageDirectory().getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }
}
