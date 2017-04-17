//
//  common.h
//  EpubParser
//
//  Created by 胡小毛 on 17/3/31.
//  Copyright © 2017年 huxiaomao. All rights reserved.
//

#ifndef common_h
#define common_h

#include <string>
#include <fstream>
#include <iostream>
#include <map>
#include <vector>

using namespace std;

#define IS_DEBUG true                       //true or false

#define LOG_BUF_SIZE                        2048
#define TAG                                 "EpubParser"
#ifdef __APPLE__
#define ANDROID_LOG_INFO                    1
#else
#define ANDROID_LOG_INFO                    100
#endif

#define BYTE_2                              2
#define BYTE_4                              4
#define BYTE_8                              8
#define BYTE_12                             12
#define BYTE_16                             16

#define MAX_BUFFER_SIZE                     8192

void LOG(const char *fmt, ...);

#define BETOH16(x) (((x&0xff)<<8)|((x&0xff00)>>8))
#define BETOH32(x) (((x&0xff)<<24)|((x&0xff00)<<8)|((x&0xff0000)>>8)|((x&0xff000000)>>24))
#define BETOH64(x) (((x&0xff)<<56)|\
    ((x&0xff00)<<40)|              \
    ((x&0xff0000)<<24)|            \
    ((x&0xff000000)<<8)|           \
    ((x&0xff00000000)>>8)|         \
    ((x&0xff0000000000)>>24)|      \
    ((x&0xff000000000000)>>40)|    \
    ((x&0xff00000000000000)>>56))

unsigned long long readNumberBigEndian(int offset,int numberWidth);
unsigned long long readNumberBigEndian(const unsigned char* src,int offset,int size);

#endif /* common_h */
