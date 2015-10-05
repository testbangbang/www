

#if defined(WIN) || defined(WIN32) || WIN_ENV



#include "jconfig.vc"



#elif defined(MAC) || defined(MACINTOSH)



#include "jconfig.xc"



#elif defined (SOLARIS) && defined (UNIX_ENV)



#include "jconfig.sol"



#elif defined (LINUX_OS)



#include "jconfig.lx"



#else



	#error



#endif

