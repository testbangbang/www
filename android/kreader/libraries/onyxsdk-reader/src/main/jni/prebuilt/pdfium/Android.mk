LOCAL_PATH := $(call my-dir)

# Prepare the SSL static library
include $(CLEAR_VARS)
LOCAL_MODULE := libpdfium-prebuilt
LOCAL_SRC_FILES := lib/libpdfium.a 
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/public
include $(PREBUILT_STATIC_LIBRARY)
