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
 * Author: Peter Sorotokin, 18-APR-2006
 */

#ifndef _UFT_MATH_H
#define _UFT_MATH_H

#include "uft_value.h"

#ifndef UFT_MATH_PROVIDER
#define UFT_MATH_PROVIDER "uft_math_std.h"
#endif

#include UFT_MATH_PROVIDER

namespace uft
{

class RealRect;

// layout is *guaranteed* to be the same as in BRVRealCoordRect: xMin, yMin, xMax, yMax
struct RealRectStruct
{
	real m_xMin;
	real m_yMin;
	real m_xMax;
	real m_yMax;

	RealRect& asRealRect() { return *(RealRect*)this; }
	const RealRect& asRealRect() const { return *(const RealRect*)this; }
};

// layout is *guaranteed* to be the same as in BRVRealCoordRect: xMin, yMin, xMax, yMax
class RealRect
{
public:

	RealRect()
		: m_xMin(real_const(1000.0f)), m_yMin(real_const(1000.0f)),
	      m_xMax(real_const(-1000.0f)), m_yMax(real_const(-1000.0f))
	{
	}

	RealRect( real xMin, real yMin, real xMax, real yMax )
		: m_xMin(xMin), m_yMin(yMin), m_xMax(xMax), m_yMax(yMax)
	{
	}

	bool isEmpty() const { return m_xMin > m_xMax || m_yMin > m_yMax; }
	uft::String toString();
	void intersectWith(const RealRect &other);
	void unionWith(const RealRect &other);
	void makeEmpty();

	real m_xMin;
	real m_yMin;
	real m_xMax;
	real m_yMax;
};

class RealPoint
{
public:

	RealPoint()
		: m_x(0), m_y(0)
	{
	}

	real m_x;
	real m_y;
};

class Real : public uft::Float
{
public:
	Real( const real& coord ) : uft::Float(uft::getFloat(coord)) {}
};

}

#endif // _UFT_MATH_H
