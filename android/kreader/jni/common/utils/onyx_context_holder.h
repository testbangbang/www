#ifndef CONTEXTHOLDER_H
#define CONTEXTHOLDER_H

#include <unordered_map>
#include <memory>

#include <jni.h>

template <class T> class OnyxContextHolder
{
public:
    OnyxContextHolder() { }
    
    T *findContext(jobject object) {
        auto find = contextMap.find(object);
        if (find == contextMap.end()) {
            return nullptr;
        }
        return find->second.get();
    }
    
    // pass in owernership of context
    void insertContext(jobject object, std::unique_ptr<T> context) {
        contextMap[object] = std::move(context);
    }
    
    void eraseContext(jobject object) {
        contextMap.erase(object);
    }
    
private:
    std::unordered_map<jobject, std::unique_ptr<T>> contextMap; 
};

#endif // CONTEXTHOLDER_H