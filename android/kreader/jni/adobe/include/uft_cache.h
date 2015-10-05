/*
 *
 *                       ADOBE CONFIDENTIAL
 *                     _ _ _ _ _ _ _ _ _ _ _ _
 *
 * Copyright 2005, Adobe Systems Incorporated
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
 * Author: Peter Sorotokin, 9-SEP-2005
 */

#ifndef _UFT_CACHE_H
#define _UFT_CACHE_H

#include "uft_value.h"

namespace uft
{

enum CacheFlushType
{
	FLUSH_GARBAGE,	// flush only what is next to useless
	FLUSH_SMART,	// do cost-value analysis
	FLUSH_ALL		// flush all that can be flushed
};

// this can be subclassed to pass more info in
class CacheFlushContext
{
public:

	CacheFlushContext( CacheFlushType type ) : m_type(type) {}
	virtual ~CacheFlushContext() {}

	CacheFlushType getType() const { return m_type; }

	virtual bool query( const uft::Value& what, void * ptr ) { return false; }

private:

	CacheFlushType m_type;
};

// can be subclassed to get various information
class CacheCostInfo
{
public:

	virtual bool need( const uft::String& infoName ) const = 0;
	virtual void add( const uft::String& infoName, uft::uint32 value ) = 0;
};

class CachingValue : public Value
{
public:

	void flush( CacheFlushContext * context );
	void queryCost( CacheCostInfo * info );

	static bool isInstanceOf( const Value& v );

};

uft::CachingValue iterateCache( /*inout*/ uft::word * iterState );
void finishCacheIteration( uft::word iterState );

void flushCache( CacheFlushContext * context );

size_t getHeapSize();

}

#endif // _UFT_CACHE_H
