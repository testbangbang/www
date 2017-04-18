//
//  directoryInfo.h
//  EpubParser
//
//  Created by 胡小毛 on 17/4/10.
//  Copyright © 2017年 huxiaomao. All rights reserved.
//

#ifndef directoryInfo_h
#define directoryInfo_h
#include "common.h"

class EpubDirectoryInfo{
public:
    long   signature;          // central file header signature   4 bytes  (0x02014b50)
    short  madeVersion;        // version made by                 2 bytes
    short  minimumVersion;     // version needed to extract       2 bytes
    short  flag;               // general purpose bit flag        2 bytes
    short  compressedType;     // compression method              2 bytes
    short  lastTime;           // last mod file time              2 bytes
    short  lastData;           // last mod file date              2 bytes
    long   crc32;              // crc-32                          4 bytes
    long   compressedSize;     // compressed size                 4 bytes
    long   uncompressedSize;   // uncompressed size               4 bytes
    short  nameLength;         // filename length                 2 bytes
    short  extraLength;        // extra field length              2 bytes
    short  commentLength;      // file comment length             2 bytes
    short  diskNumberstart;    // disk number start               2 bytes
    short  internalAttr;       // internal file attributes        2 bytes
    long   externalAttr;       // external file attributes        4 bytes
    long   offset;             // relative offset of local header 4 bytes
    string fileName;          // File name                       n bytes
public:
    EpubDirectoryInfo();
    ~EpubDirectoryInfo();
    string   getFileName();
    short    getFileNameLength();
    long     getCompressedSize();
    long     getUncompressedSize();
    
};

#endif /* directoryInfo_h */
