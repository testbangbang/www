//
//  parser.cpp
//  EpubParser
//
//  Created by huxiaomao on 17/3/31.
//  Copyright © 2017年 huxiaomao. All rights reserved.
//

#include "parser.h"

Parser::Parser(){
    
}

Parser::~Parser(){
    
}

bool Parser::load(string filePath){
    bool bRet = readFile.openFile(filePath);
    if(!bRet){
        return false;
    }
    int ret = epubParser.parser(&readFile);
    if(!ret){
        return false;
    }
    if(IS_DEBUG){
        //test
        epubParser.printDirectoryInfoList();
        bool isExist = epubParser.getDirectoryInfo(CONTAINER);
        if(isExist){
            LOG("Find result:%s\n",epubParser.currentDirectory.getFileName().c_str());
        }
    }
    
    return true;
}
