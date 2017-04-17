//
//  epub_parser.cpp
//  EpubParser
//
//  Created by 胡小毛 on 17/4/11.
//  Copyright © 2017年 huxiaomao. All rights reserved.
//

#include "epub_parser.h"

bool EpubParser::parser(ReadFile* readFile){
    bool bRet = parserHeadInfo(readFile);
    if(!bRet){
        return false;
    }    
    bRet = parserDirectoryInfo(readFile);
    return bRet;
}

bool EpubParser::parserHeadInfo(ReadFile* readFile){
    unsigned char buf[BYTE_4] = {0};
    long offset = readFile->getFileSize() - BYTE_16;
    int length = 0;
    for(;offset > 0;offset -= 2){
        memset(buf,0x00,BYTE_4);
        length = readFile->read4Byte(buf, offset);
        if(length > 0){
            if(buf[0] == 0x50 &&
               buf[1] == 0x4B &&
               buf[2] == 0x05 &&
               buf[3] == 0x06){
                offset += BYTE_12;
                directorySize = readFile->readLittleEndian(offset);
                offset += BYTE_4;
                directoryOffset = readFile->readLittleEndian(offset);
                break;
            }
        }
    }
    
    if(offset <= 0){
        return false;
    }
    
    if(directoryOffset <= 0 ||
       directoryOffset >= readFile->getFileSize() ||
       directorySize <= 0 ||
       directorySize >= readFile->getFileSize()){
        return false;
    }
    return true;
}

bool EpubParser::parserDirectoryInfo(ReadFile* readFile){
    long bufferSize = directorySize >= MAX_BUFFER_SIZE?MAX_BUFFER_SIZE:directorySize;
    long size = bufferSize;
    char* pBuf = new char[size + BYTE_2];
    if(pBuf != NULL){
        long offset = directoryOffset;
        int ret = 0;
        for(long index = 0;index < directorySize;){
            memset(pBuf, 0x00, bufferSize);
            ret = readFile->readBuffer(pBuf, size, offset);
            if(ret < 0){
                break;
            }
            long remain = getDirectoryInfo(pBuf,size);
            
            index += size - remain;
            offset += size - remain;
            size = directorySize - index < bufferSize?directorySize - index:bufferSize;
        }
    }
    
    if(pBuf != NULL){
        delete [] pBuf;
    }
    
    return true;
}

long  EpubParser::getDirectoryInfo(char* pBuf,long bufLength){
    if(pBuf == NULL){
        return 0;
    }
    int offset = 0;
    for(offset = 0;offset + DIRECTORY_STRUCT_SIZE < bufLength;){
        EpubDirectoryInfo pEpubDirectoryInfo;
        
        memcpy(&(pEpubDirectoryInfo.signature),&pBuf[offset],BYTE_4);
        offset += BYTE_4;
        //LOG("signature:0x%x\n",pEpubDirectoryInfo.signature);
        
        memcpy(&(pEpubDirectoryInfo.madeVersion),&pBuf[offset],BYTE_2);
        offset += BYTE_2;
        //LOG("madeVersion:0x%x\n",pEpubDirectoryInfo.madeVersion);
        
        memcpy(&(pEpubDirectoryInfo.minimumVersion),&pBuf[offset],BYTE_2);
        offset += BYTE_2;
        //LOG("minimumVersion:0x%x\n",pEpubDirectoryInfo.minimumVersion);
        
        memcpy(&(pEpubDirectoryInfo.flag),&pBuf[offset],BYTE_2);
        offset += BYTE_2;
        //LOG("flag:0x%x\n",pEpubDirectoryInfo.flag);
        
        memcpy(&(pEpubDirectoryInfo.compressedType),&pBuf[offset],BYTE_2);
        offset += BYTE_2;
        //LOG("compressedType:0x%x\n",pEpubDirectoryInfo.compressedType);
        
        memcpy(&(pEpubDirectoryInfo.lastTime),&pBuf[offset],BYTE_2);
        offset += BYTE_2;
        //LOG("lastTime:0x%x\n",pEpubDirectoryInfo.lastTime);
        
        memcpy(&(pEpubDirectoryInfo.lastData),&pBuf[offset],BYTE_2);
        offset += BYTE_2;
        //LOG("lastData:0x%x\n",pEpubDirectoryInfo.lastData);
        
        memcpy(&(pEpubDirectoryInfo.crc32),&pBuf[offset],BYTE_4);
        offset += BYTE_4;
        //LOG("crc32:0x%x\n",pEpubDirectoryInfo.crc32);
        
        memcpy(&(pEpubDirectoryInfo.compressedSize),&pBuf[offset],BYTE_4);
        offset += BYTE_4;
        LOG("compressedSize:%d\n",pEpubDirectoryInfo.compressedSize);
        
        memcpy(&(pEpubDirectoryInfo.uncompressedSize),&pBuf[offset],BYTE_4);
        offset += BYTE_4;
        LOG("uncompressedSize:%d\n",pEpubDirectoryInfo.uncompressedSize);
        
        memcpy(&(pEpubDirectoryInfo.nameLength),&pBuf[offset],BYTE_2);
        offset += BYTE_2;
        LOG("nameLength:%d\n",pEpubDirectoryInfo.nameLength);
        
        memcpy(&(pEpubDirectoryInfo.extraLength),&pBuf[offset],BYTE_2);
        offset += BYTE_2;
        LOG("extraLength:%d\n",pEpubDirectoryInfo.extraLength);
        
        memcpy(&(pEpubDirectoryInfo.commentLength),&pBuf[offset],BYTE_2);
        offset += BYTE_2;
        LOG("commentLength:%d\n",pEpubDirectoryInfo.commentLength);
        
        memcpy(&(pEpubDirectoryInfo.diskNumberstart),&pBuf[offset],BYTE_2);
        offset += BYTE_2;
        //LOG("diskNumberstart:0x%x\n",pEpubDirectoryInfo.diskNumberstart);

        memcpy(&(pEpubDirectoryInfo.internalAttr),&pBuf[offset],BYTE_2);
        offset += BYTE_2;
        //LOG("internalAttr:0x%x\n",pEpubDirectoryInfo.internalAttr);
        
        memcpy(&(pEpubDirectoryInfo.externalAttr),&pBuf[offset],BYTE_4);
        offset += BYTE_4;
        //LOG("externalAttr:0x%x\n",pEpubDirectoryInfo.externalAttr);
        
        memcpy(&(pEpubDirectoryInfo.offset),&pBuf[offset],BYTE_4);
        offset += BYTE_4;
        LOG("offset:0x%x\n",pEpubDirectoryInfo.offset);
        
        if(pEpubDirectoryInfo.nameLength > 0 && pEpubDirectoryInfo.nameLength < MAX_BUFFER_SIZE){
            char* pFileName = new char[pEpubDirectoryInfo.nameLength + BYTE_2];
            if(pFileName != NULL){
                memset(pFileName,0x00,pEpubDirectoryInfo.nameLength + BYTE_2);
                memcpy(pFileName,&pBuf[offset],pEpubDirectoryInfo.nameLength);
                pEpubDirectoryInfo.fileName = pFileName;
                LOG("fileName:%s\n",pEpubDirectoryInfo.fileName.c_str());
                delete [] pFileName;
            }
        }
        
        offset += pEpubDirectoryInfo.nameLength + pEpubDirectoryInfo.extraLength + pEpubDirectoryInfo.commentLength;
        
        if(pEpubDirectoryInfo.compressedSize > 0 &&
           pEpubDirectoryInfo.uncompressedSize > 0 &&
           pEpubDirectoryInfo.fileName.length() > 0){
            EpubDirectoryInfo epubDirectoryInfo;
            epubDirectoryInfo.fileName = pEpubDirectoryInfo.fileName;
            epubDataList.insert(map<string,EpubDirectoryInfo>::value_type(pEpubDirectoryInfo.fileName,epubDirectoryInfo));
        }
    
        LOG("Directory =====================\n");
    }
    return bufLength - offset;
}

bool EpubParser::getDirectoryInfo(const string directoryName){
    map<string,EpubDirectoryInfo>::iterator iterator = epubDataList.find(directoryName);
    if(iterator != epubDataList.end()){
        currentDirectory = iterator->second;
        return true;
    }
    return false;
}

void EpubParser::printDirectoryInfoList(){
    map<string, EpubDirectoryInfo>::iterator iterator;
    for(iterator = epubDataList.begin();iterator != epubDataList.end();iterator++){
        string fileName = iterator->first;
        EpubDirectoryInfo epubDirectoryInfo = iterator->second;
        
        LOG("fileName:%s\n",epubDirectoryInfo.fileName.c_str());
    }
}

EpubDataBlock* EpubParser::parserDataBlock(ReadFile* readFile,EpubDataBlock inEpubDataBlock){
    EpubDataBlock* pEpubDataBlock = new EpubDataBlock();
    return pEpubDataBlock;
}
