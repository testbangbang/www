LOCAL_PATH := $(call my-dir)

BZIP2_ROOT := $(LOCAL_PATH)/extlibs/bzip2

include $(CLEAR_VARS)
LOCAL_MODULE := bzip2
LOCAL_CFLAGS := -DFOO=2
LOCAL_C_INCLUDES := $(BZIP2_ROOT)
LOCAL_SRC_FILES := \
    $(BZIP2_ROOT)/blocksort.c \
    $(BZIP2_ROOT)/bzerror.c \
    $(BZIP2_ROOT)/bzlib.c \
    $(BZIP2_ROOT)/compress.c \
    $(BZIP2_ROOT)/crctable.c \
    $(BZIP2_ROOT)/decompress.c \
    $(BZIP2_ROOT)/huffman.c \
    $(BZIP2_ROOT)/randtable.c

LOCAL_SRC_FILES := $(addprefix ../../, $(LOCAL_SRC_FILES))

LOCAL_EXPORT_C_INCLUDES := $(BZIP2_ROOT)
LOCAL_EXPORT_CFLAGS := -DFOO=1
include $(BUILD_STATIC_LIBRARY)

