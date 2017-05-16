LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := libjsonxx

src_files := \
    jsonxx.cc

LOCAL_CFLAGS += -O3 -DUSE_MMAP
LOCAL_SRC_FILES := $(src_files)
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)

include $(BUILD_STATIC_LIBRARY)
