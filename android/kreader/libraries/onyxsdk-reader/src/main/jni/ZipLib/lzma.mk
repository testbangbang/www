LOCAL_PATH := $(call my-dir)

LZMA_ROOT := $(LOCAL_PATH)/extlibs/lzma/unix

include $(CLEAR_VARS)
LOCAL_MODULE := lzma
LOCAL_CFLAGS := -DFOO=2
LOCAL_C_INCLUDES := $(LZMA_ROOT) $(LZMA_ROOT)/LzmaUtil
LOCAL_SRC_FILES := \
    $(LZMA_ROOT)/7zBuf2.c \
    $(LZMA_ROOT)/7zCrc.c \
    $(LZMA_ROOT)/7zCrcOpt.c \
    $(LZMA_ROOT)/7zStream.c \
    $(LZMA_ROOT)/Aes.c \
    $(LZMA_ROOT)/Alloc.c \
    $(LZMA_ROOT)/Bra.c \
    $(LZMA_ROOT)/Bra86.c \
    $(LZMA_ROOT)/BraIA64.c \
    $(LZMA_ROOT)/BwtSort.c \
    $(LZMA_ROOT)/CpuArch.c \
    $(LZMA_ROOT)/Delta.c \
    $(LZMA_ROOT)/HuffEnc.c \
    $(LZMA_ROOT)/LzFind.c \
    $(LZMA_ROOT)/LzFindMt.c \
    $(LZMA_ROOT)/Lzma2Dec.c \
    $(LZMA_ROOT)/Lzma2Enc.c \
    $(LZMA_ROOT)/LzmaDec.c \
    $(LZMA_ROOT)/LzmaEnc.c \
    $(LZMA_ROOT)/MtCoder.c \
    $(LZMA_ROOT)/Ppmd7.c \
    $(LZMA_ROOT)/Ppmd7Dec.c \
    $(LZMA_ROOT)/Ppmd7Enc.c \
    $(LZMA_ROOT)/Ppmd8.c \
    $(LZMA_ROOT)/Ppmd8Dec.c \
    $(LZMA_ROOT)/Ppmd8Enc.c \
    $(LZMA_ROOT)/Sha256.c \
    $(LZMA_ROOT)/Sort.c \
    $(LZMA_ROOT)/Threads.c \
    $(LZMA_ROOT)/Xz.c \
    $(LZMA_ROOT)/XzCrc64.c \
    $(LZMA_ROOT)/XzDec.c \
    $(LZMA_ROOT)/XzEnc.c \
    $(LZMA_ROOT)/XzIn.c \
    $(LZMA_ROOT)/LzmaUtil/Lzma86Dec.c \
    $(LZMA_ROOT)/LzmaUtil/Lzma86Enc.c

LOCAL_SRC_FILES := $(addprefix ../../, $(LOCAL_SRC_FILES))

LOCAL_EXPORT_C_INCLUDES := $(LZMA_ROOT) $(LZMA_ROOT)/LzmaUtil
LOCAL_EXPORT_CFLAGS := -DFOO=1
include $(BUILD_STATIC_LIBRARY)

