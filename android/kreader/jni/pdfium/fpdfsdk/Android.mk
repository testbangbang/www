LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := libpdfium

LOCAL_ARM_MODE := arm
LOCAL_NDK_STL_VARIANT := gnustl_static

LOCAL_CXXFLAGS += -O3 -fstrict-aliasing -fprefetch-loop-arrays -fexceptions
LOCAL_CXXFLAGS += -Wno-non-virtual-dtor -Wall
LOCAL_CFLAGS += -DFOXIT_CHROME_BUILD

# Mask some warnings. These are benign, but we probably want to fix them
# upstream at some point.
LOCAL_CFLAGS += -Wno-unused-parameter \
                -Wno-sign-compare

LOCAL_STATIC_LIBRARIES := libpdfiumcore

# TODO: figure out why turning on exceptions requires manually linking libdl
LOCAL_SHARED_LIBRARIES := libdl
 #freetype2-static


LOCAL_SRC_FILES := \
    src/fpdfdoc.cpp \
    src/fpdfeditimg.cpp \
    src/fpdfeditpage.cpp \
    src/fpdfppo.cpp \
    src/fpdfsave.cpp \
    src/fpdftext.cpp \
    src/fpdfview.cpp \
    src/fpdf_dataavail.cpp \
    src/fpdf_ext.cpp \
    src/fpdf_flatten.cpp \
    src/fpdf_progressive.cpp \
    src/fpdf_searchex.cpp \
    src/fpdf_transformpage.cpp \
    src/fsdk_rendercontext.cpp


MY_SRC_ROOT := $(LOCAL_PATH)/..
LOCAL_C_INCLUDES := \
    $(MY_SRC_ROOT)  \
    $(MY_SRC_ROOT)/core/include


include $(BUILD_STATIC_LIBRARY)
