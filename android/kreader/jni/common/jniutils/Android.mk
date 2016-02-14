LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

# measurements show that the ARM version of ZLib is about x1.17 faster
# than the thumb one...

LOCAL_MODULE := libjniutils

src_files := \
	JNIUtils.cpp

LOCAL_CFLAGS += -O3 -DUSE_MMAP
LOCAL_SRC_FILES := $(src_files)
include $(BUILD_STATIC_LIBRARY)

