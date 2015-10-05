LOCAL_PATH := $(call my-dir)

MY_LOCAL_PATH := $(LOCAL_PATH)
ADOBE_LIBS_ROOT := libs

include $(CLEAR_VARS)
LOCAL_MODULE := adept
LOCAL_SRC_FILES := $(ADOBE_LIBS_ROOT)/libadept.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := cryptopenssl
LOCAL_SRC_FILES := $(ADOBE_LIBS_ROOT)/libcryptopenssl.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := cts
LOCAL_SRC_FILES := $(ADOBE_LIBS_ROOT)/libcts.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := dp
LOCAL_SRC_FILES := $(ADOBE_LIBS_ROOT)/libdp.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := expat
LOCAL_SRC_FILES := $(ADOBE_LIBS_ROOT)/libexpat.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := fonts
LOCAL_SRC_FILES := $(ADOBE_LIBS_ROOT)/libfonts.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := hobbes
LOCAL_SRC_FILES := $(ADOBE_LIBS_ROOT)/libhobbes.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := hyphen
LOCAL_SRC_FILES := $(ADOBE_LIBS_ROOT)/libhyphen.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := jpeg
LOCAL_SRC_FILES := $(ADOBE_LIBS_ROOT)/libjpeg.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := mschema
LOCAL_SRC_FILES := $(ADOBE_LIBS_ROOT)/libmschema.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := png
LOCAL_SRC_FILES := $(ADOBE_LIBS_ROOT)/libpng.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := t3
LOCAL_SRC_FILES := $(ADOBE_LIBS_ROOT)/libt3.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := xml
LOCAL_SRC_FILES := $(ADOBE_LIBS_ROOT)/libxml.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := zip
LOCAL_SRC_FILES := $(ADOBE_LIBS_ROOT)/libzlib.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := ssl
LOCAL_SRC_FILES := $(ADOBE_LIBS_ROOT)/libssl.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := crypto
LOCAL_SRC_FILES := $(ADOBE_LIBS_ROOT)/libcrypto.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := curl
LOCAL_SRC_FILES := $(ADOBE_LIBS_ROOT)/libcurl.a
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
ONYX_SRC_ROOT := $(LOCAL_PATH)
ZIPLIB_SRC_ROOT := $(LOCAL_PATH)/../ZipLib
LOCAL_CXXFLAGS := -fPIC -std=c++11
LOCAL_MODULE    := onyx_pdf
LOCAL_C_INCLUDES := \
	$(ONYX_SRC_ROOT)/include \
    $(ONYX_SRC_ROOT)/src \
	$(ONYX_SRC_ROOT)/thirdparty \
    $(ONYX_SRC_ROOT)/thirdparty/zlib \
    $(ONYX_SRC_ROOT)/thirdparty/png/src \
    $(ONYX_SRC_ROOT)/thirdparty/libjpeg/sources \
    $(ONYX_SRC_ROOT)/thirdparty/curl/include \
    $(ZIPLIB_SRC_ROOT)

LOCAL_SRC_FILES := jni/adobe/onyx.cpp       \
                   $(ONYX_SRC_ROOT)/src/onyx_client.cpp   \
                   $(ONYX_SRC_ROOT)/src/onyx_data_stream.cpp \
                   $(ONYX_SRC_ROOT)/src/onyx_file_stream.cpp \
                   $(ONYX_SRC_ROOT)/src/onyx_zip_file_stream.cpp \
                   $(ONYX_SRC_ROOT)/src/onyx_adobe_backend.cpp \
                   $(ONYX_SRC_ROOT)/src/onyx_matrix.cpp \
                   $(ONYX_SRC_ROOT)/src/onyx_surface.cpp \
                   $(ONYX_SRC_ROOT)/src/onyx_adobe_timer.cpp \
                   $(ONYX_SRC_ROOT)/src/onyx_curl_net_provider.cpp \
                   $(ONYX_SRC_ROOT)/src/onyx_drm_client.cpp \
                   $(ONYX_SRC_ROOT)/src/onyx_drm_callback.cpp \
                   $(ONYX_SRC_ROOT)/src/onyx_drm_command.cpp

LOCAL_SRC_FILES := $(addprefix ../../, $(LOCAL_SRC_FILES))

# -ladept -lcryptopenssl -lcts -ldp -lexpat -lfonts -lhobbes -lhyphen -ljpeg -lpng -lmschema -lt3 -lxml -lzlib
# mschema hobbes t3 adept dp adobe-expat xml cts hyphen fonts adobe-jpeg  adobe-png adobe-zlib  adobe-cryptopenssl mschema adobe-ssl adobe-crypto

LOCAL_WHOLE_STATIC_LIBRARIES += hobbes adept dp mschema \
                          xml cryptopenssl fonts \
                          expat cts hyphen bzip2 lzma zlib ZipLib


LOCAL_STATIC_LIBRARIES += t3 png jpeg ssl crypto zlib curl
#LOCAL_STATIC_LIBRARIES := dp  xml expat zlib #zlib mschema hobbes t3 adept dp expat  ctl hyphen fonts jpeg zlib png zlib cryptopenssl ssl crypto

LOCAL_LDLIBS := -llog -lz -ljnigraphics
include $(BUILD_SHARED_LIBRARY)
