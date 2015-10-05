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
 * Author: Brian Becker, 14-SEPT-2007
 */

// math methods based on standard C++ math library

#ifndef _UFT_FIXED_H
#define _UFT_FIXED_H

#include <math.h>
#include <float.h>
#include "uft_assert.h"

namespace uft
{

enum StaticFixed32Value {
	_kMaxSignedFixed32Val = 0x7fffffff,
	_kMinSignedFixed32Val = -_kMaxSignedFixed32Val,
	_kZeroFixed32Val	  = 0,
	_kOneFixed32Val		  = 0x00010000
};

#if (defined(TETRAPHILIA_ARCH_ARM) && defined(TETRAPHILIA_INLINE_ASM_IS_GCC_COMPATIBLE) && !defined(__thumb))	\
	|| (defined(TETRAPHILIA_ARCH_ARM) && defined(TETRAPHILIA_INLINE_ASM_IS_ARMCC_COMPATIBLE) && !defined(__thumb))

	#define TETRAPHILIA_HAS_INLINE_FIXED_MUL_ASM

#endif

#ifdef TETRAPHILIA_HAS_INLINE_FIXED_MUL_ASM

	#if defined(TETRAPHILIA_ARCH_ARM) && defined(TETRAPHILIA_INLINE_ASM_IS_GCC_COMPATIBLE) && !defined(__thumb)

	inline int FixedMult( int a, int b )
	{
		int	hi, low, result;

		asm("smull	%0, %1, %2, %3" : "=&r" (low), "=&r" (hi) : "r" (a), "r" (b) );
		asm("mov	%0, %1, lsr #16" : "=r" (result) : "r" (low) );
		asm("add	%0, %1, %2, lsl #16" : "=r" (result) : "0" (result), "r" (hi) );

		return result;
	}

	#endif

	#if defined(TETRAPHILIA_ARCH_ARM) && defined(TETRAPHILIA_INLINE_ASM_IS_ARMCC_COMPATIBLE) && !defined(__thumb)

	inline int FixedMult( int a, int b )
	{
		int	hi, low, result;

		__asm
		{
			smull	low, hi, a, b
			mov		result, low, lsr #16
			add		result, result, hi, lsl #16
		}

		return result;
	}

	#endif

	#if defined(TETRAPHILIA_ARCH_IX86)

	inline int FixedMult( int a, int b )
	{
		// x86 PC
		__asm 
		{
			mov		eax,a
			mov		edx,b
			imul	edx
			mov		ax,dx
			rol		eax,16
		}
	}

	#endif

#else

	// FixedMult is an out-of-line function.  Note that it is frequently still done with assembly code
	// but in the arm case the assembly requires a non-thumb instruction.  So if the compiler is generating
	// thumb code we can't perform this operation inline.
	extern "C" int FixedMult( int a, int b );

#endif

extern "C" int FixedDiv( int a, int b );
extern "C" int FixedMultWithOverflowCheck(int a, int b, bool* pOverflow);
extern "C" int FixedExponent( int c );

extern bool RawDivWillOverflow(int a, int b);


class Fixed32
{
public:
	Fixed32( void )
	{}

	Fixed32( signed char i )
		: fVal((int)(i*65536))
	{}

	Fixed32( unsigned char i )
		: fVal((int)(i*65536))
	{}

	Fixed32( signed short i )
		: fVal((int)(i*65536))
	{}

	Fixed32( unsigned short i )
		: fVal((int)(i*65536))
	{
		assertLogic( i <= 32767 );
	}

	Fixed32( signed int i )
		: fVal((int)(i*65536))
	{
		assertLogic( -32768 <= i && i <= 32767 );
	}

	Fixed32( unsigned int i )
		: fVal((int)(i*65536))
	{
		assertLogic( i <= 32767 );
	}

	Fixed32( signed long i )
		: fVal((int)(i*65536))
	{
		assertLogic( -32768 <= i && i <= 32767 );
	}

	Fixed32( unsigned long i )
		: fVal((int)(i*65536))
	{
		assertLogic( i <= 32767 );
	}

	explicit Fixed32( double d )
		: fVal((int)(d*65536))
	{
		assertLogic( -32768 <= d && d <= 32767.9999847412109375 );
	}

	Fixed32( StaticFixed32Value val )
		: fVal((int)val)
	{}

	int fVal;
};

#define UFT_FIXED_EPSILON Fixed32( (StaticFixed32Value)1 )

/************* comparison operators *****************/

inline bool
operator<( Fixed32 c1, Fixed32 c2 )
{
	return c1.fVal < c2.fVal;
}

inline bool
operator<=( Fixed32 c1, Fixed32 c2 )
{
	return c1.fVal <= c2.fVal;
}

inline bool
operator==( Fixed32 c1, Fixed32 c2 )
{
	return c1.fVal == c2.fVal;
}

inline bool
operator!=( Fixed32 c1, Fixed32 c2 )
{
	return c1.fVal != c2.fVal;
}

inline bool
operator>=( Fixed32 c1, Fixed32 c2 )
{
	return c1.fVal >= c2.fVal;
}

inline bool
operator>( Fixed32 c1, Fixed32 c2 )
{
	return c1.fVal > c2.fVal;
}


/************* math operators *****************/

inline Fixed32
operator-( Fixed32 c1, Fixed32 c2 )
{
	return (StaticFixed32Value)(c1.fVal - c2.fVal);
}

inline Fixed32
operator-=( Fixed32& c1, Fixed32 c2 )
{
	c1.fVal -= c2.fVal;
	return c1;
}

inline Fixed32
operator+( Fixed32 c1, Fixed32 c2 )
{
	return (StaticFixed32Value)(c1.fVal + c2.fVal);
}

inline Fixed32
operator+=( Fixed32& c1, Fixed32 c2 )
{
	c1.fVal += c2.fVal;
	return c1;
}

inline Fixed32
operator/( Fixed32 c1, Fixed32 c2 )
{
	return (StaticFixed32Value)FixedDiv( c1.fVal, c2.fVal );
}

inline Fixed32
operator/=( Fixed32& c1, Fixed32 c2 )
{
	c1.fVal  = FixedDiv( c1.fVal, c2.fVal );
	return c1;
}

inline Fixed32
operator*( Fixed32 c1, Fixed32 c2 )
{
	return (StaticFixed32Value)FixedMult( c1.fVal, c2.fVal );
}

inline Fixed32
operator*=( Fixed32& c1, Fixed32 c2 )
{
	c1.fVal  = FixedMult( c1.fVal, c2.fVal );
	return c1;
}

inline Fixed32
operator-( Fixed32 c )
{
	return (StaticFixed32Value)(-c.fVal);
}

inline Fixed32
operator+( Fixed32 c )
{
	return (StaticFixed32Value)(+c.fVal);
}

/************ optimized math operators ************/

inline Fixed32 operator*( Fixed32 c1, signed char x ) { return (StaticFixed32Value)(c1.fVal * x ); }
inline Fixed32 operator*( Fixed32 c1, unsigned char x ) { return (StaticFixed32Value)(c1.fVal * x ); }
inline Fixed32 operator*( Fixed32 c1, signed short x ) { return (StaticFixed32Value)(c1.fVal * x ); }
inline Fixed32 operator*( Fixed32 c1, unsigned short x ) { return (StaticFixed32Value)(c1.fVal * x ); }
inline Fixed32 operator*( Fixed32 c1, signed int x ) { return (StaticFixed32Value)(c1.fVal * x ); }
inline Fixed32 operator*( Fixed32 c1, unsigned int x ) { return (StaticFixed32Value)(c1.fVal * x ); }
inline Fixed32 operator*( Fixed32 c1, signed long x ) { return (StaticFixed32Value)(c1.fVal * x ); }
inline Fixed32 operator*( Fixed32 c1, unsigned long x ) { return (StaticFixed32Value)(c1.fVal * x ); }
inline Fixed32 operator*( Fixed32 c1, float x ) { return (StaticFixed32Value)FixedMult(c1.fVal, (int)(x*65536)); }
inline Fixed32 operator*( Fixed32 c1, double x ) { return (StaticFixed32Value)FixedMult(c1.fVal, (int)(x*65536)); }

inline Fixed32 operator*( signed char x, Fixed32 c1 ) { return (StaticFixed32Value)(c1.fVal * x ); }
inline Fixed32 operator*( unsigned char x, Fixed32 c1 ) { return (StaticFixed32Value)(c1.fVal * x ); }
inline Fixed32 operator*( signed short x, Fixed32 c1 ) { return (StaticFixed32Value)(c1.fVal * x ); }
inline Fixed32 operator*( unsigned short x, Fixed32 c1 ) { return (StaticFixed32Value)(c1.fVal * x ); }
inline Fixed32 operator*( signed int x, Fixed32 c1 ) { return (StaticFixed32Value)(c1.fVal * x ); }
inline Fixed32 operator*( unsigned int x, Fixed32 c1 ) { return (StaticFixed32Value)(c1.fVal * x ); }
inline Fixed32 operator*( signed long x, Fixed32 c1 ) { return (StaticFixed32Value)(c1.fVal * x ); }
inline Fixed32 operator*( unsigned long x, Fixed32 c1 ) { return (StaticFixed32Value)(c1.fVal * x ); }
inline Fixed32 operator*( float x, Fixed32 c1 ) { return (StaticFixed32Value)FixedMult(c1.fVal, (int)(x*65536)); }
inline Fixed32 operator*( double x, Fixed32 c1 ) { return (StaticFixed32Value)FixedMult(c1.fVal, (int)(x*65536)); }

inline Fixed32 operator*=( Fixed32& c1, signed char x ) { c1.fVal *= x; return c1; }
inline Fixed32 operator*=( Fixed32& c1, unsigned char x ) { c1.fVal *= x; return c1; }
inline Fixed32 operator*=( Fixed32& c1, signed short x ) { c1.fVal *= x; return c1; }
inline Fixed32 operator*=( Fixed32& c1, unsigned short x ) { c1.fVal *= x; return c1; }
inline Fixed32 operator*=( Fixed32& c1, signed int x ) { c1.fVal *= x; return c1; }
inline Fixed32 operator*=( Fixed32& c1, unsigned int x ) { c1.fVal *= x; return c1; }
inline Fixed32 operator*=( Fixed32& c1, signed long x ) { c1.fVal *= x; return c1; }
inline Fixed32 operator*=( Fixed32& c1, unsigned long x ) { c1.fVal *= x; return c1; }
inline Fixed32 operator*=( Fixed32& c1, float x ) { c1.fVal = FixedMult(c1.fVal, (int)(x*65536)); return c1; }
inline Fixed32 operator*=( Fixed32& c1, double x ) { c1.fVal = FixedMult(c1.fVal, (int)(x*65536)); return c1; }

inline Fixed32 operator/( Fixed32 c1, signed char x ) { return (StaticFixed32Value)(c1.fVal / x); }
inline Fixed32 operator/( Fixed32 c1, unsigned char x ) { return (StaticFixed32Value)(c1.fVal / x); }
inline Fixed32 operator/( Fixed32 c1, signed short x ) { return (StaticFixed32Value)(c1.fVal / x); }
inline Fixed32 operator/( Fixed32 c1, unsigned short x ) { return (StaticFixed32Value)(c1.fVal / x); }
inline Fixed32 operator/( Fixed32 c1, signed int x ) { return (StaticFixed32Value)(c1.fVal / x); }
inline Fixed32 operator/( Fixed32 c1, unsigned int x ) { return (StaticFixed32Value)(c1.fVal / x); }
inline Fixed32 operator/( Fixed32 c1, signed long x ) { return (StaticFixed32Value)(c1.fVal / x); }
inline Fixed32 operator/( Fixed32 c1, unsigned long x ) { return (StaticFixed32Value)(c1.fVal / x); }
inline Fixed32 operator/( Fixed32 c1, float x ) { return (StaticFixed32Value)FixedDiv(c1.fVal, (int)(x*65536)); }
inline Fixed32 operator/( Fixed32 c1, double x ) { return (StaticFixed32Value)FixedDiv(c1.fVal, (int)(x*65536)); }

#if 0
inline Fixed32 operator/( signed char x, Fixed32 c1 ) { return (StaticFixed32Value)(65536 * ( x * 65536 / c1.fVal ) ); }
inline Fixed32 operator/( unsigned char x, Fixed32 c1 ) { return (StaticFixed32Value)(65536 * ( x * 65536 / c1.fVal ) ); }
inline Fixed32 operator/( signed short x, Fixed32 c1 ) { return (StaticFixed32Value)(65536 * ( x * 65536 / c1.fVal ) ); }
inline Fixed32 operator/( unsigned short x, Fixed32 c1 ) { return (StaticFixed32Value)(65536 * ( x * 65536 / c1.fVal ) ); }
inline Fixed32 operator/( signed int x, Fixed32 c1 ) { return (StaticFixed32Value)(65536 * ( x * 65536 / c1.fVal ) ); }
inline Fixed32 operator/( unsigned int x, Fixed32 c1 ) { return (StaticFixed32Value)(65536 * ( x * 65536 / c1.fVal ) ); }
inline Fixed32 operator/( signed long x, Fixed32 c1 ) { return (StaticFixed32Value)(65536 * ( x * 65536 / c1.fVal ) ); }
inline Fixed32 operator/( unsigned long x, Fixed32 c1 ) { return (StaticFixed32Value)(65536 * ( x * 65536 / c1.fVal ) ); }
inline Fixed32 operator/( float x, Fixed32 c1 ) { return (StaticFixed32Value)FixedDiv((int)(x * 65536), c1.fVal); }
inline Fixed32 operator/( double x, Fixed32 c1 ) { return (StaticFixed32Value)FixedDiv((int)(x * 65536), c1.fVal); }
#endif

inline Fixed32 operator/=( Fixed32& c1, signed char x ) { c1.fVal /= x; return c1; }
inline Fixed32 operator/=( Fixed32& c1, unsigned char x ) { c1.fVal /= x; return c1; }
inline Fixed32 operator/=( Fixed32& c1, signed short x ) { c1.fVal /= x; return c1; }
inline Fixed32 operator/=( Fixed32& c1, unsigned short x ) { c1.fVal /= x; return c1; }
inline Fixed32 operator/=( Fixed32& c1, signed int x ) { c1.fVal /= x; return c1; }
inline Fixed32 operator/=( Fixed32& c1, unsigned int x ) { c1.fVal /= x; return c1; }
inline Fixed32 operator/=( Fixed32& c1, signed long x ) { c1.fVal /= x; return c1; }
inline Fixed32 operator/=( Fixed32& c1, unsigned long x ) { c1.fVal /= x; return c1; }
inline Fixed32 operator/=( Fixed32& c1, float x ) { c1.fVal = FixedDiv(c1.fVal, (int)(x*65536)); return c1; }
inline Fixed32 operator/=( Fixed32& c1, double x ) { c1.fVal = FixedDiv(c1.fVal, (int)(x*65536)); return c1; }

#ifdef TETRAPHILIA_DISALLOW_POORLY_ORDERED_REAL_COMPARES
void operator<(int, Fixed32);
void operator<(double, Fixed32);
void operator<=(int, Fixed32);
void operator<=(double, Fixed32);
void operator==(int, Fixed32);
void operator==(double, Fixed32);
void operator!=(int, Fixed32);
void operator!=(double, Fixed32);
void operator>=(int, Fixed32);
void operator>=(double, Fixed32);
void operator>(int, Fixed32);
void operator>(double, Fixed32);
#endif

inline
bool operator<(StaticFixed32Value c1, Fixed32 c2)
{
	return (int)c1 < c2.fVal;
}

inline
bool operator<(Fixed32 c1, StaticFixed32Value c2)
{
	return c1.fVal < (int)c2;
}

inline
bool operator<=(StaticFixed32Value c1, Fixed32 c2)
{
	return (int)c1 <= c2.fVal;
}

inline
bool operator<=(Fixed32 c1, StaticFixed32Value c2)
{
	return c1.fVal <= (int)c2;
}

inline
bool operator==(StaticFixed32Value c1, Fixed32 c2)
{
	return (int)c1 <= c2.fVal;
}

inline
bool operator==(Fixed32 c1, StaticFixed32Value c2)
{
	return c1.fVal <= (int)c2;
}

inline
bool operator>=(StaticFixed32Value c1, Fixed32 c2)
{
	return (int)c1 >= c2.fVal;
}

inline
bool operator>=(Fixed32 c1, StaticFixed32Value c2)
{
	return c1.fVal >= (int)c2;
}

inline
bool operator>(StaticFixed32Value c1, Fixed32 c2)
{
	return (int)c1 > c2.fVal;
}

inline
bool operator>(Fixed32 c1, StaticFixed32Value c2)
{
	return c1.fVal > (int)c2;
}

inline
Fixed32 operator-(StaticFixed32Value c1, Fixed32 c2)
{
	return (StaticFixed32Value)((int)c1 - c2.fVal);
}

inline
Fixed32 operator-(Fixed32 c1, StaticFixed32Value c2)
{
	return (StaticFixed32Value)(c1.fVal - (int)c2);
}

inline
Fixed32 operator+(StaticFixed32Value c1, Fixed32 c2)
{
	return (StaticFixed32Value)((int)c1 + c2.fVal);
}

inline
Fixed32 operator+(Fixed32 c1, StaticFixed32Value c2)
{
	return (StaticFixed32Value)(c1.fVal + (int)c2);
}

inline
Fixed32 operator/(StaticFixed32Value c1, Fixed32 c2)
{
	return (StaticFixed32Value)FixedDiv((int)c1, c2.fVal);
}

inline
Fixed32 operator/(Fixed32 c1, StaticFixed32Value c2)
{
	return (StaticFixed32Value)FixedDiv(c1.fVal, (int)c2);
}

inline
Fixed32 operator*(StaticFixed32Value c1, Fixed32 c2)
{
	return (StaticFixed32Value)FixedMult( (int)c1, c2.fVal );
}

inline
Fixed32 operator*(Fixed32 c1, StaticFixed32Value c2)
{
	return (StaticFixed32Value)FixedMult( c1.fVal, (int)c2 );
}

inline
Fixed32 operator-=(Fixed32& c1, StaticFixed32Value c2)
{
	c1.fVal -= (int)c2;
	return c1;
}

inline
Fixed32 operator+=(Fixed32& c1, StaticFixed32Value c2)
{
	c1.fVal += (int)c2;
	return c1;
}

inline
Fixed32 operator/=(Fixed32& c1, StaticFixed32Value c2)
{
	c1.fVal = FixedDiv(c1.fVal, (int)c2);
	return c1;
}

inline
Fixed32 operator*=(Fixed32& c1, StaticFixed32Value c2)
{
	c1.fVal = FixedMult(c1.fVal, (int)c2);
	return c1;
}

inline Fixed32 Floor( Fixed32 c )
{
	return (StaticFixed32Value)(c.fVal & 0xffff0000);
}

inline Fixed32 Ceil( Fixed32 c )
{
	assertLogic( c.fVal <= 0x7fff0000 );
	return (StaticFixed32Value)((c.fVal+0x0ffff) & 0xffff0000);
}

inline int FloorToInt( Fixed32 c )
{
	return (int)(c.fVal >> 16);
}

inline int FloorToIntPinned( Fixed32 c )
{
	return (int)(c.fVal >> 16);
}

inline int CeilToInt( Fixed32 c )
{
	if ( c.fVal <= 0x7fff0000 )
		return (int)((c.fVal + 0x0ffff) >> 16);
	else	{
		int	ret = 0x7fff;
		return (int)ret;
	}
}

inline int CeilToIntPinned( Fixed32 c )
{
	if ( c.fVal <= 0x7fff0000 )
		return (int)((c.fVal + 0x0ffff) >> 16);
	else	{
		int	ret = 0x7fff;
		return (int)ret;
	}
}

inline int RoundToInt( Fixed32 c )
{
	if ( c.fVal < 0x7fff8000 )
		return (int)((c.fVal + 0x08000) >> 16);
	else	{
		int	ret = 0x8000;
		return (int)ret;
	}
}

inline Fixed32 Round( Fixed32 c )
{
	assertLogic( c.fVal < 0x7fff8000 );
	return (StaticFixed32Value)((c.fVal+0x08000) & 0xffff0000);
}

inline Fixed32 Fraction( Fixed32 c )
{
	return (StaticFixed32Value)(c.fVal & 0x0ffff);
}

inline Fixed32 Mod( Fixed32 c, Fixed32 modulus )
{
	return c - modulus * Floor( c / modulus );
}

inline float GetFloat( Fixed32 c )
{
	return c.fVal / float(65536);
}

inline double GetDouble( Fixed32 c )
{
	return c.fVal / double(65536);
}

//TODO make a real fixed point version of this
inline Fixed32 log( Fixed32 r )
{
	return Fixed32(::log(GetFloat(r)));
}

//TODO make a real fixed point version of this
inline Fixed32 sin( Fixed32 r )
{
	return Fixed32(::sin(GetFloat(r)));
}

//TODO make a real fixed point version of this
inline Fixed32 cos( Fixed32 r )
{
	return Fixed32(::cos(GetFloat(r)));
}

inline Fixed32 Pow( Fixed32 x, Fixed32 y )
{
	return Fixed32(::pow(GetFloat(x), GetFloat(y)));
}

inline Fixed32 Exp( Fixed32 c )
{
	return Fixed32(::exp(GetFloat(c)));
}

inline Fixed32 Sqrt( Fixed32 c )
{
	return Fixed32(::sqrt(GetFloat(c)));
}

} //namespace
#endif //_UFT_FIXED_H