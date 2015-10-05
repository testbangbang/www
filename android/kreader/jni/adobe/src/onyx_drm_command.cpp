#include "onyx_drm_command.h"

#include "time.h"
#include "types.h"

namespace
{
bool g_debug_verbose = true;
}

namespace onyx
{

const char * const DRM_ACTIVATION_NONE = "none";
const char * const DRM_ACTIVATION_OTA  = "ota";
const char * const DRM_ACTIVATION_ADE  = "ade";

OnyxDRMCommand::OnyxDRMCommand()
    : net_provider_(0)
    , drm_processor_(0)
{
}

OnyxDRMCommand::~OnyxDRMCommand()
{
    if (drm_processor_ != 0)
    {
        drm_processor_->release();
    }
}

void OnyxDRMCommand::init()
{
    net_provider_.reset(new OnyxCurlNetProvider);
    dpnet::NetProvider::setProvider(net_provider_.get());
    if (g_debug_verbose) LOGI("after set the net provider");
    dp::timerRegisterMasterTimer((dptimer::Timer*)&master_timer_);
    if (g_debug_verbose) LOGI("init end");
}

ActivationStatus OnyxDRMCommand::checkActivationRecords(const QString &user_id,
                                                        dpdrm::DRMProcessor * drm_processor,
                                                        dpdev::Device * device)
{
    QString activation_type = DRM_ACTIVATION_OTA;

    dp::list<dpdrm::Activation> activations = drm_processor->getActivations();
    if (g_debug_verbose) LOGI("activation's size: %d", activations.length());
    for (size_t i = 0; i < activations.length(); ++i)
    {
        dp::ref<dpdrm::Activation> activation = activations[i];

        dp::String usr_name = activation->getUsername();
        QString usr_name_str(usr_name.utf8());
        if (g_debug_verbose) LOGI("Input User ID: %s", user_id.c_str());
        if (g_debug_verbose) LOGI("Activation User ID:%s", usr_name_str.c_str());

        bool expired = false;
        dp::time_t expiration = activation->getExpiration();
        if (expiration > 0)
        {
            time_t current = time(0);
            if (expiration <= current)
            {
                LOGW("Activation has expired. Date: %lld", expiration);
                expired = true;
            }
        }

        if (!expired)
        {
            if (activation->hasCredentials() &&
                user_id == usr_name_str &&
                activation_type == DRM_ACTIVATION_OTA)
            {
                // the activation is only useful when OTA is executed at least once
                if (g_debug_verbose) LOGI("Find valid activation record. Skip Activation");
                return HAS_VALID_ACTIVATION_REC;
            }
            else if (!user_id.length() <= 0 && user_id != usr_name_str)
            {
                if (g_debug_verbose) LOGI("The previous activation is out-of-date. Need reactivate device");
                return ACTIVATION_OUT_OF_DATE;
            }
            else
            {
                if (g_debug_verbose) LOGI("Activation should be executed to fill the OTA flag");
                return NO_OTA_FLAG;
            }
        }
    }
    return NO_ACTIVATION_REC;
}

dp::String OnyxDRMCommand::getActivatedDRMAccount(dpdrm::DRMProcessor *drm_processor, dpdev::Device *device)
{
    dp::String drm_account("");
    dp::list<dpdrm::Activation> activations = drm_processor->getActivations();
    if (g_debug_verbose) LOGI("activation's size: %d", activations.length());
    for (size_t i = 0; i < activations.length(); ++i)
    {
        dp::ref<dpdrm::Activation> activation = activations[i];

        dp::String usr_name = activation->getUsername();
        if (0 != usr_name.length()) {
            if (g_debug_verbose) LOGI("drm account: %s", usr_name.utf8());
            drm_account = usr_name;
            break;
        }
    }
    return drm_account;
}

bool OnyxDRMCommand::deactivateDevice(dpdrm::DRMProcessor * drm_processor,
                                      dpdev::Device *device)
{
    dp::Data empty_data;
    device->setActivationRecord(empty_data);

    // check if successfully done
    dp::list<dpdrm::Activation> activations = drm_processor->getActivations();
    if (g_debug_verbose) LOGI("activation's size: %d", activations.length());
    bool succeeded = false;
    if (0 == activations.length()) {
        succeeded = true;
    }
    return succeeded;
}

dp::time_t OnyxDRMCommand::getExpirationDate(dp::ref<dpdrm::FulfillmentItem> item)
{
    dp::time_t expire_time = 0;
    dp::ref<dpdrm::Rights> rights = item->getRights();

    // TODO. Rollback this change when getValidLicenses() works again.
    //dp::list<dpdrm::License> licenses = rights->getValidLicenses();
    dp::list<dpdrm::License> licenses = rights->getLicenses();
    for (size_t i = 0; i < licenses.length(); ++i)
    {
        dp::list<dpdrm::Permission> permissions = licenses[i]->getPermissions("display");
        for (size_t m = 0; m < permissions.length(); ++m)
        {
            expire_time = permissions[m]->getExpiration();
        }
    }
    return expire_time;
}

}
