package com.neverland.engbook.bookobj;

import com.neverland.engbook.bookobj.FileBlockInfo.LoadBlockInfo;
import com.neverland.engbook.bookobj.FileBlockInfo.ParagraphInfo;
import com.neverland.engbook.level2.AlFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huxiaomao on 2017/12/14.
 */

public class TxtParserHelp {
    public static final int TXT_BASE_SIZE = 8096;
    public static final int MIN_BLOCK = 1024;
    public static final int BLOCK_ONE = 0;
    public static final int BLOCK_TWO = 1;
    public static final int BLOCK_THREE = 2;

    public enum SplitBlockType {
        BLOCK_TYPE_ONE, BLOCK_TYPE_TWO, BLOCK_TYPE_THREE
    }

    public static FileBlockInfo parserHelp(int readPosition, AlFormat alFormat) {
        List<FileBlockInfo> fileBlockInfos = FileBlockInfo.loadParagraphData(alFormat);
        return getBlockLoadInfo(readPosition, fileBlockInfos, alFormat);
    }

    public static FileBlockInfo getBlockLoadInfo(int readPosition, List<FileBlockInfo> fileBlockInfos, AlFormat alFormat) {
        LoadBlockInfo blockInfo = new LoadBlockInfo();
        FileBlockInfo fileBlockInfo = findReadPositionBlockInfo(readPosition, fileBlockInfos, blockInfo);
        if(fileBlockInfo == null){
            return null;
        }


        if (blockInfo.startParagraphIndex == 0) {
            //{0,endIndex,endIndex,size}
            return splitDataBlock(SplitBlockType.BLOCK_TYPE_ONE, blockInfo, fileBlockInfo, alFormat);
        }
        if (blockInfo.endParagraphIndex == fileBlockInfo.getParagraphInfos().size()) {
            //{0,startIndex,startIndex,endIndex}
            return splitDataBlock(SplitBlockType.BLOCK_TYPE_ONE, blockInfo, fileBlockInfo, alFormat);
        }
        //{0,startIndex,startIndex,endIndex,endIndex,size}
        return splitDataBlock(SplitBlockType.BLOCK_TYPE_THREE, blockInfo, fileBlockInfo, alFormat);
    }

    public static FileBlockInfo findReadPositionBlockInfo(int readPosition, List<FileBlockInfo> fileBlockInfos, LoadBlockInfo blockInfo) {
        FileBlockInfo fileBlockInfo = null;
        for (int i = 0; i < fileBlockInfos.size(); i++) {
            fileBlockInfo = fileBlockInfos.get(i);
            int size = getTextDataSize(fileBlockInfo.getParagraphInfos());
            int index = findParagraphByPos(readPosition, size, fileBlockInfo.getParagraphInfos());
            if (index >= 0 && index < fileBlockInfo.getParagraphInfos().size()) {
                LoadBlockInfo splitBlockInfo = extendBlock(index, fileBlockInfo.getParagraphInfos());
                blockInfo.startParagraphIndex = splitBlockInfo.startParagraphIndex;
                blockInfo.endParagraphIndex = splitBlockInfo.endParagraphIndex;
                break;
            }
        }
        return fileBlockInfo;
    }

    public static int getTextDataSize(List<ParagraphInfo> paragraphInfoList){
        int endIndex = paragraphInfoList.size() -1;
        ParagraphInfo paragraphInfo = paragraphInfoList.get(endIndex);
        return paragraphInfo.startOffset + paragraphInfo.length;
    }

    private static LoadBlockInfo extendBlock(int index, List<ParagraphInfo> paragraphInfoList) {
        int positionS = paragraphInfoList.get(index).positionS;
        int positionE = paragraphInfoList.get(index).positionE;
        //front
        int startIndex = forwardExtendBlock(index, positionS, paragraphInfoList);
        if (startIndex == Integer.MAX_VALUE) {
            startIndex = 0;
        }
        //back
        int endIndex = backwardExtendBlock(index, positionE, paragraphInfoList);
        if (endIndex == Integer.MAX_VALUE) {
            endIndex = paragraphInfoList.size();
        }
        LoadBlockInfo loadBlockInfo = new LoadBlockInfo();
        loadBlockInfo.startParagraphIndex = startIndex;
        loadBlockInfo.endParagraphIndex = endIndex;
        return loadBlockInfo;
    }

    private static List<ParagraphInfo> newEmptyParagraph(int index, List<ParagraphInfo> paragraphInfoList) {
        List<FileBlockInfo.ParagraphInfo> emptyParagraphList = new ArrayList<>();
        for (int i = 0; i < index; i++) {
            emptyParagraphList.add(paragraphInfoList.get(i));
        }
        return emptyParagraphList;
    }

    private static int backwardExtendBlock(int index, int positionE, List<ParagraphInfo> paragraphInfoList) {
        for (int i = index; i < paragraphInfoList.size(); i++) {
            FileBlockInfo.ParagraphInfo paragraphInfo = paragraphInfoList.get(i);
            if (paragraphInfo.positionE - positionE >= MIN_BLOCK) {
                return i;
            }
        }
        return Integer.MAX_VALUE;
    }

    private static int forwardExtendBlock(int index, int positionE, List<ParagraphInfo> paragraphInfoList) {
        for (int i = index; i > 0; i--) {
            ParagraphInfo paragraphInfo = paragraphInfoList.get(i);
            if (positionE - paragraphInfo.positionE >= MIN_BLOCK) {
                return i;
            }
        }
        return Integer.MAX_VALUE;
    }

    public static FileBlockInfo getBlockLoadInfo(int blockStart, int blockEnd) {
        FileBlockInfo result = new FileBlockInfo();
        result.twoBlock = FileBlockInfo.setFileBlockInfo(blockStart, blockEnd, Integer.MAX_VALUE
                , Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, null);
        return result;
    }

    public static int findParagraphByPos01(int start, int end, int pos, List<ParagraphInfo> paragraphInfoList) {
        int tmp = (end + start) >> 1;
        ParagraphInfo ap = paragraphInfoList.get(tmp);
        if (ap.startOffset > pos) {
            if(end - start <= 1){
                return -1;
            }
            return findParagraphByPos01(start, tmp, pos, paragraphInfoList);
        } else if (ap.startOffset + ap.length <= pos) {
            if(end - start <= 1){
                return -1;
            }
            return findParagraphByPos01(tmp, end, pos, paragraphInfoList);
        }
        return tmp;
    }

    public static int findParagraphByPos(int pos, int size, List<ParagraphInfo> paragraphInfoList) {
        if (pos >= size) {
            pos = size;
        }
        if (pos < 0) {
            pos = 0;
        }
        return findParagraphByPos01(0, paragraphInfoList.size(), pos, paragraphInfoList);
    }

    public static LoadBlockInfo initBlockInfo(int startOffset, int endOffset, int textDataStart, int startParagraphIndex, List<ParagraphInfo> emptyParagraphList) {
        LoadBlockInfo loadBlockInfo = new LoadBlockInfo();
        loadBlockInfo.startBlockOffset = startOffset;
        loadBlockInfo.endBlockOffset = endOffset;
        loadBlockInfo.textDataStart = textDataStart;
        loadBlockInfo.startParagraphIndex = startParagraphIndex;

        if (emptyParagraphList != null) {
            loadBlockInfo.emptyParagraphList = new ArrayList<>();
            loadBlockInfo.emptyParagraphList.addAll(emptyParagraphList);
        }
        return loadBlockInfo;
    }

    public static FileBlockInfo splitDataBlock(SplitBlockType flags, LoadBlockInfo blockInfo, FileBlockInfo fileBlockInfo, AlFormat alFormat) {
        List<LoadBlockInfo> splitBlockInfos = new ArrayList<>();
        if (flags == SplitBlockType.BLOCK_TYPE_ONE) {
            if(blockInfo.endParagraphIndex >= fileBlockInfo.getParagraphInfos().size()){
                splitBlockInfos.add(initBlockInfo(0, alFormat.aFiles.getSize(), Integer.MAX_VALUE, Integer.MAX_VALUE, null));
            }else {
                ParagraphInfo paragraphInfo = fileBlockInfo.getParagraphInfos().get(blockInfo.endParagraphIndex);
                splitBlockInfos.add(initBlockInfo(0, paragraphInfo.positionE + 1, Integer.MAX_VALUE, Integer.MAX_VALUE, null));
                paragraphInfo = fileBlockInfo.getParagraphInfos().get(blockInfo.endParagraphIndex + 1);
                splitBlockInfos.add(initBlockInfo(paragraphInfo.positionS, alFormat.aFiles.getSize(), Integer.MAX_VALUE, Integer.MAX_VALUE, null));
            }
            return getBlockLoadInfo(splitBlockInfos);
        }

        if (flags == SplitBlockType.BLOCK_TYPE_TWO) {
            ParagraphInfo paragraphInfo = fileBlockInfo.getParagraphInfos().get(blockInfo.startParagraphIndex);
            List<ParagraphInfo> emptyParagraphList = newEmptyParagraph(paragraphInfo.paragraphIndex, fileBlockInfo.getParagraphInfos());
            splitBlockInfos.add(initBlockInfo(paragraphInfo.positionS, alFormat.aFiles.getSize(), paragraphInfo.startOffset, paragraphInfo.paragraphIndex, emptyParagraphList));

            paragraphInfo = fileBlockInfo.getParagraphInfos().get(blockInfo.startParagraphIndex - 1);
            splitBlockInfos.add(initBlockInfo(0, paragraphInfo.positionE, 0, 1, null));

            return getBlockLoadInfo(splitBlockInfos);
        }

        if (flags == SplitBlockType.BLOCK_TYPE_THREE) {
            //{0,startIndex,startIndex,endIndex,endIndex,size}
            ParagraphInfo startParagraphInfo = fileBlockInfo.getParagraphInfos().get(blockInfo.startParagraphIndex);
            ParagraphInfo endParagraphInfo = fileBlockInfo.getParagraphInfos().get(blockInfo.endParagraphIndex);

            List<ParagraphInfo> emptyParagraphList = newEmptyParagraph(startParagraphInfo.paragraphIndex, fileBlockInfo.getParagraphInfos());

            splitBlockInfos.add(initBlockInfo(startParagraphInfo.positionS, endParagraphInfo.positionE + 1, startParagraphInfo.startOffset, startParagraphInfo.paragraphIndex, emptyParagraphList));

            startParagraphInfo = fileBlockInfo.getParagraphInfos().get(blockInfo.startParagraphIndex - 1);
            splitBlockInfos.add(initBlockInfo(0, startParagraphInfo.positionE + 1, 0, 1, null));

            endParagraphInfo = fileBlockInfo.getParagraphInfos().get(blockInfo.endParagraphIndex + 1);
            splitBlockInfos.add(initBlockInfo(endParagraphInfo.positionS, alFormat.aFiles.getSize(), endParagraphInfo.startOffset, endParagraphInfo.paragraphIndex, null));

            return getBlockLoadInfo(splitBlockInfos);
        }
        return null;
    }

    private static FileBlockInfo getBlockLoadInfo(List<LoadBlockInfo> splitBlockInfos) {
        FileBlockInfo result = new FileBlockInfo();
        int i = 0;
        for (LoadBlockInfo splitBlockInfo : splitBlockInfos) {
            switch (i) {
                case BLOCK_ONE:
                    result.oneBlock = FileBlockInfo.setFileBlockInfo(splitBlockInfo.startBlockOffset, splitBlockInfo.endBlockOffset, splitBlockInfo.textDataStart
                            , splitBlockInfo.startParagraphIndex, Integer.MAX_VALUE, Integer.MAX_VALUE, splitBlockInfo.emptyParagraphList);
                    break;
                case BLOCK_TWO:
                    result.twoBlock = FileBlockInfo.setFileBlockInfo(splitBlockInfo.startBlockOffset, splitBlockInfo.endBlockOffset, splitBlockInfo.textDataStart
                            , splitBlockInfo.startParagraphIndex, Integer.MAX_VALUE, Integer.MAX_VALUE, splitBlockInfo.emptyParagraphList);
                    break;
                case BLOCK_THREE:
                    result.threeBlock = FileBlockInfo.setFileBlockInfo(splitBlockInfo.startBlockOffset, splitBlockInfo.endBlockOffset, splitBlockInfo.textDataStart
                            , splitBlockInfo.startParagraphIndex, Integer.MAX_VALUE, Integer.MAX_VALUE, splitBlockInfo.emptyParagraphList);
                    break;
                default:
                    break;
            }
            i++;
        }
        return result;
    }
}
