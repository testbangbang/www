LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

UNRAR_PATH := $(LOCAL_PATH)/unrar

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../utils

LOCAL_MODULE    := neo_unrar

LOCAL_SRC_FILES := unrar_jni.cpp unrar/dll.cpp unrar/rar.cpp unrar/strlist.cpp unrar/strfn.cpp unrar/pathfn.cpp unrar/smallfn.cpp unrar/global.cpp unrar/file.cpp unrar/filefn.cpp unrar/filcreat.cpp \
    unrar/archive.cpp unrar/arcread.cpp unrar/unicode.cpp unrar/system.cpp unrar/isnt.cpp unrar/crypt.cpp unrar/crc.cpp unrar/rawread.cpp unrar/encname.cpp \
    unrar/resource.cpp unrar/match.cpp unrar/timefn.cpp unrar/rdwrfn.cpp unrar/consio.cpp unrar/options.cpp unrar/errhnd.cpp unrar/rarvm.cpp unrar/secpassword.cpp \
    unrar/rijndael.cpp unrar/getbits.cpp unrar/sha1.cpp unrar/sha256.cpp unrar/blake2s.cpp unrar/hash.cpp unrar/extinfo.cpp unrar/extract.cpp unrar/volume.cpp \
    unrar/list.cpp unrar/find.cpp unrar/unpack.cpp unrar/headers.cpp unrar/threadpool.cpp unrar/rs16.cpp unrar/cmddata.cpp unrar/ui.cpp \
    unrar/filestr.cpp unrar/recvol.cpp unrar/rs.cpp unrar/scantree.cpp unrar/qopen.cpp 
    
LOCAL_STATIC_LIBRARIES := libutils

LOCAL_CFLAGS := -O2
LOCAL_CPPFLAGS := -fexceptions -D_FILE_OFFSET_BITS=64 -D_LARGEFILE_SOURCE -DRARDLL -Dgetpass\(a\)=a -DIGNORE_NDK_MBS_ERROR
LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)
