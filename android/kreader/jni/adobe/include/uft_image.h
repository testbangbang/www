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
 * Author: Peter Sorotokin, 8-NOV-2004
 */

#ifndef _UFT_IMAGE_H
#define _UFT_IMAGE_H

#include "uft_value.h"
#include "uft_buffer.h"

namespace uft
{

class IntRectStruct
{
public:

	// empty rect
	IntRectStruct()
		: m_xMin(1), m_yMin(1), m_xMax(0), m_yMax(0)
	{
	}

	IntRectStruct( int32 xMin, int32 yMin, int32 xMax, int32 yMax )
		: m_xMin(xMin), m_yMin(yMin), m_xMax(xMax), m_yMax(yMax)
	{
	}

	String toString() const;
	UFT_STRUCT_DECL

	int32 getXMin() const { return m_xMin; }
	int32 getXMax() const { return m_xMax; }
	int32 getYMin() const { return m_yMin; }
	int32 getYMax() const { return m_yMax; }
	int32 getWidth() const { return m_xMax - m_xMin; }
	int32 getHeight() const { return m_yMax - m_yMin; }
	bool isEmpty() const { return m_xMax <= m_xMin || m_yMax <= m_yMin; }

	bool contains( const IntRectStruct& other ) const;
	void unionWith( const IntRectStruct& other );
	void intersectWidth( const IntRectStruct& other );
	void makeEmpty();

public:

	int32 m_xMin;
	int32 m_yMin;
	int32 m_xMax;
	int32 m_yMax;
};

typedef IntRectStruct IntRect_t;

class IntRect : public uft::sref<IntRectStruct>
{
public:

	IntRect() {}

	IntRect( const IntRectStruct& rect )
	{
		new(*this) IntRectStruct(rect);
	}

	IntRect( int32 xMin, int32 yMin, int32 xMax, int32 yMax )
	{
		new(*this) IntRectStruct( xMin, yMin, xMax, yMax );
	}

};

class BitmapImageStruct
{
public:

	BitmapImageStruct();
	BitmapImageStruct( const BitmapImageStruct& bi );
	~BitmapImageStruct();

	bool isValid() const { return !m_bounds.isEmpty(); }
	Buffer getBuffer() const { return m_buffer; }
	uint32 getByteWidth() const { return m_byteWidth; }
	const IntRectStruct& getBounds() const { return m_bounds; }
	int32 getValidYMin() const { return m_validYMin; }
	int32 getValidYMax() const { return m_validYMax; }
	String getFormat() const { return m_format; }
	uint32 getPixelSize() const { return getPixelSize(m_format); }
	size_t getUpdatedRectCount() const { return m_updatedRects.isNull() ? 0 : m_updatedRects.length(); }
	IntRect getUpdatedRect( size_t index ) const { return assumed_cast<IntRect>(m_updatedRects[index]); }
	void addUpdatedRect( const IntRectStruct& rect );
	void clearUpdatedRects() { m_updatedRects.makeEmpty(); }

	void init( const Buffer& buffer, uint32 byteWidth, const String& format, const IntRectStruct& bounds );
	void validate( int32 validYMin, int32 validYMax );

	static uint32 getPixelSize( const uft::String& format );

	uft::String toString() const { return uft::String("Image ") + m_bounds.getWidth() + " " + m_bounds.getHeight(); }

	UFT_STRUCT_W_DECL

private:

	IntRectStruct			m_bounds;
	int32					m_validYMin;
	int32					m_validYMax;
	uint32					m_byteWidth;
	uft::String				m_format;
	Buffer					m_buffer;
	Vector					m_updatedRects;
};

class BitmapImage : public uft::sref<BitmapImageStruct>
{
public:

	BitmapImage();
	BitmapImage( uint32 width, uint32 height, uft::String format = Atom(RGB) );

	bool isValid() const { return (*this)->isValid(); }
	Buffer getBuffer() const { return (*this)->getBuffer(); }
	uint32 getByteWidth() const { return (*this)->getByteWidth(); }
	const IntRectStruct& getBounds() const { return (*this)->getBounds(); }
	int32 getValidYMin() const { return (*this)->getValidYMin(); }
	int32 getValidYMax() const { return (*this)->getValidYMax(); }
	String getFormat() const { return (*this)->getFormat(); }
	uint32 getPixelSize() const { return (*this)->getPixelSize(); }
	size_t getUpdatedRectCount() const { return (*this)->getUpdatedRectCount(); }
	IntRect getUpdatedRect( size_t index ) const { return (*this)->getUpdatedRect(index); }
	void addUpdatedRect( const IntRectStruct& rect ) { (*this)->addUpdatedRect(rect); }
	void clearUpdatedRects() { (*this)->clearUpdatedRects(); }

	void init( const Buffer& buffer, uint32 byteWidth, const String& format, const IntRectStruct& bounds )
	{
		(*this)->init( buffer, byteWidth, format, bounds );
	}

	void validate( int32 validYMin, int32 validYMax ) { (*this)->validate( validYMin, validYMax ); }

	static uint32 getPixelSize( const uft::String& format );


};

}

#endif // _UFT_IMAGE_H
