#include "onyx_drm_client.h"

#include <iostream>

#include "dp_all.h"
#include "onyx_client.h"

namespace
{
bool g_debug_verbose = true;
}

namespace onyx
{

OnyxDRMClient::OnyxDRMClient(OnyxAdobeClient *adobe_client, dpdev::Device *device)
    : adobe_client_(adobe_client)
    , passhash_head(dp::ref<PasshashInputData>())
    , passhash_tail(dp::ref<PasshashInputData>())
{
    processor_ = dpdrm::DRMProvider::getProvider()->createDRMProcessor(this, device);
}

OnyxDRMClient::~OnyxDRMClient()
{
}

/**
 *  Indicates that the workflows have finished. If followUp parameter is
 *  not null, it indicates a follow-up workflows that must be performed
 *  next (server notification).
 */
void OnyxDRMClient::workflowsDone(unsigned int workflows, const dp::Data& follow_up)
{
    if (g_debug_verbose) LOGI("workflow %s is done.", workflowToString(workflows).utf8());
    if (follow_up.length() > 0)
    {
        dp::String str(reinterpret_cast<const char *>(follow_up.data()));
        if (g_debug_verbose) LOGI("Data: %s", str.utf8());
    }
//    dp::String str(reinterpret_cast<const char*>(follow_up.data()));
//    std::string std_str(str.utf8());
    QByteArray data;
//    data.resize(std_str.size());
//    data.assign(std_str.begin(), std_str.end());
    adobe_client_->reportWorkflowDone(workflows, data);
}

/**
 *  Request user input based on simple XHTML UI. Simple XHTML
 *  form syntax is supported. Client is only required to support
 *  plain text and input elements of types "text", "password" and "hidden".
 *  UI based on the given XHTML should be presented to the user.
 *  Once user enters values, input should be given to
 *  dpdrm::DRMProcessor::provideInput method.
 *
 *  Currently unused (for future TransparentID support).
 */
void OnyxDRMClient::requestInput(const dp::Data& input_XHTML)
{
    if (g_debug_verbose) LOGI("get here at requestInput");
}

/**
 *  Request user's confirmation
 *  "Q_ADEPT_NEW_ACCOUNT accountid accountName" - new DRM (Eden2) account is about to be created for accountName
 *  "Q_ADEPT_ADD_ACCOUNT accountid accountName Eden2GUID" - accountName is about to be mapped to the given Eden2 GUID
 * 
 *  Currently unused (for future TransparentID support).
 */
void OnyxDRMClient::requestConfirmation(const dp::String& code)
{
    if (g_debug_verbose) LOGI("get here at requestConfirmation");
}

/**
 *  Reports which DRM workflow is currently active and its progress.
 */
void OnyxDRMClient::reportWorkflowProgress(unsigned int workflow, const dp::String& title, double progress )
{
    adobe_client_->reportWorkflowProgress(workflow, QString(title.utf8()), progress);
}

void OnyxDRMClient::reportFollowUpURL(unsigned int workflow, const dp::String& url)
{
    LOGI("reportFollowUpURL");
    if (g_debug_verbose) LOGI("Workflow %s, BROWSE: %s", workflowToString(workflow).utf8(), url.utf8());
}

void OnyxDRMClient::deliverPasshash()
{
    LOGI("deliverPasshash");
    dp::Data passhash = dp::Data();
    if (passhash_head) {
        dp::ref<PasshashInputData> temp = passhash_head;
        passhash_head = temp->next_;
        if (!passhash_head) {
            passhash_tail = passhash_head;
        }

        if (temp->user_.length() > 0) {
            passhash = processor_->calculatePasshash(temp->user_, temp->password_);
        }
    }
    processor_->providePasshash(passhash);
}

void OnyxDRMClient::requestPasshash( const dp::ref<dpdrm::FulfillmentItem>& fulfillmentItem )
{
    LOGI("requestPasshash");
    if (!passhash_head || !passhash_head->timeout_)
    {
        deliverPasshash();
    }
    else
    {
        dptimer::TimerProvider * provider = dptimer::TimerProvider::getProvider();
        dptimer::Timer * timer = provider->createTimer(this);
        timer->setTimeout(passhash_head->timeout_);
    }
}

void OnyxDRMClient::reportDocumentWrittenWithLicense(const dp::String &url)
{
    if (url.length() > 0)
    {
        adobe_client_->reportDocumentWrittenWithLicense(QString(url.utf8()));
    }
}

void OnyxDRMClient::timerFired( dptimer::Timer * timer )
{
    LOGI("timerFired");
    timer->release();
    deliverPasshash();
}

/**
 *  Report an error for a DRM workflow which is currently in progress.
 */
void OnyxDRMClient::reportWorkflowError(unsigned int workflow, const dp::String& error_code )
{
    adobe_client_->reportWorkflowError(workflow, QString(error_code.utf8()));
}

dp::String OnyxDRMClient::workflowToString(int workflow)
{
    switch (workflow)
    {
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

}
