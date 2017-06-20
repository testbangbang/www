LOCAL_PATH := $(call my-dir)

MY_LOCAL_PATH := $(LOCAL_PATH)

include $(MY_LOCAL_PATH)/zlib.mk
include $(MY_LOCAL_PATH)/bzip2.mk
include $(MY_LOCAL_PATH)/lzma.mk

include $(CLEAR_VARS)

ZIPLIB_ROOT := $(LOCAL_PATH)

LOCAL_C_INCLUDES := \
    $(ZIPLIB_ROOT)/compression \
    $(ZIPLIB_ROOT)/compression/bzip2 \
    $(ZIPLIB_ROOT)/compression/deflate \
    $(ZIPLIB_ROOT)/compression/lzma \
    $(ZIPLIB_ROOT)/compression/lzma/detail \
    $(ZIPLIB_ROOT)/compression/store \
    $(ZIPLIB_ROOT)/detail \
    $(ZIPLIB_ROOT)/extlibs \
    $(ZIPLIB_ROOT)/extlibs/bzip2 \
    $(ZIPLIB_ROOT)/extlibs/lzma \
    $(ZIPLIB_ROOT)/extlibs/lzma/unix \
    $(ZIPLIB_ROOT)/extlibs/lzma/unix/LzmaUtil \
    $(ZIPLIB_ROOT)/extlibs/zlib \
    $(ZIPLIB_ROOT)/methods \
    $(ZIPLIB_ROOT)/streams \
    $(ZIPLIB_ROOT)/streams/streambuffs \
    $(ZIPLIB_ROOT)/utils \
    $(ZIPLIB_ROOT)

LOCAL_CFLAGS := -fPIC -Wno-enum-conversion -O3
LOCAL_CXXFLAGS := -fPIC -std=c++11 -O3
LOCAL_MODULE := ZipLib
LOCAL_SRC_FILES := \
    $(wildcard $(ZIPLIB_ROOT)/detail/*.cpp) \
    $(wildcard $(ZIPLIB_ROOT)/*.cpp)

LOCAL_SRC_FILES := $(addprefix ../../, $(LOCAL_SRC_FILES))

include $(BUILD_STATIC_LIBRARY)

