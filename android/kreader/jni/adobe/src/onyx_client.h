

#ifndef ONYX_PDF_CLIENT_H_
#define ONYX_PDF_CLIENT_H_

#include <stdio.h>
#include <string.h>

#include "dp_all.h"
#include "types.h"
#include "onyx_surface.h"


namespace onyx
{

/// To receive message from adobe library and forward messages to java application.
class OnyxAdobeClient
{
public:
    OnyxAdobeClient();
    virtual bool setDisplaySize(int width, int height);
    virtual QSizeF displaySize();

    virtual OnyxSurface &surfaceInstance();

    virtual bool canContinueProcessing();
    virtual void holdOnProcessing(bool);

    virtual void reportPositionChanged();
    virtual void reportFontSizeChanging(double);

    virtual void reportLoadingState(int);

    virtual void reportRequestDocumentPassword();
    virtual void requestRepaint(int xMin, int yMin, int xMax, int yMax);

    virtual void reportNavigateToURL(const QString & url, const QString & target);
    virtual void reportExternalHyperlinkClicked(const QString & url);
    virtual void reportInternalNavigation();

    virtual void reportWorkflowDone(unsigned int workflows, const QByteArray & data);
    virtual void reportWorkflowProgress(unsigned int workflows, const QString &, double);
    virtual void reportWorkflowError(unsigned int workflows, const QString &);
    virtual void reportDocumentWrittenWithLicense(const QString& url);

    virtual void reportLibraryItemAdded(dplib::Library *library, const dp::ref<dplib::ContentRecord> &record);

public:
    void setAbortFlag(bool a);
    bool getAbortFlag();
    void clearPasswordRequiredFlag();
    bool isPasswordRequired();

private:
    volatile bool abort;
    bool passwordRequired;
};

// To receive document message.
class OnyxDocClient : public dpdoc::DocumentClient
{
public:
    OnyxDocClient(OnyxAdobeClient *client, const std::string &zipPassword);
    ~OnyxDocClient();

    virtual int getInterfaceVersion();
    virtual dpio::Stream * getResourceStream(const dp::String& url, unsigned int capabilities);
    virtual bool canContinueProcessing(int kind);
    virtual void reportLoadingState(int state);
    virtual void reportDocumentError(const dp::String& errorString);
    virtual void reportErrorListChange();
    virtual void requestLicense(const dp::String& type, const dp::String& resourceId, const dp::Data& request);
    virtual void requestDocumentPassword();
    virtual void documentSerialized();
    virtual void * getOptionalInterface(const char * name);

public:
    int loadingState();
    void setAbortFlag(bool a);

private:
    dpio::Partition* m_partition;
    OnyxAdobeClient* adobe_client_;
    std::string m_zip_password_;
    int state_;
    volatile bool abort;
};


class OnyxRenderClient : public dpdoc::RendererClient
{
public:
    OnyxRenderClient(OnyxAdobeClient *adobe_client);

public:
    virtual int getInterfaceVersion();
    virtual double getUnitsPerInch();
    virtual void requestRepaint( int xMin, int yMin, int xMax, int yMax );
    virtual void navigateToURL( const dp::String& url, const dp::String& target );
    virtual void reportMouseLocationInfo( const dpdoc::MouseLocationInfo& info );
    virtual void reportInternalNavigation();
    virtual void reportDocumentSizeChange();
    virtual void reportHighlightChange( int highlightType );
    virtual void reportRendererError(const dp::String& errorString);
    virtual void finishedPlaying();

    void * getOptionalInterface( const char * name );
private:
    OnyxAdobeClient *adobe_client_;
};



// impl res provider.
class OnyxResProvider : public dpres::ResourceProvider
{
public:
    OnyxResProvider(dp::String res_folder, bool verbose);
    virtual ~OnyxResProvider();

    /**
     *  Request a global resource download from a given url with a Stream with at least
     *  given capabilities. Security considerations are responsibilities of the host.
     *  If NULL is returned, request is considered to be failed.
     */
    virtual dpio::Stream * getResourceStream(const dp::String& url_in, unsigned int capabilities);

private:
    dp::String resFolder;
    bool verbose;
};

class OnyxLibraryListener : public dplib::LibraryListener {
public:
    OnyxLibraryListener(OnyxAdobeClient *client)
        : adobe_client_(client) {

    }


    // LibraryListener interface
public:
    void libraryLoaded(dplib::Library *library) {
        LOGI("LibraryListener::libraryLoaded");
    }

    void newContentRecordCreated( dplib::Library * library, const dp::ref<dplib::ContentRecord>& record ) {
        LOGI("LibraryListener::newContentRecordCreated");
        adobe_client_->reportLibraryItemAdded(library, record);
    }

    void contentRecordAdded(dplib::Library *library, const dp::ref<dplib::ContentRecord> &record) {
        LOGI("LibraryListener::contentRecordAdded");
        adobe_client_->reportLibraryItemAdded(library, record);
    }

    void contentRecordRemoved(dplib::Library *library, const dp::ref<dplib::ContentRecord> &record) {
        LOGI("LibraryListener::contentRecordRemoved");
    }

    void contentRecordChanged(dplib::Library *library, const dp::ref<dplib::ContentRecord> &record) {
        LOGI("LibraryListener::contentRecordChanged");
    }

    void annotationChanged(dplib::Library *library, const dp::ref<dplib::ContentRecord> &record) {
        LOGI("LibraryListener::annotationChanged");
    }

    void thumbnailChanged(dplib::Library *library, const dp::ref<dplib::ContentRecord> &record) {
        LOGI("LibraryListener::thumbnailChanged");
    }

    void contentTagAdded(dplib::Library *library, const dp::ref<dplib::ContentTag> &tag) {
        LOGI("LibraryListener::contentTagAdded");
    }

    void contentTagRemoved(dplib::Library *library, const dp::ref<dplib::ContentTag> &tag) {
        LOGI("LibraryListener::contentTagRemoved");
    }

    void contentTagChanged(dplib::Library *library, const dp::ref<dplib::ContentTag> &tag) {
        LOGI("LibraryListener::contentTagChanged");
    }

    void libraryPartitionRemoved(dplib::Library *library) {
        LOGI("LibraryListener::libraryPartitionRemoved");
    }

    void libraryError(dplib::Library *library, const dp::String error) {
        LOGI("LibraryListener::libraryError");
    }

    OnyxAdobeClient *adobe_client_;
};


}

#endif // ONYX_PDF_BACKEND_H_
