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
 * Author: Peter Sorotokin, 2-OCT-2004
 */

#ifndef _UFT_BUFFER_H
#define _UFT_BUFFER_H

#include "uft_ref.h"

namespace uft
{

class Buffer;
class BufferStruct;
class BufferManager;


struct BufferRec
{
	enum Flags
	{
		WRITABLE = 1,
		IMMOVABLE = 2,
		RESIZABLE = 4,
		SUBBUFFER = 0x100
	};

	BufferManager *		m_manager;
	uint8 *				m_buffer;
	size_t				m_length;
	size_t				m_capacity;
	uft::uint32			m_flags;
	uft::uint32			m_pinCount;

	BufferRec() {}
};

class BufferManager
{
public:

	virtual ~BufferManager() {}
	virtual void alloc( BufferRec * buf, size_t capacity, uft::uint32 flags ) = 0;
	virtual void realloc( BufferRec * buf, size_t newCapacity ) = 0;
	virtual void clone( const BufferRec * buf, BufferRec * newBuf, uint32 flags ) = 0;
	virtual void pin( BufferRec * buf ) = 0;
	virtual void unpin( BufferRec * buf ) = 0;
	virtual void free( BufferRec * buf ) = 0;
	virtual uft::String toString( const BufferRec * buf ) = 0;

protected:

	// this is so subclasses can initialize uft::Buffer's BufferRec
	static BufferRec * getBuffer( const uft::Buffer& binaryVal );

};

class BufferStruct
{
	friend class uft::Buffer;
	friend class uft::BufferManager;

public:
	
	BufferStruct();
	BufferStruct( const BufferStruct& other );
	BufferStruct( size_t capacity, BufferManager * mgr, uint32 flags );
	~BufferStruct();

	uft::String toString() const { return m_buf.m_manager?m_buf.m_manager->toString(&m_buf):"uft::BufferStruct[BLANK]"; }
	bool query( const uft::Value& what, void * res );

	void * operator new( size_t size, Value& value ) { return ::operator new( size, uft::s_bufferDescriptor, &value ); }
	void operator delete( void *ptr, Value& value ) { assertLogic(false); }
	void * operator new( size_t size, Value& value, const char * file, int line ) { return ::operator new( size, uft::s_bufferDescriptor, &value, file, line ); }
	void operator delete( void *ptr, Value& value, const char * file, int line ) { assertLogic(false); }
	WeakReferencePtr * getWeakReferencePtr() { return NULL; }

private:

	BufferRec m_buf;
};

class Buffer : public Value
{
	friend class uft::BufferManager;

public:

	enum Flags
	{
		WRITABLE = BufferRec::WRITABLE,
		IMMOVABLE = BufferRec::IMMOVABLE,
		RESIZABLE = BufferRec::RESIZABLE
	};

	Buffer();
	Buffer( size_t capacity, uint32 flags = WRITABLE|RESIZABLE );
	Buffer( size_t capacity, BufferManager * mgr, uint32 flags = WRITABLE|RESIZABLE );
	Buffer( const uft::uint8 * data, size_t size );
	Buffer( const uft::String& str ); 
	Buffer( const uft::String& str, size_t index1, size_t index2 );

	void pin() const;
	void unpin() const;

	size_t length() const;
	const uint8 * buffer() const;

	bool isWritable() const;
	uint8 * writableBuffer() const; // fails is !isWritable() or not pinned
	bool isResizable() const;
	bool isImmovable() const;
	Buffer clone() const;
	Buffer readonly() const;

	void ensure( uft::uint32 flags ); // makes a copy if needed
	void ensureWritableAndResize( size_t length ); // makes a copy if needed

	Buffer region( size_t index ) const;
	Buffer region( size_t index1, size_t index2 ) const;

	// must be writable and resizable
	void append( const Buffer& buf );
	void append( const uint8 * buf, size_t size );
	void insert( size_t index, const Buffer& buf );
	void insert( size_t index, const uint8 * buf, size_t size );
	void splice( size_t index1, size_t index2, const Buffer& buf );
	void splice( size_t index1, size_t index2, const uint8 * buf, size_t size );

	static bool isInstanceOf( const Value& v ) { return v.isInstanceOf(uft::s_bufferDescriptor); }

	static const Buffer& nullValue() { return checked_cast<Buffer>(Value::nullValue()); }

private:

	BufferStruct* operator->() const
	{
		StructBlock * block = (StructBlock*)getBlock();
		assertLogic( block->getStructDescriptor() == uft::s_bufferDescriptor );
		return (BufferStruct*)block->getStructPtr();
	}

};

class BufferPin
{
public:

	BufferPin( const Buffer& bin )
		: m_bin(bin)
	{
		bin.pin();
	}

	~BufferPin()
	{
		m_bin.unpin();
	}

	operator const Buffer& () const
	{
		return m_bin;
	}

	const uint8 * buffer() const
	{
		return m_bin.buffer();
	}

	uint8 * writableBuffer() const
	{
		assertLogic( m_bin.isWritable() );
		return const_cast<uint8*>(m_bin.buffer());
	}

private:

	Buffer m_bin;
};

/**
 *  Buffer manager that uses standard allocation methods.
 */
class StdBufferManager : public BufferManager
{
public:

	virtual void alloc( BufferRec * buf, size_t capacity, uft::uint32 flags );
	virtual void realloc( BufferRec * buf, size_t newCapacity );
	virtual void clone( const BufferRec * buf, BufferRec * newBuf, uint32 flags );
	virtual void pin( BufferRec * buf );
	virtual void unpin( BufferRec * buf );
	virtual void free( BufferRec * buf );
	virtual uft::String toString( const BufferRec * buf );

	static BufferManager * getInstance();
};

/**
 *  Buffer manager that manages a subregion of another uft::Buffer.
 */
class SubBufferManager : public BufferManager
{
public:

	virtual void alloc( BufferRec * buf, size_t capacity, uft::uint32 flags );
	virtual void realloc( BufferRec * buf, size_t newCapacity );
	virtual void clone( const BufferRec * buf, BufferRec * newBuf, uint32 flags );
	virtual void pin( BufferRec * buf );
	virtual void unpin( BufferRec * buf );
	virtual void free( BufferRec * buf );
	virtual uft::String toString( const BufferRec * buf );

	static void init( uft::BufferRec * bb, const uft::Buffer& base, size_t index1, size_t index2, uint32 flags );

private:

	SubBufferManager( const uft::Buffer& base, size_t offset );

private:

	uft::Buffer m_base;
	size_t		m_offset;

};


}

#endif // UFT_BINARY

