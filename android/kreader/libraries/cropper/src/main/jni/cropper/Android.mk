LOCAL_PATH := $(call my-dir)
K2PDFOPTLIB_PATH := $(LOCAL_PATH)/../k2pdfoptlib
WILLUSLIB_PATH := $(LOCAL_PATH)/../willuslib

include $(CLEAR_VARS)
LOCAL_MODULE := k2pdfoptlib
LOCAL_STATIC_LIBRARIES += willuslib
LOCAL_C_INCLUDES := K2PDFOPTLIB_PATH WILLUSLIB_PATH
LOCAL_SRC_FILES := $(K2PDFOPTLIB_PATH)/devprofile.c $(K2PDFOPTLIB_PATH)/k2files.c $(K2PDFOPTLIB_PATH)/k2mem.c $(K2PDFOPTLIB_PATH)/k2parsecmd.c $(K2PDFOPTLIB_PATH)/k2publish.c $(K2PDFOPTLIB_PATH)/k2usage.c $(K2PDFOPTLIB_PATH)/pageregions.c $(K2PDFOPTLIB_PATH)/bmpregion.c $(K2PDFOPTLIB_PATH)/k2bmp.c $(K2PDFOPTLIB_PATH)/k2mark.c $(K2PDFOPTLIB_PATH)/k2menu.c $(K2PDFOPTLIB_PATH)/k2settings.c $(K2PDFOPTLIB_PATH)/k2version.c $(K2PDFOPTLIB_PATH)/userinput.c $(K2PDFOPTLIB_PATH)/k2file.c $(K2PDFOPTLIB_PATH)/k2master.c $(K2PDFOPTLIB_PATH)/k2ocr.c $(K2PDFOPTLIB_PATH)/k2proc.c $(K2PDFOPTLIB_PATH)/k2sys.c $(K2PDFOPTLIB_PATH)/pagelist.c $(K2PDFOPTLIB_PATH)/wrapbmp.c $(K2PDFOPTLIB_PATH)/textrows.c $(K2PDFOPTLIB_PATH)/textwords.c
LOCAL_EXPORT_C_INCLUDES := $(K2PDFOPTLIB_PATH)
include $(BUILD_STATIC_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := willuslib
LOCAL_C_INCLUDES := $(LOCAL_PATH)/willuslib
LOCAL_SRC_FILES := $(WILLUSLIB_PATH)/ansi.c $(WILLUSLIB_PATH)/filelist.c $(WILLUSLIB_PATH)/math.c $(WILLUSLIB_PATH)/pdfwrite.c $(WILLUSLIB_PATH)/token.c $(WILLUSLIB_PATH)/win.c $(WILLUSLIB_PATH)/array.c $(WILLUSLIB_PATH)/fontdata.c $(WILLUSLIB_PATH)/mem.c $(WILLUSLIB_PATH)/point2d.c $(WILLUSLIB_PATH)/wfile.c $(WILLUSLIB_PATH)/wmupdf.c $(WILLUSLIB_PATH)/bmp.c $(WILLUSLIB_PATH)/fontrender.c $(WILLUSLIB_PATH)/ocr.c $(WILLUSLIB_PATH)/render.c $(WILLUSLIB_PATH)/wgs.c $(WILLUSLIB_PATH)/wsys.c $(WILLUSLIB_PATH)/bmpdjvu.c $(WILLUSLIB_PATH)/gslpolyfit.c $(WILLUSLIB_PATH)/ocrjocr.c $(WILLUSLIB_PATH)/strbuf.c $(WILLUSLIB_PATH)/wzfile.c $(WILLUSLIB_PATH)/bmpmupdf.c $(WILLUSLIB_PATH)/linux.c $(WILLUSLIB_PATH)/string.c $(WILLUSLIB_PATH)/willusversion.c $(WILLUSLIB_PATH)/dtcompress.c $(WILLUSLIB_PATH)/wgui.c
LOCAL_EXPORT_C_INCLUDES := $(WILLUSLIB_PATH)
include $(BUILD_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := onyx_cropper
LOCAL_C_INCLUDES := $(LOCAL_PATH) WILLUSLIB_PATH K2PDFOPTLIB_PATH
LOCAL_SRC_FILES := cropper.cpp image_embolden_filter.cpp image_filter_util.cpp image_gamma_filter.cpp
LOCAL_SRC_FILES += setting.c
# LOCAL_SRC_FILES := $(addprefix ../../../../, $(LOCAL_SRC_FILES))

LOCAL_LDLIBS := -llog -lz -ljnigraphics
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)
LOCAL_STATIC_LIBRARIES += k2pdfoptlib willuslib
include $(BUILD_SHARED_LIBRARY)
