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
    
    T *findContext(JNIEnv *env, jint id) {
        auto find = contextMap.find(id);
        if (find == contextMap.end()) {
            return nullptr;
        }
        return find->second.get();
    }

    
    // pass in owernership of context
    void insertContext(JNIEnv *env, jint id, std::unique_ptr<T> context) {
        contextMap[id] = std::move(context);
    }
    
    void eraseContext(JNIEnv *env, jint id) {
        contextMap.erase(id);
    }
    
private:
    std::unordered_map<jint, std::unique_ptr<T>> contextMap;
};

#endif // CONTEXTHOLDER_H
