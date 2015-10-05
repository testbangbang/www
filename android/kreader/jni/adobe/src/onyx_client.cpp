
#include "onyx_client.h"
#include "onyx_adobe_backend.h"
#include "onyx_data_stream.h"
#include "onyx_file_stream.h"
#include "onyx_zip_file_stream.h"
#include "uft_trace.h"

dp::String getDeviceNameAndroid() {
    return onyx::AdobeLibrary::deviceNameString;
}

dp::String getDeviceSerialAndroid() {
    return onyx::AdobeLibrary::deviceSerialString;
}

dp::String getApplicationPrivateStorage() {
    return onyx::AdobeLibrary::applicatonPrivateStorageString;
}

namespace {

dp::String convertResToFileUrl(const dp::String &resFolder, const dp::String &resUrl)
{
    char tmp[2048] = { 0 };
    ::strcpy(tmp, resFolder.utf8());
    ::strcat(tmp, resUrl.utf8()+7); // skip "res:///"

    return dp::String(tmp);
}

dpio::Stream *readResourceUrl(const dp::String &resFolder, const dp::String &resUrl, int capabilities)
{
    dp::String u = convertResToFileUrl(resFolder, resUrl);
    dpio::Partition * partition = dpio::Partition::findPartitionForURL(u);
    if (partition)
    {
        return partition->readFile(u, NULL, capabilities);
    }

    return NULL;
}

}

namespace onyx {

OnyxAdobeClient::OnyxAdobeClient()
 : abort(false)
 , passwordRequired(false) {
}

bool OnyxAdobeClient::setDisplaySize(int width, int height) {
    return false;
}

QSizeF OnyxAdobeClient::displaySize() {
    return QSizeF(1440, 1080);
}

OnyxSurface & OnyxAdobeClient::surfaceInstance() {
    static OnyxSurface instance(0, 0);
    return instance;
}

void OnyxAdobeClient::setAbortFlag(bool a) {
    abort = a;
}

bool OnyxAdobeClient::getAbortFlag() {
    return abort;
}

bool OnyxAdobeClient::canContinueProcessing(){
   return !abort;
}

void OnyxAdobeClient::holdOnProcessing(bool) {

}

void OnyxAdobeClient::reportPositionChanged() {

}

void OnyxAdobeClient::reportFontSizeChanging(double) {

}

void OnyxAdobeClient::reportLoadingState(int) {

}

void OnyxAdobeClient::clearPasswordRequiredFlag() {
    passwordRequired = false;
}

bool OnyxAdobeClient::isPasswordRequired() {
    return passwordRequired;
}

void OnyxAdobeClient::reportRequestDocumentPassword() {
    passwordRequired = true;
}

void OnyxAdobeClient::requestRepaint(int xMin, int yMin, int xMax, int yMax) {

}

void OnyxAdobeClient::reportNavigateToURL(const QString & url, const QString & target) {

}

void OnyxAdobeClient::reportExternalHyperlinkClicked(const QString & url) {

}

void OnyxAdobeClient::reportInternalNavigation() {

}

void OnyxAdobeClient::reportWorkflowDone(unsigned int workflows, const QByteArray & data) {

}

void OnyxAdobeClient::reportWorkflowProgress(unsigned int workflows, const QString &, double) {

}

void OnyxAdobeClient::reportWorkflowError(unsigned int workflows, const QString &) {

}

void OnyxAdobeClient::reportDocumentWrittenWithLicense(const QString &url)
{

}

void OnyxAdobeClient::reportLibraryItemAdded(dplib::Library *library, const dp::ref<dplib::ContentRecord> &record)
{

}

OnyxDocClient::OnyxDocClient(OnyxAdobeClient *client, const std::string &zipPassword)
: m_partition(0)
, adobe_client_(client)
, m_zip_password_(zipPassword)
, state_(0)
, abort(false)
{
}

OnyxDocClient::~OnyxDocClient() {
    if (m_partition) {
        dpio::Partition::releaseFileSystemPartition(m_partition);
    }
}

int OnyxDocClient::getInterfaceVersion() {
    return 1;
}

dpio::Stream * OnyxDocClient::getResourceStream(const dp::String& url,
                                                unsigned int capabilities) {
    // never use qDebug(), see comment in OnyxResProvider::getResourceStream()
    // fprintf(stdout, "OnyxDocClient::getResourceStream, url: %s\n", url.utf8());
    if (::strncmp(url.utf8(), "data:", 5) == 0) {
        return dpio::Stream::createDataURLStream(url, NULL, NULL);
    }

    // resources: user stylesheet, fonts, hyphenation dictionaries and resources they references
    if (::strncmp(url.utf8(), "res:///", 7 ) == 0 && url.length() < 1024) {
        // try read user folder first
        dp::String res_folder_by_user = AdobeLibrary::resourceURLByUser();
        dpio::Stream *stream = readResourceUrl(res_folder_by_user, url, capabilities);
        if (stream) {
            return stream;
        }

        dp::String res_folder = AdobeLibrary::resourceURL();
        if (res_folder.isNull()) {
            LOGE("res folder is empty!");
            return NULL;
        }

        if (::strncmp(url.utf8(), "res:///userStyle.css", 1024) == 0) {
            std::string style_content(AdobeLibrary::userStyleOnFly());
            if (!style_content.empty()) {
                return new OnyxDataStream(style_content, capabilities, "Content-Type", "text/css");
            }
        }

        char tmp[2048] = {0};
        ::strcpy(tmp, res_folder.utf8());
        ::strcat(tmp, url.utf8()+7);
        dp::String u = dp::String(tmp);

        dpio::Partition * partition = dpio::Partition::findPartitionForURL(u);
        if (partition) {
            return partition->readFile(u, NULL, capabilities);
        }
        return NULL;
    }

    if (m_zip_password_.size() > 0) {
        OnyxZipFileStream *tmp = new OnyxZipFileStream(url, m_zip_password_);
        if (tmp->open()) {
            return tmp;
        }
        delete tmp;
    } else {
        OnyxFileStream *tmp = new OnyxFileStream(url, capabilities);
        if (tmp->open()) {
            return tmp;
        }
        delete tmp;
    }

    return NULL;

    /*

    //
    // Look only on the local file system and removable partions. No network fetches
    //
    m_partition = dpio::Partition::findPartitionForURL( url );
    if( m_partition != NULL )
    {
    return m_partition->readFile(url, NULL, capabilities);
    }

    return NULL;
    */
}

void OnyxDocClient::setAbortFlag(bool a) {
    abort = a;
}

bool OnyxDocClient::canContinueProcessing(int kind) {
    return !abort;
}

int OnyxDocClient::loadingState() {
    return state_;
}

void OnyxDocClient::reportLoadingState(int state) {
    LOGI("Loading state %d", state);
    state_ = state;
    adobe_client_->reportLoadingState(state);
}

void OnyxDocClient::reportDocumentError(const dp::String& errorString) {
    LOGE("Document error %s", errorString.utf8());
}

void OnyxDocClient::reportErrorListChange() {
}

void OnyxDocClient::requestLicense(const dp::String& type,
                                   const dp::String& resourceId,
                                   const dp::Data& request) {
}

void OnyxDocClient::requestDocumentPassword() {
    adobe_client_->reportRequestDocumentPassword();
}

void OnyxDocClient::documentSerialized() {
}

void * OnyxDocClient::getOptionalInterface(const char * name) {
    return NULL;
}


OnyxRenderClient::OnyxRenderClient(OnyxAdobeClient *adobe_client)
: adobe_client_(adobe_client) {
}

int OnyxRenderClient::getInterfaceVersion() {
    return 1;
}

double OnyxRenderClient::getUnitsPerInch() {
    return 72.0;
}

void OnyxRenderClient::requestRepaint(int xMin,
                                      int yMin,
                                      int xMax,
                                      int yMax) {
    adobe_client_->requestRepaint(xMin, yMin, xMax, yMax);
}

void OnyxRenderClient::navigateToURL(const dp::String& url,
                                     const dp::String& target) {
    adobe_client_->reportNavigateToURL(url.utf8(), target.utf8());
}

void OnyxRenderClient::reportMouseLocationInfo(const dpdoc::MouseLocationInfo& info) {
    if (!info.linkURL.isNull()) {
        QString url(info.linkURL.utf8());
        adobe_client_->reportExternalHyperlinkClicked(url);
    }
}

void OnyxRenderClient::reportInternalNavigation() {
    adobe_client_->reportInternalNavigation();
}

void OnyxRenderClient::reportDocumentSizeChange() {
}

void OnyxRenderClient::reportHighlightChange(int highlightType) {
}

void OnyxRenderClient::reportRendererError(const dp::String& errorString) {
    LOGE("renderer error %s", errorString.utf8());
}

void OnyxRenderClient::finishedPlaying() {
}

void * OnyxRenderClient::getOptionalInterface(const char * name) {
    return NULL;
}

OnyxResProvider::OnyxResProvider(dp::String res_folder, bool v)
: resFolder(res_folder)
, verbose(v) {
}

OnyxResProvider::~OnyxResProvider() {
}


dpio::Stream * OnyxResProvider::getResourceStream(const dp::String& url_in,
                                                  unsigned int capabilities) {
    dp::String url = url_in;
    if (verbose) {
        // once a time i was like to know in which thread this method is called, so i printed it by prefixing with a QThread::currentThreadId(),
        // which caused a REALLY strange "segmentation fault" when document was needing to load a fair amount of resources, such as fonts,
        // error msg looks like:
        // *********************************
        // * 3077695824 OnyxResProvider::getResourceStream, url:  "res:///fonts/AdobeHeitiStd-Regular.otf"
        // * 3077695824 OnyxResProvider::getResourceStream, url:  "res:///fonts/AdobeHeitiStd-Regular.otf"
        // * 3077695824 OnyxResProvider::getResourceStream, url:  "res:///fonts/AdobeHeitiStd-Regular.otf"
        // * 160505408 OnyxResProvider::getResourceStream, url:  "res:///fonts/AdobeHeitiStd-Regular.otf"
        // * Segmentation fault
        // *********************************
        // which can be fixed by just throwing away the fucking #QThread::currentThreadId()#
        // leave a comment here for your sake
        // ps, one word more, read the fucking document!
        // ps 2, even qDebug() can cause problem, be careful of using Qt method in callback functions

        // fprintf(stdout, "OnyxResProvider::getResourceStream, url: %s\n", url.utf8());
        LOGI("OnyxResProvider::getResourceStream, url: %s\n", url.utf8());
    }

    if (::strncmp(url.utf8(), "data:", 5) == 0) {
        return dpio::Stream::createDataURLStream(url, NULL, NULL);
    }

    // resources: user stylesheet, fonts, hyphenation dictionaries and resources they references
    if (::strncmp(url.utf8(), "res:///", 7 ) == 0 && url.length() < 1024 && !resFolder.isNull()) {
        char tmp[2048] = {0};
        ::strcpy(tmp, resFolder.utf8());
        ::strcat(tmp, url.utf8()+7);
        url = dp::String(tmp);
    }

    if (true) {
        LOGI("OnyxResProvider::getResourceStream, result url: %s\n", url.utf8());
    }
    dpio::Partition * partition = dpio::Partition::findPartitionForURL(url);

    if (verbose) {
        LOGI("findPartitionForURL result %p ", partition);
    }
    if (partition != NULL) {
        return partition->readFile(url, NULL, capabilities);
    }
    return NULL;
}


}




