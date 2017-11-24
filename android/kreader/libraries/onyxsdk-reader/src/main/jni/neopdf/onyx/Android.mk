LOCAL_PATH:= $(call my-dir)

MY_LOCAL_PATH := $(LOCAL_PATH)

JSONXX_ROOT := $(MY_LOCAL_PATH)/../../common/jsonxx

include $(CLEAR_VARS)

LOCAL_MODULE := neo_pdf

LOCAL_NDK_STL_VARIANT := gnustl_static

LOCAL_CXXFLAGS := -fPIC -std=c++11
LOCAL_CXXFLAGS += -O3 -fstrict-aliasing -fprefetch-loop-arrays -fexceptions
LOCAL_CXXFLAGS += -Wno-non-virtual-dtor -Wall


# LOCAL_STATIC_LIBRARIES := libpdfium libpdfiumcore libutils
LOCAL_STATIC_LIBRARIES := libutils libpodofo libjsonxx libpdfium-prebuilt

# TODO: figure out why turning on exceptions requires manually linking libdl
LOCAL_SHARED_LIBRARIES := libdl

LOCAL_LDLIBS := -llog -ljnigraphics -lz

LOCAL_SRC_FILES := \
    onyx_pdfium.cpp \
    form_helper.cpp \
    onyx_drm_decrypt.cpp \
    onyx_drm_jni.cpp \
    pdfwriter/onyx_pdf_writer.cpp \
    pdfwriter/writer_jni.cpp

MY_SRC_ROOT := $(LOCAL_PATH)/..
LOCAL_C_INCLUDES := \
    $(MY_SRC_ROOT) \
    $(JSONXX_ROOT) \
    $(MY_SRC_ROOT)/../prebuilt/pdfium \
    $(MY_SRC_ROOT)/../prebuilt/pdfium/public \
    $(MY_SRC_ROOT)/../common/utils \
    $(MY_SRC_ROOT)/third_party/podofo \
    $(MY_SRC_ROOT)/third_party/podofo/src

include $(BUILD_SHARED_LIBRARY)

