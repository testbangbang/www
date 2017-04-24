#include <stdio.h>
#include <vector>

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

OnyxZipFileStream::OnyxZipFileStream(const std::string &path, const std::string &password)
    : zipPassword(password)
    , decompressionContent(NULL)
{
    filePath = path;
}

OnyxZipFileStream::~OnyxZipFileStream()
{
}

bool OnyxZipFileStream::open()
{
    FILE* fp = fopen(filePath.data(), "rb");
    if (fp == NULL)
    {
        LOGE("could not open file %s", filePath.data());
        return false;
    }
    fclose(fp);

    static ZipArchive::Ptr archive = ZipFile::Open(filePath);
    pArchiveEntry = archive->GetEntry(0);
    if (pArchiveEntry == NULL) {
        LOGE("could not get the entry of zip file %s", filePath.data());
        return false;
    }

    if (pArchiveEntry->IsPasswordProtected())
    {
        pArchiveEntry->SetPassword(zipPassword);
    }

    totalSize = pArchiveEntry->GetSize();

    contentStream = pArchiveEntry->GetDecompressionStream();
    decompressionContent = new char[totalSize];
    contentStream->read(decompressionContent, totalSize);
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
    unsigned char * src = (unsigned char *)decompressionContent + offset;
    memcpy(pBuffer, src, actualSize);
    return actualSize;
}

void OnyxZipFileStream::close()
{
    if (decompressionContent != NULL) {
        free(decompressionContent);
    }
    if (pArchiveEntry != NULL) {
        pArchiveEntry->CloseDecompressionStream();
    }
}
