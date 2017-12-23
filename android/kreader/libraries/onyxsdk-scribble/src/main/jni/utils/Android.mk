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

include $(CLEAR_VARS)
LOCAL_MODULE := ink_utils

LOCAL_NDK_STL_VARIANT := gnustl_static
LOCAL_CXXFLAGS := -fPIC -std=c++11
LOCAL_CXXFLAGS += -O3 -fstrict-aliasing -fprefetch-loop-arrays -fexceptions
LOCAL_CXXFLAGS += -Wno-non-virtual-dtor -Wall

LOCAL_STATIC_LIBRARIES := libutils

LOCAL_LDLIBS := -llog -lz -ljnigraphics

LOCAL_SRC_FILES := \
    ink_utils.cpp \

include $(BUILD_SHARED_LIBRARY)
