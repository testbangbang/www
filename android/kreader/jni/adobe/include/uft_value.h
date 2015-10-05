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
 * Author: Peter Sorotokin, 9-APR-2004
 */

#ifndef _UFT_VALUE_H
#define _UFT_VALUE_H

#include <stdio.h>
#include <stdlib.h>
#include "uft_alloc.h"
#include "uft_assert.h"

// true, false and bool must remain C++ keywords for UFT to function properly
// either #undef those before including uft headers or define UFT_UNDEF_BOOLEAN


#ifndef USING_MAC_SDK_VERSION_10_5_OR_ABOVE
// Apple's variant of GCC's stdbool.h,for SDKs 10.5 and above, define's bool to be bool (similar for true and false)
// this isn't a problem but will make the following test fail
// For these platforms do not run the BOOLEAN checks

#if defined(false)
#if defined(UFT_UNDEF_BOOLEAN)
#undef false
#else
#error "keyword false was defined as a macro"
#endif
#endif

#if defined(true)
#if defined(UFT_UNDEF_BOOLEAN)
#undef true
#else
#error "keyword true was defined as a macro"
#endif
#endif

#if defined(bool)
#if defined(UFT_UNDEF_BOOLEAN)
#undef bool
#else
#error "keyword bool was defined as a macro"
#endif
#endif
#endif

#define STATIC_ARRAY_SIZE(arr) (sizeof(arr)/sizeof(*arr))

// opaque class to mark a pointer to uninitialized memory
class RawMemory;

inline void * operator new( size_t size, RawMemory * mem )
{
	return (void*)mem;
}

inline void operator delete( void * ptr, RawMemory * mem )
{
}

// forward declarations
namespace uft
{
class StructDescriptor;
class Value;
class String;
class UTF16String;
class PlatformString;
class StringBuffer;
class SizedBlock;
class StructBlock;
class StringBufferStruct;
class Tuple;
class Vector;
class Set;
class VectorStruct;
class TreePathElement;
class ValueParser;
class SimpleRefStruct;
class CacheFlushContext;

// defined in uft_private.h
class Runtime;
class RuntimeImpl;

// built-in struct descriptors

extern const StructDescriptor * s_tokenDescriptor;
extern const StructDescriptor * s_dictDescriptor;
extern const StructDescriptor * s_qnameDescriptor;
extern const StructDescriptor * s_urlDescriptor;
extern const StructDescriptor * s_bufferDescriptor;
extern const StructDescriptor * s_stringBufferDescriptor;
extern const StructDescriptor * s_vectorDescriptor;
extern const StructDescriptor * s_setDescriptor;
extern const StructDescriptor * s_longIntDescriptor;
extern const StructDescriptor * s_doubleDescriptor;
extern const StructDescriptor * s_dateDescriptor;
extern const StructDescriptor * s_simpleRefDescriptor;
extern const StructDescriptor * s_mutableRefDescriptor;
extern const StructDescriptor * s_cacheRefDescriptor;
extern const StructDescriptor * s_mutableCacheRefDescriptor;
extern const StructDescriptor * s_weakRefDescriptor;
extern const StructDescriptor * s_lazyRefDescriptor;
extern const StructDescriptor * s_lazyCacheRefDescriptor;

}

namespace rib
{
	class Binder;
}

// a way to create structs
void * operator new( size_t size, const uft::StructDescriptor * structDesc, uft::Value * value ); 
inline void operator delete( void *, const uft::StructDescriptor * structDesc, uft::Value * value ) { }
void * operator new( size_t size, const uft::StructDescriptor * structDesc, uft::Value * value, const char * file, int line ); 
inline void operator delete( void *, const uft::StructDescriptor * structDesc, uft::Value * value, const char * file, int line ) { }

namespace uft
{

typedef signed char int8;
typedef unsigned char uint8;
typedef short int16;
typedef unsigned short uint16;
typedef unsigned short char16;
typedef int int32;
typedef unsigned int uint32;
typedef size_t word; // an int that can hold a pointer

#if defined(_MSC_VER)
typedef __int64 int64;
typedef unsigned __int64 uint64;
#define FORCE_INLINE __forceinline 
#else
typedef long long int int64;
typedef unsigned long long int uint64;
#define FORCE_INLINE inline
#endif

typedef uint32 cacheid;

// generic hash functions
uint32 hash( const uint8 * key, size_t length, uint32 initval = 0 );
uint32 hash4( uint32 key );
uint32 hash8( uint64 key );

inline uint32 hashWord( word key ) { return sizeof(word)==4?hash4((uint32)key):hash8((uint64)key); }

enum ValueType
{
	// ref counted variable size
	// isString() relies on STRING and ASCII_STRING numeric values
	STRING = 0, // String, can be atomized, immutable array of UTF8 characters, corresponds to CSS ident
	ASCII_STRING = 1, // just like String, but all characters are known to be ASCII
	PLATFORM_STRING = 2, // just like String, but all characters are in local/platform encoding, cannot be atomized
	UTF16_STRING = 3, // immutable array of UTF16 characters
	TUPLE = 4, // const-length sequence of Values - corresponds to CSS list
    
	// refcounted fixed size
	STRUCT = 0xF, // area of memory which knows what it is and can destruct and clone itself, etc.; size from StructDescriptor; most kinds of Values are Structs

	// primitive values, not ref counted
	INTEGER = 0x10,
	FLOAT = 0x11,
	NONE = 0x12 // empty value
};

/**
  head of the refcounted object block (like string)
*/
class BlockHead
{
	friend class uft::Value;
	friend class uft::PlatformString;
	friend class uft::UTF16String;
	friend class uft::Tuple;
	friend class uft::RuntimeImpl;
public:
	
	// either STRING or ASCII_STRING
	bool isString() const { return (m_typeAndRefCount >> 29) == 0; } // relies on type numeric values
	// either ASCII_STRING or PLATFORM_STRING
	bool isPlatformString() const { return getType() == ASCII_STRING || getType() == PLATFORM_STRING; }
	bool isASCIIString() const { return getType() == ASCII_STRING; }
	bool isUTF16String() const { return getType() == UTF16_STRING; }
	bool isTuple() const { return getType() == TUPLE; }
	bool isStruct() const { return getType() == STRUCT; }
	ValueType getType() const { return ValueType(m_typeAndRefCount >> 28); }

	String toString() const;

	void addRef()
	{
#if defined(UFT_MT) || defined(UFT_MT_MP)
	#if defined(MT_MP)
		__asm	mov		EAX, 1;
		__asm lock xadd	m_typeAndRefCount,	EAX;
	#else
		__asm	mov		EAX, 1;
		__asm	xadd	m_typeAndRefCount,	EAX;
	#endif
#else
		m_typeAndRefCount++;
#endif
		assertLogic( (m_typeAndRefCount & ~((-1)<<28)) != 0 ); // check that no overflow
	}

	void release()
	{
		assertLogic( (m_typeAndRefCount & ~((-1)<<28)) != 0 ); // check that no underflow
#if defined(UFT_MT) || defined(UFT_MT_MP)
	#if defined(MT_MP)
		__asm	mov		EAX, -1;
		__asm lock xadd	m_typeAndRefCount,	EAX;
	#else
		__asm	mov		EAX, -1;
		__asm	xadd	m_typeAndRefCount,	EAX;
	#endif
		__asm	and		EAX,	~((-1)<<28);
		__asm	dec		EAX;
		__asm	jnz		no_free;
		//if( (m_typeAndRefCount & ~((-1)<<28)) == 0 )
			freeBlock( this );
no_free:
#else
		--m_typeAndRefCount;
		if( (m_typeAndRefCount & ~((-1)<<28)) == 0 )
			freeBlock( this );
#endif
	}

	bool isUnique() const { return (m_typeAndRefCount &~ ((-1)<<28)) == 1; }

	BlockHead * clone() const;

private:

	void forceDecRef()
	{
		assertLogic( (m_typeAndRefCount & ~((-1)<<28)) != 0 ); // check that no underflow
		// TODO MT: interlocked decrement
#if defined(UFT_MT) || defined(UFT_MT_MP)
	#if defined(MT_MP)
		__asm	mov		EAX, -1;
		__asm lock xadd	m_typeAndRefCount,	EAX;
	#else
		__asm	mov		EAX, -1;
		__asm	xadd	m_typeAndRefCount,	EAX;
	#endif
#else
		--m_typeAndRefCount;
#endif
	}

	static void freeBlock( BlockHead * block );
	static SizedBlock * allocBlock( ValueType type, size_t byteSize );
	static StructBlock * allocBlock( const StructDescriptor * desc );

#if UFT_MEMORY_DEBUG
	static SizedBlock * allocBlock( ValueType type, size_t byteSize, const char * file, int line );
	static StructBlock * allocBlock( const StructDescriptor * desc, const char * file, int line );
#endif // UFT_MEMORY_DEBUG

	void changeType( ValueType type ) { m_typeAndRefCount = (type<<28)|(m_typeAndRefCount&~((-1)<<28)); }

private:

	// contains block type in the upper 4 bits and ref count in the lower 28 bits
	uint32 m_typeAndRefCount;
};

class SizedBlock : public BlockHead
{
	friend class uft::BlockHead;
	friend class uft::RuntimeImpl;
public:

	size_t getByteSize() const { return m_byteSize; }
	uint8 * getData() { return (uint8*)this + sizeof(*this); }

private:

	size_t m_byteSize;
};

class StringBlock : public SizedBlock
{
	friend class uft::BlockHead;
	friend class uft::RuntimeImpl;
	friend class uft::Value;
public:

	char * utf8() { return m_utf8; }

	uint32 hashCode() const
	{
		// getByteSize() contains field for m_atomID, which needs to be excluded
		return hash( (uft::uint8*)m_utf8, getByteSize()-sizeof(uft::uint32)-1 );
	}

	uint32 atomID() const
	{
		return m_atomID;
	}

	bool isAtom() const
	{
		return m_atomID != 0;
	}

private:
	uint32 m_atomID;
	char m_utf8[1]; // m_byteSize-sizeof(uint32) characters including trailing 0
};

// structured just like StringBlock
class PlatformStringBlock : public SizedBlock
{
	friend class uft::BlockHead;
	friend class uft::PlatformString;
	friend class uft::Value;
public:

	char * localStr() { return m_localStr; }

	uint32 hashCode() const
	{
		// getByteSize() contains field for m_atomID, which needs to be excluded
		return hash( (uft::uint8*)m_localStr, getByteSize()-sizeof(uft::uint32)-1 );
	}

private:
	uint32 m_atomID; // can be non-zero only for ASCII_STRING
	char m_localStr[1]; // m_byteSize-sizeof(uint32) characters including trailing 0
};

class StructBlock : public BlockHead
{
	friend class uft::Value;
	friend class uft::BlockHead;
	friend class uft::RuntimeImpl;
public:

	inline bool isStringBuffer() const { return m_desc == s_stringBufferDescriptor; }
	inline bool isLongInt() const { return m_desc == s_longIntDescriptor; }
	inline bool isDouble() const { return m_desc == s_doubleDescriptor; }
	inline bool isToken() const { return m_desc == s_tokenDescriptor; }

	const StructDescriptor * getStructDescriptor() { return m_desc; }
	void * getStructPtr() { return (uint8*)this + sizeof(*this); }

	static int staticInit();

private:

	const StructDescriptor * m_desc;
};

class WeakReferencePtr;

typedef void (*destroyFunc_t)( const StructDescriptor * self, void * ptr );
typedef void (*copyFunc_t)( const StructDescriptor * self, void * dest, const void * src );
typedef String (*toStringFunc_t)( const StructDescriptor * self, void * ptr );
typedef bool (*queryFunc_t)( const StructDescriptor * self, void * ptr, const Value& what, void * result );
typedef WeakReferencePtr * (*getWeakRefPtrFunc_t)( const StructDescriptor * self, void * ptr );
typedef void (*flushFunc_t)( const StructDescriptor * self, void * ptr, CacheFlushContext * context );

// class that describes a uft-enabled struct. If this chages, its couterpart in uft_c.h,
// StructDescriptor_s also must be changed
class StructDescriptor
{
public:

	StructDescriptor( word blockByteSize, const char * name, destroyFunc_t destroyFunc, 
					copyFunc_t copyFunc, toStringFunc_t toStringFunc, queryFunc_t queryFunc, 
					getWeakRefPtrFunc_t getWeakRefPtr, flushFunc_t flushPtr, rib::Binder* binder=NULL )
	  : m_blockByteSize(blockByteSize),
		m_name(name),
		m_destroy(destroyFunc),
		m_copy(copyFunc),
		m_toString(toStringFunc),
		m_query(queryFunc),
		m_getWeakRefPtr(getWeakRefPtr),
		m_ribBinder(binder),
		m_flush(flushPtr)
	{
	}

public:

	word m_blockByteSize;
	const char * m_name;
	destroyFunc_t m_destroy;
	copyFunc_t m_copy;
	toStringFunc_t m_toString;
	queryFunc_t m_query;
	getWeakRefPtrFunc_t m_getWeakRefPtr;
	flushFunc_t m_flush;
	rib::Binder* m_ribBinder;
};

class LongIntBlock : public StructBlock
{
public:

	int64 m_val;
};

class DoubleBlock : public StructBlock
{
public:

	double m_val;
};

class Value
{
	friend class uft::RuntimeImpl;
	friend class uft::Tuple;
	friend class uft::SimpleRefStruct;

public:

	Value() : m_value(1) { } // null
	Value( const Value& other ) { init(other); }
	Value( const char * utf8 ) { init(utf8); }
	Value( const char * utf8, size_t length ) { init(utf8,length); }
	Value( const char16 * utf16 ) { init(utf16); }
	Value( const char16 * utf16, size_t length ) { init(utf16,length); }
	Value( int32 i ) : m_value((i<<2)|3) {}
	Value( float f ) { init(f); }
	Value( bool cond );
	explicit Value( int64 i ) { init(i); }
	explicit Value( double d ) { init(d); }
	~Value() { destroy(); }

	const Value& operator=( const Value& other );
	const Value& operator=( int32 i ) { destroy(); m_value = (i<<2)|3; return *this; }
	const Value& operator=( float f ) { destroy(); init(f); return *this; }
	const Value& operator=( char c ) { destroy(); init(&c,1); return *this; }
	const Value& operator=( char * utf8 ) { destroy(); init(utf8); return *this; }
	const Value& operator=( char16 * utf16 ) { destroy(); init(utf16); return *this; }
	const Value& operator=( int64 v ) { destroy(); init(v); return *this; }
	const Value& operator=( double v ) { destroy(); init(v); return *this; }

	// inline version of operator=, use where performance is critical
	void inlineSet( const Value& other )
	{
		// Attention! Be careful! We have to assign first and dereference next
		// because dereferencing can invoke any sort of open-ended code, including something
		// that tries to access this Value! -- psorotok

		// this code is hand-crafted for speed, it is one of the most-called routines

		uft::word newBlock = other.m_value-1;
		uft::word oldBlock = m_value-1;
		if( (newBlock & 3) == 0 && newBlock )
			((BlockHead*)newBlock)->addRef(); // it is refcounted
		m_value = other.m_value;
		if( (oldBlock & 3) == 0 && oldBlock )
			((BlockHead*)oldBlock)->release();
	}
	
	bool isTrue() const;
	bool isFalse() const;
	bool isFloat() const { return (m_value&1)==0; }
	bool isInt() const { return (m_value&3)==3; }
	bool isNull() const { return m_value==1; }
	bool isNumber() const;
	bool isRefCounted() const { return (m_value&3)==1 && m_value != 1; }
	bool isString() const { if(!isRefCounted()) return false; BlockHead * ptr = getBlock(); return ptr->isString(); }
	bool isPlatformString() const { if(!isRefCounted()) return false; BlockHead * ptr = getBlock(); return ptr->isPlatformString(); }
	bool isASCIIString() const { if(!isRefCounted()) return false; BlockHead * ptr = getBlock(); return ptr->isASCIIString(); }
	bool isUTF16String() const { if(!isRefCounted()) return false; BlockHead * ptr = getBlock(); return ptr->isUTF16String(); }
	bool isAtom() const { if(!isRefCounted()) return false; BlockHead * ptr = getBlock(); return ptr->isString() && ((StringBlock*)ptr)->isAtom(); }
	bool isTuple() const { if(!isRefCounted()) return false; BlockHead * ptr = getBlock(); return ptr->isTuple(); }
	bool isStruct() const { if(!isRefCounted()) return false; BlockHead * ptr = getBlock(); return ptr->isStruct(); }
	bool isStringBuffer() const { if(!isRefCounted()) return false; BlockHead * ptr = getBlock(); return ptr->isStruct() && ((StructBlock*)ptr)->isStringBuffer(); }
	bool isLong() const { if(!isRefCounted()) return false; BlockHead * ptr = getBlock(); return ptr->isStruct() && ((StructBlock*)ptr)->isLongInt(); }
	bool isDouble() const { if(!isRefCounted()) return false; BlockHead * ptr = getBlock(); return ptr->isStruct() && ((StructBlock*)ptr)->isDouble(); }
	bool isToken() const { if(!isRefCounted()) return false; BlockHead * ptr = getBlock(); return ptr->isStruct() && ((StructBlock*)ptr)->isToken(); }
	bool isInstanceOf( const StructDescriptor * sd ) const { if(!isRefCounted()) return false; BlockHead * ptr = getBlock(); return ptr->isStruct() && ((StructBlock*)ptr)->m_desc == sd; }

	void * getStructPtr() const
	{
		assertLogic( isStruct() );
		uft::StructBlock * block = (uft::StructBlock*)getBlock();
		return block->getStructPtr();
	}

	/**
		Extract a value or convert this value into something different. Exact meaning depends on the what parameter
		(which typically is an atom). Call to this function should never modify value itself and should be repeatable,
		i.e. second call should yield the same result. Function returns true if query/conversion was successful (and
		the result of query/conversion is returned in memory supplied by parameter res); false otherwise.
	*/
	bool query( const Value& what, void * res ) const;

	float getFloat() const { assertLogic(isFloat()); return *(float *)&m_value; }
	int32 getInt() const { assertLogic(isInt()); return int(m_value)>>2; }
	int64 getLong() const { assertLogic(isLong()); return ((LongIntBlock*)getBlock())->m_val; }
	double getDouble() const { assertLogic(isDouble()); return ((DoubleBlock*)getBlock())->m_val; }
	double getNumber() const;

	// makes a numerical value that fits the best (int, float, double)
	static uft::Value makeNumber( double v );

	const StructDescriptor * getStructDescriptor() const { assertLogic(isStruct()); return ((StructBlock*)getBlock())->getStructDescriptor(); }
	
	String toString() const;
	String toStringOrNull() const;
	
	String& asString() { assertLogic(isString()); return *(String*)this; }
	const String& asString() const { assertLogic(isString()); return *(const String*)this; }
	String& asStringOrNull() { assertLogic(isString()||isNull()); return *(String*)this; }
	const String& asStringOrNull() const { assertLogic(isString()||isNull()); return *(const String*)this; }

	StringBuffer& asStringBuffer() { assertLogic(isStringBuffer()); return *(StringBuffer*)this; }

	Value clone() const;
	void uniqueRef();
	bool isUniqueRef() const { return !isRefCounted() || isNull() || getBlock()->isUnique(); }
	
	Value extractValue() const;

	void setNull() { destroy(); m_value = 1; }
	
	// allocate uninitialized struct
	void * allocStruct( const StructDescriptor * desc );

#if UFT_MEMORY_DEBUG
	void * allocStruct( const StructDescriptor * desc, const char * file, int line );
#endif

	int ptrCompare( const Value& other ) const { return int(m_value - other.m_value); }
	bool ptrEq( const Value& other ) const { return m_value == other.m_value; }

	// given a pointer to uft struct returns uft::Value which points to this struct; inverse to uft::sref<Cs>::operator Cs*
	static uft::Value fromStructPtr( void * ptr );

	// given pointer to uft struct, increment/decrement its ref count
	static void addRefStructPtr( void * ptr );
	static void releaseStructPtr( void * ptr );

	// These are two very special and potentially dangerous functions. You can call createdCircularReference after you created
	// a "circular reference" - a chain of references that go back to struct. Such reference normally should not be counted
	// and createdCircularReference simply decrements refcount to account for that. When the circular reference is cleaned
	// up, it must be done in the following manner: first call enterCircularReferenceCleanup(this) to artificially bump up
	// reference count for the struct= being destroyed by 2. When circular reference is destroyed call
	// exitCircularReferenceCleanup(this) to decrement reference count by 1 without cleanup. This strategy works in the
	// struct= destructor.
	static void createdCircularReference( const Value& val ); 
	static void enterCircularReferenceCleanup( void * ); 
	static void exitCircularReferenceCleanup( void * ); 

	static const uft::Value& nullValue() { return *(const uft::Value*)&sNull; }

	// handle access - specialized methods, use with care 

	void * handleGet() const
	{
		void * handle = reinterpret_cast<void*>(m_value);
		handleAddRef(handle);
		return handle;
	}

	void handleSet(void * handle)
	{
		handleAddRef(handle);
		destroy();
		m_value = reinterpret_cast<word>(handle);
	}

	static void handleAddRef(void * handle)
	{
		word block = reinterpret_cast<word>(handle) - 1;
		if( (block & 3) == 0 && block )
			reinterpret_cast<BlockHead*>(block)->addRef(); // it is refcounted
	}

	static void handleRelease(void * handle)
	{
		word block = reinterpret_cast<word>(handle) - 1;
		if( (block & 3) == 0 && block )
			reinterpret_cast<BlockHead*>(block)->release(); // it is refcounted
	}

protected:

#if defined(_MSC_VER)
#pragma warning(push)
#pragma warning(disable:4311)
#pragma warning(disable:4312)
#endif

	Value( BlockHead * block ) : m_value((word)block+1) { block->addRef(); }
	Value( const char * utf8, size_t length, bool asciiOnly, bool localEnc ) { init(utf8,length); }

	// returns NULL for Null Value
	BlockHead * getBlock() const { assertLogic(isRefCounted()); return (BlockHead*)(m_value-1); }
	void setBlock( BlockHead * block ) { m_value = ((word)block)+1; block->addRef(); }

#if defined(_MSC_VER)
#pragma warning(pop)
#endif

	void init( float f ) { *(float*)&m_value = f; m_value &= ~1; }

	void init( const Value& other )
	{
		uft::word newBlock = other.m_value-1;
		m_value = other.m_value;
		if( (newBlock & 3) == 0 && newBlock )
			((BlockHead*)newBlock)->addRef();
	}

	void init( double val );
	void init( int64 val );
	void init( const char * utf8 );
	void init( const char * utf8, size_t len );
	void init( const char * chars, size_t len, bool asciiOnly, bool localEnc );
	void init( const char16 * utf16 );
	void init( const char16 * utf16, size_t len );

	void destroy()
	{
		uft::word block = m_value-1;
		// block->release() can get back to this value in some obscure way, prevent this by making this value null
		if( (block & 3) == 0 && block )
		{
			m_value = 1;
			((BlockHead*)block)->release(); // it is refcounted
		}
	}

private:

	static const uft::word sNull;

	// depending on the bit pattern can be:
	// - a float (with 1 bit precision loss ~ 1e-7) - if last bit is 0
	// - a pointer to a refcounted Block - if last two bits are 01
	// - an integer (with 2 bit precision loss) - if last two bits are 11
	word m_value;
};

class Integer : public Value
{
public:
	Integer() : Value(0) {}
	Integer( int i ) : Value(i) {}

	operator int() const { return getInt(); }

	static bool isInstanceOf( const Value& v ) { return v.isInt(); }

	static const int MAX_VALUE = 0x1FFFFFFF;
	static const int MIN_VALUE = 0xE0000000;
};

class Float : public Value
{
public:
	Float() : Value(0.0f) {}
	Float( float f ) : Value(f) {}

	operator float() const { return getFloat(); }

	static bool isInstanceOf( const Value& v ) { return v.isFloat(); }
};

class Long : public Value
{
public:
	Long() : Value((uft::int64)0) {}
	Long( int64 v ) : Value(v) {}

	operator int64() const { return getLong(); }

	static bool isInstanceOf( const Value& v ) { return v.isLong(); }
};

class Double : public Value
{
public:
	Double() : Value((double)0.0) {}
	Double( double f ) : Value(f) {}

	operator double() const { return getDouble(); }

	static bool isInstanceOf( const Value& v ) { return v.isDouble(); }
};

/**
 * Token is a Value which is guaranteed to be different from all other values
 * handy for things like lookup keys
 */
class Token : public Value
{
public:
	Token() {} // null
	Token( const Token& other ) : Value(other) {}

	static Token newToken();
#if UFT_MEMORY_DEBUG
	static Token newToken( const char * file, int line );
#endif // UFT_MEMORY_DEBUG

	static bool isInstanceOf( const Value& v ) { return v.isToken(); }
};

//----------------------------- casts ---------------------------------------------------------

template<class T> inline const T& checked_cast(const Value& p)
{
	if( p.isNull() || T::isInstanceOf(p) )
		return (const T&)p;
	return *(const T *)&Value::nullValue();
}

template<class T> inline const T& assumed_cast(const Value& p)
{
	assertLogic( p.isNull() || T::isInstanceOf(p) );
	return (const T&)p;
}

template<class T> inline T& assumed_cast(Value& p)
{
	assertLogic( p.isNull() || T::isInstanceOf(p) );
	return (T&)p;
}

//---------------------------------------------------------------------------------------------------

class UFTInitializer
{
public:
	UFTInitializer();
	~UFTInitializer();
};

void selfTest();

}

// must-run-first initializer
static uft::UFTInitializer s_uftInitializer;

#endif // _UFT_VALUE_H
