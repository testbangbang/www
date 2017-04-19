//
//  read_file.cpp
//  EpubParser
//
//  Created by huxiaomao on 17/3/30.
//  Copyright © 2017年 huxiaomao. All rights reserved.
//

#include "read_file.h"


ReadFile::ReadFile(){
    
}

ReadFile::~ReadFile(){
    if(pFile != NULL){
        fclose(pFile);
        pFile = NULL;
    }
}

bool ReadFile::openFile(const string filePath){
    LOG("Open file:%s\n",filePath.c_str());
    
    pFile = fopen(filePath.c_str(), "rb");
    if(pFile == NULL){
        LOG("Open file fail:%s\n",filePath.c_str());
        return false;
    }
    
    this->filePath = filePath;
    
    fseek(pFile, 0, SEEK_END);
    fileSize = ftell(pFile);
    fseek(pFile, 0, SEEK_SET);
    LOG("FileSize:%d\n",fileSize);
    return true;
}

long ReadFile::getFileSize(){
    return fileSize;
}

int ReadFile::readBigEndian(long offset){
    int value = 0;
    if(offset != -1){
        fseek(pFile,offset,SEEK_SET);
    }
    fread(&value, sizeof(value), 1, pFile);
    return BETOH32(value);
}

int ReadFile::readBigEndian(const unsigned char* src,long offset,long size){
    int value;
    memcpy(&value,&src[offset],size);
    return BETOH32(value);
}

int ReadFile::readLittleEndian(long offset){
    int value = 0;
    if(offset != -1){
        fseek(pFile,offset,SEEK_SET);
    }
    fread(&value, sizeof(value), 1, pFile);
    return value;
}

int ReadFile::readBuffer(void* buf,long len,long offset){
    if(offset != -1){
        fseek(pFile,offset,SEEK_SET);
    }
    return (int)fread(buf,len,1,pFile);
}

int ReadFile::read2Byte(unsigned char* buf,long offset){
    if(offset != -1){
        fseek(pFile,offset,SEEK_SET);
    }
    return (int)fread(buf,BYTE_2,1,pFile);
}

int ReadFile::read4Byte(unsigned char* buf,long offset){
    if(offset != -1){
        fseek(pFile,offset,SEEK_SET);
    }
    return (int)fread(buf,BYTE_4,1,pFile);
}

int ReadFile::read8Byte(unsigned char* buf,long offset){
    if(offset != -1){
        fseek(pFile,offset,SEEK_SET);
    }
    return (int)fread(buf,BYTE_8,1,pFile);
}
