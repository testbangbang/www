#ifndef ONYX_DATA_STREAM_H
#define ONYX_DATA_STREAM_H

#include <string>
#include <vector>

#include "dp_all.h"

class OnyxDataStream : public dpio::Stream
{
public:
    OnyxDataStream(const std::string &data, const unsigned int capabilities,
                   const std::string &propertyName, const std::string &propertyValue);

    virtual void release();
    virtual void setStreamClient(dpio::StreamClient * receiver);
    virtual unsigned int getCapabilities();
    virtual void requestInfo();
    virtual void requestBytes( size_t offset, size_t len );
    virtual void reportWriteError( const dp::String& error );

private:
    std::string m_data;
    unsigned int m_caps;
    std::string m_property_name;
    std::string m_property_value;
    dpio::StreamClient* m_client;
};

#endif // ONYX_MEMORY_STREAM_H
