
#ifndef ONYX_FILESTREAM_H_
#define ONYX_FILESTREAM_H_

#include "dp_all.h"

namespace onyx
{

class OnyxFileStream : public dpio::Stream
{
public:
    OnyxFileStream(const dp::String& url, unsigned int minCaps);
    ~OnyxFileStream();

    bool open();

    virtual void release();
    virtual void setStreamClient(dpio::StreamClient * receiver);
    virtual unsigned int getCapabilities();
    virtual void requestInfo();
    virtual void requestBytes( size_t offset, size_t len );
    virtual void reportWriteError( const dp::String& error );

private:
    unsigned int        m_caps;
    dpio::StreamClient* m_client;
    size_t              m_len;
    dp::String          m_url;
};

};

#endif // _ONYX_FILESTREAM_H_
