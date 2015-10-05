/*
 *
 *                       ADOBE CONFIDENTIAL
 *                     _ _ _ _ _ _ _ _ _ _ _ _
 *
 * Copyright 2004, Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of
 * Adobe Systems Incorporated and its suppliers, if any.  The intellectual and
 * technical concepts contained herein are proprietary to Adobe Systems
 * Incorporated and its suppliers and may be covered by U.S. and Foreign
 * Patents, patents in process, and are protected by trade secret or copyright
 * law.  Dissemination of this information or reproduction of this material is
 * strictly forbidden unless prior written permission is obtained from Adobe
 * Systems Incorporated.
 *
 * Author: Peter Sorotokin, 8-MAR-2005
 */

#ifndef _UFT_DATE_H
#define _UFT_DATE_H

#include "uft_string.h"

namespace uft
{

// signed milliseconds since 00:00:00.000 01/01 Year 0 (which is really 1BC) of Georgian calendar
// time is in general assumed to be GMT
typedef uft::int64 time_t;

/**
  supporting class for uft::Date.
 */
class DateStruct
{
public:

	DateStruct( time_t time ) : m_time(time) {}
	DateStruct( const DateStruct& other ) : m_time(other.m_time) {}

	time_t getTime() { return m_time; }

	String toString() const;
	bool query( const Value&, void * res ) { return false; }
	uft::WeakReferencePtr * getWeakReferencePtr() { return NULL; }

	String toW3CDTFString() const;

private:

	uft::int64 m_time;
};

class Date : public uft::Value
{
public:

	Date() {}
	Date( time_t );
	
	time_t getTime() const { return getDateStruct()->getTime(); }

	int32 getYear() const; // YYYY
	uint32 getMonth() const; // 1-based
	uint32 getDayOfMonth() const; // 1-based
	uint32 getHours() const; // 0-23
	uint32 getMinutes() const; // 0-59
	uint32 getSeconds() const; // 0-59
	uint32 getMilliseconds() const; // 0-999
	uint32 getDayOfWeek() const; // 1-7
	uint32 getDayOfYear() const; // 1-366

	String toString() const { return uft::Value::toString(); }
	String toW3CDTFString() const { return getDateStruct()->toW3CDTFString(); }

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_dateDescriptor); }
	static const Date& nullValue() { return checked_cast<uft::Date>(uft::Value::nullValue()); }

	static uft::time_t getYear1970GMT();

public:

	// utility functions:
	// year is regular 4-digit for recent years, year 0 is 1BC, year -1 is 2BC
	// month and days are 1-based (Sunday = 1, Saturday = 7)
	static time_t makeTime( int32 year, uint32 month, uint32 dayOfMonth,
		uint32 hours = 12, uint32 minutes = 0, uint32 seconds = 0, uint32 msec = 0 );
	static void breakUpTime( time_t time, int32 * year, uint32 * month, uint32 * dayOfMonth,
		uint32 * hours, uint32 * minutes, uint32 * seconds, uint32 * msec,
		uint32 * dayOfYear, uint32 * dayOfWeek);
	static String toString( time_t time ); // something like "Wed, 09 Mar 2005 22:05:22 GMT" (that is what HTTP and Java use)
	static time_t getCurrentTime();
	static Date getCurrentDate() { return uft::Date( getCurrentTime() ); }

	static Date fromW3CDTFString(const uft::String& str) { return uft::Date( timeFromW3CDTFString(str) ); }

	static time_t timeFromW3CDTFString(const uft::String& str);
	static String toW3CDTFString(time_t time);

	// get some machine-dependent measure of time, as granular and precise as possible
	static uft::int64 getCurrentTicks()
	{
#if defined(_MSC_VER) && !defined(WINCE)
		// eax points to where the data should be returned
		__asm {
			cpuid
			rdtsc
		}
		// return value, by MS conventions is in eax and edx, which is exactly what rdtsc does
#else
		return getCurrentTime();
#endif
	}

private:

	DateStruct * getDateStruct() const
	{
		assertLogic( uft::s_dateDescriptor == ((StructBlock*)getBlock())->getStructDescriptor() );
		return (DateStruct*)((StructBlock*)getBlock())->getStructPtr();
	}

};


}

#endif // _UFT_DATE_H
