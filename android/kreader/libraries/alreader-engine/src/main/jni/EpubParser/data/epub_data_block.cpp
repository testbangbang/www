//
//  epub_data_block.cpp
//  EpubParser
//
//  Created by 胡小毛 on 17/4/12.
//  Copyright © 2017年 huxiaomao. All rights reserved.
//

#include "epub_data_block.h"

EpubDataBlock::EpubDataBlock(){
    
}

EpubDataBlock::~EpubDataBlock(){
    if(pOutData != NULL){
        delete [] pOutData;
        pOutData = NULL;
    }
}
