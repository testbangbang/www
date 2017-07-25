LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := onyx_cfa
LOCAL_C_INCLUDES := $(LOCAL_PATH) 
LOCAL_SRC_FILES := jni/common/imageUtils/cfa/cfa.cpp
LOCAL_SRC_FILES := $(addprefix ../../../../, $(LOCAL_SRC_FILES))

LOCAL_LDLIBS := -llog -lz -ljnigraphics
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)
LOCAL_STATIC_LIBRARIES += libutils

include $(BUILD_SHARED_LIBRARY)
