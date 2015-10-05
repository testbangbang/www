#include "onyx_adobe_timer.h"
#include "dp_all.h"

namespace onyx
{

class OnyxAdobeTimer::OnyxAdobeTimerPrivate : public dptimer::Timer
{
public:
    virtual void release()
    {

    }

    virtual void setTimeout( int millisec )
    {

    }

    virtual void cancel()
    {

    }
};

OnyxAdobeTimer::OnyxAdobeTimer()
    : data_(new OnyxAdobeTimer::OnyxAdobeTimerPrivate())
{
}

OnyxAdobeTimer::~OnyxAdobeTimer()
{
    data_->release();
}

void OnyxAdobeTimer::release()
{

}

void OnyxAdobeTimer::setTimeout(int millisec)
{

}

void OnyxAdobeTimer::cancel()
{

}

}   // namespace pdf
