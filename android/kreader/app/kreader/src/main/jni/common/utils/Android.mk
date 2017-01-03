LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

# measurements show that the ARM version of ZLib is about x1.17 faster
# than the thumb one...

LOCAL_MODULE := libutils


src_files := \
	JNIUtils.cpp


LOCAL_CFLAGS += -O3 -DUSE_MMAP
LOCAL_SRC_FILES := $(src_files)
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)

include $(BUILD_STATIC_LIBRARY)
