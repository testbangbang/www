//
//  parser.h
//  EpubParser
//
//  Created by huxiaomao on 17/3/31.
//  Copyright © 2017年 huxiaomao. All rights reserved.
//

#ifndef parser_h
#define parser_h

#include "read_file.h"
#include "epub_parser.h"

class Parser{
private:
    ReadFile readFile;
    EpubParser epubParser;
    const string CONTAINER = "META-INF/container.xml";
public:
    Parser();
    ~Parser();
    bool load(string filePath);
};
#endif /* parser_h */
