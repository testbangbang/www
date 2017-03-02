package com.onyx.android.sdk.utils;


import android.util.SparseArray;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

/**
 * TTF Font file pareser
 * <p/>
 * sample:
 * <code><pre>
 *             File fs = new File("/system/fonts");
 *             File[] files = fs.listFiles(new FilenameFilter() { *
 *               public boolean accept(File dir, String name) {
 *                 if (name.endsWith("ttf")) return true;
 *                   return false;
 *                 }
 *               });
 *             TTFUtils utils = new TTFUtils();
 *             for (File file : files) {
 *               utils.parse(file.getAbsolutePath());
 *               System.out.println("font name: " + parser.getFontName());
 *             }
 * </pre></code>
 * <p/>
 * ref link:http://www.microsoft.com/typography/otspec/name.htm
 * Created by solskjaer49 on 15/11/19 18:04.
 */
public class TTFUtils {
    public static int COPYRIGHT = 0;
    public static int FAMILY_NAME = 1;
    public static int FONT_SUBFAMILY_NAME = 2;
    public static int UNIQUE_FONT_IDENTIFIER = 3;
    public static int FULL_FONT_NAME = 4;
    public static int VERSION = 5;
    public static int POSTSCRIPT_NAME = 6;
    public static int TRADEMARK = 7;
    public static int MANUFACTURER = 8;
    public static int DESIGNER = 9;
    public static int DESCRIPTION = 10;
    public static int URL_VENDOR = 11;
    public static int URL_DESIGNER = 12;
    public static int LICENSE_DESCRIPTION = 13;
    public static int LICENSE_INFO_URL = 14;

    private SparseArray<String> fontProperties = new SparseArray<String>();

    /**
     * ttf font name
     *
     * @return may return null
     */
    public String getFontName() {
        return fontProperties.get(FULL_FONT_NAME, fontProperties.get(FAMILY_NAME, null));
    }

    /**
     * ttf Property
     *
     * @param nameID
     * @return
     */
    public String getFontProperty(int nameID) {
        return fontProperties.get(nameID, null);
    }

    public SparseArray<String> getFontProperties() {
        return fontProperties;
    }

    /**
     * @param fileName
     * @throws IOException
     */
    public void parse(String fileName) throws IOException {
        fontProperties.clear();
        RandomAccessFile f = null;
        try {
            f = new RandomAccessFile(fileName, "r");
            parseInner(f);
        } finally {
            try {
                f.close();
            } catch (Exception e) {
                // ignore;
            }
        }
    }


    private void parseInner(RandomAccessFile randomAccessFile) throws IOException {
        int majorVersion = randomAccessFile.readShort();
        int minorVersion = randomAccessFile.readShort();
        int numOfTables = randomAccessFile.readShort();

        if (majorVersion != 1 || minorVersion != 0) {
            return;
        }

        // jump to TableDirectory struct
        randomAccessFile.seek(12);

        boolean found = false;
        byte[] buff = new byte[4];
        TableDirectory tableDirectory = new TableDirectory();
        for (int i = 0; i < numOfTables; i++) {
            randomAccessFile.read(buff);
            tableDirectory.name = new String(buff);
            tableDirectory.checkSum = randomAccessFile.readInt();
            tableDirectory.offset = randomAccessFile.readInt();
            tableDirectory.length = randomAccessFile.readInt();

            if ("name".equalsIgnoreCase(tableDirectory.name)) {
                found = true;
                break;
            } else if (tableDirectory.name == null || tableDirectory.name.length() == 0) {
                break;
            }
        }

        // not found table of name
        if (!found) {
            return;
        }

        randomAccessFile.seek(tableDirectory.offset);
        NameTableHeader nameTableHeader = new NameTableHeader();
        nameTableHeader.fSelector = randomAccessFile.readShort();
        nameTableHeader.nRCount = randomAccessFile.readShort();
        nameTableHeader.storageOffset = randomAccessFile.readShort();

        NameRecord nameRecord = new NameRecord();
        for (int i = 0; i < nameTableHeader.nRCount; i++) {
            nameRecord.platformID = randomAccessFile.readShort();
            nameRecord.encodingID = randomAccessFile.readShort();
            nameRecord.languageID = randomAccessFile.readShort();
            nameRecord.nameID = randomAccessFile.readShort();
            nameRecord.stringLength = randomAccessFile.readShort();
            nameRecord.stringOffset = randomAccessFile.readShort();

            long pos = randomAccessFile.getFilePointer();
            byte[] bf = new byte[nameRecord.stringLength];
            long vpos = tableDirectory.offset + nameRecord.stringOffset + nameTableHeader.storageOffset;
            randomAccessFile.seek(vpos);
            randomAccessFile.read(bf);
            Charset charset;
            switch (nameRecord.encodingID) {
                case 0:
                    charset = Charset.forName("US-ASCII");
                    break;
                case 1:
                    charset = Charset.forName("UTF-16");
                    break;
                case 2:
                    charset = Charset.forName("Shift-JIS");
                    break;
                case 3:
                    charset = Charset.forName("GB18030");
                    break;
                case 4:
                    charset = Charset.forName("Big5");
                    break;
                default:
                    charset = Charset.forName("UTF-16");
            }
            String temp = new String(bf, charset);
            fontProperties.put(nameRecord.nameID, temp);
            randomAccessFile.seek(pos);
        }
    }

    @Override
    public String toString() {
        return fontProperties.toString();
    }

    private static class TableDirectory {
        String name; //table name
        int checkSum; //Check sum
        int offset; //Offset from beginning of file
        int length; //length of the table in bytes
    }

    private static class NameTableHeader {
        int fSelector; //format selector. Always 0
        int nRCount; //Name Records count
        int storageOffset; //Offset for strings storage,
    }

    private static class NameRecord {
        int platformID;
        int encodingID;
        int languageID;
        int nameID;
        int stringLength;
        int stringOffset; //from start of storage area
    }
}