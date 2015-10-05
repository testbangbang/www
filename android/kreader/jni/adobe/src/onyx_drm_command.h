#ifndef ONYX_DRM_COMMAND_H
#define ONYX_DRM_COMMAND_H

#include "scoped_ptr.h"
#include "dp_all.h"
#include "onyx_adobe_timer.h"
#include "onyx_curl_net_provider.h"
#include "types.h"

namespace onyx
{

enum ActivationStatus
{
    HAS_VALID_ACTIVATION_REC = 0,
    ACTIVATION_OUT_OF_DATE,
    NO_OTA_FLAG,
    NO_ACTIVATION_REC
};

class OnyxDRMCommand
{
public:
    OnyxDRMCommand();
    ~OnyxDRMCommand();

    void init();
    inline dpdrm::DRMProcessor * processor() { return drm_processor_; }
    inline void setProcessor(dpdrm::DRMProcessor * p) { drm_processor_ = p; }

    ActivationStatus checkActivationRecords(const QString &user_id,
                                            dpdrm::DRMProcessor * drm_processor,
                                            dpdev::Device * device);
    dp::String getActivatedDRMAccount(dpdrm::DRMProcessor * drm_processor,
                                            dpdev::Device * device);
    bool deactivateDevice(dpdrm::DRMProcessor * drm_processor,
                          dpdev::Device * device);

private:
    dp::time_t getExpirationDate(dp::ref<dpdrm::FulfillmentItem> item);

private:
    // network provider
    base::scoped_ptr<dpnet::NetProvider> net_provider_;

    // drm processor
    dpdrm::DRMProcessor *drm_processor_;

    // timer
    OnyxAdobeTimer master_timer_;

};

}

#endif // ONYX_DRM_COMMAND_H
