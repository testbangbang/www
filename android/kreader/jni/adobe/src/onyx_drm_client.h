#ifndef ONYX_DRM_PROCESSOR_CLIENT_H_
#define ONYX_DRM_PROCESSOR_CLIENT_H_

#include "dp_all.h"

namespace onyx
{

class OnyxAdobeClient;

class PasshashInputData : public dp::RefCounted
{
public:
    PasshashInputData(const dp::String& user, const dp::String& password, int timeout) :
        ref_count_(0),
        user_(user),
        password_(password),
        timeout_(timeout),
        next_(dp::ref<PasshashInputData>())
    {
    }

    void addRef()
    {
        ref_count_++;
    }

    void release()
    {
        if ( --ref_count_ == 0 )
            delete this;
    }

    int ref_count_;
    dp::String user_;
    dp::String password_;
    int timeout_;
    dp::ref<PasshashInputData> next_;
};

class OnyxDRMClient : public dpdrm::DRMProcessorClient, public dptimer::TimerClient
{
public:
    OnyxDRMClient(OnyxAdobeClient *adobe_client, dpdev::Device *device);
    ~OnyxDRMClient();

public:
    virtual void workflowsDone(unsigned int workflows, const dp::Data& follow_up);
    virtual void requestInput(const dp::Data& input_XHTML);
    virtual void requestConfirmation(const dp::String& code);
    virtual void reportWorkflowProgress(unsigned int workflow, const dp::String& title, double progress);
    virtual void reportWorkflowError(unsigned int workflow, const dp::String& error_code);
    virtual void reportFollowUpURL(unsigned int workflow, const dp::String& url);
    virtual void requestPasshash(const dp::ref<dpdrm::FulfillmentItem>& fulfillmentItem);
    virtual void reportDocumentWrittenWithLicense(const dp::String& url);
    virtual void timerFired(dptimer::Timer * timer);

private:
    void deliverPasshash();
    dp::String workflowToString(int workflow);

private:
    OnyxAdobeClient *adobe_client_;
    dp::ref<PasshashInputData> passhash_head;
    dp::ref<PasshashInputData> passhash_tail;
    dpdrm::DRMProcessor * processor_;
};

}

#endif  // ONYX_DRM_PROCESSOR_CLIENT_H_

