LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := neo_cropper
LOCAL_C_INCLUDES := $(LOCAL_PATH) $(LOCAL_PATH)/../willuslib $(LOCAL_PATH)/../k2pdfoptlib
LOCAL_SRC_FILES := jni/common/imageUtils/cropper/cropper.cpp jni/common/imageUtils/cropper/image_embolden_filter.cpp jni/common/imageUtils/cropper/image_filter_util.cpp jni/common/imageUtils/cropper/image_gamma_filter.cpp
LOCAL_SRC_FILES += jni/common/imageUtils/cropper/setting.c
LOCAL_SRC_FILES := $(addprefix ../../../../, $(LOCAL_SRC_FILES))

LOCAL_LDLIBS := -llog -lz -ljnigraphics
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)
LOCAL_STATIC_LIBRARIES += k2pdfoptlib willuslib libutils

include $(BUILD_SHARED_LIBRARY)
