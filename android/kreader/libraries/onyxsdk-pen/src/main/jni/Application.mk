# When we build for google play, we build 4 different apk's, each with
# a different version, by uncommenting one of the pairs of lines below.
# Suppose our base version is X:

# Version X: armeabi
#APP_PLATFORM=android-8
#APP_ABI := armeabi

# Version X+1: armeabi-v7a (Much faster due to the availability of hardware
# FP, but cannot be run in the emulator).
APP_PLATFORM=android-10
APP_ABI := armeabi-v7a

# Version X+2: x86 (Requires android-9, so a change needs to be made in
# AndroidManifest.xml too)
#APP_PLATFORM=android-9
#APP_ABI := x86

# Version X+3: mips (Requires android-9, so a change needs to be made in
# AndroidManifest.xml too)
#APP_PLATFORM=android-9
#APP_ABI := mips

APP_OPTIM := release
APP_STL := gnustl_static

# If the ndk is r8b then workaround bug by uncommenting the following line
#NDK_TOOLCHAIN_VERSION=4.4.3

# If the ndk is newer than r8c, try using clang.
#NDK_TOOLCHAIN_VERSION=clang3.1


#APP_CFLAGS += -std=c++11
APP_CPPFLAGS := -fexceptions  -std=c++11

