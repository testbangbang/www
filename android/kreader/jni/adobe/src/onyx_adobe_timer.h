#ifndef ONYX_ADOBE_TIMER_H
#define ONYX_ADOBE_TIMER_H

#include "dp_all.h"

namespace onyx
{

class OnyxAdobeTimer : public dptimer::Timer
{
public:
    OnyxAdobeTimer();
    virtual ~OnyxAdobeTimer();

    virtual void release();
    virtual void setTimeout( int millisec );
    virtual void cancel();

private:
    class OnyxAdobeTimerPrivate;
    OnyxAdobeTimerPrivate * data_;
};

}   // namespace pdf

#endif // ONYX_ADOBE_TIMER_H
