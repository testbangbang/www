#ifndef ONYX_ZIP_FILE_STREAM_H
#define ONYX_ZIP_FILE_STREAM_H

#include "dp_all.h"
#include <istream>
#include "types.h"


namespace onyx
{

// File stream that support zip with password protected file
class OnyxZipFileStream : public dpio::Stream
{
public:
    OnyxZipFileStream(const dp::String& url, const QString& zip_password);
    ~OnyxZipFileStream();

    virtual bool open();

    virtual void release();
    virtual void setStreamClient(dpio::StreamClient * receiver);
    virtual unsigned int getCapabilities();
    virtual void requestInfo();
    virtual void requestBytes( size_t offset, size_t len );
    virtual void reportWriteError( const dp::String& error );

private:
    void deepCopy(unsigned char *buffer, const char *src, size_t len);

private:
    QString             m_zip_password;
    dpio::StreamClient* m_client;
    size_t              m_len;
    dp::String          m_url;


    std::istream*  m_content_stream;
};

}   // namespace onyx

#endif // ONYX_ZIP_FILE_STREAM_H
