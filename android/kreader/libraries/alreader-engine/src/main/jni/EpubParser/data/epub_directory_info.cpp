//
//  directoryInfo.cpp
//  EpubParser
//
//  Created by 胡小毛 on 17/4/10.
//  Copyright © 2017年 huxiaomao. All rights reserved.
//
#include "epub_directory_info.h"

EpubDirectoryInfo::EpubDirectoryInfo(){
    
}
EpubDirectoryInfo::~EpubDirectoryInfo(){
    
}
string EpubDirectoryInfo::getFileName(){
    return fileName;
}

short EpubDirectoryInfo::getFileNameLength(){
    return nameLength;
}

long EpubDirectoryInfo::getCompressedSize(){
    return compressedSize;
}

long EpubDirectoryInfo::getUncompressedSize(){
    return uncompressedSize;
}

