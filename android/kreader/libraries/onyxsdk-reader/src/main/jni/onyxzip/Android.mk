LOCAL_PATH := $(call my-dir)

MY_LOCAL_PATH := $(LOCAL_PATH)

include $(CLEAR_VARS)

ONYXZIP_ROOT := $(LOCAL_PATH)

LOCAL_C_INCLUDES := \
    $(ONYXZIP_ROOT)/../ZipLib \
    $(ONYXZIP_ROOT) \
    $(ONYXZIP_ROOT)/../common/utils

LOCAL_CFLAGS := -fPIC -Wno-enum-conversion -O3
LOCAL_CXXFLAGS := -fPIC -std=c++11 -O3
LOCAL_MODULE := onyxzip

LOCAL_LDLIBS := -llog

LOCAL_SRC_FILES := \
    $(wildcard $(ONYXZIP_ROOT)/*.cpp)

LOCAL_STATIC_LIBRARIES := libutils ZipLib bzip2 lzma zlib

LOCAL_SRC_FILES := $(addprefix ../../, $(LOCAL_SRC_FILES))

include $(BUILD_SHARED_LIBRARY)
