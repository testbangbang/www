//
//  epub_head_info.h
//  EpubParser
//
//  Created by 胡小毛 on 17/4/10.
//  Copyright © 2017年 huxiaomao. All rights reserved.
//

#ifndef epub_head_info_h
#define epub_head_info_h
#include "common.h"

class EpubDataBlock{
private:
    long	signature;          // signature                    4 bytes
    char	version;            // version needed to extract    2 bytes
    char	flag;               // general purpose flag         2 bytes
    char	compressedType;     // compression method           2 bytes
    long	lastTime;           // last modification time       2 bytes
    long	crc32;              // CRC 32                       4 bytes
    long	compressSize;	    // compressed size              4 bytes
    long	uncompressSize;	    // uncompressed size            4 bytes
    char	fileNameLength;     // filename length              2 bytes
    char	extraLength;        // extra field length           2 bytes
    char    *pOutData;
    int     outDatalength;
public:
    EpubDataBlock();
    ~EpubDataBlock();
};

#endif /* epub_head_info_h */
