#include <stdio.h>
#include <vector>
#include <istream>

#include "onyx_zip_file_stream.h"
#include "ZipArchive.h"
#include "log.h"


int zipFileGetBlock(void* param,
                    unsigned long position,
                    unsigned char* pBuf,
                    unsigned long size)
{
    OnyxZipFileStream *fileStream = (OnyxZipFileStream *)param;
    return fileStream->requestBytes((size_t)position, pBuf, (size_t)size);
}

OnyxZipFileStream::OnyxZipFileStream(const std::string &url, const std::string &zip_password)
    : m_zip_password(zip_password)
{
    LOGI("constructor of OnyxZipFileStream, url: %s", url.data());
    m_url = url;
}

OnyxZipFileStream::~OnyxZipFileStream()
{
}

bool OnyxZipFileStream::open()
{
    FILE* fp = fopen(m_url.data(), "rb");
    if (fp == NULL)
    {
        LOGE("could not open file %s", m_url.data());
        return false;
    }
    fclose(fp);

    static ZipArchive::Ptr archive = ZipFile::Open(m_url);
    pArchiveEntry = archive->GetEntry(0);
    if (pArchiveEntry == NULL) {
        LOGE("could not get the entry of zip file %s", m_url.data());
        return false;
    }

    if (pArchiveEntry->IsPasswordProtected())
    {
        pArchiveEntry->SetPassword(m_zip_password);
    }

    totalSize = pArchiveEntry->GetSize();
    return true;
}

int OnyxZipFileStream::getSize() {
    return totalSize;
}

// offset and size is the value after decompressed.
int OnyxZipFileStream::requestBytes(size_t offset, unsigned char * pBuffer, size_t size)
{
    size_t remained = totalSize - offset;
    int actualSize = (size <= remained) ? size : remained;
    int readSize = offset + actualSize;
    char * readBuffer = new char[readSize];

    std::istream *contentStream = pArchiveEntry->GetDecompressionStream();
    contentStream->read(readBuffer, readSize);
    pBuffer = (unsigned char *)readBuffer + offset;
    return actualSize;
}

void OnyxZipFileStream::close()
{
    if (pArchiveEntry != NULL) {
        pArchiveEntry->CloseDecompressionStream();
    }
}
