//
//  testParser.cpp
//  EpubParser
//
//  Created by 胡小毛 on 17/3/31.
//  Copyright © 2017年 huxiaomao. All rights reserved.
//
#include "common.h"
#include "testParser.h"
#include "parser.h"

void TestParser::TestOpenBooks_001(){
    string filePath = "/Users/huxiaomao/src/debug_data/test_data/luobupo.epub";
    Parser parser;
    LOG("test book:%s\n",filePath.c_str());
    parser.load(filePath);
}

void TestParser::TestOpenBooks_002(){
    string filePath = "/Users/huxiaomao/Downloads/03/03/HTML_CSS_JavaScript网页制作从入门到精通 - 刘西杰.epub";
    Parser parser;
    LOG("test book:%s\n",filePath.c_str());
    parser.load(filePath);
}

void TestParser::TestOpenBooks_003(){
    string filePath = "/Users/huxiaomao/Downloads/03/03/Linux程序设计(第4版) (图灵程序设计丛书·Linux_UNIX系列) - 马修(Neil Matthew).epub";
    Parser parser;
    LOG("test book:%s\n",filePath.c_str());
    parser.load(filePath);
}

void TestParser::TestOpenBooks_004(){
    string filePath = "/Users/huxiaomao/Downloads/03/03/Office 2013应用技巧实例大全 - 未知.epub";
    Parser parser;
    LOG("test book:%s\n",filePath.c_str());
    parser.load(filePath);
}

void TestParser::TestOpenBooks_005(){
    string filePath = "/Users/huxiaomao/Downloads/03/03/Project 2010企业项目管理实践 - 张会斌.epub";
    Parser parser;
    LOG("test book:%s\n",filePath.c_str());
    parser.load(filePath);
}

void TestParser::TestOpenBooks_006(){
    string filePath = "/Users/huxiaomao/Downloads/琥珀之剑.epub";
    Parser parser;
    LOG("test book:%s\n",filePath.c_str());
    parser.load(filePath);
}
