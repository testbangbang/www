#ifndef ONYX_ZIP_FILE_STREAM_H
#define ONYX_ZIP_FILE_STREAM_H

#include <string>
#include <istream>
#include "ZipArchive.h"
#include "ZipFile.h"


extern int zipFileGetBlock(void* param,
                    unsigned long position,
                    unsigned char* pBuf,
                    unsigned long size);

// File stream that support zip with password protected file
class OnyxZipFileStream
{
public:
    OnyxZipFileStream(const std::string &path, const std::string &password);
    ~OnyxZipFileStream();

    bool open();
    int requestBytes(size_t offset, unsigned char * pBuffer, size_t size);
    int getSize();
    void close();
    void setSeekPos(size_t pos);
    int getSeekPos();

private:

private:
    std::string filePath;
    std::string zipPassword;
    size_t      totalSize;
    size_t      seekPos;

    ZipArchive::Ptr pArchive;
    ZipArchiveEntry::Ptr pArchiveEntry;

};

#endif // ONYX_ZIP_FILE_STREAM_H
