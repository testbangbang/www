//
//  epub_parser.h
//  EpubParser
//
//  Created by 胡小毛 on 17/4/11.
//  Copyright © 2017年 huxiaomao. All rights reserved.
//

#ifndef epub_parser_h
#define epub_parser_h

#include "common.h"
#include "read_file.h"
#include "epub_data_block.h"
#include "epub_directory_info.h"

class EpubParser{
public:
    long directoryOffset;
    long directorySize;
    map<string,EpubDirectoryInfo> epubDataList;
    EpubDirectoryInfo currentDirectory;
public:
    const int DIRECTORY_STRUCT_SIZE = 46;
private:
    bool parserHeadInfo(ReadFile* readFile);
    bool parserDirectoryInfo(ReadFile* readFile);
    long getDirectoryInfo(char* pBuf,long bufLength);
public:
    bool parser(ReadFile* readFile);
    EpubDataBlock* parserDataBlock(ReadFile* readFile,EpubDataBlock inEpubDataBlock);
    void printDirectoryInfoList();
    bool getDirectoryInfo(const string directoryName);
};

#endif /* epub_parser_h */
