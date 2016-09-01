LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := hw_colorpen
LOCAL_SRC_FILES := libs/$(TARGET_ARCH_ABI)/libhw_colorpen.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/..
include $(PREBUILT_SHARED_LIBRARY)


include $(CLEAR_VARS)

LOCAL_MODULE := onyx_algorithm
LOCAL_NDK_STL_VARIANT := gnustl_static
LOCAL_CXXFLAGS := -fPIC -std=c++11
LOCAL_CXXFLAGS += -O3 -fstrict-aliasing -fprefetch-loop-arrays -fexceptions
LOCAL_CXXFLAGS += -Wno-non-virtual-dtor -Wall

LOCAL_SRC_FILES := \
    onyx_algorithm.cpp


LOCAL_LDLIBS := -llog -lz
LOCAL_SHARED_LIBRARIES := hw_colorpen
include $(BUILD_SHARED_LIBRARY)
