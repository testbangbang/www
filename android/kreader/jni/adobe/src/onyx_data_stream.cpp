#include "onyx_data_stream.h"

#include <cstdio>

OnyxDataStream::OnyxDataStream(const std::string &data, const unsigned int capabilities,
                               const std::string &propertyName, const std::string &propertyValue)
    : m_data(data), m_caps(capabilities),
      m_property_name(propertyName), m_property_value(propertyValue) {
}

void OnyxDataStream::release() {
}

void OnyxDataStream::setStreamClient(dpio::StreamClient *receiver) {
    m_client = receiver;
}

unsigned int OnyxDataStream::getCapabilities() {
    //
    // Our data stream is synchronous and random access
    //
    return dpio::SC_SYNCHRONOUS | dpio::SC_BYTE_RANGE;
}

void OnyxDataStream::requestInfo() {
    //
    // Requesting Mimetype and Length
    //
    m_client->totalLengthReady(m_data.size());
    m_client->propertyReady(dp::String(m_property_name.c_str()),
                            dp::String(m_property_value.c_str()));
    m_client->propertiesReady();
}

void OnyxDataStream::requestBytes(size_t offset, size_t len) {
    int left = m_data.size() - offset;
    int n = len <= left ? len : left;

    const unsigned char *p = (const unsigned char*)m_data.data();

    dp::Data data(p + offset, n);
    m_client->bytesReady(offset, data, offset + n >= m_data.size());
}

void OnyxDataStream::reportWriteError(const dp::String &error) {
}
