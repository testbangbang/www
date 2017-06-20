#include "unrar_jni.h"

#include <cstring>

#include <string>
#include <vector>
#include <unordered_map>
#include <memory>

#include <android/log.h>

#include "unrar/rar.hpp"
#include "unrar/dll.hpp"

#include "JNIUtils.h"
#include "plugin_context_holder.h"

#define  LOG_TAG    "neo_unrar"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

namespace {

class HandleHolder {
public:
    HandleHolder(HANDLE handle)
        : handle_(handle) {
    }
    ~HandleHolder() {
        RARCloseArchive(handle_);
    }
    
    operator HANDLE() {
        return handle_;
    }
    
    HANDLE value() {
        return handle_;
    }
    
private:
    HANDLE handle_;
};

class OnyxRarContext {
public:
    OnyxRarContext(const std::string &path) 
        : filePath_(path), isEncrypted_(false) { 
    }
public:
    std::string getFilePath() {
        return filePath_;
    }
    
    bool isEncrypted() {
        return isEncrypted_;
    }
    
    void setEncrypted(bool e) {
        isEncrypted_ = e;
    }
    
    std::string getPassword() {
        return password_;
    }
    
    void setPassword(const std::string &password) {
        password_ = password;
    }
    
    JNIByteArray *getByteArray() {
        return byteArray_.get();
    }
    
    void resetByteArray(JNIEnv *env, size_t size) {
        byteArray_.reset(new JNIByteArray(env, size));
    }
    
    void resetByteArray() {
        byteArray_.reset(nullptr);
    }
    
    HANDLE getHandle() {
        return handle_;
    }
    
    void setHandle(HANDLE handle) {
        handle_ = handle;
    }
    
private:
    std::string filePath_;
    std::string password_;
    std::unique_ptr<JNIByteArray> byteArray_;
    HANDLE handle_;
    bool isEncrypted_;
};

void displayError(unsigned int error, const char *filename)
{
    switch(error)
    {
    case ERAR_END_ARCHIVE:
        LOGE("Unable to open %s, ERAR_END_ARCHIVE", filename);
        break;
        
    case ERAR_NO_MEMORY:
        LOGE("Unable to open %s, ERAR_NO_MEMORY", filename);
        break;
        
    case ERAR_BAD_DATA:
        LOGE("Unable to open %s, ERAR_BAD_DATA", filename);
        break;
        
    case ERAR_BAD_ARCHIVE:
        LOGE("Unable to open %s, ERAR_BAD_ARCHIVE", filename);
        break;
        
    case ERAR_UNKNOWN_FORMAT:
        LOGE("Unable to open %s, ERAR_UNKNOWN_FORMAT", filename);
        break;
        
    case ERAR_EOPEN:
        LOGE("Unable to open %s, ERAR_EOPEN", filename);
        break;
        
    case ERAR_ECREATE:
        LOGE("Unable to open %s, ERAR_ECREATE", filename);
        break;
        
    case ERAR_ECLOSE:
        LOGE("Unable to open %s, ERAR_ECLOSE", filename);
        break;
        
    case ERAR_EREAD:
        LOGE("Unable to open %s, ERAR_EREAD", filename);
        break;
        
    case ERAR_EWRITE:
        LOGE("Unable to open %s, ERAR_EWRITE", filename);
        break;
        
    case ERAR_SMALL_BUF:
        LOGE("Unable to open %s, ERAR_SMALL_BUF", filename);
        break;
        
    case ERAR_UNKNOWN:
        LOGE("Unable to open %s, ERAR_UNKNOWN", filename);
        break;
        
    case ERAR_MISSING_PASSWORD:
        LOGE("Unable to open %s, ERAR_MISSING_PASSWORD", filename);
        break;
        
    default:
        LOGE("Unable to open %s, unknown error: %d", filename, error);
    }
}

int CALLBACK callbackData(UINT msg, LPARAM UserData, LPARAM P1, LPARAM P2)
{
    OnyxRarContext *context = (OnyxRarContext *)UserData;
    switch (msg) {
    case UCM_PROCESSDATA: {
        if (context && context->getByteArray()) {
            context->getByteArray()->appendBuffer((jbyte *)P1, (size_t)P2);
        }
        break;
    }
    case UCM_NEEDPASSWORD:
        if (context) {
            context->setEncrypted(true);
            return -1;
        }
    default:
        break;
    }
    
    return 1;
}

void setPassword(HANDLE handle, OnyxRarContext *context) {
    if (context->isEncrypted() && context->getPassword().size() > 0) {
        RARSetPassword(handle, const_cast<char *>(context->getPassword().c_str()));
    }
}

PluginContextHolder<OnyxRarContext> contextHolder;

}

JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_plugins_comic_UnrarJniWrapper_open(JNIEnv *env, jobject thiz, jint id, jstring filePath)
{
    OnyxRarContext *context = contextHolder.findContext(env, id);
    if (!context) {
        JNIString path(env, filePath);
        context = new OnyxRarContext(path.getLocalString());
        contextHolder.insertContext(env, id, std::unique_ptr<OnyxRarContext>(context));
    }
    return true;
}

JNIEXPORT jboolean JNICALL Java_com_onyx_android_sdk_reader_plugins_comic_UnrarJniWrapper_isEncrypted(JNIEnv *env, jobject thiz, jint id)
{
    OnyxRarContext *context = contextHolder.findContext(env, id);
    if (!context) {
        return false;
    }
    return context->isEncrypted();
}

JNIEXPORT void JNICALL Java_com_onyx_android_sdk_reader_plugins_comic_UnrarJniWrapper_setPassword(JNIEnv *env, jobject thiz, jint id, jstring password)
{
    OnyxRarContext *context = contextHolder.findContext(env, id);
    if (!context) {
        return;
    }
    JNIString p(env, password);
    context->setEncrypted(true);
    context->setPassword(p.getLocalString());
}

JNIEXPORT jobjectArray JNICALL Java_com_onyx_android_sdk_reader_plugins_comic_UnrarJniWrapper_getEntries
(JNIEnv *env, jobject thiz, jint id)
{
    OnyxRarContext *context = contextHolder.findContext(env, id);
    if (!context) {
        return nullptr;
    }
    
    JNIUtils utils(env); 
    if (!utils.findClass("java/lang/String")) {
        return nullptr;
    }
    
    RAROpenArchiveData data;
    memset(&data, 0, sizeof(RAROpenArchiveData));
    
    data.ArcName = const_cast<char *>(context->getFilePath().c_str());
    data.OpenMode = RAR_OM_EXTRACT;
    
    HandleHolder handle(RAROpenArchive(&data));
    if (!handle) {
        LOGE("Open archive failed: %s", context->getFilePath().c_str());
        return nullptr;
    }
    if (data.OpenResult) {
        displayError(data.OpenResult, context->getFilePath().c_str());
        return nullptr;
    }
    context->setHandle(handle);
    setPassword(handle, context);
    
    RARHeaderData header;
    memset(&header, 0, sizeof(RARHeaderData));
    
    RARSetCallback(handle, callbackData, (LPARAM)context);
    
    std::vector<std::string> list;
    int error = 0;
    bool firstEntry = true;
    while ((error = RARReadHeader(handle, &header)) == 0) {
        if (!(header.Flags & LHD_DIRECTORY) && header.FileName) {
            list.push_back(header.FileName);
        }

        int code = RARProcessFile(handle, RAR_SKIP, NULL, NULL);
        if (code) {
            displayError(code, header.FileName);
            return nullptr;
        }
        
        if (firstEntry) {
            // test first entry to see if contents is encrypted
            firstEntry = false;
            int code = RARProcessFile(handle, RAR_TEST, NULL, NULL);
            if (code) {
                displayError(code, header.FileName);
                return nullptr;
            }
        }
    }

    LOGI("open archive status: %d", error);
    if (error == ERAR_MISSING_PASSWORD || error == ERAR_BAD_PASSWORD) {
        displayError(error, context->getFilePath().c_str());
        context->setEncrypted(true);
        return nullptr;
    }

    if (error != ERAR_END_ARCHIVE) {
        return nullptr;
    }
    
    jobjectArray ret = (jobjectArray)env->NewObjectArray(list.size(), utils.getClazz(), NULL);
    for (size_t i = 0; i < list.size(); ++i) {
        const auto &s = list.at(i);
        jstring newStr = env->NewStringUTF(s.c_str());
        if (!newStr) {
            LOGE("NewStringUTF failed: %s", s.c_str());
            return nullptr;
        }
        env->SetObjectArrayElement(ret, i, newStr);
        env->DeleteLocalRef(newStr);
    }
    
    return ret;
}

JNIEXPORT jbyteArray JNICALL Java_com_onyx_android_sdk_reader_plugins_comic_UnrarJniWrapper_extractEntryData
(JNIEnv *env, jobject thiz, jint id, jstring jEntry)
{
    OnyxRarContext *context = contextHolder.findContext(env, id);
    if (!context) {
        return nullptr;
    }
    
    JNIString entry(env, jEntry);
    
    RAROpenArchiveData data;
    memset(&data, 0, sizeof(RAROpenArchiveData));
    
    data.ArcName = const_cast<char *>(context->getFilePath().c_str());
    data.OpenMode = RAR_OM_EXTRACT;
    
    HandleHolder handle(RAROpenArchive(&data));
    if (!handle) {
        LOGE("Open archive failed: %s", context->getFilePath().c_str());
        return nullptr;
    }
    if (data.OpenResult) {
        displayError(data.OpenResult, context->getFilePath().c_str());
        return nullptr;
    }
    context->setHandle(handle);
    setPassword(handle, context);
    
    RARHeaderData header;
    memset(&header, 0, sizeof(RARHeaderData));
    
    RARSetCallback(handle, callbackData, (LPARAM)context);
    int error = 0;
    while ((error = RARReadHeader(handle, &header)) == 0) {
        if (strcmp(header.FileName, entry.getLocalString()) != 0) {
            int error = RARProcessFile(handle, RAR_SKIP, NULL, NULL);
            if (error) {
                LOGE("Unable to skip %s, error: %d", header.FileName, error);
                return nullptr;
            }
            continue;
        } 
        
        if (header.UnpSize <= 0) {
            return JNIByteArray(env, 0).getByteArray(false);
        }
        context->resetByteArray(env, header.UnpSize);
        
        // don't use RAR_EXTRACT because files will be extracted in current directory
        int code = RARProcessFile(handle, RAR_TEST, NULL, NULL);
        if (code) {
            displayError(code, header.FileName);
            return nullptr;
        } 
        
        jbyteArray array = context->getByteArray()->getByteArray(true);
        context->getByteArray()->detachByteArray();
        context->resetByteArray();
        return array;
    }
    
    return nullptr;
}

JNIEXPORT void JNICALL Java_com_onyx_android_sdk_reader_plugins_comic_UnrarJniWrapper_close(JNIEnv *env, jobject thiz, jint id)
{
    contextHolder.eraseContext(env, id);
}
