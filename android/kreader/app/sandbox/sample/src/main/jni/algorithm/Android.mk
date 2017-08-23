LOCAL_PATH:= $(call my-dir)



include $(CLEAR_VARS)

LOCAL_MODULE := onyx_epd_update
LOCAL_NDK_STL_VARIANT := gnustl_static
LOCAL_CXXFLAGS := -fPIC -std=c++11
LOCAL_CXXFLAGS += -O3 -fstrict-aliasing -fprefetch-loop-arrays -fexceptions
LOCAL_CXXFLAGS += -Wno-non-virtual-dtor -Wall

LOCAL_SRC_FILES := \
    onyx_epd_update.cpp


LOCAL_LDLIBS := -llog -lz -ljnigraphics
include $(BUILD_SHARED_LIBRARY)
