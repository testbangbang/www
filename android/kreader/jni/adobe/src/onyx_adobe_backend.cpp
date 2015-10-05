
#include <stdlib.h>
#include <stdio.h>
#include <algorithm>
#include <sstream>
#include <iostream>
#include <fstream>
#include <string>
#include <map>
#include <cctype>
#include <math.h>
#include "uft_string.h"


#include "onyx_adobe_backend.h"

namespace {
static const char * deviceInfoClassName = "com/onyx/reader/ReaderDeviceInfo";
static const char * entryClassName = "com/onyx/reader/ReaderTableOfContentEntry";
static const char * readerLocationRangeClassName = "com/onyx/reader/ReaderLocationRange";
static const char * hitTestResultClassName = "com/onyx/reader/ReaderSelection";
static const char * metadataClassName = "com/onyx/reader/ReaderDocumentMetadata";
static const char * readerLinkClassName = "com/onyx/reader/ReaderLink";
static const char * readerPageInfoClassName = "com/onyx/reader/ReaderPageInfo";
static const char * sentenceResultClassName = "com/onyx/reader/ReaderSentenceResult";
static const char * splitterClassName = "com/onyx/reader/utils/ReaderTextSplitter";

static const int TOUCH = 2;
static const int SELECT_WORD = 3;

static QString epubMimeType = "application/epub+zip";
static QString pdfMimeType = "application/pdf";

static bool MATRIX_DEBUG = false;
static bool DRM_DEBUG = false;
static const QString DRM_FILE_DOWNLOAD_TITLE = "DWS_DOWNLOAD";

static bool isDRMInfoValid(const QString &user_id, const QString &password) {
    return user_id.length() > 0 && password.length() > 0;
}

static bool isBlankString(const dp::String string) {
    return (string.utf8() == NULL || strlen(string.utf8()) <= 0);
}

static bool isNonBlankString(const dp::String string) {
    return (string.utf8() != NULL && strlen(string.utf8()) > 0);
}

// check if source contains target or not.
static bool contains(const dp::String source, const dp::String target) {
    const char * s = strstr(source.utf8(), target.utf8());
    return (s != NULL);
}


static jdoubleArray doubleArrayFromRectList(JNIEnv * env, std::vector<onyx::OnyxRectangle> rectangles) {
    const int COUNT = 4;
    if (rectangles.size() <= 0) {
        return NULL;
    }
    int length = rectangles.size();
    int size = length * COUNT;
	jdouble * buffer = new double[size];
	for(int i = 0; i < length; ++i) {
	    dpdoc::Rectangle item = rectangles[i];
	    buffer[i * COUNT] = item.xMin;
        buffer[i * COUNT + 1] = item.yMin;
        buffer[i * COUNT + 2] = item.xMax - item.xMin + 1;
        buffer[i * COUNT + 3] = item.yMax - item.yMin + 1;
	}

	// copy from buffer to double array.
	jdoubleArray array = env->NewDoubleArray(size);
    env->SetDoubleArrayRegion(array, 0, size, buffer);
    return array;
}

dp::String workflowToString(int workflow)
{
    switch (workflow) {
    case dpdrm::DW_SIGN_IN: return "DW_SIGN_IN";
    case dpdrm::DW_AUTH_SIGN_IN: return "DW_AUTH_SIGN_IN";
    case dpdrm::DW_ADD_SIGN_IN: return "DW_ADD_SIGN_IN";
    case dpdrm::DW_ACTIVATE: return "DW_ACTIVATE";
    case dpdrm::DW_FULFILL: return "DW_FULFILL";
    case dpdrm::DW_ENABLE_CONTENT: return "DW_ENABLE_CONTENT";
    case dpdrm::DW_LOAN_RETURN: return "DW_LOAN_RETURN";
    case dpdrm::DW_UPDATE_LOANS: return "DW_UPDATE_LOANS";
    case dpdrm::DW_DOWNLOAD: return "DW_DOWNLOAD";
    case dpdrm::DW_NOTIFY: return "DW_NOTIFY";
    }
    return "";
}

static bool removeOriginActivationRecord()
{
    return ::remove("/flash/.adobe-digital-editions/activation.xml") == 0;
}

class AdobeLibraryClient : public onyx::OnyxAdobeClient {

public:
    AdobeLibraryClient(onyx::AdobeLibrary &library)
        : library_(library)
    {
    }

    virtual void reportWorkflowDone(unsigned int workflow, const QByteArray & follow_up)
    {
        if (DRM_DEBUG) LOGI("Workflow %s done.", workflowToString(workflow).utf8());
        if (follow_up.size() > 0) {
            if (DRM_DEBUG) LOGI("follow up: %s", static_cast<unsigned char const*>(follow_up.data()));
        }
        
        switch (workflow) {
        case dpdrm::DW_ACTIVATE: {
            library_.reportActivationDone();
            break;
        }
        case dpdrm::DW_AUTH_SIGN_IN:
            library_.reportAuthSignInDone();
            break;
        case dpdrm::DW_DOWNLOAD: {
            library_.reportDownloadDone();
            break;
        }
        case dpdrm::DW_LOAN_RETURN:
            library_.reportLoanReturnDone();
            break;
        case dpdrm::DW_FULFILL: {
            library_.reportFulfillDone();
            break;
        }
        default:
            if (DRM_DEBUG) LOGI("ignore workflow type.");
            break;
        }
    }

    virtual void reportWorkflowProgress(unsigned int workflow, const QString &title, double progress)
    {
        if (DRM_DEBUG) LOGI("reportWorkflowProgress, workflow %s: %s, %f",  workflowToString(workflow).utf8(), title.c_str(), progress);
        switch (workflow)
        {
            case dpdrm::DW_DOWNLOAD: {
                if (title.find(DRM_FILE_DOWNLOAD_TITLE) >= 0) {
                    library_.reportDownloadProgress(progress);
                }
            }
            break;
            default:
                if (DRM_DEBUG) LOGI("ignore workflow type.");
            break;
        }
    }

    virtual void reportWorkflowError(unsigned int workflow, const QString &error)
    {
        if (DRM_DEBUG) LOGI("Workflow %s error %s.", workflowToString(workflow).utf8(), error.c_str());
        switch (workflow) {
        case dpdrm::DW_ACTIVATE: {
            library_.reportActivationFailed(error);
            break;
        }
        case dpdrm::DW_AUTH_SIGN_IN: {
            library_.reportSignInFailed(error);
            break;
        }
        case dpdrm::DW_DOWNLOAD: {
            library_.reportDownloadFailed(error);
            break;
        }
        case dpdrm::DW_LOAN_RETURN:
            library_.reportLoanReturnFailed(error);
            break;
        case dpdrm::DW_FULFILL: {
            library_.reportFulfillFailed(error);
            break;
        }
        default:
            if (DRM_DEBUG) LOGI("other workflow types, no need to handle.");
            break;
        }
    }

    virtual void reportLibraryItemAdded(dplib::Library *library, const dp::ref<dplib::ContentRecord> &record)
    {
        std::string path(dp::String::urlDecode(record->getContentURL()).utf8());
        path = path.substr(7); // sub string from "file://"
        library_.reportDRMFulfillContentPath(path);
    }


private:
    onyx::AdobeLibrary &library_;

};

}

namespace dpdev {

void deviceRegisterPrimaryAndroid();
void deviceRegisterExternalAndroid();

}

namespace onyx
{

std::string AdobeLibrary::userFont;

// TODO, move to dedicated file
static const char * getUtf8String(JNIEnv * env, jstring string) {
    if (string != NULL) {
        return env->GetStringUTFChars(string, NULL);
    }
    return NULL;
}

static jobject createReaderLocationRange(JNIEnv * env, const char * start, int sn, const char * end, int en) {
    jclass cls = env->FindClass(readerLocationRangeClassName);
    if (cls == 0) {
        LOGE("Could not find class: %s", readerLocationRangeClassName);
        return 0;
    }

    jmethodID mid = env->GetStaticMethodID(cls, "rangeFromString", "(Ljava/lang/String;ILjava/lang/String;I)Lcom/onyx/reader/ReaderLocationRange;");
    if (mid == 0) {
        LOGE("Find method rangeFromString failed");
        return 0;
    }

    jstring jstr_start = env->NewStringUTF(start);
    jstring jstr_end = env->NewStringUTF(end);
    jobject result = env->CallStaticObjectMethod(cls, mid, jstr_start, sn, jstr_end, en);
    if (result == 0) {
        LOGE("OOM: env->CallStaticObjectMethod failed");
    }

    env->DeleteLocalRef(jstr_start);
    env->DeleteLocalRef(jstr_end);
    env->DeleteLocalRef(cls);
    return result;
}

jobject AdobeLibrary::createHitTestResult(JNIEnv * env, dp::ref<dpdoc::Location>  start, dp::ref<dpdoc::Location>  end, const char * text) {
    jclass cls = env->FindClass(hitTestResultClassName);
    if (cls == 0) {
        LOGE("Could not find class: %s", hitTestResultClassName);
        return 0;
    }

    jmethodID mid = env->GetStaticMethodID(cls, "create", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[D)Lcom/onyx/reader/ReaderSelection;");
    if (mid == 0) {
        LOGE("Find method result failed");
        env->DeleteLocalRef(cls);
        return 0;
    }

    std::vector<onyx::OnyxRectangle> rects;
    getRectanglesByLocation(start, end, rects);
    jdoubleArray array = doubleArrayFromRectList(env, rects);

    jstring jstrStart = env->NewStringUTF(start->getBookmark().utf8());
    jstring jstrEnd = env->NewStringUTF(end->getBookmark().utf8());
    jstring jstrText = env->NewStringUTF(text);

    jobject result = env->CallStaticObjectMethod(cls, mid, jstrStart, jstrEnd, jstrText, array);
    if (result == 0) {
        LOGE("OOM: env->CallStaticObjectMethod failed");
    }
    env->DeleteLocalRef(jstrStart);
    env->DeleteLocalRef(jstrEnd);
    env->DeleteLocalRef(jstrText);
    env->DeleteLocalRef(cls);
    return result;
}

jobject AdobeLibrary::createSentenceResult(JNIEnv * env, const dp::ref<dpdoc::Location> & startLocation, const dp::ref<dpdoc::Location> & endLocation, const char * text, bool endOfScreen, bool endOfDocument) {
    jobject readerSelectionObj = createHitTestResult(env, startLocation, endLocation, text);

    jclass cls = env->FindClass(sentenceResultClassName);
    if (cls == 0) {
        LOGE("Could not find class: %s", sentenceResultClassName);
        return 0;
    }
    jmethodID mid = env->GetStaticMethodID(cls, "create", "(Lcom/onyx/reader/ReaderSelection;ZZ)Lcom/onyx/reader/ReaderSentenceResult;");
    if (mid == 0) {
        LOGE("Get create method id failed");
        return 0;
    }
    jobject result = env->CallStaticObjectMethod(cls, mid, readerSelectionObj, endOfScreen, endOfDocument);
    if (result == 0) {
        LOGE("env->CallStaticObjectMethod failed for createSentenceResult");
    }
    return result;
}

static bool addObjectToList(JNIEnv * env, jobject object, jobject list) {
    jclass listClass = env->FindClass("java/util/List");
    if( listClass == NULL ) {
        LOGE("Can't Find Class java/util/List");
        return false;
    }

    jmethodID addId = env->GetMethodID(listClass, "add", "(Ljava/lang/Object;)Z");
    if( addId == NULL ) {
        LOGE("Can't get MethodID for add");
        return false;
    }

    env->CallBooleanMethod(list, addId, object);
    env->DeleteLocalRef(listClass);
    return true;
}

static jobject createPageInfoObject(JNIEnv * env, int pn, dp::ref<dpdoc::Location> location, double scale, int displayLeft, int displayTop, int displayRight, int displayBottom, float width, float height, int pageLeft, int pageTop) {
    jclass cls = env->FindClass(readerPageInfoClassName);
    if (cls == 0) {
        LOGE("Could not find class: %s", readerPageInfoClassName);
        return 0;
    }

    jmethodID mid = env->GetStaticMethodID(cls, "createInfo", "(ILjava/lang/String;DIIIIIIII)Lcom/onyx/reader/ReaderPageInfo;");
    if (mid == 0) {
        LOGE("Find method result failed");
        env->DeleteLocalRef(cls);
        return 0;
    }

    const char * data = "";
    if (location && location->getBookmark().utf8() != NULL) {
        data = location->getBookmark().utf8();
    }
    jstring object = env->NewStringUTF(data);
    jobject result = env->CallStaticObjectMethod(cls, mid, pn, object, scale, displayLeft, displayTop, displayRight, displayBottom, (int)width, (int)height, pageLeft, pageTop);
    if (result == 0) {
        LOGE("OOM: env->CallStaticObjectMethod failed");
    }
    env->DeleteLocalRef(object);
    env->DeleteLocalRef(cls);
    return result;
}

static void addRangeToList(JNIEnv * env, dpdoc::Range range, jobject list) {
    jobject rangeObject = createReaderLocationRange(env, range.beginning->getBookmark().utf8(),
                                                         (int)range.beginning->getPagePosition(),
                                                         range.end->getBookmark().utf8(),
                                                         (int)range.end->getPagePosition());
    addObjectToList(env, rangeObject, list);
    env->DeleteLocalRef(rangeObject);
}

static dp::String getDeviceInfo(JNIEnv * env, const char * entry) {
    dp::String info;
    jclass deviceInfoClass = env->FindClass(deviceInfoClassName);
    if (deviceInfoClass == NULL) {
        LOGE("Can't Find Class %s", deviceInfoClassName);
        return info;
    }

    jmethodID getMethod = env->GetStaticMethodID(deviceInfoClass, "infoEntry", "(Ljava/lang/String;)Ljava/lang/String;");
    if (getMethod == NULL) {
        LOGE("Can't get MethodID for get");
        env->DeleteLocalRef(deviceInfoClass);
        return info;
    }

    jstring key = env->NewStringUTF(entry);
    jstring value = (jstring)env->CallStaticObjectMethod(deviceInfoClass, getMethod, key);
    env->DeleteLocalRef(key);
    env->DeleteLocalRef(deviceInfoClass);
    return getUtf8String(env, value);
}

dp::String AdobeLibrary::resourceURLString;
dp::String AdobeLibrary::userResourceURLString;
dp::String AdobeLibrary::deviceSerialString;
dp::String AdobeLibrary::applicatonPrivateStorageString;
dp::String AdobeLibrary::deviceNameString;

AdobeLibrary::AdobeLibrary(JNIEnv *env) {
    adobeClient.reset(new AdobeLibraryClient(*this));
    data_.reset(new OnyxDRMCommand());
    initLibrary(env);
    initDrmData();
}

AdobeLibrary::~AdobeLibrary() {
}

// URL-encode UTF8 string, adding file:/// and replacing \ to /
bool AdobeLibrary::urlEncodeFileName(const char * str,
                                     QString & result) {
    size_t len = 0;
    const char * p = str;
    while( true ) {
        char c = *(p++);
        if( c == '\0' ) {
            break;
        }
        len++;
        if( c < ' ' || (unsigned char)c > '~' || c == '%' || c == '+' ) {
            len += 2;
        }
    }

    bool winShare = str[0] == '\\' && str[1] == '\\';
    bool unixAbs = !winShare && (str[0] == '/' || str[1] == '\\');
    char * url = new char[len+( winShare? 6 : ( unixAbs ? 8 : 9 ) )];
    if (winShare) {
        ::strcpy( url, "file:" );
    } else if( unixAbs ) {
        ::strcpy( url, "file://" );
    } else {
        ::strcpy( url, "file:///" );
    }

    char * t = url + ::strlen(url);
    p = str;

    while( true ) {
        char c = *(p++);
        if( c == '\0' )
            break;
        if( c < ' ' || (unsigned char)c > '~' || c == '%' || c == '+' ) {
            sprintf( t, "%%%02X", (unsigned char)c );
            t += 3;
        } else if( c == ' ' ) {
            *(t++) = '+';
        } else if( c == '\\' ) {
            *(t++) = '/';
        } else {
            *(t++) = c;
        }
    }
    *(t++) = '\0';

    result = url;
    delete [] url;
    return true;
}

void AdobeLibrary::initDeviceInfo(JNIEnv *env) {
    resourceURLString = getDeviceInfo(env, "resourceURL");
    userResourceURLString = getDeviceInfo(env, "userResourceURL");
    deviceSerialString = getDeviceInfo(env, "deviceSerial");
    applicatonPrivateStorageString = getDeviceInfo(env, "applicationPrivateStorage");
    deviceNameString = getDeviceInfo(env, "deviceName");
}

void AdobeLibrary::initDeviceEnv() {
    setenv("ADOBE_DE_MOBILE", "1", 1);
    setenv("ADEPT_DEVICE_TYPE", "mobile", 1);
    setenv("ADOBE_DE_ROOT_FOLDER", "/", 1);
    setenv("ADOBE_DE_DOC_FOLDER", "file:///mnt/sdcard/", 1);
    setenv("ADOBE_RESOURCE_FOLDER", resourceURL().utf8(), 1);
    setenv("ADEPT_DEVICE_NAME", deviceNameString.utf8(), 1);
    setenv("ADEPT_ACTIVATION_FILE", "/mnt/sdcard/.adobe-digital-editions/device.xml", 1);
}

void AdobeLibrary::initDrmData()
{
    data_->init();

    libraryListener.reset(new OnyxLibraryListener(adobeClient.get()));
    dpdev::DeviceProvider *device_provider = dpdev::DeviceProvider::getProvider(0);
    if (device_provider) {
        device_ = device_provider->getDevice(0);
        if (device_ != 0) {
            if (DRM_DEBUG) LOGI("initDrmData, current partition: %s", device_->getPartition(0)->getRootURL().utf8());
            dplib::Library *lib = dplib::Library::getPartitionLibrary(device_->getPartition(0));
            lib->addListener(libraryListener.get());
            if (DRM_DEBUG) LOGI("initDrmData, library loaded: %d", lib->isLoaded());
        } else {
            LOGW("initDrmData, No Device Implementation");
        }
    } else {
        LOGW("initDrmData, No Device Provider Implementation");
    }
}

bool AdobeLibrary::prepareDRMCommand()
{
    dpdev::DeviceProvider *device_provider = dpdev::DeviceProvider::getProvider(0);
    if (device_provider == 0) {
        if (DRM_DEBUG) LOGW("No Device Provider Implementation");
        return false;
    }

    device_ = device_provider->getDevice(0);
    if (device_ == 0) {
        if (DRM_DEBUG) LOGW("No Device Implementation");
        return false;
    }

    if (0 != drm_client_) {
        drm_client_.reset();
    }

    drm_client_.reset(new OnyxDRMClient(adobeClient.get(), device_));
    if (data_->processor() != 0) {
        data_->processor()->release();
    }
    data_->setProcessor(dpdrm::DRMProvider::getProvider()->createDRMProcessor(
                            (dpdrm::DRMProcessorClient*)(drm_client_.get()), device_));
    return true;
}

bool AdobeLibrary::wrapFulfillByAcsm(const QString &acsm_path)
{
    if (acsm_path.length() <= 0) {
        return false;
    }

    if (!prepareDRMCommand()) {
        LOGW("prepare DRM command failed.");
        return false;
    }

    if(data_->processor()->getActivations().length() == 0) {
        LOGW("Warning : Device is not activated");
        return false;
    }

    std::string acsm_string;
    std::ifstream infile;
    infile.open(acsm_path.c_str());
    std::string line_string;
    while (!infile.eof()) {
        getline(infile, line_string);
        acsm_string.append(line_string);
    }
    infile.close();

    if (acsm_string.length() <= 0) {
        LOGW("read acsm file failed: %s", acsm_path.c_str());
        return false;
    }
    dp::String content_data(acsm_string.c_str());
    data_->processor()->initWorkflows(dpdrm::DW_FULFILL | dpdrm::DW_DOWNLOAD | dpdrm::DW_NOTIFY, content_data);
    LOGI("Start fulfill...");
    data_->processor()->startWorkflows(dpdrm::DW_FULFILL | dpdrm::DW_DOWNLOAD | dpdrm::DW_NOTIFY);
    size_t count = data_->processor()->getFulfillmentItems().length();

    if (count > 0) {
        if (DRM_DEBUG) LOGI("fulfillment path: %s", fulfillment_path_.c_str());
    }
    return true;
}

void AdobeLibrary::setActivationInProgress(bool in_progress) {
    is_activation_in_progress_ = in_progress;
}

void AdobeLibrary::setFulfillmentInProgress(bool in_progress) {
    is_fulfillment_in_progress_ = in_progress;
}

// the callback will be called couple of times during start work flows.
// so we need a flag to indicate if it's in progress or not.
void AdobeLibrary::reportActivationDone() {
    if (isActivationInProgress()) {
        setActivationInProgress(false);
        drmCallback->reportActivationDone();
    }
}

void AdobeLibrary::reportAuthSignInDone() {
    if (isActivationInProgress()) {
        drmCallback->reportAuthSignInDone();
    }
}

void AdobeLibrary::reportDownloadDone() {
    if (isFulfillmentInProgress()) {
        setFulfillmentInProgress(false);
        drmCallback->reportDownloadDone();
    }
}

void AdobeLibrary::reportLoanReturnDone()  {
    if (isActivationInProgress()) {
        drmCallback->reportLoanReturnDone();
    }
}

void AdobeLibrary::reportFulfillDone() {
    if (isFulfillmentInProgress()) {
        drmCallback->reportFulfillDone();
    }
}

void AdobeLibrary::reportDownloadProgress(double progress) {
    drmCallback->reportDownloadProgress(progress);
}

void AdobeLibrary::reportActivationFailed(const QString &message) {
    if (isActivationInProgress()) {
        setActivationInProgress(false);
        drmCallback->reportActivationFailed(message);
    }
}

void AdobeLibrary::reportSignInFailed(const QString &message){
    if (isActivationInProgress()) {
        setActivationInProgress(false);
        drmCallback->reportSignInFailed(message);
    }
}

void AdobeLibrary::reportDownloadFailed(const QString &message) {
    if (isActivationInProgress()) {
        setActivationInProgress(false);
        drmCallback->reportDownloadFailed(message);
    }
}

void AdobeLibrary::reportLoanReturnFailed(const QString &message){
    if (isActivationInProgress()) {
        setActivationInProgress(false);
        drmCallback->reportLoanReturnFailed(message);
    }
}

void AdobeLibrary::reportFulfillFailed(const QString &message){
    if (isFulfillmentInProgress()) {
        setFulfillmentInProgress(false);
        drmCallback->reportFulfillFailed(message);
    }
}

void AdobeLibrary::reportDRMFulfillContentPath(const QString &fulfill_content_path)
{
    fulfillment_path_ = fulfill_content_path;
    drmCallback->reportDRMFulfillContentPath(fulfill_content_path);
}

dp::String AdobeLibrary::resourceURL() {
    QString url;
    if (urlEncodeFileName(resourceURLString.utf8(), url)) {
        return  url.c_str();
    }
    return dp::String();
}

dp::String AdobeLibrary::resourceURLByUser()
{
    QString url;
    if (urlEncodeFileName(userResourceURLString.utf8(), url)) {
        return url.c_str();
    }
    return dp::String();
}

bool AdobeLibrary::initLibrary(JNIEnv * env) {
    initDeviceInfo(env);
    initDeviceEnv();

    int code = dp::platformInit( dp::PI_DEFAULT );
    if( code != dp::IS_OK ) {
        LOGE("AdobeLibrary::initLibrary failed");
        return false;
    }

    dp::setVersionInfo("product", "Onyx Android Reader");
    dp::setVersionInfo("clientVersion", "Onyx Reader Android 1.0");
    dp::setVersionInfo("jpeg", "62");

    dp::cryptRegisterOpenSSL();
//    dp::deviceRegisterPrimary();
    dpdev::deviceRegisterPrimaryAndroid();

    if (!dpdev::isMobileOS()) {
        dp::deviceRegisterExternal();
    }
    dp::deviceMountRemovablePartitions();

    dp::documentRegisterEPUB();
    dp::documentRegisterPDF();

    static OnyxResProvider provider(resourceURL(), false);
    dpres::ResourceProvider::setProvider(&provider);
    //LOGI("AdobeLibrary::initLibrary finished.");

    return true;
}

const std::string &AdobeLibrary::getFontFace() {
    return userFont;
}

void AdobeLibrary::setFontFace(const std::string &font) {
    userFont = font;
}

std::string AdobeLibrary::userStyleOnFly() {
    std::ostringstream builder;
    if (!userFont.empty()) {
        builder << "@font-face { font-family: -ua-default; font-style: normal; font-weight: normal; src: url(\"" << userFont << "\") }\n";
        builder << "@font-face { font-family: -ua-default; font-style: normal; font-weight: normal; src: url(\"" << userFont << "\") }\n";
        builder << "@font-face { font-family: -ua-default; font-style: italic, oblique; font-weight: normal; src: url(\"" << userFont << "\") }\n";
        builder << "@font-face { font-family: -ua-default; font-style: normal; font-weight: bold; src: url(\"" << userFont << "\") }\n";
        builder << "@font-face { font-family: -ua-default; font-style: italic, oblique; font-weight: bold, normal; src: url(\"" << userFont << "\") }\n";
        builder << "@font-face { font-family: serif; font-style: normal; font-weight: normal; src: url(\"" << userFont << "\") }\n";
        builder << "@font-face { font-family: serif; font-style: italic, oblique; font-weight: normal; src: url(\"" << userFont << "\") }\n";
        builder << "@font-face { font-family: serif; font-style: normal; font-weight: bold; src: url(\"" << userFont << "\") }\n";
        builder << "@font-face { font-family: serif; font-style: italic, oblique; font-weight: bold, normal; src: url(\"" << userFont << "\") }\n";
    }
    builder << "\nbody\n{\n\n}";
    return builder.str();
}


bool AdobeLibrary::isEpub() {
    return mimeType == epubMimeType;
}

bool AdobeLibrary::isPDF() {
    return mimeType == pdfMimeType;
}

bool AdobeLibrary::getMimeTypeForName(const QString &path, QString &mime_type) {
    dp::String name = path.c_str();
    uft::String lowerName = name.uft().lowercase();
    if (lowerName.endsWith("psf") || lowerName.endsWith("ocf")) {
        mime_type = "application/psf";
        return true;
    }

    if (lowerName.endsWith("epub")) {
        mime_type = epubMimeType;
        return true;
    }

    if (lowerName.endsWith("pdf")) {
        mime_type = pdfMimeType;
        return true;
    }

    if (lowerName.endsWith("svg")) {
        mime_type = "image/svg+xml";
        return true;
    }

    if (lowerName.endsWith("etd")) {
        mime_type = "application/x-ebx";
        return true;
    }
    return false;
}

long AdobeLibrary::createDocument(const QString & path, const QString & password, const QString & zip_password) {
    url = path;
    if (!getMimeTypeForName(path, mimeType)) {
        LOGE("Could not get mime type for %s", path.c_str());
        return -100;
    }
    //LOGI("Mime type %s", mimeType.c_str());

    adobeClient->clearPasswordRequiredFlag();
    docClient.reset(new OnyxDocClient(adobeClient.get(), zip_password));
    renderClient.reset(new OnyxRenderClient(adobeClient.get()));

    doc = dpdoc::Document::createDocument(docClient.get(), mimeType.c_str());
    if (!doc) {
        LOGE("Could not create document for %s", path.c_str());
        release();
        return -100;
    }

    doc->setURL(path.c_str());
    if (password.length() > 0) {
        doc->setDocumentPassword(password.c_str());
    }
    renderer = doc->createRenderer(renderClient.get());
    LOGI("Renderer created for %s %p %p", path.c_str(), doc, renderer);

    if (renderer) {
        onDocumentCreated();
        ready = true;
        return 0;
    }

    if (adobeClient->isPasswordRequired()) {
        return -1;
    }
    return -100;
}

void AdobeLibrary::onDocumentCreated() {
    if (1) {
        //LOGI("Don't apply page decoration");
        dpdoc::PageDecoration decoration;
        decoration.shadowColor = decoration.borderColorTL = decoration.borderColorBR =  decoration.pageBackgroundColor;
        renderer->setPageDecoration(decoration);
    }
    renderer->showPageNumbers(false);
}

bool AdobeLibrary::closeDocument() {
    return release();
}

bool AdobeLibrary::release() {
    if (renderer) {
        renderer->release();
        renderer = 0;
    }

    if (doc) {
        doc->release();
        doc = 0;
    }

    docClient.reset(0);
    renderClient.reset(0);
    ready = false;
    url.clear();
    return true;
}

bool AdobeLibrary::gotoLocation(int page, const char * location) {
    if ((page < 0 || page >= getPageCount()) && (location == NULL)) {
        LOGE("goto location failed %d %p", page, location);
        return false;
    }

    dp::ref<dpdoc::Location> pos;
    if (location != NULL && strlen(location) > 0) {
        pos = doc->getLocationFromBookmark(location);
        if (!pos || pos->getBookmark().length() == 0) {
            pos = doc->getLocationFromPagePosition(page);
        }
    } else {
        pos = doc->getLocationFromPagePosition(page);
    }

    renderer->navigateToLocation(pos);
    return true;
}

int  AdobeLibrary::getPageCount() {
    return doc->getPageCount();
}

void AdobeLibrary::pageNaturalSize(int page, double & width, double & height) {
    PositionHolder p(renderer, true);
    OnyxRectangle rect;
    if (renderer->getPagingMode() != dpdoc::PM_HARD_PAGES) {
        renderer->getNaturalSize(&rect);
        width = rect.xMax;
        height = rect.yMax;
        if (MATRIX_DEBUG) {
            LOGI("pageNaturalSize with page mode %d %d %lf %lf %lf %lf", page, renderer->getPagingMode(), rect.xMin, rect.yMin, rect.xMax, rect.yMax);
        }
    }
    if (page < 0) {
        return;
    }

    renderer->setPagingMode(dpdoc::PM_HARD_PAGES);
    dp::ref<dpdoc::Location> pos = doc->getLocationFromPagePosition(page);
    renderer->navigateToLocation(pos);
    renderer->getNaturalSize(&rect);
    width = rect.xMax;
    height = rect.yMax;
    // use xMax instead of width.
    // LOGI("pageNaturalSize %lf %lf %lf %lf", rect.xMin, rect.yMin, rect.xMax, rect.yMax);
}

bool AdobeLibrary::drawPage(int page, const AndroidBitmapInfo & info, void * pixels,
                            int displayLeft, int displayTop, int displayWidth, int displayHeight, int left, int top, double scale) {
    adobeClient->surfaceInstance().attach(info, pixels, true);
    if (renderer->getPagingMode() != dpdoc::PM_FLOW_PAGES) {
        if (MATRIX_DEBUG) {
            LOGE("drawPage and set navigation matrix %d %d scale %lf", left, top, scale);
        }
        OnyxMatrix matrix;
        renderer->getNavigationMatrix(&matrix);
        matrix.setScale(scale);
        matrix.setXY(-left, -top);
        renderer->setNavigationMatrix(matrix);
    }
    renderer->paint(displayLeft, displayTop, displayWidth, displayHeight, &adobeClient->surfaceInstance());
    return true;
}

bool AdobeLibrary::drawPages(const AndroidBitmapInfo & info, void * pixels, int displayLeft, int displayTop, int displayWidth, int displayHeight,  int left, int top, double scale) {
    if (MATRIX_DEBUG) {
        LOGE("drawPages without setting navigation matrix %d %d scale %lf", left, top, scale);
    }
    adobeClient->surfaceInstance().attach(info, pixels, true);
    renderer->paint(displayLeft, displayTop, displayWidth, displayHeight, &adobeClient->surfaceInstance());
    return true;
}

jobject AdobeLibrary::hitTest(JNIEnv * env, float x, float  y, int type, jobject splitter) {
    dp::ref<dpdoc::Location> current = renderer->hitTest(x, y, dpdoc::HF_SELECT);
    if (type == TOUCH) {
        if (current) {
            return createHitTestResult(env, current, current, "");
        }
        return NULL;
    } else if (type == SELECT_WORD) {
        if (!current) {
            return NULL;
        }
        dp::ref<dpdoc::Location> start, end;
        dp::String word = getWord(current, current, start, end);
        return createHitTestResult(env, start, end, word.utf8());
    }
    return NULL;
}

static bool computePageRegionInScrollMode(dpdoc::Document *doc, dpdoc::Renderer *renderer, int page, OnyxRectangle *region)
{
    OnyxMatrix current_matrix;
    if (!renderer->getNavigationMatrix(&current_matrix)) {
        LOGW("computePageRegionInScrollMode, getNavigationMatrix of current failed");
        return false;
    }

    dp::ref<dpdoc::Location> location = doc->getLocationFromPagePosition(page);

    OnyxMatrix page_matrix;
    // restore to default matrix first
    renderer->setNavigationMatrix(page_matrix);

    renderer->navigateToLocation(location);
    if (!renderer->getNavigationMatrix(&page_matrix)) {
        LOGW("computePageRegionInScrollMode, getNavigationMatrix of page failed");
        renderer->setNavigationMatrix(current_matrix);
        return false;
    }

    renderer->setPagingMode(dpdoc::PM_HARD_PAGES);
    if (!renderer->getNaturalSize(region)) {
        renderer->setPagingMode(dpdoc::PM_SCROLL_PAGES);
        renderer->setNavigationMatrix(current_matrix);
    }

    double left = -page_matrix.e;
    double top = -page_matrix.f;
    double right = left + region->width();
    double bottom = top + region->height();
    region->setCoords(left, top, right, bottom);
    LOGI("computePageRegionInScrollMode, page region of %d: (%d, %d - %d, %d)", page, (int)left, (int)top, int(right), (int)bottom);

    renderer->setPagingMode(dpdoc::PM_SCROLL_PAGES);
    renderer->setNavigationMatrix(current_matrix);
    return true;
}

static bool convertPointFromScreenToPage(double screenX, double screenY, double *docX, double *docY, const OnyxRectangle &page, double scale) {
    if (screenX < page.left() || screenX > page.right() || screenY < page.top() || screenY > page.bottom()) {
        LOGW("convertPointFromScreenToPage, screen point (%f, %f) out of page region (%f, %f)-(%f, %f)", screenX, screenY, page.left(), page.top(), page.right(), page.bottom());
        return false;
    }
    *docX = (screenX - page.left()) / scale;
    *docY = (screenY - page.top()) / scale;
    return true;
}

static bool convertPointFromPageToScreen(double docX, double docY, double *screenX, double *screenY, const OnyxRectangle &page, double scale) {
    if (docX < 0 || docX > page.right() - page.left() || docY < 0 || docY > page.bottom() - page.top()) {
        LOGW("convertPointFromPageToScreen, doc point (%f, %f) out of page region (%f, %f)-(%f, %f)", docX, docY, page.left(), page.top(), page.right() - page.left(), page.bottom() - page.top());
        return false;
    }
    *screenX = (docX + page.left()) * scale;
    *screenY = (docY + page.top()) * scale;
    return true;
}

static std::map<int, OnyxRectangle> scrollModePageRegionCollection;

static bool getPageDocRegionInScrollMode(dpdoc::Document *doc, dpdoc::Renderer *renderer, int pageNum, OnyxRectangle *region) {
    std::map<int, OnyxRectangle>::iterator find = scrollModePageRegionCollection.find(pageNum);
    if (find != scrollModePageRegionCollection.end()) {
        *region = find->second;
    } else {
        if (!computePageRegionInScrollMode(doc, renderer, pageNum, region)) {
            LOGW("computePageRegionInScrollMode failed");
            return false;
        }
        scrollModePageRegionCollection.insert(std::pair<int, OnyxRectangle>(pageNum, *region));
    }
    return true;
}

static bool getPageScreenRegionInScrollMode(dpdoc::Document *doc, dpdoc::Renderer *renderer, int pageNum, OnyxRectangle *region) {
    OnyxMatrix matrix;
    if (!renderer->getNavigationMatrix(&matrix)) {
        LOGW("getPageScreenRegionInScrollMode, getNavigationMatrix failed");
        return false;
    }
    if (!getPageDocRegionInScrollMode(doc, renderer, pageNum, region)) {
        LOGW("getPageScreenRegionInScrollMode, getPageDocRegionInScrollMode failed");
        return false;
    }
    double left = region->left() * matrix.scale() + matrix.x();
    double top = region->top() * matrix.scale() + matrix.y();
    double right = left + region->width() * matrix.scale();
    double bottom = top + region->height() * matrix.scale();
    region->setCoords(left, top, right, bottom);
    return true;
}

static bool isPointInPageOfScrollMode(dpdoc::Document *doc, dpdoc::Renderer *renderer, int pageNum, double screenX, double screenY) {
    OnyxRectangle region;
    if (!getPageDocRegionInScrollMode(doc, renderer, pageNum, &region)) {
        LOGW("isPointInPageOfScrollMode, getPageDocRegionInScrollMode failed");
        return false;
    }

    OnyxMatrix matrix;
    if (!renderer->getNavigationMatrix(&matrix)) {
        LOGW("isPointInPageOfScrollMode, getNavigationMatrix failed");
        return false;
    }
    double left = region.left() * matrix.scale() + matrix.x();
    double top = region.top() * matrix.scale() + matrix.y();
    double right = left + region.width() * matrix.scale();
    double bottom = top + region.height() * matrix.scale();
    return left <= screenX && screenX <= right && top <= screenY && screenY <= bottom;
}

static int getPageNumberOfScreenPointInScrollMode(dpdoc::Document *doc, dpdoc::Renderer *renderer, double screenX, double screenY) {
    dpdoc::PageNumbers numbers;
    if (!renderer->getPageNumbersForScreen(&numbers)) {
        LOGW("getPageNumberOfScreenPointInScrollMode, getPageNumbersForScreen failed");
        return -1;
    }

    int begin = renderer->getScreenBeginning()->getPagePosition();
    int end = renderer->getScreenEnd()->getPagePosition();

    for (int i = begin; i <= end; i++) {
        if (isPointInPageOfScrollMode(doc, renderer, i, screenX, screenY)) {
            return i;
        }
    }

    LOGW("getPageNumberOfScreenPointInScrollMode: not found page");
    return -1;
}

int AdobeLibrary::getPageNumberOfScreenPoint(double screenX, double screenY)
{
    if (renderer->getPagingMode() == dpdoc::PM_HARD_PAGES) {
        return (int)renderer->getCurrentLocation()->getPagePosition();
    } else if (renderer->getPagingMode() == dpdoc::PM_SCROLL_PAGES) {
        return getPageNumberOfScreenPointInScrollMode(doc, renderer, screenX, screenY);
    }
    LOGW("getPageNumberOfScreenPoint failed: unknown paging mode");
    return -1;
}

static bool convertPointFromScreenToPageInScrollMode(dpdoc::Document *doc, dpdoc::Renderer *renderer, double screenX, double screenY, double *docX, double *docY, int pageNum) {
    OnyxMatrix matrix;
    if (!renderer->getNavigationMatrix(&matrix)) {
        LOGW("convertPointFromScreenToPageInScrollMode, getNavigationMatrix failed");
        return false;
    }
    OnyxRectangle region;
    if (!getPageScreenRegionInScrollMode(doc, renderer, pageNum, &region)) {
        LOGW("convertPointFromScreenToPageInScrollMode, getPageScreenRegionInScrollMode failed");
        return false;
    }
    return convertPointFromScreenToPage(screenX, screenY, docX, docY, region, matrix.scale());
}

static bool convertPointFromPageToScreenInScrollMode(dpdoc::Document *doc, dpdoc::Renderer *renderer, double docX, double docY, double *screenX, double *screenY, int pageNum) {
    OnyxMatrix matrix;
    if (!renderer->getNavigationMatrix(&matrix)) {
        LOGW("convertPointFromPageToScreenInScrollMode, getNavigationMatrix failed");
        return false;
    }
    OnyxRectangle region;
    if (!getPageDocRegionInScrollMode(doc, renderer, pageNum, &region)) {
        LOGW("convertPointFromScreenToPageInScrollMode, getPageDocRegionInScrollMode failed");
        return false;
    }
    if (!convertPointFromPageToScreen(docX, docY, screenX, screenY, region, matrix.scale())) {
        return false;
    }
    *screenX += matrix.x();
    *screenY += matrix.y();
    return true;
}

static bool convertPointFromScreenToPageInHardPagesMode(dpdoc::Document *doc, dpdoc::Renderer *renderer, double screenX, double screenY, double *docX, double *docY, int pageNum) {
    OnyxMatrix matrix;
    if (!renderer->getNavigationMatrix(&matrix)) {
        LOGW("convertPointFromScreenToPageInScrollMode, getNavigationMatrix failed");
        return false;
    }
    *docX = (screenX - matrix.x()) / matrix.scale();
    *docY = (screenY - matrix.y()) / matrix.scale();
    return true;
}

static bool convertPointFromPageToScreenInHardPagesMode(dpdoc::Document *doc, dpdoc::Renderer *renderer, double docX, double docY, double *screenX, double *screenY, int pageNum) {
    OnyxMatrix matrix;
    if (!renderer->getNavigationMatrix(&matrix)) {
        LOGW("convertPointFromPageToScreenInScrollMode, getNavigationMatrix failed");
        return false;
    }
    *screenX = (docX * matrix.scale()) + matrix.x();
    *screenY = (docY * matrix.scale()) + matrix.y();
    return true;
}

bool AdobeLibrary::convertPointFromDeviceSpaceToDocumentSpace(double screenX, double screenY, double *docX, double *docY, int pageNum)
{
    if (renderer->getPagingMode() == dpdoc::PM_SCROLL_PAGES) {
        return convertPointFromScreenToPageInScrollMode(doc, renderer, screenX, screenY, docX, docY, pageNum);
    } else if (renderer->getPagingMode() == dpdoc::PM_HARD_PAGES) {
        return convertPointFromScreenToPageInHardPagesMode(doc, renderer, screenX, screenY, docX, docY, pageNum);
    } else {
        return false;
    }
}

bool AdobeLibrary::convertPointFromDocumentSpaceToDeviceSpace(double docX, double docY, double *screenX, double *screenY, int pageNum)
{
    if (renderer->getPagingMode() == dpdoc::PM_SCROLL_PAGES) {
        return convertPointFromPageToScreenInScrollMode(doc, renderer, docX, docY, screenX, screenY, pageNum);
    } else if (renderer->getPagingMode() == dpdoc::PM_HARD_PAGES) {
        return convertPointFromPageToScreenInHardPagesMode(doc, renderer, docX, docY, screenX, screenY, pageNum);
    } else {
        return false;
    }
}

int AdobeLibrary::getPageNumberByLocation(const char * locationStr)
{
    dp::ref<dpdoc::Location> location = doc->getLocationFromBookmark(locationStr);
    return (int)location->getPagePosition();
}

dp::String AdobeLibrary::getText(const char *start, const char * end) {
    if (!start || !end) {
        return dp::String();
    }
    dp::ref<dpdoc::Location> startLocation = doc->getLocationFromBookmark(start);
    dp::ref<dpdoc::Location> endLocation = doc->getLocationFromBookmark(end);
    return doc->getText(startLocation, endLocation);
}

dp::String AdobeLibrary::getPageText(int pageNumber) {
    if (pageNumber >= getPageCount()) {
        return dp::String();
    }
    if (pageNumber < 0) {
        dp::ref<dpdoc::Location> startLocation = renderer->getScreenBeginning();
        dp::ref<dpdoc::Location> endLocation = renderer->getScreenEnd();
        return doc->getText(startLocation, endLocation);
    } else {
        dp::ref<dpdoc::Location> startLocation = doc->getLocationFromPagePosition(pageNumber);
        dp::ref<dpdoc::Location> endLocation;
        if (pageNumber + 1 >= getPageCount()) {
            endLocation = doc->getEnd();
        } else {
            endLocation = doc->getLocationFromPagePosition(pageNumber + 1);
        }
        return doc->getText(startLocation, endLocation);
    }
}

jobject AdobeLibrary::getNextSentence(JNIEnv *env, jobject splitter, const std::string &start) {
    std::string text;
    dp::String screenText;
    bool endOfScreen = false, endOfDocument = false;

    dp::ref<dpdoc::Location> endLocation;
    dp::ref<dpdoc::Location> startLocation = doc->getLocationFromBookmark(start.c_str());
    dp::ref<dpdoc::Location> screenBegin = renderer->getScreenBeginning();
    dp::ref<dpdoc::Location> screenEnd = renderer->getScreenEnd();
    dp::ref<dpdoc::Location> documentEnd = doc->getEnd();

    if (!startLocation || isBlankString(start.c_str())) {
        startLocation = screenBegin;
    }

    dpdoc::ContentIterator *iter = doc->getContentIterator(dpdoc::CV_TEXT, startLocation);
    if (iter != NULL) {
        while (1) {
            iter->next(0);
            endLocation = iter->getCurrentPosition();
            screenText = doc->getText(startLocation, endLocation);
            int ret = isSentenceBoundaryBySplitter(env, splitter, screenText.utf8());
            if (ret > 0 || screenEnd->compare(endLocation)  <= 0 || documentEnd->compare(endLocation) <= 0) {
                break;
            }
        }
        iter->release();
    }
    if (endLocation && (screenEnd->compare(endLocation)  <= 0 || documentEnd->compare(endLocation) <= 0)) {
        endOfScreen = true;
        if (documentEnd->compare(endLocation) <= 0) {
            endOfDocument = true;
        }
    }

    if (!endLocation) {
        endLocation = screenEnd;
    }
    // get the text.
    screenText = doc->getText(startLocation, endLocation);
    return createSentenceResult(env, startLocation, endLocation, screenText.utf8(), endOfScreen, endOfDocument);
}

dp::String AdobeLibrary::getWord2(JNIEnv * env,
                                  dp::ref<dpdoc::Location> start,
                                  dp::ref<dpdoc::Location> end,
                                  dp::ref<dpdoc::Location> & newStart,
                                  dp::ref<dpdoc::Location> & newEnd,
                                  jobject splitter) {
    if (!start || !end) {
        return dp::String();
    }

    dp::ref<dpdoc::Location> wordStart, wordEnd, rightStart;
    dp::String word, temp;
    QString left, right;
    int flags = 0;
    dpdoc::ContentIterator *iter = doc->getContentIterator(dpdoc::CV_TEXT, start);
    if (iter == NULL) {
        return dp::String();
    }

    // collect the left.
    const int limit = 50;
    wordStart  = iter->getCurrentPosition();
    for(int i = 0; i < limit; ++i) {
        temp = iter->previous(flags);
        if (isBlankString(temp)) {
            LOGI("blank string found in previous");
            break;
        }
        left += temp.utf8();
    }
    wordStart  = iter->getCurrentPosition();
    if (0) {
        LOGI("final left %s", left.c_str());
    }
    iter->release();

    // collect the right, call previous to make sure we will not miss the click position.
    iter = doc->getContentIterator(dpdoc::CV_TEXT, start);
    iter->previous(flags);
    rightStart = iter->getCurrentPosition();
    wordEnd = iter->getCurrentPosition();
    for(int j = 0; j < limit; ++j) {
        temp = iter->next(flags);
        if (isBlankString(temp)) {
            LOGI("blank string found in next");
            break;
        }
        right += temp.utf8();
    }
    wordEnd = iter->getCurrentPosition();
    if (0) {
        LOGI("final right %s", right.c_str());
    }
    iter->release();

    word = doc->getText(rightStart, wordEnd);
    int leftBoundary = getTextLeftBoundary(env, splitter, word.utf8(), left.c_str(), right.c_str());
    int rightBoundary = getTextRightBoundary(env, splitter, word.utf8(), left.c_str(), right.c_str());

    // update with boundary now, left first.
    iter = doc->getContentIterator(dpdoc::CV_TEXT, start);
    wordStart  = iter->getCurrentPosition();
    for(int i = 0; i < limit && i < leftBoundary; ++i) {
        wordStart  = iter->getCurrentPosition();
        if (isBlankString(iter->previous(flags))) {
            break;
        }
    }
    iter->release();

    // update with boundary now, right now. first.
    iter = doc->getContentIterator(dpdoc::CV_TEXT, start);
    iter->previous(flags);
    wordEnd = iter->getCurrentPosition();
    for(int j = 0; j < limit && j < rightBoundary; ++j) {
        wordEnd = iter->getCurrentPosition();
        temp = iter->next(flags);
        if (0) {
            LOGI("collecting right %s", temp.utf8());
        }
        if (isBlankString(temp)) {
            break;
        }
    }
    iter->release();

    // todo: getText sometimes may not work correctly. use left+right later.
    word = doc->getText(wordStart, wordEnd);
    newStart = wordStart;
    newEnd = wordEnd;
    return word;

}

// try to extend start to [newStart, newEnd], if start is in range of [newStart, newEnd], this function return the string between [newStart, newEnd].
// otherwise, it returns the string between [start, end]
// location comparsion
// *  \return 0 - locations point to the same position; -1 - this comes first, 1 - other comes first
dp::String AdobeLibrary::getWord(dp::ref<dpdoc::Location> start, dp::ref<dpdoc::Location> end, dp::ref<dpdoc::Location> & newStart, dp::ref<dpdoc::Location> & newEnd) {
    if (!start || !end) {
        return dp::String();
    }

    dp::ref<dpdoc::Location> wholeStart, wholeEnd;
    dp::ref<dpdoc::Location> nextStart, nextEnd;
    dp::String whole, next;
    int flags = dpdoc::CI_JOIN_ALPHA |dpdoc::CI_JOIN_NUMERIC;
    dpdoc::ContentIterator *iter = doc->getContentIterator(dpdoc::CV_TEXT, start);
    if (iter == NULL) {
        return dp::String();
    }

    // try to get whole word at first. extend from start to [newStart ... start... newEnd], for english word
    // call next twice.
    iter->previous(flags);
    do  {
        wholeStart  = iter->getCurrentPosition();
        whole = iter->next(flags);
        if (!isNonBlankString(whole)) {
            break;
        }
    } while (iter->getCurrentPosition()->compare(start) <= 0);
    wholeEnd = iter->getCurrentPosition();
    whole = doc->getText(wholeStart, wholeEnd);

    // try [prev, start] as usually, user click the center of word
    iter->release();
    iter = doc->getContentIterator(dpdoc::CV_TEXT, start);
    nextStart = start;
    iter->next(flags);
    nextEnd = iter->getCurrentPosition();
    next = doc->getText(nextStart, nextEnd);
    iter->release();

    // if whole word contains the next, just return the whole word, otherwise return the next only.
    if (isNonBlankString(whole) && !isNonBlankString(next)) {
        newStart = wholeStart;
        newEnd = wholeEnd;
        return whole;
    }
    if (!isNonBlankString(whole) && isNonBlankString(next)) {
        newStart = nextStart;
        newEnd = nextEnd;
        return next;
    }
    if (contains(whole, next)) {
        newStart = wholeStart;
        newEnd = wholeEnd;
        return whole;
    }
    newStart = nextStart;
    newEnd = nextEnd;
    return next;
}

bool AdobeLibrary::prevScreen() {
    return renderer->previousScreen();
}

bool AdobeLibrary::nextScreen(){
    return renderer->nextScreen();
}

bool AdobeLibrary::setPDFFontSize(double width, double height, double size) {
    OnyxMatrix matrix;
    if (size <= 0) {
        size = 1;
    }
    renderer->setViewport(width / size, height / size, true);
    matrix.setScale(size, false, false);
    renderer->setEnvironmentMatrix(matrix);
    return true;
}

bool AdobeLibrary::setePubFontSize(double width, double height, double size) {
    //LOGI("Set epub font size %lf", size);
    renderer->setViewport(width, height, true);
    renderer->setDefaultFontSize(size);
    return true;
}

bool AdobeLibrary::setFontSize(double width, double height, double size) {
    if (isEpub()) {
        setePubFontSize(width, height, size);
    } else if (isPDF()) {
        setPDFFontSize(width, height, size);
    }
    return false;
}

bool AdobeLibrary::setPageMode(int mode) {
    if (mode != dpdoc::PM_FLOW_PAGES) {
        OnyxMatrix matrix;
        renderer->setEnvironmentMatrix(matrix);
    }
    renderer->setPagingMode(mode);
    return true;
}



int AdobeLibrary::getRectangles(const char *start, const char * end, std::vector<onyx::OnyxRectangle> & rects) {
    if (start == NULL || end == NULL) {
        return 0 ;
    }
    dp::ref<dpdoc::Location> startLocation = doc->getLocationFromBookmark(start);
    dp::ref<dpdoc::Location> endLocation = doc->getLocationFromBookmark(end);
    if (!startLocation || !endLocation) {
        LOGE("getRectangles with null start or end location");
        return 0;
    }
    dpdoc::RangeInfo * range1 = renderer->getRangeInfo(startLocation, endLocation);
    dpdoc::RangeInfo * range2 = renderer->getRangeInfo(endLocation, startLocation);
    dpdoc::RangeInfo * range;
    if (range1 != NULL && range1->getBoxCount() > 0) {
        range = range1;
    } else if (range2 != NULL && range2->getBoxCount() > 0) {
        range = range2;
    }

    if (range == 0) {
        return 0;
    }

    int count = range->getBoxCount();
    for (int i = 0; i < count; ++i) {
        OnyxRectangle rect;
        range->getBox(i, false, &rect);

        // merge before rendering.
        // Adjust to make it look nicer. To avoid the gap between two rectangles.
        // rect.adjust(0, 0, 1, 1);
        OnyxRectangle area(rect.left(), rect.top(), rect.width(), rect.height());
        rects.push_back(area);
    }
    if (range1 != NULL) {
        range1->release();
    }
    if (range2 != NULL) {
        range2->release();
    }
    return count;
}

OnyxRectangle AdobeLibrary::pageDisplayPosition(int number, double *pageLeft, double * pageTop){
    // Ignore reflow paing mode.
    int mode = renderer->getPagingMode();
    if (mode == dpdoc::PM_FLOW_PAGES) {
        return OnyxRectangle();
    }

    PositionHolder  p(renderer, true);
    dp::ref<dpdoc::Location> pos = doc->getLocationFromPagePosition(number);
    renderer->navigateToLocation(pos);
    OnyxMatrix pm;
    OnyxRectangle viewRect;
    renderer->getNavigationMatrix(&pm);

    // Consider both scroll pages and hard pages.
    // In scroll pages mode, calculate it by offset.
    // In hard pages mode, calculate it by value from matrix.
    if (MATRIX_DEBUG) {
        LOGE("pageDisplayPosition: %d %lf %lf %lf %lf", number, pm.x(), p.matrix().x(), pm.y(), p.matrix().y());
    }

    if (renderer->getPagingMode() == dpdoc::PM_SCROLL_PAGES) {
        if (pageTop) {
            *pageTop = -pm.y();
        }
        if (pageLeft) {
            *pageLeft = -pm.x();
        }
        viewRect.setTopLeft(-p.matrix().x(), -p.matrix().y());
    } else if (renderer->getPagingMode() == dpdoc::PM_HARD_PAGES) {
        if (pageTop) {
            *pageTop = -pm.y() + p.matrix().y();
        }
        if (pageLeft) {
            *pageLeft = -pm.x() + p.matrix().x();
        }
        viewRect.setTopLeft(-pm.x(), -pm.y());
    }
    return viewRect;
}

// hard page mode only.
OnyxRectangle AdobeLibrary::pageDisplayRect(int number, double * pageLeft,  double *pageTop, double & natureWidth, double& natureHeight) {
    if (number < 0 || number >= doc->getPageCount()) {
        return OnyxRectangle();
    }

    if (renderer->getPagingMode() == dpdoc::PM_FLOW_PAGES) {
        return OnyxRectangle();
    }

    pageNaturalSize(number, natureWidth, natureHeight);
    OnyxRectangle viewRect = pageDisplayPosition(number, pageLeft, pageTop);

    OnyxMatrix matrix;
    renderer->getNavigationMatrix(&matrix);
    viewRect.setBottomRight(viewRect.left() + natureWidth * matrix.scale() - 1, viewRect.top() + natureHeight * matrix.scale() - 1);
    return viewRect;
}

// handle hard page only.
int AdobeLibrary::getDisplayRectangles(int start, int count, double *pageLeft, double * pageTop, std::vector<OnyxRectangle> & rectangles, std::vector<OnyxRectangle> & natureSize) {
    for(int i = 0; i < count; ++i) {
        int pn = start + i;
        double width, height;
        OnyxRectangle rect = pageDisplayRect(pn, pageLeft, pageTop, width, height);
        if (rect.isEmpty()) {
            continue;
        }
        rectangles.push_back(rect);
        OnyxRectangle size(width, height);
        natureSize.push_back(size);
    }
    return rectangles.size();
}

OnyxMatrix AdobeLibrary::getCurrentLocation() {
    OnyxMatrix pm;
    renderer->getNavigationMatrix(&pm);
    pm.mirror();
    if (MATRIX_DEBUG) {
        LOGE("getCurrentLocation %lf %lf %lf", pm.x(), pm.y(), pm.scale());
    }
    return pm;
}

bool AdobeLibrary::changeNavigationMatrix(double scale, double dx, double dy) {
    OnyxMatrix pm;
    renderer->getNavigationMatrix(&pm);
    if (scale > 0) {
        pm.setScale(scale);
    }
    pm.move(dx, dy);
    if (MATRIX_DEBUG) {
        LOGE("changeNavigationMatrix %lf %lf %lf", pm.x(), pm.y(), pm.scale());
    }
    renderer->setNavigationMatrix(pm);
    return true;
}

bool AdobeLibrary::setNavigationMatrix(double scale, double absX, double absY) {
    OnyxMatrix pm;
    renderer->getNavigationMatrix(&pm);
    if (scale > 0) {
        pm.setScale(scale);
    }
    pm.setXY(-absX, -absY);
    if (MATRIX_DEBUG) {
        LOGE("setNavigationMatrix %lf %lf %lf", pm.x(), pm.y(), pm.scale());
    }
    renderer->setNavigationMatrix(pm);
    return true;
}

// Use getScreenBeginning and getScreenEnd, seems
// getPageNumbersForScreen always return the same page number.
int AdobeLibrary::allVisiblePagesRectangle(JNIEnv * env, jobject list) {
    dp::ref<dpdoc::Location> location = renderer->getCurrentLocation();
    int pn = round(location->getPagePosition());
    if (isEpub()) {
        //LOGI("epub current location %lf", location->getPagePosition());
        jobject info = createPageInfoObject(env, pn, location, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        addObjectToList(env, info, list);
        return pn;
    }

    int begin = round(renderer->getScreenBeginning()->getPagePosition());
    int end = round(renderer->getScreenEnd()->getPagePosition());
    // LOGI("pdf location range with ignore support. %d %d %d", pn, begin, end);
    if (renderer->getPagingMode() == dpdoc::PM_FLOW_PAGES) {
        dp::ref<dpdoc::Location> location = renderer->getScreenBeginning();
        jobject info = createPageInfoObject(env, begin, location, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        addObjectToList(env, info, list);
        return begin;
    }

    int viewWidth = adobeClient->surfaceInstance().image().width();
    int viewHeight = adobeClient->surfaceInstance().image().height();
    std::vector<OnyxRectangle>  rectangles;
    std::vector<OnyxRectangle>  natureSize;
    OnyxMatrix pm;
    renderer->getNavigationMatrix(&pm);
    double pageLeft, pageTop;
    if (renderer->getPagingMode() == dpdoc::PM_SCROLL_PAGES) {
        for (int p = begin; p <= end; p++) {
            rectangles.clear();
            natureSize.clear();
            getDisplayRectangles(p, 1, &pageLeft, &pageTop, rectangles, natureSize);
            if (rectangles.size() <= 0 || natureSize.size() <= 0) {
                continue;
            }
            double xMin = rectangles.at(0).xMin;
            double yMin = rectangles.at(0).yMin;
            double xMax = rectangles.at(0).xMax;
            double yMax = rectangles.at(0).yMax;
            double width = natureSize.at(0).width();
            double height = natureSize.at(0).height();
            dp::ref<dpdoc::Location> location = doc->getLocationFromPagePosition(p);
            jobject info = createPageInfoObject(env, p, location, pm.scale(), (int)xMin, (int)yMin, (int)xMin + viewWidth, (int)yMin + viewHeight, (int)width, (int)height, pageLeft, pageTop);
            addObjectToList(env, info, list);
        }
        return begin;
    }

    getDisplayRectangles(begin, 1, &pageLeft, &pageTop, rectangles, natureSize);
    for(int i = 0; i < rectangles.size(); ++i) {
        double xMin = rectangles.at(i).xMin;
        double yMin = rectangles.at(i).yMin;
        double xMax = rectangles.at(i).xMax;
        double yMax = rectangles.at(i).yMax;
        double width = natureSize.at(i).width();
        double height = natureSize.at(i).height();
        int page = begin + i;
        dp::ref<dpdoc::Location> location = doc->getLocationFromPagePosition(page);
        jobject info = createPageInfoObject(env, page,  location, pm.scale(), (int)xMin, (int)yMin, (int)xMin + viewWidth, (int)yMin + viewHeight, (int)width, (int)height, pageLeft, pageTop);
        addObjectToList(env, info, list);
    }
    return begin;
}

void AdobeLibrary::setAbortFlag(bool abort) {
    if (adobeClient) {
        adobeClient->setAbortFlag(abort);
    }
    if (docClient) {
        docClient->setAbortFlag(abort);
    }
}

bool AdobeLibrary::getAbortFlag() {
    if (adobeClient) {
        return adobeClient->getAbortFlag();
    }
    return false;
}

bool AdobeLibrary::searchAllInPage(JNIEnv *env, const char * pattern, bool caseSensitive, bool matchWholeWord, int startPageNumber, int endPageNumber,  jobject list) {
    int flags = 0;
    if (matchWholeWord) {
        flags |= dpdoc::SF_WHOLE_WORD;
    }
    if (caseSensitive) {
        flags |= dpdoc::SF_MATCH_CASE;
    }
    dp::ref<dpdoc::Location> fromLocation = renderer->getScreenBeginning();
    dp::ref<dpdoc::Location> endLocation = renderer->getScreenEnd();

    int count = 0;
    bool found = true;
    dpdoc::Range range;
    while (found) {
        found =  doc->findText(fromLocation, endLocation, flags, pattern, &range);
        if (found) {
            addRangeToList(env, range, list);
            ++count;
            fromLocation = range.end;
        }
    }
    return (count > 0);
}

// search all matched result in whole page.
bool AdobeLibrary::searchNextOccur(JNIEnv * env, const char * pattern, bool caseSensitive, bool matchWholeWord, int startPageNumber, int endPageNumber,  jobject list) {
    int flags = 0;
    if (matchWholeWord) {
        flags |= dpdoc::SF_WHOLE_WORD;
    }
    if (caseSensitive) {
        flags |= dpdoc::SF_MATCH_CASE;
    }

    dp::ref<dpdoc::Location> fromLocation;
    dp::ref<dpdoc::Location> endLocation;
    if (startPageNumber + 1 >= doc->getPageCount()) {
        fromLocation = doc->getEnd();
    } else {
        fromLocation = doc->getLocationFromPagePosition(startPageNumber);
    }
    endLocation = doc->getEnd();

    dpdoc::Range range;
    bool found =  doc->findText(fromLocation, endLocation, flags, pattern, &range);
    if (found) {
        addRangeToList(env, range, list);
    }
    return found;
}

bool AdobeLibrary::searchPrevOccur(JNIEnv * env, const char * pattern, bool caseSensitive, bool matchWholeWord, int startPageNumber, int endPageNumber,  jobject list){
    int flags = dpdoc::SF_BACK;
    if (matchWholeWord) {
        flags |= dpdoc::SF_WHOLE_WORD;
    }
    if (caseSensitive) {
        flags |= dpdoc::SF_MATCH_CASE;
    }

    dp::ref<dpdoc::Location> fromLocation;
    dp::ref<dpdoc::Location> endLocation;
    if (startPageNumber + 1 >= doc->getPageCount()) {
        fromLocation = doc->getEnd();
    } else {
        fromLocation = doc->getLocationFromPagePosition(startPageNumber + 1);
    }
    endLocation = doc->getBeginning();

    dpdoc::Range range;
    bool found = doc->findText(fromLocation, endLocation, flags, pattern, &range);
    if (found) {
        addRangeToList(env, range, list);
    }
    return found;
}

bool AdobeLibrary::hasTableOfContent() {
    return (doc->getTocRoot() != NULL);
}

static jobject createTableOfContentEntry(JNIEnv * env, const char *title, int pageNumber) {
    jclass entry = env->FindClass(entryClassName);
    if (entry == 0) {
        LOGE("Could not find class: %s", entryClassName);
        return 0;
    }

    jmethodID mid = env->GetStaticMethodID(entry, "createEntry", "(Ljava/lang/String;I)Lcom/onyx/reader/ReaderTableOfContentEntry;");
    if (mid == 0) {
        LOGE("Find method createEntry failed");
        return 0;
    }

    jstring jstr = env->NewStringUTF(title);
    jobject obj = env->CallStaticObjectMethod(entry, mid, jstr, pageNumber);

    env->DeleteLocalRef(jstr);
    env->DeleteLocalRef(entry);
    return obj;
}

static bool addTableOfContentEntry(JNIEnv * env, jobject parent, jobject  child) {
    jclass entry = env->FindClass(entryClassName);
    if (entry == 0) {
        LOGE("Could not find class: %s", entryClassName);
        return false;
    }

    jmethodID mid = env->GetStaticMethodID(entry, "addEntry", "(Lcom/onyx/reader/ReaderTableOfContentEntry;Lcom/onyx/reader/ReaderTableOfContentEntry;)V");
    if (mid == 0) {
        LOGE("Find method addEntry failed");
        return false;
    }

    env->CallStaticVoidMethod(entry, mid, parent, child);

    env->DeleteLocalRef(entry);
    return true;
}

bool AdobeLibrary::buildTableOfContent(JNIEnv * env, jobject toc, dpdoc::TOCItem * parent) {
    int count = parent->getChildCount();
    for(int i = 0; i < count; ++i) {
        dpdoc::TOCItem *node = parent->getChild(i);
        if (node == 0) {
            continue;
        }
        dp::ref<dpdoc::Location> location = node->getLocation();
        if (location == 0) {
            continue;
        }
        jobject childEntry = createTableOfContentEntry(env, node->getTitle().utf8(), static_cast<int>(location->getPagePosition()));
        addTableOfContentEntry(env,  toc, childEntry);
        buildTableOfContent(env, childEntry, node);

        env->DeleteLocalRef(childEntry);
        node->release();
    }
    return true;
}

bool AdobeLibrary::getTableOfContent(JNIEnv * env, jobject toc) {
    dpdoc::TOCItem * root = doc->getTocRoot();
    if (root == 0) {
        return false;
    }
    buildTableOfContent(env, toc, root);
    root->release();
    return true;
}

dp::String AdobeLibrary::getMetadataEntry(const char * tag) {
    int index = 0;
    const int max = 100;
    while (index < max) {
        dp::ref<dpdoc::MetadataItem> item = doc->getMetadata(tag, index++);
        if (item != 0) {
            return item->getValue();
        }
    }
    return "";
}

bool AdobeLibrary::getMetadata(JNIEnv * env, jobject metadata, jobject tagList) {
    jclass listClass = env->FindClass("java/util/List");
    if( listClass == NULL ) {
        LOGE("Can't Find Class java/util/List");
        return false;
    }

    jmethodID sizeMethod = env->GetMethodID(listClass, "size", "()I");
    if (sizeMethod == NULL) {
        LOGE("Can't get MethodID for size");
        env->DeleteLocalRef(listClass);
        return false;
    }

    jmethodID getMethodID = env->GetMethodID(listClass, "get", "(I)Ljava/lang/Object;" );
    if (getMethodID == NULL) {
        LOGE("Can't get MethodID for get");
        env->DeleteLocalRef(listClass);
        return false;
    }

    jclass metadataClass = env->FindClass(metadataClassName);
    if (metadataClass == NULL) {
        LOGE("Can't find class for %s", metadataClassName);
        env->DeleteLocalRef(listClass);
        return false;
    }
    jmethodID addMethod = env->GetMethodID(metadataClass, "add", "(Ljava/lang/String;Ljava/lang/String;)V");
    int size = env->CallIntMethod(tagList, sizeMethod);
    for(int i = 0; i < size; ++i) {
        jstring tag = (jstring)env->CallObjectMethod(tagList, getMethodID, i);
        const char * tagString = getUtf8String(env, tag);
        dp::String data = getMetadataEntry(tagString);
        jstring str = env->NewStringUTF(data.utf8());
        env->CallVoidMethod(metadata, addMethod, tag, str);
        env->DeleteLocalRef(str);
    }
    env->DeleteLocalRef(listClass);
    env->DeleteLocalRef(metadataClass);
    return true;
}

int AdobeLibrary::getRectanglesByLocation(dp::ref<dpdoc::Location> startLocation, dp::ref<dpdoc::Location> endLocation, std::vector<onyx::OnyxRectangle> & rects) {
    if (!startLocation || !endLocation) {
        LOGE("getRectangles with null start or end location");
        return 0;
    }
    dpdoc::RangeInfo * range1 = renderer->getRangeInfo(startLocation, endLocation);
    dpdoc::RangeInfo * range2 = renderer->getRangeInfo(endLocation, startLocation);
    dpdoc::RangeInfo * range;
    if (range1 != NULL && range1->getBoxCount() > 0) {
        range = range1;
    } else if (range2 != NULL && range2->getBoxCount() > 0) {
        range = range2;
    }

    if (range == 0) {
        return 0;
    }

    int count = range->getBoxCount();
    for (int i = 0; i < count; ++i) {
        OnyxRectangle rect;
        range->getBox(i, false, &rect);

        // merge before rendering.
        // Adjust to make it look nicer. To avoid the gap between two rectangles.
        // rect.adjust(0, 0, 1, 1);
        OnyxRectangle area(rect.left(), rect.top(), rect.width(), rect.height());
        rects.push_back(area);
    }
    if (range1 != NULL) {
        range1->release();
    }
    if (range2 != NULL) {
        range2->release();
    }
    return count;
}

int AdobeLibrary::getTextLeftBoundary(JNIEnv * env, jobject splitter, const char *character, const char * left, const char * right) {
    jclass splitterClass = env->FindClass(splitterClassName);
    if( splitterClass == NULL ) {
        LOGE("Can't Find Class %s", splitterClassName);
        return -1;
    }

    jmethodID checkMethodID = env->GetStaticMethodID(splitterClass, "getTextLeftBoundary", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I" );
    if (checkMethodID == NULL) {
        LOGE("Can't get getTextLeftBoundary method.");
        env->DeleteLocalRef(splitterClass);
        return -1;
    }

    jstring str = env->NewStringUTF(character);
    jstring leftStr = env->NewStringUTF(left);
    jstring rightStr = env->NewStringUTF(right);
    int ret = env->CallStaticIntMethod(splitterClass, checkMethodID, str, leftStr, rightStr);
    env->DeleteLocalRef(str);
    env->DeleteLocalRef(leftStr);
    env->DeleteLocalRef(rightStr);
    env->DeleteLocalRef(splitterClass);
    return ret;
}

int AdobeLibrary::getTextRightBoundary(JNIEnv * env, jobject splitter, const char *character, const char *left, const char *right) {
    jclass splitterClass = env->FindClass(splitterClassName);
    if( splitterClass == NULL ) {
        LOGE("Can't Find Class %s", splitterClassName);
        return -1;
    }

    jmethodID checkMethodID = env->GetStaticMethodID(splitterClass, "getTextRightBoundary", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I" );
    if (checkMethodID == NULL) {
        LOGE("Can't get getTextRightBoundary method.");
        env->DeleteLocalRef(splitterClass);
        return -1;
    }

    jstring str = env->NewStringUTF(character);
    jstring leftStr = env->NewStringUTF(left);
    jstring rightStr = env->NewStringUTF(right);
    int ret = env->CallStaticIntMethod(splitterClass, checkMethodID, str, leftStr, rightStr);
    env->DeleteLocalRef(str);
    env->DeleteLocalRef(leftStr);
    env->DeleteLocalRef(rightStr);
    env->DeleteLocalRef(splitterClass);
    return ret;
}

int AdobeLibrary::isSentenceBoundaryBySplitter(JNIEnv * env, jobject splitter,  const char * screenText) {
    jclass splitterClass = env->FindClass(splitterClassName);
    if( splitterClass == NULL ) {
        LOGE("Can't Find Class %s", splitterClassName);
        return -1;
    }

    jmethodID checkMethodID = env->GetStaticMethodID(splitterClass, "isSentenceBoundary", "(Ljava/lang/String;)I" );
    if (checkMethodID == NULL) {
        LOGE("Can't get getTextRightBoundary method.");
        env->DeleteLocalRef(splitterClass);
        return -1;
    }

    jstring text = env->NewStringUTF(screenText);
    int ret = env->CallStaticIntMethod(splitterClass, checkMethodID, text);
    env->DeleteLocalRef(text);
    env->DeleteLocalRef(splitterClass);
    return ret;
}

int AdobeLibrary::collectVisibleLinks(JNIEnv * env, jobject list) {
    jclass listClass = env->FindClass(readerLinkClassName);
    if( listClass == NULL ) {
        LOGE("Can't Find Class %s", readerLinkClassName);
        return -1;
    }

    jmethodID addMethodID = env->GetStaticMethodID(listClass, "addToList", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[DLjava/util/List;)Z" );
    if (addMethodID == NULL) {
        LOGE("Can't get addToList method.");
        env->DeleteLocalRef(listClass);
        return -1;
    }

    int count = renderer->getLinkCount();
    for(int i = 0; i < count; ++i) {
        dpdoc::LinkInfo target;
        if (!renderer->getLinkInfo(i, &target)) {
            LOGE("get link info failed");
            continue;
        }
        if (target.beginning && target.end && target.target) {
            std::vector<onyx::OnyxRectangle> rects;
            getRectanglesByLocation(target.beginning, target.end, rects);
            jdoubleArray array = doubleArrayFromRectList(env, rects);

            jstring jstrStart = env->NewStringUTF(target.beginning->getBookmark().utf8());
            jstring jstrEnd = env->NewStringUTF(target.end->getBookmark().utf8());
            jstring jstrText = env->NewStringUTF(target.target->getBookmark().utf8());

            env->CallStaticBooleanMethod(listClass,
                addMethodID, jstrStart,
                 jstrEnd,
                 jstrText,
                 array,
                 list);
            env->DeleteLocalRef(jstrStart);
            env->DeleteLocalRef(jstrEnd);
            env->DeleteLocalRef(jstrText);
        }
    }
    env->DeleteLocalRef(listClass);
    return count;
}

bool AdobeLibrary::isLocationInCurrentScreen(JNIEnv *env, jstring location)
{
    const char *str = getUtf8String(env, location);
    if (str == NULL) {
        return false;
    }
    dp::ref<dpdoc::Location> loc = doc->getLocationFromBookmark(str);
    if (!loc) {
        return false;
    }
    return loc->compare(renderer->getScreenBeginning()) >= 0 && loc->compare(renderer->getScreenEnd()) < 0;
}

bool AdobeLibrary::setDisplayMargins(double left, double top, double right, double bottom) {
    renderer->setMargins(top, right, bottom, left);
    return true;
}

// seems not available in adobe sdk.
int AdobeLibrary::getPageOrientation(int page) {
    return 0;
}

bool AdobeLibrary::registerDrmCallback(shared_ptr<HostDRMCallback> callback) {
    drmCallback = callback;
}

bool AdobeLibrary::fulfillByAcsm(const QString &acsm_path)
{
    setFulfillmentInProgress(true);
    if (!wrapFulfillByAcsm(acsm_path)) {
        return false;
    }
    return true;
}

bool AdobeLibrary::activateDevice(const QString &user_id, const QString &password)
{
    LOGI("Activating device");
    if (!prepareDRMCommand()) {
        LOGW("prepare DRM command failed.");
        return false;
    }

    setActivationInProgress(true);

    bool activation_succeeded = false;
    ActivationStatus status = data_->checkActivationRecords(user_id, data_->processor(), device_);
    unsigned int workflow = 0;
    switch (status) {
    case HAS_VALID_ACTIVATION_REC:
        LOGI("valid activation record found.");
        setActivationInProgress(false);
        return true;
    case NO_OTA_FLAG:
        if (!isDRMInfoValid(user_id, password)) {
            setActivationInProgress(false);
            return false;
        }
        workflow = dpdrm::DW_AUTH_SIGN_IN;
        break;
    case ACTIVATION_OUT_OF_DATE:
        LOGI("activation out of date.");
        removeOriginActivationRecord();
        break;
    case NO_ACTIVATION_REC:
        if (!isDRMInfoValid(user_id, password)) {
            setActivationInProgress(false);
            return false;
        }
        workflow = dpdrm::DW_AUTH_SIGN_IN | dpdrm::DW_ACTIVATE;
        break;
    default:
        setActivationInProgress(false);
        return false;
    }

    dp::String adobe_id(user_id.c_str());
    dp::String dp_password(password.c_str());
    dp::String auth_provider("AdobeID");
    activation_succeeded = true;
    data_->processor()->initSignInWorkflow(workflow, auth_provider, adobe_id, dp_password);
    LOGI("Start work flows...");
    data_->processor()->startWorkflows(workflow);
    return activation_succeeded;
}

QString AdobeLibrary::getActivatedDRMAccount()
{
    if (!prepareDRMCommand()) {
        LOGW("prepare DRM command failed.");
        return QString("");
    }

    QString drm_account(data_->getActivatedDRMAccount(data_->processor(), device_).utf8());
    return drm_account;
}

bool AdobeLibrary::deactivateDevice()
{
    if (!prepareDRMCommand()) {
        LOGW("prepare DRM command failed.");
        return false;
    }

    return data_->deactivateDevice(data_->processor(), device_);
}

}
