
#ifndef ONYX_ADOBE_BACKEND_H_
#define ONYX_ADOBE_BACKEND_H_

#include "dp_all.h"
#include "onyx_client.h"
#include "onyx_drm_client.h"
#include "onyx_drm_command.h"
#include "types.h"
#include "scoped_ptr.h"
#include "shared_ptr.h"
#include "onyx_drm_callback.h"
#include <vector>

namespace onyx
{

class AdobeLibrary
{
public:
    AdobeLibrary(JNIEnv *env);
    ~AdobeLibrary();

public:
    static bool urlEncodeFileName(const char * str, QString & result);
    static dp::String resourceURL();
    static dp::String resourceURLByUser();
    static bool initLibrary(JNIEnv * env);

    static const std::string &getFontFace();
    static void setFontFace(const std::string &font);

    static std::string userStyleOnFly();

public:
    bool getMimeTypeForName(const QString &ext, QString &mime_type);
    long createDocument(const QString & url, const QString & password = QString(), const QString & zip_password = QString());
    bool closeDocument();

    bool gotoLocation(int page, const char * location);
    int  getPageCount();
    void pageNaturalSize(int page, double & width, double & height);
    bool drawPage(int page, const AndroidBitmapInfo & info, void * pixels, int displayLeft, int displayTop, int displayWidth, int displayHeight);
    bool drawPages(const AndroidBitmapInfo & info, void * pixels, int displayLeft, int displayTop, int displayWidth, int displayHeight);
    jobject createSentenceResult(JNIEnv * env, const dp::ref<dpdoc::Location>  & startLocation, const dp::ref<dpdoc::Location>  & endLocation, const char * text, bool endOfScreen, bool endOfDocument);
    jobject createHitTestResult(JNIEnv * env, dp::ref<dpdoc::Location>  start, dp::ref<dpdoc::Location>  end, const char * text);
    jobject hitTest(JNIEnv * env, float x, float  y, int type, jobject splitter);

    int getPageNumberOfScreenPoint(double screenX, double screenY);
    bool convertPointFromDeviceSpaceToDocumentSpace(double screenX, double screenY, double *docX, double *docY, int pageNum);
    bool convertPointFromDocumentSpaceToDeviceSpace(double docX, double docY, double *screenX, double *screenY, int pageNum);
    int getPageNumberByLocation(const char * locationStr);

    dp::String getText(const char *start, const char * end);
    dp::String getWord(dp::ref<dpdoc::Location> start, dp::ref<dpdoc::Location> end, dp::ref<dpdoc::Location> & newStart, dp::ref<dpdoc::Location> & newEnd);
    dp::String getWord2(JNIEnv * env, dp::ref<dpdoc::Location> start, dp::ref<dpdoc::Location> end, dp::ref<dpdoc::Location> & newStart, dp::ref<dpdoc::Location> & newEnd, jobject splitter);
    dp::String getPageText(int pageNumber);
    jobject getNextSentence(JNIEnv *env, jobject splitter, const std::string &start);

    int getRectanglesByLocation(dp::ref<dpdoc::Location> startLocation, dp::ref<dpdoc::Location> endLocation, std::vector<onyx::OnyxRectangle> & rects);
    int getRectangles(const char *start, const char * end, std::vector<onyx::OnyxRectangle> & rectangles);
    OnyxRectangle pageDisplayPosition(int number, double * left, double * top);
    OnyxRectangle pageDisplayRect(int number, double *left, double *top, double & natureWidth, double& natureHeight);
    int getDisplayRectangles(int page , int count, double * left, double * top,  std::vector<OnyxRectangle> & rectangles, std::vector<OnyxRectangle> & natureSize);
    int allVisiblePagesRectangle(JNIEnv * env, jobject list);

    bool prevScreen();
    bool nextScreen();

    bool setFontSize(double width, double height, double size);
    bool setPDFFontSize(double width, double height, double size);
    bool setePubFontSize(double width, double height, double size);
    bool setPageMode(int mode);

    OnyxMatrix getCurrentLocation();
    bool changeNavigationMatrix(double scale, double dx, double dy);
    bool setNavigationMatrix(double scale, double absX, double absY);
    void setAbortFlag(bool abort);
    bool getAbortFlag();

    bool searchNextOccur(JNIEnv * env, const char * pattern, bool caseSensitive, bool matchWholeWord, int startPageNumber, int endPageNumber,  jobject list);
    bool searchPrevOccur(JNIEnv * env, const char * pattern, bool caseSensitive, bool matchWholeWord, int startPageNumber, int endPageNumber,  jobject list);
    bool searchAllInPage(JNIEnv * env, const char * pattern, bool caseSensitive, bool matchWholeWord, int startPageNumber, int endPageNumber,  jobject list);

    bool hasTableOfContent();
    bool getTableOfContent(JNIEnv * env, jobject toc);
    dp::String getMetadataEntry(const char * tag);
    bool getMetadata(JNIEnv * env, jobject metadata, jobject tagList);

    int getTextLeftBoundary(JNIEnv * env, jobject splitter, const char *character, const char *left, const char *right);
    int getTextRightBoundary(JNIEnv * env, jobject splitter, const char *character, const char *left, const char *right);
    int isSentenceBoundaryBySplitter(JNIEnv * env, jobject splitter,  const char * screenText);
    int collectVisibleLinks(JNIEnv * env, jobject list);

    bool isLocationInCurrentScreen(JNIEnv * env, jstring location);

    bool isEpub();
    bool isPDF();

    bool setDisplayMargins(double left, double top, double right, double bottom);
    int getPageOrientation(int);

    bool registerDrmCallback(shared_ptr<HostDRMCallback> callback);
    bool fulfillByAcsm(const QString &acsm_path);
    bool activateDevice(const QString &user_id, const QString &password);
    QString getActivatedDRMAccount();
    bool deactivateDevice();


    void reportActivationDone();
    void reportAuthSignInDone();
    void reportDownloadDone();
    void reportLoanReturnDone();
    void reportFulfillDone();

    void reportDownloadProgress(double progress);

    void reportActivationFailed(const QString &message);
    void reportSignInFailed(const QString &message);
    void reportDownloadFailed(const QString &message);
    void reportLoanReturnFailed(const QString &message);
    void reportFulfillFailed(const QString &message);
    void reportDRMFulfillContentPath(const QString &fulfill_content_path);

private:
    void onDocumentCreated();
    bool release();
    bool buildTableOfContent(JNIEnv * env, jobject toc, dpdoc::TOCItem * parent);
    static void initDeviceInfo(JNIEnv *env);
    static void initDeviceEnv();

    void initDrmData();
    bool prepareDRMCommand();
    bool wrapFulfillByAcsm(const QString &acsm_path);
    void setActivationInProgress(bool in_progress);
    bool isActivationInProgress() { return is_activation_in_progress_; }
    void setFulfillmentInProgress(bool in_progress);
    bool isFulfillmentInProgress() { return is_fulfillment_in_progress_; }

public:
    static dp::String resourceURLString;
    static dp::String userResourceURLString;
    static dp::String deviceSerialString;
    static dp::String applicatonPrivateStorageString;
    static dp::String deviceNameString;

private:
    static std::string userFont;

    QString url;
    QString mimeType;
    base::scoped_ptr<OnyxAdobeClient> adobeClient;
    base::scoped_ptr<OnyxDocClient> docClient;
    base::scoped_ptr<OnyxRenderClient> renderClient;
    base::scoped_ptr<OnyxLibraryListener> libraryListener;
    bool ready;

    dpdoc::Document *doc;
    dpdoc::Renderer *renderer;

    dpdev::Device *device_;
    shared_ptr<OnyxDRMClient> drm_client_;
    shared_ptr<HostDRMCallback> drmCallback;
    base::scoped_ptr<OnyxDRMCommand> data_;
    QString fulfillment_path_;
    bool can_continue_processing_;
    bool is_activation_in_progress_;
    bool is_fulfillment_in_progress_;
};

};


#endif
