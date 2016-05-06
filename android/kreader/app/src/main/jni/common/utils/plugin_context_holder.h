#ifndef CONTEXTHOLDER_H
#define CONTEXTHOLDER_H

#include <unordered_map>
#include <memory>

#include <jni.h>

#include "JNIUtils.h"

template <class T> class PluginContextHolder
{
public:
    PluginContextHolder() { }
    
    T *findContext(JNIEnv *env, jobject object) {
        JNIUtils utils(env);
        jint hashcode = utils.hashcode(object);
        auto find = contextMap.find(hashcode);
        if (find == contextMap.end()) {
            return nullptr;
        }
        return find->second.get();
    }

    
    // pass in owernership of context
    void insertContext(JNIEnv *env, jobject object, std::unique_ptr<T> context) {
        JNIUtils utils(env);
        jint hashcode = utils.hashcode(object);
        contextMap[hashcode] = std::move(context);
    }
    
    void eraseContext(JNIEnv *env, jobject object) {
        JNIUtils utils(env);
        jint hashcode = utils.hashcode(object);
        contextMap.erase(hashcode);
    }
    
private:
    std::unordered_map<jint, std::unique_ptr<T>> contextMap;
};

#endif // CONTEXTHOLDER_H
