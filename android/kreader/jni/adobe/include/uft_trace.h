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
 * Author: Peter Sorotokin, 4-JUL-2005
 *
 *  Tracing and performance measurment support
 *
 *  On Intel processors, the process must be bound to a single processor (because we read processor
 *  cycles to measure time) - this is called setting "affinity" and can be done with Imagecfg.exe tool. 
 */

#ifndef _UFT_TRACE_H
#define _UFT_TRACE_H

#include "uft_date.h"

#if defined(_MSC_VER) && !defined(WINCE)
extern "C" {
	__declspec(dllimport) void __stdcall OutputDebugStringA( const char * s );
}
#endif

namespace uft
{

// -------------------- default settings ----------------------------------

#ifndef UFT_TRACE_DUMP
#define UFT_TRACE_DUMP 0
#endif

#ifndef UFT_PERF_COLLECT
#define UFT_PERF_COLLECT 0
#endif

// -------------------- tracing utilities ---------------------------------

enum TraceFlag
{
	TR_NORMAL,
	TR_BEGIN,
	TR_END
};

void trace( TraceFlag flag, const char * msg, const char * val );
void traceFlush();

#if UFT_TRACE_DUMP
#define UFT_TRACE( msg, val ) uft::trace( uft::TR_NORMAL, msg, val )
#define UFT_TRACE_BEGIN( msg, val ) uft::trace( uft::TR_BEGIN, msg, val )
#define UFT_TRACE_END( msg, val ) uft::trace( uft::TR_END, msg, val )
char * UFT_FORMAT_ARGS(const char *, ...);
#else
#define UFT_TRACE( msg, val )
#define UFT_TRACE_BEGIN( msg, val )
#define UFT_TRACE_END( msg, val )
void UFT_FORMAT_ARGS(const char *, ...);
#endif

// -------------------- performance collection utilities ------------------

#if UFT_PERF_COLLECT

/*
  This number is overhead of a single performance scope in CPU cycles. It might need to be tuned up
  for different CPUs by trying to make a counter like this: { UFT_PERF_SCOPE( zero ); } report
  approximately zero.
*/
#define UFT_PERF_OVERHEAD 300

class PerfCounter
{
public:

	PerfCounter( const char * name )
		: m_name(name), m_totalTime(0), m_count(0)
	{
	}

	void tally( int64 time )
	{
		m_count++;
		m_totalTime += time;
	}

	void exclude( int64 time )
	{
		m_totalTime -= time;
	}

	~PerfCounter()
	{
		if( m_count )
		{
			char buf[128];
			sprintf( buf, "Perf %50s: Count: %-10d  Avg: %-15.2f  Total: %-15.0f\n",
				m_name, m_count, m_totalTime/(1000.0*m_count), m_totalTime/1000.0 );
#ifdef _MSC_VER
			::OutputDebugStringA( buf );
#endif
			if( s_reportFile == NULL )
				s_reportFile = fopen( "uft_perf.out.txt", "w" );
			fputs( buf, s_reportFile );
			fflush( s_reportFile );
		}
	}

private:

	const char * m_name;
	int64 m_totalTime;
	int		m_count;

	static FILE * s_reportFile;

public:

	static unsigned int	s_overhead;
};

class PerfCollectScope;

class PerfScope
{
	friend class PerfCollectScope;
public:

	PerfScope( PerfCounter * counter )
		: m_counter(counter), m_start(uft::Date::getCurrentTicks()), m_startOverhead(PerfCounter::s_overhead)
	{
		PerfCounter::s_overhead++;
	}

	void kill() { m_counter = NULL; }

	~PerfScope()
	{
		if( m_counter && s_collect )
			m_counter->tally( uft::Date::getCurrentTicks() - m_start - UFT_PERF_OVERHEAD*(PerfCounter::s_overhead-m_startOverhead) );
	}

public:

	static int s_collect;

private:

	PerfCounter * m_counter;
	unsigned int	m_startOverhead;
	int64			m_start;
};

class PerfExcludingScope
{
public:

	PerfExcludingScope( PerfCounter * counter )
		: m_counter(counter), m_start(uft::Date::getCurrentTicks()), m_startOverhead(PerfCounter::s_overhead)
	{
	}

	void kill() { m_counter = NULL; }

	~PerfExcludingScope()
	{
		if( m_counter && PerfScope::s_collect )
			m_counter->exclude( uft::Date::getCurrentTicks() - m_start - UFT_PERF_OVERHEAD*(PerfCounter::s_overhead-m_startOverhead) );
	}

private:

	PerfCounter * m_counter;
	unsigned int	m_startOverhead;
	int64			m_start;
};

class PerfSetCollectScope
{
public:
	PerfSetCollectScope( int collect )
	{
		m_collect = PerfScope::s_collect;
		PerfScope::s_collect = collect;
	}

	~PerfSetCollectScope()
	{
		PerfScope::s_collect = m_collect;
	}

private:

	int m_collect;
};

class PerfAddCollectScope
{
public:
	PerfAddCollectScope(int collect )
	{
		m_collect = collect;
		PerfScope::s_collect += collect;
	}

	~PerfAddCollectScope()
	{
		PerfScope::s_collect -= m_collect;
	}

private:

	int m_collect;
};

#define UFT_PERF_SCOPE( name )  \
	static uft::PerfCounter name##_pct_(#name); \
	uft::PerfScope name##_psp_(&name##_pct_)

#define UFT_PERF_SCOPE_IF( name, cond )  \
	static uft::PerfCounter name##_pct_(#name); \
	uft::PerfScope name##_psp_((cond)?&name##_pct_:NULL)

#define UFT_PERF_DONT_COUNT( name )  \
	name##_psp_.kill()

#define UFT_PERF_EXCLUDE( name )  \
	uft::PerfExcludingScope name##_pes_(&name##_pct_)

#define UFT_PERF_DONT_COLLECT( name )  \
	uft::PerfSetCollectScope name##_pstop(0);

#define UFT_PERF_DO_COLLECT( name )  \
	uft::PerfSetCollectScope name##_pstop(1);

#else

#define UFT_PERF_SCOPE( name )
#define UFT_PERF_DONT_COUNT( name )
#define UFT_PERF_EXCLUDE( name )
#define UFT_PERF_DONT_COLLECT( name )
#define UFT_PERF_DO_COLLECT( name )

#endif

} // namespace uft

#endif
