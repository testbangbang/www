LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := willuslib
LOCAL_LDLIBS += -llog -lz
LOCAL_C_INCLUDES := $(LOCAL_PATH)
LOCAL_SRC_FILES := ansi.c filelist.c math.c pdfwrite.c token.c win.c array.c fontdata.c mem.c point2d.c wfile.c wmupdf.c bmp.c fontrender.c ocr.c render.c wgs.c wsys.c bmpdjvu.c gslpolyfit.c ocrjocr.c strbuf.c wzfile.c bmpmupdf.c linux.c string.c willusversion.c dtcompress.c wgui.c
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)
include $(BUILD_STATIC_LIBRARY)
