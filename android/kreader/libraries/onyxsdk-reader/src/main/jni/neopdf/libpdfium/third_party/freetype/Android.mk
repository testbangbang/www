LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE:= libft2-new

LOCAL_SRC_FILES:= \
    src/base/ftbbox.c \
    src/base/ftbitmap.c \
    src/base/ftfntfmt.c \
    src/base/ftfstype.c \
    src/base/ftglyph.c \
    src/base/ftlcdfil.c \
    src/base/ftstroke.c \
    src/base/fttype1.c \
    src/base/ftbase.c \
    src/base/ftsystem.c \
    src/base/ftinit.c \
    src/base/ftgasp.c \
    src/base/ftmm.c \
    src/cff/cff.c \
    src/cid/type1cid.c \
    src/psaux/psaux.c \
    src/pshinter/pshinter.c \
    src/psnames/psmodule.c \
    src/psnames/psnames.c \
    src/raster/raster.c \
    src/sfnt/sfnt.c \
    src/smooth/smooth.c \
    src/truetype/truetype.c \
    src/type1/type1.c

LOCAL_C_INCLUDES += \
   $(LOCAL_PATH)/include    \
   $(LOCAL_PATH)/../../../common/libpng  \
   $(LOCAL_PATH)/../../../common/zlib

LOCAL_CFLAGS += -W -Wall
LOCAL_CFLAGS += -fPIC -DPIC
LOCAL_CFLAGS += "-DDARWIN_NO_CARBON"
LOCAL_CFLAGS += "-DFT2_BUILD_LIBRARY"

LOCAL_STATIC_LIBRARIES += libpng_ndk libz

# the following is for testing only, and should not be used in final builds
# of the product
#LOCAL_CFLAGS += "-DTT_CONFIG_OPTION_BYTECODE_INTERPRETER"

LOCAL_CFLAGS += -O2



include $(BUILD_STATIC_LIBRARY)

