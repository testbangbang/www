
#include <stdio.h>
#include <vector>

#include "onyx_file_stream.h"
#include "types.h"


namespace onyx
{

OnyxFileStream::OnyxFileStream(const dp::String& url, unsigned int minCaps)
: m_caps(minCaps)
{
    LOGI("Open document %s", url.utf8());
    QString data(url.utf8());
    QString prefix("file://");
    if(data.substr(0, prefix.size()) == prefix) {
        m_url = data.substr(prefix.size()).c_str();
    } else {
        m_url = url;
    }


    // directly use of url.utf8() will cause encoding problem, so wrap with QString::fromUtf8()
    //QUrl u(QString::fromUtf8(url.utf8()));
    //m_url = dp::String(u.toLocalFile().toLocal8Bit().constData());

}

OnyxFileStream::~OnyxFileStream() {
}

bool OnyxFileStream::open() {
    FILE* fp = fopen(m_url.utf8(), "rb");
    if (fp == NULL) {
        printf("could not open file %s\n", m_url.utf8());
        return false;
    }

    fseek(fp, 0, SEEK_END);
    m_len = ftell(fp);
    fclose(fp);
    return true;
}

void OnyxFileStream::release() {
}

void OnyxFileStream::setStreamClient(dpio::StreamClient * receiver) {
    m_client = receiver;
}

unsigned int OnyxFileStream::getCapabilities() {
    //
    // Our file stream is synchronous and random access
    //
    return dpio::SC_SYNCHRONOUS | dpio::SC_BYTE_RANGE;
}

void OnyxFileStream::requestInfo() {
    //
    // Requesting Mimetype and Length
    //
    m_client->propertyReady("Content-Type", "application/epub+zip");
    m_client->totalLengthReady(m_len);
    m_client->propertiesReady();
}

void OnyxFileStream::requestBytes(size_t offset, size_t len) {
    std::vector<unsigned char> buffer(len);
    FILE* fp = fopen(m_url.utf8(), "rb");
    if (fp == NULL) {
        printf("Could not open file %s\n", m_url.utf8());
        return;
    }
    fseek(fp, offset, SEEK_SET);
    fread(&buffer[0], 1, len, fp);
    fclose(fp);

    dp::Data data(&buffer[0], buffer.size());
    m_client->bytesReady(offset, data, false);
}

void OnyxFileStream::reportWriteError(const dp::String& error) {
    LOGI("Report write error %s", error.utf8());
}

}

