/*
 *
 *                       ADOBE CONFIDENTIAL
 *                     _ _ _ _ _ _ _ _ _ _ _ _
 *
 * Copyright 2004 - 2007, Adobe Systems Incorporated
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
 * Author: Peter Sorotokin, 18-APR-2006
 */

// math methods based on standard C++ math library

#ifndef _UFT_MATH_STD_H
#define _UFT_MATH_STD_H

#include <math.h>
#include <float.h>
#include "uft_assert.h"
#include "uft_fixed.h"

namespace uft
{

#ifndef USE_FIXED_POINT

typedef float real;

inline float getFloat( real r ) { return r; }
inline real intToReal( int r ) { return (float)r; }
inline real floatToReal( float f ) { return f; }
inline real log( real r ) { return (float)::log(r); }
inline real exp( real r ) {return (float)::exp(r); }
inline real cos( real r ) { return (float)::cos(r); }
inline real sin( real r ) { return (float)::sin(r); }
inline real sqrt( real r ) { return (float)::sqrt(r); }
inline real pow( real x, real y) {return (float)::pow(x, y); }
inline real floor( real r ) { return (float)::floor(r); }
inline real ceil( real r ) { return (float)::ceil(r); }
inline real round( real r ) { return (float)::floor(r+0.5f); }
inline int floorToInt( real r ) { return (int)::floor(r); }
inline int ceilToInt( real r ) { return (int)::ceil(r); }
inline int roundToInt( real r ) { return (int)::floor(r+0.5f); }
inline const real real_const( const int r ) { return (real)r; }
inline const real real_const( const double r ) { return (real)r; }

#define UFT_MAX_REAL		FLT_MAX
#define UFT_MIN_REAL		(-FLT_MAX)
#define UFT_EPSILON			FLT_EPSILON
#define UFT_REAL_ZERO		0.0f
#define UFT_REAL_ONE		1.0f

#else

typedef Fixed32 real;

inline Fixed32 intToReal( int i )
{
	assertLogicMsg( (-32768 <= i && i <= 32767 ), "int to real: out of bounds" );
	Fixed32 r = Fixed32(i);
	return r;
}

inline Fixed32 floatToReal( float f )
{
	assertLogicMsg( (-32768.0 <= f && f <= 32767.0 ), "float to real: out of bounds" );
	Fixed32 r = Fixed32(f);
	return r;
}

inline const Fixed32 real_const( const int i )
{
	assertLogicMsg( (-32768 <= i && i <= 32767 ), "int const: out of bounds" );
	Fixed32 r = Fixed32(i);
	return r;
}

inline const Fixed32 real_const( const double d )
{
	assertLogicMsg( (-32768.0 <= d && d <= 32767.0 ), "real const: out of bounds" );
	Fixed32 r = Fixed32(d);
	return r;
}

/*
real log( real r)
real sin( real r ) {return sin(r);}
*/

inline real sqrt( real r )				{ return Sqrt(r); }
inline real pow( real _x, real _y)		{ return Pow(_x, _y); }
inline real exp( real r )				{ return Exp(r); }

inline real floor( real r )
{
	return Floor(r);
}

inline real ceil( real r )
{
	return Ceil(r) ;
}

inline real round( real r )
{
	return Round(r);
}

inline int floorToInt( real r )
{
	return FloorToInt(r);
}

inline int ceilToInt( real r )
{
	return CeilToInt(r);
}

inline int roundToInt( real r )
{
	return RoundToInt(r);
}

inline float getFloat( real r )
{
	return GetFloat(r);
}

#define UFT_MAX_REAL		uft::real_const(32767.0)
#define UFT_MIN_REAL		uft::real_const(-32767.0)
#define UFT_EPSILON			0x1
#define UFT_REAL_ZERO		uft::_kZeroFixed32Val
#define UFT_REAL_ONE		uft::_kOneFixed32Val


#endif

class MathScope
{
public:
	MathScope() {};
};

}
#endif // _UFT_MATH_STD_H