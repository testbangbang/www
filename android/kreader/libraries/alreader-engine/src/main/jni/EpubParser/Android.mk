
LOCAL_PATH := $(call my-dir)

MY_LOCAL_PATH := $(LOCAL_PATH)


EPUB_ROOT := $(LOCAL_PATH)

include $(CLEAR_VARS)
LOCAL_C_INCLUDES :=                      \
    jni/andprof                          \
    $(MY_LOCAL_PATH)/include             \
    $(MY_LOCAL_PATH)/third_party/zlib


LOCAL_MODULE    := onyx_epub
LOCAL_SRC_FILES := $(EPUB_ROOT)/parser/epub_parser.cpp                    \
                   $(EPUB_ROOT)/parser/parser.cpp                         \
                   $(EPUB_ROOT)/parser/xml_parser.cpp                     \
                   $(EPUB_ROOT)/data/epub_data_block.cpp                  \
                   $(EPUB_ROOT)/data/epub_directory_info.cpp              \
                   $(EPUB_ROOT)/readfile/read_file.cpp                    \
                   $(EPUB_ROOT)/include/common.cpp                        \

LOCAL_SRC_FILES := $(addprefix ../../, $(LOCAL_SRC_FILES))
LOCAL_CFLAGS += -O3
LOCAL_WHOLE_STATIC_LIBRARIES += android_support

LOCAL_STATIC_LIBRARIES += libutils


LOCAL_LDLIBS    := -lm -llog -lz
include $(BUILD_SHARED_LIBRARY)
$(call import-module, android/support)
