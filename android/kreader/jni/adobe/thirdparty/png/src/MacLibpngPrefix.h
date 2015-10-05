// MacLibpngPrefix.h

#include <MacTypes.h>   // includes this so we don't have a prob with redefining true and false.

#ifndef MACOS
#define MACOS
#define OutputDebugString(x)
#endif

#define PNG_NO_STDIO
#define PNG_NO_CONSOLE_IO

// There are many other options we might be able to turn off as well
//	libpng 1.0.6 has a dependency for png_set_unknown_chunks which is excluded by this def
//#define PNG_NO_WRITE_ANCILLARY_CHUNKS

#define PNG_NO_WRITE_TRANSFORMS
#define PNG_NO_WRITE_WEIGHTED_FILTER
#define PNG_NO_WRITE_FLUSH
