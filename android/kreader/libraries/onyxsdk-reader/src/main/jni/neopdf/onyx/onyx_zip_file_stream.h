#ifndef ONYX_ZIP_FILE_STREAM_H
#define ONYX_ZIP_FILE_STREAM_H

#include <string>
#include "ZipFile.h"


extern int zipFileGetBlock(void* param,
                    unsigned long position,
                    unsigned char* pBuf,
                    unsigned long size);

// File stream that support zip with password protected file
class OnyxZipFileStream
{
public:
    OnyxZipFileStream(const std::string &url, const std::string &zip_password);
    ~OnyxZipFileStream();

    bool open();
    int requestBytes(size_t offset, unsigned char * pBuffer, size_t size);
    int getSize();
    void close();

private:

private:
    std::string m_url;
    std::string m_zip_password;
    size_t      totalSize;

    ZipArchiveEntry::Ptr pArchiveEntry;
};

#endif // ONYX_ZIP_FILE_STREAM_H
