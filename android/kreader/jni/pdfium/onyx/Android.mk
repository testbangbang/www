LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := onyx_pdfium

LOCAL_NDK_STL_VARIANT := gnustl_static

LOCAL_CXXFLAGS := -fPIC -std=c++11
LOCAL_CXXFLAGS += -O3 -fstrict-aliasing -fprefetch-loop-arrays -fexceptions
LOCAL_CXXFLAGS += -Wno-non-virtual-dtor -Wall


LOCAL_STATIC_LIBRARIES := libpdfium libpdfiumcore libjniutils

# TODO: figure out why turning on exceptions requires manually linking libdl
LOCAL_SHARED_LIBRARIES := libdl

LOCAL_LDLIBS := -llog -lz -ljnigraphics

LOCAL_SRC_FILES := \
    onyx_pdfium.cpp

MY_SRC_ROOT := $(LOCAL_PATH)/..
LOCAL_C_INCLUDES := \
    $(MY_SRC_ROOT)                      \
    $(MY_SRC_ROOT)/public               \
    $(MY_SRC_ROOT)/../common/jniutils   \

include $(BUILD_SHARED_LIBRARY)
