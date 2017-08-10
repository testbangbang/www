package com.onyx.android.dr.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.DictTypeBean;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;

import static com.onyx.android.sdk.dict.utils.Utils.getDictionaryFileList;
import static com.onyx.android.sdk.dict.utils.Utils.loadLocalDict;

/**
 * Created by zhuzeng on 6/3/15.
 */
public class Utils {

    public static final String UTF8_TAG = "UTF-8";
    public static final String UTF16BE_TAG = "UTF-16BE";
    public static final String UTF16LE_TAG = "UTF-16LE";
    public static final String UTF16_TAG = "UTF-16";
    public static final String GBK_TAG = "GBK";
    public static final String GB2312_TAG = "GB2312";
    public static final String GB18030_TAG = "GB18030";
    public static final String WEBSIT_DIR = "/mnt/sdcard/dicts/.onyxdict/";
    public static final String HTML_FILE = "onyxdict.html";
    public static final String CSS_FILE = "onyxdict.css";
    public static final String JS_FILE = "onyxdict.js";
    public static final String MORE_PNG = "more.png";
    public static final String VERSION_FILE = "version";
    public static final String TTS_VOLUME_INCREASE_PNG = "tts_volume_increase.png";
    public static final String SAVE_KEYWORD_PNG = "save_keyword.png";
    public static final String DICT_RESOURCE_CACHE_DIR = File.separator + ".cache";

    public static final String[] SPECIAL_CHAR = new String[]{"-", " "};
    public static final String UNDERLINE_CHAR = "_";

    /**
     * load dictionary return status
     * success:
     * 0:success
     * fail:
     * 1:dictionary password == null
     * 2:dictionary password error
     * -1:dictinary file error
     */
    public static final int LOAD_DICTIONARY_SUCCESS = 0;
    public static final int LOAD_DICTIONARY_PASSWORD_NULL = 1;
    public static final int LOAD_DICTIONARY_PASSWORD_ERROR = 2;
    public static final int LOAD_DICTIONARY_FILE_ERROR = -1;

    //lemmaGen data
    public static final String LEMMAGEN_ENGLISH_BIN = "lem-m-en.bin";
    // Hex charset
    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
    private static final int BUFFER_SIZE = 1024;

    private static final int INTERCEPTION_MAX_LENGTH_100KB = 5 * 1024;//10KB
    private static final String INTERCEPTION_START_TAG = "</";//10KB after '</'
    private static final String INTERCEPTION_END_TAG = ">";
    private static final String INTERCEPTION_FLAGS_BLANK = " ";//10KB after blank

    private static final String MORE_BUTTON_START_TAG = "<br><img id=\"dict_more\" src=\"more.png\" onclick=\"showMoreExplanation";

    private static final String MORE_BUTTON_END_TAG = "\"></img>";

    static public final String endsWith(final List<String> list, final String ext) {
        for (String s : list) {
            if (s.toLowerCase().endsWith(ext)) {
                return s;
            }
        }
        return null;
    }

    public static final byte[] readContentOfFile(final String path, int offset, int size) {
        FileInputStream fin = null;
        byte[] buffer = new byte[size];
        int read = size;
        try {
            fin = new FileInputStream(path);
            fin.skip(offset);
            read = fin.read(buffer, 0, size);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            FileUtils.closeQuietly(fin);
        }
        buffer[read] = 0;
        return buffer;
    }

    public static boolean writeIntegerList(final File file, final List<Integer> list) {
        boolean ret = true;
        RandomAccessFile out = null;
        try {
            out = new RandomAccessFile(file, "rw");
            FileChannel channel = out.getChannel();
            ByteBuffer buf = channel.map(FileChannel.MapMode.READ_WRITE, 0, 4 * list.size());
            for (int i : list) {
                buf.putInt(i);
            }
            channel.close();
        } catch (Exception e) {
            e.printStackTrace();
            ret = false;
        } finally {
            FileUtils.closeQuietly(out);
        }
        return ret;
    }

    public static boolean readIntegerList(final File file, final List<Integer> list) {
        boolean ret = true;
        RandomAccessFile out = null;
        try {
            out = new RandomAccessFile(file, "r");
            FileChannel channel = out.getChannel();
            ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            while (buf.remaining() > 0) {
                list.add(buf.getInt());
            }
            channel.close();
        } catch (Exception e) {
            e.printStackTrace();
            ret = false;
        } finally {
            FileUtils.closeQuietly(out);
        }
        return ret;
    }

    /**
     * Computes the failure function using a boot-strapping process,
     * where the pattern is matched against itself.
     */
    private static int[] computeFailure(byte[] pattern) {
        int[] failure = new int[pattern.length];

        int j = 0;
        for (int i = 1; i < pattern.length; i++) {
            while (j > 0 && pattern[j] != pattern[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == pattern[i]) {
                j++;
            }
            failure[i] = j;
        }

        return failure;
    }

    /**
     * Search the data byte array for the first occurrence of the byte array pattern within given boundaries.
     *
     * @param data
     * @param start   First index in data
     * @param stop    Last index in data so that stop-start = length
     * @param pattern What is being searched. '*' can be used as wildcard for "ANY character"
     * @return
     */
    public static int indexOf(byte[] data, int start, int stop, byte[] pattern) {
        if (data == null || pattern == null) return -1;

        int[] failure = computeFailure(pattern);

        int j = 0;

        for (int i = start; i < stop; i++) {
            while (j > 0 && (pattern[j] != '*' && pattern[j] != data[i])) {
                j = failure[j - 1];
            }
            if (pattern[j] == '*' || pattern[j] == data[i]) {
                j++;
            }
            if (j == pattern.length) {
                return i - pattern.length + 1;
            }
        }
        return -1;
    }

    public static final String detectEncoding(final String path, final String fallback) throws Exception {
        FileInputStream fis = null;
        String encoding = null;
        try {
            fis = new FileInputStream(path);
            byte[] buf = new byte[4096];
            UniversalDetector detector = new UniversalDetector(null);
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();

            encoding = detector.getDetectedCharset();
            detector.reset();
        } catch (Exception e) {
            FileUtils.closeQuietly(fis);
        } finally {
            if (StringUtils.isNullOrEmpty(encoding)) {
                return fallback;
            }
            return encoding;
        }
    }

    public static String stringValue(final String string, final String splitter) {
        if (StringUtils.isNullOrEmpty(string)) {
            return null;
        }

        String items[] = string.split(splitter);
        if (items.length < 2) {
            return null;
        }
        return items[1].trim();
    }

    public static Integer integerValue(final String string, final String splitter) {
        String value = stringValue(string, splitter);
        return Integer.valueOf(value);
    }

    public static boolean compare(final String src, final String target) {
        if (src == null || target == null) {
            return false;
        }
        return (src.compareToIgnoreCase(target) == 0);
    }

    public static String toString(byte[] ba) {
        return toString(ba, 0, ba.length);
    }

    /**
     * <p>Returns a string of hexadecimal digits from a byte array, starting at
     * <code>offset</code> and consisting of <code>length</code> bytes. Each byte
     * is converted to 2 hex symbols; zero(es) included.</p>
     *
     * @param ba     the byte array to convert.
     * @param offset the index from which to start considering the bytes to
     *               convert.
     * @param length the count of bytes, starting from the designated offset to
     *               convert.
     * @return a string of hexadecimal characters (two for each byte)
     * representing the designated input byte sub-array.
     */
    public static final String toString(byte[] ba, int offset, int length) {
        char[] buf = new char[length * 2];
        for (int i = 0, j = 0, k; i < length; ) {
            k = ba[offset + i++];
            buf[j++] = HEX_DIGITS[(k >>> 4) & 0x0F];
            buf[j++] = HEX_DIGITS[k & 0x0F];
        }
        return new String(buf);
    }

    public static final String md5(final byte data[]) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] dd = digest.digest(data);
        BigInteger bigInt = new BigInteger(1, dd);
        String hashtext = bigInt.toString(16);

        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }

    public static byte[] decompress(final byte data[], int size) throws Exception {
        Inflater decompresser = new Inflater();
        decompresser.setInput(data);
        byte[] result = new byte[size];
        decompresser.inflate(result);
        return result;
    }

    public static byte[] readData(final RandomAccessFile file, long size) throws Exception {
        byte[] data = new byte[(int) size];
        file.read(data);
        return data;
    }

    public static byte[] decompress(final byte[] data, final int offset, final int compressedSize, final int decompressedSize) throws Exception {
        Inflater decompresser = new Inflater();
        decompresser.setInput(data, offset, compressedSize);
        byte[] result = new byte[decompressedSize];
        decompresser.inflate(result);
        return result;
    }

    public static byte[] readAllOfGZFile(File file) {
        final int bufsize = 8 * 1024;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPInputStream in = null;
        try {
            in = new GZIPInputStream(new FileInputStream(file));
            byte[] buf = new byte[bufsize];
            int readbytes = 0;
            readbytes = in.read(buf);
            while (readbytes != -1) {
                baos.write(buf, 0, readbytes);
                readbytes = in.read(buf);
            }
            baos.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.closeQuietly(in);
        }
        return null;
    }

    /**
     * 检查加载字典解释的HTML框架文件，如果没有需要写一份到
     * mnt/sdcard/Books/目录下
     * 如果不写到本地，字典中的图片就加载不了
     */
    public static boolean localDictWebsiteFile(Context context) {
        try {
            File file = new File(WEBSIT_DIR + VERSION_FILE);
            if (file.exists()) {
                //检查版本是否有更新
                if (checkUpdate(context)) {
                    return false;
                }
            }
            file = new File(WEBSIT_DIR);
            if (!file.exists()) {
                file.mkdirs();
            }

            AssetManager am = context.getAssets();
            readAssetsFile(am, HTML_FILE, WEBSIT_DIR + HTML_FILE);
            readAssetsFile(am, CSS_FILE, WEBSIT_DIR + CSS_FILE);
            readAssetsFile(am, JS_FILE, WEBSIT_DIR + JS_FILE);
            readAssetsFile(am, MORE_PNG, WEBSIT_DIR + MORE_PNG);
            readAssetsFile(am, TTS_VOLUME_INCREASE_PNG, WEBSIT_DIR + TTS_VOLUME_INCREASE_PNG);
            readAssetsFile(am, SAVE_KEYWORD_PNG, WEBSIT_DIR + SAVE_KEYWORD_PNG);
            readAssetsFile(am, VERSION_FILE, WEBSIT_DIR + VERSION_FILE);
            readAssetsFile(am, LEMMAGEN_ENGLISH_BIN, WEBSIT_DIR + LEMMAGEN_ENGLISH_BIN);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private static boolean checkUpdate(Context context) {

        AssetManager am = context.getAssets();
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            is = am.open(VERSION_FILE);
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String newVersion = br.readLine();

            String oldVersion = FileUtils.readContentOfFile(new File(WEBSIT_DIR + VERSION_FILE));
            if (newVersion.equals(oldVersion)) {
                return true;
            }
        } catch (Exception e) {
        } finally {
            FileUtils.closeQuietly(br);
            FileUtils.closeQuietly(isr);
            FileUtils.closeQuietly(is);
        }
        return false;
    }

    public static void readAssetsFile(AssetManager am, String srcfileName, String destPath) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = am.open(srcfileName);
            fos = new FileOutputStream(new File(destPath));
            int len = 0;
            byte[] buf = new byte[BUFFER_SIZE];
            while (true) {
                len = is.read(buf);
                if (len < 0) {
                    break;
                }
                fos.write(buf, 0, len);
            }
        } catch (Exception e) {

        } finally {
            FileUtils.closeQuietly(fos);
            FileUtils.closeQuietly(is);
        }

    }

    // 根据Unicode编码完美的判断中文汉字和符号
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    // 完整的判断中文汉字和符号
    public static boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    public static void showToastMessage(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size < 0) {
            size = 0;
        }
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }

    public static String stringUriToString(String uri) {
        String result = Uri.parse(uri).getEncodedPath();
        if (result != null) {
            result = Uri.decode(result);
        }
        return result;
    }

    public static void toggleWiFi(Context context, boolean enabled) {
        WifiManager wm = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        wm.setWifiEnabled(enabled);
    }

    public static int getConfiguredNetworks(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            List<WifiConfiguration> netlist = wifiManager.getConfiguredNetworks();
            if (netlist == null) {
                return -1;
            }
            return netlist.size();
        }
        return 0;
    }

    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    public static String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        Date currentTime = cal.getTime();
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
        return sdFormat.format(currentTime);
    }

    public static String delHTMLTag(String htmlStr) {
        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>";
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>";
        String regEx_html = "<[^>]+>";

        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll("");

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll("");

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll("");

        return htmlStr.trim();
    }

    public static String lastWeek() {
        Date date = new Date();
        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
        int month = Integer.parseInt(new SimpleDateFormat("MM").format(date));
        int day = Integer.parseInt(new SimpleDateFormat("dd").format(date)) - 6;

        if (day < 1) {
            month -= 1;
            if (month == 0) {
                year -= 1;
                month = 12;
            }
            if (month == 4 || month == 6 || month == 9 || month == 11) {
                day = 30 + day;
            } else if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                day = 31 + day;
            } else if (month == 2) {
                if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) day = 29 + day;
                else day = 28 + day;
            }
        }
        String y = year + "";
        String m = "";
        String d = "";
        if (month < 10) m = "0" + month;
        else m = month + "";
        if (day < 10) d = "0" + day;
        else d = day + "";

        return y + "-" + m + "-" + d;
    }

    public static String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        Date currentTime = cal.getTime();
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
        return sdFormat.format(currentTime);
    }

    public static String trim(String input) {
        if (StringUtils.isNotBlank(input)) {
            input = input.trim();
            input = input.replace("\u0000", "");
            input = input.replace("\\u0000", "");
            input = input.replaceAll("\\u0000", ""); // removes NUL chars
            input = input.replaceAll("\\\\u0000", ""); // removes backslash+u0000
        }
        return input;
    }

    public static String titleCharDelete(String input) {
        String result = input;
        for (String string : SPECIAL_CHAR) {
            result = result.replaceAll(string, UNDERLINE_CHAR);
        }
        return result;
    }

    public static int getFileList(final String path) {
        File file = new File(path);
        if (file.listFiles() == null) {
            return 0;
        }
        return file.listFiles().length;
    }

    public static boolean isChinese(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh")) {
            return true;
        } else {
            return false;
        }
    }

    public static void hideSoftWindow(Activity activity) {
        InputMethodManager imm = (InputMethodManager) DRApplication.getInstance().getSystemService(DRApplication.getInstance().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    public static int getScreenWidth(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    public static void openBaiduBaiKe(Context context, String editQuery) {
        if (StringUtils.isNullOrEmpty(editQuery)) {
            Toast.makeText(context, R.string.illegalInput, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        String baseUrl = Constants.WIKTIONARY_URL;
        if (Utils.isChinese(context)) {
            baseUrl = Constants.BAIDU_BAIKE_URL;
        }
        Uri content_url = Uri.parse(baseUrl + editQuery);
        intent.setData(content_url);
        context.startActivity(intent);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static String getStringLength(String content) {
        return content.length() + "";
    }

    public static String getTimeAndMinute(String hour, String minute) {
        return hour + ":" + minute;
    }

    public static String getTimeAndMinuteSecond(int hour, int minute) {
        String content = "";
        if (Integer.valueOf(hour) < 10 && Integer.valueOf(minute) < 10) {
            content = "0" + hour + ":0" + minute;
            return content;
        } else if (Integer.valueOf(hour) < 10 && Integer.valueOf(minute) >= 10) {
            content = "0" + hour + ":" + minute;
            return content;
        } else if (Integer.valueOf(hour) >= 10 && Integer.valueOf(minute) < 10) {
            content = hour + ":0" + minute;
            return content;
        } else if (Integer.valueOf(hour) >= 10 && Integer.valueOf(minute) >= 10) {
            content = hour + ":" + minute;
            return content;
        }
        return content;
    }

    public static String getTimeQuantum(String hour, String minute) {
        return hour + "-" + minute;
    }

    public static List<DictTypeBean> getDictName(String catalogue) {
        List<File> files = getDictionaryFileList(catalogue);
        List<DictTypeBean> list = new ArrayList<DictTypeBean>();
        for (File subFile : files) {
            if (subFile.isHidden()) {
                continue;
            }
            if (subFile.isDirectory()) {
                String dictPath = subFile.getPath();
                File path = Environment.getExternalStorageDirectory();
                String dictName = dictPath.substring((path + catalogue).length() + 1);
                DictTypeBean dictTypeData = new DictTypeBean(dictName);
                if (!list.contains(dictTypeData)) {
                    list.add(dictTypeData);
                }
            }
        }
        return list;
    }

    public static List<String> loadItemData(Context context) {
        List<String> itemList = new ArrayList<>();
        itemList.add(context.getString(R.string.webview_action_copy));
        itemList.add(context.getString(R.string.webview_action_cancel));
        return itemList;
    }

    public static List<String> getPathList(int dictType) {
        List<String> pathList = new ArrayList<>();
        if (dictType == Constants.ENGLISH_TYPE) {
            pathList = loadLocalDict(Constants.ENGLISH_DICTIONARY);
        } else if (dictType == Constants.CHINESE_TYPE) {
            pathList = loadLocalDict(Constants.CHINESE_DICTIONARY);
        } else if (dictType == Constants.OTHER_TYPE) {
            pathList = loadLocalDict(Constants.OTHER_DICTIONARY);
        }
        return pathList;
    }

    public static List<String> getAllDictPathList() {
        List<String> pathList = new ArrayList<>();
        pathList.addAll(loadLocalDict(Constants.CHINESE_DICTIONARY));
        pathList.addAll(loadLocalDict(Constants.ENGLISH_DICTIONARY));
        pathList.addAll(loadLocalDict(Constants.OTHER_DICTIONARY));
        return pathList;
    }

    public static List<String> getAllFolderPathList() {
        List<String> dictPaths = new ArrayList<>();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory();
            dictPaths.add(path + Constants.CHINESE_DICTIONARY);
            dictPaths.add(path + Constants.ENGLISH_DICTIONARY);
            dictPaths.add(path + Constants.OTHER_DICTIONARY);
        }
        return dictPaths;
    }

    public static void closeQuietly(SQLiteDatabase closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeQuietly(Cursor cursor) {
        try {
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
