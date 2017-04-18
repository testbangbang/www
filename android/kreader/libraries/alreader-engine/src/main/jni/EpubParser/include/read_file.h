//
//  readfile.h
//  EpubParser
//
//  Created by huxiaomao on 17/3/30.
//  Copyright © 2017年 huxiaomao. All rights reserved.
//

#ifndef readfile_h
#define readfile_h
#include "common.h"
#include "zlib.h"

class ReadFile{
private:
    string filePath;
    FILE* pFile;
    long fileSize;
public:
    ReadFile();
    ~ReadFile();
    bool openFile(string filePath);
    long getFileSize();
    int readBigEndian(long offset);
    int readBigEndian(const unsigned char* src,long offset,long size);
    int readLittleEndian(long offset);
    int readBuffer(void* buf,long len,long offset);
    int read2Byte(unsigned char* buf,long offset);
    int read4Byte(unsigned char* buf,long offset);
    int read8Byte(unsigned char* buf,long offset);
};
#endif /* readfile_h */
