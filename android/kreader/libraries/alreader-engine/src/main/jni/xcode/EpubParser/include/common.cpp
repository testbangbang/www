//
//  common.cpp
//  EpubParser
//
//  Created by 胡小毛 on 17/3/31.
//  Copyright © 2017年 huxiaomao. All rights reserved.
//

#include "common.h"

void LOG(const char *fmt, ...)
{
    if(IS_DEBUG){
        va_list args;
        
        char printBuf[LOG_BUF_SIZE];
        
        va_start(args, fmt);
        vsnprintf(printBuf, LOG_BUF_SIZE, fmt, args);
        va_end(args);
#if defined(__APPLE__) || defined(__WIN32) || defined(__WIN64) || defined(__linux__)
        cout<<printBuf;
#else
        __android_log_write(ANDROID_LOG_INFO, TAG, printBuf);
#endif
    }
}


unsigned long long readNumberBigEndian(int offset,int numberWidth){
    return 0;
}

unsigned long long readNumberBigEndian(const unsigned char* src,int offset,int size){
    return 0;
}
