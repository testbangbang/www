package com.neverland.engbook.bookobj;

import android.content.Context;
import android.util.Log;

import com.neverland.engbook.level1.AlFilesBypassNative;
import com.neverland.engbook.level2.AlFormat;

import java.io.Closeable;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huxiaomao on 2017/11/24.
 */

public class FileBlockInfo {
    public static final int CACHE_FILE_VERSION = 1;
    public static final String OPEN_MODE = "rw";
    public static final int CACHE_FILE_VERSION_OFFSET = 0;
    public static final int BYTE_4 = 4;
    public static final int MD5_LENGTH = 32;//MD5 + last block info
    public static final int VERSION_LENGTH = BYTE_4;
    public static final int MD5_INFO_OFFSET = CACHE_FILE_VERSION_OFFSET + BYTE_4;
    public static final int PARAGRAPH_BLOCK_INFO_SIZE = BYTE_4 + BYTE_4 + BYTE_4 + BYTE_4 + BYTE_4;
    public static final int FILE_BLOCK_NUMBER_SIZE = BYTE_4;
    public static final int LAST_BLOCK_INFO_SIZE = BYTE_4;
    public static final int FILE_BLOCK_NUMBER_INFO_OFFSET = MD5_INFO_OFFSET + MD5_LENGTH;
    public static final int LAST_BLOCK_INFO_OFFSET = MD5_INFO_OFFSET + MD5_LENGTH + FILE_BLOCK_NUMBER_SIZE;
    public static final int TEXT_DATA_SIZE_LENGTH = BYTE_4;
    public static final int TEXT_DATA_SIZE_OFFSET = LAST_BLOCK_INFO_OFFSET + LAST_BLOCK_INFO_SIZE;
    public static final int EXTEND_SIZE = 56;
    public static final int EPUB_HEAD_SIZE = VERSION_LENGTH + MD5_LENGTH + FILE_BLOCK_NUMBER_SIZE + LAST_BLOCK_INFO_SIZE + TEXT_DATA_SIZE_LENGTH + EXTEND_SIZE;
    public static final int BLOCK_SIZE = 3 * BYTE_4;

    private static final String[] HTML = {".xhtml", ".html","htm"};
    private int blockSize;
    private int paragraphOffset;
    private int paragraphNumber;

    public FileBlockInfo oneBlock;
    public FileBlockInfo twoBlock;
    public FileBlockInfo threeBlock;
    public LoadBlockInfo loadBlockInfo;

    private List<ParagraphInfo> paragraphInfos = new ArrayList<>();

    public static class ParagraphInfo {
        public int paragraphIndex;
        public int startOffset;
        public int length;
        public int positionS;
        public int positionE;
    }

    public static class LoadBlockInfo {
        public int startBlockOffset;
        public int endBlockOffset;
        public int startParagraphIndex;
        public int endParagraphIndex;
        public int paragraphNumber;
        public int textDataStart;
        public List<ParagraphInfo> emptyParagraphList;
    }

    public static class CacheHeadInfo {
        public int version;
        public String md5;
        public int fileNumber;
    }

    public static boolean isBookContent(String fileName) {
        for (String html : HTML) {
            if (fileName.toLowerCase().endsWith(html)) {
                return true;
            }
        }
        return false;
    }

    private ParagraphInfo createParagraphInfo(int index, int startOffset, int length,int positionS, int positionE) {
        ParagraphInfo paragraphInfo = new ParagraphInfo();
        paragraphInfo.paragraphIndex = index;
        paragraphInfo.startOffset = startOffset;
        paragraphInfo.length = length;
        paragraphInfo.positionS = positionS;
        paragraphInfo.positionE = positionE;
        return paragraphInfo;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public List<ParagraphInfo> getParagraphInfos() {
        return paragraphInfos;
    }

    public void addParagraphInfo(int index, int startOffset, int length,int positionS, int positionE) {
        this.paragraphInfos.add(createParagraphInfo(index, startOffset, length,positionS, positionE));
    }

    private static int getHeadDataLength(List<FileBlockInfo> fileBlocks) {
        int size = fileBlocks.size();
        if (size <= 0) {
            return -1;
        }
        return size * BLOCK_SIZE;
    }

    private static List<FileBlockInfo> saveParagraphData(RandomAccessFile randomAccessFile, List<FileBlockInfo> fileBlocks) throws Exception {
        List<FileBlockInfo> blockInfos = new ArrayList<>();
        int headDataLength = getHeadDataLength(fileBlocks) + EPUB_HEAD_SIZE;
        randomAccessFile.setLength(headDataLength);
        randomAccessFile.seek(headDataLength);
        for (FileBlockInfo fileBlockInfo : fileBlocks) {
            int paragraphSize = fileBlockInfo.getParagraphInfos().size() * PARAGRAPH_BLOCK_INFO_SIZE;
            byte[] data = new byte[paragraphSize];
            int index = 0;
            for (FileBlockInfo.ParagraphInfo paragraphInfo : fileBlockInfo.getParagraphInfos()) {
                putInt(data, index, paragraphInfo.paragraphIndex);
                index += BYTE_4;
                putInt(data, index, paragraphInfo.startOffset);
                index += BYTE_4;
                putInt(data, index, paragraphInfo.length);
                index += BYTE_4;
                putInt(data, index, paragraphInfo.positionS);
                index += BYTE_4;
                putInt(data, index, paragraphInfo.positionE);
                index += BYTE_4;
            }

            int offset = (int) randomAccessFile.length();

            FileBlockInfo block = new FileBlockInfo();
            block.blockSize = fileBlockInfo.blockSize;
            block.paragraphOffset = offset;
            block.paragraphNumber = fileBlockInfo.getParagraphInfos().size();
            blockInfos.add(block);

            randomAccessFile.write(data);
        }
        return blockInfos;
    }

    public static int getInt(byte[] data) {
        return data[3] & 0xFF |
                (data[2] & 0xFF) << 8 |
                (data[1] & 0xFF) << 16 |
                (data[0] & 0xFF) << 24;
    }

    public static void putInt(byte[] data, int index, int value) {
        data[index] = (byte) ((value >> 24) & 0xff);
        data[index + 1] = (byte) ((value >> 16) & 0xff);
        data[index + 2] = (byte) ((value >> 8) & 0xff);
        data[index + 3] = (byte) ((value >> 0) & 0xff);
    }

    public static boolean isCacheData(AlFormat alFormat) {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = FileBlockInfo.openFile(FileBlockInfo.getFilePath(alFormat));
            return FileBlockInfo.checkParagraphData(randomAccessFile, alFormat);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileBlockInfo.closeFile(randomAccessFile);
        }
        return false;
    }

    private static void saveHeadData(RandomAccessFile randomAccessFile, List<FileBlockInfo> fileBlocks, String md5) throws Exception {
        randomAccessFile.seek(MD5_INFO_OFFSET);
        //write md5
        randomAccessFile.write(md5.getBytes());
        //write file count
        byte[] temp = new byte[BYTE_4];
        putInt(temp, 0, fileBlocks.size());
        randomAccessFile.seek(FILE_BLOCK_NUMBER_INFO_OFFSET);
        randomAccessFile.write(temp);

        randomAccessFile.seek(EPUB_HEAD_SIZE);
        int size = getHeadDataLength(fileBlocks);
        byte[] data = new byte[size];
        int index = 0;
        for (FileBlockInfo fileBlockInfo : fileBlocks) {
            putInt(data, index, fileBlockInfo.blockSize);
            index += BYTE_4;
            putInt(data, index, fileBlockInfo.paragraphOffset);
            index += BYTE_4;
            putInt(data, index, fileBlockInfo.paragraphNumber);
            index += BYTE_4;
        }
        randomAccessFile.write(data);
    }

    public static void saveTextDatSize(RandomAccessFile randomAccessFile, int size) throws Exception {
        randomAccessFile.seek(TEXT_DATA_SIZE_OFFSET);
        byte[] temp = new byte[BYTE_4];
        putInt(temp, 0, size);
        randomAccessFile.write(temp);
    }

    public static void setCacheVersion(RandomAccessFile randomAccessFile, int version) throws Exception {
        randomAccessFile.seek(CACHE_FILE_VERSION_OFFSET);
        byte[] temp = new byte[BYTE_4];
        putInt(temp, 0, version);
        randomAccessFile.write(temp);
    }

    public static int getTextDataSize(AlFormat alFormat) {
        int offset = TEXT_DATA_SIZE_OFFSET;
        RandomAccessFile randomAccessFile = null;
        int textDataSize = 0;
        try {
            randomAccessFile = openFile(getFilePath(alFormat));
            randomAccessFile.seek(offset);
            byte[] temp = new byte[BYTE_4];
            randomAccessFile.read(temp);
            textDataSize = getInt(temp);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeFile(randomAccessFile);
        }
        return textDataSize;
    }

    public static void saveParagraphData(List<FileBlockInfo> fileBlocks, AlFormat alFormat) {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = openFile(getFilePath(alFormat));
            if (checkParagraphData(randomAccessFile, alFormat)) {
                return;
            }
            String md5 = alFormat.getFileMD5();

            List<FileBlockInfo> blockInfos = saveParagraphData(randomAccessFile, fileBlocks);
            saveHeadData(randomAccessFile, blockInfos, md5);
            saveTextDatSize(randomAccessFile, alFormat.getSize());
            setCacheVersion(randomAccessFile, CACHE_FILE_VERSION);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeFile(randomAccessFile);
        }
    }

    public static boolean checkParagraphData(RandomAccessFile randomAccessFile, AlFormat alFormat) throws Exception {
        String md5 = alFormat.getFileMD5();

        File file = new File(getFilePath(alFormat));
        if (!file.exists()) {
            return false;
        }
        //check version
        byte[] data = new byte[BYTE_4];
        randomAccessFile.seek(CACHE_FILE_VERSION_OFFSET);
        randomAccessFile.read(data);
        int version = getInt(data);
        if (version < CACHE_FILE_VERSION) {
            return false;
        }

        data = new byte[MD5_LENGTH];
        randomAccessFile.seek(MD5_INFO_OFFSET);
        randomAccessFile.read(data, 0, MD5_LENGTH);
        String saveMd5 = new String(data);
        if (saveMd5.equals(md5)) {
            return true;
        }
        return false;
    }

    public static List<FileBlockInfo> loadParagraphData(AlFormat alFormat) {
        List<FileBlockInfo> blockInfos = null;
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = openFile(getFilePath(alFormat));
            if (checkParagraphData(randomAccessFile, alFormat)) {
                blockInfos = new ArrayList<>();
                loadHeadData(randomAccessFile, blockInfos);
                loadParagraphInfo(randomAccessFile, blockInfos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeFile(randomAccessFile);
        }
        return blockInfos;
    }

    private static boolean loadHeadData(RandomAccessFile randomAccessFile, List<FileBlockInfo> blockInfos) throws Exception {
        randomAccessFile.seek(FILE_BLOCK_NUMBER_INFO_OFFSET);
        byte[] data = new byte[BYTE_4];
        randomAccessFile.read(data);
        int fileNumber = getInt(data);
        if (fileNumber <= 0) {
            return false;
        }

        int blockSize = BLOCK_SIZE;
        data = new byte[fileNumber * blockSize];
        randomAccessFile.seek(EPUB_HEAD_SIZE);
        randomAccessFile.read(data);
        byte[] intData = new byte[BYTE_4];
        for (int index = 0; index < data.length; ) {
            FileBlockInfo fileBlockInfo = new FileBlockInfo();
            System.arraycopy(data, index, intData, 0, BYTE_4);
            index += BYTE_4;
            fileBlockInfo.blockSize = getInt(intData);

            System.arraycopy(data, index, intData, 0, BYTE_4);
            index += BYTE_4;
            fileBlockInfo.paragraphOffset = getInt(intData);

            System.arraycopy(data, index, intData, 0, BYTE_4);
            index += BYTE_4;
            fileBlockInfo.paragraphNumber = getInt(intData);

            blockInfos.add(fileBlockInfo);
        }
        return true;
    }

    private static void loadParagraphInfo(RandomAccessFile randomAccessFile, List<FileBlockInfo> blockInfos) throws Exception {
        for (FileBlockInfo fileBlockInfo : blockInfos) {
            int offset = fileBlockInfo.paragraphOffset;
            byte[] data = new byte[fileBlockInfo.paragraphNumber * PARAGRAPH_BLOCK_INFO_SIZE];
            randomAccessFile.seek(offset);
            randomAccessFile.read(data);
            byte[] intData = new byte[BYTE_4];
            int index = 0;
            for (int i = 0; i < fileBlockInfo.paragraphNumber; i++) {
                System.arraycopy(data, index, intData, 0, BYTE_4);
                int paragraphIndex = getInt(intData);
                index += BYTE_4;

                System.arraycopy(data, index, intData, 0, BYTE_4);
                int startOffset = getInt(intData);
                index += BYTE_4;

                System.arraycopy(data, index, intData, 0, BYTE_4);
                int length = getInt(intData);
                index += BYTE_4;

                System.arraycopy(data, index, intData, 0, BYTE_4);
                int positionS = getInt(intData);
                index += BYTE_4;

                System.arraycopy(data, index, intData, 0, BYTE_4);
                int positionE = getInt(intData);
                index += BYTE_4;

                fileBlockInfo.addParagraphInfo(paragraphIndex, startOffset, length,positionS, positionE);
            }
        }
    }

    public static boolean checkData(List<FileBlockInfo> data1, List<FileBlockInfo> data2) {
        if (data1.size() != data2.size()) {
            Log.e("checkData", "data size error data1.size:" + data1.size() + ",data2.size:" + data2.size());
            return false;
        }

        for (int i = 0; i < data1.size(); i++) {
            FileBlockInfo fileBlockInfo1 = data1.get(i);
            FileBlockInfo fileBlockInfo2 = data2.get(i);
            if (fileBlockInfo1.getBlockSize() != fileBlockInfo2.getBlockSize()) {
                Log.e("checkData", i + ",FileBlockInfo size error FileBlockInfo1.size:" + fileBlockInfo1.getBlockSize() + ",data2.size:" + fileBlockInfo2.getBlockSize());
                return false;
            }
            if (fileBlockInfo1.getParagraphInfos().size() != fileBlockInfo2.getParagraphInfos().size()) {
                Log.e("checkData", i + ",FileBlockInfo size error FileBlockInfo1.getParagraphInfos:" + fileBlockInfo1.getParagraphInfos().size() + ",fileBlockInfo2.getParagraphInfos:" + fileBlockInfo2.getParagraphInfos().size());
                return false;
            }
            for (int j = 0; j < fileBlockInfo1.getParagraphInfos().size(); j++) {
                FileBlockInfo.ParagraphInfo paragraphInfo1 = fileBlockInfo1.getParagraphInfos().get(j);
                FileBlockInfo.ParagraphInfo paragraphInfo2 = fileBlockInfo2.getParagraphInfos().get(j);
                if (paragraphInfo1.paragraphIndex != paragraphInfo2.paragraphIndex) {
                    Log.e("checkData", j + ",paragraph index error 1:" + paragraphInfo1.paragraphIndex + ",2:" + paragraphInfo2.paragraphIndex);
                    return false;
                }
                if (paragraphInfo1.startOffset != paragraphInfo2.startOffset) {
                    Log.e("checkData", j + ",paragraph startOffset 1:" + paragraphInfo1.startOffset + ",2:" + paragraphInfo2.startOffset);
                    return false;
                }
                if (paragraphInfo1.length != paragraphInfo2.length) {
                    Log.e("checkData", j + ",paragraph length 1:" + paragraphInfo1.length + ",2:" + paragraphInfo2.length);
                }
            }
        }
        return true;
    }

    public static void closeFile(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static RandomAccessFile openFile(String path) throws Exception {
        return new RandomAccessFile(path, OPEN_MODE);
    }

    public static void testData(List<FileBlockInfo> fileBlocks, AlFormat alFormat) {
        List<FileBlockInfo> saveParagraph = FileBlockInfo.loadParagraphData(alFormat);
        checkData(fileBlocks, saveParagraph);
    }

    public static void saveLastBlockInfo(int lastBlock, AlFormat alFormat) {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = openFile(getFilePath(alFormat));
            randomAccessFile.seek(LAST_BLOCK_INFO_OFFSET);
            byte[] temp = new byte[BYTE_4];
            putInt(temp, 0, lastBlock);
            randomAccessFile.write(temp);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeFile(randomAccessFile);
        }
    }

    public static int getLastBlockInfo(AlFormat alFormat) {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = openFile(getFilePath(alFormat));
            randomAccessFile.seek(LAST_BLOCK_INFO_OFFSET);
            byte[] temp = new byte[BYTE_4];
            randomAccessFile.read(temp);
            return getInt(temp);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeFile(randomAccessFile);
        }
        return Integer.MAX_VALUE;
    }

    public static String getFilePath(AlFormat alFormat) {
        String filePath = alFormat.getFileName();
        String pathMd5 = AlFilesBypassNative.getMd5(filePath.getBytes());
        String cacheFileName = alFormat.aFiles.applicationDirectory + File.separator + pathMd5;
        return cacheFileName;
    }

    private static boolean isFirstBlock(int readPosition,List<ParagraphInfo> paragraphInfoList){
        if(paragraphInfoList.size() > 0){
            ParagraphInfo paragraphInfo = paragraphInfoList.get(0);
            if(paragraphInfo != null && readPosition < paragraphInfo.startOffset){
                return true;
            }
        }
        return false;
    }

    public static FileBlockInfo getBlockLoadInfo(int startBlockOffset, int endBlockOffset, int totalSize, int readPosition, List<FileBlockInfo> blockInfos) {
        FileBlockInfo result = new FileBlockInfo();
        int oneParagraphNumber = 0;
        int startOffset = 0;
        FileBlockInfo fileBlockInfo = null;
        List<ParagraphInfo> emptyParagraphList = new ArrayList<>();
        int i;

        int paragraphIndex = Integer.MAX_VALUE;
        for (i = 0; i < blockInfos.size(); i++) {
            fileBlockInfo = blockInfos.get(i);
            if(isFirstBlock(readPosition,fileBlockInfo.getParagraphInfos())){
                break;
            }
            int size = TxtParserHelp.getTextDataSize(fileBlockInfo.getParagraphInfos());
            int index = TxtParserHelp.findParagraphByPos(readPosition,size,fileBlockInfo.getParagraphInfos());
            if (index >= 0 && index < fileBlockInfo.getParagraphInfos().size()) {
                break;
            }
            oneParagraphNumber += fileBlockInfo.paragraphNumber;
            startOffset = fileBlockInfo.blockSize;
            if (paragraphIndex == Integer.MAX_VALUE && fileBlockInfo.getParagraphInfos().size() > 0) {
                paragraphIndex = fileBlockInfo.getParagraphInfos().get(0).paragraphIndex;
            }
            emptyParagraphList.addAll(fileBlockInfo.getParagraphInfos());
        }
        if (fileBlockInfo == null) {
            return null;
        }
        if (paragraphIndex == Integer.MAX_VALUE) {
            paragraphIndex = 0;
        }
        if (i == 0) {
            return getBlockLoadInfo(startBlockOffset, endBlockOffset, totalSize);
        }
        //one
        int textDataStart = fileBlockInfo.getParagraphInfos().get(0).startOffset;
        result.oneBlock = setFileBlockInfo(startOffset, fileBlockInfo.blockSize, textDataStart,
                oneParagraphNumber, oneParagraphNumber + fileBlockInfo.paragraphNumber,
                fileBlockInfo.paragraphNumber, emptyParagraphList);
        //two
        textDataStart = emptyParagraphList.get(0).startOffset;
        int lastItem = blockInfos.size() - 1;
        if (i == lastItem) {
            startOffset = fileBlockInfo.blockSize;
        }
        result.twoBlock = setFileBlockInfo(startBlockOffset, startOffset, textDataStart, paragraphIndex, oneParagraphNumber, oneParagraphNumber, null);
        //three
        if (i < lastItem) {
            FileBlockInfo threeFileBlockInfo = blockInfos.get(lastItem);
            int start = fileBlockInfo.getParagraphInfos().get(fileBlockInfo.getParagraphInfos().size() - 1).startOffset;
            int len = fileBlockInfo.getParagraphInfos().get(fileBlockInfo.getParagraphInfos().size() - 1).length;
            textDataStart = start + len;
            result.threeBlock = setFileBlockInfo(fileBlockInfo.blockSize, threeFileBlockInfo.blockSize,
                    textDataStart, oneParagraphNumber + fileBlockInfo.paragraphNumber,
                    oneParagraphNumber + fileBlockInfo.paragraphNumber + threeFileBlockInfo.paragraphNumber,
                    threeFileBlockInfo.paragraphNumber, null);
        }
        return result;
    }

    public static FileBlockInfo setFileBlockInfo(int startBlockOffset, int endBlockOffset, int textDataStart, int startParagraphIndex, int endParagraphIndex, int paragraphNumber,
                                                 List<ParagraphInfo> emptyParagraphList) {
        FileBlockInfo fileBlockInfo = new FileBlockInfo();
        fileBlockInfo.loadBlockInfo = new LoadBlockInfo();
        fileBlockInfo.loadBlockInfo.startBlockOffset = startBlockOffset;
        fileBlockInfo.loadBlockInfo.endBlockOffset = endBlockOffset;
        fileBlockInfo.loadBlockInfo.startParagraphIndex = startParagraphIndex;
        fileBlockInfo.loadBlockInfo.endParagraphIndex = endParagraphIndex;
        fileBlockInfo.loadBlockInfo.paragraphNumber = paragraphNumber;
        fileBlockInfo.loadBlockInfo.textDataStart = textDataStart;
        if (emptyParagraphList != null && emptyParagraphList.size() > 0) {
            fileBlockInfo.loadBlockInfo.emptyParagraphList = new ArrayList<>();
            fileBlockInfo.loadBlockInfo.emptyParagraphList.addAll(emptyParagraphList);
        }
        return fileBlockInfo;
    }

    public static FileBlockInfo getBlockLoadInfo(int oneBlockStart, int endBlockEnd, int totalSize) {
        FileBlockInfo result = new FileBlockInfo();

        result.oneBlock = setFileBlockInfo(oneBlockStart, endBlockEnd, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, null);
        result.twoBlock = setFileBlockInfo(endBlockEnd, totalSize, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, null);
        return result;
    }

    public static FileBlockInfo getBlockLoadInfo(int blockStart, int totalSize) {
        FileBlockInfo result = new FileBlockInfo();

        result.oneBlock = setFileBlockInfo(blockStart, totalSize, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, null);
        return result;
    }

    public static void loadOneBlockData(AlFormat alFormat, FileBlockInfo oneBlock) {
        if (oneBlock == null) {
            return;
        }
        if (oneBlock.loadBlockInfo.startParagraphIndex != Integer.MAX_VALUE) {
            alFormat.addEmptyParagraph(oneBlock.loadBlockInfo.emptyParagraphList);

            alFormat.customSize = oneBlock.loadBlockInfo.textDataStart;
        }
        alFormat.paragraphIndex = Integer.MAX_VALUE;
        alFormat.parserData(oneBlock.loadBlockInfo.startBlockOffset, oneBlock.loadBlockInfo.endBlockOffset);

        alFormat.setSize(alFormat.customSize);
    }

    public static void loadTwoBlockData(AlFormat alFormat, FileBlockInfo twoBlock) {
        if (twoBlock == null) {
            return;
        }
        if (twoBlock.loadBlockInfo.startParagraphIndex != Integer.MAX_VALUE) {
            alFormat.customSize = twoBlock.loadBlockInfo.textDataStart;
            alFormat.paragraphIndex = twoBlock.loadBlockInfo.startParagraphIndex - 1;
        } else {
            alFormat.paragraphIndex = Integer.MAX_VALUE;
        }
        alFormat.parserData(twoBlock.loadBlockInfo.startBlockOffset, twoBlock.loadBlockInfo.endBlockOffset);
    }

    public static void loadThreeBlockData(AlFormat alFormat, FileBlockInfo threeBlock) {
        if (threeBlock == null) {
            return;
        }
        if (threeBlock.loadBlockInfo.startParagraphIndex != Integer.MAX_VALUE) {
            alFormat.customSize = threeBlock.loadBlockInfo.textDataStart;
        }
        alFormat.paragraphIndex = Integer.MAX_VALUE;
        int size = alFormat.aFiles.getSize();
        alFormat.parserData(threeBlock.loadBlockInfo.startBlockOffset, size);
        alFormat.setSize(alFormat.customSize);
    }

    private int getTagValues(String tags) {
        int t = 0;
        for (int i = 0; i < tags.length(); i++) {
            char val = tags.charAt(i);
            t = (t * 31) + Character.toLowerCase(val);
        }
        return t;
    }

    public static void deleteCacheFile(String cachePath) {
        File file = new File(cachePath);
        if (file.exists()) {
            file.delete();
        }
    }

    public static CacheHeadInfo loadCacheHeadInfo(AlFormat alFiles) {
        RandomAccessFile randomAccessFile = null;
        try {
            CacheHeadInfo cacheHeadInfo = new CacheHeadInfo();
            randomAccessFile = openFile(getFilePath(alFiles));
            byte[] temp = new byte[BYTE_4];
            randomAccessFile.seek(CACHE_FILE_VERSION_OFFSET);
            randomAccessFile.read(temp);
            cacheHeadInfo.version = getInt(temp);

            byte[] data = new byte[MD5_LENGTH];
            randomAccessFile.seek(MD5_INFO_OFFSET);
            randomAccessFile.read(data, 0, MD5_LENGTH);
            cacheHeadInfo.md5 = new String(data);

            randomAccessFile.seek(CACHE_FILE_VERSION_OFFSET);
            randomAccessFile.read(temp);
            cacheHeadInfo.fileNumber = getInt(temp);
            return cacheHeadInfo;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeFile(randomAccessFile);
        }
        return null;
    }

    public static String getBookCachePath(Context context, String bookPath) {
        String applicationPath = context.getFilesDir().getAbsolutePath();
        String pathMd5 = AlFilesBypassNative.getMd5(bookPath.getBytes());
        return applicationPath + File.separator + pathMd5;
    }
}
