LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := touch_reader

LOCAL_NDK_STL_VARIANT := gnustl_static
LOCAL_CXXFLAGS := -fPIC -std=c++11
LOCAL_CXXFLAGS += -O3 -fstrict-aliasing -fprefetch-loop-arrays -fexceptions
LOCAL_CXXFLAGS += -Wno-non-virtual-dtor -Wall

LOCAL_STATIC_LIBRARIES := libutils

LOCAL_SRC_FILES := \
    touch_reader.cpp \
    touch_reader_jni.cpp

LOCAL_LDLIBS := -llog -lz -ljnigraphics
LOCAL_C_INCLUDES := \
     com_onyx_android_sdk_scribble_touch_RawInputProcessor.h \
     touch_reader.h \
     log.h \
     $(LOCAL_PATH)/../utils

include $(BUILD_SHARED_LIBRARY)