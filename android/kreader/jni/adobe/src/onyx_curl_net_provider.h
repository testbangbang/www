#ifndef ONYX_CURL_NET_PROVIDER_H
#define ONYX_CURL_NET_PROVIDER_H

#include "dp_drm.h"
#include "dp_net.h"
#include "dp_io.h"
#include "dp_utils.h"

namespace onyx
{
class OnyxCurlNetProvider : public dpnet::NetProvider
{
public:
    OnyxCurlNetProvider();

    virtual ~OnyxCurlNetProvider();

    /**
     *  Initiate a raw network request (HTTP or HTTPS, GET or POST). When request is processed,
     *  StreamClient methods should be called with the result obtained from the server. Note that for best
     *  performance this call should not block waiting for result from the server. Instead the communication
     *  to the server should happen on a separate thread or through event-based system.
     */
    virtual dpio::Stream * open( const dp::String& method,
                                 const dp::String& url,
                                 dpio::StreamClient * client,
                                 unsigned int cap,
                                 dpio::Stream * data_to_post = 0 );
};


}

#endif // ONYX_CURL_NET_PROVIDER_H
