#include <stdio.h>
#include <vector>

#include "onyx_zip_file_stream.h"
#include "ZipArchive.h"
#include "ZipFile.h"

static ZipArchive::Ptr     globalArchive;
static ZipArchiveEntry::Ptr    entry;
char * whole = 0;

namespace onyx
{

OnyxZipFileStream::OnyxZipFileStream(const dp::String& url, const QString& zip_password)
    : m_zip_password(zip_password)
    , m_content_stream(0)
{
    LOGI("constructor of OnyxZipFileStream");
    QString data(url.utf8());
    QString prefix("file://");
    if(data.substr(0, prefix.size()) == prefix) {
        m_url = data.substr(prefix.size()).c_str();
    } else {
        m_url = url;
    }
}

OnyxZipFileStream::~OnyxZipFileStream()
{
}

bool OnyxZipFileStream::open()
{
    FILE* fp = fopen(m_url.utf8(), "rb");
    if (fp == NULL)
    {
        printf("could not open file %s\n", m_url.utf8());
        return false;
    }
    fclose(fp);

    globalArchive = ZipFile::Open(m_url.utf8());
    entry = globalArchive->GetEntry(0);
    if (entry == NULL) {
        printf("could not get the entry of zip file %s\n", m_url.utf8());
        return false;
    }

    if (entry->IsPasswordProtected())
    {
        entry->SetPassword(m_zip_password);
    }

    m_len = entry->GetSize();
    m_content_stream = entry->GetDecompressionStream();
    whole = new char[m_len];
    m_content_stream->read(whole, m_len);
    LOGI("OnyxZipFileStream::open, file size is: %d, file name: %s",  m_len, entry->GetName().c_str());
    return true;
}

void OnyxZipFileStream::release()
{
}

void OnyxZipFileStream::setStreamClient(dpio::StreamClient * receiver)
{
    m_client = receiver;
}

unsigned int OnyxZipFileStream::getCapabilities()
{
    //
    // Our file stream is synchronous and random access
    //
    return dpio::SC_SYNCHRONOUS | dpio::SC_BYTE_RANGE;
}

void OnyxZipFileStream::requestInfo()
{
    //
    // Requesting Mimetype and Length
    //
    m_client->propertyReady("Content-Type", "application/epub+zip");
    m_client->totalLengthReady(m_len);
    m_client->propertiesReady();
}

// offset and len is the value after decompressed.
void OnyxZipFileStream::requestBytes(size_t offset, size_t len)
{
    LOGI("OnyxZipFileStream %p, requestBytes, offset: %d, len: %d", this, offset, len);

    int remained = m_len - offset;
    int n = (len <= remained) ? len : remained;
    unsigned char * p = (unsigned char *)whole + offset;
    dp::Data data(p, n);
    m_client->bytesReady(offset, data, false);
    LOGI("OnyxZipFileStream::requestBytes, length %d", n);
}

void OnyxZipFileStream::reportWriteError(const dp::String& error)
{
}

void OnyxZipFileStream::deepCopy(unsigned char *buffer, const char *src, size_t len)
{
    for (int i=0; i<len; i++) {
        buffer[i] = (unsigned char)src[i];
    }
}

}   // namespace onyx
