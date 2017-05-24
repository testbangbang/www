LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := k2pdfoptlib
LOCAL_STATIC_LIBRARIES += willuslib
LOCAL_C_INCLUDES := $(LOCAL_PATH) $(LOCAL_PATH)/../willuslib
LOCAL_SRC_FILES := devprofile.c k2files.c k2mem.c k2parsecmd.c k2publish.c k2usage.c pageregions.c bmpregion.c k2bmp.c k2mark.c k2menu.c k2settings.c k2version.c userinput.c k2file.c k2master.c k2ocr.c k2proc.c k2sys.c pagelist.c wrapbmp.c textrows.c textwords.c
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)
include $(BUILD_STATIC_LIBRARY)
