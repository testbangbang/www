LOCAL_PATH := $(call my-dir)

ZLIB_ROOT := $(LOCAL_PATH)/extlibs/zlib

include $(CLEAR_VARS)
LOCAL_MODULE := zlib
LOCAL_CFLAGS := -DFOO=2
LOCAL_C_INCLUDES := $(ZLIB_ROOT)
LOCAL_SRC_FILES := \
    $(ZLIB_ROOT)/adler32.c \
    $(ZLIB_ROOT)/compress.c \
    $(ZLIB_ROOT)/crc32.c \
    $(ZLIB_ROOT)/deflate.c \
    $(ZLIB_ROOT)/infback.c \
    $(ZLIB_ROOT)/inffast.c \
    $(ZLIB_ROOT)/inflate.c \
    $(ZLIB_ROOT)/inftrees.c \
    $(ZLIB_ROOT)/trees.c \
    $(ZLIB_ROOT)/uncompr.c \
    $(ZLIB_ROOT)/zutil.c

LOCAL_SRC_FILES := $(addprefix ../../, $(LOCAL_SRC_FILES))

LOCAL_EXPORT_C_INCLUDES := $(ZLIB_ROOT)
LOCAL_EXPORT_CFLAGS := -DFOO=1
include $(BUILD_STATIC_LIBRARY)

